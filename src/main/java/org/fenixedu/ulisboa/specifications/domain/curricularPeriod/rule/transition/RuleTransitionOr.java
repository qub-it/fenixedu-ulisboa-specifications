package org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.transition;

import java.math.BigDecimal;
import java.util.List;

import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.CurricularPeriodConfiguration;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.RuleTransition;

import pt.ist.fenixframework.Atomic;

public class RuleTransitionOr extends RuleTransitionOr_Base {

    protected RuleTransitionOr() {
        super();
    }

    protected void init(final CurricularPeriodConfiguration configuration, final List<RuleTransition> children) {
        super.init(configuration, BigDecimal.valueOf(children.size()), null, null);
        children.forEach(x -> addChildRule(x));
        checkRules();
    }

    private void checkRules() {
        if (getChildrenSet().isEmpty()) {
            throw new DomainException("error.RuleTransitionOr.at.least.one.child.is.required");
        }
    }

    @Atomic
    static public RuleTransitionOr create(final CurricularPeriodConfiguration configuration,
            final List<RuleTransition> children) {
        final RuleTransitionOr result = new RuleTransitionOr();
        result.init(configuration, children);

        return result;
    }

    @Override
    public RuleResult execute(Curriculum curriculum) {
        for (final RuleTransition child : getChildrenSet()) {
            final RuleResult childResult = child.execute(curriculum);
            if (childResult.isTrue()) {
                return childResult;
            }
        }

        //TODO: legidio
        return createFalseLabelled("");
    }

    @Override
    public String getLabel() {
        return BundleUtil.getString(MODULE_BUNDLE, "label." + this.getClass().getSimpleName());
    }

}
