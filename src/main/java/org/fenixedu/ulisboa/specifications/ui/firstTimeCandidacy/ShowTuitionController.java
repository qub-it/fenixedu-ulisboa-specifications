package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = PersonalInformationFormController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/showtuition")
public class ShowTuitionController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String showtuition(Model model) {
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/showtuition";
    }

    @RequestMapping(value = "/continue")
    public String showtuitionToContinue(Model model, RedirectAttributes redirectAttributes) {
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/cgddataauthorization", model, redirectAttributes);
    }
}
