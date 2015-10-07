package org.fenixedu.ulisboa.specifications.domain.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;

public class RegistrationServices {

    public static Set<SchoolClass> getSchoolClassesToEnrolBy(final Registration registration,
            final DegreeCurricularPlan degreeCurricularPlan, final ExecutionSemester executionSemester) {

        return registration.getAssociatedAttendsSet().stream()
                .filter(attends -> attends.getExecutionPeriod() == executionSemester)
                .flatMap(attends -> attends.getExecutionCourse().getSchoolClassesBy(degreeCurricularPlan).stream())
                .collect(Collectors.toSet());
    }

    public static Optional<SchoolClass> getSchoolClassBy(final Registration registration,
            final ExecutionSemester executionSemester) {
        return registration.getSchoolClassesSet().stream().filter(sc -> sc.getExecutionPeriod() == executionSemester).findFirst();
    }

    public static void replaceSchoolClass(final Registration registration, final SchoolClass schoolClass,
            final ExecutionSemester executionSemester) {
        final Optional<SchoolClass> currentSchoolClass = getSchoolClassBy(registration, executionSemester);
        if (currentSchoolClass.isPresent()) {
            currentSchoolClass.get().getAssociatedShiftsSet().forEach(s -> s.removeStudents(registration));
            registration.getSchoolClassesSet().remove(currentSchoolClass.get());
        }
        if (schoolClass != null) {
            final List<ExecutionCourse> attendingExecutionCourses =
                    registration.getAttendingExecutionCoursesFor(executionSemester);
            for (Shift shift : schoolClass.getAssociatedShiftsSet().stream()
                    .filter(s -> attendingExecutionCourses.contains(s.getExecutionCourse())).collect(Collectors.toSet())) {
                if (!shift.reserveForStudent(registration)) {
                    throw new DomainException("error.registration.replaceSchoolClass.shiftFull", shift.getNome(),
                            shift.getShiftTypesPrettyPrint(), shift.getExecutionCourse().getName());
                }
            }
            registration.getSchoolClassesSet().add(schoolClass);
        }
    }

}
