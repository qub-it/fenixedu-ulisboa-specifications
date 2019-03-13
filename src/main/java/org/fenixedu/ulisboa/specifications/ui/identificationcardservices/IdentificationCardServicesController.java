package org.fenixedu.ulisboa.specifications.ui.identificationcardservices;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;

import javax.servlet.ServletContext;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationGlobalConfiguration;
import org.fenixedu.ulisboa.specifications.domain.student.access.StudentAccessServices;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.util.CGDPdfFiller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lowagie.text.DocumentException;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.identificationCardServices",
        accessGroup = "logged")
@RequestMapping(IdentificationCardServicesController.CONTROLLER_URL)
public class IdentificationCardServicesController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/identificationcardservices";
    private @Autowired ServletContext context;
    private static final String CGD_PERSONAL_INFORMATION_PDF_PATH = "candidacy/firsttime/CGD43.pdf";

    private static final Comparator<Registration> REGISTRATION_COMPARATOR = new Comparator<Registration>() {

        @Override
        public int compare(Registration registration1, Registration registration2) {
            SortedSet<ExecutionYear> sortedEnrolmentsExecutionYears = registration1.getSortedEnrolmentsExecutionYears();
            SortedSet<ExecutionYear> sortedEnrolmentsExecutionYears2 = registration2.getSortedEnrolmentsExecutionYears();

            if (sortedEnrolmentsExecutionYears.isEmpty() && !sortedEnrolmentsExecutionYears2.isEmpty()) {
                return 1;
            } else if (!sortedEnrolmentsExecutionYears.isEmpty() && sortedEnrolmentsExecutionYears2.isEmpty()) {
                return -1;
            } else if (sortedEnrolmentsExecutionYears.isEmpty() && sortedEnrolmentsExecutionYears2.isEmpty()) {
                return 0;
            } else {
                return sortedEnrolmentsExecutionYears.last().compareTo(sortedEnrolmentsExecutionYears2.last());
            }
        }
    };

    @RequestMapping
    public String home(Model model) {
        return "fenixedu-ulisboa-specifications/identificationcardservices/idservices";
    }

    @RequestMapping(value = "/sendCGDMod43")
    public String sendCGDMod43(Model model, RedirectAttributes redirectAttributes) {
        Person person = AccessControl.getPerson();
        Student student = person.getStudent();
        Registration registrationToSend = null;
        boolean sent = false;
        if (student != null) {
            List<Registration> activeRegistrations = student.getActiveRegistrations();
            int size = activeRegistrations.size();
            if (size == 1) {
                registrationToSend = activeRegistrations.iterator().next();
            } else if (size > 1) {
                Optional<Registration> findFirst =
                        activeRegistrations.stream().filter(registration -> registration.getRegistrationYear().isCurrent())
                                .sorted(REGISTRATION_COMPARATOR).findFirst();
                if (findFirst.isPresent()) {
                    registrationToSend = findFirst.get();
                }
            }
        }
        if (registrationToSend != null) {
            sent = StudentAccessServices.triggerSyncRegistrationToExternal(registrationToSend);
        }

        if (sent) {
            addInfoMessage(BundleUtil.getString(BUNDLE, "label.webserviceCall.cgd.form43.success"), model);
        } else {
            addErrorMessage(BundleUtil.getString(BUNDLE, "label.webserviceCall.cgd.form43.fail"), model);
        }
        model.addAttribute("webserviceSuccess", sent);

        return "fenixedu-ulisboa-specifications/identificationcardservices/idservices";
    }

    @RequestMapping(value = "/downloadCGDMod43", produces = "application/pdf")
    public ResponseEntity<byte[]> downloadCGDMod43(Model model, RedirectAttributes redirectAttributes) {
        Person person = Authenticate.getUser().getPerson();
        InputStream pdfTemplateStream;
        if (FirstYearRegistrationGlobalConfiguration.getInstance().hasMod43Template()) {
            pdfTemplateStream = new ByteArrayInputStream(
                    FirstYearRegistrationGlobalConfiguration.getInstance().getMod43Template().getContent());
        } else {
            pdfTemplateStream = context.getResourceAsStream(CGD_PERSONAL_INFORMATION_PDF_PATH);
        }

        ByteArrayOutputStream stream;
        try {
            CGDPdfFiller cgdPdfFiller = new CGDPdfFiller();
            stream = cgdPdfFiller.getFilledPdf(person, pdfTemplateStream);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        byte[] pdfBytes = stream.toByteArray();
        String filename = person.getUsername() + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment;filename=" + filename);
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(pdfBytes, headers, HttpStatus.OK);
        return response;
    }

}
