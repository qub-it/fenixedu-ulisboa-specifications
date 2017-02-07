package org.fenixedu.ulisboa.specifications.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.bennu.IBean;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlotEntry;

public class ServiceRequestPropertiesBean implements IBean {

    private List<ServiceRequestPropertyBean> serviceRequestProperties;

    public List<ServiceRequestPropertyBean> getServiceRequestProperties() {
        return serviceRequestProperties;
    }

    public void setServiceRequestProperties(final List<ServiceRequestProperty> serviceRequestProperties) {
        this.serviceRequestProperties =
                serviceRequestProperties.stream().map(prop -> new ServiceRequestPropertyBean(prop)).collect(Collectors.toList());
    }

    public void setServiceRequestEntries(final List<ServiceRequestSlotEntry> serviceRequestEntries) {
        this.serviceRequestProperties =
                serviceRequestEntries.stream().map(prop -> new ServiceRequestPropertyBean(prop)).collect(Collectors.toList());
    }

    public void updateLists() {

    }

    public ServiceRequestPropertiesBean() {
    }

    public ServiceRequestPropertiesBean(final List<ServiceRequestSlotEntry> entries) {
        setServiceRequestEntries(entries);
    }
}
