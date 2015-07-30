package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@BennuSpringController(value = InstructionsController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/finished")
public class FinishedController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String finished(Model model) {
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/finished";
    }
}
