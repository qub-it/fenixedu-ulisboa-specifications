package org.fenixedu.ulisboa.specifications.dto;

import java.util.List;

import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlotEntry;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.UIComponentType;

public class ServiceRequestPropertyBean implements IBean {

    /**
     * In case of adding more variables don't forget to add in ServiceRequestPropertyBeanAdapter
     */
    private String code;
    private UIComponentType uiComponentType;
    private LocalizedString label;
    private List<TupleDataSourceBean> dataSource;
    private String value;
    private boolean required;
    private boolean showMessage;

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

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isShowMessage() {
        return showMessage;
    }

    public void setShowMessage(boolean showMessage) {
        this.showMessage = showMessage;
    }

    public ServiceRequestPropertyBean() {
        setShowMessage(false);
    }

    public ServiceRequestPropertyBean(ServiceRequestSlotEntry serviceRequestSlotEntry) {
        this();
        ServiceRequestSlot serviceRequestSlot = serviceRequestSlotEntry.getServiceRequestSlot();
        setCode(serviceRequestSlot.getCode());
        setRequired(serviceRequestSlotEntry.getRequired());
        setUiComponentType(serviceRequestSlot.getUiComponentType());
        setLabel(serviceRequestSlot.getLabel());
    }

    public ServiceRequestPropertyBean(ServiceRequestProperty property) {
        this();
        ServiceRequestSlot slot = property.getServiceRequestSlot();
        setCode(slot.getCode());
        setUiComponentType(slot.getUiComponentType());
        setLabel(slot.getLabel());
        setRequired(true);
    }

    public boolean isValueStoredAsString() {
        return getUiComponent() == UIComponentType.TEXT || getUiComponent() == UIComponentType.DROP_DOWN_ONE_VALUE
                || getUiComponent() == UIComponentType.DATE;
    }

}
