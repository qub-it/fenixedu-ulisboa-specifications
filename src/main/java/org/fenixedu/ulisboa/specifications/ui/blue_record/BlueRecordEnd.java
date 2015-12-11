package org.fenixedu.ulisboa.specifications.ui.blue_record;

import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@BennuSpringController(value = BlueRecordEntryPoint.class)
@RequestMapping(BlueRecordEnd.CONTROLLER_URL)
public class BlueRecordEnd extends FenixeduUlisboaSpecificationsBaseController {
    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/blueRecord/end";

    @RequestMapping
    public String home(Model model) {
        return "fenixedu-ulisboa-specifications/blueRecord/end";
    }

}
