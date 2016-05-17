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
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Function;

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.DomainObjectUtil;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.util.EnrolmentEvaluationState;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.services.enrollment.EnrolmentServices;
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
    static public CurriculumAggregator create(final Context context, final AggregationMemberEvaluationType evaluationType,
            final EvaluationSeason evaluationSeason, final AggregationEnrolmentType enrolmentType) {

        final CurriculumAggregator result = new CurriculumAggregator();
        result.setContext(context);
        result.init(evaluationType, evaluationSeason, enrolmentType);

        return result;
    }

    @Atomic
    public CurriculumAggregator edit(final AggregationMemberEvaluationType evaluationType,
            final EvaluationSeason evaluationSeason, final AggregationEnrolmentType enrolmentType) {

        init(evaluationType, evaluationSeason, enrolmentType);

        return this;
    }

    private void init(final AggregationMemberEvaluationType evaluationType, final EvaluationSeason evaluationSeason,
            final AggregationEnrolmentType enrolmentType) {

        super.setEvaluationType(evaluationType);
        super.setEvaluationSeason(evaluationSeason);
        super.setEnrolmentType(enrolmentType);

        checkRules();
    }

    private void checkRules() {
        if (getContext() == null) {
            throw new DomainException("error.CurriculumAggregator.required.Context");
        }

        if (getEvaluationType() == null) {
            throw new DomainException("error.CurriculumAggregator.required.EvaluationType");
        }

        if (getEvaluationSeason() == null) {
            throw new DomainException("error.CurriculumAggregator.required.EvaluationSeason");
        }

        if (getEnrolmentType() == null) {
            throw new DomainException("error.CurriculumAggregator.required.EnrolmentType");
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
            final BigDecimal weighing) {

        final CurriculumAggregatorEntry result = CurriculumAggregatorEntry.create(this, context, evaluationType, weighing);

        checkRules();
        return result;
    }

    public String getDescription() {
        return isMaster() ? "Módulo" : "Tronco";
    }

    public DegreeCurricularPlan getDegreeCurricularPlan() {
        return getContext().getParentCourseGroup().getParentDegreeCurricularPlan();
    }

    protected CurricularCourse getCurricularCourse() {
        return (CurricularCourse) getContext().getChildDegreeModule();
    }

    public boolean isCandidateForEvaluation() {
        return getEvaluationType().isCandidateForEvaluation();
    }

    public boolean isEvaluatedByEntries() {
        return getEvaluationType() == AggregationMemberEvaluationType.WITH_WEIGHING_GRADE;
    }

    public boolean isMaster() {
        return getEnrolmentType() == AggregationEnrolmentType.ONLY_AGGREGATOR;
    }

    public boolean isSlave() {
        return getEnrolmentType() == AggregationEnrolmentType.ONLY_AGGREGATOR_ENTRIES;
    }

    public void updateGrade(final StudentCurricularPlan plan) {

        if (!isEvaluatedByEntries()) {
            return;
        }

        final Enrolment enrolment = getLastEnrolment(plan);
        if (enrolment == null) {
            return;
        }

        // get EnrolmentEvaluation
        final EnrolmentEvaluation evaluation;
        if (enrolment.getEvaluationsSet().isEmpty()) {
            evaluation = new EnrolmentEvaluation(enrolment, getEvaluationSeason());
        } else if (enrolment.getEvaluationsSet().size() != 1) {
            throw new DomainException("error.CurriculumAggregator.unexpected.number.of.EnrolmentEvaluations");
        } else {
            evaluation = enrolment.getEvaluationsSet().iterator().next();
        }

        if (isConcluded(plan)) {

            final Date now = new Date();
            evaluation.edit(Authenticate.getUser().getPerson(), calculateGrade(plan), now, now);
            evaluation.confirmSubmission(Authenticate.getUser().getPerson(), getDescription());

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

            final SortedSet<CurriculumLine> lines = Sets.newTreeSet((x, y) -> {
                final int c = x.getExecutionYear().compareTo(y.getExecutionYear());
                return c == 0 ? DomainObjectUtil.COMPARATOR_BY_ID.compare(x, y) : c;
            });
            for (final CurriculumAggregatorEntry entry : getEntriesSet()) {
                lines.addAll(entry.getApprovedCurriculumLines(plan));
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
    private boolean isConcluded(final StudentCurricularPlan plan) {

        if (!plan.hasDegreeModule(getCurricularCourse())) {
            return false;
        }

        for (final CurriculumAggregatorEntry entry : getEntriesSet()) {
            if (!entry.isConcluded(plan)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Note that this behaviour is independent from this aggregator being master or slave in the enrolment process
     */
    private Grade calculateGrade(final StudentCurricularPlan plan) {
        if (getEntriesSet().isEmpty()) {
            return Grade.createEmptyGrade();
        }

        final BigDecimal sum = getEntriesSet().stream().map(e -> e.getWeighing()).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (sum.compareTo(BigDecimal.ONE) != 0) {
            throw new ULisboaSpecificationsDomainException("error.CurriculumAggregator.unexpected.total.weight");
        }

        final Function<CurriculumAggregatorEntry, BigDecimal> mapper = i -> i.calculateGradeValue(plan).multiply(i.getWeighing());
        final BigDecimal sumPiCi = getEntriesSet().stream().map(mapper).reduce(BigDecimal.ZERO, BigDecimal::add);
        final BigDecimal divisor =
                new BigDecimal(getEntriesSet().stream().filter(i -> i.calculateGradeValue(plan) != BigDecimal.ZERO).count());

        final BigDecimal avg =
                sumPiCi.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : sumPiCi.divide(divisor, RoundingMode.HALF_UP);

        return Grade.createGrade(avg.setScale(0, RoundingMode.HALF_UP).toString(), GradeScale.TYPE20);
    }

    public Set<Context> getMasterContexts() {
        final Set<Context> result = Sets.newHashSet();

        if (isMaster()) {
            result.add(getContext());

        } else if (isSlave()) {
            // Warning: must collect contexts from inner groups in order to know when this aggregator can be enroled
            getEntriesSet().stream().forEach(i -> result.add(i.getContext()));
        }

        return result;
    }

    public Set<Context> getSlaveContexts() {
        final Set<Context> result = Sets.newHashSet();

        if (isMaster()) {
            // Warning: no need to collect contexts from inner groups since enroling in aggregator can never replace the human choice within those groups
            getEntriesSet().stream().forEach(i -> result.add(i.getContext()));

        } else if (isSlave()) {
            result.add(getContext());
        }

        return result;
    }

}
