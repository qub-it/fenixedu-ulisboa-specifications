package org.fenixedu.ulisboa.specifications.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestCategory;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlotEntry;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors.ULisboaServiceRequestProcessor;

public class ServiceRequestTypeConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    private String srtCode;
    private LocalizedString srtName;
    private boolean active;
    private boolean emolument;
    private boolean notify;
    private boolean print;
    private boolean online;
    private ServiceRequestCategory category;
    private List<String> processorNames;
    private List<ServiceRequestSlotEntryConfiguration> slotConfigurations;

    public ServiceRequestTypeConfiguration() {
        slotConfigurations = new ArrayList<ServiceRequestSlotEntryConfiguration>();
    }

    public String getServiceRequestTypeCode() {
        return srtCode;
    }

    public void setServiceRequestTypeCode(String srtCode) {
        this.srtCode = srtCode;
    }

    public String getSrtCode() {
        return srtCode;
    }

    public void setSrtCode(String srtCode) {
        this.srtCode = srtCode;
    }

    public LocalizedString getSrtName() {
        return srtName;
    }

    public void setSrtName(LocalizedString srtName) {
        this.srtName = srtName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isEmolument() {
        return emolument;
    }

    public void setEmolument(boolean emolument) {
        this.emolument = emolument;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public boolean isPrint() {
        return print;
    }

    public void setPrint(boolean print) {
        this.print = print;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public ServiceRequestCategory isCategory() {
        return category;
    }

    public void setCategory(ServiceRequestCategory category) {
        this.category = category;
    }

    public List<String> getProcessors() {
        return processorNames;
    }

    public void setProcessors(List<String> processors) {
        this.processorNames = processors;
    }

    public List<ServiceRequestSlotEntryConfiguration> getSlotConfigurations() {
        return slotConfigurations;
    }

    public void setSlotConfigurations(List<ServiceRequestSlotEntryConfiguration> slotConfigurations) {
        this.slotConfigurations = slotConfigurations;
    }

    public void addSlotConfiguration(ServiceRequestSlotEntryConfiguration slotConfiguration) {
        this.slotConfigurations.add(slotConfiguration);
    }

    public void execute() {
        Optional<ServiceRequestType> optSRT = ServiceRequestType.findUniqueByCode(srtCode);
        ServiceRequestType serviceRequestType;
        if (optSRT.isPresent()) {
            serviceRequestType = optSRT.get();
            serviceRequestType.edit(srtCode, srtName, active, emolument, notify, print, online, category, new LocalizedString());
        } else {
            serviceRequestType = ServiceRequestType.create(srtCode, srtName, active, emolument, notify, print, online, category);
        }
        for (ULisboaServiceRequestProcessor processor : serviceRequestType.getULisboaServiceRequestProcessorsSet()) {
            serviceRequestType.removeULisboaServiceRequestProcessors(processor);
        }
        for (String processorName : getProcessors()) {
            serviceRequestType.addULisboaServiceRequestProcessors(ULisboaServiceRequestProcessor.getByName(processorName));
        }
        for (ServiceRequestSlotEntry serviceRequestSlotEntry : serviceRequestType.getServiceRequestSlotEntriesSet()) {
            serviceRequestSlotEntry.delete();
        }
        for (ServiceRequestSlotEntryConfiguration serviceRequestSlotEntryConfiguration : slotConfigurations) {
            serviceRequestSlotEntryConfiguration.execute(serviceRequestType);
        }
    }

}
