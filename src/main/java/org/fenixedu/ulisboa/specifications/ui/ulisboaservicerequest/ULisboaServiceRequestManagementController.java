package org.fenixedu.ulisboa.specifications.ui.ulisboaservicerequest;

import java.util.stream.Collectors;

import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.qubdocs.ui.FenixeduQubdocsReportsController;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.dto.ULisboaServiceRequestBean;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.util.Constants;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@BennuSpringController(FenixeduQubdocsReportsController.class)
@RequestMapping(ULisboaServiceRequestManagementController.CONTROLLER_URL)
public class ULisboaServiceRequestManagementController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/ulisboaspecifications/ulisboaservicerequest";

    private ULisboaServiceRequestBean getULisboaServiceRequestBean(Model model) {
        return (ULisboaServiceRequestBean) model.asMap().get("ulisboaServiceRequestBean");
    }

    private void setULisboaServiceRequestBean(ULisboaServiceRequestBean bean, Model model) {
        bean.updateModelLists();
        model.addAttribute("ulisboaServiceRequestBeanJson", getBeanJson(bean));
        model.addAttribute("ulisboaServiceRequestBean", bean);
    }

    private static final String _CREATE_URI = "/create/";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI + "{oid}", method = RequestMethod.GET)
    public String createAcademicRequest(@PathVariable(value = "oid") Registration registration, Model model) {
        if (getULisboaServiceRequestBean(model) == null) {
            setULisboaServiceRequestBean(new ULisboaServiceRequestBean(registration), model);
        }
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/create";
    }

    @RequestMapping(value = _CREATE_URI + "{oid}", method = RequestMethod.POST)
    public String createAcademicRequest(@PathVariable(value = "oid") Registration registration, @RequestParam(value = "bean",
            required = true) ULisboaServiceRequestBean bean, Model model) {
        setULisboaServiceRequestBean(bean, model);

        ULisboaServiceRequest request = ULisboaServiceRequest.createULisboaServiceRequest(bean);

        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/create";
    }

    private static final String _CREATE_POSTBACK_URI = "/createpostback/";
    public static final String CREATE_POSTBACK_URL = CONTROLLER_URL + _CREATE_POSTBACK_URI;

    @RequestMapping(value = _CREATE_POSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String createpostback(@RequestParam(value = "bean", required = true) ULisboaServiceRequestBean bean,
            Model model) {
        setULisboaServiceRequestBean(bean, model);
        return getBeanJson(bean);
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

    private static final String _READ_ACADEMIC_REQUEST_URI = "/read/";
    public static final String READ_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _READ_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _READ_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.GET)
    public String read(@PathVariable(value = "oid") ULisboaServiceRequest serviceRequest, Model model) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/read";
    }

    private static final String _PROCESS_ACADEMIC_REQUEST_URI = "/process/";
    public static final String PROCESS_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _PROCESS_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _PROCESS_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String process(@PathVariable(value = "oid") ULisboaServiceRequest serviceRequest, Model model) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            serviceRequest.transitToProcessState();
            addInfoMessage(BundleUtil.getString(Constants.BUNDLE, "label.serviceRequests.ulisboarequest.processed.success"),
                    model);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/read";
    }

    private static final String _CONCLUDE_ACADEMIC_REQUEST_URI = "/conclude/";
    public static final String CONCLUDE_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _CONCLUDE_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _CONCLUDE_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String conclude(@PathVariable(value = "oid") ULisboaServiceRequest serviceRequest, Model model) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            serviceRequest.transitToConcludedState();
            addInfoMessage(BundleUtil.getString(Constants.BUNDLE, "label.serviceRequests.ulisboarequest.concluded.success"),
                    model);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/read";
    }

    private static final String _DELIVER_ACADEMIC_REQUEST_URI = "/deliver/";
    public static final String DELIVER_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _DELIVER_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _DELIVER_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String deliver(@PathVariable(value = "oid") ULisboaServiceRequest serviceRequest, Model model) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            serviceRequest.transitToDeliverState();
            addInfoMessage(BundleUtil.getString(Constants.BUNDLE, "label.serviceRequests.ulisboarequest.delivered.success"),
                    model);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/read";
    }

    private static final String _CANCEL_ACADEMIC_REQUEST_URI = "/cancel/";
    public static final String CANCEL_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _CANCEL_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _CANCEL_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String cancel(@PathVariable(value = "oid") ULisboaServiceRequest serviceRequest, @RequestParam(
            value = "justification", required = true) String justification, Model model) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            serviceRequest.transitToCancelState(justification);
            addInfoMessage(BundleUtil.getString(Constants.BUNDLE, "label.serviceRequests.ulisboarequest.canceled.success"), model);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/read";
    }

    private static final String _REJECT_ACADEMIC_REQUEST_URI = "/reject/";
    public static final String REJECT_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _REJECT_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _REJECT_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String reject(@PathVariable(value = "oid") ULisboaServiceRequest serviceRequest, @RequestParam(
            value = "justification", required = true) String justification, Model model) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            serviceRequest.transitToProcessState();
            addInfoMessage(BundleUtil.getString(Constants.BUNDLE, "label.serviceRequests.ulisboarequest.rejected.success"), model);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/read";
    }

    private static final String _PRINT_ACADEMIC_REQUEST_URI = "/print/";
    public static final String PRINT_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _PRINT_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _PRINT_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.GET)
    public String print(@PathVariable(value = "oid") ULisboaServiceRequest serviceRequest, Model model) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            //???????????????????????
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/read";
    }

    private static final String _REVERT_ACADEMIC_REQUEST_URI = "/revert/";
    public static final String REVERT_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _REVERT_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _REVERT_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String revert(@PathVariable(value = "oid") ULisboaServiceRequest serviceRequest, Model model) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            //???????????????????????
            addInfoMessage(BundleUtil.getString(Constants.BUNDLE, "label.serviceRequests.ulisboarequest.reverted.success"), model);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/read";
    }

}
