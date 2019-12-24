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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.CompetenceCourseServices;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.enrolment.DegreeModuleToEnrol;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.curriculum.CurriculumLineServices;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;
import org.fenixedu.treasury.services.integration.FenixEDUTreasuryPlatformDependentServices;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;

abstract public class CurriculumAggregatorServices {

    static final private Logger logger = LoggerFactory.getLogger(CurriculumAggregatorServices.class);

    static final public Comparator<CurriculumLine> LINE_COMPARATOR =
            Comparator.comparing(CurriculumLine::getExecutionPeriod).thenComparing(CurriculumLine::getExternalId);

    static private ExecutionYear firstExecutionYear = null;

    static private ExecutionYear getCurriculumAggregatorFirstExecutionYear() {
        if (firstExecutionYear == null) {
            firstExecutionYear = ExecutionYear.readExecutionYearByName(
                    ULisboaConfiguration.getConfiguration().getCurriculumAggregatorFirstExecutionYearName());
        }

        return firstExecutionYear;
    }

    static public boolean isAggregationsActive(final ExecutionYear year) {
        return !ULisboaSpecificationsRoot.getInstance().getCurriculumAggregatorSet().isEmpty()
                && (year == null || getCurriculumAggregatorFirstExecutionYear().isBeforeOrEquals(year));
    }

    static public void updateAggregatorEvaluationTriggeredByEntry(final EnrolmentEvaluation entryEvaluation) {
//        if (FenixFramework.isDomainObjectValid(entryEvaluation)) {
        final Enrolment entryLine = entryEvaluation.getEnrolment();
        updateAggregatorEvaluationTriggeredByEntry(entryLine, entryEvaluation);
//        }
    }

    static public void updateAggregatorEvaluationTriggeredByEntry(final CurriculumLine entryLine) {
        updateAggregatorEvaluationTriggeredByEntry(entryLine, (EnrolmentEvaluation) null);
    }

    static public void updateAggregatorEvaluationTriggeredByEntry(final CurriculumLine entryLine,
            final EnrolmentEvaluation entryEvaluation) {

        if (isAggregationsActive(entryLine.getExecutionYear())) {

            // CAN NOT update evaluations on it self, so WAS explicitly searching for an entry and it's aggregator
            // BUT with different configurations per year we cannot depend on direct relation:
            // AggregatorEntry for a given CurriculumLine may not be of the same year of the Aggregator to be updated
            final CurriculumAggregator aggregator = getAggregationRoots(entryLine).stream()
                    .filter(i -> i.getCurricularCourse() != entryLine.getDegreeModule()).findFirst().orElse(null);

            if (aggregator != null) {
                aggregator.updateEvaluationTriggeredByEntry(entryLine, entryEvaluation);
            }
        }
    }

    static public CurriculumAggregator getAggregationRoot(final CurriculumLine line) {
        final Set<CurriculumAggregator> aggregationRoots = getAggregationRoots(line);
        return aggregationRoots.isEmpty() ? null : aggregationRoots.iterator().next();
    }

