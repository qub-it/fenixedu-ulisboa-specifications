package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import java.util.function.Predicate;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.candidacy.Candidacy;
import org.fenixedu.academic.domain.candidacy.DegreeCandidacy;
import org.fenixedu.academic.domain.candidacy.IMDCandidacy;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.instructions")
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/instructions")
public class InstructionsController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String instructions(Model model) {
        Predicate<Candidacy> firstTimeCandidaciesPredicate = c -> ((c instanceof DegreeCandidacy) || (c instanceof IMDCandidacy));
        Stream<Candidacy> candidacies =
                Authenticate.getUser().getPerson().getCandidaciesSet().stream().filter(firstTimeCandidaciesPredicate);
        long count = candidacies.count();
        if (count == 0) {
            throw new RuntimeException(
                    "Students with no DegreeCandidacies or IMDCandidacies are not supported in the first time registration flow");
        }
        if (count > 1) {
            throw new RuntimeException(
                    "Students with multiple DegreeCandidacies or IMDCandidacies are not supported in the first time registration flow");
        }

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/instructions";
    }

    @RequestMapping(value = "/continue")
    public String instructionsToContinue(Model model, RedirectAttributes redirectAttributes) {
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/personalinformationform/fillpersonalinformation",
                model, redirectAttributes);
    }

    public static StudentCandidacy getStudentCandidacy() {
        Predicate<Candidacy> candidaciesPredicate = c -> ((c instanceof DegreeCandidacy) || (c instanceof IMDCandidacy));
        Stream<Candidacy> candidacies = AccessControl.getPerson().getCandidaciesSet().stream().filter(candidaciesPredicate);
        return (StudentCandidacy) candidacies.findAny().get();
    }
}
