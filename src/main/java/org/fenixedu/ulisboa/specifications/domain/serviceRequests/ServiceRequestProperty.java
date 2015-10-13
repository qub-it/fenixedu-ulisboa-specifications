package org.fenixedu.ulisboa.specifications.domain.serviceRequests;

import java.util.Collection;
import java.util.Locale;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentPurposeTypeInstance;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.exceptions.TreasuryDomainException;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public class ServiceRequestProperty extends ServiceRequestProperty_Base {

    public ServiceRequestProperty() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected ServiceRequestProperty(Locale locale, Boolean booleanValue, CycleType cycleType, Integer integer, String string,
            LocalizedString localizedString, DateTime dateTime, Registration registration, ExecutionYear executionYear,
            DocumentPurposeTypeInstance documentPurposeTypeInstance, ServiceRequestSlot serviceRequestSlot) {
        this();
        //Values
        setLocale(locale);
        setBooleanValue(booleanValue);
        setCycleType(cycleType);
        setInteger(integer);
        setString(string);
        setLocalizedString(localizedString);
        setDateTime(dateTime);
        setRegistration(registration);
        setExecutionYear(executionYear);
        setDocumentPurposeTypeInstance(documentPurposeTypeInstance);

        setServiceRequestSlot(serviceRequestSlot);
        checkRules();
    }

    private void checkRules() {

        if (getServiceRequestSlot() == null) {
            throw new DomainException("error.ServiceRequestProperty.serviceRequestSlot.required");
        }
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);
    }

    @Atomic
    public void delete() {
        TreasuryDomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        setULisboaServiceRequest(null);
        setDocumentPurposeTypeInstance(null);
        setExecutionYear(null);
        setRegistration(null);
        setServiceRequestSlot(null);

        setBennu(null);
        deleteDomainObject();
    }

    @Override
    public DocumentPurposeTypeInstance getDocumentPurposeTypeInstance() {
        boolean isValid = FenixFramework.isDomainObjectValid(super.getDocumentPurposeTypeInstance());
        return isValid ? super.getDocumentPurposeTypeInstance() : null;
    }

    @Override
    public Registration getRegistration() {
        boolean isValid = FenixFramework.isDomainObjectValid(super.getRegistration());
        return isValid ? super.getRegistration() : null;
    }

    @Override
    public ExecutionYear getExecutionYear() {
        boolean isValid = FenixFramework.isDomainObjectValid(super.getExecutionYear());
        return isValid ? super.getExecutionYear() : null;
    }

    public static Stream<ServiceRequestProperty> findAll() {
        return Bennu.getInstance().getServiceRequestPropertiesSet().stream();
    }

    @Atomic
    public static ServiceRequestProperty createLocaleProperty(Locale value, ServiceRequestSlot serviceRequestSlot) {
        return new ServiceRequestProperty(value, null, null, null, null, null, null, null, null, null, serviceRequestSlot);
    }

    @Atomic
    public static ServiceRequestProperty createBooleanProperty(Boolean value, ServiceRequestSlot serviceRequestSlot) {
        return new ServiceRequestProperty(null, value, null, null, null, null, null, null, null, null, serviceRequestSlot);
    }

    @Atomic
    public static ServiceRequestProperty createCycleTypeProperty(CycleType value, ServiceRequestSlot serviceRequestSlot) {
        return new ServiceRequestProperty(null, null, value, null, null, null, null, null, null, null, serviceRequestSlot);
    }

    @Atomic
    public static ServiceRequestProperty createIntegerProperty(Integer value, ServiceRequestSlot serviceRequestSlot) {
        return new ServiceRequestProperty(null, null, null, value, null, null, null, null, null, null, serviceRequestSlot);
    }

    @Atomic
    public static ServiceRequestProperty createStringProperty(String value, ServiceRequestSlot serviceRequestSlot) {
        return new ServiceRequestProperty(null, null, null, null, value, null, null, null, null, null, serviceRequestSlot);
    }

    @Atomic
    public static ServiceRequestProperty createLocalizedStringProperty(LocalizedString value,
            ServiceRequestSlot serviceRequestSlot) {
        return new ServiceRequestProperty(null, null, null, null, null, value, null, null, null, null, serviceRequestSlot);
    }

    @Atomic
    public static ServiceRequestProperty createDateTimeProperty(DateTime value, ServiceRequestSlot serviceRequestSlot) {
        return new ServiceRequestProperty(null, null, null, null, null, null, value, null, null, null, serviceRequestSlot);
    }

    @Atomic
    public static ServiceRequestProperty createRegistrationProperty(Registration value, ServiceRequestSlot serviceRequestSlot) {
        return new ServiceRequestProperty(null, null, null, null, null, null, null, value, null, null, serviceRequestSlot);
    }

    @Atomic
    public static ServiceRequestProperty createExecutionYearProperty(ExecutionYear value, ServiceRequestSlot serviceRequestSlot) {
        return new ServiceRequestProperty(null, null, null, null, null, null, null, null, value, null, serviceRequestSlot);
    }

    @Atomic
    public static ServiceRequestProperty createDocumentPurposeTypeInstanceProperty(DocumentPurposeTypeInstance value,
            ServiceRequestSlot serviceRequestSlot) {
        return new ServiceRequestProperty(null, null, null, null, null, null, null, null, null, value, serviceRequestSlot);
    }

}
