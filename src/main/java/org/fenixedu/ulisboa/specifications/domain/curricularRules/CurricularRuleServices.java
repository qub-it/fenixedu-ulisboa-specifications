package org.fenixedu.ulisboa.specifications.domain.curricularRules;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.curricularRules.ICurricularRule;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;

abstract public class CurricularRuleServices {

    /**
     * @see {@link org.fenixedu.academic.domain.degreeStructure.DegreeModule.getCurricularRules(CurricularRuleType,
     *      ExecutionSemester)}
     */
    static public List<? extends ICurricularRule> getCurricularRules(final DegreeModule source,
            final Class<? extends ICurricularRule> ruleClass, final ExecutionInterval executionInterval) {

        return getCurricularRules(source, (CourseGroup) null, ruleClass, executionInterval);
    }

    /**
     * @see {@link org.fenixedu.academic.domain.degreeStructure.DegreeModule.getCurricularRules(CurricularRuleType, CourseGroup,
     *      ExecutionYear)}
     */
    static public List<? extends ICurricularRule> getCurricularRules(final DegreeModule source, final CourseGroup parent,
            final Class<? extends ICurricularRule> ruleClass, final ExecutionInterval interval) {

        return source.getCurricularRulesSet().stream().filter(rule ->

        ruleClass.isAssignableFrom(rule.getClass())

                && isCurricularRuleValid(rule, interval)

                && (parent == null || rule.appliesToCourseGroup(parent))

        ).collect(Collectors.toList());
    }

    /**
     * @see {@link org.fenixedu.academic.domain.degreeStructure.DegreeModule.isCurricularRuleValid(ICurricularRule,
     *      ExecutionSemester)}
     */
    static private boolean isCurricularRuleValid(final ICurricularRule rule, final ExecutionInterval interval) {
        return interval == null || (interval instanceof ExecutionSemester ? rule.isValid((ExecutionSemester) interval) : rule
                .isValid((ExecutionYear) interval));
    }

    static protected boolean appliesToPeriod(final Context context, final CurricularPeriod period) {
        return period == null || period == context.getCurricularPeriod();
    }

}
