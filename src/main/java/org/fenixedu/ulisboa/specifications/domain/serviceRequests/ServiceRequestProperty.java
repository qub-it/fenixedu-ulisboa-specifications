package org.fenixedu.ulisboa.specifications.domain.serviceRequests;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.ExternalEnrolment;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.dto.ServiceRequestPropertyBean;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import com.google.common.base.Strings;

import pt.ist.fenixframework.Atomic;

public class ServiceRequestProperty extends ServiceRequestProperty_Base {

    public static final Map<String, Object> PROPERTY_NAMES = new HashMap<>();

    static {
        initPropertyNames();
    }

    public static final Comparator<ServiceRequestProperty> COMPARATE_BY_ENTRY_NUMBER = (o1, o2) -> {
        ServiceRequestSlotEntry entry1 = ServiceRequestSlotEntry.findByServiceRequestProperty(o1);
        ServiceRequestSlotEntry entry2 = ServiceRequestSlotEntry.findByServiceRequestProperty(o2);
        if (entry1 == null) {
            return 1;
        }
        if (entry2 == null) {
            return -1;
        }
        return Integer.compare(entry1.getOrderNumber(), entry2.getOrderNumber());
    };

    protected ServiceRequestProperty() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected ServiceRequestProperty(final ServiceRequestSlot serviceRequestSlot) {
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
    protected void checkForDeletionBlockers(final Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);
    }

    @Atomic
    public void delete() {
        ULisboaSpecificationsDomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        setULisboaServiceRequest(null);
        setDocumentPurposeTypeInstance(null);
        setExecutionInterval(null);
        setProgramConclusion(null);
        setServiceRequestSlot(null);
        super.getCurriculumLinesSet().clear();
        super.getExternalEnrolmentsSet().clear();

        setBennu(null);
        deleteDomainObject();
    }

    public List<ICurriculumEntry> getICurriculumEntriesSet() {
        Stream<ICurriculumEntry> curriculumEntries = super.getCurriculumLinesSet().stream().map(ICurriculumEntry.class::cast);
        Stream<ICurriculumEntry> externalEntries = super.getExternalEnrolmentsSet().stream().map(ICurriculumEntry.class::cast);
        return Stream.concat(curriculumEntries, externalEntries).collect(Collectors.toList());
    }

    public void setICurriculumEntriesSet(final List<ICurriculumEntry> entries) {
        getCurriculumLinesSet().clear();
        getExternalEnrolmentsSet().clear();
        for (ICurriculumEntry entry : entries) {
            if (entry instanceof CurriculumLine) {
                addCurriculumLines((CurriculumLine) entry);
            } else if (entry instanceof ExternalEnrolment) {
                addExternalEnrolments((ExternalEnrolment) entry);
            } else {
                throw new ULisboaSpecificationsDomainException("error.ServiceRequestProperty.curriculumEntry.not.supported");
            }
        }
    }

    public ExecutionYear getExecutionYear() {
        ExecutionInterval executionInterval = getExecutionInterval();
        if (executionInterval != null && executionInterval instanceof ExecutionSemester) {
            throw new ULisboaSpecificationsDomainException("error.ServiceRequestProperty.executionInterval.wrong.getter",
                    "getExecutionSemester");
        }
        return (ExecutionYear) executionInterval;
    }

    public void setExecutionYear(final ExecutionYear executionYear) {
        setExecutionInterval(executionYear);
    }

    public ExecutionSemester getExecutionSemester() {
        ExecutionInterval executionInterval = getExecutionInterval();
        if (executionInterval != null && executionInterval instanceof ExecutionYear) {
            throw new ULisboaSpecificationsDomainException("error.ServiceRequestProperty.executionInterval.wrong.getter",
                    "getExecutionYear");
        }
        return (ExecutionSemester) executionInterval;
    }

    public void setExecutionSemester(final ExecutionSemester executionSemester) {
        setExecutionInterval(executionSemester);
    }

