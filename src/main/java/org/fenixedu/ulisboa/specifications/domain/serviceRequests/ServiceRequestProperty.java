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
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.treasury.domain.exceptions.TreasuryDomainException;
import org.fenixedu.ulisboa.specifications.util.Constants;

import pt.ist.fenixframework.Atomic;

public class ServiceRequestProperty extends ServiceRequestProperty_Base {

    public ServiceRequestProperty() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected ServiceRequestProperty(Locale locale, Boolean booleanValue, CycleType cycleType, Integer integer, String string,
            Registration registration, ExecutionYear executionYear, DocumentPurposeTypeInstance documentPurposeTypeInstance,
            ServiceRequestSlot serviceRequestSlot) {
        //Values
        setLocale(locale);
        setBooleanValue(booleanValue);
        setCycleType(cycleType);
        setInteger(integer);
        setString(string);
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
        if (getULisboaServiceRequest() != null) {
            blockers.add(BundleUtil.getString(Constants.BUNDLE, "error.ServiceRequestSlot.connected.ULisboaServiceRequest"));
        }
    }

    @Atomic
    public void delete() {
        TreasuryDomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        setDocumentPurposeTypeInstance(null);
        setExecutionYear(null);
        setRegistration(null);
        setServiceRequestSlot(null);

        setBennu(null);
        deleteDomainObject();
    }

    public static Stream<ServiceRequestProperty> findAll() {
        return Bennu.getInstance().getServiceRequestPropertiesSet().stream();
    }

    @Atomic
    public static ServiceRequestProperty createLocaleProperty(Locale value, ServiceRequestSlot serviceRequestSlot) {
        return new ServiceRequestProperty(value, null, null, null, null, null, null, null, serviceRequestSlot);
    }

    @Atomic
    public static ServiceRequestProperty createBooleanProperty(Boolean value, ServiceRequestSlot serviceRequestSlot) {
        return new ServiceRequestProperty(null, value, null, null, null, null, null, null, serviceRequestSlot);
    }

    @Atomic
    public static ServiceRequestProperty createCycleTypeProperty(CycleType value, ServiceRequestSlot serviceRequestSlot) {
        return new ServiceRequestProperty(null, null, value, null, null, null, null, null, serviceRequestSlot);
    }

    @Atomic
    public static ServiceRequestProperty createIntegerProperty(Integer value, ServiceRequestSlot serviceRequestSlot) {
        return new ServiceRequestProperty(null, null, null, value, null, null, null, null, serviceRequestSlot);
    }

    @Atomic
    public static ServiceRequestProperty createStringProperty(String value, ServiceRequestSlot serviceRequestSlot) {
        return new ServiceRequestProperty(null, null, null, null, value, null, null, null, serviceRequestSlot);
    }

    @Atomic
    public static ServiceRequestProperty createRegistrationProperty(Registration value, ServiceRequestSlot serviceRequestSlot) {
        return new ServiceRequestProperty(null, null, null, null, null, value, null, null, serviceRequestSlot);
    }

    @Atomic
    public static ServiceRequestProperty createExecutionYearProperty(ExecutionYear value, ServiceRequestSlot serviceRequestSlot) {
        return new ServiceRequestProperty(null, null, null, null, null, null, value, null, serviceRequestSlot);
    }

    @Atomic
    public static ServiceRequestProperty createDocumentPurposeTypeInstanceProperty(DocumentPurposeTypeInstance value,
            ServiceRequestSlot serviceRequestSlot) {
        return new ServiceRequestProperty(null, null, null, null, null, null, null, value, serviceRequestSlot);
    }

}
