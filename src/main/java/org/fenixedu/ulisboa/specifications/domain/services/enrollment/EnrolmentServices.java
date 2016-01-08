package org.fenixedu.ulisboa.specifications.domain.services.enrollment;

import java.util.Collection;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.curriculum.EnrollmentState;

public class EnrolmentServices {

    static public Collection<Shift> getShiftsFor(final Enrolment enrolment) {
        return enrolment.getRegistration()
                .getShiftEnrolmentsSet().stream().filter(s -> s.getShift().getExecutionCourse()
                        .getAssociatedCurricularCoursesSet().contains(enrolment.getCurricularCourse()))
                .map(s -> s.getShift()).collect(Collectors.toSet());
    }

    static public boolean containsAnyShift(final Enrolment enrolment, final Collection<Shift> shifts) {
        return getShiftsFor(enrolment).stream().anyMatch(s -> shifts.contains(s));
    }

    static public String getShiftsDescription(final Enrolment enrolment) {
        return getShiftsFor(enrolment).stream().map(s -> s.getNome()).collect(Collectors.joining(", "));
    }

    static public EnrollmentState calculateState(final Enrolment enrolment) {
        final Grade finalGrade = enrolment.getGrade();
        return finalGrade.isEmpty() ? EnrollmentState.ENROLLED : finalGrade.getEnrolmentState();
    }

    static public void updateState(final Enrolment enrolment) {
        enrolment.setEnrollmentState(calculateState(enrolment));
    }

}
