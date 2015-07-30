package org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.transition;

import java.math.BigDecimal;

import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.CurricularPeriodConfiguration;

import pt.ist.fenixframework.Atomic;

public class FlunkedCredits extends FlunkedCredits_Base {

    protected FlunkedCredits() {
        super();
    }

    static public FlunkedCredits create(final CurricularPeriodConfiguration configuration, final BigDecimal credits) {
        return createForYear(configuration, credits, configuration.getCurricularPeriod().getChildOrder());
    }

    static public FlunkedCredits createForYear(final CurricularPeriodConfiguration configuration, final BigDecimal credits,
            final Integer year) {
        return createForYearInterval(configuration, credits, year, year);
    }

    @Atomic
    static public FlunkedCredits createForYearInterval(final CurricularPeriodConfiguration configuration,
            final BigDecimal credits, final Integer yearMin, final Integer yearMax) {

        final FlunkedCredits result = new FlunkedCredits();
        result.init(configuration, credits, yearMin, yearMax);
        return result;
    }

    @Override
    public RuleResult execute(final Curriculum curriculum) {
        // TODO legidio
        return RuleResult.createInitialFalse();
    }

}
