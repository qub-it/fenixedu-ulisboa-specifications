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
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.util.EnrolmentEvaluationState;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot;
import org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheet;
import org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheetChangeRequest;
import org.fenixedu.ulisboa.specifications.domain.services.enrollment.EnrolmentServices;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.YearMonthDay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public class CurriculumAggregator extends CurriculumAggregator_Base {

    static private final Logger logger = LoggerFactory.getLogger(CurriculumAggregator.class);

    protected CurriculumAggregator() {
        super();
        setRoot(ULisboaSpecificationsRoot.getInstance());
    }

    @Atomic
    static public CurriculumAggregator create(final Context context, final LocalizedString description,
            final AggregationEnrolmentType enrolmentType, final AggregationMemberEvaluationType evaluationType,
            final EvaluationSeason evaluationSeason, final AggregationGradeCalculator gradeCalculator,
            final int optionalConcluded) {

        final CurriculumAggregator result = new CurriculumAggregator();
        result.setContext(context);
        result.init(description, enrolmentType, evaluationType, evaluationSeason, gradeCalculator, optionalConcluded);

        return result;
    }

    @Atomic
    public CurriculumAggregator edit(final LocalizedString description, final AggregationEnrolmentType enrolmentType,
            final AggregationMemberEvaluationType evaluationType, final EvaluationSeason evaluationSeason,
            final AggregationGradeCalculator gradeCalculator, final int optionalConcluded) {

        init(description, enrolmentType, evaluationType, evaluationSeason, gradeCalculator, optionalConcluded);

        return this;
    }

    private void init(final LocalizedString description, final AggregationEnrolmentType enrolmentType,
            final AggregationMemberEvaluationType evaluationType, final EvaluationSeason evaluationSeason,
            final AggregationGradeCalculator gradeCalculator, final int optionalConcluded) {

        super.setDescription(description);
        super.setEnrolmentType(enrolmentType);
        super.setEvaluationType(evaluationType);
        super.setEvaluationSeason(evaluationSeason);
        super.setGradeCalculator(gradeCalculator);
        super.setOptionalConcluded(optionalConcluded);

        checkRules();
    }

    private void checkRules() {
        if (getContext() == null) {
            throw new DomainException("error.CurriculumAggregator.required.Context");
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

        if (getGradeCalculator() == null) {
            throw new DomainException("error.CurriculumAggregator.required.GradeCalculator");
        }

        if (getEvaluationSeason() == null) {
            throw new DomainException("error.CurriculumAggregator.required.EvaluationSeason");
        }
    }

    @Atomic
    public void delete() {
        if (!getEntriesSet().isEmpty()) {
            throw new DomainException("error.CurriculumAggregator.delete.entries");
        }

        super.setContext(null);
        super.setEvaluationSeason(null);

        super.deleteDomainObject();
    }

    @Atomic
    public CurriculumAggregatorEntry createEntry(final Context context, final AggregationMemberEvaluationType evaluationType,
            final BigDecimal gradeFactor) {

        final CurriculumAggregatorEntry result = CurriculumAggregatorEntry.create(this, context, evaluationType, gradeFactor);

        checkRules();
        return result;
    }

    @Override
    public LocalizedString getDescription() {
        if (super.getDescription() != null && !super.getDescription().isEmpty()) {
            return super.getDescription();
        }

        return ULisboaSpecificationsUtil
                .bundleI18N(isEnrolmentMaster() ? "CurriculumAggregator.master" : "CurriculumAggregator.slave");
    }

    public DegreeCurricularPlan getDegreeCurricularPlan() {
        return getContext().getParentCourseGroup().getParentDegreeCurricularPlan();
    }

    protected CurricularCourse getCurricularCourse() {
        return (CurricularCourse) getContext().getChildDegreeModule();
    }

    private boolean isWithMarkSheet() {
        return getEvaluationType() == AggregationMemberEvaluationType.WITH_MARK_SHEET;
    }

    private boolean isWithoutMarkSheet() {
        return getEvaluationType() == AggregationMemberEvaluationType.WITHOUT_MARK_SHEET;
    }

    public boolean isCandidateForEvaluation(final EvaluationSeason season) {
        return getEvaluationType().isCandidateForEvaluation() && (getEvaluationSeason() == season || season.isImprovement());
    }

    public boolean isEnrolmentMaster() {
        return getEnrolmentType() == AggregationEnrolmentType.ONLY_AGGREGATOR;
    }

    public boolean isEnrolmentSlave() {
        return getEnrolmentType() == AggregationEnrolmentType.ONLY_AGGREGATOR_ENTRIES;
    }

    public void updateEvaluation(final StudentCurricularPlan plan) {

        final Enrolment enrolment = getLastEnrolment(plan);
        if (enrolment == null) {
            return;
        }

        if (isWithMarkSheet()) {
            updateMarkSheets(enrolment);

        } else if (isWithoutMarkSheet()) {
            updateGrade(enrolment);
        }
    }

    private void updateGrade(final Enrolment enrolment) {
        final StudentCurricularPlan plan = enrolment.getStudentCurricularPlan();

        // get EnrolmentEvaluation
        final EnrolmentEvaluation evaluation;
        if (enrolment.getEvaluationsSet().isEmpty()) {
            evaluation = new EnrolmentEvaluation(enrolment, getEvaluationSeason());
        } else if (enrolment.getEvaluationsSet().size() != 1) {
            throw new DomainException("error.CurriculumAggregator.unexpected.number.of.EnrolmentEvaluations");
        } else {
            evaluation = enrolment.getEvaluationsSet().iterator().next();
        }

        final Grade conclusionGrade = calculateConclusionGrade(plan);
        final Date conclusionDate = calculateConclusionDate(plan);
        if (!conclusionGrade.isEmpty()) {

            final Date availableDate = new Date();
            evaluation.edit(Authenticate.getUser().getPerson(), conclusionGrade, availableDate, conclusionDate);
            evaluation.confirmSubmission(Authenticate.getUser().getPerson(), getDescription().getContent());

        } else {

            // check if evaluation needs to be removed
            if (evaluation != null) {
                logger.debug("Aggregator is not concluded, will remove existing evaluation in {} at {}",
                        getCurricularCourse().getOneFullName(), enrolment.getExecutionPeriod().getQualifiedName());

                evaluation.setEnrolmentEvaluationState(EnrolmentEvaluationState.TEMPORARY_OBJ);
                evaluation.delete();
            }
        }

        EnrolmentServices.updateState(enrolment);
    }

    private void updateMarkSheets(final Enrolment enrolment) {
        final StudentCurricularPlan plan = enrolment.getStudentCurricularPlan();

        final Grade conclusionGrade = calculateConclusionGrade(plan);
        enrolment.getEvaluationsSet().stream().map(i -> i.getCompetenceCourseMarkSheet())
                .forEach(i -> updateMarkSheet(i, enrolment, conclusionGrade));
    }

    private void updateMarkSheet(final CompetenceCourseMarkSheet markSheet, final Enrolment enrolment,
            final Grade conclusionGrade) {

        if (markSheet != null) {
            final String reason =
                    ULisboaSpecificationsUtil.bundle("CurriculumAggregator.CompetenceCourseMarkSheetChangeRequest.reason",
                            getDescription().getContent(), enrolment.getFullPath(), conclusionGrade.getValue());

            CompetenceCourseMarkSheetChangeRequest.create(markSheet, Authenticate.getUser().getPerson(), reason);
        }
    }

    private Enrolment getLastEnrolment(final StudentCurricularPlan plan) {
        Enrolment result = null;

        if (plan != null) {

            final ExecutionSemester semester = getLastExecutionSemester(plan);
            if (semester != null) {

                result = plan.getEnrolmentByCurricularCourseAndExecutionPeriod(getCurricularCourse(), semester);
                if (result == null) {
                    logger.debug("No aggregator enrolment found, no grade to update in {} at {}",
                            getCurricularCourse().getOneFullName(), semester.getQualifiedName());
                }
            }
        }

        return result;
    }

    private ExecutionSemester getLastExecutionSemester(final StudentCurricularPlan plan) {
        ExecutionSemester result = null;

        if (plan != null) {

            final SortedSet<ICurriculumEntry> lines = Sets.newTreeSet((x, y) -> {
                final int c = x.getExecutionYear().compareTo(y.getExecutionYear());
                return c == 0 ? ICurriculumEntry.COMPARATOR_BY_ID.compare(x, y) : c;
            });
            for (final CurriculumAggregatorEntry entry : getEntriesSet()) {
                lines.addAll(entry.getApprovedCurriculumEntries(plan));
            }

            if (!lines.isEmpty()) {
                result = lines.last().getExecutionPeriod();
            }
        }

        return result;
    }

    /**
     * Note that this behaviour is independent from this aggregator being master or slave in the enrolment process
     */
    public boolean isConcluded(final StudentCurricularPlan plan) {

        if (!plan.hasDegreeModule(getCurricularCourse())) {
            return false;
        }

        int optionalConcludedEntries = 0;

        for (final CurriculumAggregatorEntry entry : getEntriesSet()) {

            if (!entry.isConcluded(plan)) {
                if (!entry.getOptional()) {
                    return false;
                }

            } else if (entry.getOptional()) {
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
        if (getEntriesSet().isEmpty() || !isConcluded(plan)) {
            return Grade.createEmptyGrade();
        }

        return getGradeCalculator().calculate(this, plan);
    }

    /**
     * Note that this behaviour is independent from this aggregator being master or slave in the enrolment process
     */
    @SuppressWarnings("deprecation")
    private Date calculateConclusionDate(final StudentCurricularPlan plan) {
        if (getEntriesSet().isEmpty() || !isConcluded(plan)) {
            return null;
        }

        YearMonthDay result = null;

        for (final CurriculumAggregatorEntry iter : getEntriesSet()) {
            final YearMonthDay conclusionDate = iter.calculateConclusionDate(plan);
            if (conclusionDate != null && (result == null || conclusionDate.isAfter(result))) {
                result = conclusionDate;
            }
        }

        return new Date(result.getYear(), result.getMonthOfYear(), result.getDayOfMonth());
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
