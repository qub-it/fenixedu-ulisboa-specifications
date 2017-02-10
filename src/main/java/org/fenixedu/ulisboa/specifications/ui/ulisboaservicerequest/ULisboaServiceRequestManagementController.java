package org.fenixedu.ulisboa.specifications.ui.ulisboaservicerequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituationType;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestYear;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentSigner;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.qubdocs.domain.serviceRequests.AcademicServiceRequestTemplate;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.fenixedu.treasury.domain.paymentcodes.PaymentReferenceCode;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlotEntry;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.UIComponentType;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequestGeneratedDocument;
import org.fenixedu.ulisboa.specifications.dto.ServiceRequestPropertiesBean;
import org.fenixedu.ulisboa.specifications.dto.ServiceRequestPropertyBean;
import org.fenixedu.ulisboa.specifications.dto.ULisboaServiceRequestBean;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.joda.time.DateTime;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.JsonParser;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.manageULisboaServiceRequest",
        accessGroup = "academic(SERVICE_REQUESTS)| #managers")
@RequestMapping(ULisboaServiceRequestManagementController.CONTROLLER_URL)
public class ULisboaServiceRequestManagementController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/ulisboaspecifications/ulisboaservicerequest";
    public static final int SEARCH_REQUEST_LIST_LIMIT_SIZE = 300;
    public static final int FIRST_CIVIL_YEAR = 2000;
    public static final Comparator<Integer> INTEGER_COMPARATOR = (i1, i2) -> i1.compareTo(i2);

    private static final String ERROR_MESSAGE_ATTRIBUTE = "sessionErrorMessages";

    @RequestMapping
    public String home(final Model model) {
        return "forward:" + SEARCH_URL;
    }

    private ULisboaServiceRequestBean getULisboaServiceRequestBean(final Model model) {
        return (ULisboaServiceRequestBean) model.asMap().get("ulisboaServiceRequestBean");
    }

    private void setULisboaServiceRequestBean(final ULisboaServiceRequestBean bean, final Model model) {
        bean.updateModelLists();
        model.addAttribute("ulisboaServiceRequestBeanJson", getBeanJson(bean));
        model.addAttribute("ulisboaServiceRequestBean", bean);
    }

    private void setServiceRequestPropertiesBean(final ServiceRequestPropertiesBean bean, final Model model) {
        bean.updateLists();
        model.addAttribute("serviceRequestPropertiesBeanJson", getBeanJson(bean));
        model.addAttribute("serviceRequestPropertiesBean", bean);
    }

    private static final String _SEARCH_URI = "/search/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI)
    public String search(@RequestParam(value = "civilYear", required = false) Integer civilYear,
            @RequestParam(value = "degreeType", required = false) final DegreeType degreeType,
            @RequestParam(value = "degree", required = false) final Degree degree,
            @RequestParam(value = "serviceRequestType", required = false) final ServiceRequestType serviceRequestType,
            @RequestParam(value = "state", required = false) final AcademicServiceRequestSituationType situationType,
            @RequestParam(value = "urgent", required = false) final boolean isUrgent,
            @RequestParam(value = "selfIssued", required = false) final boolean isSelfIssued,
            @RequestParam(value = "requestNumber", required = false) final String requestNumber,
            @RequestParam(value = "payed", required = false) final Boolean isPayed, final Model model) {

        Set<Integer> years = new HashSet<>();
        Collection<AcademicServiceRequestYear> requestYears = Bennu.getInstance().getAcademicServiceRequestYearsSet();
        for (AcademicServiceRequestYear requestYear : requestYears) {
            years.add(requestYear.getYear());
        }
        List<Integer> yearsList = new ArrayList<>(years);
        Collections.sort(yearsList, INTEGER_COMPARATOR.reversed());
        model.addAttribute("civilYearsList", yearsList);

        List<DegreeType> degreeTypes = new ArrayList<>(DegreeType.all().collect(Collectors.toList()));
        Collections.sort(degreeTypes, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        model.addAttribute("degreeTypesList", degreeTypes);

        List<Degree> degrees = Degree.readAllMatching(dT -> degreeType == null || dT == degreeType);
        Collections.sort(degrees, Degree.COMPARATOR_BY_NAME_AND_ID);
        model.addAttribute("degreesList", degrees);

        List<ServiceRequestType> serviceRequestTypes = ServiceRequestType.findAll().collect(Collectors.toList());
        Collections.sort(serviceRequestTypes, ServiceRequestType.COMPARE_BY_CATEGORY_THEN_BY_NAME);
        model.addAttribute("serviceRequestTypesList", serviceRequestTypes);

        model.addAttribute("states", ULisboaConstants.USED_SITUATION_TYPES);
        if (civilYear == null) {
            model.addAttribute("currentCivilYear", yearsList.get(0));
            civilYear = yearsList.get(0);
        }
        model.addAttribute("searchServiceRequestsSet", filterSearchServiceRequest(civilYear, degreeType, degree,
                serviceRequestType, situationType, isUrgent, isSelfIssued, requestNumber, isPayed));
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/search";
    }

    private List<ULisboaServiceRequest> filterSearchServiceRequest(final int civilYear, final DegreeType degreeType,
            final Degree degree, final ServiceRequestType serviceRequestType,
            final AcademicServiceRequestSituationType situationType, final boolean isUrgent, final boolean isSelfIssued,
            final String requestNumber, final Boolean isPayed) {
        AcademicServiceRequestYear requestsYear = AcademicServiceRequestYear.readByYear(civilYear, false);

        return requestsYear.getAcademicServiceRequestsSet().stream().filter(req -> req instanceof ULisboaServiceRequest)
                .map(ULisboaServiceRequest.class::cast)
                .filter(req -> degreeType == null || req.getRegistration().getDegree().getDegreeType().equals(degreeType))
                .filter(req -> degree == null || req.getRegistration().getDegree().equals(degree))
                .filter(req -> serviceRequestType == null || req.getServiceRequestType().equals(serviceRequestType))
                .filter(req -> situationType == null
                        || req.getActiveSituation().getAcademicServiceRequestSituationType().equals(situationType))
                .filter(req -> req.isUrgent() == isUrgent).filter(req -> req.isSelfIssued() == isSelfIssued)
                .filter(req -> requestNumber == null || req.getServiceRequestNumberYear().contains(requestNumber))
                .filter(req -> isPayed == null || !AcademicTreasuryEvent.findUnique(req).isPresent() && isPayed
                        || !AcademicTreasuryEvent.findUnique(req).get().isCharged() && isPayed
                        || AcademicTreasuryEvent.findUnique(req).isPresent()
                                && isPayed.equals(!AcademicTreasuryEvent.findUnique(req).get().isInDebt()))
                .limit(SEARCH_REQUEST_LIST_LIMIT_SIZE).collect(Collectors.toList());
    }

    private static final String _CREATE_URI = "/create/";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI + "{oid}", method = RequestMethod.GET)
    public String createAcademicRequest(@PathVariable(value = "oid") final Registration registration, final Model model) {
        if (getULisboaServiceRequestBean(model) == null) {
            setULisboaServiceRequestBean(new ULisboaServiceRequestBean(registration, false), model);
        }
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/create";
    }

    @RequestMapping(value = _CREATE_URI + "{oid}", method = RequestMethod.POST)
    public String createAcademicRequest(@PathVariable(value = "oid") final Registration registration,
            @RequestParam(value = "bean", required = true) final ULisboaServiceRequestBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {
        setULisboaServiceRequestBean(bean, model);

        try {
            ULisboaServiceRequest serviceRequest = ULisboaServiceRequest.create(bean);
            return redirect(READ_ACADEMIC_REQUEST_URL + serviceRequest.getExternalId(), model, redirectAttributes);
        } catch (DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        } catch (org.fenixedu.bennu.core.domain.exceptions.DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        } catch (RuntimeException e) {
            addErrorMessage(e.getMessage(), model);
        }
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/create";
    }

    private static final String _CREATE_POSTBACK_URI = "/createpostback/";
    public static final String CREATE_POSTBACK_URL = CONTROLLER_URL + _CREATE_POSTBACK_URI;

    @RequestMapping(value = _CREATE_POSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String createpostback(
            @RequestParam(value = "bean", required = true) final ULisboaServiceRequestBean bean, final Model model) {
        setULisboaServiceRequestBean(bean, model);
        return getBeanJson(bean);
    }

    private static final String _HISTORY_ACADEMIC_REQUEST_URI = "/history/";
    public static final String HISTORY_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _HISTORY_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _HISTORY_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.GET)
    public String viewRequestHistory(@PathVariable(value = "oid") final Registration registration, final Model model) {
        model.addAttribute("registration", registration);
        model.addAttribute("uLisboaServiceRequestList",
                ULisboaServiceRequest.findByRegistration(registration).collect(Collectors.toList()));
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/history";
    }

    private static final String _READ_ACADEMIC_REQUEST_URI = "/read/";
    public static final String READ_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _READ_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _READ_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.GET)
    public String read(@PathVariable(value = "oid") final ULisboaServiceRequest serviceRequest, final Model model,
            final HttpServletRequest request) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        model.addAttribute("documentSignatures",
                DocumentSigner.findAll().filter(ds -> ds.getAdministrativeOffice() == serviceRequest.getAdministrativeOffice())
                        .sorted(DocumentSigner.DEFAULT_COMPARATOR).collect(Collectors.toList()));
        addDocumentTemplatesToModel(serviceRequest, model);

        model.addAttribute("printProperties",
                serviceRequest.getServiceRequestType().getServiceRequestSlotEntriesSet().stream()
                        .filter(ServiceRequestSlotEntry.PRINT_PROPERTY).sorted(ServiceRequestSlotEntry.COMPARE_BY_ORDER_NUMBER)
                        .collect(Collectors.toList()));

        if (request.getSession().getAttribute(ERROR_MESSAGE_ATTRIBUTE) != null) {
            List<String> errorMessages = (List<String>) request.getSession().getAttribute(ERROR_MESSAGE_ATTRIBUTE);
            for (String errorMessage : errorMessages) {
                addErrorMessage(errorMessage, model);
            }
            request.getSession().removeAttribute(ERROR_MESSAGE_ATTRIBUTE);
        }
        if (serviceRequest.getAcademicTreasuryEvent() != null) {
            List<DebitEntry> activeDebitEntries =
                    DebitEntry.findActive(serviceRequest.getAcademicTreasuryEvent()).collect(Collectors.<DebitEntry> toList());
            model.addAttribute("activeDebitEntries", activeDebitEntries);
            if (isAnyPaymentCodeInUsedState(activeDebitEntries)) {
                addWarningMessage(
                        BundleUtil.getString(ULisboaConstants.BUNDLE, "label.ULisboaServiceRequest.need.to.do.the.payment"),
                        model);
            }
        } else {
            model.addAttribute("activeDebitEntries", Collections.emptyList());
        }

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

    private boolean isAnyPaymentCodeInUsedState(final List<DebitEntry> activeDebitEntries) {
        List<PaymentReferenceCode> paymentReferenceCodes = activeDebitEntries.stream().map(e -> e.getPaymentCodesSet())
                .flatMap(x -> x.stream()).map(pc -> pc.getPaymentReferenceCode()).collect(Collectors.toList());
        return paymentReferenceCodes.stream().filter(p -> p.getState().isUsed()).count() > 0;
    }

    private void addDocumentTemplatesToModel(final ULisboaServiceRequest serviceRequest, final Model model) {
        Locale language = serviceRequest.getLanguage();

        Set<AcademicServiceRequestTemplate> templates = new TreeSet<>((t1, t2) -> {
            if (!t1.getCustom() && t2.getCustom()) {
                return -1;
            } else if (t1.getCustom() && !t2.getCustom()) {
                return 1;
            } else {
                return t1.getName().getContent().compareTo(t2.getName().getContent());
            }
        });

        AcademicServiceRequestTemplate standardTemplate = AcademicServiceRequestTemplate.findTemplateFor(language,
                serviceRequest.getServiceRequestType(), serviceRequest.getRegistration().getDegree().getDegreeType(),
                serviceRequest.getProgramConclusion(), serviceRequest.getRegistration().getDegree());
        Set<AcademicServiceRequestTemplate> cutomTemplates =
                AcademicServiceRequestTemplate.readCustomTemplatesFor(language, serviceRequest.getServiceRequestType());

        if (standardTemplate != null) {
            templates.add(standardTemplate);
        }
        templates.addAll(cutomTemplates);
        model.addAttribute("templates", templates);
    }

    private static final String _UPDATE_URI = "/update/";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI + "{oid}", method = RequestMethod.GET)
    public String updateAcademicRequest(@PathVariable(value = "oid") final ULisboaServiceRequest request, final Model model) {
        if (getULisboaServiceRequestBean(model) == null) {
            ULisboaServiceRequestBean bean = new ULisboaServiceRequestBean(request);
            model.addAttribute("ulisboaServiceRequestBeanJson", getBeanJson(bean));
            model.addAttribute("ulisboaServiceRequestBean", bean);
        }
        model.addAttribute("serviceRequest", request);
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/update";
    }

    @RequestMapping(value = _UPDATE_URI + "{oid}", method = RequestMethod.POST)
    public String updateAcademicRequest(@PathVariable(value = "oid") final ULisboaServiceRequest serviceRequest,
            @RequestParam(value = "bean", required = true) final ULisboaServiceRequestBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {
        model.addAttribute("ulisboaServiceRequestBeanJson", getBeanJson(bean));
        model.addAttribute("ulisboaServiceRequestBean", bean);

        try {
            serviceRequest.update(bean);
            return redirect(READ_ACADEMIC_REQUEST_URL + serviceRequest.getExternalId(), model, redirectAttributes);
        } catch (DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        } catch (org.fenixedu.bennu.core.domain.exceptions.DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }
        return "fenixedu-ulisboa-specifications/servicerequests/ulisboarequest/update";
    }

    private static final String _PROCESS_ACADEMIC_REQUEST_URI = "/process/";
    public static final String PROCESS_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _PROCESS_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _PROCESS_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String process(@PathVariable(value = "oid") final ULisboaServiceRequest serviceRequest,
            @RequestParam(value = "redirect", required = false, defaultValue = "false") final boolean redirectToReferrer,
            final Model model, final RedirectAttributes redirectAttributes) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            serviceRequest.transitToProcessState();
            addInfoMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, "info.serviceRequests.ulisboarequest.processed.success"),
                    model);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        } catch (org.fenixedu.bennu.core.domain.exceptions.DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }
        if (redirectToReferrer) {
            return redirectToReferrer(model, redirectAttributes);
        }
        return redirect(READ_ACADEMIC_REQUEST_URL + serviceRequest.getExternalId(), model, redirectAttributes);
    }

    private static final String _CONCLUDE_ACADEMIC_REQUEST_URI = "/conclude/";
    public static final String CONCLUDE_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _CONCLUDE_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _CONCLUDE_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String conclude(@PathVariable(value = "oid") final ULisboaServiceRequest serviceRequest,
            @RequestParam(value = "redirect", required = false, defaultValue = "false") final boolean redirectToReferrer,
            final Model model, final RedirectAttributes redirectAttributes) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            serviceRequest.transitToConcludedState();
            addInfoMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, "info.serviceRequests.ulisboarequest.concluded.success"),
                    model);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        } catch (org.fenixedu.bennu.core.domain.exceptions.DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }
        if (redirectToReferrer) {
            return redirectToReferrer(model, redirectAttributes);
        }
        return redirect(READ_ACADEMIC_REQUEST_URL + serviceRequest.getExternalId(), model, redirectAttributes);
    }

    private static final String _DELIVER_ACADEMIC_REQUEST_URI = "/deliver/";
    public static final String DELIVER_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _DELIVER_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _DELIVER_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String deliver(@PathVariable(value = "oid") final ULisboaServiceRequest serviceRequest,
            @RequestParam(value = "redirect", required = false, defaultValue = "false") final boolean redirectToReferrer,
            final Model model, final RedirectAttributes redirectAttributes) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            serviceRequest.transitToDeliverState();
            addInfoMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, "info.serviceRequests.ulisboarequest.delivered.success"),
                    model);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        } catch (org.fenixedu.bennu.core.domain.exceptions.DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }
        if (redirectToReferrer) {
            return redirectToReferrer(model, redirectAttributes);
        }
        return redirect(READ_ACADEMIC_REQUEST_URL + serviceRequest.getExternalId(), model, redirectAttributes);
    }

    private static final String _CANCEL_ACADEMIC_REQUEST_URI = "/cancel/";
    public static final String CANCEL_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _CANCEL_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _CANCEL_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String cancel(@PathVariable(value = "oid") final ULisboaServiceRequest serviceRequest,
            @RequestParam(value = "justification", required = true) final String justification, final Model model,
            final RedirectAttributes redirectAttributes) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            serviceRequest.transitToCancelState(justification);
            addInfoMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, "info.serviceRequests.ulisboarequest.canceled.success"),
                    model);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        } catch (org.fenixedu.bennu.core.domain.exceptions.DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }
        return redirect(READ_ACADEMIC_REQUEST_URL + serviceRequest.getExternalId(), model, redirectAttributes);
    }

    private static final String _REJECT_ACADEMIC_REQUEST_URI = "/reject/";
    public static final String REJECT_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _REJECT_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _REJECT_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String reject(@PathVariable(value = "oid") final ULisboaServiceRequest serviceRequest,
            @RequestParam(value = "justification", required = true) final String justification, final Model model,
            final RedirectAttributes redirectAttributes) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            serviceRequest.transitToRejectState(justification);
            addInfoMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, "info.serviceRequests.ulisboarequest.rejected.success"),
                    model);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        } catch (org.fenixedu.bennu.core.domain.exceptions.DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }
        return redirect(READ_ACADEMIC_REQUEST_URL + serviceRequest.getExternalId(), model, redirectAttributes);
    }

    private static final String _PRINT_ACADEMIC_REQUEST_URI = "/print/";
    public static final String PRINT_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _PRINT_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _PRINT_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public void print(@PathVariable(value = "oid") final ULisboaServiceRequest serviceRequest,
            @RequestParam(value = "template", required = true) final AcademicServiceRequestTemplate template,
            @RequestParam(value = "signature", required = true) final DocumentSigner signer,
            @RequestParam(value = "propertySlotEntry", required = false) final List<ServiceRequestSlotEntry> entries,
            @RequestParam(value = "propertyValue", required = false) final List<String> propertiesValues, final Model model,
            final RedirectAttributes redirectAttributes, final HttpServletResponse response, final HttpServletRequest request)
            throws IOException {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            if (serviceRequest.getAcademicServiceRequestSituationType() != AcademicServiceRequestSituationType.PROCESSING) {
                throw new ULisboaSpecificationsDomainException(
                        "error.serviceRequests.UlisboaServiceRequest.cannot.generate.document");
            }

            if (entries != null && propertiesValues == null || entries == null && propertiesValues != null
                    || entries != null && propertiesValues != null && entries.size() != propertiesValues.size()) {
                throw new ULisboaSpecificationsDomainException("error.serviceRequests.UlisboaServiceRequest.wrong.request");
            }
            // Process print configuration
            if (entries != null) {
                updatePrintConfiguration(serviceRequest, entries, propertiesValues);
            }

            serviceRequest.setPrintSettings(signer, template);
            serviceRequest.generateDocument();
            download(serviceRequest, model, redirectAttributes, response, request);
            return;
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        } catch (org.fenixedu.bennu.core.domain.exceptions.DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }
        request.getSession().setAttribute(ERROR_MESSAGE_ATTRIBUTE, model.asMap().get(ERROR_MESSAGES));
        response.sendRedirect(request.getContextPath() + READ_ACADEMIC_REQUEST_URL + serviceRequest.getExternalId());
    }

    @Atomic
    private void updatePrintConfiguration(final ULisboaServiceRequest serviceRequest, final List<ServiceRequestSlotEntry> entries,
            final List<String> propertiesValues) {
        Iterator<String> propertiesIterator = propertiesValues.iterator();
        for (ServiceRequestSlotEntry entry : entries) {
            String propertyValue = propertiesIterator.next();
            ServiceRequestPropertyBean bean = new ServiceRequestPropertyBean(entry);
            if (entry.getServiceRequestSlot().getUiComponentType() == UIComponentType.DATE) {
                String dateString = propertyValue;
                if (dateString.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$")) {
                    dateString += "T00:00:00.000Z";
                }
                bean.setDateTimeValue(DateTime.parse(dateString));
            } else if (entry.getServiceRequestSlot().getUiComponentType() == UIComponentType.NUMBER) {
                bean.setIntegerValue(Integer.parseInt(propertyValue));
            } else if (entry.getServiceRequestSlot().getUiComponentType() == UIComponentType.TEXT) {
                bean.setStringValue(propertyValue);
            } else if (entry.getServiceRequestSlot().getUiComponentType() == UIComponentType.TEXT_LOCALIZED_STRING) {
                JsonParser parser = new JsonParser();
                bean.setLocalizedStringValue(LocalizedString.fromJson(parser.parse(propertyValue)));
            } else if (entry.getServiceRequestSlot().getUiComponentType() == UIComponentType.DROP_DOWN_BOOLEAN) {
                bean.setBooleanValue(Boolean.valueOf(propertyValue));
            } else if (entry.getServiceRequestSlot().getUiComponentType() == UIComponentType.DROP_DOWN_ONE_VALUE) {
                if (entry.getServiceRequestSlot().getCode().equals(ULisboaConstants.LANGUAGE)) {
                    Locale localeValue = Locale.forLanguageTag(propertyValue.replace("_", "-"));
                    bean.setLocaleValue(localeValue);
                } else if (entry.getServiceRequestSlot().getCode().equals(ULisboaConstants.CYCLE_TYPE)) {
                    CycleType type = CycleType.valueOf(propertyValue);
                    bean.setCycleTypeValue(type);
                } else {
                    bean.setDomainObjectValue(FenixFramework.getDomainObject(propertyValue));
                }
            }
            if (serviceRequest.hasProperty(entry.getServiceRequestSlot().getCode())) {
                ServiceRequestProperty property = serviceRequest.findProperty(entry.getServiceRequestSlot().getCode());
                property.setValue(bean.getValue());
            } else {
                ServiceRequestProperty.create(serviceRequest, entry.getServiceRequestSlot(), bean.getValue());
            }
        }
    }

    private static final String _DOWNLOAD_PRINTED_ACADEMIC_REQUEST_URI = "/download/";
    public static final String DOWNLOAD_PRINTED_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _DOWNLOAD_PRINTED_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _DOWNLOAD_PRINTED_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.GET)
    public void download(@PathVariable(value = "oid") final ULisboaServiceRequest serviceRequest, final Model model,
            final RedirectAttributes redirectAttributes, final HttpServletResponse response, final HttpServletRequest request)
            throws IOException {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            ULisboaServiceRequestGeneratedDocument document = serviceRequest.downloadDocument();

            response.setContentType(document.getContentType());
            response.setHeader("Content-disposition", "attachment; filename=" + document.getFilename());
            response.getOutputStream().write(document.getContent());
            return;
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        } catch (org.fenixedu.bennu.core.domain.exceptions.DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }
        request.getSession().setAttribute(ERROR_MESSAGE_ATTRIBUTE, model.asMap().get(ERROR_MESSAGES));
        response.sendRedirect(request.getContextPath() + READ_ACADEMIC_REQUEST_URL + serviceRequest.getExternalId());
    }

    public void error(final ULisboaServiceRequest serviceRequest, final Model model, final HttpServletResponse response,
            final Throwable t) {
        try {
            response.setContentType("text/plain");
            response.setHeader("Content-disposition", "attachment; filename=Error.txt");
            response.getOutputStream().write(t.getLocalizedMessage().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String _REVERT_ACADEMIC_REQUEST_URI = "/revert/";
    public static final String REVERT_ACADEMIC_REQUEST_URL = CONTROLLER_URL + _REVERT_ACADEMIC_REQUEST_URI;

    @RequestMapping(value = _REVERT_ACADEMIC_REQUEST_URI + "{oid}", method = RequestMethod.POST)
    public String revert(@PathVariable(value = "oid") final ULisboaServiceRequest serviceRequest,
            @RequestParam(value = "notifyRevertAction", required = true) final boolean notifyRevertAction, final Model model,
            final RedirectAttributes redirectAttributes) {
        model.addAttribute("registration", serviceRequest.getRegistration());
        model.addAttribute("serviceRequest", serviceRequest);
        try {
            serviceRequest.revertState(notifyRevertAction);
            addInfoMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, "info.serviceRequests.ulisboarequest.reverted.success",
                    serviceRequest.getAcademicServiceRequestSituationType().getLocalizedName()), model);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        } catch (org.fenixedu.bennu.core.domain.exceptions.DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }
        return redirect(READ_ACADEMIC_REQUEST_URL + serviceRequest.getExternalId(), model, redirectAttributes);
    }

}
