package org.fenixedu.ulisboa.specifications.ui.ulisboaservicerequest;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituationType;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentSigner;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.qubdocs.domain.serviceRequests.AcademicServiceRequestTemplate;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequestGeneratedDocument;
import org.fenixedu.ulisboa.specifications.dto.ULisboaServiceRequestBean;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.manageULisboaServiceRequest",
        accessGroup = "academic(SERVICE_REQUESTS)| #managers")
@RequestMapping(ULisboaServiceRequestManagementController.CONTROLLER_URL)
public class ULisboaServiceRequestManagementController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/ulisboaspecifications/ulisboaservicerequest";

    @RequestMapping
    public String home(Model model) {
        return "forward:" + SEARCH_URL;
    }

    private ULisboaServiceRequestBean getULisboaServiceRequestBean(Model model) {
        return (ULisboaServiceRequestBean) model.asMap().get("ulisboaServiceRequestBean");
    }

    private void setULisboaServiceRequestBean(ULisboaServiceRequestBean bean, Model model) {
        bean.updateModelLists();
        model.addAttribute("ulisboaServiceRequestBeanJson", getBeanJson(bean));
        model.addAttribute("ulisboaServiceRequestBean", bean);
    }

    private static final String _SEARCH_URI = "/search/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI)
    public String search(@RequestParam(value = "executionYear", required = false) ExecutionYear executionYear, @RequestParam(
            value = "degreeType", required = false) DegreeType degreeType,
            @RequestParam(value = "degree", required = false) Degree degree, @RequestParam(value = "serviceRequestType",
                    required = false) ServiceRequestType serviceRequestType,
            @RequestParam(value = "state", required = false) AcademicServiceRequestSituationType situationType, @RequestParam(
                    value = "urgent", required = false) boolean isUrgent, Model model) {
        model.addAttribute(
                "executionYearsList",
                ExecutionYear.readOpenExecutionYears().stream().sorted(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR)
                        .collect(Collectors.toList()));
        model.addAttribute("degreeTypesList", DegreeType.all().collect(Collectors.toList()));
        model.addAttribute("degreesList", Degree.readAllMatching(dT -> degreeType == null || dT == degreeType));
        model.addAttribute("serviceRequestTypesList", ServiceRequestType.findAll().collect(Collectors.toList()));
        model.addAttribute("states", ULisboaConstants.USED_SITUATION_TYPES);
        model.addAttribute("searchServiceRequestsSet",
                filterSearchServiceRequest(executionYear, degreeType, degree, serviceRequestType, situationType, isUrgent));
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/search";
    }

    private List<ULisboaServiceRequest> filterSearchServiceRequest(ExecutionYear executionYear, DegreeType degreeType,
            Degree degree, ServiceRequestType serviceRequestType, AcademicServiceRequestSituationType situationType,
            boolean isUrgent) {
        return ULisboaServiceRequest
                .findAll()
                .filter(req -> executionYear == null || req.hasExecutionYear() && req.getExecutionYear().equals(executionYear))
                .filter(req -> degreeType == null || req.getRegistration().getDegree().getDegreeType().equals(degreeType))
                .filter(req -> degree == null || req.getRegistration().getDegree().equals(degree))
                .filter(req -> serviceRequestType == null || req.getServiceRequestType().equals(serviceRequestType))
                .filter(req -> situationType == null
                        || req.getActiveSituation().getAcademicServiceRequestSituationType().equals(situationType))
                .filter(req -> req.isUrgent() == isUrgent).collect(Collectors.toList());
    }

    private static final String _CREATE_URI = "/create/";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI + "{oid}", method = RequestMethod.GET)
    public String createAcademicRequest(@PathVariable(value = "oid") Registration registration, Model model) {
        if (getULisboaServiceRequestBean(model) == null) {
            setULisboaServiceRequestBean(new ULisboaServiceRequestBean(registration, false), model);
        }
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/create";
    }

    @RequestMapping(value = _CREATE_URI + "{oid}", method = RequestMethod.POST)
    public String createAcademicRequest(@PathVariable(value = "oid") Registration registration, @RequestParam(value = "bean",
            required = true) ULisboaServiceRequestBean bean, Model model, RedirectAttributes redirectAttributes) {
        setULisboaServiceRequestBean(bean, model);

        try {
            ULisboaServiceRequest serviceRequest = ULisboaServiceRequest.createULisboaServiceRequest(bean);
            return redirect(READ_ACADEMIC_REQUEST_URL + serviceRequest.getExternalId(), model, redirectAttributes);
        } catch (DomainException | ULisboaSpecificationsDomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }
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
        model.addAttribute("documentSignatures",
                DocumentSigner.findAll().filter(ds -> ds.getAdministrativeOffice() == serviceRequest.getAdministrativeOffice())
                        .sorted(DocumentSigner.DEFAULT_COMPARATOR).collect(Collectors.toList()));
        addDocumentTemplatesToModel(serviceRequest, model);
        if (!serviceRequest.getIsValid() && (serviceRequest.isNewRequest() || serviceRequest.isProcessing())) {
            addWarningMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, "label.ULisboaServiceRequest.is.invalid.warning"),
                    model);
            addWarningMessage(
                    BundleUtil.getString(ULisboaConstants.BUNDLE, "label.ULisboaServiceRequest.invalid.instruction.one"), model);
            addWarningMessage(
                    BundleUtil.getString(ULisboaConstants.BUNDLE, "label.ULisboaServiceRequest.invalid.instruction.two"), model);
        }
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/read";
    }

    private void addDocumentTemplatesToModel(ULisboaServiceRequest serviceRequest, Model model) {
        Locale language = serviceRequest.getLanguage();

        AcademicServiceRequestTemplate standardTemplate =
                AcademicServiceRequestTemplate.findTemplateFor(language, serviceRequest.getServiceRequestType(), serviceRequest
                        .getRegistration().getDegree().getDegreeType(), null, serviceRequest.getRegistration().getDegree());
        Set<AcademicServiceRequestTemplate> templates =
                AcademicServiceRequestTemplate.readCustomTemplatesFor(language, serviceRequest.getServiceRequestType());
        if (standardTemplate != null) {
            templates.add(standardTemplate);
        }
        model.addAttribute("templates", templates);
    }

    private static final String _PROCESS_ACADEMIC_REQUEST_URI = "/process/";
    public static final String PROCESS_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _PROCESS_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _PROCESS_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String process(@PathVariable(value = "oid") ULisboaServiceRequest serviceRequest, @RequestParam(value = "redirect",
            required = false, defaultValue = "false") boolean redirectToReferrer, Model model,
            RedirectAttributes redirectAttributes) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            serviceRequest.transitToProcessState();
            addInfoMessage(
                    BundleUtil.getString(ULisboaConstants.BUNDLE, "info.serviceRequests.ulisboarequest.processed.success"), model);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }
        if (redirectToReferrer) {
            return redirectToReferrer(model, redirectAttributes);
        }
        return redirect(READ_ACADEMIC_REQUEST_URL + serviceRequest.getExternalId(), model, redirectAttributes);
    }

    private static final String _CONCLUDE_ACADEMIC_REQUEST_URI = "/conclude/";
    public static final String CONCLUDE_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _CONCLUDE_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _CONCLUDE_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String conclude(@PathVariable(value = "oid") ULisboaServiceRequest serviceRequest, @RequestParam(value = "redirect",
            required = false, defaultValue = "false") boolean redirectToReferrer, Model model,
            RedirectAttributes redirectAttributes) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            serviceRequest.transitToConcludedState();
            addInfoMessage(
                    BundleUtil.getString(ULisboaConstants.BUNDLE, "info.serviceRequests.ulisboarequest.concluded.success"), model);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }
        if (redirectToReferrer) {
            return redirectToReferrer(model, redirectAttributes);
        }
        return redirect(READ_ACADEMIC_REQUEST_URL + serviceRequest.getExternalId(), model, redirectAttributes);
    }

    private static final String _DELIVER_ACADEMIC_REQUEST_URI = "/deliver/";
    public static final String DELIVER_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _DELIVER_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _DELIVER_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String deliver(@PathVariable(value = "oid") ULisboaServiceRequest serviceRequest, @RequestParam(value = "redirect",
            required = false, defaultValue = "false") boolean redirectToReferrer, Model model,
            RedirectAttributes redirectAttributes) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            serviceRequest.transitToDeliverState();
            addInfoMessage(
                    BundleUtil.getString(ULisboaConstants.BUNDLE, "info.serviceRequests.ulisboarequest.delivered.success"), model);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }
        if (redirectToReferrer) {
            return redirectToReferrer(model, redirectAttributes);
        }
        return redirect(READ_ACADEMIC_REQUEST_URL + serviceRequest.getExternalId(), model, redirectAttributes);
    }

    private static final String _CANCEL_ACADEMIC_REQUEST_URI = "/cancel/";
    public static final String CANCEL_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _CANCEL_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _CANCEL_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String cancel(@PathVariable(value = "oid") ULisboaServiceRequest serviceRequest, @RequestParam(
            value = "justification", required = true) String justification, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            serviceRequest.transitToCancelState(justification);
            addInfoMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, "info.serviceRequests.ulisboarequest.canceled.success"),
                    model);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }
        return redirect(READ_ACADEMIC_REQUEST_URL + serviceRequest.getExternalId(), model, redirectAttributes);
    }

    private static final String _REJECT_ACADEMIC_REQUEST_URI = "/reject/";
    public static final String REJECT_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _REJECT_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _REJECT_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String reject(@PathVariable(value = "oid") ULisboaServiceRequest serviceRequest, @RequestParam(
            value = "justification", required = true) String justification, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            serviceRequest.transitToRejectState(justification);
            addInfoMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, "info.serviceRequests.ulisboarequest.rejected.success"),
                    model);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }
        return redirect(READ_ACADEMIC_REQUEST_URL + serviceRequest.getExternalId(), model, redirectAttributes);
    }

    private static final String _PRINT_ACADEMIC_REQUEST_URI = "/print/";
    public static final String PRINT_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _PRINT_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _PRINT_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String print(@PathVariable(value = "oid") ULisboaServiceRequest serviceRequest, @RequestParam(value = "template",
            required = true) AcademicServiceRequestTemplate template,
            @RequestParam(value = "signature", required = true) DocumentSigner signer, Model model,
            RedirectAttributes redirectAttributes, HttpServletResponse response) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            serviceRequest.setPrintSettings(signer, template);
            serviceRequest.generateDocument();
            download(serviceRequest, model, response);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }
        return redirect(READ_ACADEMIC_REQUEST_URL + serviceRequest.getExternalId(), model, redirectAttributes);
    }

    private static final String _DOWNLOAD_PRINTED_ACADEMIC_REQUEST_URI = "/download/";
    public static final String DOWNLOAD_PRINTED_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _DOWNLOAD_PRINTED_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _DOWNLOAD_PRINTED_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.GET)
    public void download(@PathVariable(value = "oid") ULisboaServiceRequest serviceRequest, Model model,
            HttpServletResponse response) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            ULisboaServiceRequestGeneratedDocument document = serviceRequest.downloadDocument();
            response.setContentType(document.getContentType());
            response.setHeader("Content-disposition", "attachment; filename=" + document.getFilename());
            response.getOutputStream().write(document.getContent());
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String _REVERT_ACADEMIC_REQUEST_URI = "/revert/";
    public static final String REVERT_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _REVERT_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _REVERT_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String revert(@PathVariable(value = "oid") ULisboaServiceRequest serviceRequest, @RequestParam(
            value = "notifyRevertAction", required = true) boolean notifyRevertAction, Model model,
            RedirectAttributes redirectAttributes) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            serviceRequest.revertState(notifyRevertAction);
            addInfoMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, "info.serviceRequests.ulisboarequest.reverted.success",
                    serviceRequest.getAcademicServiceRequestSituationType().getLocalizedName()), model);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }
        return redirect(READ_ACADEMIC_REQUEST_URL + serviceRequest.getExternalId(), model, redirectAttributes);
    }

}
