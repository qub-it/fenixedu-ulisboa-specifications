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
package org.fenixedu.ulisboa.specifications.domain;

import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;

import com.google.common.collect.Sets;

abstract public class CompetenceCourseServices {

    static public boolean isCompetenceCourseApproved(final StudentCurricularPlan plan, final CurricularCourse course) {

        final Registration registration = plan.getRegistration();
        final CompetenceCourse competence = course.getCompetenceCourse();

        // optional curricular course
        if (competence == null) {
            return plan.isApproved(course);
        }

        return getScpsToCheck(registration).stream()
                .anyMatch(i -> competence.getAssociatedCurricularCoursesSet().stream().anyMatch(j -> i.isApproved(j)));
    }

    static public boolean isCompetenceCourseApproved(final StudentCurricularPlan plan, final CurricularCourse course,
            final ExecutionSemester semester) {

        final Registration registration = plan.getRegistration();
        final CompetenceCourse competence = course.getCompetenceCourse();

        // optional curricular course
        if (competence == null) {
            return plan.isApproved(course, semester);
        }

        return getScpsToCheck(registration).stream()
                .anyMatch(i -> competence.getAssociatedCurricularCoursesSet().stream().anyMatch(j -> i.isApproved(j, semester)));
    }

    static private Set<StudentCurricularPlan> getScpsToCheck(final Registration registration) {
        final Set<StudentCurricularPlan> result = Sets.newHashSet();

        if (ULisboaConfiguration.getConfiguration().getCurricularRulesApprovalsAwareOfCompetenceCourseAtStudentScope()) {
            registration.getStudent().getRegistrationsSet().stream().flatMap(r -> r.getStudentCurricularPlansSet().stream())
                    .sequential().collect(Collectors.toCollection(() -> result));

        } else {
            result.addAll(registration.getStudentCurricularPlansSet());

        }

        return result;
    }

}
