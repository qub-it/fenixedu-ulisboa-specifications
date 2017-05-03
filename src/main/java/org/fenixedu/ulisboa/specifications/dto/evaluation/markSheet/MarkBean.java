/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: luis.egidio@qub-it.com
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

package org.fenixedu.ulisboa.specifications.dto.evaluation.markSheet;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.IBean;
import org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheet;
import org.fenixedu.ulisboa.specifications.domain.services.enrollment.EnrolmentServices;
import org.fenixedu.ulisboa.specifications.domain.services.statute.StatuteServices;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregator;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorServices;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.LocalDate;
import org.joda.time.YearMonthDay;

import com.google.common.base.Strings;

import pt.ist.fenixframework.Atomic;

public class MarkBean implements IBean, Comparable<MarkBean> {

    private CompetenceCourseMarkSheet markSheet;
    private Enrolment enrolment;
    private EnrolmentEvaluation evaluation;
    private Integer studentNumber;
    private String studentName;
    private String gradeValue;
    private String degreeName;
    private String degreeCode;
    private String shifts;
    private String statutes;
    private LocalDate gradeAvailableDate;
    private String errorMessage;
    private String infoMessage;

    public MarkBean(final CompetenceCourseMarkSheet markSheet, final EnrolmentEvaluation evaluation) {
        this(markSheet, evaluation.getEnrolment());
        setEvaluation(evaluation);
        setGradeValueSuggested();

        if (!evaluation.isTemporary()) {
            setGradeAvailableDate(evaluation.getGradeAvailableDateYearMonthDay().toLocalDate());
        }
    }

    public MarkBean(final CompetenceCourseMarkSheet markSheet, final Enrolment enrolment) {
        setMarkSheet(markSheet);
        setEnrolment(enrolment);
        setGradeValueSuggested();

        final Registration registration = enrolment.getRegistration();
        this.studentNumber = registration.getNumber();
        this.studentName = registration.getName();
        this.degreeName =
                enrolment.getStudentCurricularPlan().getDegree().getPresentationName().replace("'", " ").replace("\"", " ");
        this.degreeCode = enrolment.getStudentCurricularPlan().getDegree().getCode();
        this.shifts = EnrolmentServices.getShiftsDescription(enrolment, markSheet.getExecutionSemester());
        this.statutes =
                StatuteServices.getVisibleStatuteTypesDescription(enrolment.getRegistration(), enrolment.getExecutionPeriod())
                        .replace("'", " ").replace("\"", " ");
    }

    public CompetenceCourseMarkSheet getMarkSheet() {
        return markSheet;
    }

    public void setMarkSheet(final CompetenceCourseMarkSheet input) {
        this.markSheet = input;
    }

    public Enrolment getEnrolment() {
        return enrolment;
    }

    public void setEnrolment(final Enrolment input) {
        this.enrolment = input;
    }

    public EnrolmentEvaluation getEvaluation() {
        return evaluation;
    }

    private void setEvaluation(final EnrolmentEvaluation input) {
        this.evaluation = input;
    }

