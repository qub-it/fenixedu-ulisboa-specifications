package org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.transition;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.CurricularPeriodConfiguration;
import org.fenixedu.ulisboa.specifications.domain.services.CurricularPeriodServices;

import pt.ist.fenixframework.Atomic;

public class ApprovedCredits extends ApprovedCredits_Base {

    protected ApprovedCredits() {
        super();
    }

    @Atomic
    static public ApprovedCredits create(final CurricularPeriodConfiguration configuration, final BigDecimal credits) {
        return create(configuration, credits, false);
    }

    @Atomic
    static public ApprovedCredits createForSemester(final CurricularPeriodConfiguration configuration, final BigDecimal credits,
            final boolean allowToCollectAllCurricularPlans, final Integer semester) {

        final ApprovedCredits result = new ApprovedCredits();
        result.init(configuration, credits, null /*yearMin*/, null /*yearMax*/);
        result.setAllowToCollectAllCurricularPlans(allowToCollectAllCurricularPlans);
        result.setSemester(semester);

        return result;
    }

    @Atomic
    static public ApprovedCredits create(final CurricularPeriodConfiguration configuration, final BigDecimal credits,
            final boolean allowToCollectAllCurricularPlans) {
        final ApprovedCredits result = new ApprovedCredits();
        result.init(configuration, credits, null /*yearMin*/, null /*yearMax*/);
        result.setAllowToCollectAllCurricularPlans(allowToCollectAllCurricularPlans);

        return result;
    }

    @Atomic
    static public ApprovedCredits createForYearInterval(final CurricularPeriodConfiguration configuration,
            final BigDecimal credits, final Integer yearMin, final Integer yearMax) {

        final ApprovedCredits result = new ApprovedCredits();
        result.init(configuration, credits, yearMin, yearMax);
        return result;
    }

    @Override
    public String getLabel() {
        return BundleUtil.getString(MODULE_BUNDLE, "label." + this.getClass().getSimpleName(), getCredits().toString());
    }

    public RuleResult executeYearless(final Curriculum input) {
        final Curriculum curriculum = prepareCurriculum(input);
        return curriculum.getSumEctsCredits().compareTo(getCredits()) >= 0 ? createTrue() : createFalseLabelled(getCredits());
    }

    @Override
    public RuleResult execute(final Curriculum input) {
        if (getYearMin() == null && getYearMax() == null) {
            return executeYearless(input);
        }

        final Curriculum curriculum = prepareCurriculum(input);

        final int yearMin = getYearMin() == null ? 1 : getYearMin();
        final int yearMax =
                getYearMax() == null ? getConfiguration().getCurricularPeriod().getChildOrder().intValue() - 1 : getYearMax();

        final Set<CurricularPeriod> configured = getCurricularPeriodsConfigured(yearMin, yearMax, false);
        if (configured == null) {
            return createFalseConfiguration();
        }

        final BigDecimal total = calculateTotalApproved(curriculum, configured);

        return total.compareTo(getCredits()) >= 0 ? createTrue() : createFalseLabelled(total);
    }

    private BigDecimal calculateTotalApproved(final Curriculum curriculum, final Set<CurricularPeriod> configured) {

        BigDecimal result = BigDecimal.ZERO;
        final Map<CurricularPeriod, BigDecimal> curricularPeriodCredits = CurricularPeriodServices.mapYearCredits(curriculum);
        final Set<CurricularPeriod> toInspect = configured.isEmpty() ? curricularPeriodCredits.keySet() : configured;

        for (final CurricularPeriod curricularPeriod : toInspect) {
            final BigDecimal approved = curricularPeriodCredits.get(curricularPeriod) != null ? curricularPeriodCredits
                    .get(curricularPeriod) : BigDecimal.ZERO;
            result = result.add(approved);
        }

        return result;
    }

}
