package org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.enrolment;

import java.math.BigDecimal;

import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.enrolment.EnrolmentContext;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.CurricularPeriodConfiguration;

import pt.ist.fenixframework.Atomic;

public class CreditsInEnrolmentPeriodForPartialRegime extends CreditsInEnrolmentPeriodForPartialRegime_Base {

    protected CreditsInEnrolmentPeriodForPartialRegime() {
        super();
    }

    @Atomic
    static public CreditsInEnrolmentPeriodForPartialRegime create(final CurricularPeriodConfiguration configuration,
            final BigDecimal credits, final Integer semester, final Integer year) {

        final CreditsInEnrolmentPeriodForPartialRegime result = new CreditsInEnrolmentPeriodForPartialRegime();
        result.init(configuration, credits, semester);
        result.setYearMin(year);
        result.setYearMax(year);

        return result;
    }

    @Override
    public RuleResult execute(EnrolmentContext enrolmentContext) {
        return enrolmentContext.getRegistration()
                .isPartialRegime(enrolmentContext.getExecutionYear()) ? super.execute(enrolmentContext) : createNA();

    }

}
