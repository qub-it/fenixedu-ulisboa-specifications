package org.fenixedu.ulisboa.specifications.domain.curricularRules.executors.ruleExecutors;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.curricularRules.ICurricularRule;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors.CurricularRuleExecutor;
import org.fenixedu.academic.domain.enrolment.EnroledCurriculumModuleWrapper;
import org.fenixedu.academic.domain.enrolment.EnrolmentContext;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;
import org.fenixedu.academic.domain.person.RoleType;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;

public class ConditionedRouteExecutor extends CurricularRuleExecutor {

    @Override
    protected RuleResult executeEnrolmentInEnrolmentEvaluation(final ICurricularRule curricularRule,
            final IDegreeModuleToEvaluate sourceDegreeModuleToEvaluate, final EnrolmentContext enrolmentContext) {
        return RuleResult.createNA(sourceDegreeModuleToEvaluate.getDegreeModule());
    }

    @Override
    protected boolean canBeEvaluated(ICurricularRule curricularRule, IDegreeModuleToEvaluate sourceDegreeModuleToEvaluate,
            EnrolmentContext enrolmentContext) {
        return true;
    }

    // author = "legidio", comment = "Deprecated: grades must be set before enrolment periods"
    @Override
    @Deprecated
    protected RuleResult executeEnrolmentWithRulesAndTemporaryEnrolment(final ICurricularRule curricularRule,
            IDegreeModuleToEvaluate sourceDegreeModuleToEvaluate, final EnrolmentContext enrolmentContext) {
        return executeEnrolmentVerificationWithRules(curricularRule, sourceDegreeModuleToEvaluate, enrolmentContext);
    }

    @Override
    protected RuleResult executeEnrolmentVerificationWithRules(ICurricularRule curricularRule,
            IDegreeModuleToEvaluate sourceDegreeModuleToEvaluate, EnrolmentContext enrolmentContext) {

        if (!canApplyRule(enrolmentContext, curricularRule)) {
            return RuleResult.createNA(sourceDegreeModuleToEvaluate.getDegreeModule());
        }

        if (isPersonAuthorized(enrolmentContext.getResponsiblePerson())) {
            return RuleResult.createTrue(sourceDegreeModuleToEvaluate.getDegreeModule());
        }

        final IDegreeModuleToEvaluate degreeModuleToEvaluate = searchDegreeModuleToEvaluate(enrolmentContext, curricularRule);

        if (degreeModuleToEvaluate.isEnroled() && degreeModuleToEvaluate instanceof EnroledCurriculumModuleWrapper) {
            // TODO legidio, expand AuditingServices
            final CurriculumModule auditable = ((EnroledCurriculumModuleWrapper) degreeModuleToEvaluate).getCurriculumModule();
            final String creatorUsername = auditable.getVersioningCreator();
            final Person creator = Person.readPersonByUsername(creatorUsername);

            if (creator != null && !isPersonAuthorized(creator)) {
                return createFalseResult(sourceDegreeModuleToEvaluate, degreeModuleToEvaluate);
            }

            return RuleResult.createTrue(sourceDegreeModuleToEvaluate.getDegreeModule());
        }

        return createFalseResult(sourceDegreeModuleToEvaluate, degreeModuleToEvaluate);
    }

    private RuleResult createFalseResult(final IDegreeModuleToEvaluate sourceDegreeModuleToEvaluate,
            final IDegreeModuleToEvaluate degreeModuleToEvaluate) {
        return RuleResult.createFalse(sourceDegreeModuleToEvaluate.getDegreeModule(),
                "curricularRules.ruleExecutors.ConditionedRouteExecutor.route.choice.must.be.performed.by.academic.office",
                degreeModuleToEvaluate.getDegreeModule().getName());
    }

    private boolean isPersonAuthorized(final Person person) {
        return person != null && (RoleType.ACADEMIC_ADMINISTRATIVE_OFFICE.isMember(person.getUser())
                || RoleType.MANAGER.isMember(person.getUser()));
    }

}
