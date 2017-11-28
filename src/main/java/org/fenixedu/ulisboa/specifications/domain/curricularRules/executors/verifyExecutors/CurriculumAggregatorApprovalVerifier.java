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
package org.fenixedu.ulisboa.specifications.domain.curricularRules.executors.verifyExecutors;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularRules.ICurricularRule;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.curricularRules.executors.verifyExecutors.VerifyRuleExecutor;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.enrolment.EnrolmentContext;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregator;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorServices;

public class CurriculumAggregatorApprovalVerifier extends VerifyRuleExecutor {

    //  author = "legidio", comment = "Deprecated: grades must be set before enrolment periods"
    @Deprecated
    @Override
    protected RuleResult verifyEnrolmentWithTemporaryEnrolment(ICurricularRule curricularRule, EnrolmentContext enrolmentContext,
            DegreeModule degreeModuleToVerify, CourseGroup parentCourseGroup) {
        return RuleResult.createNA(degreeModuleToVerify);
    }

    @Override
    protected RuleResult verifyEnrolmentWithRules(ICurricularRule curricularRule, EnrolmentContext enrolmentContext,
            DegreeModule degreeModuleToVerify, CourseGroup parentCourseGroup) {

        final CurriculumAggregator aggregatorConcluded =
                collectAggregatorConcluded(enrolmentContext, getContext(degreeModuleToVerify, parentCourseGroup));

        if (aggregatorConcluded != null) {
            // some Aggregator approval has freed degreeModuleToVerify from being enroled
            return RuleResult.createFalse(degreeModuleToVerify);

        } else {

            // degreeModuleToVerify must be verified by PreviousYearsEnrolmentCurricularRule
            return RuleResult.createTrue(degreeModuleToVerify);
        }
    }

    /**
     * TODO legidio, check this with nadir
     */
    static private Context getContext(final DegreeModule degreeModuleToVerify, final CourseGroup parentCourseGroup) {
        for (final Context context : degreeModuleToVerify.getParentContextsSet()) {
            if (context.getParentCourseGroup() == parentCourseGroup) {
                return context;
            }
        }

        return null;
    }

    static private CurriculumAggregator collectAggregatorConcluded(final EnrolmentContext enrolmentContext,
            final Context context) {

        final ExecutionSemester semester = enrolmentContext.getExecutionPeriod();
        final ExecutionYear year = semester.getExecutionYear();

        if (!CurriculumAggregatorServices.isAggregationsActive(year)) {
            return null;
        }

        final CurriculumAggregator aggregator = CurriculumAggregatorServices.getAggregationRoot(context, year);
        final StudentCurricularPlan plan = enrolmentContext.getStudentCurricularPlan();
        if (aggregator == null || aggregator.isAggregationConcluded(plan)) {
            return aggregator;
        }

        return collectAggregatorConcluded(enrolmentContext, aggregator.getContext());
    }

}
