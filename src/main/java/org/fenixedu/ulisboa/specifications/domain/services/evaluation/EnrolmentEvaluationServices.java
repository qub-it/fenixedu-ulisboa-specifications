package org.fenixedu.ulisboa.specifications.domain.services.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.util.EnrolmentEvaluationState;
import org.fenixedu.ulisboa.specifications.domain.evaluation.EnrolmentEvaluationExtendedInformation;
import org.fenixedu.ulisboa.specifications.domain.evaluation.EvaluationServices;
import org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheet;
import org.fenixedu.ulisboa.specifications.domain.services.enrollment.EnrolmentServices;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorServices;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

public class EnrolmentEvaluationServices {

    static final public String EVALUATION_DATE_FORMAT = "yyyy-MM-dd";
    static final public String EVALUATION_DATE_TIME_FORMAT = EVALUATION_DATE_FORMAT + " HH:mm";

    private static final Collection<Consumer<EnrolmentEvaluation>> STATE_CHANGE_LISTENERS = new ArrayList<>();

    static public void onStateChange(EnrolmentEvaluation evaluation) {
        STATE_CHANGE_LISTENERS.forEach(c -> c.accept(evaluation));
    }

    static public void registerStateChangeListener(final Consumer<EnrolmentEvaluation> listener) {
        STATE_CHANGE_LISTENERS.add(listener);
    }

    static public void setRemarks(final EnrolmentEvaluation enrolmentEvaluation, final String remarks) {
        EnrolmentEvaluationExtendedInformation.findOrCreate(enrolmentEvaluation).setRemarks(remarks);
    }

    static public String getRemarks(final EnrolmentEvaluation enrolmentEvaluation) {
        return enrolmentEvaluation.getExtendedInformation() == null ? null : enrolmentEvaluation.getExtendedInformation()
                .getRemarks();
    }

    static public String getExamDatePresentation(final EnrolmentEvaluation input) {
        String result = null;

        final DateTime examDateTime = getExamDateTime(input);
        if (examDateTime != null) {

            if (examDateTime.toString().contains("T00:00")) {
                result = examDateTime.toString(EVALUATION_DATE_FORMAT);
            } else {
                result = examDateTime.toString(EVALUATION_DATE_TIME_FORMAT);
            }
        }

        return result;
    }

    static public DateTime getExamDateTime(final EnrolmentEvaluation input) {
        DateTime result = null;

        if (input != null) {

            final CompetenceCourseMarkSheet sheet = input.getCompetenceCourseMarkSheet();
            if (sheet != null) {
                // has time, if has a course evaluation associated
                result = sheet.getEvaluationDateTime();
            }

            // has time
            if (result == null) {
                result = EvaluationServices
                        .findEnrolmentCourseEvaluations(input.getEnrolment(), input.getEvaluationSeason(),
                                input.getExecutionPeriod())
                        .stream().filter(ev -> ev.getEvaluationDate() != null).map(ev -> new DateTime(ev.getEvaluationDate()))
                        .max((x, y) -> x.compareTo(y)).orElse(null);
            }

            // fallback to default
            if (result == null && input.getExamDateYearMonthDay() != null) {
                result = input.getExamDateYearMonthDay().toDateTimeAtMidnight();
            }
        }

        return result;
    }

    @Atomic
    static public void annul(final EnrolmentEvaluation evaluation) {
        if (!evaluation.isAnnuled()) {
            final Enrolment enrolment = evaluation.getEnrolment();

            evaluation.setEnrolmentEvaluationState(EnrolmentEvaluationState.ANNULED_OBJ);
            EnrolmentEvaluationServices.onStateChange(evaluation);
            EnrolmentServices.updateState(enrolment);
            CurriculumAggregatorServices.updateAggregatorEvaluation(evaluation);
        }
    }

    @Atomic
    static public void activate(final EnrolmentEvaluation evaluation) {
        if (evaluation.isAnnuled()) {
            final Enrolment enrolment = evaluation.getEnrolment();

            evaluation.setEnrolmentEvaluationState(EnrolmentEvaluationState.TEMPORARY_OBJ);
            EnrolmentEvaluationServices.onStateChange(evaluation);
            EnrolmentServices.updateState(enrolment);
            CurriculumAggregatorServices.updateAggregatorEvaluation(evaluation);
        }
    }

}
