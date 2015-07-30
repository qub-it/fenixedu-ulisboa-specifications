package org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.enrolment;

import java.math.BigDecimal;

import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.enrolment.EnrolmentContext;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.CurricularPeriodConfiguration;

import pt.ist.fenixframework.Atomic;

public class CreditsInCurricularPeriod extends CreditsInCurricularPeriod_Base {

    protected CreditsInCurricularPeriod() {
        super();
    }

    static public CreditsInCurricularPeriod createForSemester(final CurricularPeriodConfiguration configuration,
            final BigDecimal credits, final Integer semester, final Integer year) {

        return create(configuration, credits, semester, year, year);
    }

    static public CreditsInCurricularPeriod createForYear(final CurricularPeriodConfiguration configuration,
            final BigDecimal credits, final Integer year) {

        return createForYearInterval(configuration, credits, year, year);
    }

    static public CreditsInCurricularPeriod createForYearInterval(final CurricularPeriodConfiguration configuration,
            final BigDecimal credits, final Integer yearMin, final Integer yearMax) {

        return create(configuration, credits, (Integer) null /* semester */, yearMin, yearMax);
    }

    @Atomic
    static private CreditsInCurricularPeriod create(final CurricularPeriodConfiguration configuration, final BigDecimal credits,
            final Integer semester, final Integer yearMin, final Integer yearMax) {

        final CreditsInCurricularPeriod result = new CreditsInCurricularPeriod();
        result.init(configuration, credits, semester, yearMin, yearMax);
        return result;
    }

    private void init(final CurricularPeriodConfiguration configuration, final BigDecimal credits, final Integer semester,
            final Integer yearMin, final Integer yearMax) {

        super.init(configuration, credits, semester);
        setYearMin(yearMin);
        setYearMax(yearMax);
        checkRules();
    }

    private void checkRules() {

    }

    @Override
    public RuleResult execute(EnrolmentContext enrolmentContext) {
        // TODO legidio
        return RuleResult.createInitialFalse();
    }

}
