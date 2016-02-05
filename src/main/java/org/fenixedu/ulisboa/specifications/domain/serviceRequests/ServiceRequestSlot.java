package org.fenixedu.ulisboa.specifications.domain.serviceRequests;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.util.LocalizedStringUtil;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import pt.ist.fenixframework.Atomic;

public class ServiceRequestSlot extends ServiceRequestSlot_Base {

    public static final Comparator<ServiceRequestSlot> COMPARE_BY_LABEL = new Comparator<ServiceRequestSlot>() {
        @Override
        public int compare(ServiceRequestSlot o1, ServiceRequestSlot o2) {
            return o1.getLabel().getContent().compareTo(o2.getLabel().getContent());
        }
    };

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
            throw new ULisboaSpecificationsDomainException("error.ServiceRequestSlot.code.required");
        }
        if (findByCode(getCode()).count() > 1) {
            throw new ULisboaSpecificationsDomainException("error.ServiceRequestSlot.code.duplicated");
        }
        if (getUiComponentType() == null) {
            throw new ULisboaSpecificationsDomainException("error.ServiceRequestSlot.uiComponentType.required");
        }
        if (LocalizedStringUtil.isTrimmedEmpty(getLabel())) {
            throw new ULisboaSpecificationsDomainException("error.ServiceRequestSlot.label.required");
        }
    }

    @Atomic
    public void edit(final String code, final UIComponentType uiComponentType, final LocalizedString label) {
        if (!getChangeable()) {
            throw new ULisboaSpecificationsDomainException("error.ServiceRequestSlot.unchangeableSlot");
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
            blockers.add(
                    BundleUtil.getString(ULisboaConstants.BUNDLE, "error.ServiceRequestSlot.connected.ServiceRequestProperties"));
        }
        if (!getServiceRequestSlotEntriesSet().isEmpty()) {
            blockers.add(BundleUtil.getString(ULisboaConstants.BUNDLE,
                    "error.ServiceRequestSlot.connected.ServiceRequestSlotEntries"));
        }
        if (!getChangeable()) {
            blockers.add(BundleUtil.getString(ULisboaConstants.BUNDLE, "error.ServiceRequestSlot.unchangeableSlot"));
        }
    }

    @Atomic
    public void delete() {
        ULisboaSpecificationsDomainException.throwWhenDeleteBlocked(getDeletionBlockers());

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
            createStaticSlot(ULisboaConstants.OTHER_DOCUMENT_PURPOSE, UIComponentType.TEXT, BundleUtil
                    .getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.otherDocumentPurposeType"));
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
        if (findByCode(ULisboaConstants.PROGRAM_CONCLUSION).count() == 0) {
            createStaticSlot(ULisboaConstants.PROGRAM_CONCLUSION, UIComponentType.DROP_DOWN_ONE_VALUE,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.programConclusion"));
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
            createStaticSlot(ULisboaConstants.APPROVED_EXTRA_CURRICULUM, UIComponentType.DROP_DOWN_MULTIPLE, BundleUtil
                    .getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.approvedExtraCurriculum"));
        }
        if (findByCode(ULisboaConstants.APPROVED_STANDALONE_CURRICULUM).count() == 0) {
            createStaticSlot(ULisboaConstants.APPROVED_STANDALONE_CURRICULUM, UIComponentType.DROP_DOWN_MULTIPLE, BundleUtil
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
        if (findByCode(ULisboaConstants.STANDALONE_ENROLMENTS_BY_YEAR).count() == 0) {
            createStaticSlot(ULisboaConstants.STANDALONE_ENROLMENTS_BY_YEAR, UIComponentType.DROP_DOWN_MULTIPLE, BundleUtil
                    .getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.standaloneEnrolmentsByYear"));
        }
        if (findByCode(ULisboaConstants.EXTRACURRICULAR_ENROLMENTS_BY_YEAR).count() == 0) {
            createStaticSlot(ULisboaConstants.EXTRACURRICULAR_ENROLMENTS_BY_YEAR, UIComponentType.DROP_DOWN_MULTIPLE,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                            "label.ServiceRequestSlot.label.extracurricularEnrolmentsByYear"));
        }
        if (findByCode(ULisboaConstants.EXECUTION_SEMESTER).count() == 0) {
            createStaticSlot(ULisboaConstants.EXECUTION_SEMESTER, UIComponentType.DROP_DOWN_ONE_VALUE,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.executionSemester"));
        }
        if (findByCode(ULisboaConstants.EVALUATION_SEASON).count() == 0) {
            createStaticSlot(ULisboaConstants.EVALUATION_SEASON, UIComponentType.DROP_DOWN_ONE_VALUE,
                    BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, "label.ServiceRequestSlot.label.evaluationSeason"));
        }

    }
}
