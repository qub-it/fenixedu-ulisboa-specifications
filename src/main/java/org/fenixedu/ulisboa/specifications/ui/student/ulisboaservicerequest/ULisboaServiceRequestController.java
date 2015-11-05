package org.fenixedu.ulisboa.specifications.ui.student.ulisboaservicerequest;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.qubdocs.ui.FenixeduQubdocsReportsController;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SpringFunctionality(app = FenixeduQubdocsReportsController.class, title = "label.title.student.manageULisboaServiceRequest",
        accessGroup = "logged")
@RequestMapping(ULisboaServiceRequestController.CONTROLLER_URL)
public class ULisboaServiceRequestController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/ulisboaspecifications/student/ulisboaservicerequest";

    @RequestMapping
    public String home(Model model, RedirectAttributes redirectAttributes) {
        return redirect(CHOOSE_REGISTRATION_URL, model, redirectAttributes);
    }

    private static final String _CHOOSE_REGISTRATION_URI = "/choose/registration/";
    public static final String CHOOSE_REGISTRATION_URL = CONTROLLER_URL + _CHOOSE_REGISTRATION_URI;

    @RequestMapping(value = _CHOOSE_REGISTRATION_URI, method = RequestMethod.GET)
    public String chooseRegistration(Model model, RedirectAttributes redirectAttributes) {
        List<Registration> registrations =
                AccessControl.getPerson().getStudent().getRegistrationsSet().stream().collect(Collectors.toList());
        if (registrations.size() == 1) {
            return redirect(READ_URL + registrations.get(0).getExternalId(), model, redirectAttributes);
        }
        model.addAttribute("registrationsSet", registrations);
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/student/chooseRegistration";
    }

    private static final String _READ_URI = "/read/registration/";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI + "{oid}", method = RequestMethod.GET)
    public String read(@PathVariable("oid") Registration registration, Model model) {
        model.addAttribute("registration", registration);
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/student/read";
    }

    private static final String _READ_SERVICE_REQUEST_URI = "/read/serviceRequest/";
    public static final String READ_SERVICE_REQUEST_URL = CONTROLLER_URL + _READ_SERVICE_REQUEST_URI;

    @RequestMapping(value = _READ_SERVICE_REQUEST_URI + "{oid}", method = RequestMethod.GET)
    public String readServiceRequest(@PathVariable("oid") ULisboaServiceRequest serviceRequest, Model model) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/read";
    }

    private static final String _HISTORY_ACADEMIC_REQUEST_URI = "/history/";
    public static final String HISTORY_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _HISTORY_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _HISTORY_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.GET)
    public String viewRequestHistory(@PathVariable(value = "oid") Registration registration, Model model) {
        model.addAttribute("registration", registration);
        model.addAttribute("uLisboaServiceRequestList",
                ULisboaServiceRequest.findByRegistration(registration).collect(Collectors.toList()));
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/history";
    }

}
