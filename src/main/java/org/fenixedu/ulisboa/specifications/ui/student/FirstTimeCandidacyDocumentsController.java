package org.fenixedu.ulisboa.specifications.ui.student;

import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.candidacy.FirstTimeCandidacy;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class,
        title = "label.title.student.downloadFirstTimeCandidacyDocuments", accessGroup = "activeStudents")
@RequestMapping(FirstTimeCandidacyDocumentsController.CONTROLLER_URL)
public class FirstTimeCandidacyDocumentsController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/ulisboaspecifications/student/firstTimeCandidacyDocuments";

    @RequestMapping
    public String home(Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("controllerURL", CONTROLLER_URL);
        model.addAttribute("registrationsSet", AccessControl.getPerson().getStudent().getRegistrationsSet().stream()
                .filter(r -> r.getStudentCandidacy() instanceof FirstTimeCandidacy).collect(Collectors.toList()));
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/student/download";
    }

    @RequestMapping(value = "{registrationId}/printalldocuments", produces = "application/pdf")
    public ResponseEntity<byte[]> finishedToPrintAllDocuments(@PathVariable("registrationId") Registration registration,
            Model model, RedirectAttributes redirectAttributes) {
        Person person = AccessControl.getPerson();
        Person registrationPerson = registration.getStudent().getPerson();
        if (person != registrationPerson) {
            throw new RuntimeException("Error. Mismatch between logged person and registration.");
        }

        StudentCandidacy candidacy = registration.getStudentCandidacy();
        if (candidacy == null) {
            throw new RuntimeException("Registration[" + registration.getExternalId() + "] doesn't have candidacy.");
        }

        byte[] pdfBytes = new byte[0];
        if (candidacy.getSummaryFile() != null) {
            pdfBytes = candidacy.getSummaryFile().getContent();
        }
        String filename = person.getStudent().getNumber() + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline;filename=" + filename);
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(pdfBytes, headers, HttpStatus.OK);

        return response;
    }

}
