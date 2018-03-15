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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.EvaluationConfiguration;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.evaluation.markSheet.CompetenceCourseMarkSheet;
import org.fenixedu.academic.domain.evaluation.markSheet.CompetenceCourseMarkSheetChangeRequest;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.Dismissal;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.academic.util.EnrolmentEvaluationState;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.domain.CompetenceCourseServices;
import org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot;
import org.fenixedu.ulisboa.specifications.domain.services.PersonServices;
import org.fenixedu.ulisboa.specifications.domain.services.enrollment.EnrolmentServices;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.YearMonthDay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public class CurriculumAggregator extends CurriculumAggregator_Base {

    /**
     * Attention: Enrolments are dealt with explicitly upon their grade change, since (un)enrol doesn't change aggregator grade.
     * See other usages of CurriculumAggregatorServices.updateAggregatorEvaluationTriggeredByEntry(CurriculumLine)
     */
    static {
        Dismissal.getRelationDegreeModuleCurriculumModule().addListener(CurriculumAggregatorListeners.ON_CREATION);
        Dismissal.getRelationCreditsDismissalEquivalence().addListener(CurriculumAggregatorListeners.ON_DELETION);
    }

    static private final Logger logger = LoggerFactory.getLogger(CurriculumAggregator.class);

    protected CurriculumAggregator() {
        super();
        setRoot(ULisboaSpecificationsRoot.getInstance());
    }

    @Atomic
    static public CurriculumAggregator create(final Context context, final ExecutionYear since, final LocalizedString description,
            final AggregationEnrolmentType enrolmentType, final AggregationMemberEvaluationType evaluationType,
            final EvaluationSeason evaluationSeason, final AggregationGradeCalculator gradeCalculator, final int gradeValueScale,
            final int optionalConcluded) {

        final CurriculumAggregator result = new CurriculumAggregator();
        result.setContext(context);
        result.setSince(since);
        result.init(description, enrolmentType, evaluationType, evaluationSeason, gradeCalculator, gradeValueScale,
                optionalConcluded);

        return result;
    }

    @Atomic
    public CurriculumAggregator edit(final LocalizedString description, final AggregationEnrolmentType enrolmentType,
            final AggregationMemberEvaluationType evaluationType, final EvaluationSeason evaluationSeason,
            final AggregationGradeCalculator gradeCalculator, final int gradeValueScale, final int optionalConcluded) {

        init(description, enrolmentType, evaluationType, evaluationSeason, gradeCalculator, gradeValueScale, optionalConcluded);

        return this;
    }

    @Atomic
    public CurriculumAggregator duplicate(final ExecutionYear targetYear) {

        final CurriculumAggregator result = create(getContext(), targetYear, getDescription(), getEnrolmentType(),
                getEvaluationType(), getEvaluationSeason(), getGradeCalculator(), getGradeValueScale(), getOptionalConcluded());

        for (final CurriculumAggregatorEntry entry : getEntriesSet()) {
            if (entry.getContext().isValid(getSince())) {
                CurriculumAggregatorEntry.create(result, entry.getContext(), entry.getSupportsTeacherConfirmation(),
                        entry.getGradeFactor(), entry.getGradeValueScale(), entry.getOptional());
            }
        }

        return result;
    }

    private void init(final LocalizedString description, final AggregationEnrolmentType enrolmentType,
            final AggregationMemberEvaluationType evaluationType, final EvaluationSeason evaluationSeason,
            final AggregationGradeCalculator gradeCalculator, final int gradeValueScale, final int optionalConcluded) {

        super.setDescription(description);
        super.setEnrolmentType(enrolmentType);
        super.setEvaluationType(evaluationType);
        super.setEvaluationSeason(evaluationSeason);
        super.setGradeCalculator(gradeCalculator);
        super.setGradeValueScale(gradeValueScale);
        super.setOptionalConcluded(optionalConcluded);

        checkRules();
    }

    private void checkRules() {
        if (getContext() == null) {
            throw new DomainException("error.CurriculumAggregator.required.Context");
        }

        if (getSince() == null) {
            throw new DomainException("error.CurriculumAggregator.required.Since");
        }

        final CurriculumAggregator found = CurriculumAggregatorServices.findAggregator(getContext(), getSince());
        if (found != null && found != this) {
            throw new DomainException("error.CurriculumAggregator.duplicate");
        }

        if (!getContext().isValid(getSince())) {
            throw new DomainException("error.CurriculumAggregator.invalid.Context");
        }

        if (getDescription() == null || getDescription().isEmpty()) {
            throw new DomainException("error.CurriculumAggregator.required.Description");
        }

        if (getEnrolmentType() == null) {
            throw new DomainException("error.CurriculumAggregator.required.EnrolmentType");
        }

        if (getEvaluationType() == null) {
            throw new DomainException("error.CurriculumAggregator.required.EvaluationType");
        }

        if (getEvaluationSeason() == null) {
            throw new DomainException("error.CurriculumAggregator.required.EvaluationSeason");
        }

        if (getGradeCalculator() == null) {
            throw new DomainException("error.CurriculumAggregator.required.GradeCalculator");
        }

        if (getGradeValueScale() < 0) {
            throw new DomainException("error.CurriculumAggregator.required.GradeValueScale");
        }

        if (getOptionalConcluded() < 0) {
            throw new DomainException("error.CurriculumAggregator.required.OptionalConcluded");
        }
    }

    @Atomic
    public void delete() {
        if (!getEntriesSet().isEmpty()) {
            throw new DomainException("error.CurriculumAggregator.delete.entries");
        }

        super.setContext(null);
        super.setSince(null);
        super.setEvaluationSeason(null);

        super.setRoot(null);
        super.deleteDomainObject();
    }

    @Atomic
    public CurriculumAggregatorEntry createEntry(final Context context, final boolean supportsTeacherConfirmation,
            final BigDecimal gradeFactor, final int gradeValueScale, final boolean optional) {

        final CurriculumAggregatorEntry result = CurriculumAggregatorEntry.create(this, context, supportsTeacherConfirmation,
                gradeFactor, gradeValueScale, optional);

        checkRules();
        return result;
    }

    @Override
    public LocalizedString getDescription() {
        if (super.getDescription() != null && !super.getDescription().isEmpty()) {
            return super.getDescription();
        }

        return getDescriptionDefault();
    }

    public String getDescriptionFull() {
        final String description = getDescription().getContent();
        final String since = getSince().getQualifiedName();

        final GradeScale gradeScale = getGradeScale();
        String gradeScaleDescription = "";
        if (gradeScale != GradeScale.TYPE20) {
            gradeScaleDescription = ", " + gradeScale.getDescription().replace(GradeScale.TYPE20.getDescription(), "");
        }

        String result = String.format("%s [%s %s%s]", description, BundleUtil.getString(Bundle.APPLICATION, "label.since"), since,
                gradeScaleDescription);

        final int optionalConcluded = getOptionalConcluded();
        if (optionalConcluded != 0) {
            result += " [" + optionalConcluded + " Op]";
        }

        return result;
    }

    private LocalizedString getDescriptionDefault() {
        return getEnrolmentType().getAggregatorDefaultDescription();
    }

    public boolean isLegacy() {
        return getDescription().getContent().equals("Legacy");
    }

    public DegreeCurricularPlan getDegreeCurricularPlan() {
        return getContext().getParentCourseGroup().getParentDegreeCurricularPlan();
    }

    public boolean isValid(final ExecutionYear year) {
        if (year != null) {

            if (getSince().isBeforeOrEquals(year)) {

                final CurriculumAggregator nextConfig = getNextConfig();
                if (nextConfig == null || !nextConfig.isValid(year)) {
                    return true;
                }
            }
        }

        return false;
    }

    public CurriculumAggregator getPreviousConfig() {
        return getContext().getCurriculumAggregatorSet().stream().filter(i -> i.getSince().isBefore(getSince()))
                .max(Comparator.comparing(CurriculumAggregator::getSince)).orElse(null);
    }

    public CurriculumAggregator getNextConfig() {
        return getContext().getCurriculumAggregatorSet().stream().filter(i -> i.getSince().isAfter(getSince()))
                .min(Comparator.comparing(CurriculumAggregator::getSince)).orElse(null);
    }

    public CurricularCourse getCurricularCourse() {
        return (CurricularCourse) getContext().getChildDegreeModule();
    }

    public GradeScale getGradeScale() {
        final GradeScale competenceScale = getCurricularCourse().getCompetenceCourse().getGradeScale();
        return competenceScale != null ? competenceScale : getCurricularCourse().getGradeScaleChain();
    }

    public boolean isWithMarkSheet() {
        return getEvaluationType() == AggregationMemberEvaluationType.WITH_MARK_SHEET;
    }

    public boolean isWithLooseEvaluation() {
        return isWithoutMarkSheet();
    }

    private boolean isWithoutMarkSheet() {
        return getEvaluationType() == AggregationMemberEvaluationType.WITHOUT_MARK_SHEET;
    }

    public boolean isCandidateForEvaluation(final EvaluationSeason season) {
        return getEvaluationType().isCandidateForEvaluation() && (getEvaluationSeason() == season || season.isImprovement());
    }

    public boolean isEnrolmentMaster() {
        return getEnrolmentType().isEnrolmentMaster();
    }

    public boolean isEnrolmentSlave() {
        return getEnrolmentType() == AggregationEnrolmentType.ONLY_AGGREGATOR_ENTRIES;
    }

    protected void updateEvaluationTriggeredByEntry(final CurriculumLine entryLine, final EnrolmentEvaluation entryEvaluation) {
        final Enrolment enrolment = getLastEnrolment(entryLine.getStudentCurricularPlan());
        if (enrolment == null) {
            return;
        }

        updateEvaluation(getEnrolmentEvaluation(enrolment, entryEvaluation));
    }

    /**
     * Method to force update of an aggregator enrolment
     */
    public void updateEvaluation(final Enrolment enrolment) {
        if (enrolment == null) {
            return;
        }

        if (enrolment.getCurricularCourse() != getCurricularCourse()) {
            return;
        }

        if (!isValid(enrolment.getExecutionYear())) {
            return;
        }

        final StudentCurricularPlan plan = enrolment.getStudentCurricularPlan();
        if (enrolment != getLastEnrolment(plan)) {
            return;
        }

        updateEvaluation(getEnrolmentEvaluation(enrolment, (EnrolmentEvaluation) null));
    }

    private void updateEvaluation(final EnrolmentEvaluation evaluation) {
        if (evaluation == null) {
            return;
        }

        if (isWithMarkSheet()) {
            updateMarkSheets(evaluation);

        } else if (isWithLooseEvaluation()) {
            updateLooseEvaluation(evaluation);
        }
    }

    private EnrolmentEvaluation getEnrolmentEvaluation(final Enrolment enrolment, final EnrolmentEvaluation entryEvaluation) {
        EnrolmentEvaluation result = null;

        final EvaluationSeason season = getEvaluationSeason(enrolment, entryEvaluation);

        final Set<EnrolmentEvaluation> evaluations = enrolment.getEvaluationsSet();
        final Set<EnrolmentEvaluation> evaluationsOfSeason =
                evaluations.stream().filter(i -> i.getEvaluationSeason() == season).collect(Collectors.toSet());

        if (evaluationsOfSeason.size() == 1) {
            result = evaluationsOfSeason.iterator().next();

        } else if (evaluationsOfSeason.size() > 1) {
            throw new DomainException("error.CurriculumAggregator.unexpected.number.of.EnrolmentEvaluations");

        } else if (evaluationsOfSeason.isEmpty()) {

            if (isWithLooseEvaluation()) {

                // try to use default season evaluation
                if (evaluations.size() == 1) {
                    final EnrolmentEvaluation candidate = evaluations.iterator().next();
                    if (!candidate.isFinal() && candidate.getCompetenceCourseMarkSheet() == null && candidate
                            .getEvaluationSeason() == EvaluationConfiguration.getInstance().getDefaultEvaluationSeason()) {
                        result = candidate;
                        result.setEvaluationSeason(season);
                    }
                }
            }

            if (result == null && (isWithLooseEvaluation()

                    // with mark sheet must not create evaluation seasons unless we're dealing with an improvement of different year
                    || season != getEvaluationSeason())) {

                result = new EnrolmentEvaluation(enrolment, season);
            }

            if (result != null && season.isImprovement()) {
                result.setExecutionPeriod(entryEvaluation.getExecutionPeriod());
            }
        }

        return result;
    }

    private EvaluationSeason getEvaluationSeason(final Enrolment enrolment, final EnrolmentEvaluation entryEvaluation) {
        EvaluationSeason result = null;

        final EvaluationSeason entrySeason = entryEvaluation == null ? null : entryEvaluation.getEvaluationSeason();
        if (entrySeason != null && entrySeason.isImprovement()
                && enrolment.getExecutionYear() != entryEvaluation.getExecutionPeriod().getExecutionYear()) {

            // must prepare enrolment evaluation in aggregator enrolment if entry enrolment had an improvement in a different year
            result = EvaluationSeason.readSpecialAuthorizations().filter(i -> i.isImprovement()).findAny().orElse(null);
        }

        if (result == null) {
            result = getEvaluationSeason();
        }

        return result;
    }

    private void updateLooseEvaluation(final EnrolmentEvaluation evaluation) {
        if (!isWithLooseEvaluation()) {
            return;
        }

        final Enrolment enrolment = evaluation.getEnrolment();
        final StudentCurricularPlan plan = enrolment.getStudentCurricularPlan();

        final Grade conclusionGrade = calculateConclusionGrade(plan);
        final Date conclusionDate = calculateConclusionDate(plan);

        if (conclusionGrade.isEmpty()) {

            // might have been with mark sheet in previous configurations
            if (evaluation.getCompetenceCourseMarkSheet() == null) {
                logger.debug("Aggregator is not concluded, will remove existing evaluation in {} at {}",
                        getCurricularCourse().getOneFullName(), enrolment.getExecutionPeriod().getQualifiedName());

                evaluation.setEnrolmentEvaluationState(EnrolmentEvaluationState.TEMPORARY_OBJ);
                evaluation.delete();
            }

        } else if (!isGradeEquals(evaluation.getGrade(), conclusionGrade)) {
            evaluation.setEnrolmentEvaluationState(EnrolmentEvaluationState.TEMPORARY_OBJ);
            final Date availableDate = new Date();
            evaluation.edit(Authenticate.getUser().getPerson(), conclusionGrade, availableDate, conclusionDate);
            evaluation.confirmSubmission(Authenticate.getUser().getPerson(), getDescriptionDefault().getContent());
        }

        EnrolmentServices.updateState(enrolment);
    }

    static private boolean isGradeEquals(final Grade before, final Grade after) {

        // grade scale factor might have changed through the years
        if (before.isNumeric() && after.isNumeric()) {
            final RoundingMode round = RoundingMode.HALF_UP;
            return before.getNumericValue().setScale(0, round).equals(after.getNumericValue().setScale(0, round));
        }

        // manual loose evaluation or mark sheet non-approval might have occoured
        if (!before.isEmpty() && !before.isApproved() && !after.isApproved()) {
            return true;
        }

        return before.equals(after);
    }

    private void updateMarkSheets(final EnrolmentEvaluation evaluation) {
        if (!isWithMarkSheet()) {
            return;
        }

        final Enrolment enrolment = evaluation.getEnrolment();
        final StudentCurricularPlan plan = enrolment.getStudentCurricularPlan();

        final CompetenceCourseMarkSheet markSheet = evaluation.getCompetenceCourseMarkSheet();
        if (markSheet == null) {
            return;
        }

        final Grade conclusionGrade = calculateConclusionGrade(plan);
        updateMarkSheet(markSheet, enrolment, conclusionGrade);
    }

    private void updateMarkSheet(final CompetenceCourseMarkSheet markSheet, final Enrolment enrolment,
            final Grade conclusionGrade) {

        if (markSheet != null && !markSheet.isEdition() && markSheet.getLastPendingChangeRequest() == null) {
            final Registration registration = enrolment.getRegistration();

            final String reason =
                    ULisboaSpecificationsUtil.bundle("CurriculumAggregator.CompetenceCourseMarkSheetChangeRequest.reason",
                            registration.getNumber().toString(), PersonServices.getDisplayName(registration.getPerson()),
                            getDescriptionDefault().getContent(), conclusionGrade.getValue());

            CompetenceCourseMarkSheetChangeRequest.create(markSheet, Authenticate.getUser().getPerson(), reason);
        }
    }

    public Enrolment getLastEnrolment(final StudentCurricularPlan plan) {
        Enrolment result = null;

        if (plan != null) {

            final ExecutionSemester entriesLastSemester = getEntriesLastExecutionSemester(plan);

            result = getContext().getChildDegreeModule().getCurriculumModulesSet().stream().filter(i -> {

                final Enrolment enrolment = i instanceof Enrolment ? (Enrolment) i : null;
                return enrolment != null && enrolment.getStudentCurricularPlan() == plan

                // note that enrolments prior to aggregator's Since may belong to another configuration
                        && enrolment.getExecutionYear().isAfterOrEquals(getSince())

                        && getContext().isValid(enrolment.getExecutionPeriod())

                // if entriesLastSemester is not null, may be redudant
                        && (entriesLastSemester == null || enrolment.isValid(entriesLastSemester));

            }).map(Enrolment.class::cast).max(CurriculumAggregatorServices.LINE_COMPARATOR).orElse(null);

            if (result == null) {
                logger.debug("No aggregator enrolment found, no grade to update in {} at {}",
                        getCurricularCourse().getOneFullName(),
                        entriesLastSemester == null ? "" : entriesLastSemester.getQualifiedName());
            }
        }

        return result;
    }

    private ExecutionSemester getEntriesLastExecutionSemester(final StudentCurricularPlan plan) {
        ExecutionSemester result = null;

        if (plan != null) {

            final SortedSet<CurriculumLine> lines = Sets.newTreeSet(CurriculumAggregatorServices.LINE_COMPARATOR);
            for (final CurriculumAggregatorEntry entry : getEntriesSet()) {
                final CurriculumLine line = entry.getLastCurriculumLine(plan, false);
                if (line != null) {
                    lines.add(line);
                }
            }

            if (!lines.isEmpty()) {
                result = lines.last().getExecutionPeriod();
            }
        }

        return result;
    }

    public boolean isAggregationEvaluated(final StudentCurricularPlan plan) {
        int optionalEvaluatedEntries = 0;

        for (final CurriculumAggregatorEntry iter : getEntriesSet()) {

            if (!iter.isAggregationEvaluated(plan)) {
                if (!iter.getOptional()) {
                    return false;
                }

            } else if (iter.getOptional()) {
                optionalEvaluatedEntries = optionalEvaluatedEntries + 1;
            }
        }

        if (optionalEvaluatedEntries < getOptionalConcluded()) {
            return false;
        }

        return true;
    }

    /**
     * Note that this behaviour is independent from this aggregator being master or slave in the enrolment process
     */
    public boolean isAggregationConcluded(final StudentCurricularPlan plan) {

        if (ULisboaConfiguration.getConfiguration().getCurricularRulesApprovalsAwareOfCompetenceCourse()) {
            // approval may be in previous plan
            if (CompetenceCourseServices.isCompetenceCourseApproved(plan, getCurricularCourse(), (ExecutionSemester) null)) {
                return true;
            }

        } else if (plan.isConcluded(getCurricularCourse(), (ExecutionYear) null)) {
            return true;
        }

        // may be concluded by approval of another aggregator on which aggregation it participates
        final CurriculumAggregatorEntry entry = CurriculumAggregatorServices.getAggregatorEntry(getContext(), getSince());
        if (entry != null && entry.getAggregator().isAggregationConcluded(plan)) {
            return true;
        }

        int optionalConcludedEntries = 0;

        for (final CurriculumAggregatorEntry iter : getEntriesSet()) {

            if (!iter.isAggregationConcluded(plan)) {
                if (!iter.getOptional()) {
                    return false;
                }

            } else if (iter.getOptional()) {
                optionalConcludedEntries = optionalConcludedEntries + 1;
            }
        }

        if (optionalConcludedEntries < getOptionalConcluded()) {
            return false;
        }

        return true;
    }

    /**
     * Note that this behaviour is independent from this aggregator being master or slave in the enrolment process
     */
    public Grade calculateConclusionGrade(final StudentCurricularPlan plan) {
        if (getEntriesSet().isEmpty()) {
            return Grade.createEmptyGrade();
        }

        if (!isAggregationEvaluated(plan)) {
            return Grade.createEmptyGrade();
        }

        if (!isAggregationConcluded(plan)) {
            return Grade.createGrade(GradeScale.RE, getGradeScale());
        }

        return getGradeCalculator().calculate(this, plan);
    }

    /**
     * Note that this behaviour is independent from this aggregator being master or slave in the enrolment process
     */
    @SuppressWarnings("deprecation")
    private Date calculateConclusionDate(final StudentCurricularPlan plan) {
        if (getEntriesSet().isEmpty()) {
            return null;
        }

        YearMonthDay result = null;

        for (final CurriculumAggregatorEntry iter : getEntriesSet()) {
            final YearMonthDay conclusionDate = iter.calculateConclusionDate(plan);
            if (conclusionDate != null && (result == null || conclusionDate.isAfter(result))) {
                result = conclusionDate;
            }
        }

        if (result == null && isAggregationConcluded(plan)) {
            final CurriculumLine line = plan.getApprovedCurriculumLine(getCurricularCourse());
            result = line == null ? null : line.calculateConclusionDate();
        }

        if (result == null) {
            result = new YearMonthDay();
        }

        return new Date(result.getYear() - 1900, result.getMonthOfYear() - 1, result.getDayOfMonth());
    }

    public Set<Context> getEnrolmentMasterContexts() {
        final Set<Context> result = Sets.newHashSet();

        if (isEnrolmentMaster()) {
            result.add(getContext());

        } else if (isEnrolmentSlave()) {
            // Warning: must collect contexts from inner groups in order to know when this aggregator can be enroled
            getEntriesSet().stream().forEach(i -> result.add(i.getContext()));
        }

        return result;
    }

    public Set<Context> getEnrolmentSlaveContexts() {
        final Set<Context> result = Sets.newHashSet();

        if (isEnrolmentMaster()) {
            // Warning: no need to collect contexts from inner groups since enroling in aggregator can never replace the human choice within those groups
            getEntriesSet().stream().forEach(i -> result.add(i.getContext()));

        } else if (isEnrolmentSlave()) {
            result.add(getContext());
        }

        return result;
    }

}
