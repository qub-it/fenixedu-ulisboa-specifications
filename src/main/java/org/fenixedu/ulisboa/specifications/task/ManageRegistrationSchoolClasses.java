/**
 * 
 */
package org.fenixedu.ulisboa.specifications.task;

import java.util.Optional;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationServices;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.scheduler.custom.CustomTask;

import pt.ist.fenixframework.FenixFramework;

/**
 * @author shezad - Aug 31, 2015
 *
 */
public class ManageRegistrationSchoolClasses extends CustomTask {

    @Override
    public void runTask() throws Exception {

        final Registration registration = FenixFramework.getDomainObject("REGISTRATION OID");
//        final SchoolClass schoolClass = FenixFramework.getDomainObject("SCHOOL_CLASS OID");
        final SchoolClass schoolClass = null;
        final ExecutionSemester executionSemester =
                schoolClass != null ? schoolClass.getExecutionPeriod() : FenixFramework.getDomainObject("SEMESTER OID");

        if (registration == null) {
            taskLog("Error: unable to detect registration");
            return;
        }

        final Student student = registration.getStudent();
        taskLog("[%d] %s\n", student.getNumber(), student.getName());

        if (executionSemester != null) {
            final Optional<SchoolClass> oldSchoolClassOptional = RegistrationServices.getSchoolClassBy(registration, executionSemester);
            if (oldSchoolClassOptional.isPresent()) {
                final SchoolClass oldSchoolClass = oldSchoolClassOptional.get();
                taskLog("Old SchoolClass: [%s - %dA] %s\n", oldSchoolClass.getEditablePartOfName(),
                        oldSchoolClass.getAnoCurricular(), oldSchoolClass.getExecutionDegree().getPresentationName());
            }
            RegistrationServices.replaceSchoolClass(registration, schoolClass, executionSemester);
            if (schoolClass != null) {
                taskLog("New SchoolClass: [%s - %dA] %s\n", schoolClass.getEditablePartOfName(), schoolClass.getAnoCurricular(),
                        schoolClass.getExecutionDegree().getPresentationName());
            } else {
                taskLog("New SchoolClass: EMPTY\n");
            }

        } else {
            taskLog("Error: unable to detect execution semester");
        }

    }

}
