package org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.enrolment;

import java.math.BigDecimal;

import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.enrolment.EnrolmentContext;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.CurricularPeriodConfiguration;

import pt.ist.fenixframework.Atomic;

public class CreditsInEnrolmentPeriodForFlunkedStudent extends CreditsInEnrolmentPeriodForFlunkedStudent_Base {

    protected CreditsInEnrolmentPeriodForFlunkedStudent() {
        super();
    }

    @Atomic
    static public CreditsInEnrolmentPeriodForFlunkedStudent create(final CurricularPeriodConfiguration configuration,
            final BigDecimal credits, final Integer semester, final Integer year) {

        final CreditsInEnrolmentPeriodForFlunkedStudent result = new CreditsInEnrolmentPeriodForFlunkedStudent();
        result.init(configuration, credits, semester);
        result.setYearMin(year);
        result.setYearMax(year);

        return result;
    }

    @Override
    public RuleResult execute(EnrolmentContext enrolmentContext) {

        final Registration registration = enrolmentContext.getRegistration();
        
        if (registration.getStartExecutionYear() == enrolmentContext.getExecutionYear()) {
            return createNA();
        }

        final boolean flunked =
                registration.getCurricularYear(enrolmentContext.getExecutionYear().getPreviousExecutionYear()) == registration
                        .getCurricularYear(enrolmentContext.getExecutionYear());

        return flunked ? super.execute(enrolmentContext) : createNA();
    }

}
