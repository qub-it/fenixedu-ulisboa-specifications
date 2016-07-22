package org.fenixedu.ulisboa.specifications.ui.blue_record;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationGlobalConfiguration;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides;
import org.fenixedu.ulisboa.specifications.domain.student.access.StudentAccessServices;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.CgdDataAuthorizationController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.DocumentsPrintController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.util.CGDPdfFiller;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lowagie.text.DocumentException;

@BennuSpringController(value = BlueRecordEntryPoint.class)
@RequestMapping(CgdDataAuthorizationControllerBlueRecord.CONTROLLER_URL)
public class CgdDataAuthorizationControllerBlueRecord extends CgdDataAuthorizationController {

    public static final String CONTROLLER_URL =
            "/fenixedu-ulisboa-specifications/blueRecord/{executionYearId}/cgddataauthorization";

    @Override
    public String back(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model,
            RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        String url = MotivationsExpectationsFormControllerBlueRecord.CONTROLLER_URL;
        return redirect(urlWithExecutionYear(url, executionYear), model, redirectAttributes);
    }

    @Override
    public String cgddataauthorization(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        if (isFormIsFilled(executionYear, model)) {
            return nextScreen(executionYear, model, redirectAttributes);
        }

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/cgddataauthorization";
    }

    protected String nextScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(BlueRecordEnd.CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    @RequestMapping(value = "/authorize")
    public String cgddataauthorizationToAuthorize(@PathVariable("executionYearId") final ExecutionYear executionYear,
            final Model model, final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        if (isFormIsFilled(executionYear, model)) {
            return nextScreen(executionYear, model, redirectAttributes);
        }

        authorizeSharingDataWithCGD(true);

        final Registration registration = findFirstTimeRegistration(executionYear);

        boolean wsCallSuccess;
        try {
            wsCallSuccess = StudentAccessServices.triggerSyncRegistrationToExternal(registration);
        } catch(Exception e) {
            wsCallSuccess = false;
        }

        if (wsCallSuccess) {
            return nextScreen(executionYear, model, redirectAttributes);
        } else {
            final String url =
                    String.format("/fenixedu-ulisboa-specifications/blueRecord/%s/cgddataauthorization/showmodelo43download/true",
                            executionYear.getExternalId());

            return redirect(url, model, redirectAttributes);
        }
    }

    @RequestMapping(value = "/unauthorize")
    public String cgddataauthorizationToUnauthorize(@PathVariable("executionYearId") final ExecutionYear executionYear,
            final Model model, final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        if (isFormIsFilled(executionYear, model)) {
            return nextScreen(executionYear, model, redirectAttributes);
        }

        authorizeSharingDataWithCGD(false);

        final String url =
                String.format("/fenixedu-ulisboa-specifications/blueRecord/%s/cgddataauthorization/showmodelo43download/false",
                        executionYear.getExternalId());

        return redirect(url, model, redirectAttributes);
    }

    @RequestMapping(value = "/showmodelo43download/{dueToError}", method=RequestMethod.GET)
    public String showmodelo43download(@PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("dueToError") boolean dueToError, final Model model) {
        
        final String url =
                String.format("/fenixedu-ulisboa-specifications/blueRecord/%s/cgddataauthorization/printmodelo43",
                        executionYear.getExternalId());
        
        model.addAttribute("dueToError", dueToError);
        model.addAttribute("printURL", url);

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/showmodelo43download";
    }

    @RequestMapping(value = "/showmodelo43download/{dueToError}", method=RequestMethod.POST)
    public String showmodelo43download(@PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("dueToError") boolean dueToError, final Model model, final RedirectAttributes redirectAttributes) {

        return nextScreen(executionYear, model, redirectAttributes);
    }

    @RequestMapping(value = "/printmodelo43")
    @ResponseBody
    public byte[] cgddataauthorizationToUnauthorize(final HttpServletResponse response) {
        byte[] printModel43Content = printModel43();

        response.setContentType("application/pdf");
        response.addHeader("Content-Disposition", "attachment; filename=CGD_Modelo43.pdf");

        return printModel43Content;
    }

    private byte[] printModel43() {
        Person person = Authenticate.getUser().getPerson();

        InputStream pdfTemplateStream;
        if (FirstYearRegistrationGlobalConfiguration.getInstance().hasMod43Template()) {
            pdfTemplateStream = new ByteArrayInputStream(
                    FirstYearRegistrationGlobalConfiguration.getInstance().getMod43Template().getContent());

            ByteArrayOutputStream stream;
            try {
                CGDPdfFiller cgdPdfFiller = new CGDPdfFiller();
                stream = cgdPdfFiller.getFilledPdf(person, pdfTemplateStream);
            } catch (IOException | DocumentException e) {
                LoggerFactory.getLogger(this.getClass()).error("Exception for user " + AccessControl.getPerson().getUsername());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            return stream.toByteArray();

        } else {
            throw new RuntimeException("error");
        }

    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    public boolean isFormIsFilled(final ExecutionYear executionYear, final Student student) {
        final Registration firstTimeRegistration = findFirstTimeRegistration(executionYear);
        if (firstTimeRegistration == null) {
            return true;
        }

        if (firstTimeRegistration.getPerson().getPersonUlisboaSpecifications() != null
                && firstTimeRegistration.getPerson().getPersonUlisboaSpecifications().isSharingDataWithCGDAnswered()) {
            return true;
        }

        return hasCgdCard(firstTimeRegistration.getPerson());
    }

    private boolean hasCgdCard(final Person person) {
        return !person.getCgdCardsSet().isEmpty();
    }

    @Override
    protected Student getStudent(Model model) {
        return AccessControl.getPerson().getStudent();
    }

    private Registration findFirstTimeRegistration(final ExecutionYear executionYear) {
        final List<Registration> registrations =
                Raides.findActiveFirstTimeRegistrationsOrWithEnrolments(executionYear, AccessControl.getPerson().getStudent());
        return registrations.stream().filter(r -> r.getRegistrationYear() == executionYear).findFirst().orElse(null);
    }

}
