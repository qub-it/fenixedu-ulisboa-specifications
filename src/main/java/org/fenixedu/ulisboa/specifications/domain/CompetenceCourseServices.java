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

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

abstract public class CompetenceCourseServices {

    static private final Map<CompetenceCourse, Set<CurricularCourse>> CACHE_COMPETENCE_CURRICULARS = new ConcurrentHashMap<>();

    static private WeakReference<Map<String, Boolean>> CACHE_APPROVALS =
            new WeakReference<Map<String, Boolean>>(Maps.newConcurrentMap());

    static public boolean isCompetenceCourseApproved(final StudentCurricularPlan plan, final CurricularCourse course,
            final ExecutionSemester semester) {

        final Registration registration = plan.getRegistration();
        final CompetenceCourse competence = course.getCompetenceCourse();

        // optional curricular course
        if (competence == null) {
            return plan.isApproved(course, semester);
        }

        return getScpsToCheck(registration).stream().anyMatch(i -> isApproved(i, competence, semester));
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

    static private boolean isApproved(final StudentCurricularPlan plan, final CompetenceCourse competence,
            final ExecutionSemester semester) {

        Map<String, Boolean> map = CACHE_APPROVALS.get();
        if (map == null) {
            map = Maps.newConcurrentMap();
            CACHE_APPROVALS = new WeakReference<Map<String, Boolean>>(map);
        }

        final String key = plan.getExternalId() + competence.getExternalId() + (semester == null ? "" : semester.getExternalId());
        Boolean value = map.get(key);
        if (value == null) {
            value = getExpandedCurricularCourses(competence).stream()
                    .anyMatch(curricular -> plan.isApproved(curricular, semester));
            map.put(key, value);
        }

        return value;
    }

    static public Set<CurricularCourse> getExpandedCurricularCourses(final CompetenceCourse competence) {
        final Set<CurricularCourse> result;
        final String code = competence.getCode();

        if (!isExpandedCode(code)) {
            result = competence.getAssociatedCurricularCoursesSet();

        } else if (CACHE_COMPETENCE_CURRICULARS.containsKey(competence)) {

            result = CACHE_COMPETENCE_CURRICULARS.get(competence);

        } else {

            result = Sets.newHashSet();
            for (final CompetenceCourse iter : Bennu.getInstance().getCompetenceCoursesSet()) {
                if (filterCode(competence.getCode()).equals(filterCode(iter.getCode()))) {
                    result.addAll(iter.getAssociatedCurricularCoursesSet());
                }
            }

            CACHE_COMPETENCE_CURRICULARS.put(competence, result);
        }

        return result;
    }

    static private boolean isExpandedCode(final String input) {
        return !Strings.isNullOrEmpty(input) && input.endsWith("ects") && input.contains("_");
    }

    static private String filterCode(final String input) {
        return !isExpandedCode(input) ? input : input.substring(0, input.lastIndexOf("_"));
    }

}
