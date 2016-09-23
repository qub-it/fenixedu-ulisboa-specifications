package org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.transition;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.StatuteType;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.CurricularPeriodConfiguration;
import org.fenixedu.ulisboa.specifications.domain.services.CurricularPeriodServices;
import org.fenixedu.ulisboa.specifications.domain.services.statute.StatuteServices;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public class FlunkedCredits extends FlunkedCredits_Base {

    static public final BigDecimal FLUNKED_CREDITS_BY_YEAR = BigDecimal.valueOf(60);

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

    public StatuteType getStatuteType() {
        return super.getStatuteTypeForRuleTransition();
    }

    @Override
    public String getLabel() {
        if (getYearMin() == null) {
            return BundleUtil.getString(MODULE_BUNDLE, "label." + this.getClass().getSimpleName() + ".total",
                    getCredits().toString());

        } else if (isForYear()) {
            return BundleUtil.getString(MODULE_BUNDLE, "label." + this.getClass().getSimpleName() + ".year",
                    getCredits().toString(), getYearMin().toString());

        } else {
            return BundleUtil.getString(MODULE_BUNDLE, "label." + this.getClass().getSimpleName(), getCredits().toString(),
                    getYearMin().toString(), getYearMax().toString());
        }
    }

    @Override
    public RuleResult execute(final Curriculum input) {
        if (getStatuteType() != null) {
            final Registration registration = input.getStudentCurricularPlan().getRegistration();
            final ExecutionYear executionYear =
                    input.getExecutionYear() == null ? ExecutionYear.readCurrentExecutionYear() : input.getExecutionYear();
            if (StatuteServices.findStatuteTypes(registration, executionYear).stream().noneMatch(i -> getStatuteType() == i)) {
                return createFalseLabelled(getMessagesSuffix(getStatuteType()));
            }
        }

        final Curriculum curriculum = prepareCurriculum(input);

        final Set<CurricularPeriod> configured = Sets.newHashSet();

        final int minYear;
        final int maxYear;

        // convert min max to curricular periods
        if (getYearMin() != null) {
            minYear = getYearMin();
            maxYear = getYearMax();
        } else {
            minYear = 1;
            maxYear = Math.max(1, getConfiguration().getCurricularPeriod().getChildOrder().intValue() - 1);
        }
        final DegreeCurricularPlan dcp = getDegreeCurricularPlan();
        for (int i = minYear; i <= maxYear; i++) {
            final CurricularPeriod curricularPeriod = CurricularPeriodServices.getCurricularPeriod(dcp, i);

            if (curricularPeriod == null) {
                // if even one is not found, return false
                return createFalseConfiguration();
            } else {
                configured.add(curricularPeriod);
            }
        }

        final BigDecimal totalFlunked = calculateTotalFlunked(curriculum, configured);

        return totalFlunked.compareTo(getCredits()) <= 0 ? createTrue() : getStatuteType() != null ? createFalseLabelled(
                getMessagesSuffix(getStatuteType())) : createFalseLabelled(totalFlunked);
    }

    private BigDecimal calculateTotalFlunked(Curriculum curriculum, Set<CurricularPeriod> configured) {

        BigDecimal result = BigDecimal.ZERO;
        final Map<CurricularPeriod, BigDecimal> curricularPeriodCredits = CurricularPeriodServices.mapYearCredits(curriculum);
        final Set<CurricularPeriod> toInspect = configured.isEmpty() ? curricularPeriodCredits.keySet() : configured;

        for (final CurricularPeriod curricularPeriod : toInspect) {
            final BigDecimal approved = curricularPeriodCredits.get(curricularPeriod) != null ? curricularPeriodCredits
                    .get(curricularPeriod) : BigDecimal.ZERO;
            final BigDecimal approvedWithLimit = getFlunkedCreditsBaseline().min(approved);
            final BigDecimal flunked = getFlunkedCreditsBaseline().subtract(approvedWithLimit);

            result = result.add(flunked);
        }

        return result;
    }

    private BigDecimal getFlunkedCreditsBaseline() {
        return FLUNKED_CREDITS_BY_YEAR.divide(BigDecimal.valueOf(getSemester() == null ? 1 : 2));
    }

    static private String getMessagesSuffix(final StatuteType input) {
        return input == null ? "" : (" " + BundleUtil.getString(MODULE_BUNDLE,
                "label." + FlunkedCredits.class.getSimpleName() + ".suffix", input.getName().getContent()));
    }

}
