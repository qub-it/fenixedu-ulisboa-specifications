package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import pt.ist.fenixframework.Atomic;

public class FillEnrolmentsByYearPropertyProcessor extends FillEnrolmentsByYearPropertyProcessor_Base {

    protected FillEnrolmentsByYearPropertyProcessor() {
        super();
    }

    protected FillEnrolmentsByYearPropertyProcessor(final LocalizedString name) {
        this();
        super.init(name);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(LocalizedString name) {
        return new FillEnrolmentsByYearPropertyProcessor(name);
    }

    @Override
    public void process(ULisboaServiceRequest request) {
        if (!request.hasEnrolmentsByYear()) {
            ExecutionYear executionYear =
                    request.hasExecutionYear() ? request.getExecutionYear() : ExecutionYear.readCurrentExecutionYear();
            List<ICurriculumEntry> enrolments = request.getRegistration().getStudentCurricularPlan(executionYear)
                    .getEnrolmentsByExecutionYear(executionYear).stream().filter(ULisboaConstants.isNormalEnrolment)
                    .map(ICurriculumEntry.class::cast).collect(Collectors.toList());
            if (validate(enrolments)) {
                //asd
//                throw new ULisboaSpecificationsDomainException("error.serviceRequest.hasNoEnrolments.forExecutionYear",
//                        executionYear.getYear());
                ServiceRequestProperty property = ServiceRequestProperty
                        .create(ServiceRequestSlot.getByCode(ULisboaConstants.ENROLMENTS_BY_YEAR), enrolments);
                request.addServiceRequestProperties(property);
            }
        }
    }

    private boolean validate(List<ICurriculumEntry> enrolments) {
        return !enrolments.isEmpty();
    }

}
