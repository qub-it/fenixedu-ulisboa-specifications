package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import org.fenixedu.academic.domain.candidacy.CandidacySummaryFile;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = InstructionsController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/cgddataauthorization")
public class CgdDataAuthorizationController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String cgddataauthorization(Model model) {
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/cgddataauthorization";
    }

    @RequestMapping(value = "/authorize")
    public String cgddataauthorizationToAuthorize(Model model, RedirectAttributes redirectAttributes) {
        resetCandidacySummaryFile(InstructionsController.getStudentCandidacy());
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/model43print", model, redirectAttributes);
    }

    @RequestMapping(value = "/unauthorize")
    public String cgddataauthorizationToUnauthorize(Model model, RedirectAttributes redirectAttributes) {
        resetCandidacySummaryFile(InstructionsController.getStudentCandidacy());
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/documentsprint", model, redirectAttributes);
    }

    @Atomic
    public static void resetCandidacySummaryFile(StudentCandidacy studentCandidacy) {
        CandidacySummaryFile summaryFile = studentCandidacy.getSummaryFile();
        if (summaryFile != null) {
            summaryFile.setStudentCandidacy(null);
            summaryFile.delete();
        }
    }
}
