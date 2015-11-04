package org.fenixedu.ulisboa.specifications.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlotEntry;

import com.google.common.collect.Sets;

public class ServiceRequestSlotsBean implements IBean {

    private ServiceRequestType serviceRequestType;
    private List<ServiceRequestSlotEntryBean> serviceRequestSlotEntries;
    private List<TupleDataSourceBean> serviceRequestSlotsDataSource;

    public ServiceRequestType getServiceRequestType() {
        return serviceRequestType;
    }

    public void setServiceRequestType(ServiceRequestType serviceRequestType) {
        this.serviceRequestType = serviceRequestType;
    }

    public List<ServiceRequestSlotEntryBean> getServiceRequestSlotEntries() {
        return serviceRequestSlotEntries;
    }

    public void setServiceRequestSlotEntries(List<ServiceRequestSlotEntry> serviceRequestSlotEntries) {
        this.serviceRequestSlotEntries =
                serviceRequestSlotEntries.stream().map(entry -> new ServiceRequestSlotEntryBean(entry))
                        .collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getServiceRequestSlotsDataSource() {
        return serviceRequestSlotsDataSource;
    }

    public void setServiceRequestSlotsDataSource(List<ServiceRequestSlot> serviceRequestSlotsSet) {
        this.serviceRequestSlotsDataSource = serviceRequestSlotsSet.stream().map(srs -> {
            TupleDataSourceBean tupleDataSourceBean = new TupleDataSourceBean();
            tupleDataSourceBean.setId(srs.getExternalId());
            tupleDataSourceBean.setText(srs.getLabel().getContent());
            return tupleDataSourceBean;
        }).collect(Collectors.toList());
    }

    public ServiceRequestSlotsBean() {
        setServiceRequestSlotsDataSource(ServiceRequestSlot.findAll().collect(Collectors.toList()));
    }

    public ServiceRequestSlotsBean(ServiceRequestType serviceRequestType) {
        // remove the already selected
        setServiceRequestType(serviceRequestType);
        setServiceRequestSlotsDataSource(filterServiceRequestSlotList(serviceRequestType));
        setServiceRequestSlotEntries(serviceRequestType.getServiceRequestSlotEntriesSet().stream()
                .sorted(ServiceRequestSlotEntry.COMPARE_BY_ORDER_NUMBER).collect(Collectors.toList()));
    }

    public List<ServiceRequestSlot> filterServiceRequestSlotList(ServiceRequestType serviceRequestType) {
        return Sets
                .difference(
                        ServiceRequestSlot.findAll().collect(Collectors.toSet()),
                        serviceRequestType.getServiceRequestSlotEntriesSet().stream()
                                .map(ServiceRequestSlotEntry::getServiceRequestSlot).collect(Collectors.toSet())).stream()
                .sorted(ServiceRequestSlot.COMPARE_BY_LABEL).collect(Collectors.toList());
    }

    public void updateModelLists() {
        setServiceRequestSlotsDataSource(filterServiceRequestSlotList(serviceRequestType));
        setServiceRequestSlotEntries(serviceRequestType.getServiceRequestSlotEntriesSet().stream()
                .sorted(ServiceRequestSlotEntry.COMPARE_BY_ORDER_NUMBER).collect(Collectors.toList()));
    }
}
