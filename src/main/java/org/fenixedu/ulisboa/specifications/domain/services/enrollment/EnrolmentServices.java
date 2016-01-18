package org.fenixedu.ulisboa.specifications.domain.services.enrollment;

import java.util.Collection;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.curriculum.EnrollmentState;

public class EnrolmentServices {

    static public Collection<Shift> getShiftsFor(final Enrolment enrolment, final ExecutionSemester executionSemester) {
        return enrolment.getRegistration().getShiftsFor(enrolment.getExecutionCourseFor(executionSemester));
    }

    static public boolean containsAnyShift(final Enrolment enrolment, final ExecutionSemester executionSemester,
            final Collection<Shift> shifts) {
        return getShiftsFor(enrolment, executionSemester).stream().anyMatch(s -> shifts.contains(s));
    }

    static public String getShiftsDescription(final Enrolment enrolment, final ExecutionSemester executionSemester) {
        return getShiftsFor(enrolment, executionSemester).stream().map(s -> s.getNome()).collect(Collectors.joining(", "));
    }

    static public EnrollmentState calculateState(final Enrolment enrolment) {
        final Grade finalGrade = enrolment.getGrade();
        return finalGrade.isEmpty() ? EnrollmentState.ENROLLED : finalGrade.getEnrolmentState();
    }

    static public void updateState(final Enrolment enrolment) {
        enrolment.setEnrollmentState(calculateState(enrolment));
    }

}
