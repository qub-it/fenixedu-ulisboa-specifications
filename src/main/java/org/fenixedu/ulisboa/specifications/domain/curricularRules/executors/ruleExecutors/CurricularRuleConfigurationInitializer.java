/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 *
 * 
 * This file is part of FenixEdu fenixedu-ulisboa-specifications.
 *
 * FenixEdu Specifications is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Specifications is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Specifications.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.ulisboa.specifications.domain.curricularRules.executors.ruleExecutors;

import java.util.List;
import java.util.function.Supplier;

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularRules.ICurricularRule;
import org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors.CurricularRuleExecutor;
import org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors.CurricularRuleExecutor.CurricularRuleApprovalExecutor;
import org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors.PreviousYearsEnrolmentBySemesterExecutor;
import org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors.PreviousYearsEnrolmentByYearExecutor;
import org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors.PreviousYearsEnrolmentByYearExecutor.SkipCollectCurricularCoursesPredicate;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.enrolment.EnrolmentContext;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.domain.CompetenceCourseServices;
import org.fenixedu.ulisboa.specifications.domain.curricularRules.CreditsLimitWithPreviousApprovals;
import org.fenixedu.ulisboa.specifications.domain.curricularRules.CurricularRuleServices;
import org.fenixedu.ulisboa.specifications.domain.services.RegistrationServices;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregator;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class CurricularRuleConfigurationInitializer {

    static private final Logger logger = LoggerFactory.getLogger(CurricularRuleConfigurationInitializer.class);

    static public void init() {

        CurricularRuleExecutor.setCurricularRuleApprovalExecutor(CURRICULAR_RULE_APPROVAL_EXECUTOR);
        logger.info("CurricularRuleApprovalExecutor: Overriding default");

        PreviousYearsEnrolmentBySemesterExecutor
                .setSkipCollectCurricularCoursesPredicate(SKIP_COLLECT_CURRICULAR_COURSES_PREDICATE);
        PreviousYearsEnrolmentByYearExecutor.setSkipCollectCurricularCoursesPredicate(SKIP_COLLECT_CURRICULAR_COURSES_PREDICATE);
    }

    static private Supplier<CurricularRuleApprovalExecutor> CURRICULAR_RULE_APPROVAL_EXECUTOR =
            () -> new CurricularRuleApprovalExecutor() {

                @Override
                public boolean isApproved(final EnrolmentContext enrolmentContext, final CurricularCourse curricularCourse) {
                    return isApproved(enrolmentContext, curricularCourse, (ExecutionSemester) null);
                }

                @Override
                public boolean isApproved(final EnrolmentContext enrolmentContext, final CurricularCourse curricularCourse,
                        final ExecutionSemester executionSemester) {

                    final StudentCurricularPlan plan = enrolmentContext.getStudentCurricularPlan();

                    if (ULisboaConfiguration.getConfiguration().getCurricularRulesApprovalsAwareOfCompetenceCourse()) {

                        return CompetenceCourseServices.isCompetenceCourseApproved(plan, curricularCourse, executionSemester);

                    } else {
                        return plan.isApproved(curricularCourse, executionSemester);
                    }
                }
            };

    static private SkipCollectCurricularCoursesPredicate SKIP_COLLECT_CURRICULAR_COURSES_PREDICATE =
            (courseGroup, enrolmentContext) -> {

                return skipByCreditsLimitWithPreviousApprovals(courseGroup, enrolmentContext)

                        || skipByCurriculumAggregatorApproval(courseGroup, enrolmentContext);
            };

    static private boolean skipByCreditsLimitWithPreviousApprovals(final CourseGroup courseGroup,
            final EnrolmentContext enrolmentContext) {

        final CurriculumGroup curriculumGroup = enrolmentContext.getStudentCurricularPlan().findCurriculumGroupFor(courseGroup);

        if (curriculumGroup == null) {
            return false;
        }

        final Registration registration = enrolmentContext.getRegistration();
        if (!RegistrationServices.isCurriculumAccumulated(registration)) {
            return false;
        }

        final List<? extends ICurricularRule> rules = CurricularRuleServices.getCurricularRules(curriculumGroup.getDegreeModule(),
                CreditsLimitWithPreviousApprovals.class, enrolmentContext.getExecutionPeriod());
        if (rules.isEmpty()) {
            return false;
        }

        final double ectsFromPrevious = CurricularRuleServices.calculateEctsCreditsFromPreviousGroups(enrolmentContext,
                (CreditsLimitWithPreviousApprovals) rules.iterator().next());

        final double minEctsToApprove = curriculumGroup.getDegreeModule().getMinEctsCredits();
        final double totalEcts =
                CurricularRuleServices.calculateTotalEctsInGroup(enrolmentContext, curriculumGroup) + ectsFromPrevious;

        return totalEcts >= minEctsToApprove;
    }

    /**
     * We have a 1-1 relation between CurriculumAggregator and CourseGroup. So let's take advantage of that by excluding
     * CurriculumAggregatorEntrys from PreviousYears curricular rule if the aggregation is already concluded
     */
    static private boolean skipByCurriculumAggregatorApproval(final CourseGroup courseGroup,
            final EnrolmentContext enrolmentContext) {

        final ExecutionSemester semester = enrolmentContext.getExecutionPeriod();
        final ExecutionYear year = semester.getExecutionYear();

        if (!CurriculumAggregatorServices.isAggregationsActive(year)) {
            return false;
        }

        for (final Context iter : courseGroup.getChildContexts(CurricularCourse.class)) {

            if (enrolmentContext.isToEvaluateRulesByYear() ? !iter.isValid(year) : !iter.isValid(semester)) {
                continue;
            }

            final CurriculumAggregator aggregator = CurriculumAggregatorServices.getAggregator(iter, year);
            final StudentCurricularPlan plan = enrolmentContext.getStudentCurricularPlan();
            if (aggregator != null && aggregator.isAggregationConcluded(plan)) {
                return true;
            }
        }

        return false;
    }

}
