package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import pt.ist.fenixframework.Atomic;

public class FillExtracurricularEnrolmentsByYearPropertyProcessor extends
        FillExtracurricularEnrolmentsByYearPropertyProcessor_Base {

    protected FillExtracurricularEnrolmentsByYearPropertyProcessor() {
        super();
    }

    protected FillExtracurricularEnrolmentsByYearPropertyProcessor(final LocalizedString name) {
        this();
        super.init(name);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(LocalizedString name) {
        return new FillExtracurricularEnrolmentsByYearPropertyProcessor(name);
    }

    @Override
    public void process(ULisboaServiceRequest request) {
        if (request.isNewRequest() && !request.hasEnrolmentsByYear()) {
            ExecutionYear executionYear =
                    request.hasExecutionYear() ? request.getExecutionYear() : ExecutionYear.readCurrentExecutionYear();
            List<ICurriculumEntry> enrolments =
                    request.getRegistration().getStudentCurricularPlan(executionYear).getEnrolmentsByExecutionYear(executionYear)
                            .stream().filter(ULisboaConstants.isExtraCurricular).map(ICurriculumEntry.class::cast)
                            .collect(Collectors.toList());

            if (!validate(enrolments)) {
                throw new ULisboaSpecificationsDomainException(
                        "error.serviceRequest.hasNoExtracurricularEnrolments.forExecutionYear", executionYear.getYear());
            }

            ServiceRequestProperty property =
                    ServiceRequestProperty.createForICurriculumEntry(enrolments,
                            ServiceRequestSlot.getByCode(ULisboaConstants.ENROLMENTS_BY_YEAR));
            request.addServiceRequestProperties(property);
        }

    }

    private boolean validate(List<ICurriculumEntry> enrolments) {
        return !enrolments.isEmpty();
    }

}
