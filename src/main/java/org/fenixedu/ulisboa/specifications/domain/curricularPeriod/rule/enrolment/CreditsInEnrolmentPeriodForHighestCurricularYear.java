package org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.enrolment;

import java.math.BigDecimal;

import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.enrolment.EnrolmentContext;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.CurricularPeriodConfiguration;

import pt.ist.fenixframework.Atomic;

public class CreditsInEnrolmentPeriodForHighestCurricularYear extends CreditsInEnrolmentPeriodForHighestCurricularYear_Base {

    protected CreditsInEnrolmentPeriodForHighestCurricularYear() {
        super();
    }

    @Atomic
    static public CreditsInEnrolmentPeriodForHighestCurricularYear create(final CurricularPeriodConfiguration configuration,
            final BigDecimal credits, final Integer semester, final Integer year) {

        final CreditsInEnrolmentPeriodForHighestCurricularYear result = new CreditsInEnrolmentPeriodForHighestCurricularYear();
        result.init(configuration, credits, semester);
        result.setYearMin(year);
        result.setYearMax(year);

        return result;
    }

    @Override
    public RuleResult execute(EnrolmentContext enrolmentContext) {

        if (enrolmentContext.getDegreeModulesToEvaluate().stream().anyMatch(x -> x.isLeaf() && x.getContext() != null
                && x.getContext().getCurricularYear().intValue() > getYearMin().intValue())) {
            return createNA();
        }

        return super.execute(enrolmentContext);
    }
}
