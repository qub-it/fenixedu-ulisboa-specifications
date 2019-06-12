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

import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.enrolment.EnrolmentContext;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;
import org.fenixedu.academic.domain.enrolment.StudentCurricularPlanEnrolmentManager;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;

import com.google.common.collect.Sets;

public class StudentCurricularPlanEnrolmentWithAggregatorManager extends StudentCurricularPlanEnrolmentManager {

    public StudentCurricularPlanEnrolmentWithAggregatorManager(final EnrolmentContext enrolmentContext) {
        super(enrolmentContext);
        checkCurriculumAggregatorParticipants();
    }

    private void checkCurriculumAggregatorParticipants() {
        if (CurriculumAggregatorServices.isAggregationsActive(enrolmentContext.getExecutionYear())) {

            checkToEnrol();
            checkToRemove();
        }
    }

    /**
     * @see {@link org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorListeners.checkToEnrol(CurriculumLine)}
     */
    private void checkToEnrol() {
        final Set<IDegreeModuleToEvaluate> toChange = Sets.newHashSet();

        final StudentCurricularPlan plan = enrolmentContext.getStudentCurricularPlan();
        final ExecutionSemester semester = enrolmentContext.getExecutionPeriod();
        final Set<Context> allChosen =
                enrolmentContext.getDegreeModulesToEvaluate().stream().map(i -> i.getContext()).collect(Collectors.toSet());

        for (final IDegreeModuleToEvaluate chosen : enrolmentContext.getDegreeModulesToEvaluate()) {
            for (final IDegreeModuleToEvaluate iter : CurriculumAggregatorServices
                    .getAggregationParticipantsToEnrol(chosen.getContext(), plan, semester, allChosen)) {

                toChange.add(iter);
            }
        }

        enrolmentContext.getDegreeModulesToEvaluate().addAll(toChange);
    }

    /**
     * @see {@link org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorListeners.checkToRemove(CurriculumLine)}
     */
    private void checkToRemove() {
        final Set<CurriculumModule> toChange = Sets.newHashSet();

        final StudentCurricularPlan plan = enrolmentContext.getStudentCurricularPlan();
        final ExecutionSemester semester = enrolmentContext.getExecutionPeriod();

        for (final CurriculumModule chosen : enrolmentContext.getToRemove()) {
            if (chosen instanceof CurriculumLine) {

                for (final CurriculumModule iter : CurriculumAggregatorServices.getAggregationParticipantsToRemove(
                        CurriculumAggregatorServices.getContext((CurriculumLine) chosen), plan, semester)) {

                    toChange.add(iter);
                }
            }
        }

        // enrolmentContext.getToRemove() is a list, unfortunately
        toChange.stream().filter(i -> !enrolmentContext.getToRemove().contains(i)).sequential()
                .collect(Collectors.toCollection(() -> enrolmentContext.getToRemove()));
    }

}
