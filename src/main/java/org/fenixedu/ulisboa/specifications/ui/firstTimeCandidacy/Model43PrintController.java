package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = PersonalInformationFormController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/model43print")
public class Model43PrintController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String model43print(Model model) {
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/model43print";
    }

    @RequestMapping(value = "/continue")
    public String model43printToContinue(Model model, RedirectAttributes redirectAttributes) {
        return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/documentsprint", model, redirectAttributes);
    }
}
