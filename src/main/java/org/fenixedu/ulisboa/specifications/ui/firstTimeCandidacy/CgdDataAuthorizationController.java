package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = InstructionsController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/cgddataauthorization")
public class CgdDataAuthorizationController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String cgddataauthorization(Model model) {
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/cgddataauthorization";
    }

    @RequestMapping(value = "/authorize")
    public String cgddataauthorizationToAuthorize(Model model, RedirectAttributes redirectAttributes) {
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/model43print", model, redirectAttributes);
    }

    @RequestMapping(value = "/unauthorize")
    public String cgddataauthorizationToUnauthorize(Model model, RedirectAttributes redirectAttributes) {
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/cgddataauthorizationrecheck", model, redirectAttributes);
    }
}
