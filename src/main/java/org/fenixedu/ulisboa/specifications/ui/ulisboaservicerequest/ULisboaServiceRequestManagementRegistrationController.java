package org.fenixedu.ulisboa.specifications.ui.ulisboaservicerequest;

import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class,
        title = "label.title.manageULisboaServiceRequest.create", accessGroup = "academic(SERVICE_REQUESTS)| #managers")
@RequestMapping(ULisboaServiceRequestManagementRegistrationController.CONTROLLER_URL)
public class ULisboaServiceRequestManagementRegistrationController extends FenixeduUlisboaSpecificationsBaseController {
    public static final String CONTROLLER_URL = "/ulisboaspecifications/ulisboaservicerequest/registration/select";

    @RequestMapping
    public String home(final Model model) {
        return "forward:" + ULisboaServiceRequestManagementController.CREATE_WITH_REGISTRATION_URL;
    }
}
