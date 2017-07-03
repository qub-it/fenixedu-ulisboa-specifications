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
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularRules.ICurricularRule;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;
import org.fenixedu.academic.domain.studentCurriculum.Dismissal;
import org.fenixedu.ulisboa.specifications.domain.curricularRules.CurricularRuleServices;
import org.fenixedu.ulisboa.specifications.domain.curricularRules.CurriculumAggregatorApproval;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.YearMonthDay;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public class CurriculumAggregatorEntry extends CurriculumAggregatorEntry_Base {

    protected CurriculumAggregatorEntry() {
        super();
    }

    static protected CurriculumAggregatorEntry create(final CurriculumAggregator aggregator, final Context context,
            final AggregationMemberEvaluationType evaluationType, final BigDecimal gradeFactor, final int gradeValueScale,
            final boolean optional) {

        final CurriculumAggregatorEntry result = new CurriculumAggregatorEntry();
        result.setAggregator(aggregator);
        result.setContext(context);
        result.init(evaluationType, gradeFactor, gradeValueScale, optional);

        final DegreeModule degreeModule = context.getChildDegreeModule();
        if (degreeModule.isLeaf()) {
            final List<? extends ICurricularRule> curricularRules = CurricularRuleServices.getCurricularRules(degreeModule,
                    CurriculumAggregatorApproval.class, context.getBeginExecutionPeriod());
            if (curricularRules.isEmpty()) {
                new CurriculumAggregatorApproval(degreeModule, context.getParentCourseGroup(), context.getBeginExecutionPeriod(),
                        (ExecutionSemester) null);
            }
        }

        return result;
    }

    @Atomic
    public CurriculumAggregatorEntry edit(final AggregationMemberEvaluationType evaluationType, final BigDecimal gradeFactor,
            final int gradeValueScale, final boolean optional) {

        init(evaluationType, gradeFactor, gradeValueScale, optional);

        return this;
    }

    private void init(final AggregationMemberEvaluationType evaluationType, final BigDecimal gradeFactor,
            final int gradeValueScale, final boolean optional) {

        super.setEvaluationType(evaluationType);
        super.setGradeFactor(gradeFactor);
        super.setGradeValueScale(gradeValueScale);
        super.setOptional(optional);

        checkRules();
    }

    private void checkRules() {
        if (getAggregator() == null) {
            throw new ULisboaSpecificationsDomainException("error.CurriculumAggregatorEntry.required.Aggregator");
        }

        if (getContext() == null) {
            throw new ULisboaSpecificationsDomainException("error.CurriculumAggregatorEntry.required.Context");
        }

        if (getEvaluationType() == null) {
            throw new ULisboaSpecificationsDomainException("error.CurriculumAggregatorEntry.required.EvaluationType");
        }

        if (getGradeFactor() == null || getGradeFactor().compareTo(BigDecimal.ZERO) < 0) {
            throw new ULisboaSpecificationsDomainException("error.CurriculumAggregatorEntry.required.GradeFactor");
        }

        if (getGradeValueScale() < 0) {
            throw new DomainException("error.CurriculumAggregatorEntry.required.GradeValueScale");
        }
    }

    @Atomic
    public void delete() {
        super.setAggregator(null);
        super.setContext(null);

        super.deleteDomainObject();
    }

    public String getDescription() {
        return ULisboaSpecificationsUtil.bundle("CurriculumAggregatorEntry");
    }

    public boolean isCandidateForEvaluation() {
        return getEvaluationType().isCandidateForEvaluation();
    }

    protected boolean isAggregationEvaluated(final StudentCurricularPlan plan) {
        final CurriculumModule module = getCurriculumModule(plan, false);
        if (module != null) {
            if (module instanceof CurriculumGroup) {
                // TODO legidio
            }
            if (module instanceof Dismissal) {
                return true;
            }
            if (module instanceof Enrolment) {
                return !((Enrolment) module).getGrade().isEmpty();
            }
        }

        return false;
    }

    public boolean isAggregationConcluded(final StudentCurricularPlan plan) {
        final CurriculumModule module = getCurriculumModule(plan, true);
        return module != null && module.isConcluded();
    }

    private CurriculumModule getCurriculumModule(final StudentCurricularPlan plan, final boolean approved) {
        final CurriculumModule result;

        final DegreeModule degreeModule = getContext().getChildDegreeModule();
        if (degreeModule.isCourseGroup()) {
            result = plan.findCurriculumGroupFor((CourseGroup) degreeModule);

        } else if (degreeModule.isCurricularCourse()) {

            if (approved) {
                result = plan.getApprovedCurriculumLine((CurricularCourse) degreeModule);

            } else {
                result = getLastEnrolment(plan);
            }

        } else {
            throw new ULisboaSpecificationsDomainException("error.CurriculumAggregatorEntry.unexpected.entry.type");
        }

        return result;
    }

    private Enrolment getLastEnrolment(final StudentCurricularPlan plan) {
        Enrolment result = null;

        if (plan != null && getContext().getChildDegreeModule().isCurricularCourse()) {

            result = plan.getEnrolments((CurricularCourse) getContext().getChildDegreeModule()).stream().max((x, y) -> {
                final int c = x.getExecutionYear().compareTo(y.getExecutionYear());
                return c == 0 ? ICurriculumEntry.COMPARATOR_BY_ID.compare(x, y) : c;
            }).orElse(null);
        }

        return result;
    }

    private Set<ICurriculumEntry> getApprovedCurriculumEntries(final StudentCurricularPlan plan) {
        return getCurriculumEntries(plan, true);
    }

    protected Set<ICurriculumEntry> getCurriculumEntries(final StudentCurricularPlan plan, final boolean approved) {
        final Set<ICurriculumEntry> result = Sets.newHashSet();

        final CurriculumModule module = getCurriculumModule(plan, approved);
        if (module != null) {

            final Collection<CurriculumLine> lines =
                    approved ? module.getApprovedCurriculumLines() : module.getAllCurriculumLines();

            result.addAll(lines.stream().filter(i -> i instanceof ICurriculumEntry).map(i -> ((ICurriculumEntry) i))
                    .collect(Collectors.toSet()));
        }

        return result;
    }

    protected BigDecimal calculateGradeValue(final StudentCurricularPlan plan) {
        BigDecimal result = BigDecimal.ZERO;

        if (isAggregationConcluded(plan)) {
            final Set<ICurriculumEntry> approvals = getApprovedCurriculumEntries(plan);

            if (approvals.size() == 1) {
                final Grade grade = approvals.iterator().next().getGrade();
                if (grade.isNumeric()) {
                    result = new BigDecimal(grade.getValue());
                }

            } else {

                final Supplier<Stream<ICurriculumEntry>> supplier =
                        () -> approvals.stream().filter(i -> i.getGrade().isNumeric());

                final BigDecimal sum =
                        supplier.get().map(i -> new BigDecimal(i.getGrade().getValue())).reduce(BigDecimal.ZERO, BigDecimal::add);
                final BigDecimal divisor = new BigDecimal(supplier.get().count());

                final BigDecimal avg =
                        sum.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : sum.divide(divisor, 10, RoundingMode.UNNECESSARY);

                result = avg.setScale(getGradeValueScale(), RoundingMode.UNNECESSARY);
            }
        }

        return result;
    }

    @SuppressWarnings("deprecation")
    protected YearMonthDay calculateConclusionDate(final StudentCurricularPlan plan) {
        YearMonthDay result = null;

        if (isAggregationConcluded(plan)) {
            final Set<ICurriculumEntry> approvals = getApprovedCurriculumEntries(plan);

            if (approvals.size() == 1) {
                result = approvals.iterator().next().getApprovementDate();

            } else {

                for (final ICurriculumEntry iter : approvals) {
                    final YearMonthDay conclusionDate = iter.getApprovementDate();
                    if (conclusionDate != null && (result == null || conclusionDate.isAfter(result))) {
                        result = conclusionDate;
                    }
                }
            }
        }

        return result;
    }

}
