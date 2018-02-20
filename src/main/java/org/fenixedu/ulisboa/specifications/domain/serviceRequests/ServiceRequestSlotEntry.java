package org.fenixedu.ulisboa.specifications.domain.serviceRequests;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;

import pt.ist.fenixframework.Atomic;

public class ServiceRequestSlotEntry extends ServiceRequestSlotEntry_Base {

    public static final Comparator<ServiceRequestSlotEntry> COMPARE_BY_ORDER_NUMBER = new Comparator<ServiceRequestSlotEntry>() {
        @Override
        public int compare(final ServiceRequestSlotEntry o1, final ServiceRequestSlotEntry o2) {
            return Integer.compare(o1.getOrderNumber(), o2.getOrderNumber());
        }
    };

    public static final Predicate<ServiceRequestSlotEntry> PRINT_PROPERTY = entry -> entry.getIsPrintConfiguration();

    protected ServiceRequestSlotEntry() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected ServiceRequestSlotEntry(final ServiceRequestType serviceRequestType, final ServiceRequestSlot slot,
            final Boolean required, final int orderNumber, final Boolean isPrintConfiguration, final Boolean isEditable) {
        this();
        setServiceRequestType(serviceRequestType);
        setServiceRequestSlot(slot);
        setRequired(required);
        setOrderNumber(orderNumber);
        setIsPrintConfiguration(isPrintConfiguration);
        setIsEditable(isEditable);
        checkRules();
    }

    private void checkRules() {
        if (getServiceRequestSlot() == null) {
            throw new ULisboaSpecificationsDomainException("error.ServiceRequestSlotEntry.slot.required");
        }
    }

    @Override
    public Boolean getRequired() {
        return super.getRequired() == null ? Boolean.FALSE : super.getRequired();
    }

    @Override
    public Boolean getIsPrintConfiguration() {
        return super.getIsPrintConfiguration() == null ? Boolean.FALSE : super.getIsPrintConfiguration();
    }

    @Override
    public Boolean getIsEditable() {
        return super.getIsEditable() == null ? Boolean.TRUE : super.getIsEditable();
    }

    @Atomic
    public void edit(final Boolean required, final int orderNumber, final Boolean isPrintConfiguration, final Boolean isEditable,
            final ServiceRequestProperty defaultProperty) {
        setRequired(required);
        setOrderNumber(orderNumber);
        setIsPrintConfiguration(isPrintConfiguration);
        setIsEditable(isEditable);
        setDefaultServiceRequestProperty(defaultProperty);
    }

    @Override
    protected void checkForDeletionBlockers(final Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);
    }

    @Atomic
    public void delete() {
        ULisboaSpecificationsDomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        setBennu(null);
        setServiceRequestSlot(null);
        setServiceRequestType(null);
        ServiceRequestProperty property = getDefaultServiceRequestProperty();
        if (property != null) {
            setDefaultServiceRequestProperty(null);
            property.delete();
        }
        deleteDomainObject();
    }

    public static Stream<ServiceRequestSlotEntry> findAll() {
        return Bennu.getInstance().getServiceRequestSlotEntriesSet().stream();
    }

    public static ServiceRequestSlotEntry findByServiceRequestProperty(final ServiceRequestProperty property) {
        return property.getServiceRequestSlot().getServiceRequestSlotEntriesSet().stream()
                .filter(entry -> entry.getServiceRequestType() != null && property.getULisboaServiceRequest() != null
                        && entry.getServiceRequestType().equals(property.getULisboaServiceRequest().getServiceRequestType()))
                .findFirst().orElse(null);
    }

    @Atomic
    public static ServiceRequestSlotEntry create(final ServiceRequestType serviceRequestType, final ServiceRequestSlot slot,
            final Boolean required, final int orderNumber) {
        return new ServiceRequestSlotEntry(serviceRequestType, slot, required, orderNumber, Boolean.FALSE, Boolean.TRUE);
    }

    @Atomic
    public static ServiceRequestSlotEntry create(final ServiceRequestType serviceRequestType, final ServiceRequestSlot slot,
            final Boolean required, final int orderNumber, final Boolean isPrintConfiguration, final Boolean isEditable) {
        return new ServiceRequestSlotEntry(serviceRequestType, slot, required, orderNumber, isPrintConfiguration, isEditable);
    }
}
