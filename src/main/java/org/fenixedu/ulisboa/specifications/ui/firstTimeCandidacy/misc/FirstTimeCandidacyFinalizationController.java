package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.misc;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyAbstractController;
import org.springframework.ui.Model;

public class FirstTimeCandidacyFinalizationController extends FirstTimeCandidacyAbstractController {

    @Override
    public boolean isFormIsFilled(ExecutionYear executionYear, Student student) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected Student getStudent(Model model) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getControllerURL() {
        // TODO Auto-generated method stub
        return null;
    }

}
