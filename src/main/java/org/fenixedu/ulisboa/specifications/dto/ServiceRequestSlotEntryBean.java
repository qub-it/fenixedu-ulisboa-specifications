package org.fenixedu.ulisboa.specifications.dto;

import org.fenixedu.bennu.IBean;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlotEntry;

public class ServiceRequestSlotEntryBean implements IBean {

    private ServiceRequestSlotEntry entry;
    private String label;
    private boolean required;
    private int orderNumber;
    private ServiceRequestSlot serviceRequestSlot;
    private boolean editing;

    public ServiceRequestSlotEntry getEntry() {
        return entry;
    }

    public void setEntry(ServiceRequestSlotEntry entry) {
        this.entry = entry;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public ServiceRequestSlot getServiceRequestSlot() {
        return serviceRequestSlot;
    }

    public void setServiceRequestSlot(ServiceRequestSlot serviceRequestSlot) {
        this.serviceRequestSlot = serviceRequestSlot;
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    public ServiceRequestSlotEntryBean() {
    }

    public ServiceRequestSlotEntryBean(ServiceRequestSlotEntry entry) {
        this();
        setEntry(entry);
        setLabel(entry.getServiceRequestSlot().getLabel().getContent());
        setRequired(entry.getRequired());
        setOrderNumber(entry.getOrderNumber());
        setServiceRequestSlot(entry.getServiceRequestSlot());
        setEditing(false);
    }
}
