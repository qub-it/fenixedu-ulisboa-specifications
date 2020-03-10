/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: luis.egidio@qub-it.com, shezad.anavarali@qub-it.com
 *
 * 
 * This file is part of FenixEdu Specifications.
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.evaluation.markSheet.CompetenceCourseMarkSheet;
import org.fenixedu.academic.domain.evaluation.season.rule.GradeScaleValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class CurriculumAggregatorMarkSheetServices {

    static final private Logger logger = LoggerFactory.getLogger(CurriculumAggregatorMarkSheetServices.class);

    private static Map<CompetenceCourseMarkSheet, Set<CurriculumAggregator>> curriculumAggregatorsMap = new HashMap<>();
    private static Map<CompetenceCourseMarkSheet, Set<CurriculumAggregatorEntry>> curriculumAggregatorEntriesMap =
            new HashMap<>();

    public static void init() {

        CompetenceCourseMarkSheet.setEnrolmentCandidateForEvaluationExtensionPredicate(
                (season, enrolment) -> CurriculumAggregatorServices.isCandidateForEvaluation(season, enrolment));

        CompetenceCourseMarkSheet.setGradeValidatorToConsiderExtensionPredicate(getAggregationGradeValidator());

        CompetenceCourseMarkSheet.setSupportsTeacherConfirmationExtensionPredicate(getAggregationSupportsTeacherConfirmation());

    }

    /**
     * Strange method, just to make sure we have consistency across the execution course's curricular courses.
     */
    private static Predicate<CompetenceCourseMarkSheet> getAggregationSupportsTeacherConfirmation() {
        return (markSheet) -> {
            Boolean result = null;

            final Set<CurriculumAggregatorEntry> entries =
                    CurriculumAggregatorMarkSheetServices.getCurriculumAggregatorEntries(markSheet);

            // let's find a candidate 
            final Boolean temp = !entries.isEmpty() ? entries.iterator().next().getSupportsTeacherConfirmation() : null;

            // let's check consistency
            if (temp != null && entries.stream().allMatch(i -> i.getSupportsTeacherConfirmation() == temp.booleanValue())) {
                result = temp;
            }

            if (result == null) {
                logger.warn("Unable to find SupportsTeacherConfirmation for {}", markSheet);
            }

            return result != null && result;
        };
    }

    private static BiPredicate<GradeScaleValidator, CompetenceCourseMarkSheet> getAggregationGradeValidator() {

        return (validator, markSheet) -> {
            //TODO: Ugly hack to check non-root
            final boolean isCurriculumAggregatorChildCandidate = markSheet.getCompetenceCourse().getEctsCredits() == 0.0;
            if (isCurriculumAggregatorChildCandidate && hasCurriculumAggregationData(markSheet)) {

                if (!validator.getAppliesToCurriculumAggregatorEntry()) {
                    return false;
                }

                if (getAggregationGradeValueScale(markSheet) == null) {
                    return false;
                }

            } else {

                if (validator.getAppliesToCurriculumAggregatorEntry()) {
                    return false;
                }
            }

            return true;
        };
    }

    /**
     * Strange method, just to make sure we have consistency across the execution course's curricular courses.
     */
    private static Integer getAggregationGradeValueScale(final CompetenceCourseMarkSheet markSheet) {
        Integer result = null;

        final Set<CurriculumAggregator> aggregators = getCurriculumAggregators(markSheet);
        final Set<CurriculumAggregatorEntry> entries = getCurriculumAggregatorEntries(markSheet);

        // let's find a candidate 
        final Integer temp = !aggregators.isEmpty() ? aggregators.iterator().next()
                .getGradeValueScale() : !entries.isEmpty() ? entries.iterator().next().getGradeValueScale() : null;

        // let's check consistency
        if (temp != null && aggregators.stream().allMatch(i -> i.getGradeValueScale() == temp)
                && entries.stream().allMatch(i -> i.getGradeValueScale() == temp.intValue())) {
            result = temp;
        }

        if (result == null) {
            logger.warn("Unable to find GradeValueScale for {}", markSheet);
        }

        return result;
    }

    private static Set<CurriculumAggregator> getCurriculumAggregators(final CompetenceCourseMarkSheet markSheet) {
        if (!hasCurriculumAggregationDataInspected(markSheet)) {
            setCurriculumAggregationData(markSheet);
        }

        return curriculumAggregatorsMap.get(markSheet);
    }

    private static Set<CurriculumAggregatorEntry> getCurriculumAggregatorEntries(final CompetenceCourseMarkSheet markSheet) {
        if (!hasCurriculumAggregationDataInspected(markSheet)) {
            setCurriculumAggregationData(markSheet);
        }

        return curriculumAggregatorEntriesMap.get(markSheet);
    }

    private static boolean hasCurriculumAggregationData(final CompetenceCourseMarkSheet markSheet) {
        return !getCurriculumAggregators(markSheet).isEmpty() || !getCurriculumAggregatorEntries(markSheet).isEmpty();
    }

    private static boolean hasCurriculumAggregationDataInspected(final CompetenceCourseMarkSheet markSheet) {
        return curriculumAggregatorsMap.containsKey(markSheet) && curriculumAggregatorEntriesMap.containsKey(markSheet);
    }

    private static void setCurriculumAggregationData(final CompetenceCourseMarkSheet markSheet) {
        final Entry<Set<CurriculumAggregator>, Set<CurriculumAggregatorEntry>> data = getCurriculumAggregationData(markSheet);

        curriculumAggregatorsMap.put(markSheet, data == null ? Sets.newHashSet() : data.getKey());
        curriculumAggregatorEntriesMap.put(markSheet, data == null ? Sets.newHashSet() : data.getValue());
    }

    private static Entry<Set<CurriculumAggregator>, Set<CurriculumAggregatorEntry>> getCurriculumAggregationData(
            final CompetenceCourseMarkSheet markSheet) {
        final Map<Set<CurriculumAggregator>, Set<CurriculumAggregatorEntry>> collected = Maps.newHashMap();

        if (CurriculumAggregatorServices.isAggregationsActive(markSheet.getExecutionYear())) {

            // try to find aggregation context for each associated curricular course
            final Set<Context> contexts = markSheet.getExecutionCourse().getAssociatedCurricularCoursesSet().stream()
//                    .filter(c -> !c.getEnrolmentsByExecutionPeriod(markSheet.getExecutionSemester()).isEmpty())
                    .map(i -> CurriculumAggregatorServices.getContext(i, markSheet.getExecutionSemester(), (CourseGroup) null))
                    .filter(i -> i != null).collect(Collectors.toSet());

            if (!contexts.isEmpty()) {
                // we don't know if we are dealing with aggregators or entries...
                // the CurricularCourses may even be configured with both status...
                // so...let's search everything.
                final Set<CurriculumAggregator> aggregators =
                        contexts.stream().map(i -> CurriculumAggregatorServices.getAggregator(i, markSheet.getExecutionYear()))
                                .filter(i -> i != null && i.isCandidateForEvaluation(markSheet.getEvaluationSeason()))
                                .collect(Collectors.toSet());
                final Set<CurriculumAggregatorEntry> entries = contexts.stream()
                        .map(i -> CurriculumAggregatorServices.getAggregatorEntry(i, markSheet.getExecutionYear()))
                        .filter(i -> i != null).collect(Collectors.toSet());

                collected.put(aggregators, entries);
            }
        }

        // using a map entry just for the pairing...
        return collected.isEmpty() ? null : collected.entrySet().iterator().next();
    }

}
