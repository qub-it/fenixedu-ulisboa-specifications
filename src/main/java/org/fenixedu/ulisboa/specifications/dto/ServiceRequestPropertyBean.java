package org.fenixedu.ulisboa.specifications.dto;

import java.util.List;

import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.UIComponentType;

public class ServiceRequestPropertyBean implements IBean {

    private String code;
    private UIComponentType uiComponentType;
    private LocalizedString label;
    private List<TupleDataSourceBean> dataSource;
    private String value;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UIComponentType getUiComponent() {
        return uiComponentType;
    }

    public void setUiComponentType(UIComponentType uiComponent) {
        this.uiComponentType = uiComponent;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public void setLabel(LocalizedString label) {
        this.label = label;
    }

    public List<TupleDataSourceBean> getDataSource() {
        return dataSource;
    }

    public void setDataSource(List<TupleDataSourceBean> dataSource) {
        this.dataSource = dataSource;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ServiceRequestPropertyBean() {

    }

    public ServiceRequestPropertyBean(ServiceRequestSlot serviceRequestSlot) {
        setCode(serviceRequestSlot.getCode());
        setUiComponentType(serviceRequestSlot.getUiComponentType());
        setLabel(serviceRequestSlot.getLabel());
    }

}
