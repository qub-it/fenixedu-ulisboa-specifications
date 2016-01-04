package org.fenixedu.ulisboa.specifications.task;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors.ULisboaServiceRequestProcessor;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

public class CleanULisboaServiceRequestProcessor extends CustomTask {

    private static String processorNameKey = ""; //ULisboaConstants.PROGRAM_CONCLUSION_PROCESSOR;
    private static String replacementNameKey = ""; //ULisboaConstants.VALIDATE_PROGRAM_CONCLUSION_PROCESSOR;

    @Override
    public void runTask() throws Exception {
        taskLog("-----------------------------");
        String processorName = BundleUtil.getString(ULisboaConstants.BUNDLE, processorNameKey);
        if (ULisboaServiceRequestProcessor.findByName(processorName).count() > 0) {
            ULisboaServiceRequestProcessor processor = ULisboaServiceRequestProcessor.findByName(processorName).findFirst().get();
            taskLog("Processor '" + processorName + "' was found.");
            taskLog("Total associated types: " + processor.getServiceRequestTypesSet().size());
            for (ServiceRequestType serviceRequestType : processor.getServiceRequestTypesSet()) {
                serviceRequestType.removeULisboaServiceRequestProcessors(processor);
                String replacementName = BundleUtil.getString(ULisboaConstants.BUNDLE, replacementNameKey);
                if (ULisboaServiceRequestProcessor.findByName(replacementName).count() > 0) {
                    ULisboaServiceRequestProcessor replacement =
                            ULisboaServiceRequestProcessor.findByName(replacementName).findFirst().get();
                    serviceRequestType.addULisboaServiceRequestProcessors(replacement);
                    taskLog("Migrated processor for '" + serviceRequestType.getName().getContent() + "': " + processorName
                            + " --> " + replacementName);
                } else {
                    taskLog("Detached processor for '" + serviceRequestType.getName().getContent() + "'.");
                }
            }
            processor.delete();
            taskLog("Processor '" + processorName + "' was deleted.");
        } else {
            taskLog("No processor with designation '" + processorName + "' was found.");
        }
        taskLog("-----------------------------");
    }

}
