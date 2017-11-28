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
import java.util.Comparator;
import java.util.List;

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularRules.ICurricularRule;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.Dismissal;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.curricularRules.CurricularRuleServices;
import org.fenixedu.ulisboa.specifications.domain.curricularRules.CurriculumAggregatorApproval;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.YearMonthDay;

import pt.ist.fenixframework.Atomic;

public class CurriculumAggregatorEntry extends CurriculumAggregatorEntry_Base {

    protected CurriculumAggregatorEntry() {
        super();
    }

    static protected CurriculumAggregatorEntry create(final CurriculumAggregator aggregator, final Context context,
            final AggregationMemberEvaluationType evaluationType, final boolean supportsTeacherConfirmation,
            final BigDecimal gradeFactor, final int gradeValueScale, final boolean optional) {

        final CurriculumAggregatorEntry result = new CurriculumAggregatorEntry();
        result.setAggregator(aggregator);
        result.setContext(context);
        result.init(evaluationType, supportsTeacherConfirmation, gradeFactor, gradeValueScale, optional);

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
    public CurriculumAggregatorEntry edit(final AggregationMemberEvaluationType evaluationType,
            final boolean supportsTeacherConfirmation, final BigDecimal gradeFactor, final int gradeValueScale,
            final boolean optional) {

        init(evaluationType, supportsTeacherConfirmation, gradeFactor, gradeValueScale, optional);

        return this;
    }

    private void init(final AggregationMemberEvaluationType evaluationType, final boolean supportsTeacherConfirmation,
            final BigDecimal gradeFactor, final int gradeValueScale, final boolean optional) {

        super.setEvaluationType(evaluationType);
        super.setSupportsTeacherConfirmation(supportsTeacherConfirmation);
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

        final CurriculumAggregatorEntry found = CurriculumAggregatorServices.findAggregatorEntry(getContext(), getSince());
        if (found != null && found != this) {
            throw new DomainException("error.CurriculumAggregatorEntry.duplicate");
        }

        if (!getContext().isValid(getSince())) {
            throw new DomainException("error.CurriculumAggregatorEntry.invalid.Context");
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

    public String getDescriptionFull() {
        final String description = getAggregator().getCurricularCourse().getCode();
        final String since = getAggregator().getSince().getQualifiedName();

        final String gradeFactor =
                ", " + (getGradeFactor() == null || BigDecimal.ZERO.compareTo(getGradeFactor()) == 0 ? BundleUtil
                        .getLocalizedString(Bundle.ENUMERATION, GradeScale.TYPEQUALITATIVE.name())
                        .getContent() : getGradeFactor().multiply(BigDecimal.valueOf(100d)).stripTrailingZeros().toPlainString()
                                + "%");

        final GradeScale gradeScale = getGradeScale();
        String gradeScaleDescription = "";
        if (gradeScale != GradeScale.TYPE20) {
            gradeScaleDescription = " " + gradeScale.getDescription().replace(GradeScale.TYPE20.getDescription(), "");
        }

        String result = String.format("%s [%s %s%s%s]", description, BundleUtil.getString(Bundle.APPLICATION, "label.since"),
                since, gradeFactor, gradeScaleDescription);

        if (getOptional()) {
            result += " [Op]";
        }

        return result;
    }

    public boolean isLegacy() {
        return getAggregator().isLegacy();
    }

    public DegreeCurricularPlan getDegreeCurricularPlan() {
        return getContext().getParentCourseGroup().getParentDegreeCurricularPlan();
    }

    public ExecutionYear getSince() {
        return getAggregator().getSince();
    }

    public boolean isValid(final ExecutionYear year) {
        return getAggregator().isValid(year);
    }

    public CurriculumAggregator getAggregatorPreviousConfig() {
        return getAggregator().getPreviousConfig();
    }

    public CurriculumAggregator getAggregatorNextConfig() {
        return getAggregator().getNextConfig();
    }

    public CurricularCourse getCurricularCourse() {
        return (CurricularCourse) getContext().getChildDegreeModule();
    }

    public GradeScale getGradeScale() {
        final GradeScale competenceScale = getCurricularCourse().getCompetenceCourse().getGradeScale();
        return competenceScale != null ? competenceScale : getCurricularCourse().getGradeScaleChain();
    }

    public boolean isCandidateForEvaluation() {
        return getEvaluationType().isCandidateForEvaluation();
    }

    protected boolean isAggregationEvaluated(final StudentCurricularPlan plan) {
        final CurriculumLine line = getCurriculumLine(plan, false);
        if (line != null) {
            if (line instanceof Dismissal) {
                return true;
            }
            if (line instanceof Enrolment) {
                final Enrolment enrolment = (Enrolment) line;
                return !enrolment.isAnnulled() && !enrolment.getGrade().isEmpty();
            }
        }

        return false;
    }

    public boolean isAggregationConcluded(final StudentCurricularPlan plan) {
        final CurriculumLine line = getCurriculumLine(plan, true);
        return line != null && line.isConcluded();
    }

    public CurriculumLine getCurriculumLine(final StudentCurricularPlan plan, final boolean approved) {
        final CurriculumLine result;

        final DegreeModule degreeModule = getCurricularCourse();
        if (degreeModule.isCurricularCourse()) {

            if (approved) {
                result = plan.getApprovedCurriculumLine((CurricularCourse) degreeModule);

            } else {

                result = plan.getAllCurriculumLines().stream().filter(
                        i -> i.getDegreeModule() == getCurricularCourse() && getSince().isBeforeOrEquals(i.getExecutionYear()))
                        .max(CurriculumAggregatorServices.LINE_COMPARATOR).orElse(null);
            }

        } else {
            throw new ULisboaSpecificationsDomainException("error.CurriculumAggregatorEntry.unexpected.entry.type");
        }

        return result;
    }

    protected BigDecimal calculateGradeValue(final StudentCurricularPlan plan) {
        BigDecimal result = BigDecimal.ZERO;

        if (isAggregationConcluded(plan)) {

            final CurriculumLine line = getCurriculumLine(plan, true);
            if (line != null) {

                Grade grade = null;
                if (line instanceof ICurriculumEntry) {
                    grade = ((ICurriculumEntry) line).getGrade();
                }

                if (grade != null && grade.isNumeric()) {
                    result = new BigDecimal(grade.getValue());
                }
            }
        }

        return result;
    }

    @SuppressWarnings("deprecation")
    protected YearMonthDay calculateConclusionDate(final StudentCurricularPlan plan) {
        YearMonthDay result = null;

        if (isAggregationConcluded(plan)) {

            final CurriculumLine line = getCurriculumLine(plan, true);
            if (line != null) {

                if (line instanceof ICurriculumEntry) {
                    result = ((ICurriculumEntry) line).getApprovementDate();
                }
            }
        }

        return result;
    }

}
