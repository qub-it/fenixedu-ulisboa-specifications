package org.fenixedu.ulisboa.specifications.ui.student.ulisboaservicerequest;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.treasury.TreasuryBridgeAPIFactory;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.dto.ULisboaServiceRequestBean;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.joda.time.LocalDate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class,
        title = "label.title.student.manageULisboaServiceRequest", accessGroup = "logged")
@RequestMapping(ULisboaServiceRequestController.CONTROLLER_URL)
public class ULisboaServiceRequestController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/ulisboaspecifications/student/ulisboaservicerequest";

    private ULisboaServiceRequestBean getULisboaServiceRequestBean(Model model) {
        return (ULisboaServiceRequestBean) model.asMap().get("ulisboaServiceRequestBean");
    }

    private void setULisboaServiceRequestBean(ULisboaServiceRequestBean bean, Model model) {
        bean.updateModelLists();
        model.addAttribute("ulisboaServiceRequestBeanJson", getBeanJson(bean));
        model.addAttribute("ulisboaServiceRequestBean", bean);
    }

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
            return redirect(READ_REGISTRATION_URL + registrations.get(0).getExternalId(), model, redirectAttributes);
        }
        model.addAttribute("registrationsSet", registrations);
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/student/chooseRegistration";
    }

    private static final String _READ_REGISTRATION_URI = "/read/registration/";
    public static final String READ_REGISTRATION_URL = CONTROLLER_URL + _READ_REGISTRATION_URI;

    @RequestMapping(value = _READ_REGISTRATION_URI + "{oid}", method = RequestMethod.GET)
    public String read(@PathVariable("oid") Registration registration, Model model) {
        model.addAttribute("registration", registration);
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/student/read";
    }

    private static final String _CREATE_SERVICE_REQUEST_URI = "/create/serviceRequest/";
    public static final String CREATE_SERVICE_REQUEST_URL = CONTROLLER_URL + _CREATE_SERVICE_REQUEST_URI;

    @RequestMapping(value = _CREATE_SERVICE_REQUEST_URI + "{oid}", method = RequestMethod.GET)
    public String create(@PathVariable("oid") Registration registration, Model model, RedirectAttributes redirectAttributes) {
        if (TreasuryBridgeAPIFactory.implementation().isAcademicalActsBlocked(AccessControl.getPerson(), new LocalDate())) {
            addErrorMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, "error.serviceRequest.create.actsBlocked"), model);
            return redirect(READ_REGISTRATION_URL + registration.getExternalId(), model, redirectAttributes);
        }
        if (getULisboaServiceRequestBean(model) == null) {
            setULisboaServiceRequestBean(new ULisboaServiceRequestBean(registration), model);
        }
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/create";
    }

    @RequestMapping(value = _CREATE_SERVICE_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String createAcademicRequest(@PathVariable(value = "oid") Registration registration, @RequestParam(value = "bean",
            required = true) ULisboaServiceRequestBean bean, Model model, RedirectAttributes redirectAttributes) {
        setULisboaServiceRequestBean(bean, model);
        if (TreasuryBridgeAPIFactory.implementation().isAcademicalActsBlocked(AccessControl.getPerson(), new LocalDate())) {
            addErrorMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, "error.serviceRequest.create.actsBlocked"), model);
            return redirect(READ_REGISTRATION_URL + registration.getExternalId(), model, redirectAttributes);
        }
        ULisboaServiceRequest serviceRequest = ULisboaServiceRequest.createULisboaServiceRequest(bean);

        return redirect(READ_SERVICE_REQUEST_URL + serviceRequest.getExternalId(), model, redirectAttributes);
    }

    private static final String _CREATE_SERVICE_REQUEST_POSTBACK_URI = "/create/serviceRequest/postBack";
    public static final String CREATE_SERVICE_REQUEST_POSTBACK_URL = CONTROLLER_URL + _CREATE_SERVICE_REQUEST_POSTBACK_URI;

    @RequestMapping(value = _CREATE_SERVICE_REQUEST_POSTBACK_URI, method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody String createpostback(@RequestParam(value = "bean", required = true) ULisboaServiceRequestBean bean,
            Model model) {
        setULisboaServiceRequestBean(bean, model);
        return getBeanJson(bean);
    }

    private static final String _READ_SERVICE_REQUEST_URI = "/read/serviceRequest/";
    public static final String READ_SERVICE_REQUEST_URL = CONTROLLER_URL + _READ_SERVICE_REQUEST_URI;

    @RequestMapping(value = _READ_SERVICE_REQUEST_URI + "{oid}", method = RequestMethod.GET)
    public String readServiceRequest(@PathVariable("oid") ULisboaServiceRequest serviceRequest, Model model) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/read";
    }

    private static final String _HISTORY_SERVICE_REQUEST_URI = "/history/";
    public static final String HISTORY_SERVICE_REQUEST_URL = CONTROLLER_URL + _HISTORY_SERVICE_REQUEST_URI;

    @RequestMapping(value = _HISTORY_SERVICE_REQUEST_URI + "{oid}", method = RequestMethod.GET)
    public String viewRequestHistory(@PathVariable(value = "oid") Registration registration, Model model) {
        model.addAttribute("registration", registration);
        model.addAttribute("uLisboaServiceRequestList",
                ULisboaServiceRequest.findByRegistration(registration).collect(Collectors.toList()));
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/history";
    }

}
