package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.misc;

import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyAbstractController;
import org.springframework.ui.Model;

public class FirstTimeCandidacyFinalizationController extends FirstTimeCandidacyAbstractController {
    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/finalization";

    public static final String WITH_MODEL_URL = "";
    public static final String WITHOUT_MODEL_URL = "";

    @Override
    public boolean isFormIsFilled(ExecutionYear executionYear, Student student) {
        throw new RuntimeException("Error you should not call this method.");
    }

    @Override
    protected Student getStudent(Model model) {
        return AccessControl.getPerson().getStudent();
    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

}
