package org.fenixedu.ulisboa.specifications.task.tmp;

import java.util.ArrayList;
import java.util.List;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors.FillEnrolmentsByYearPropertyProcessor;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors.FillStandAlonePropertyProcessor;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors.ULisboaServiceRequestProcessor;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

public class FixBugProcessorTypeTask extends CustomTask {

    @Override
    public void runTask() throws Exception {
        List<ServiceRequestType> types = new ArrayList<ServiceRequestType>();
        ULisboaServiceRequestProcessor p = null;
        for (ULisboaServiceRequestProcessor processor : Bennu.getInstance().getULisboaServiceRequestProcessorsSet()) {
            if (processor instanceof FillEnrolmentsByYearPropertyProcessor
                    && processor.getName().getContent().equals("Adicionar UCs Isoladas Aprovadas ao Pedido")) {
                p = processor;
                types.addAll(processor.getServiceRequestTypesSet());
            }
        }

        if (p != null) {
            p.getServiceRequestTypesSet().clear();
            p.delete();
            p = FillStandAlonePropertyProcessor.create(BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE,
                    ULisboaConstants.FILL_STANDALONE_CURRICULUM_PROPERTY_PROCESSOR));
            for (ServiceRequestType type : types) {
                p.addServiceRequestTypes(type);
            }
        }
    }

}