    static public Set<CurriculumAggregator> getAggregationRoots(final CurriculumLine line) {
        // essential to be a linked set, order matters!!
        final Set<CurriculumAggregator> result = Sets.newLinkedHashSet();

        final Context context = getContext(line);
        final ExecutionYear lineYear = line.getExecutionYear();

        if (context != null) {

            for (final CurriculumAggregator contemporaryRoot : getAggregationRoots(context, lineYear)) {

                final DegreeModule rootModule = contemporaryRoot.getCurricularCourse();

                // itself should be taken into account in the same year, so we add it immediately
                if (rootModule == line.getDegreeModule()) {
                    result.add(contemporaryRoot);

                } else {

                    final List<DegreeModule> possibleModules = context.getCurriculumAggregatorEntrySet().stream()
                            .map(i -> i.getAggregator().getCurricularCourse()).collect(Collectors.toList());

                    final List<CurriculumLine> possibleLines = line.getStudentCurricularPlan().getAllCurriculumLines().stream()
                            .filter(i -> possibleModules.contains(i.getDegreeModule())

                                    // note that we want to investigate enrolments prior or equal to the curriculum line year
                                    && i.getExecutionYear().isBeforeOrEquals(lineYear)

                            // ATTENTION: we CANNOT make sure aggregator and entries are from the same semester - because they actually can be different
                            // && contemporaryRoot.getContext().containsSemester(i.getExecutionPeriod().getSemester())

                            ).collect(Collectors.toList());

                    final CurriculumLine rootLine =
                            possibleLines.stream().max(CurriculumAggregatorServices.LINE_COMPARATOR).orElse(null);
                    final CurriculumAggregator root = getAggregator(rootLine);
                    if (root != null) {
                        result.add(root);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Tries to find an Aggregator in the following order:
     * 
     * 1) if Context has an Aggregator, return it
     * 2) if Context has an Entry, return it's Aggregator
     */
    static public CurriculumAggregator getAggregationRoot(final Context context, final ExecutionYear year) {
        final Set<CurriculumAggregator> aggregationRoots = getAggregationRoots(context, year);
        return aggregationRoots.isEmpty() ? null : aggregationRoots.iterator().next();
    }

    /**
     * Collects all Aggregators related with the input:
     * 
     * 1) if Context has an Aggregator, add it to result
     * 2) if Context has an Entry, add it's Aggregator to result
     */
    static public Set<CurriculumAggregator> getAggregationRoots(final Context context, final ExecutionYear year) {
        // essential to be a linked set, order matters!!
        final Set<CurriculumAggregator> result = Sets.newLinkedHashSet();

        if (context != null) {

            CurriculumAggregator aggregator = getAggregator(context, year);
            if (aggregator != null) {
                result.add(aggregator);
            }

            final CurriculumAggregatorEntry entry = getAggregatorEntry(context, year);
            aggregator = entry == null ? null : entry.getAggregator();
            if (aggregator != null) {
                result.add(aggregator);
            }
        }

        return result;
    }

    public static Set<CurriculumAggregator> findRootAggregators(final DegreeCurricularPlan degreeCurricularPlan,
            final ExecutionYear executionYear) {
        final Set<Context> allContexts = degreeCurricularPlan.getDcpDegreeModules(CurricularCourse.class, executionYear).stream()
                .flatMap(dm -> dm.getParentContextsByExecutionYear(executionYear).stream()).collect(Collectors.toSet());

        final Set<Context> notAggregatorEntryContexts = allContexts.stream().filter(
                c -> c.getCurriculumAggregatorEntrySet().stream().noneMatch(e -> e.getAggregator().isValid(executionYear)))
                .collect(Collectors.toSet());

        return notAggregatorEntryContexts.stream()
                .flatMap(c -> c.getCurriculumAggregatorSet().stream().filter(a -> a.isValid(executionYear)))
                .collect(Collectors.toSet());
    }

    /**
     * Find Aggregators EXACTLY with the given year
     */
    public static Set<CurriculumAggregator> findAggregators(final DegreeCurricularPlan degreeCurricularPlan,
            final ExecutionYear executionYear) {
        final Set<Context> allContexts = degreeCurricularPlan.getDcpDegreeModules(CurricularCourse.class, executionYear).stream()
                .flatMap(dm -> dm.getParentContextsByExecutionYear(executionYear).stream()).collect(Collectors.toSet());

        return allContexts.stream().flatMap(ctx -> ctx.getCurriculumAggregatorSet().stream())
                .filter(ca -> ca.getSince() == executionYear).collect(Collectors.toSet());
    }

    static public CurriculumLine getLastCurriculumLine(final Context context, final ExecutionYear year,
            final StudentCurricularPlan plan) {

        CurriculumLine result = null;

        final CurriculumAggregatorEntry entry = getAggregatorEntry(context, year);
        if (entry != null) {
            result = entry.getLastCurriculumLine(plan);
        }

        if (result == null) {
            final CurriculumAggregator aggregator = getAggregator(context, year);
            if (aggregator != null) {
                result = aggregator.getLastEnrolment(plan);
            }
        }

        return result;
    }

    static public CurriculumAggregator getAggregator(final CurriculumLine line) {
        return line == null ? null : getAggregator(getContext(line), line.getExecutionYear());
    }

    static public CurriculumAggregator getAggregator(final Context context, final ExecutionYear year) {
        CurriculumAggregator result = null;

        if (context != null && year != null) {

            result = context.getCurriculumAggregatorSet().stream().filter(i -> i.isValid(year))
                    .max(Comparator.comparing(CurriculumAggregator::getSince)).orElse(null);
        }

        return result;
    }

    static public CurriculumAggregator getAggregatorCreatedInYear(final Context context, final ExecutionYear year) {
        CurriculumAggregator result = null;

        if (context != null && year != null) {

            for (final CurriculumAggregator iter : context.getCurriculumAggregatorSet()) {
                if (iter.getSince() == year) {

                    if (result != null) {
                        throw new DomainException("error.CurriculumAggregator.duplicate");
                    }

                    result = iter;
                }
            }
        }

        return result;
    }

    static public CurriculumAggregatorEntry getAggregatorEntry(final CurriculumLine line) {
        return line == null ? null : getAggregatorEntry(getContext(line), line.getExecutionYear());
    }

    static public CurriculumAggregatorEntry getAggregatorEntry(final Context context, final ExecutionYear year) {
        CurriculumAggregatorEntry result = null;

        if (context != null && year != null) {

            result = context.getCurriculumAggregatorEntrySet().stream().filter(i -> i.isValid(year))
                    .max(Comparator.comparing(CurriculumAggregatorEntry::getSince)).orElse(null);
        }

        return result;
    }

    /**
     * Different from getAggregatorEntry because it tries to find an AggregatorEntry EXACTLY with the given year
     */
    static public CurriculumAggregatorEntry findAggregatorEntry(final Context context, final ExecutionYear year) {
        CurriculumAggregatorEntry result = null;

        if (context != null && year != null) {

            for (final CurriculumAggregatorEntry iter : context.getCurriculumAggregatorEntrySet()) {
                if (iter.getSince() == year) {

                    if (result != null) {
                        throw new DomainException("error.CurriculumAggregatorEntry.duplicate");
                    }

                    result = iter;
                }
            }
        }

        return result;
    }

    static public Context getContext(final CurriculumLine input) {
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

            // Passing an execution semester and course group just to try to capture less possible contexts
            result = getContext(degreeModule, input.getExecutionPeriod(), input.getCurriculumGroup().getDegreeModule());
        }

        return result;
    }

    static final private Cache<String, Context> CACHE_CONTEXTS =
            CacheBuilder.newBuilder().concurrencyLevel(4).maximumSize(4 * 1000).expireAfterWrite(5, TimeUnit.MINUTES).build();

    /**
     * WARNING: CurriculumAggregator specific implementation,
     * makes no sense to pull this method to a ContextService or any other Service
     * 
     * ExecutionSemester should be as close as possible to the business logic being addressed
     */
    static public Context getContext(final DegreeModule input, final ExecutionInterval interval, final CourseGroup group) {
        if (input == null) {
            return null;
        }

        final String key = String.format("%s#%s#%s", input.getExternalId(), interval == null ? "null" : interval.getExternalId(),
                group == null ? "null" : group.getExternalId());

        try {
            return CACHE_CONTEXTS.get(key, new Callable<Context>() {
                @Override
                public Context call() {
                    logger.debug(String.format("Miss on Context cache [%s %s]", new DateTime(), key));

                    Context result = null;
                    if (input != null && isAggregationsActive(null)) {

                        final Set<Context> candidates = input.getParentContextsSet().stream()
                                .filter(i -> interval == null || i.isValid(interval))
                                .filter(i -> group == null || i.getParentCourseGroup() == group).collect(Collectors.toSet());

                        if (candidates.size() == 1) {
                            result = candidates.iterator().next();

                        } else if (candidates.size() > 1) {
                            // gave up at filter, just choose the most recent one
                            // TODO: Extract logic from treasury service to some other place
                            result = candidates
                                    .stream().max(
                                            Comparator
                                                    .comparing(
                                                            Context::getBeginExecutionPeriod)
                                                    .thenComparing((c1, c2) -> FenixEDUTreasuryPlatformDependentServices
                                                            .readVersioningCreationDate(c1)
                                                            .compareTo(FenixEDUTreasuryPlatformDependentServices
                                                                    .readVersioningCreationDate(c2))))
                                    .orElse(null);

                            if (result != null) {
                                logger.debug("Not only one parent context for [{}], returning [{}-{}-{}]", input.getName(),
                                        result.getBeginExecutionPeriod().getQualifiedName(),
                                        result.getEndExecutionPeriod() == null ? "X" : result.getEndExecutionPeriod()
                                                .getQualifiedName(),
                                        result);
                            }
                        }
                    }

                    return result;
                }
            });

        } catch (final Throwable t) {
            logger.debug(String.format("Unable to get Context [%s %s %s]", new DateTime(), key, t.getLocalizedMessage()));
            return null;
        }
    }

    static public boolean hasAnyCurriculumAggregatorEntryAtAnyTimeInAnyPlan(final CurriculumLine input) {
        if (input != null && isAggregationsActive(null)) {

            // first try to inspect existing configurations
            if (input.getDegreeModule() != null && input.getDegreeModule().isLeaf()) {
                final CurricularCourse curricular = (CurricularCourse) input.getDegreeModule();

                final CompetenceCourse competence = curricular.getCompetenceCourse();
                if (competence != null) {

                    for (final CurricularCourse iter : competence.getAssociatedCurricularCoursesSet()) {
                        final Context context = getContext(iter, null, (CourseGroup) null);
                        if (context != null && !context.getCurriculumAggregatorEntrySet().isEmpty()) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    static private Set<Context> collectEnrolmentMasterContexts(final Context context, final ExecutionYear year) {
        final Set<Context> result = Sets.newLinkedHashSet();

        for (final CurriculumAggregator aggregator : getAggregationRoots(context, year)) {
            result.addAll(collectEnrolmentContexts(aggregator, year, CurriculumAggregator::getEnrolmentMasterContexts));
        }

        return result;
    }

    static private Set<Context> collectEnrolmentSlaveContexts(final Context context, final ExecutionYear year) {
        final Set<Context> result = Sets.newLinkedHashSet();

        for (final CurriculumAggregator aggregator : getAggregationRoots(context, year)) {
            result.addAll(collectEnrolmentContexts(aggregator, year, CurriculumAggregator::getEnrolmentSlaveContexts));
        }

        return result;
    }

    static private Set<Context> collectEnrolmentContexts(final CurriculumAggregator input, final ExecutionYear year,
            final Function<CurriculumAggregator, Set<Context>> function) {

        final Set<Context> result = Sets.newLinkedHashSet();

        if (input != null) {

            final Set<Context> contexts = function.apply(input);
            result.addAll(contexts);

            for (final Context context : contexts) {
                for (final CurriculumAggregator aggregator : getAggregationRoots(context, year)) {

                    if (aggregator != input) {
                        result.addAll(collectEnrolmentContexts(aggregator, year, function));
                    }
                }
            }
        }

        return result;
    }

    static public boolean isAggregationEnroled(final Context context, final StudentCurricularPlan plan,
            final ExecutionInterval interval) {

        final ExecutionYear year = interval == null ? null : interval.getExecutionYear();
        if (getAggregationRoot(context, year) == null) {
            return false;
        }

        final DegreeModule module = context.getChildDegreeModule();
        return module.isLeaf() ? isAggregationEnroled((CurricularCourse) module, plan, interval) : false;
    }

    static private boolean isAggregationEnroled(final CurricularCourse curricularCourse, final StudentCurricularPlan plan,
            final ExecutionInterval interval) {

        // WARNING! at this level this CAN NOT be competence course approval aware, since this method is used for evaluating if a given CurricularCourse is candidate for enrolment on THIS scp
        return plan.isApproved(curricularCourse) || (interval == null ? !plan.getEnrolments(curricularCourse).isEmpty() : plan
                .isEnroledInExecutionPeriod(curricularCourse, interval));
    }

    static public Set<IDegreeModuleToEvaluate> getAggregationParticipantsToEnrol(final Context context,
            final StudentCurricularPlan plan, final ExecutionInterval interval, final Set<Context> aboutToEnrol) {

        final Set<IDegreeModuleToEvaluate> result = Sets.newHashSet();

        for (final Context iter : collectEnrolmentSlaveContexts(context, interval.getExecutionYear())) {

            if (isCandidateForEnrolmentAutomatically(iter, plan, interval, aboutToEnrol)) {

                toEnrol(iter, plan, interval, aboutToEnrol, result);
            }
        }

        return result;
    }

    static public Set<CurriculumModule> getAggregationParticipantsToRemove(final Context context,
            final StudentCurricularPlan plan, final ExecutionInterval interval) {

        final Set<CurriculumModule> result = Sets.newHashSet();

        final ExecutionYear year = interval.getExecutionYear();
        for (final Context iter : collectEnrolmentSlaveContexts(context, year)) {

            // must check if is already evaluated (by grade or dismissal), since we don't want to delete those
            if (isEvaluated(iter, year, plan)) {
                continue;
            }

            toRemove(iter, result, plan, interval);
        }

        return result;
    }

    static private boolean isEvaluated(final Context context, final ExecutionYear year, final StudentCurricularPlan plan) {
        return CurriculumLineServices.isEvaluated(getLastCurriculumLine(context, year, plan));
    }

    static private DegreeModuleToEnrol toEnrol(final Context context, final StudentCurricularPlan plan,
            final ExecutionInterval interval, final Set<Context> aboutToEnrol, final Set<IDegreeModuleToEvaluate> collection) {

        DegreeModuleToEnrol result = null;

        final CurriculumGroup curriculumGroup = findCurriculumGroupFor(context, plan);
        if (curriculumGroup != null) {

            result = new DegreeModuleToEnrol(curriculumGroup, context, interval);
            collection.add(result);
            aboutToEnrol.add(context);
        }

        return result;
    }

    static private CurriculumModule toRemove(final Context context, final Set<CurriculumModule> collection,
            final StudentCurricularPlan plan, final ExecutionInterval interval) {

        CurriculumModule result = null;

        final CurriculumGroup curriculumGroup = findCurriculumGroupFor(context, plan);
        if (curriculumGroup != null) {

            result = curriculumGroup.getCurriculumModulesSet().stream()
                    .filter(i -> i.getDegreeModule() == context.getChildDegreeModule() && i.isLeaf()
                            && ((CurriculumLine) i).getExecutionPeriod() == interval)
                    .findAny().orElse(null);

            if (result == null) {
                logger.debug("Unable to find CurriculumModule for " + context.getChildDegreeModule().getOneFullName());
            } else {

                collection.add(result);
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

    static public boolean isToDisableEnrolmentOption(final Context context, final ExecutionYear year) {
        return isAggregationsActive(year)
                && getAggregationRoots(context, year).stream().anyMatch(i -> i.getEnrolmentSlaveContexts().contains(context));
    }

    static private boolean isCandidateForEnrolmentAutomatically(final Context context, final StudentCurricularPlan plan,
            final ExecutionInterval interval, final Set<Context> aboutToEnrol) {

        final ExecutionYear year = interval.getExecutionYear();

        // must check if was enroled in other interaction
        if (isAggregationEnroled(context, plan, interval)) {
            return false;
        }

        // if is a optional aggregator entry must be manually enroled
        if (isOptionalEntryRelated(context, year)) {
            return false;
        }

        // decision: if enrolment UI is by semester (even if we are in a year-enrolment-configured institution), then we'll just automatically enrol in units of the same semester 
        final Integer candidateSemester = context == null ? null : context.getCurricularPeriod().getChildOrder();
        if (candidateSemester != null && candidateSemester.intValue() != interval.getChildOrder().intValue()) {
            return false;
        }

        // if is a slave entry, must check for aggregator's approval
        // (typically this is not a problem because the aggregator is on the "first level" and is discarted by UI) 
        final CurriculumAggregatorEntry entry = context == null ? null : getAggregatorEntry(context, year);
        if (entry != null && entry.getAggregator().isEnrolmentMaster() && CompetenceCourseServices
                .isCompetenceCourseApproved(plan, (CurricularCourse) entry.getAggregator().getCurricularCourse(), null)) {
            return false;
        }

        // if is a slave aggregator, must enrol only if all master are aggregation enroled or about to be
        final CurriculumAggregator aggregator = context == null ? null : getAggregator(context, year);
        if (aggregator != null && aggregator.isEnrolmentSlave()) {

            final int optionalConcluded = aggregator.getOptionalConcluded();
            int optionalEnroled = 0;

            for (final Context iter : aggregator.getEnrolmentMasterContexts()) {
                final boolean optionalEntryRelated = isOptionalEntryRelated(iter, year);

                if (isAggregationEnroled(iter, plan, interval) || CompetenceCourseServices.isCompetenceCourseApproved(plan,
                        (CurricularCourse) iter.getChildDegreeModule(), null)) {

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

            if (optionalConcluded > 0 && optionalEnroled != optionalConcluded) {
                return false;
            }
        }

        return true;
    }

    static public boolean isCandidateForEvaluation(final EvaluationSeason season, final Enrolment enrolment) {
        if (isAggregationsActive(enrolment.getExecutionYear())) {

            final CurriculumAggregator aggregator = getAggregator(enrolment);
            if (aggregator != null && !aggregator.isCandidateForEvaluation(season)) {
                return false;
            }
        }

        return true;
    }

    static public boolean isOptionalEntryRelated(final Context context, final ExecutionYear year) {
        if (context != null) {

            CurriculumAggregatorEntry entry = getAggregatorEntry(context, year);
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

    static public Collection<CurriculumLine> getDescendentApprovedCurriculumLines(final CurriculumLine aggregatorLine) {
        return getChildApprovedCurriculumLines(aggregatorLine).stream()
                .flatMap(line -> Stream.concat(Stream.of(line), getDescendentApprovedCurriculumLines(line).stream()))
                .collect(Collectors.toSet());
    }

    static private Collection<CurriculumLine> getChildApprovedCurriculumLines(final CurriculumLine aggregatorLine) {
        final CurriculumAggregator aggregator = CurriculumAggregatorServices.getAggregator(aggregatorLine);
        return aggregator != null ? aggregator.getEntriesSet().stream()
                .map(entry -> entry.getLastCurriculumLineApproved(aggregatorLine.getStudentCurricularPlan()))
                .filter(Objects::nonNull).collect(Collectors.toSet()) : Collections.emptySet();
    }

}
