package org.fenixedu.ulisboa.specifications.task;

import java.io.Serializable;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlotEntry;

public class ServiceRequestSlotEntryConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    private String slot;
    private boolean required;
    private int order;

    public ServiceRequestSlotEntryConfiguration(final String slot, final boolean required, final int order) {
        //In CSV file, order number starts in 1 and not in 0
        this.slot = slot;
        this.required = required;
        this.order = order - 1;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(final String slot) {
        this.slot = slot;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(final boolean required) {
        this.required = required;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(final int order) {
        this.order = order;
    }

    public void execute(final ServiceRequestType serviceRequestType) {
        ServiceRequestSlotEntry.create(serviceRequestType, ServiceRequestSlot.getByCode(slot), required, order);
    }

}
