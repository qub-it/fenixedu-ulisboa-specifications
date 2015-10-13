package org.fenixedu.ulisboa.specifications.domain.serviceRequests;

import java.util.Collection;
import java.util.Locale;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentPurposeTypeInstance;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.exceptions.TreasuryDomainException;
import org.fenixedu.treasury.util.LocalizedStringUtil;
import org.fenixedu.ulisboa.specifications.util.Constants;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ServiceRequestSlot extends ServiceRequestSlot_Base {

    public ServiceRequestSlot() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected ServiceRequestSlot(final String code, final UIComponentType uiComponentType, final LocalizedString label,
            final boolean changeable) {
        this();
        setCode(code);
        setUiComponentType(uiComponentType);
        setLabel(label);
        setChangeable(changeable);
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
        if (!getChangeable()) {
            throw new DomainException("error.ServiceRequestSlot.unchangeableSlot");
        }

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
        if (!getChangeable()) {
            blockers.add(BundleUtil.getString(Constants.BUNDLE, "error.ServiceRequestSlot.unchangeableSlot"));
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
    public static ServiceRequestSlot createDynamicSlot(final String code, final UIComponentType uiComponentType,
            final LocalizedString label) {
        return new ServiceRequestSlot(code, uiComponentType, label, true);
    }

    @Atomic
    public static ServiceRequestSlot createStaticSlot(final String code, final UIComponentType uiComponentType,
            final LocalizedString label) {
        return new ServiceRequestSlot(code, uiComponentType, label, false);
    }

    public static void initStaticSlots() {
        if (findAll().count() != 0) {
            return;
        }

        createStaticSlot(Constants.LANGUAGE, UIComponentType.DROP_DOWN_ONE_VALUE,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.language"));
        createStaticSlot(Constants.DOCUMENT_PURPOSE_TYPE, UIComponentType.DROP_DOWN_ONE_VALUE,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.documentPurposeType"));
        createStaticSlot(Constants.OTHER_DOCUMENT_PURPOSE, UIComponentType.TEXT,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.otherDocumentPurposeType"));
        createStaticSlot(Constants.IS_DETAILED, UIComponentType.DROP_DOWN_BOOLEAN,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.isDetailed"));
        createStaticSlot(Constants.IS_URGENT, UIComponentType.DROP_DOWN_BOOLEAN,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.isUrgent"));
        createStaticSlot(Constants.CYCLE_TYPE, UIComponentType.DROP_DOWN_ONE_VALUE,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.cycleType"));
        createStaticSlot(Constants.NUMBER_OF_UNITS, UIComponentType.NUMBER,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.numberOfUnits"));
        createStaticSlot(Constants.NUMBER_OF_DAYS, UIComponentType.NUMBER,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.numberOfDays"));
        createStaticSlot(Constants.NUMBER_OF_PAGES, UIComponentType.NUMBER,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.numberOfPages"));
        createStaticSlot(Constants.CURRICULAR_PLAN, UIComponentType.DROP_DOWN_ONE_VALUE,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.curricularPlan"));
        createStaticSlot(Constants.APPROVED_COURSES, UIComponentType.DROP_DOWN_MULTIPLE,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.approvedCourses"));
        createStaticSlot(Constants.ENROLLED_COURSES, UIComponentType.DROP_DOWN_MULTIPLE,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.enrolledCourses"));
        createStaticSlot(Constants.CREDITS, UIComponentType.NUMBER,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.credits"));
        createStaticSlot(Constants.EXECUTION_YEAR, UIComponentType.DROP_DOWN_ONE_VALUE,
                BundleUtil.getLocalizedString(Constants.BUNDLE, "label.ServiceRequestSlot.label.executionYear"));
    }

    @Atomic
    public static ServiceRequestProperty createProperty(String serviceRequestSlotCode, String propertyValue) {
        ServiceRequestSlot serviceRequestSlot = ServiceRequestSlot.getByCode(serviceRequestSlotCode);
        switch (serviceRequestSlot.getUiComponentType()) {
        case DROP_DOWN_BOOLEAN:
            return ServiceRequestProperty.createBooleanProperty(Boolean.valueOf(propertyValue), serviceRequestSlot);
        case NUMBER:
            return ServiceRequestProperty.createIntegerProperty(Integer.valueOf(propertyValue), serviceRequestSlot);
        case TEXT:
            return ServiceRequestProperty.createStringProperty(propertyValue, serviceRequestSlot);
        case TEXT_LOCALIZED_STRING:
            JsonObject jsonObject = new JsonParser().parse(propertyValue).getAsJsonObject();
            return ServiceRequestProperty.createLocalizedStringProperty(LocalizedString.fromJson(jsonObject), serviceRequestSlot);
        case DATE:
            return ServiceRequestProperty.createDateTimeProperty(new DateTime(propertyValue), serviceRequestSlot);
        case DROP_DOWN_MULTIPLE:
        case DROP_DOWN_ONE_VALUE:
        default:
            switch (serviceRequestSlotCode) {
            case Constants.LANGUAGE:
                Locale language = new Locale(propertyValue);
                return ServiceRequestProperty.createLocaleProperty(language, serviceRequestSlot);
            case Constants.DOCUMENT_PURPOSE_TYPE:
                DocumentPurposeTypeInstance documentPurposeTypeInstance =
                        (DocumentPurposeTypeInstance) FenixFramework.getDomainObject(propertyValue);
                return ServiceRequestProperty.createDocumentPurposeTypeInstanceProperty(documentPurposeTypeInstance,
                        serviceRequestSlot);
            case Constants.CYCLE_TYPE:
                CycleType cycleType = CycleType.valueOf(propertyValue);
                return ServiceRequestProperty.createCycleTypeProperty(cycleType, serviceRequestSlot);
            case Constants.EXECUTION_YEAR:
                ExecutionYear executionYear = (ExecutionYear) FenixFramework.getDomainObject(propertyValue);
                return ServiceRequestProperty.createExecutionYearProperty(executionYear, serviceRequestSlot);
            case Constants.CURRICULAR_PLAN:
            case Constants.APPROVED_COURSES:
            case Constants.ENROLLED_COURSES:
            case Constants.CREDITS:
            default:
                throw new DomainException("error.ServiceRequestSlot.not.supported.type");
            }
        }
    }
}
