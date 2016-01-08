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
import org.fenixedu.academic.domain.curriculum.EnrolmentEvaluationContext;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.util.EnrolmentEvaluationState;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheet;
import org.fenixedu.ulisboa.specifications.domain.services.enrollment.EnrolmentServices;
import org.fenixedu.ulisboa.specifications.domain.services.statute.StatuteServices;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.DateTime;
import org.joda.time.YearMonthDay;

import pt.ist.fenixframework.Atomic;

public class MarkBean implements IBean, Comparable<MarkBean> {

    private CompetenceCourseMarkSheet markSheet;
    private Enrolment enrolment;
    private Integer studentNumber;
    private String studentName;
    private String gradeValue;
    private String degreeName;
    private String degreeCode;
    private String shifts;
    private String statutes;
    private String errorMessage;

    public MarkBean(final CompetenceCourseMarkSheet markSheet, final Enrolment enrolment) {
        setMarkSheet(markSheet);
        setEnrolment(enrolment);

        final Registration registration = enrolment.getRegistration();
        this.studentNumber = registration.getNumber();
        this.studentName = registration.getName();
        this.degreeName =
                enrolment.getStudentCurricularPlan().getDegree().getPresentationName().replace("'", " ").replace("\"", " ");
        this.degreeCode = enrolment.getStudentCurricularPlan().getDegree().getCode();
        this.shifts = EnrolmentServices.getShiftsDescription(enrolment);
        this.statutes = StatuteServices.getStatuteTypesDescription(enrolment.getRegistration(), enrolment.getExecutionPeriod())
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
        return parsedValue % 1 == 0 ? String.valueOf(parsedValue.intValue()) : toCleanup;
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

    @Override
    public int compareTo(final MarkBean o) {
        return getStudentName().compareTo(o.getStudentName());
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void validate() {
        cleanupGrade();
        validateGrade();
    }

    private void validateGrade() {
        if (hasGradeValue() && !getMarkSheet().isGradeValueAccepted(getGradeValue())) {
            setErrorMessage(ULisboaSpecificationsUtil.bundle("error.MarkBean.gradeValue.does.not.belong.to.scale",
                    getGradeScale().getDescription()));
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
    void updateEnrolmentEvaluation() {

        final EnrolmentEvaluation evaluation = findEnrolmentEvaluation();
        if (evaluation == null) {
            if (hasGradeValue()) {
                setEnrolmentEvaluationData(new EnrolmentEvaluation(getEnrolment(), getEvaluationSeason()));
            }
        } else {
            if (!hasGradeValue()) {
                evaluation.setGrade(Grade.createEmptyGrade());
                evaluation.setEnrolmentEvaluationState(EnrolmentEvaluationState.TEMPORARY_OBJ);
                evaluation.setCompetenceCourseMarkSheet(null);
            } else {
                setEnrolmentEvaluationData(evaluation);
            }
        }

    }

    private EnrolmentEvaluation findEnrolmentEvaluation() {
        final CompetenceCourseMarkSheet markSheet = getMarkSheet();
        final Optional<EnrolmentEvaluation> foundEvaluation =
                getEnrolment().getEnrolmentEvaluation(markSheet.getEvaluationSeason(), markSheet.getExecutionSemester(), false);

        return foundEvaluation.isPresent() ? foundEvaluation.get() : null;
    }

    private void setEnrolmentEvaluationData(final EnrolmentEvaluation enrolmentEvaluation) {
        final CompetenceCourseMarkSheet markSheet = getMarkSheet();

        enrolmentEvaluation.setWhenDateTime(new DateTime());
        enrolmentEvaluation.setGrade(Grade.createGrade(getGradeValue(), getGradeScale()));
        enrolmentEvaluation.setEnrolmentEvaluationState(EnrolmentEvaluationState.TEMPORARY_OBJ);
        enrolmentEvaluation.setContext(EnrolmentEvaluationContext.MARK_SHEET_EVALUATION);
        enrolmentEvaluation.setPerson(Authenticate.getUser().getPerson());
        enrolmentEvaluation.setPersonResponsibleForGrade(markSheet.getCertifier());
        enrolmentEvaluation.setExamDateYearMonthDay(markSheet.getEvaluationDate().toDateTimeAtStartOfDay().toYearMonthDay());
        enrolmentEvaluation.setGradeAvailableDateYearMonthDay(new YearMonthDay());
        enrolmentEvaluation.setCompetenceCourseMarkSheet(markSheet);
    }

}
