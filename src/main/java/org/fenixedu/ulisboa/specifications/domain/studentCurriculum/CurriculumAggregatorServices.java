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

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularRules.CurricularRuleType;
import org.fenixedu.academic.domain.curricularRules.DegreeModulesSelectionLimit;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.enrolment.DegreeModuleToEnrol;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

abstract public class CurriculumAggregatorServices {

    static private final Logger logger = LoggerFactory.getLogger(CurriculumAggregatorServices.class);

    static public CurriculumAggregator getRootAggregator(final Context input) {
        CurriculumAggregator result = null;

        if (input != null) {

            result = input.getCurriculumAggregator();

            if (result == null) {
                final CurriculumAggregatorEntry entry = input.getCurriculumAggregatorEntry();
                result = entry == null ? null : entry.getAggregator();
            }

            final CourseGroup parentGroup = input.getParentCourseGroup();
            if (result == null && !parentGroup.isRoot()) {
                result = getRootAggregator(getContext(parentGroup, (ExecutionYear) null));
            }
        }

        return result;
    }

    static public Context getContext(final CurriculumModule input) {
        Context result = null;

        if (input != null) {

            DegreeModule degreeModule = input.getDegreeModule();
            if (degreeModule == null) {
                // CreditsDismissal
                final CurriculumGroup curriculumGroup = input.getCurriculumGroup();
                degreeModule = curriculumGroup == null ? null : curriculumGroup.getDegreeModule();
                logger.debug("No DegreeModule found for [{}], using contexts of [{}]", input.getFullPath(),
                        degreeModule == null ? null : degreeModule.getOneFullName());
            }

            result = getContext(degreeModule, input.getIEnrolmentsLastExecutionYear());
        }

        return result;
    }

    /**
     * Passing an execution year just to try to capture less possible contexts
     */
    static private Context getContext(final DegreeModule input, final ExecutionYear executionYear) {
        Context result = null;

        if (input != null) {
            final Set<Context> parentContexts = input.getParentContextsSet().stream()
                    .filter(i -> executionYear == null || i.isValid(executionYear)).collect(Collectors.toSet());
            result = parentContexts.isEmpty() ? null : parentContexts.iterator().next();
            if (parentContexts.size() != 1 && result != null) {
                logger.debug("Not only one parent context for [{}], returning [{}]", input.getOneFullName(), result);
            }
        }

        return result;
    }

    static public Set<Context> getEnrolmentMasterContexts(final Context context) {
        final Set<Context> result = Sets.newHashSet();

        final CurriculumAggregator aggregator = getRootAggregator(context);
        if (aggregator != null) {

            result.addAll(aggregator.getEnrolmentMasterContexts());
        }

        return result;
    }

    static public Set<Context> getEnrolmentSlaveContexts(final Context context) {
        final Set<Context> result = Sets.newHashSet();

        final CurriculumAggregator aggregator = getRootAggregator(context);
        if (aggregator != null) {

            result.addAll(aggregator.getEnrolmentSlaveContexts());
        }

        return result;
    }

    static public boolean isAggregationEnroled(final Context context, final StudentCurricularPlan plan,
            final ExecutionSemester semester) {

        if (getRootAggregator(context) == null) {
            return false;
        }

        final DegreeModule module = context.getChildDegreeModule();
        return module.isLeaf() ? isAggregationEnroled((CurricularCourse) module, plan,
                semester) : isAggregationEnroled((CourseGroup) module, plan, semester);
    }

    static private boolean isAggregationEnroled(final CurricularCourse curricularCourse, final StudentCurricularPlan plan,
            final ExecutionSemester semester) {
        return plan.isApproved(curricularCourse) || plan.isEnroledInExecutionPeriod(curricularCourse, semester);
    }

    static private boolean isAggregationEnroled(final CourseGroup courseGroup, final StudentCurricularPlan plan,
            final ExecutionSemester semester) {

        if (!plan.hasDegreeModule(courseGroup)) {
            return false;
        }

        final CurriculumGroup curriculumGroup = plan.findCurriculumGroupFor(courseGroup);
        long completedModules = curriculumGroup.getChildCurriculumLines().stream().filter(c -> c.isApproved()).count()
                + curriculumGroup.getEnrolmentsBy(semester).stream().filter(e -> !e.isApproved()).count();
        for (final CurriculumGroup iter : curriculumGroup.getChildCurriculumGroups()) {
            if (isAggregationEnroled(iter.getDegreeModule(), plan, semester)) {
                completedModules++;
            }
        }

        final DegreeModulesSelectionLimit modulesLimit = (DegreeModulesSelectionLimit) courseGroup
                .getCurricularRules(CurricularRuleType.DEGREE_MODULES_SELECTION_LIMIT, semester).stream().findFirst()
                .orElse(null);

        return completedModules >= (modulesLimit == null ? courseGroup.getChildDegreeModules().size() : modulesLimit
                .getMinimumLimit().intValue());
    }

