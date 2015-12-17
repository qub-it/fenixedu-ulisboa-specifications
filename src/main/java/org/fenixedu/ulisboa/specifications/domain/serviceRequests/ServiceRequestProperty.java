package org.fenixedu.ulisboa.specifications.domain.serviceRequests;

import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentPurposeTypeInstance;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.ExternalEnrolment;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.joda.time.DateTime;

import com.google.common.base.Strings;

import pt.ist.fenixframework.Atomic;

public class ServiceRequestProperty extends ServiceRequestProperty_Base {

    public static final Comparator<ServiceRequestProperty> COMPARATE_BY_ENTRY_NUMBER = new Comparator<ServiceRequestProperty>() {

        @Override
        public int compare(ServiceRequestProperty o1, ServiceRequestProperty o2) {
            ServiceRequestSlotEntry entry1 = ServiceRequestSlotEntry.findByServiceRequestProperty(o1);
            ServiceRequestSlotEntry entry2 = ServiceRequestSlotEntry.findByServiceRequestProperty(o2);
            if (entry1 == null) {
                return 1;
            }
            if (entry2 == null) {
                return -1;
            }
            return Integer.compare(entry1.getOrderNumber(), entry2.getOrderNumber());
        }
    };

    protected ServiceRequestProperty() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected ServiceRequestProperty(ServiceRequestSlot serviceRequestSlot) {
        this();
        setServiceRequestSlot(serviceRequestSlot);
        checkRules();
    }

