package org.fenixedu.ulisboa.specifications.domain.services.evaluation;

import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.ulisboa.specifications.domain.evaluation.EnrolmentEvaluationExtendedInformation;

public class EnrolmentEvaluationServices {

    static public void setRemarks(final EnrolmentEvaluation enrolmentEvaluation, final String remarks) {
        EnrolmentEvaluationExtendedInformation.findOrCreate(enrolmentEvaluation).setRemarks(remarks);
    }

    static public String getRemarks(final EnrolmentEvaluation enrolmentEvaluation) {
        return enrolmentEvaluation.getExtendedInformation() == null ? null : enrolmentEvaluation.getExtendedInformation()
                .getRemarks();
    }

}
