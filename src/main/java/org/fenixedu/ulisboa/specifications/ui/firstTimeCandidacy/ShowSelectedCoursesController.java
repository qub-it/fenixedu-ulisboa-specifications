package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/showselectedcourses")
public class ShowSelectedCoursesController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String showselectedcourses(Model model) {
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/showselectedcourses";
    }

    @RequestMapping(value = "/continue")
    public String showselectedcoursesToContinue(Model model, RedirectAttributes redirectAttributes) {
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/scheduleclasses", model, redirectAttributes);
    }
}
