package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/finished")
public class FinishedController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String finished(Model model) {
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/finished";
    }

    @RequestMapping(value = "/printalldocuments", produces = "application/pdf")
    public ResponseEntity<byte[]> finishedToPrintAllDocuments(Model model, RedirectAttributes redirectAttributes) {
        byte[] pdfBytes = FirstTimeCandidacyController.getStudentCandidacy().getSummaryFile().getContent();
        String filename = AccessControl.getPerson().getStudent().getNumber() + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline;filename=" + filename);
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(pdfBytes, headers, HttpStatus.OK);
        return response;
    }
}
