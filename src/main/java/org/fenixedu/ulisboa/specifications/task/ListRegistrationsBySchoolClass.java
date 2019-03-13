/**
 * 
 */
package org.fenixedu.ulisboa.specifications.task;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;

/**
 * @author shezad - Aug 31, 2015
 *
 */
@Task(englishTitle = "List Students enroled in each SchoolClass", readOnly = true)
public class ListRegistrationsBySchoolClass extends CronTask {

    @Override
    public void runTask() throws Exception {

        ExecutionYear.findCurrents().stream().flatMap(year -> year.getExecutionPeriodsSet().stream())
                .forEach(executionSemester -> {
                    taskLog("\n== %s ==\n\n", executionSemester.getQualifiedName());

                    for (final SchoolClass schoolClass : executionSemester.getSchoolClassesSet()) {
                        taskLog("[%s - %dA] %s - %d STUDENTS (OID: %s)\n", schoolClass.getEditablePartOfName(),
                                schoolClass.getAnoCurricular(), schoolClass.getExecutionDegree().getPresentationName(),
                                schoolClass.getRegistrationsSet().size(), schoolClass.getExternalId());

                        for (Registration registration : schoolClass.getRegistrationsSet()) {
                            final Student student = registration.getStudent();
                            taskLog("_ _ _ [%d] %s (OID: %s)\n", student.getNumber(), student.getName(),
                                    registration.getExternalId());
                        }
                    }
                });

    }

}
