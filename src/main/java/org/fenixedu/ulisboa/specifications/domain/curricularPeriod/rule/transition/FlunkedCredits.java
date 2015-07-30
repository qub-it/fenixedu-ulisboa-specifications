package org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.transition;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.CurricularPeriodConfiguration;
import org.fenixedu.ulisboa.specifications.domain.services.CurricularPeriodServices;

import pt.ist.fenixframework.Atomic;

import com.google.common.collect.Sets;

public class FlunkedCredits extends FlunkedCredits_Base {

    protected FlunkedCredits() {
        super();
    }

    @Atomic
    static public FlunkedCredits create(final CurricularPeriodConfiguration configuration, final BigDecimal credits) {
        return createForYear(configuration, credits, /* year */(Integer) null);
    }

    @Atomic
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
    protected String getLabel() {
        if (getYearMin() == null) {
            return BundleUtil.getString(MODULE_BUNDLE, "label." + this.getClass().getSimpleName() + ".total", getCredits()
                    .toString());

        } else if (isForYear()) {
            return BundleUtil.getString(MODULE_BUNDLE, "label." + this.getClass().getSimpleName() + ".year", getCredits()
                    .toString(), getYearMin().toString());

        } else {
            return BundleUtil.getString(MODULE_BUNDLE, "label." + this.getClass().getSimpleName(), getCredits().toString(),
                    getYearMin().toString(), getYearMax().toString());
        }
    }

    @Override
    public RuleResult execute(final Curriculum curriculum) {
        // TODO legidio
        // 60 - aprovados/ano curricular < X creditos

        final Set<CurricularPeriod> configured = Sets.newHashSet();
        if (getYearMin() != null) {

            final DegreeCurricularPlan dcp = getDegreeCurricularPlan();

            for (int i = getYearMin(); i <= getYearMax(); i++) {
                final CurricularPeriod curricularPeriod = CurricularPeriodServices.getCurricularPeriod(dcp, i);

                if (curricularPeriod == null) {
                    return createFalseConfiguration();
                } else {
                    configured.add(curricularPeriod);
                }
            }
        }

        final BigDecimal total = getCreditsApproved(curriculum, configured);

        return BigDecimal.valueOf(60).min(total).compareTo(getCredits()) < 0 ? createTrue() : createFalseLabelled();
    }

    static private BigDecimal getCreditsApproved(final Curriculum curriculum, final Set<CurricularPeriod> configured) {

        BigDecimal result = BigDecimal.ZERO;

        final Map<CurricularPeriod, BigDecimal> curricularPeriodCredits = CurricularPeriodServices.mapYearCredits(curriculum);
        final Set<CurricularPeriod> toInspect = configured.isEmpty() ? curricularPeriodCredits.keySet() : configured;
        
        for (final CurricularPeriod iter : toInspect) {
            final BigDecimal credits = curricularPeriodCredits.get(iter);
            if (credits != null) {
                result = result.add(credits);
            }
        }

        return result;
    }

}
