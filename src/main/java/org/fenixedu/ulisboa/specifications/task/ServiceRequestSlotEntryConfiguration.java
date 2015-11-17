package org.fenixedu.ulisboa.specifications.task;

import java.io.Serializable;

public class ServiceRequestSlotEntryConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    private String slot;
    private boolean required;
    private int order;

    public ServiceRequestSlotEntryConfiguration(String slot, boolean required, int order) {
        this.slot = slot;
        this.required = required;
        this.order = order;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
