package org.fenixedu.ulisboa.specifications.dto;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlotEntry;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.UIComponentType;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.joda.time.DateTime;

import pt.ist.fenixframework.DomainObject;

public class ServiceRequestPropertyBean implements IBean {

    public static final Map<String, Object> propertyNames = new HashMap<>();

    static {
        initPropertyNames();
    }

    private String code;
    private UIComponentType uiComponentType;
    private LocalizedString label;
    private List<TupleDataSourceBean> dataSource;
    private Boolean booleanValue;
    private Integer integerValue;
    private String stringValue;
    private LocalizedString localizedStringValue;
    private DateTime dateTimeValue;
    private List<DomainObject> domainObjectListValue;
    private DomainObject domainObjectValue;
    private Locale localeValue;
    private CycleType cycleTypeValue;
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

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public LocalizedString getLocalizedStringValue() {
        return localizedStringValue;
    }

    public void setLocalizedStringValue(LocalizedString localizedStringValue) {
        this.localizedStringValue = localizedStringValue;
    }

    public DateTime getDateTimeValue() {
        return dateTimeValue;
    }

    public void setDateTimeValue(DateTime dateTime) {
        this.dateTimeValue = dateTime;
    }

    public List<DomainObject> getDomainObjectListValue() {
        return domainObjectListValue;
    }

    public void setDomainObjectListValue(List<DomainObject> domainObjectListValue) {
        this.domainObjectListValue = domainObjectListValue;
    }

    public DomainObject getDomainObjectValue() {
        return domainObjectValue;
    }

    public void setDomainObjectValue(DomainObject domainObjectValue) {
        this.domainObjectValue = domainObjectValue;
    }

    public Locale getLocaleValue() {
        return localeValue;
    }

    public void setLocaleValue(Locale localeValue) {
        this.localeValue = localeValue;
    }

    public CycleType getCycleTypeValue() {
        return cycleTypeValue;
    }

    public void setCycleTypeValue(CycleType cycleTypeValue) {
        this.cycleTypeValue = cycleTypeValue;
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

    public <T> T getValue() {
        if (getUiComponent() == null || getCode() == null) {
            throw new ULisboaSpecificationsDomainException(
                    "error.serviceRequests.ServiceRequestPropertyBean.uicomponent.code.not.defined");
        }
        String propertyName = getPropertyName(ServiceRequestSlot.getByCode(getCode()));
        try {
            return (T) PropertyUtils.getProperty(this, propertyName);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ULisboaSpecificationsDomainException(e, "error.serviceRequests.ServiceRequestProperty.read.slot", getCode(),
                    getUiComponent().toString(), propertyName);
        }
    }

    public ServiceRequestPropertyBean() {
        setShowMessage(false);
        setDomainObjectListValue(new ArrayList<>());
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
        setRequired(false);
        String propertyName = getPropertyName(slot);
        try {
            PropertyUtils.setProperty(this, propertyName, property.getValue());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ULisboaSpecificationsDomainException(e, "error.serviceRequests.ServiceRequestProperty.write.slot",
                    slot.getCode(), slot.getUiComponentType().toString(), propertyName);
        }
    }

    private static String getPropertyName(ServiceRequestSlot slot) {
        Object propertyName = propertyNames.get(slot.getUiComponentType().toString());
        if (propertyName instanceof Map) {
            return ((Map<String, String>) propertyName).get(slot.getCode());
        } else {
            return (String) propertyName;
        }
    }

    private static void initPropertyNames() {
        propertyNames.put(UIComponentType.DROP_DOWN_BOOLEAN.toString(), "booleanValue");
        propertyNames.put(UIComponentType.NUMBER.toString(), "integerValue");
        propertyNames.put(UIComponentType.TEXT.toString(), "stringValue");
        propertyNames.put(UIComponentType.TEXT_LOCALIZED_STRING.toString(), "localizedStringValue");
        propertyNames.put(UIComponentType.DATE.toString(), "dateTimeValue");
        propertyNames.put(UIComponentType.DROP_DOWN_MULTIPLE.toString(), "domainObjectListValue");
        Map<String, String> dropDownPropertyNames = new HashMap<>();
        dropDownPropertyNames.put(ULisboaConstants.DOCUMENT_PURPOSE_TYPE, "domainObjectValue");
        dropDownPropertyNames.put(ULisboaConstants.CURRICULAR_PLAN, "domainObjectValue");
        dropDownPropertyNames.put(ULisboaConstants.PROGRAM_CONCLUSION, "domainObjectValue");
        dropDownPropertyNames.put(ULisboaConstants.EXECUTION_YEAR, "domainObjectValue");
        dropDownPropertyNames.put(ULisboaConstants.LANGUAGE, "localeValue");
        dropDownPropertyNames.put(ULisboaConstants.CYCLE_TYPE, "cycleTypeValue");
        propertyNames.put(UIComponentType.DROP_DOWN_ONE_VALUE.toString(), dropDownPropertyNames);
    }

}
