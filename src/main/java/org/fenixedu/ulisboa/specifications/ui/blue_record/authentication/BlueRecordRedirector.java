package org.fenixedu.ulisboa.specifications.ui.blue_record.authentication;

import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.ulisboa.specifications.authentication.IULisboaRedirectionHandler;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesFormPeriod;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance;
import org.fenixedu.ulisboa.specifications.ui.blue_record.BlueRecordEntryPoint;
import org.fenixedu.ulisboa.specifications.ui.blue_record.CgdDataAuthorizationControllerBlueRecord;
import org.fenixedu.ulisboa.specifications.ui.blue_record.DisabilitiesFormControllerBlueRecord;
import org.fenixedu.ulisboa.specifications.ui.blue_record.HouseholdInformationFormControllerBlueRecord;
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
        
        for(final RaidesFormPeriod period : RaidesFormPeriod.findActive().collect(Collectors.toSet())) {
            if(!hasSomeBlueRecordFormToFill(period.getExecutionYear(), user.getPerson().getStudent())) {
                continue;
            }
            
            if(!Raides.findActiveFirstTimeRegistrationsOrWithEnrolments(period.getExecutionYear(), AccessControl.getPerson().getStudent()).isEmpty()) {
                return true;
            }
        }
        
        return false;
    }

    private boolean hasSomeBlueRecordFormToFill(final ExecutionYear executionYear, final Student student) {
        boolean result = false;
        result |= !new DisabilitiesFormControllerBlueRecord().isFormIsFilled(executionYear, student);
        result |= !new HouseholdInformationFormControllerBlueRecord().isFormIsFilled(executionYear, student);
        result |= !new MotivationsExpectationsFormControllerBlueRecord().isFormIsFilled(executionYear, student);
        result |= !new OriginInformationFormControllerBlueRecord().isFormIsFilled(executionYear, student);
        result |= !new PersonalInformationFormControllerBlueRecord().isFormIsFilled(executionYear, student);
        result |= !new PreviousDegreeOriginInformationFormControllerBlueRecord().isFormIsFilled(executionYear, student);
        result |= !new CgdDataAuthorizationControllerBlueRecord().isFormIsFilled(executionYear, student);
        
        return result;
    }

    @Override
    public String redirectionPath(final User user, final HttpServletRequest request) {
        for(final RaidesFormPeriod period : RaidesFormPeriod.findActive().collect(Collectors.toSet())) {
            if(!hasSomeBlueRecordFormToFill(period.getExecutionYear(), user.getPerson().getStudent())) {
                continue;
            }
            
            if(!Raides.findActiveFirstTimeRegistrationsOrWithEnrolments(period.getExecutionYear(), AccessControl.getPerson().getStudent()).isEmpty()) {
                return BlueRecordEntryPoint.CONTROLLER_URL + "/" + period.getExecutionYear().getExternalId();
            }
        }
        
        throw new RuntimeException("error");
    }

}
