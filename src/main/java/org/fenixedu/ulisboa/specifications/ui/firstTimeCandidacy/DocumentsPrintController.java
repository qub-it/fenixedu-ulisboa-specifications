package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = InstructionsController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/documentsprint")
public class DocumentsPrintController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String documentsprint(Model model) {
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/documentsprint";
    }

    @RequestMapping(value = "/continue")
    public String documentsprintToContinue(Model model, RedirectAttributes redirectAttributes) {
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/finished", model, redirectAttributes);
    }
}
