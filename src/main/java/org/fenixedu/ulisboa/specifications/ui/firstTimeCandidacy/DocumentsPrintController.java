package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@BennuSpringController(value = PersonalInformationFormController.class)
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/documentsprint")
public class DocumentsPrintController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String documentsprint(Model model) {

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/documentsprint";
    }

}
