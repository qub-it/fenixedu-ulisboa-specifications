package org.fenixedu.ulisboa.specifications.dto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestCategory;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;

public class ServiceRequestTypeBean implements IBean {

    private String code;
    private LocalizedString name;
    private boolean active;
    private boolean payable;
    private boolean notifyUponConclusion;
    private ServiceRequestCategory serviceRequestCategory;
    private List<TupleDataSourceBean> serviceRequestCategoryDataSource;
    private List<ServiceRequestSlot> serviceRequestSlots;
    private List<TupleDataSourceBean> serviceRequestSlotsDataSource;
    private LocalizedString numberOfUnitsLabel;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalizedString getName() {
        return name;
    }

    public void setName(LocalizedString name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPayable() {
        return payable;
    }

    public void setPayable(boolean payable) {
        this.payable = payable;
    }

    public boolean isNotifyUponConclusion() {
        return notifyUponConclusion;
    }

    public void setNotifyUponConclusion(boolean notifyUponConclusion) {
        this.notifyUponConclusion = notifyUponConclusion;
    }

    public ServiceRequestCategory getServiceRequestCategory() {
        return serviceRequestCategory;
    }

    public void setServiceRequestCategory(ServiceRequestCategory serviceRequestCategory) {
        this.serviceRequestCategory = serviceRequestCategory;
    }

    public List<TupleDataSourceBean> getServiceRequestCategoryDataSource() {
        return serviceRequestCategoryDataSource;
    }

    public void setServiceRequestCategoryDataSource(List<ServiceRequestCategory> serviceRequestCategorySet) {
        this.serviceRequestCategoryDataSource = serviceRequestCategorySet.stream().map(src -> {
            TupleDataSourceBean tupleDataSourceBean = new TupleDataSourceBean();
            tupleDataSourceBean.setId(src.toString());
            tupleDataSourceBean.setText(src.toString());
            return tupleDataSourceBean;
        }).collect(Collectors.toList());
    }

    public List<ServiceRequestSlot> getServiceRequestSlots() {
        return serviceRequestSlots;
    }

    public void setServiceRequestSlots(List<ServiceRequestSlot> serviceRequestSlots) {
        this.serviceRequestSlots = serviceRequestSlots;
    }

    public List<TupleDataSourceBean> getServiceRequestSlotsDataSource() {
        return serviceRequestSlotsDataSource;
    }

    public void setServiceRequestSlotsDataSource(List<ServiceRequestSlot> serviceRequestSlotsSet) {
        this.serviceRequestSlotsDataSource = serviceRequestSlotsSet.stream().map(srs -> {
            TupleDataSourceBean tupleDataSourceBean = new TupleDataSourceBean();
            tupleDataSourceBean.setId(srs.getExternalId());
            tupleDataSourceBean.setText(srs.getCode());
            return tupleDataSourceBean;
        }).collect(Collectors.toList());
    }

    public LocalizedString getNumberOfUnitsLabel() {
        return numberOfUnitsLabel;
    }

    public void setNumberOfUnitsLabel(LocalizedString numberOfUnitsLabel) {
        this.numberOfUnitsLabel = numberOfUnitsLabel;
    }

    public ServiceRequestTypeBean() {
        setServiceRequestCategoryDataSource(Arrays.asList(ServiceRequestCategory.values()));
        setServiceRequestSlotsDataSource(ServiceRequestSlot.findAll().collect(Collectors.toList()));
    }

    public ServiceRequestTypeBean(ServiceRequestType serviceRequestType) {
        this();
        setCode(serviceRequestType.getCode());
        setName(serviceRequestType.getName());
        setActive(serviceRequestType.getActive());
        setPayable(serviceRequestType.getPayable());
        setNotifyUponConclusion(serviceRequestType.getNotifyUponConclusion());
        setServiceRequestCategory(serviceRequestType.getServiceRequestCategory());
        setServiceRequestSlots(serviceRequestType.getServiceRequestSlotsSet().stream().collect(Collectors.toList()));
        setNumberOfUnitsLabel(serviceRequestType.getNumberOfUnitsLabel());
    }
}
