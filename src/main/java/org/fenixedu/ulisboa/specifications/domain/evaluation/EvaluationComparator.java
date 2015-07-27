package org.fenixedu.ulisboa.specifications.domain.evaluation;

import java.util.Comparator;
import java.util.Date;

import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.curriculum.EnrollmentState;

/*
 * Based on original EvaluationConfiguration.EnrolmentComparator
 * 
 *
 */
public class EvaluationComparator implements Comparator<EnrolmentEvaluation> {
    @Override
    public int compare(EnrolmentEvaluation o1, EnrolmentEvaluation o2) {

        if (!o1.hasConfirmedMarkSheet() && !o2.hasConfirmedMarkSheet()) {
            return compareBySpecialAuthorizationAndStateAndGradeAndEvaluationDate(o1, o2);
        }

        if (o1.getEvaluationSeason().equals(o2.getEvaluationSeason())) {

            if ((o1.isRectification() && o2.isRectification()) || (o1.isRectified() && o2.isRectified())) {
                return compareMyWhenAlteredDateToAnotherWhenAlteredDate(o1.getWhen(), o2.getWhen());
            }

            if (o1.isRectification()) {
                return 1;
            }

            if (o2.isRectification()) {
                return -1;
            }

        }

        return compareByStateAndGradeAndEvaluationDate(o1, o2);

    }

    private int compareMyWhenAlteredDateToAnotherWhenAlteredDate(Date when1, Date whenAltered) {
        if (when1 == null) {
            return -1;
        }
        if (whenAltered == null) {
            return 1;
        }

        return when1.compareTo(whenAltered);

    }

    private int compareBySpecialAuthorizationAndStateAndGradeAndEvaluationDate(EnrolmentEvaluation left, EnrolmentEvaluation right) {

        if (left.getEvaluationSeason().isSpecialAuthorization() && !right.getEvaluationSeason().isSpecialAuthorization()) {
            return 1;
        }

        if (right.getEvaluationSeason().isSpecialAuthorization() && !left.getEvaluationSeason().isSpecialAuthorization()) {
            return -1;
        }

        return compareByStateAndGradeAndEvaluationDate(left, right);

    }

    protected int compareByStateAndGradeAndEvaluationDate(EnrolmentEvaluation left, EnrolmentEvaluation right) {
        final EnrollmentState leftEnrolmentState = left.getEnrollmentStateByGrade();
        final EnrollmentState rightEnrolmentState = right.getEnrollmentStateByGrade();

        if (leftEnrolmentState == EnrollmentState.APROVED && rightEnrolmentState == EnrollmentState.APROVED) {
            return compareByGradeAndEvaluationDate(left, right);
        } else if (leftEnrolmentState == EnrollmentState.APROVED) {
            return 1;
        } else if (rightEnrolmentState == EnrollmentState.APROVED) {
            return -1;
        } else {
            return compareByGradeAndEvaluationDate(left, right);
        }
    }

    protected int compareByGradeAndEvaluationDate(EnrolmentEvaluation left, EnrolmentEvaluation right) {
        int result = left.getGrade().compareTo(right.getGrade());
        if (result != 0) {
            return result;
        }

        result = left.getExamDateYearMonthDay().compareTo(right.getExamDateYearMonthDay());
        if (result != 0) {
            return result;
        }

        return result;
    }

}