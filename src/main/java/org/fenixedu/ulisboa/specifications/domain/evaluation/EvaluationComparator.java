package org.fenixedu.ulisboa.specifications.domain.evaluation;

import java.util.Comparator;

import org.fenixedu.academic.domain.DomainObjectUtil;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.curriculum.EnrollmentState;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices;

/*
 * Based on original EvaluationConfiguration.EnrolmentComparator
 * 
 *
 */
public class EvaluationComparator implements Comparator<EnrolmentEvaluation> {

    @Override
    public int compare(final EnrolmentEvaluation left, final EnrolmentEvaluation right) {

        if (hasConfirmedMarkSheet(left) && !hasConfirmedMarkSheet(right)) {

            if (!isSpecialAuthorization(right)) {
                return 1;
            }
        }

        if (!hasConfirmedMarkSheet(left) && hasConfirmedMarkSheet(right)) {

            if (!isSpecialAuthorization(left)) {
                return -1;
            }
        }

        return compareBySpecialAuthorizationAndStateAndGradeAndEvaluationDate(left, right);
    }

    static private int compareBySpecialAuthorizationAndStateAndGradeAndEvaluationDate(final EnrolmentEvaluation left,
            final EnrolmentEvaluation right) {

        if (isSpecialAuthorization(left) && !isSpecialAuthorization(right)) {

            if (!hasConfirmedMarkSheet(right)) {
                return 1;
            }
        }

        if (!isSpecialAuthorization(left) && isSpecialAuthorization(right)) {

            if (!hasConfirmedMarkSheet(left)) {
                return -1;
            }
        }

        return compareByStateAndGradeAndEvaluationDate(left, right);
    }

    static private boolean hasConfirmedMarkSheet(final EnrolmentEvaluation input) {
        return input != null && input.getCompetenceCourseMarkSheet() != null
                && input.getCompetenceCourseMarkSheet().isConfirmed();
    }

    static private boolean isSpecialAuthorization(final EnrolmentEvaluation input) {
        return input != null && input.getEvaluationSeason().isSpecialAuthorization();
    }

    static private int compareByStateAndGradeAndEvaluationDate(final EnrolmentEvaluation left, final EnrolmentEvaluation right) {
        final EnrollmentState leftEnrolmentState = left.getEnrollmentStateByGrade();
        final EnrollmentState rightEnrolmentState = right.getEnrollmentStateByGrade();

        if (leftEnrolmentState == EnrollmentState.APROVED && rightEnrolmentState == EnrollmentState.APROVED) {
            return compareByGradeAndEvaluationDate(left, right, true);
        } else if (leftEnrolmentState == EnrollmentState.APROVED) {
            return 1;
        } else if (rightEnrolmentState == EnrollmentState.APROVED) {
            return -1;
        } else {
            return compareByGradeAndEvaluationDate(left, right, false);
        }
    }

    static private int compareByGradeAndEvaluationDate(final EnrolmentEvaluation left, final EnrolmentEvaluation right,
            final boolean bothApproved) {
        int result = left.getGrade().compareTo(right.getGrade());
        if (result != 0) {
            return result;
        }

        if (bothApproved) {
            // TODO legidio, use EnrolmentEvaluationServices.getExamDateTime ?
            result = left.getExamDateYearMonthDay().compareTo(right.getExamDateYearMonthDay());
            if (result != 0) {
                return result;
            }
        }

        result = EvaluationSeasonServices.SEASON_ORDER_COMPARATOR.compare(left.getEvaluationSeason(),
                right.getEvaluationSeason());
        if (result != 0) {
            return result;
        }

        result = DomainObjectUtil.COMPARATOR_BY_ID.compare(left, right);
        if (result != 0) {
            return result;
        }

        return result;
    }

}
