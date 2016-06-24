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
import java.util.function.Function;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EvaluationSeason;
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

    /**
     * Tries to find a Aggregator in the following order:
     * 
     * 1) if Context has a Aggregator, return it;
     * 2) if Context has an Entry, return it's Aggregator
     */
    static public CurriculumAggregator getAggregationRoot(final Context input) {
        final Set<CurriculumAggregator> aggregationRoots = getAggregationRoots(input);
        return aggregationRoots.isEmpty() ? null : aggregationRoots.iterator().next();
    }

    /**
     * Collects all Aggregators related with the input:
     * 1) if Context has a Aggregator, return it;
     * 2) if Context has an Entry, return it's Aggregator
     */
    static public Set<CurriculumAggregator> getAggregationRoots(final Context input) {
        // essential to be a linked set, order matters!!
        final Set<CurriculumAggregator> result = Sets.newLinkedHashSet();

        if (input != null) {

            CurriculumAggregator aggregator = input.getCurriculumAggregator();
            if (aggregator != null) {
                result.add(aggregator);
            }

            final CurriculumAggregatorEntry entry = input.getCurriculumAggregatorEntry();
            aggregator = entry == null ? null : entry.getAggregator();
            if (aggregator != null) {
                result.add(aggregator);
            }
        }

        return result;
    }

    /**
     * WARNING: CurriculumAggregator specific implementation,
     * makes no sense to pull this method to a ContextService or any other Service
     */
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
    static public Context getContext(final DegreeModule input, final ExecutionYear executionYear) {
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

    static private Set<Context> collectEnrolmentMasterContexts(final Context context) {
        final Set<Context> result = Sets.newLinkedHashSet();

        for (final CurriculumAggregator aggregator : getAggregationRoots(context)) {
            result.addAll(collectEnrolmentContexts(aggregator, CurriculumAggregator::getEnrolmentMasterContexts));
        }

        return result;
    }

    static private Set<Context> collectEnrolmentSlaveContexts(final Context context) {
        final Set<Context> result = Sets.newLinkedHashSet();

        for (final CurriculumAggregator aggregator : getAggregationRoots(context)) {
            result.addAll(collectEnrolmentContexts(aggregator, CurriculumAggregator::getEnrolmentSlaveContexts));
        }

        return result;
    }

    static private Set<Context> collectEnrolmentContexts(final CurriculumAggregator input,
            final Function<CurriculumAggregator, Set<Context>> function) {

        final Set<Context> result = Sets.newLinkedHashSet();

        if (input != null) {

            final Set<Context> contexts = function.apply(input);
            result.addAll(contexts);

            for (final Context context : contexts) {
                for (final CurriculumAggregator aggregator : getAggregationRoots(context)) {

                    if (aggregator != input) {
                        result.addAll(collectEnrolmentContexts(aggregator, function));
                    }
                }
            }
        }

        return result;
    }

    static public boolean isAggregationEnroled(final Context context, final StudentCurricularPlan plan,
            final ExecutionSemester semester) {

        if (getAggregationRoot(context) == null) {
            return false;
        }

        final DegreeModule module = context.getChildDegreeModule();
        return module.isLeaf() ? isAggregationEnroled((CurricularCourse) module, plan,
                semester) : isAggregationEnroled((CourseGroup) module, plan, semester);
    }

    static private boolean isAggregationEnroled(final CurricularCourse curricularCourse, final StudentCurricularPlan plan,
            final ExecutionSemester semester) {

        // WARNING! at this leve this CAN NOT be competence course approval aware, since this method is used for evaluating if a given CurricularCourse is candidate for enrolment on THIS scp
        return plan.isApproved(curricularCourse) || (semester == null ? !plan.getEnrolments(curricularCourse).isEmpty() : plan
                .isEnroledInExecutionPeriod(curricularCourse, semester));
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

        for (final Context iter : collectEnrolmentSlaveContexts(context)) {

            if (isCandidateForEnrolmentAutomatically(iter, plan, semester, aboutToEnrol)) {

                toEnrol(iter, plan, semester, aboutToEnrol, result);
            }
        }

        return result;
    }

    static public Set<CurriculumModule> getAggregationParticipantsToRemove(final Context context,
            final StudentCurricularPlan plan, final ExecutionSemester semester) {

        final Set<CurriculumModule> result = Sets.newHashSet();

        for (final Context iter : collectEnrolmentSlaveContexts(context)) {

            // must check if is already approved (grade or dismissal)
            if (isApproved(iter, plan)) {
                continue;
            }

            toRemove(iter, result, plan, semester);
        }

        return result;
    }

    static private DegreeModuleToEnrol toEnrol(final Context context, final StudentCurricularPlan plan,
            final ExecutionSemester semester, final Set<Context> aboutToEnrol, final Set<IDegreeModuleToEvaluate> collection) {

        DegreeModuleToEnrol result = null;

        final CurriculumGroup curriculumGroup = findCurriculumGroupFor(context, plan);
        if (curriculumGroup != null) {

            result = new DegreeModuleToEnrol(curriculumGroup, context, semester);
            collection.add(result);
            aboutToEnrol.add(context);
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

    static public boolean isToDisableEnrolmentOption(final Context context) {
        return getAggregationRoots(context).stream().anyMatch(i -> i.getEnrolmentSlaveContexts().contains(context));
    }

    static private boolean isCandidateForEnrolmentAutomatically(final Context context, final StudentCurricularPlan plan,
            final ExecutionSemester semester, final Set<Context> aboutToEnrol) {

        // must check if was enroled in other interaction
        if (isAggregationEnroled(context, plan, semester)) {
            return false;
        }

        // if is a optional aggregator entry must be manually enroled
        if (isOptionalEntryRelated(context)) {
            return false;
        }

        // if is a slave aggregator, must enrol only if all master are aggregation enroled or about to be
        final CurriculumAggregator aggregator = context == null ? null : context.getCurriculumAggregator();
        if (aggregator != null && aggregator.isEnrolmentSlave()) {

            final int optionalConcluded = aggregator.getOptionalConcluded();
            int optionalEnroled = 0;

            for (final Context iter : aggregator.getEnrolmentMasterContexts()) {
                final boolean optionalEntryRelated = isOptionalEntryRelated(iter);

                if (isAggregationEnroled(iter, plan, semester)) {

                    if (optionalEntryRelated) {
                        optionalEnroled++;
                    }

                } else {

                    if (aboutToEnrol.contains(iter)) {
                        if (optionalEntryRelated) {
                            optionalEnroled++;
                        }

                        continue;
                    }

                    if (!optionalEntryRelated) {
                        return false;
                    }
                }
            }

            if (optionalConcluded > 0 && optionalEnroled < optionalConcluded) {
                return false;
            }
        }

        return true;
    }

    static public boolean isCandidateForEvaluation(final EvaluationSeason season, final Enrolment enrolment) {
        boolean result = true;

        final Context context = getContext(enrolment);
        if (context != null) {

            final CurriculumAggregator aggregator = context.getCurriculumAggregator();
            if (aggregator != null && !aggregator.isCandidateForEvaluation(season)) {
                result = false;
            }

            final CurriculumAggregatorEntry entry = context.getCurriculumAggregatorEntry();
            if (entry != null && !entry.isCandidateForEvaluation()) {
                result = false;
            }
        }

        return result;
    }

    static public boolean isOptionalEntryRelated(final Context context) {
        if (context != null) {
            CurriculumAggregatorEntry entry = context.getCurriculumAggregatorEntry();
            if (entry != null) {
                if (entry.getOptional()) {
                    return true;
                }

                // TODO legidio, check if this recursive investigation is needed in some other way
                // for now, we'll keep a more conservative approach
                // return isOptionalEntryRelated(entry.getAggregator().getContext());
            }
        }

        return false;
    }

}