    static public Set<IDegreeModuleToEvaluate> getAggregationParticipantsToEnrol(final Context context,
            final StudentCurricularPlan plan, final ExecutionSemester semester, final Set<Context> aboutToEnrol) {

        final Set<IDegreeModuleToEvaluate> result = Sets.newHashSet();

        for (final Context iter : getEnrolmentSlaveContexts(context)) {

            // must check if was enroled in other interaction
            if (isAggregationEnroled(iter, plan, semester)) {
                continue;
            }

            if (!isCandidateForEnrolment(iter.getCurriculumAggregator(), plan, semester, aboutToEnrol)) {
                continue;
            }

            toEnrol(iter, result, plan, semester);
        }

        return result;
    }

    static public Set<CurriculumModule> getAggregationParticipantsToRemove(final Context context,
            final StudentCurricularPlan plan, final ExecutionSemester semester) {

        final Set<CurriculumModule> result = Sets.newHashSet();

        for (final Context iter : getEnrolmentSlaveContexts(context)) {

            // must check if is already approved (grade or dismissal)
            if (isApproved(iter, plan)) {
                continue;
            }

            toRemove(iter, result, plan, semester);
        }

        return result;
    }

    static private DegreeModuleToEnrol toEnrol(final Context context, final Set<IDegreeModuleToEvaluate> collection,
            final StudentCurricularPlan plan, final ExecutionSemester semester) {

        DegreeModuleToEnrol result = null;

        final CurriculumGroup curriculumGroup = findCurriculumGroupFor(context, plan);
        if (curriculumGroup != null) {

            result = new DegreeModuleToEnrol(curriculumGroup, context, semester);
            collection.add(result);
        }

        return result;
    }

    static private CurriculumModule toRemove(final Context context, final Set<CurriculumModule> collection,
            final StudentCurricularPlan plan, final ExecutionSemester semester) {

        CurriculumModule result = null;

        final CurriculumGroup curriculumGroup = findCurriculumGroupFor(context, plan);
        if (curriculumGroup != null) {

            result = curriculumGroup.getChildCurriculumModule(context.getChildDegreeModule());
            if (result == null) {
                logger.debug("Unable to find CurriculumModule for " + context.getChildDegreeModule().getOneFullName());
            } else {

                collection.add(result);
            }
        }

        return result;
    }

    static private boolean isApproved(final Context context, final StudentCurricularPlan plan) {

        boolean result = false;

        final DegreeModule degreeModule = context.getChildDegreeModule();

        if (degreeModule.isLeaf()) {
            result = plan.isApproved((CurricularCourse) degreeModule);
        } else {
            final CurriculumGroup curriculumGroup = findCurriculumGroupFor(context, plan);
            if (curriculumGroup != null) {
                result = curriculumGroup.isConcluded();
            }
        }

        return result;
    }

    static private CurriculumGroup findCurriculumGroupFor(final Context context, final StudentCurricularPlan plan) {
        final CourseGroup courseGroup = context.getParentCourseGroup();

        final CurriculumGroup result = plan.findCurriculumGroupFor(courseGroup);
        if (result == null) {
            logger.debug("Unable to find CurriculumGroup for " + context.getChildDegreeModule().getOneFullName());
        }

        return result;
    }

    static private boolean isCandidateForEnrolment(final CurriculumAggregator aggregator, final StudentCurricularPlan plan,
            final ExecutionSemester semester, final Set<Context> aboutToEnrol) {

        boolean result = true;

        if (aggregator != null && aggregator.isEnrolmentSlave()) {

            // game changer: if is a slave aggregator, must enrol only if all master are aggregation enroled or soon to be
            final Set<Context> remaining = aggregator.getEnrolmentMasterContexts().stream()
                    .filter(i -> !isAggregationEnroled(i, plan, semester)).collect(Collectors.toSet());
            remaining.removeAll(aboutToEnrol);

            result = remaining.isEmpty();
        }

        return result;
    }

    static public boolean isCandidateForEvaluation(final Enrolment enrolment) {
        boolean result = true;

        final Context context = getContext(enrolment);
        if (context != null) {

            final CurriculumAggregator aggregator = context.getCurriculumAggregator();
            if (aggregator != null && !aggregator.isCandidateForEvaluation()) {
                result = false;
            }

            final CurriculumAggregatorEntry aggregatorEntry = context.getCurriculumAggregatorEntry();
            if (aggregatorEntry != null && !aggregatorEntry.isCandidateForEvaluation()) {
                result = false;
            }
        }

        return result;
    }

}
