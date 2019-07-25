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
package org.fenixedu.ulisboa.specifications.domain.studentCurriculum;

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.Enrolment.EnrolmentPredicate;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors.CurricularRuleConfigurationInitializer;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.enrolment.EnrolmentContext;
import org.fenixedu.academic.domain.enrolment.EnrolmentPredicateInitializer;
import org.fenixedu.academic.domain.evaluation.services.EnrolmentEvaluationServices;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;

abstract public class CurriculumAggregatorRulesInitializer {

    static public void init() {

        EnrolmentPredicateInitializer.addExtraImprovementPredicate(new EnrolmentPredicate() {

            @Override
            public boolean test(Enrolment enrolment) {
                if (!CurriculumAggregatorServices.isCandidateForEvaluation(getEvaluationSeason(), enrolment)) {
                    throw new ULisboaSpecificationsDomainException(
                            "error.EnrolmentEvaluation.aggregation.member.not.configured.for.evaluation",
                            enrolment.getPresentationName().getContent());
                }

                return true;
            }
        });

        EnrolmentPredicateInitializer.addExtraSpecialPredicate(new EnrolmentPredicate() {

            @Override
            public boolean test(Enrolment enrolment) {
                if (!CurriculumAggregatorServices.isCandidateForEvaluation(getEvaluationSeason(), enrolment)) {
                    throw new ULisboaSpecificationsDomainException(
                            "error.EnrolmentEvaluation.aggregation.member.not.configured.for.evaluation",
                            enrolment.getPresentationName().getContent());
                }

                return true;
            }
        });

        CurricularRuleConfigurationInitializer
                .addPreviousYearsEnrolmentCoursesSkipPredicate((cg, ctx) -> skipByCurriculumAggregatorApproval(cg, ctx));

        EnrolmentEvaluationServices
                .registerStateChangeListener(ee -> CurriculumAggregatorServices.updateAggregatorEvaluationTriggeredByEntry(ee));
    }

    /**
     * We have a 1-1 relation between CurriculumAggregator and CourseGroup. So let's take advantage of that by excluding
     * CurriculumAggregatorEntrys from PreviousYears curricular rule if the aggregation is already concluded
     */
    static private boolean skipByCurriculumAggregatorApproval(final CourseGroup courseGroup,
            final EnrolmentContext enrolmentContext) {

        final ExecutionInterval interval = enrolmentContext.getExecutionPeriod();
        final ExecutionYear year = interval.getExecutionYear();

        if (!CurriculumAggregatorServices.isAggregationsActive(year)) {
            return false;
        }

        for (final Context iter : courseGroup.getChildContexts(CurricularCourse.class)) {

            if (enrolmentContext.isToEvaluateRulesByYear() ? !iter.isValid(year) : !iter.isValid(interval)) {
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
