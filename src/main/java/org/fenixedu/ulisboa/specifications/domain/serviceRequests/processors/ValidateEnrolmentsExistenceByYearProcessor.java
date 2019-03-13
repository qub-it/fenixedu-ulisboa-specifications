package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import pt.ist.fenixframework.Atomic;

public class ValidateEnrolmentsExistenceByYearProcessor extends ValidateEnrolmentsExistenceByYearProcessor_Base {

    protected ValidateEnrolmentsExistenceByYearProcessor() {
        super();
    }

    protected ValidateEnrolmentsExistenceByYearProcessor(final LocalizedString name, final Boolean exclusiveTransation) {
        this();
        super.init(name, exclusiveTransation);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(final LocalizedString name, final Boolean exclusiveTransation) {
        return new ValidateEnrolmentsExistenceByYearProcessor(name, exclusiveTransation);
    }

    @Override
    public void process(final ULisboaServiceRequest request, final boolean forceUpdate) {
        if (!request.hasEnrolmentsByYear()) {
            final Registration registration = request.getRegistration();
            ExecutionYear executionYear = request.hasExecutionYear() ? request.getExecutionYear() : ExecutionYear
                    .findCurrent(registration.getDegree().getCalendar());
            List<ICurriculumEntry> enrolments = registration.getStudentCurricularPlan(executionYear)
                    .getEnrolmentsByExecutionYear(executionYear).stream().filter(ULisboaConstants.isNormalEnrolment
                            .or(ULisboaConstants.isExtraCurricular).or(ULisboaConstants.isStandalone))
                    .map(ICurriculumEntry.class::cast).collect(Collectors.toList());
            if (!validate(enrolments)) {
                throw new ULisboaSpecificationsDomainException("error.serviceRequest.hasNoEnrolments.forExecutionYear",
                        executionYear.getYear());
            }
        }
    }

    private boolean validate(final List<ICurriculumEntry> enrolments) {
        return !enrolments.isEmpty();
    }

}
