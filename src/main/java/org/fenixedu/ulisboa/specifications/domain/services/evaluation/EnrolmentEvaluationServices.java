package org.fenixedu.ulisboa.specifications.domain.services.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.ulisboa.specifications.domain.evaluation.EnrolmentEvaluationExtendedInformation;

public class EnrolmentEvaluationServices {

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

}
