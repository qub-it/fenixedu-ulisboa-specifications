package org.fenixedu.ulisboa.specifications.domain.serviceRequests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentPurposeTypeInstance;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.exceptions.TreasuryDomainException;
import org.fenixedu.treasury.util.LocalizedStringUtil;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ServiceRequestSlot extends ServiceRequestSlot_Base {

    protected ServiceRequestSlot() {
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
            blockers.add(BundleUtil.getString(ULisboaConstants.BUNDLE, "error.ServiceRequestSlot.connected.ServiceRequestProperties"));
        }
        if (!getServiceRequestTypesSet().isEmpty()) {
            blockers.add(BundleUtil.getString(ULisboaConstants.BUNDLE, "error.ServiceRequestSlot.connected.ServiceRequestTypes"));
        }
        if (!getChangeable()) {
            blockers.add(BundleUtil.getString(ULisboaConstants.BUNDLE, "error.ServiceRequestSlot.unchangeableSlot"));
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
//        if (findAll().count() != 0) {
//            return;
//        }

        if (findByCode(ULisboaConstants.LANGUAGE).count() == 0) {
            createStaticSlot(ULisboaConstants.LANGUAGE, UIComponentType.DROP_DOWN_ONE_VALUE,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.language"));
        }
        if (findByCode(ULisboaConstants.DOCUMENT_PURPOSE_TYPE).count() == 0) {
            createStaticSlot(ULisboaConstants.DOCUMENT_PURPOSE_TYPE, UIComponentType.DROP_DOWN_ONE_VALUE,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.documentPurposeType"));
        }
        if (findByCode(ULisboaConstants.OTHER_DOCUMENT_PURPOSE).count() == 0) {
            createStaticSlot(ULisboaConstants.OTHER_DOCUMENT_PURPOSE, UIComponentType.TEXT,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.otherDocumentPurposeType"));
        }
        if (findByCode(ULisboaConstants.IS_DETAILED).count() == 0) {
            createStaticSlot(ULisboaConstants.IS_DETAILED, UIComponentType.DROP_DOWN_BOOLEAN,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.isDetailed"));
        }
        if (findByCode(ULisboaConstants.IS_URGENT).count() == 0) {
            createStaticSlot(ULisboaConstants.IS_URGENT, UIComponentType.DROP_DOWN_BOOLEAN,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.isUrgent"));
        }
        if (findByCode(ULisboaConstants.CYCLE_TYPE).count() == 0) {
            createStaticSlot(ULisboaConstants.CYCLE_TYPE, UIComponentType.DROP_DOWN_ONE_VALUE,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.cycleType"));
        }
        if (findByCode(ULisboaConstants.NUMBER_OF_UNITS).count() == 0) {
            createStaticSlot(ULisboaConstants.NUMBER_OF_UNITS, UIComponentType.NUMBER,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.numberOfUnits"));
        }
        if (findByCode(ULisboaConstants.NUMBER_OF_DAYS).count() == 0) {
            createStaticSlot(ULisboaConstants.NUMBER_OF_DAYS, UIComponentType.NUMBER,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.numberOfDays"));
        }
        if (findByCode(ULisboaConstants.NUMBER_OF_PAGES).count() == 0) {
            createStaticSlot(ULisboaConstants.NUMBER_OF_PAGES, UIComponentType.NUMBER,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.numberOfPages"));
        }
        if (findByCode(ULisboaConstants.EXECUTION_YEAR).count() == 0) {
            createStaticSlot(ULisboaConstants.EXECUTION_YEAR, UIComponentType.DROP_DOWN_ONE_VALUE,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.executionYear"));
        }
        if (findByCode(ULisboaConstants.CURRICULAR_PLAN).count() == 0) {
            createStaticSlot(ULisboaConstants.CURRICULAR_PLAN, UIComponentType.DROP_DOWN_ONE_VALUE,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.curricularPlan"));
        }
        if (findByCode(ULisboaConstants.APPROVED_EXTRA_CURRICULUM).count() == 0) {
            createStaticSlot(ULisboaConstants.APPROVED_EXTRA_CURRICULUM, UIComponentType.DROP_DOWN_MULTIPLE,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.approvedExtraCurriculum"));
        }
        if (findByCode(ULisboaConstants.APPROVED_STANDALONE_CURRICULUM).count() == 0) {
            createStaticSlot(ULisboaConstants.APPROVED_STANDALONE_CURRICULUM, UIComponentType.DROP_DOWN_MULTIPLE,
                    BundleUtil
                            .getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.approvedStandaloneCurriculum"));
        }
        if (findByCode(ULisboaConstants.APPROVED_ENROLMENTS).count() == 0) {
            createStaticSlot(ULisboaConstants.APPROVED_ENROLMENTS, UIComponentType.DROP_DOWN_MULTIPLE,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.approvedEnrolments"));
        }
        if (findByCode(ULisboaConstants.CURRICULUM).count() == 0) {
            createStaticSlot(ULisboaConstants.CURRICULUM, UIComponentType.DROP_DOWN_MULTIPLE,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.curriculum"));
        }
        if (findByCode(ULisboaConstants.ENROLMENTS_BY_YEAR).count() == 0) {
            createStaticSlot(ULisboaConstants.ENROLMENTS_BY_YEAR, UIComponentType.DROP_DOWN_MULTIPLE,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.enrolmentsByYear"));
        }
    }

    public static <T> T convertValue(String serviceRequestSlotCode, String propertyValue) {
        ServiceRequestSlot serviceRequestSlot = ServiceRequestSlot.getByCode(serviceRequestSlotCode);

        if (propertyValue == null) {
            return (serviceRequestSlot.getUiComponentType().isMultipleDropDown()) ? (T) new ArrayList<T>() : (T) null;
        }

        switch (serviceRequestSlot.getUiComponentType()) {
        case DROP_DOWN_BOOLEAN:
            return (T) Boolean.valueOf(propertyValue);
        case NUMBER:
            return (T) Integer.valueOf(propertyValue);
        case TEXT:
            return (T) propertyValue;
        case TEXT_LOCALIZED_STRING:
            JsonObject jsonObject = new JsonParser().parse(propertyValue).getAsJsonObject();
            return (T) LocalizedString.fromJson(jsonObject);
        case DATE:
            return (T) new DateTime(propertyValue);
        case DROP_DOWN_MULTIPLE:
            if (ULisboaConstants.DROP_DOWN_MULTIPLE_DOMAIN_OBJECTS.contains(serviceRequestSlotCode)) {
                final Collection<DomainObject> convertedList = Sets.newHashSet();
                JsonArray domainObjectJsonArray = new JsonParser().parse(propertyValue).getAsJsonArray();
                for (final JsonElement element : domainObjectJsonArray) {
                    String oid = element.getAsJsonObject().get("id").getAsString().trim();
                    convertedList.add(FenixFramework.getDomainObject(oid));
                }
                return (T) convertedList;
            } else {
                throw new DomainException("error.ServiceRequestSlot.not.supported.type");
            }
        case DROP_DOWN_ONE_VALUE:
        default:
            if (ULisboaConstants.DROP_DOWN_SINGLE_DOMAIN_OBJECTS.contains(serviceRequestSlotCode)) {
                return FenixFramework.getDomainObject(propertyValue);
            }
            if (serviceRequestSlotCode.equals(ULisboaConstants.LANGUAGE)) {
                return (T) Locale.forLanguageTag(propertyValue);
            }
            if (serviceRequestSlotCode.equals(ULisboaConstants.CYCLE_TYPE)) {
                return (T) CycleType.valueOf(propertyValue);
            }
            throw new DomainException("error.ServiceRequestSlot.not.supported.type");
        }

    }

    @Atomic
    public static ServiceRequestProperty createProperty(String serviceRequestSlotCode, String propertyValue) {
        ServiceRequestSlot serviceRequestSlot = ServiceRequestSlot.getByCode(serviceRequestSlotCode);
        if (propertyValue == null) {
            return ServiceRequestProperty.create(serviceRequestSlot);
        }

        final Object value = convertValue(serviceRequestSlotCode, propertyValue);

        switch (serviceRequestSlot.getUiComponentType()) {
        case DROP_DOWN_BOOLEAN:
            return ServiceRequestProperty.createForBoolean((Boolean) value, serviceRequestSlot);
        case NUMBER:
            return ServiceRequestProperty.createForInteger((Integer) value, serviceRequestSlot);
        case TEXT:
            return ServiceRequestProperty.createForString(propertyValue, serviceRequestSlot);
        case TEXT_LOCALIZED_STRING:
            return ServiceRequestProperty.createForLocalizedString((LocalizedString) value, serviceRequestSlot);
        case DATE:
            return ServiceRequestProperty.createForDateTime((DateTime) value, serviceRequestSlot);
        case DROP_DOWN_MULTIPLE:
            if (ULisboaConstants.ICURRICULUM_ENTRY_OBJECTS.contains(serviceRequestSlotCode)) {
                return ServiceRequestProperty.createForICurriculumEntry((Collection<ICurriculumEntry>) value, serviceRequestSlot);
            } else {
                throw new DomainException("error.ServiceRequestSlot.not.supported.type");
            }

        case DROP_DOWN_ONE_VALUE:
        default:
            switch (serviceRequestSlotCode) {
            case ULisboaConstants.LANGUAGE:
                return ServiceRequestProperty.createForLocale((Locale) value, serviceRequestSlot);
            case ULisboaConstants.DOCUMENT_PURPOSE_TYPE:
                return ServiceRequestProperty.createForDocumentPurposeTypeInstance((DocumentPurposeTypeInstance) value,
                        serviceRequestSlot);
            case ULisboaConstants.CYCLE_TYPE:
                return ServiceRequestProperty.createForCycleType((CycleType) value, serviceRequestSlot);
            case ULisboaConstants.EXECUTION_YEAR:
                return ServiceRequestProperty.createForExecutionYear((ExecutionYear) value, serviceRequestSlot);
            case ULisboaConstants.CURRICULAR_PLAN:
                return ServiceRequestProperty.createForCurricularPlan((StudentCurricularPlan) value, serviceRequestSlot);
            default:
                throw new DomainException("error.ServiceRequestSlot.not.supported.type");
            }
        }
    }
}
