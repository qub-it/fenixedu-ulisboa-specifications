package org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.transition;

import java.math.BigDecimal;

import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.CurricularPeriodConfiguration;

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

    @Override
    public String getLabel() {
        return BundleUtil.getString(MODULE_BUNDLE, "label." + this.getClass().getSimpleName(), getCredits().toString());
    }

    @Override
    public RuleResult execute(final Curriculum input) {
        final Curriculum curriculum = prepareCurriculum(input);
        return curriculum.getSumEctsCredits().compareTo(getCredits()) >= 0 ? createTrue() : createFalseLabelled(getCredits());
    }

}
