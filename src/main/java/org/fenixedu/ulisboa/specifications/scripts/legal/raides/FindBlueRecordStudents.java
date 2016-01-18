package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance;
import org.fenixedu.ulisboa.specifications.ui.blue_record.DisabilitiesFormControllerBlueRecord;
import org.fenixedu.ulisboa.specifications.ui.blue_record.HouseholdInformationFormControllerBlueRecord;
import org.fenixedu.ulisboa.specifications.ui.blue_record.MotivationsExpectationsFormControllerBlueRecord;
import org.fenixedu.ulisboa.specifications.ui.blue_record.OriginInformationFormControllerBlueRecord;
import org.fenixedu.ulisboa.specifications.ui.blue_record.PersonalInformationFormControllerBlueRecord;
import org.fenixedu.ulisboa.specifications.ui.blue_record.PreviousDegreeOriginInformationFormControllerBlueRecord;

public class FindBlueRecordStudents extends CustomTask {

    @Override
    public void runTask() throws Exception {
        int p = 0;
        for (User user : Bennu.getInstance().getUserSet()) {
            try {
                
                if(user == null) {
                    continue;
                }
                
                if(user.getPerson() == null) {
                    continue;
                }
                
                if(user.getPerson().getStudent() == null) {
                    continue;
                }
                
                if(RaidesInstance.getInstance() == null) {
                    continue;
                }
                
                if(!RaidesInstance.getInstance().getFormsAvailableToStudents()) {
                    continue;
                }
                
                if(!hasSomeBlueRecordFormToFill(user.getPerson().getStudent())) {
                    continue;
                }
                
                if(Raides.findActiveRegistrationsWithEnrolments(user.getPerson().getStudent()).isEmpty()) {
                    continue;
                }
                
                printFillFormForStudent(user.getPerson().getStudent());
            } catch(NullPointerException e) {
                p++;
            }
            
            taskLog("P: %s\n", p);
        }
    }

    private boolean hasSomeBlueRecordFormToFill(final Student student) {
        boolean result = false;
        result |= !new DisabilitiesFormControllerBlueRecord().isFormIsFilled(student);
        result |= !new HouseholdInformationFormControllerBlueRecord().isFormIsFilled(student);
        result |= !new MotivationsExpectationsFormControllerBlueRecord().isFormIsFilled(student);
        result |= !new OriginInformationFormControllerBlueRecord().isFormIsFilled(student);
        result |= !new PersonalInformationFormControllerBlueRecord().isFormIsFilled(student);
        result |= !new PreviousDegreeOriginInformationFormControllerBlueRecord().isFormIsFilled(student);

        return result;
    }

    private void printFillFormForStudent(final Student student) {
        final Integer studentNumber = student.getNumber();
        final String username = student.getPerson().getUser().getUsername();

        final boolean D = !new DisabilitiesFormControllerBlueRecord().isFormIsFilled(student);
        final boolean H = !new HouseholdInformationFormControllerBlueRecord().isFormIsFilled(student);
        final boolean M = !new MotivationsExpectationsFormControllerBlueRecord().isFormIsFilled(student);
        final boolean O = !new OriginInformationFormControllerBlueRecord().isFormIsFilled(student);
        final boolean Pers = !new PersonalInformationFormControllerBlueRecord().isFormIsFilled(student);
        final boolean Prev = !new PreviousDegreeOriginInformationFormControllerBlueRecord().isFormIsFilled(student);

        taskLog("%s;%s;D:%s;H:%s;M:%s;O:%s;Pers:%s;Prev:%s\n", studentNumber, username, D, H, M, O, Pers, Prev);
    }
}
