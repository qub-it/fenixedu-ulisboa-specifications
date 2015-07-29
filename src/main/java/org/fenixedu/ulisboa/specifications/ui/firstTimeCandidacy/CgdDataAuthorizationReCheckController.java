package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = InstructionsController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/cgddataauthorizationrecheck")
public class CgdDataAuthorizationReCheckController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String cgddataauthorizationrecheck(Model model) {
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/cgddataauthorizationrecheck";
    }

    @RequestMapping(value = "/authorize")
    public String cgddataauthorizationrecheckToAuthorize(Model model, RedirectAttributes redirectAttributes) {
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/model43print", model, redirectAttributes);
    }

    @RequestMapping(value = "/unauthorize")
    public String cgddataauthorizationrecheckToUnauthorize(Model model, RedirectAttributes redirectAttributes) {
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/documentsprint", model, redirectAttributes);
    }
}
