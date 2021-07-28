package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.misc;

import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academicextensions.domain.person.dataShare.DataShareAuthorization;
import org.fenixedu.academicextensions.domain.person.dataShare.DataShareAuthorizationType;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.domain.services.student.StudentServices;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyAbstractController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lowagie.text.DocumentException;
import com.qubit.solution.fenixedu.integration.cgd.domain.configuration.CgdIntegrationConfiguration;
import com.qubit.solution.fenixedu.integration.cgd.services.CGDPdfFiller;
import com.qubit.solution.fenixedu.integration.cgd.services.form43.CgdForm43Sender;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(CgdDataAuthorizationController.CONTROLLER_URL)
public class CgdDataAuthorizationController extends FirstTimeCandidacyAbstractController {

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/cgddataauthorization";

    @RequestMapping(value = "/back", method = RequestMethod.GET)
    public String back(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        return redirect(urlWithExecutionYear(TuitionController.CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    protected String nextScreen(final ExecutionYear executionYear, final Model model, final RedirectAttributes redirectAttributes,
            final boolean wsCallSuccess) {
        PersonUlisboaSpecifications personUlisboaSpecifications =
                PersonUlisboaSpecifications.findOrCreate(AccessControl.getPerson());
        //Send with success
        if (wsCallSuccess) {
            return redirect(urlWithExecutionYear(FirstTimeCandidacyFinalizationController.WITHOUT_MODEL_URL, executionYear),
                    model, redirectAttributes);
        }

        return redirect(urlWithExecutionYear(FirstTimeCandidacyFinalizationController.WITH_MODEL_URL, executionYear), model,
                redirectAttributes);
    }

    @RequestMapping
    public String cgddataauthorization(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/cgd/cgddataauthorization";
    }

    @RequestMapping(value = "/authorize")
    public String cgddataauthorizationToAuthorize(@PathVariable("executionYearId") final ExecutionYear executionYear,
            final Model model, final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        if (isFormIsFilled(executionYear, model)) {
            return nextScreen(executionYear, model, redirectAttributes, true);
        }

        authorizeSharingDataWithCGD(true);

        final Registration registration = findFirstTimeRegistration(executionYear);

        boolean wsCallSuccess;
        try {
            wsCallSuccess = new CgdForm43Sender().sendForm43For(registration);
        } catch (Exception e) {
            wsCallSuccess = false;
        }

        return nextScreen(executionYear, model, redirectAttributes, wsCallSuccess);
        //This was redirect to the cgd model download page.
//            return nextScreen(executionYear, model, redirectAttributes);
//        } else {
//            final String url = urlWithExecutionYear(SHOW_MODEL_43_URL, executionYear) + "/true";
//            return redirect(url, model, redirectAttributes);
//        }
    }

    @RequestMapping(value = "/unauthorize")
    public String cgddataauthorizationToUnauthorize(@PathVariable("executionYearId") final ExecutionYear executionYear,
            final Model model, final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        if (isFormIsFilled(executionYear, model)) {
            return nextScreen(executionYear, model, redirectAttributes, true);
        }

        authorizeSharingDataWithCGD(false);

        final Registration registration = findFirstTimeRegistration(executionYear);
        try {
            new CgdForm43Sender().sendForm43For(registration);
        } catch (Exception e) {
        }

        return nextScreen(executionYear, model, redirectAttributes, true);
//        final String url = urlWithExecutionYear(SHOW_MODEL_43_URL, executionYear) + "/false";
//        return redirect(url, model, redirectAttributes);
    }

    @RequestMapping(value = "/unauthorize/withoutModel")
    public String cgddataauthorizationToUnauthorizeAndNoModel(@PathVariable("executionYearId") final ExecutionYear executionYear,
            final Model model, final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        authorizeSharingDataWithCGD(false);

        return redirect(urlWithExecutionYear(FirstTimeCandidacyFinalizationController.WITHOUT_MODEL_URL, executionYear), model,
                redirectAttributes);
    }

    @Atomic
    protected void authorizeSharingDataWithCGD(final boolean authorize) {
        DataShareAuthorizationType authorizationType = DataShareAuthorizationType.findUnique("CGD_EXTENDED_INFO");
        DataShareAuthorization.create(AccessControl.getPerson(), authorizationType, new Boolean(authorize));
    }

    protected static final String _SHOW_MODEL_43_URI = "/showmodelo43download";
    public static final String SHOW_MODEL_43_URL = CONTROLLER_URL + _SHOW_MODEL_43_URI;

    @RequestMapping(value = _SHOW_MODEL_43_URI + "/{dueToError}", method = RequestMethod.GET)
    public String showmodelo43download(@PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("dueToError") final boolean dueToError, final Model model,
            final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        final String url = getPrintURL(executionYear);

        model.addAttribute("dueToError", dueToError);
        model.addAttribute("printURL", url);

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/cgd/showmodelo43download";
    }

    protected String getPrintURL(final ExecutionYear executionYear) {
        return urlWithExecutionYear(PRINT_43_URL, executionYear);
    }

    @RequestMapping(value = "/showmodelo43download/{dueToError}", method = RequestMethod.POST)
    public String showmodelo43downloadPost(@PathVariable("executionYearId") final ExecutionYear executionYear,
            @PathVariable("dueToError") final boolean dueToError, final Model model,
            final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        return nextScreen(executionYear, model, redirectAttributes, dueToError);
    }

    protected static final String _PRINT_43_URI = "/printmodelo43";
    public static final String PRINT_43_URL = CONTROLLER_URL + _PRINT_43_URI;

    @RequestMapping(value = _PRINT_43_URI)
    @ResponseBody
    public byte[] cgddataauthorizationToUnauthorize(@PathVariable("executionYearId") final ExecutionYear executionYear,
            final Model model, final RedirectAttributes redirectAttributes, final HttpServletResponse response) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return null;
        }

        byte[] printModel43Content = printModel43();

        response.setContentType("application/pdf");
        response.addHeader("Content-Disposition", "attachment; filename=CGD_Modelo43.pdf");

        return printModel43Content;
    }

    private byte[] printModel43() {
        Person person = AccessControl.getPerson();

        InputStream pdfTemplateStream;
        if (CgdIntegrationConfiguration.getInstance().hasMod43Template()) {
            pdfTemplateStream =
                    new ByteArrayInputStream(CgdIntegrationConfiguration.getInstance().getMod43Template().getContent());

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
        final List<Registration> registrations = StudentServices.findActiveFirstTimeRegistrationsOrWithEnrolments(executionYear,
                AccessControl.getPerson().getStudent());
        return registrations.stream().filter(r -> r.getRegistrationYear() == executionYear).findFirst().orElse(null);
    }

    @Override
    protected Student getStudent(final Model model) {
        return AccessControl.getPerson().getStudent();
    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

}
