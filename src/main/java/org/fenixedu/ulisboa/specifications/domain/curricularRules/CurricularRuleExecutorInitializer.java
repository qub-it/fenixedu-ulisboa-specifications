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
package org.fenixedu.ulisboa.specifications.domain.curricularRules;

import java.util.function.Supplier;

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors.CurricularRuleExecutor;
import org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors.CurricularRuleExecutor.CurricularRuleApprovalExecutor;
import org.fenixedu.academic.domain.enrolment.EnrolmentContext;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.domain.CompetenceCourseServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class CurricularRuleExecutorInitializer {

    static private final Logger logger = LoggerFactory.getLogger(CurricularRuleExecutorInitializer.class);

    static public void init() {

        CurricularRuleExecutor.setCurricularRuleApprovalExecutor(CURRICULAR_RULE_APPROVAL_EXECUTOR);
        logger.info("CurricularRuleApprovalExecutor: Overriding default");
    }

    static private Supplier<CurricularRuleApprovalExecutor> CURRICULAR_RULE_APPROVAL_EXECUTOR =
            () -> new CurricularRuleApprovalExecutor() {

                @Override
                public boolean isApproved(final EnrolmentContext enrolmentContext, final CurricularCourse curricularCourse) {

                    final StudentCurricularPlan plan = enrolmentContext.getStudentCurricularPlan();
                    if (ULisboaConfiguration.getConfiguration().getCurricularRulesApprovalsAwareOfCompetenceCourse()) {

                        return CompetenceCourseServices.isCompetenceCourseApproved(plan, curricularCourse);

                    } else {
                        return plan.isApproved(curricularCourse);
                    }
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

}
