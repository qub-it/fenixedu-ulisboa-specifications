package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.misc;

import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

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
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyAbstractController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.util.CGDPdfFiller;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lowagie.text.DocumentException;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(CgdDataAuthorizationController.CONTROLLER_URL)
public class CgdDataAuthorizationController extends FirstTimeCandidacyAbstractController {

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/cgddataauthorization";

    @RequestMapping(value = "/back", method = RequestMethod.GET)
    public String back(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model,
            RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        return redirect(urlWithExecutionYear(TuitionController.CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    protected String nextScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(FirstTimeCandidacyFinalizationController.CONTROLLER_URL, executionYear), model,
                redirectAttributes);
    }

    @RequestMapping
    public String cgddataauthorization(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        if (!FirstTimeCandidacyController.isPeriodOpen()) {
            return redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes);
        }
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/cgd/cgddataauthorization";
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
        } catch (Exception e) {
            wsCallSuccess = false;
        }

        if (wsCallSuccess) {
            return nextScreen(executionYear, model, redirectAttributes);
        } else {
            final String url = urlWithExecutionYear(SHOW_MODEL_43_URL, executionYear) + "/true";
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

        final String url = urlWithExecutionYear(SHOW_MODEL_43_URL, executionYear) + "/false";
        return redirect(url, model, redirectAttributes);
    }

    @Atomic
    protected void authorizeSharingDataWithCGD(boolean authorize) {
        PersonUlisboaSpecifications.findOrCreate(AccessControl.getPerson()).setAuthorizeSharingDataWithCGD(authorize);
        PersonUlisboaSpecifications.findOrCreate(AccessControl.getPerson()).setSharingDataWithCGDAnswered(true);
    }

    protected static final String _SHOW_MODEL_43_URI = "/showmodelo43download";
    public static final String SHOW_MODEL_43_URL = CONTROLLER_URL + _SHOW_MODEL_43_URI;

    @RequestMapping(value = _SHOW_MODEL_43_URI + "/{dueToError}", method = RequestMethod.GET)
    public String showmodelo43download(@PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("dueToError") boolean dueToError, final Model model) {

        final String url = urlWithExecutionYear(PRINT_43_URL, executionYear);

        model.addAttribute("dueToError", dueToError);
        model.addAttribute("printURL", url);

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/cgd/showmodelo43download";
    }

    @RequestMapping(value = "/showmodelo43download/{dueToError}", method = RequestMethod.POST)
    public String showmodelo43download(@PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("dueToError") boolean dueToError, final Model model, final RedirectAttributes redirectAttributes) {

        return nextScreen(executionYear, model, redirectAttributes);
    }

    protected static final String _PRINT_43_URI = "/printmodelo43";
    public static final String PRINT_43_URL = CONTROLLER_URL + _PRINT_43_URI;

    @RequestMapping(value = _PRINT_43_URI)
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

    private Registration findFirstTimeRegistration(final ExecutionYear executionYear) {
        final List<Registration> registrations =
                Raides.findActiveFirstTimeRegistrationsOrWithEnrolments(executionYear, AccessControl.getPerson().getStudent());
        return registrations.stream().filter(r -> r.getRegistrationYear() == executionYear).findFirst().orElse(null);
    }

    @Override
    protected Student getStudent(Model model) {
        return AccessControl.getPerson().getStudent();
    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

}
