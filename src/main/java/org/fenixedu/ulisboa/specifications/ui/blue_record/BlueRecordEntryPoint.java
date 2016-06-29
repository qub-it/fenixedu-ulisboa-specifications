package org.fenixedu.ulisboa.specifications.ui.blue_record;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.blueRecord")
@RequestMapping(BlueRecordEntryPoint.CONTROLLER_URL)
public class BlueRecordEntryPoint extends FenixeduUlisboaSpecificationsBaseController {
    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/blueRecord";

    @RequestMapping(value= "/{executionYearId}")
    public String home(@PathVariable("executionYearId") final ExecutionYear executionYear, Model model) {
        model.addAttribute("nextURL", PersonalInformationFormControllerBlueRecord.CONTROLLER_URL.replace("{executionYearId}", executionYear.getExternalId()));
        
        return "fenixedu-ulisboa-specifications/blueRecord/home";
    }

}
