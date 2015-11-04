package org.fenixedu.ulisboa.specifications.ui.adapters;

import java.lang.reflect.Type;

import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.UIComponentType;
import org.fenixedu.ulisboa.specifications.dto.ServiceRequestPropertyBean;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ServiceRequestPropertyBeanAdapter implements JsonSerializer<ServiceRequestPropertyBean>,
        JsonDeserializer<ServiceRequestPropertyBean> {

    @Override
    public JsonElement serialize(ServiceRequestPropertyBean src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("code", src.getCode());
        object.add("uiComponentType", context.serialize(src.getUiComponent()));
        object.add("label", context.serialize(src.getLabel()));
        object.add("required", context.serialize(src.isRequired()));
        object.add("dataSource", context.serialize(src.getDataSource()));
        if (src.getValue() != null) {
            addValueProperty(src, typeOfSrc, context, object);
        }
        return object;
    }

    private void addValueProperty(ServiceRequestPropertyBean src, Type typeOfSrc, JsonSerializationContext context,
            JsonObject object) {
        switch (src.getUiComponent()) {
        case DROP_DOWN_BOOLEAN:
            object.add("value", context.serialize(Boolean.valueOf(src.getValue())));
            break;
        case NUMBER:
            object.add("value", context.serialize(Integer.valueOf(src.getValue())));
            break;
        case TEXT_LOCALIZED_STRING:
            object.add("value", new JsonParser().parse(src.getValue()).getAsJsonObject());
            break;
        case DROP_DOWN_MULTIPLE:
            object.add("value", new JsonParser().parse(src.getValue()).getAsJsonArray());
            break;
        //Handle as a string
        case TEXT:
        case DROP_DOWN_ONE_VALUE:
        case DATE:
        default:
            object.addProperty("value", src.getValue());
            break;
        }
    }

    @Override
    public ServiceRequestPropertyBean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonBean = json.getAsJsonObject();
        ServiceRequestPropertyBean bean = new ServiceRequestPropertyBean();
        bean.setCode(jsonBean.get("code").getAsString());
        bean.setRequired(jsonBean.get("required").getAsBoolean());
        bean.setUiComponentType(context.deserialize(jsonBean.get("uiComponentType"), UIComponentType.class));
        bean.setLabel(context.deserialize(jsonBean.get("label"), LocalizedString.class));
        if (jsonBean.get("value") != null) {
            if (bean.isValueStoredAsString()) {
                bean.setValue(jsonBean.get("value").getAsString());
            } else {
                bean.setValue(jsonBean.get("value").toString());
            }
        }
        return bean;
    }
}
