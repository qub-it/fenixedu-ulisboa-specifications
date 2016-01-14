package org.fenixedu.ulisboa.specifications.ui.blue_record.authentication;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.ulisboa.specifications.authentication.IULisboaRedirectionHandler;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance;
import org.fenixedu.ulisboa.specifications.ui.blue_record.BlueRecordEntryPoint;
import org.fenixedu.ulisboa.specifications.ui.blue_record.DisabilitiesFormControllerBlueRecord;
import org.fenixedu.ulisboa.specifications.ui.blue_record.MotivationsExpectationsFormControllerBlueRecord;
import org.fenixedu.ulisboa.specifications.ui.blue_record.OriginInformationFormControllerBlueRecord;
import org.fenixedu.ulisboa.specifications.ui.blue_record.PersonalInformationFormControllerBlueRecord;
import org.fenixedu.ulisboa.specifications.ui.blue_record.PreviousDegreeOriginInformationFormControllerBlueRecord;

public class BlueRecordRedirector implements IULisboaRedirectionHandler {

    @Override
    public boolean isToRedirect(final User user, final HttpServletRequest request) {
        if(user == null) {
            return false;
        }
        
        if(user.getPerson() == null) {
            return false;
        }
        
        if(user.getPerson().getStudent() == null) {
            return false;
        }
        
        if(RaidesInstance.getInstance() == null) {
            return false;
        }
        
        if(!RaidesInstance.getInstance().getFormsAvailableToStudents()) {
            return false;
        }
        
        if(!hasSomeBlueRecordFormToFill(user.getPerson().getStudent())) {
            return false;
        }
        
        return !Raides.findActiveRegistrationsWithEnrolments(AccessControl.getPerson().getStudent()).isEmpty();
    }

    private boolean hasSomeBlueRecordFormToFill(final Student student) {
        boolean result = false;
        result |= !new DisabilitiesFormControllerBlueRecord().isFormIsFilled(student);
        result |= !new MotivationsExpectationsFormControllerBlueRecord().isFormIsFilled(student);
        result |= !new OriginInformationFormControllerBlueRecord().isFormIsFilled(student);
        result |= !new PersonalInformationFormControllerBlueRecord().isFormIsFilled(student);
        result |= !new PreviousDegreeOriginInformationFormControllerBlueRecord().isFormIsFilled(student);
        
        return result;
    }

    @Override
    public String redirectionPath(final User user, final HttpServletRequest request) {
        return BlueRecordEntryPoint.CONTROLLER_URL;
    }

}
