package pt.ist.fenixframework.core;

import java.util.ArrayList;
import java.util.List;

import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequest;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors.ULisboaServiceRequestProcessor;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

public class UpdateULSRTask extends CustomTask {

    @Override
    public void runTask() throws Exception {
        List<ULisboaServiceRequest> requestsToCancel = new ArrayList<ULisboaServiceRequest>();
        for (AcademicServiceRequest request : Bennu.getInstance().getAcademicServiceRequestsSet()) {
            if (!(request instanceof ULisboaServiceRequest)) {
                continue;
            }
            ULisboaServiceRequest uLisboaRequest = (ULisboaServiceRequest) request;
            if (uLisboaRequest.getServiceRequestType().getCode().equals("TRANSCRIPT_OF_RECORDS")) {
                requestsToCancel.add(uLisboaRequest);
            }
        }
        for (ULisboaServiceRequest request : requestsToCancel) {
            if (request.hasStandaloneEnrolmentsByYear()) {
                ServiceRequestProperty property = request.findProperty(ULisboaConstants.STANDALONE_ENROLMENTS_BY_YEAR);
                request.removeServiceRequestProperties(property);
            }
            if (request.getRegistration() == null) {
                continue;
            }
            if (request.getRegistration().getLastStudentCurricularPlan() == null) {
                continue;
            }
            if (request.getRegistration().getLastStudentCurricularPlan().getStandaloneCurriculumGroup() == null) {
                continue;
            }
            for (ULisboaServiceRequestProcessor uLisboaServiceRequestValidator : request.getServiceRequestType()
                    .getULisboaServiceRequestProcessorsSet()) {
                uLisboaServiceRequestValidator.process(request);
            }
        }

        System.out.println(requestsToCancel.size());
    }

}
