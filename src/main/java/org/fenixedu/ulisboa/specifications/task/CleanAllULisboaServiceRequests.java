package org.fenixedu.ulisboa.specifications.task;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;

public class CleanAllULisboaServiceRequests extends CustomTask {

    @Override
    public void runTask() throws Exception {
        Bennu.getInstance().getAcademicServiceRequestsSet().stream().filter(asr -> asr instanceof ULisboaServiceRequest)
                .map(ULisboaServiceRequest.class::cast).forEach(ULisboaServiceRequest::delete);
    }

}
