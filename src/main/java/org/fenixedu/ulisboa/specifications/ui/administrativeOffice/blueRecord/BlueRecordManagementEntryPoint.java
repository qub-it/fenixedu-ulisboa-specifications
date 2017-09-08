package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.blueRecord;

import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.blueRecord.management")
@RequestMapping(BlueRecordManagementEntryPoint.CONTROLLER_URL)
public class BlueRecordManagementEntryPoint extends FenixeduUlisboaSpecificationsBaseController {
    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/blueRecord/management";

    @RequestMapping(value = "/")
    public String home(final Model model) {

        return "";
    }

}