    public Integer getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(Integer studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setGradeValueSuggested() {
        // info message caches suggestion
        String suggestionValue = null;
        if (Strings.isNullOrEmpty(getInfoMessage())) {
            final Grade suggestion = getGradeSuggestedByAggregation();
            suggestionValue = suggestion == null || suggestion.isEmpty() ? null : suggestion.getValue();
            if (suggestionValue != null) {
                setInfoMessage(ULisboaSpecificationsUtil.bundle("info.MarkBean.gradeValue.suggestion", suggestionValue));
            }
        }

        setGradeValue(evaluation != null ? evaluation.getGrade().getValue() : suggestionValue != null ? suggestionValue : null);
    }

    private Grade getGradeSuggestedByAggregation() {
        final Context context = CurriculumAggregatorServices.getContext(getEnrolment());
        final CurriculumAggregator aggregator = context == null ? null : context.getCurriculumAggregator();
        if (aggregator != null && aggregator.isCandidateForEvaluation(getEvaluationSeason())) {
            return aggregator.calculateConclusionGrade(getEnrolment().getStudentCurricularPlan());
        }

        return null;
    }

    public String getGradeValue() {
        return gradeValue;
    }

    public void setGradeValue(final String input) {
        this.gradeValue = input;
        cleanupGrade();
    }

    private void cleanupGrade() {
        if (!StringUtils.isBlank(this.gradeValue)) {
            this.gradeValue = this.gradeValue.replaceAll("\\s+", "").toUpperCase();
        }

        this.gradeValue = NumberUtils.isNumber(this.gradeValue) ? cleanupNumber(this.gradeValue) : this.gradeValue;
    }

    private String cleanupNumber(String toCleanup) {
        final Double parsedValue = Double.valueOf(toCleanup);
        return parsedValue % 1 == 0 ? String.valueOf(parsedValue.intValue()) : parsedValue.toString();
    }

    public String getDegreeName() {
        return degreeName;
    }

    public void setDegreeName(String degreeName) {
        this.degreeName = degreeName;
    }

    public String getDegreeCode() {
        return degreeCode;
    }

    public void setDegreeCode(String degreeCode) {
        this.degreeCode = degreeCode;
    }

    public String getShifts() {
        return shifts;
    }

    public void setShifts(String shifts) {
        this.shifts = shifts;
    }

    public String getStatutes() {
        return statutes;
    }

    public void setStatutes(String statutes) {
        this.statutes = statutes;
    }

    public LocalDate getGradeAvailableDate() {
        return gradeAvailableDate;
    }

    public void setGradeAvailableDate(final LocalDate input) {
        this.gradeAvailableDate = input;
    }

    @Override
    public int compareTo(final MarkBean o) {
        return CompetenceCourseMarkSheet.COMPARATOR_FOR_STUDENT_NAME.compare(getStudentName(), o.getStudentName());
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(final String input) {
        this.infoMessage = input;
    }

    public void validate() {
        cleanupGrade();
        validateGrade();
    }

    private void validateGrade() {
        if (hasGradeValue()) {

            if (!getMarkSheet().isGradeValueAccepted(getGradeValue())) {
                setErrorMessage(ULisboaSpecificationsUtil.bundle("error.MarkBean.gradeValue.does.not.belong.to.scale",
                        getGradeScale().getDescription()));

            } else {

                final EnrolmentEvaluation evaluation = findEnrolmentEvaluation();
                if (evaluation != null) {

                    // report concurrent mark sheet editions
                    final CompetenceCourseMarkSheet evaluationMarkSheet = evaluation.getCompetenceCourseMarkSheet();
                    if (evaluationMarkSheet != null && evaluationMarkSheet != getMarkSheet()) {
                        setErrorMessage(ULisboaSpecificationsUtil.bundle("error.MarkBean.evaluation.already.edited",
                                getGradeScale().getDescription()));
                    }
                }
            }
        }
    }

    public boolean hasGradeValue() {
        return !StringUtils.isBlank(getGradeValue());
    }

    private GradeScale getGradeScale() {
        return getMarkSheet().getGradeScale();
    }

    private EvaluationSeason getEvaluationSeason() {
        return getMarkSheet().getEvaluationSeason();
    }

    @Atomic
    void updateGrade() {

        final EnrolmentEvaluation evaluation = findEnrolmentEvaluation();
        if (evaluation == null) {

            if (hasGradeValue()) {
                CompetenceCourseMarkSheet.setEnrolmentEvaluationData(getMarkSheet(),
                        new EnrolmentEvaluation(getEnrolment(), getEvaluationSeason()), getGradeValue(), getGradeScale());
            }

        } else {

            if (!hasGradeValue()) {
                CompetenceCourseMarkSheet.removeEnrolmentEvaluationData(evaluation);

            } else {
                CompetenceCourseMarkSheet.setEnrolmentEvaluationData(getMarkSheet(), evaluation, getGradeValue(),
                        getGradeScale());
            }
        }
    }

    private EnrolmentEvaluation findEnrolmentEvaluation() {
        if (getEvaluation() != null) {
            return getEvaluation();
        }

        final CompetenceCourseMarkSheet markSheet = getMarkSheet();
        final Optional<EnrolmentEvaluation> foundEvaluation =
                getEnrolment().getEnrolmentEvaluation(markSheet.getEvaluationSeason(), markSheet.getExecutionSemester(), false);

        return foundEvaluation.isPresent() ? foundEvaluation.get() : null;
    }

    @Atomic
    void updateGradeAvailableDate() {
        final EnrolmentEvaluation evaluation = findEnrolmentEvaluation();
        if (evaluation != null && getGradeAvailableDate() != null) {

            if (!evaluation.getGradeAvailableDateYearMonthDay().equals(getGradeAvailableDate())
                    && getMarkSheet().getEvaluationDate().isBefore(getGradeAvailableDate())) {

                evaluation.setGradeAvailableDateYearMonthDay(new YearMonthDay(getGradeAvailableDate()));
            }
        }
    }

}
