package org.fenixedu.ulisboa.specifications.domain.serviceRequests;

import java.util.Collection;
import java.util.Locale;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentPurposeTypeInstance;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.exceptions.TreasuryDomainException;
import org.fenixedu.treasury.util.LocalizedStringUtil;
import org.fenixedu.ulisboa.specifications.util.Constants;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public class ServiceRequestSlot extends ServiceRequestSlot_Base {

    public ServiceRequestSlot() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected ServiceRequestSlot(final String code, final UIComponentType uiComponentType, final LocalizedString label) {
        this();
        setCode(code);
        setUiComponentType(uiComponentType);
        setLabel(label);
        checkRules();
    }

    private void checkRules() {
        if (StringUtils.isEmpty(getCode())) {
            throw new DomainException("error.ServiceRequestSlot.code.required");
        }
        if (findByCode(getCode()).count() > 1) {
            throw new DomainException("error.ServiceRequestSlot.code.duplicated");
        }
        if (getUiComponentType() == null) {
            throw new DomainException("error.ServiceRequestSlot.uiComponentType.required");
        }
        if (LocalizedStringUtil.isTrimmedEmpty(getLabel())) {
            throw new DomainException("error.ServiceRequestSlot.label.required");
        }
    }

    @Atomic
    public void edit(final String code, final UIComponentType uiComponentType, final LocalizedString label) {
        setCode(code);
        setUiComponentType(uiComponentType);
        setLabel(label);
        checkRules();
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);
        if (!getServiceRequestPropertiesSet().isEmpty()) {
            blockers.add(BundleUtil.getString(Constants.BUNDLE, "error.ServiceRequestSlot.connected.ServiceRequestProperties"));
        }
        if (!getServiceRequestTypesSet().isEmpty()) {
            blockers.add(BundleUtil.getString(Constants.BUNDLE, "error.ServiceRequestSlot.connected.ServiceRequestTypes"));
        }
    }

    @Atomic
    public void delete() {
        TreasuryDomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        setBennu(null);
        deleteDomainObject();
    }

    public static Stream<ServiceRequestSlot> findAll() {
        return Bennu.getInstance().getServiceRequestSlotsSet().stream();
    }

    public static Stream<ServiceRequestSlot> findByCode(final String code) {
        return findAll().filter(fi -> fi.getCode().equalsIgnoreCase(code));
    }

    public static ServiceRequestSlot getByCode(final String code) {
        return findByCode(code).findFirst().get();
    }

    @Atomic
    public static ServiceRequestSlot create(final String code, final UIComponentType uiComponentType, final LocalizedString label) {
        return new ServiceRequestSlot(code, uiComponentType, label);
    }

    public static void initBaseSlots() {
        if (findAll().count() != 0) {
            return;
        }

        create(Constants.REGISTRATION, UIComponentType.DROP_DOWN_ONE_VALUE,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.registration"));
        create(Constants.LANGUAGE, UIComponentType.DROP_DOWN_ONE_VALUE,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.language"));
        create(Constants.DOCUMENT_PURPOSE_TYPE, UIComponentType.DROP_DOWN_ONE_VALUE,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.documentPurposeType"));
        create(Constants.OTHER_DOCUMENT_PURPOSE, UIComponentType.TEXT,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.otherDocumentPurposeType"));
        create(Constants.IS_DETAILED, UIComponentType.DROP_DOWN_BOOLEAN,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.isDetailed"));
        create(Constants.IS_URGENT, UIComponentType.DROP_DOWN_BOOLEAN,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.isUrgent"));
        create(Constants.CYCLE_TYPE, UIComponentType.DROP_DOWN_ONE_VALUE,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.cycleType"));
        create(Constants.NUMBER_OF_UNITS, UIComponentType.NUMBER,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.numberOfUnits"));
        create(Constants.NUMBER_OF_DAYS, UIComponentType.NUMBER,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.numberOfDays"));
        create(Constants.NUMBER_OF_PAGES, UIComponentType.NUMBER,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.numberOfPages"));
        create(Constants.CURRICULAR_PLAN, UIComponentType.DROP_DOWN_ONE_VALUE,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.curricularPlan"));
        create(Constants.APPROVED_COURSES, UIComponentType.DROP_DOWN_MULTIPLE,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.approvedCourses"));
        create(Constants.ENROLLED_COURSES, UIComponentType.DROP_DOWN_MULTIPLE,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.enrolledCourses"));
        create(Constants.CREDITS, UIComponentType.NUMBER,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.credits"));
        create(Constants.EXECUTION_YEAR, UIComponentType.DROP_DOWN_ONE_VALUE,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.executionYear"));
    }

    @Atomic
    public static void createProperty(String serviceRequestSlotCode, String propertyValue) {
        ServiceRequestSlot serviceRequestSlot = ServiceRequestSlot.getByCode(serviceRequestSlotCode);
        switch (serviceRequestSlotCode) {
        case Constants.REGISTRATION:
            Registration registration = (Registration) FenixFramework.getDomainObject(propertyValue);
            ServiceRequestProperty.createRegistrationProperty(registration, serviceRequestSlot);
            break;
        case Constants.LANGUAGE:
            Locale language = Locale.forLanguageTag(propertyValue);
            ServiceRequestProperty.createLocaleProperty(language, serviceRequestSlot);
            break;
        case Constants.DOCUMENT_PURPOSE_TYPE:
            DocumentPurposeTypeInstance documentPurposeTypeInstance =
                    (DocumentPurposeTypeInstance) FenixFramework.getDomainObject(propertyValue);
            ServiceRequestProperty.createDocumentPurposeTypeInstanceProperty(documentPurposeTypeInstance, serviceRequestSlot);
            break;
        case Constants.OTHER_DOCUMENT_PURPOSE:
            ServiceRequestProperty.createStringProperty(propertyValue, serviceRequestSlot);
            break;
        case Constants.IS_DETAILED:
            Boolean detailed = Boolean.valueOf(propertyValue);
            ServiceRequestProperty.createBooleanProperty(detailed, serviceRequestSlot);
            break;
        case Constants.IS_URGENT:
            Boolean urgent = Boolean.valueOf(propertyValue);
            ServiceRequestProperty.createBooleanProperty(urgent, serviceRequestSlot);
            break;
        case Constants.CYCLE_TYPE:
            CycleType cycleType = CycleType.valueOf(propertyValue);
            ServiceRequestProperty.createCycleTypeProperty(cycleType, serviceRequestSlot);
            break;
        case Constants.NUMBER_OF_UNITS:
            Integer numberOfUnits = Integer.valueOf(propertyValue);
            ServiceRequestProperty.createIntegerProperty(numberOfUnits, serviceRequestSlot);
            break;
        case Constants.NUMBER_OF_DAYS:
            Integer numberOfDays = Integer.valueOf(propertyValue);
            ServiceRequestProperty.createIntegerProperty(numberOfDays, serviceRequestSlot);
            break;
        case Constants.NUMBER_OF_PAGES:
            Integer numberOfPages = Integer.valueOf(propertyValue);
            ServiceRequestProperty.createIntegerProperty(numberOfPages, serviceRequestSlot);
            break;
        case Constants.EXECUTION_YEAR:
            ExecutionYear executionYear = (ExecutionYear) FenixFramework.getDomainObject(propertyValue);
            ServiceRequestProperty.createExecutionYearProperty(executionYear, serviceRequestSlot);
            break;
        case Constants.CURRICULAR_PLAN:

        case Constants.APPROVED_COURSES:
        case Constants.ENROLLED_COURSES:
        case Constants.CREDITS:
        default:
            throw new DomainException("error.ServiceRequestSlot.not.supported.type");
        }
    }
}
