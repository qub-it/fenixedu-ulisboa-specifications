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
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;

abstract public class CompetenceCourseServices {

    static final private Logger logger = LoggerFactory.getLogger(CompetenceCourseServices.class);

    static final private LoadingCache<CompetenceCourse, Set<CurricularCourse>> CACHE_COMPETENCE_CURRICULARS =
            CacheBuilder.newBuilder().concurrencyLevel(8).build(new CacheLoader<CompetenceCourse, Set<CurricularCourse>>() {

                @Override
                public Set<CurricularCourse> load(final CompetenceCourse key) throws Exception {
                    logger.debug(String.format("Miss on CompetenceCourse CurricularCourses cache [%s %s]", new DateTime(), key));
                    return getExpandedCurricularCourses(key);
                }
            });

    static final private Cache<String, Boolean> CACHE_APPROVALS =
            CacheBuilder.newBuilder().concurrencyLevel(4).maximumSize(1500).expireAfterWrite(1, TimeUnit.MINUTES).build();

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

    static public int countEnrolmentsUntil(final StudentCurricularPlan plan, final CurricularCourse curricularCourse,
            final ExecutionYear executionYear) {

        final Registration registration = plan.getRegistration();
        final CompetenceCourse competence = curricularCourse.getCompetenceCourse();

        final Predicate<Enrolment> validEnrolment = (e) -> e.isActive() && e.getExecutionYear().isBeforeOrEquals(executionYear);

        // optional curricular course
        if (competence == null) {
            return (int) plan.getEnrolments(curricularCourse).stream().filter(validEnrolment).count();
        }

        final Set<CurricularCourse> expandedCourses = getExpandedCurricularCourses(competence);
        int total = 0;
        for (final StudentCurricularPlan scpToCheck : getScpsToCheck(registration)) {
            for (final CurricularCourse expandedCourse : expandedCourses) {
                total += scpToCheck.getEnrolments(expandedCourse).stream().filter(validEnrolment).count();
            }
        }

        return total;
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

        final String key = String.format("%s#%s#%s", plan.getExternalId(), competence.getExternalId(),
                (semester == null ? "null" : semester.getExternalId()));

        try {
            return CACHE_APPROVALS.get(key, new Callable<Boolean>() {

                @Override
                public Boolean call() throws Exception {
                    logger.debug(String.format("Miss on Approvals cache [%s %s]", new DateTime(), key));
                    final Set<CurricularCourse> curriculars = CACHE_COMPETENCE_CURRICULARS.get(competence);
                    return curriculars == null ? false : curriculars.stream()
                            .anyMatch(curricular -> plan.isApproved(curricular, semester));
                }
            });

        } catch (final Throwable t) {
            throw new RuntimeException(t.getCause());
        }
    }

    static private Set<CurricularCourse> getExpandedCurricularCourses(final CompetenceCourse competence) {
        final Set<CurricularCourse> result;
        final String code = competence.getCode();

        if (!isExpandedCode(code)) {
            result = competence.getAssociatedCurricularCoursesSet();

        } else {

            result = Sets.newHashSet();
            for (final CompetenceCourse iter : Bennu.getInstance().getCompetenceCoursesSet()) {
                if (filterCode(competence.getCode()).equals(filterCode(iter.getCode()))) {
                    result.addAll(iter.getAssociatedCurricularCoursesSet());
                }
            }
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
