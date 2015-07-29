package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = InstructionsController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/scheduleclasses")
public class ScheduleClassesController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String scheduleclasses(Model model) {
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/scheduleclasses";
    }

    @RequestMapping(value = "/openshiftenrollments")
    public String scheduleclassesToOpenShiftEnrollments(Model model, RedirectAttributes redirectAttributes) {
        return redirect("CHANGEME", model, redirectAttributes);
    }

    @RequestMapping(value = "/continue")
    public String scheduleclassesToContinue(Model model, RedirectAttributes redirectAttributes) {
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/showscheduledclasses", model, redirectAttributes);
    }
}
