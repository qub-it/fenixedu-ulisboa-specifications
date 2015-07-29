package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = InstructionsController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/showscheduledclasses")
public class ShowScheduledClassesController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String showscheduledclasses(Model model) {

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/showscheduledclasses";
    }

    @RequestMapping(value = "/continue")
    public String showscheduledclassesToContinue(Model model, RedirectAttributes redirectAttributes) {
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/showtuition", model, redirectAttributes);
    }
}
