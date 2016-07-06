package org.fenixedu.ulisboa.specifications.ui.blue_record;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@BennuSpringController(value = BlueRecordEntryPoint.class)
@RequestMapping(BlueRecordEnd.CONTROLLER_URL)
public class BlueRecordEnd extends FenixeduUlisboaSpecificationsBaseController {
    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/blueRecord/{executionYearId}/end";

    @RequestMapping
    public String home(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model) {
        model.addAttribute("controllerURL", CONTROLLER_URL + "/" + executionYear.getExternalId());
        return "fenixedu-ulisboa-specifications/blueRecord/end";
    }

}