    public void setValue(final Object value) {
        String propertyName = getPropertyName(getServiceRequestSlot());
        try {
            BeanUtils.setProperty(this, propertyName, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ULisboaSpecificationsDomainException(e, "error.serviceRequests.ServiceRequestProperty.write.slot",
                    getServiceRequestSlot().getCode(), getServiceRequestSlot().getUiComponentType().toString(), propertyName);
        }
    }

    public <T> T getValue() {
        ServiceRequestSlot slot = getServiceRequestSlot();
        if (slot.getUiComponentType() == null || slot.getCode() == null) {
            throw new ULisboaSpecificationsDomainException(
                    "error.serviceRequests.ServiceRequestPropertyBean.uicomponent.code.not.defined");
        }
        String pName = getPropertyName(slot);
        try {
            return (T) PropertyUtils.getProperty(this, pName);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ULisboaSpecificationsDomainException(e, "error.serviceRequests.ServiceRequestProperty.read.slot",
                    slot.getCode(), slot.getUiComponentType().toString(), pName);
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

    public static Stream<ServiceRequestProperty> findAll() {
        return Bennu.getInstance().getServiceRequestPropertiesSet().stream();
    }

    public static Stream<ServiceRequestProperty> findByCode(final String code) {
        return ServiceRequestProperty.findAll().filter(prop -> prop.getServiceRequestSlot().getCode().equals(code));
    }

    public static Stream<ServiceRequestProperty> find(final ULisboaServiceRequest request, final ServiceRequestSlot slot) {
        return request.getServiceRequestPropertiesSet().stream().filter(p -> p.getServiceRequestSlot() == slot);
    }

    @Atomic
    public static ServiceRequestProperty create(final ServiceRequestSlot slot) {
        return new ServiceRequestProperty(slot);
    }

    @Atomic
    @Deprecated
    // This method should receive the service request
    public static ServiceRequestProperty create(final ServiceRequestSlot slot, final Object value) {
        if (value == null) {
            return create(slot);
        }
        ServiceRequestProperty property = new ServiceRequestProperty(slot);
        property.setValue(value);
        return property;
    }

    // This method should receive the service request
    public static ServiceRequestProperty create(final ULisboaServiceRequest request, final ServiceRequestSlot slot,
            final Object value) {
        if (value == null) {
            return create(slot);
        }
        ServiceRequestProperty property = new ServiceRequestProperty(slot);
        property.setValue(value);

        property.setULisboaServiceRequest(request);

        return property;
    }

    @Atomic
    public static ServiceRequestProperty create(final ServiceRequestPropertyBean bean) {
        ServiceRequestSlot slot = ServiceRequestSlot.getByCode(bean.getCode());
        Object value = bean.getValue();
        return create(slot, value);
    }

    private static String getPropertyName(final ServiceRequestSlot slot) {
        Object propertyName = PROPERTY_NAMES.get(slot.getUiComponentType().toString());
        if (propertyName instanceof Map) {
            return ((Map<String, String>) propertyName).get(slot.getCode());
        } else {
            return (String) propertyName;
        }
    }

    private static void initPropertyNames() {
        PROPERTY_NAMES.put(UIComponentType.DROP_DOWN_BOOLEAN.toString(), "booleanValue");
        PROPERTY_NAMES.put(UIComponentType.NUMBER.toString(), "integer");
        PROPERTY_NAMES.put(UIComponentType.TEXT.toString(), "string");
        PROPERTY_NAMES.put(UIComponentType.TEXT_LOCALIZED_STRING.toString(), "localizedString");
        PROPERTY_NAMES.put(UIComponentType.DATE.toString(), "dateTime");
        PROPERTY_NAMES.put(UIComponentType.DROP_DOWN_MULTIPLE.toString(), "ICurriculumEntriesSet");
        Map<String, String> dropDownPropertyNames = new HashMap<>();
        dropDownPropertyNames.put(ULisboaConstants.LANGUAGE, "locale");
        dropDownPropertyNames.put(ULisboaConstants.DOCUMENT_PURPOSE_TYPE, "documentPurposeTypeInstance");
        dropDownPropertyNames.put(ULisboaConstants.CYCLE_TYPE, "cycleType");
        dropDownPropertyNames.put(ULisboaConstants.PROGRAM_CONCLUSION, "programConclusion");
        dropDownPropertyNames.put(ULisboaConstants.EXECUTION_YEAR, "executionInterval");
        dropDownPropertyNames.put(ULisboaConstants.EXECUTION_SEMESTER, "executionInterval");
        dropDownPropertyNames.put(ULisboaConstants.EVALUATION_SEASON, "evaluationSeason");
        dropDownPropertyNames.put(ULisboaConstants.CURRICULAR_PLAN, "studentCurricularPlan");
        dropDownPropertyNames.put(ULisboaConstants.ENROLMENTS_BY_SEMESTER, "enrolment");
        dropDownPropertyNames.put(ULisboaConstants.ENROLMENTS_BEFORE_SEMESTER, "enrolment");
        PROPERTY_NAMES.put(UIComponentType.DROP_DOWN_ONE_VALUE.toString(), dropDownPropertyNames);
    }

}
