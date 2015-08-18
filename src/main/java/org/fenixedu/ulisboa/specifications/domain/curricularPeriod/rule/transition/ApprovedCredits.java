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
        final ApprovedCredits result = new ApprovedCredits();
        result.init(configuration, credits, null /*yearMin*/, null /*yearMax*/);

        return result;
    }

    @Override
    public String getLabel() {
        return BundleUtil.getString(MODULE_BUNDLE, "label." + this.getClass().getSimpleName(), getCredits().toString());
    }

    @Override
    public RuleResult execute(Curriculum curriculum) {
        return curriculum.getSumEctsCredits().compareTo(getCredits()) >= 0 ? createTrue() : createFalseLabelled(getCredits());
    }

}