    private void checkRules() {

        if (getServiceRequestSlot() == null) {
            throw new ULisboaSpecificationsDomainException("error.ServiceRequestProperty.serviceRequestSlot.required");
        }
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);
    }

    @Atomic
    public void delete() {
        ULisboaSpecificationsDomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        setULisboaServiceRequest(null);
        setDocumentPurposeTypeInstance(null);
        setExecutionYear(null);
        setServiceRequestSlot(null);
        super.getCurriculumLinesSet().clear();
        super.getExternalEnrolmentsSet().clear();

        setBennu(null);
        deleteDomainObject();
    }

    public Set<ICurriculumEntry> getICurriculumEntriesSet() {
        Stream<ICurriculumEntry> curriculumEntries = super.getCurriculumLinesSet().stream().map(ICurriculumEntry.class::cast);
        Stream<ICurriculumEntry> externalEntries = super.getExternalEnrolmentsSet().stream().map(ICurriculumEntry.class::cast);
        return Stream.concat(curriculumEntries, externalEntries).collect(Collectors.toSet());
    }

    public static Stream<ServiceRequestProperty> findAll() {
        return Bennu.getInstance().getServiceRequestPropertiesSet().stream();
    }

    public static Stream<ServiceRequestProperty> findByCode(String code) {
        return ServiceRequestProperty.findAll().filter(prop -> prop.getServiceRequestSlot().getCode().equals(code));
    }

    @Atomic
    public static ServiceRequestProperty create(ServiceRequestSlot serviceRequestSlot) {
        ServiceRequestProperty serviceRequestProperty = new ServiceRequestProperty(serviceRequestSlot);
        return serviceRequestProperty;
    }

    @Atomic
    public static ServiceRequestProperty createForLocale(Locale value, ServiceRequestSlot serviceRequestSlot) {
        ServiceRequestProperty serviceRequestProperty = new ServiceRequestProperty(serviceRequestSlot);
        serviceRequestProperty.setLocale(value);
        return serviceRequestProperty;
    }

    @Atomic
    public static ServiceRequestProperty createForBoolean(Boolean value, ServiceRequestSlot serviceRequestSlot) {
        ServiceRequestProperty serviceRequestProperty = new ServiceRequestProperty(serviceRequestSlot);
        serviceRequestProperty.setBooleanValue(value);
        return serviceRequestProperty;
    }

    @Atomic
    public static ServiceRequestProperty createForCycleType(CycleType value, ServiceRequestSlot serviceRequestSlot) {
        ServiceRequestProperty serviceRequestProperty = new ServiceRequestProperty(serviceRequestSlot);
        serviceRequestProperty.setCycleType(value);
        return serviceRequestProperty;
    }

    @Atomic
    public static ServiceRequestProperty createForInteger(Integer value, ServiceRequestSlot serviceRequestSlot) {
        ServiceRequestProperty serviceRequestProperty = new ServiceRequestProperty(serviceRequestSlot);
        serviceRequestProperty.setInteger(value);
        return serviceRequestProperty;
    }

    @Atomic
    public static ServiceRequestProperty createForString(String value, ServiceRequestSlot serviceRequestSlot) {
        ServiceRequestProperty serviceRequestProperty = new ServiceRequestProperty(serviceRequestSlot);
        serviceRequestProperty.setString(value);
        return serviceRequestProperty;
    }

    @Atomic
    public static ServiceRequestProperty createForLocalizedString(LocalizedString value, ServiceRequestSlot serviceRequestSlot) {
        ServiceRequestProperty serviceRequestProperty = new ServiceRequestProperty(serviceRequestSlot);
        serviceRequestProperty.setLocalizedString(value);
        return serviceRequestProperty;
    }

    @Atomic
    public static ServiceRequestProperty createForDateTime(DateTime value, ServiceRequestSlot serviceRequestSlot) {
        ServiceRequestProperty serviceRequestProperty = new ServiceRequestProperty(serviceRequestSlot);
        serviceRequestProperty.setDateTime(value);
        return serviceRequestProperty;
    }

    @Atomic
    public static ServiceRequestProperty createForExecutionYear(ExecutionYear value, ServiceRequestSlot serviceRequestSlot) {
        ServiceRequestProperty serviceRequestProperty = new ServiceRequestProperty(serviceRequestSlot);
        serviceRequestProperty.setExecutionYear(value);
        return serviceRequestProperty;
    }

    @Atomic
    public static ServiceRequestProperty createForDocumentPurposeTypeInstance(DocumentPurposeTypeInstance value,
            ServiceRequestSlot serviceRequestSlot) {
        ServiceRequestProperty serviceRequestProperty = new ServiceRequestProperty(serviceRequestSlot);
        serviceRequestProperty.setDocumentPurposeTypeInstance(value);
        return serviceRequestProperty;
    }

    @Atomic
    public static ServiceRequestProperty createForProgramConclusion(ProgramConclusion value,
            ServiceRequestSlot serviceRequestSlot) {
        ServiceRequestProperty serviceRequestProperty = new ServiceRequestProperty(serviceRequestSlot);
        serviceRequestProperty.setProgramConclusion(value);
        return serviceRequestProperty;
    }

    @Atomic
    public static ServiceRequestProperty createForCurricularPlan(StudentCurricularPlan value,
            ServiceRequestSlot serviceRequestSlot) {
        ServiceRequestProperty serviceRequestProperty = new ServiceRequestProperty(serviceRequestSlot);
        serviceRequestProperty.setStudentCurricularPlan(value);
        return serviceRequestProperty;
    }

    @Atomic
    public static ServiceRequestProperty createForICurriculumEntry(Collection<ICurriculumEntry> collection,
            ServiceRequestSlot serviceRequestSlot) {
        ServiceRequestProperty serviceRequestProperty = new ServiceRequestProperty(serviceRequestSlot);
        for (ICurriculumEntry entry : collection) {
            if (entry instanceof CurriculumLine) {
                serviceRequestProperty.addCurriculumLines((CurriculumLine) entry);
            } else if (entry instanceof ExternalEnrolment) {
                serviceRequestProperty.addExternalEnrolments((ExternalEnrolment) entry);
            } else {
                throw new ULisboaSpecificationsDomainException("error.ServiceRequestProperty.curriculumEntry.not.supported");
            }
        }
        return serviceRequestProperty;
    }

    public String getValueAsString() {
        switch (getServiceRequestSlot().getUiComponentType()) {
        case DROP_DOWN_BOOLEAN:
            return getBooleanValue().toString();
        case NUMBER:
            return getInteger().toString();
        case TEXT:
            return getString();
        case TEXT_LOCALIZED_STRING:
            return getLocalizedString().toString();
        case DATE:
            return getDateTime().toString();
        case DROP_DOWN_MULTIPLE:
            if (ULisboaConstants.ICURRICULUM_ENTRY_OBJECTS.contains(getServiceRequestSlot().getCode())) {
                return getICurriculumEntriesSet().toString();
            } else {
                throw new ULisboaSpecificationsDomainException("error.ServiceRequestSlot.not.supported.type");
            }

        case DROP_DOWN_ONE_VALUE:
        default:
            switch (getServiceRequestSlot().getCode()) {
            case ULisboaConstants.LANGUAGE:
                return getLocale().toString();
            case ULisboaConstants.DOCUMENT_PURPOSE_TYPE:
                return getDocumentPurposeTypeInstance().getExternalId();
            case ULisboaConstants.CYCLE_TYPE:
                return getCycleType().toString();
            case ULisboaConstants.PROGRAM_CONCLUSION:
                return getProgramConclusion().getExternalId();
            case ULisboaConstants.EXECUTION_YEAR:
                return getExecutionYear().getExternalId();
            case ULisboaConstants.CURRICULAR_PLAN:
                return getStudentCurricularPlan().getExternalId();
            default:
                throw new ULisboaSpecificationsDomainException("error.ServiceRequestSlot.not.supported.type");
            }
        }
    }

    public Object getValue() {
        switch (getServiceRequestSlot().getUiComponentType()) {
        case DROP_DOWN_BOOLEAN:
            return getBooleanValue();
        case NUMBER:
            return getInteger();
        case TEXT:
            return getString();
        case TEXT_LOCALIZED_STRING:
            return getLocalizedString();
        case DATE:
            return getDateTime();
        case DROP_DOWN_MULTIPLE:
            if (ULisboaConstants.ICURRICULUM_ENTRY_OBJECTS.contains(getServiceRequestSlot().getCode())) {
                return getICurriculumEntriesSet();
            } else {
                throw new ULisboaSpecificationsDomainException("error.ServiceRequestSlot.not.supported.type");
            }

        case DROP_DOWN_ONE_VALUE:
        default:
            switch (getServiceRequestSlot().getCode()) {
            case ULisboaConstants.LANGUAGE:
                return getLocale();
            case ULisboaConstants.DOCUMENT_PURPOSE_TYPE:
                return getDocumentPurposeTypeInstance();
            case ULisboaConstants.CYCLE_TYPE:
                return getCycleType();
            case ULisboaConstants.PROGRAM_CONCLUSION:
                return getProgramConclusion();
            case ULisboaConstants.EXECUTION_YEAR:
                return getExecutionYear();
            case ULisboaConstants.CURRICULAR_PLAN:
                return getStudentCurricularPlan();
            default:
                throw new ULisboaSpecificationsDomainException("error.ServiceRequestSlot.not.supported.type");
            }
        }
    }

    public boolean isNullOrEmpty() {
        if (getValue() == null) {
            return true;
        }
        Object value = getValue();
        if (getServiceRequestSlot().getUiComponentType() == UIComponentType.TEXT) {
            return Strings.isNullOrEmpty((String) value);
        }
        if (getServiceRequestSlot().getUiComponentType() == UIComponentType.TEXT_LOCALIZED_STRING) {
            return ((LocalizedString) value).isEmpty();
        }
        if (getServiceRequestSlot().getUiComponentType() == UIComponentType.DROP_DOWN_MULTIPLE) {
            return ((Collection) value).isEmpty();
        }
        return false;
    }

}
