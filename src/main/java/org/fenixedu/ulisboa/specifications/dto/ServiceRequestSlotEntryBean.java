package org.fenixedu.ulisboa.specifications.dto;

import org.fenixedu.bennu.IBean;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlotEntry;

public class ServiceRequestSlotEntryBean implements IBean {

    private ServiceRequestSlotEntry entry;
    private String label;
    private boolean required;
    private boolean isPrintConfiguration;
    private int orderNumber;
    private ServiceRequestPropertyBean propertyBean;
    private ServiceRequestProperty defaultProperty;
    private ServiceRequestSlot serviceRequestSlot;
    private boolean editing;

    public ServiceRequestSlotEntry getEntry() {
        return entry;
    }

    public void setEntry(final ServiceRequestSlotEntry entry) {
        this.entry = entry;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(final boolean required) {
        this.required = required;
    }

    public boolean isPrintConfiguration() {
        return isPrintConfiguration;
    }

    public void setPrintConfiguration(final boolean isPrintConfiguration) {
        this.isPrintConfiguration = isPrintConfiguration;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(final int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public ServiceRequestPropertyBean getPropertyBean() {
        return propertyBean;
    }

    public void setPropertyBean(final ServiceRequestPropertyBean propertyBean) {
        this.propertyBean = propertyBean;
    }

    public ServiceRequestProperty getDefaultProperty() {
        return defaultProperty;
    }

    public void setDefaultProperty(final ServiceRequestProperty defaultProperty) {
        this.defaultProperty = defaultProperty;
    }

    public ServiceRequestSlot getServiceRequestSlot() {
        return serviceRequestSlot;
    }

    public void setServiceRequestSlot(final ServiceRequestSlot serviceRequestSlot) {
        this.serviceRequestSlot = serviceRequestSlot;
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(final boolean editing) {
        this.editing = editing;
    }

    public ServiceRequestSlotEntryBean() {
    }

    public ServiceRequestSlotEntryBean(final ServiceRequestSlotEntry entry) {
        this();
        setEntry(entry);
        setLabel(entry.getServiceRequestSlot().getLabel().getContent());
        setRequired(entry.getRequired());
        setPrintConfiguration(entry.getIsPrintConfiguration());
        setOrderNumber(entry.getOrderNumber());
        if (entry.getDefaultServiceRequestProperty() != null) {
            setPropertyBean(new ServiceRequestPropertyBean(entry.getDefaultServiceRequestProperty()));
            setDefaultProperty(entry.getDefaultServiceRequestProperty());
        } else {
            setPropertyBean(new ServiceRequestPropertyBean(entry));
        }
        setServiceRequestSlot(entry.getServiceRequestSlot());
        setEditing(false);
    }
}
