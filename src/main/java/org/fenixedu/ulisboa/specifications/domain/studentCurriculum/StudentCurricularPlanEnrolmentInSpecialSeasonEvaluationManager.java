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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.curricularRules.EnrolmentInSpecialSeasonEvaluation;
import org.fenixedu.academic.domain.curricularRules.ICurricularRule;
import org.fenixedu.academic.domain.enrolment.EnroledCurriculumModuleWrapper;
import org.fenixedu.academic.domain.enrolment.EnrolmentContext;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;
import org.fenixedu.academic.domain.exceptions.DomainException;

public class StudentCurricularPlanEnrolmentInSpecialSeasonEvaluationManager
        extends org.fenixedu.academic.domain.studentCurriculum.StudentCurricularPlanEnrolmentInSpecialSeasonEvaluationManager {

    public StudentCurricularPlanEnrolmentInSpecialSeasonEvaluationManager(final EnrolmentContext enrolmentContext) {
        super(enrolmentContext);
    }

    @Override
    protected void assertEnrolmentPreConditions() {
        if (isResponsiblePersonManager()) {
            return;
        }

// qubExtension, removed check - should be replaced somehow?
//        if (!hasRegistrationInValidState()) {
//            throw new DomainException("error.StudentCurricularPlan.cannot.enrol.with.registration.inactive");
//        }

        super.assertEnrolmentPreConditions();
    }

    @Override
    protected Map<IDegreeModuleToEvaluate, Set<ICurricularRule>> getRulesToEvaluate() {
        final Map<IDegreeModuleToEvaluate, Set<ICurricularRule>> result =
                new HashMap<IDegreeModuleToEvaluate, Set<ICurricularRule>>();

        for (final IDegreeModuleToEvaluate degreeModuleToEvaluate : enrolmentContext.getDegreeModulesToEvaluate()) {

            if (degreeModuleToEvaluate.isEnroled() && degreeModuleToEvaluate.canCollectRules()) {
                final EnroledCurriculumModuleWrapper moduleEnroledWrapper =
                        (EnroledCurriculumModuleWrapper) degreeModuleToEvaluate;

                if (moduleEnroledWrapper.getCurriculumModule() instanceof Enrolment) {
                    final Enrolment enrolment = (Enrolment) moduleEnroledWrapper.getCurriculumModule();

                    final Set<ICurricularRule> curricularRules = new HashSet<ICurricularRule>();
                    if (!enrolment.hasSpecialSeason()) {
                        curricularRules.add(new EnrolmentInSpecialSeasonEvaluation(enrolment, getEvaluationSeason()));
                    }

                    result.put(degreeModuleToEvaluate, curricularRules);
                } else {
                    throw new DomainException(
                            "StudentCurricularPlanEnrolmentInSpecialSeasonEvaluationManager.can.only.manage.enrolment.evaluations.of.enrolments");
                }
            }
        }

        return result;
    }

}
