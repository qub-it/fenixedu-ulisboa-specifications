package org.fenixedu.ulisboa.specifications.domain.serviceRequests;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;

public class ServiceRequestSlotEntry extends ServiceRequestSlotEntry_Base {

    public static final Comparator<ServiceRequestSlotEntry> COMPARE_BY_ORDER_NUMBER = new Comparator<ServiceRequestSlotEntry>() {
        @Override
        public int compare(ServiceRequestSlotEntry o1, ServiceRequestSlotEntry o2) {
            return Integer.compare(o1.getOrderNumber(), o2.getOrderNumber());
        }
    };

    protected ServiceRequestSlotEntry() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected ServiceRequestSlotEntry(ServiceRequestType serviceRequestType, final ServiceRequestSlot slot,
            final Boolean required, final int orderNumber) {
        this();
        setServiceRequestType(serviceRequestType);
        setServiceRequestSlot(slot);
        setRequired(required);
        setOrderNumber(orderNumber);
        checkRules();
    }

    private void checkRules() {
        if (getServiceRequestSlot() == null) {
            throw new DomainException("error.ServiceRequestSlotEntry.slot.required");
        }
    }

    @Override
    public Boolean getRequired() {
        return super.getRequired() == null ? false : super.getRequired().booleanValue();
    }

    @Atomic
    public void edit(final Boolean required, final int orderNumber) {
        setRequired(required);
        setOrderNumber(orderNumber);
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);
    }

    @Atomic
    public void delete() {
        DomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        setBennu(null);
        setServiceRequestSlot(null);
        setServiceRequestType(null);
        deleteDomainObject();
    }

    public static Stream<ServiceRequestSlotEntry> findAll() {
        return Bennu.getInstance().getServiceRequestSlotEntriesSet().stream();
    }

    public static ServiceRequestSlotEntry findByServiceRequestProperty(ServiceRequestProperty property) {
        return property.getServiceRequestSlot().getServiceRequestSlotEntriesSet().stream().filter(
                entry -> entry.getServiceRequestType().equals(property.getULisboaServiceRequest().getServiceRequestType()))
                .findFirst().orElse(null);
    }

    @Atomic
    public static ServiceRequestSlotEntry create(final ServiceRequestType serviceRequestType, final ServiceRequestSlot slot,
            final Boolean required, final int orderNumber) {
        return new ServiceRequestSlotEntry(serviceRequestType, slot, required, orderNumber);
    }
}
