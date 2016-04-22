package org.fenixedu.ulisboa.specifications.task.tmp;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.bennu.scheduler.annotation.Task;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.qubdocs.FenixEduDocumentGenerator;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestOutputType;

@Task(englishTitle = "Update output type of service request types", readOnly = false)
public class UpdateServiceRequestType extends CustomTask {

    @Override
    public void runTask() throws Exception {
        ServiceRequestOutputType outputType = ServiceRequestOutputType.readByCode(FenixEduDocumentGenerator.PDF);

        ServiceRequestType.findAll().forEach(srt -> {
            if (srt.getServiceRequestOutputType() == null) {
                srt.setServiceRequestOutputType(outputType);
            }
        });
    }

}
