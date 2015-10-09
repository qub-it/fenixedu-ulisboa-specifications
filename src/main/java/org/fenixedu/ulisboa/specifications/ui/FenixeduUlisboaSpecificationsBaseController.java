package org.fenixedu.ulisboa.specifications.ui;

import org.fenixedu.bennu.spring.FenixEDUBaseController;
import org.fenixedu.ulisboa.specifications.dto.ServiceRequestPropertyBean;
import org.fenixedu.ulisboa.specifications.ui.adapters.ServiceRequestPropertyBeanAdapter;

import com.google.gson.GsonBuilder;

public class FenixeduUlisboaSpecificationsBaseController extends FenixEDUBaseController {

    @Override
    protected void registerTypeAdapters(GsonBuilder builder) {
        super.registerTypeAdapters(builder);
        builder.registerTypeAdapter(ServiceRequestPropertyBean.class, new ServiceRequestPropertyBeanAdapter());
    }

}
