package org.fenixedu.ulisboa.specifications.domain.evaluation;

import java.util.Comparator;
import java.util.Date;

import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.curriculum.EnrollmentState;
import org.fenixedu.academic.domain.curriculum.EnrolmentEvaluationContext;

/*
 * Based on original EvaluationConfiguration.EnrolmentComparator
 * 
 *
 */
public class EvaluationComparator implements Comparator<EnrolmentEvaluation> {
    @Override
    public int compare(EnrolmentEvaluation o1, EnrolmentEvaluation o2) {
        if (o1.getEnrolment().getStudentCurricularPlan().getDegreeType().isPreBolonhaMasterDegree()) {
            return compareMyWhenAlteredDateToAnotherWhenAlteredDate(o1.getWhen(), o2.getWhen());
        }

        if (isInCurriculumValidationContextAndIsFinal(o1) && !isInCurriculumValidationContextAndIsFinal(o2)) {
            return 1;
        } else if (!isInCurriculumValidationContextAndIsFinal(o1) && isInCurriculumValidationContextAndIsFinal(o2)) {
            return -1;
        } else if (isInCurriculumValidationContextAndIsFinal(o1) && isInCurriculumValidationContextAndIsFinal(o2)) {
            return compareMyWhenAlteredDateToAnotherWhenAlteredDate(o1.getWhen(), o2.getWhen());
        } else if (o1.getEvaluationSeason().equals(o2.getEvaluationSeason())) {
            if ((o1.isRectification() && o2.isRectification()) || (o1.isRectified() && o2.isRectified())) {
                return compareMyWhenAlteredDateToAnotherWhenAlteredDate(o1.getWhen(), o2.getWhen());
            }
            if (o1.isRectification()) {
                return 1;
            }
            if (o2.isRectification()) {
                return -1;
            }
            return compareByStateAndGradeAndEvaluationDateAndSpecialAuthorization(o1, o2);

        } else {
            return compareByStateAndGradeAndEvaluationDateAndSpecialAuthorization(o1, o2);
        }
    }

    private int compareByStateAndGradeAndEvaluationDateAndSpecialAuthorization(EnrolmentEvaluation left, EnrolmentEvaluation right) {
        EnrollmentState leftEnrolmentState = left.getEnrollmentStateByGrade();
        EnrollmentState rightEnrolmentState = right.getEnrollmentStateByGrade();

        if (leftEnrolmentState == EnrollmentState.APROVED && rightEnrolmentState == EnrollmentState.APROVED) {
            return compareByGradeAndEvaluationDateAndSpecialAuthorization(left, right);
        } else if (leftEnrolmentState == EnrollmentState.APROVED) {
            return 1;
        } else if (rightEnrolmentState == EnrollmentState.APROVED) {
            return -1;
        } else {
            return compareByGradeAndEvaluationDateAndSpecialAuthorization(left, right);
        }

    }

    protected int compareByGradeAndEvaluationDateAndSpecialAuthorization(EnrolmentEvaluation left, EnrolmentEvaluation right) {
        int result = left.getGrade().compareTo(right.getGrade());
        if (result != 0) {
            return result;
        }

        result = left.getExamDateYearMonthDay().compareTo(right.getExamDateYearMonthDay());
        if (result != 0) {
            return result;
        }

        if (left.getEvaluationSeason().isSpecialAuthorization() && right.getEvaluationSeason().isSpecialAuthorization()) {
            return 0;
        }

        if (left.getEvaluationSeason().isSpecialAuthorization()) {
            return 1;
        }

        if (right.getEvaluationSeason().isSpecialAuthorization()) {
            return -1;
        }

        return result;
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

    public boolean isInCurriculumValidationContextAndIsFinal(EnrolmentEvaluation enrolmentEvaluation) {
        return enrolmentEvaluation.getContext() != null
                && enrolmentEvaluation.getContext().equals(EnrolmentEvaluationContext.CURRICULUM_VALIDATION_EVALUATION)
                && enrolmentEvaluation.isFinal();
    }
}