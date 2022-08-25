package org.fenixedu.ulisboa.specifications.domain.services.enrollment.shift;

import java.util.ArrayList;
import java.util.List;

import org.fenixedu.academic.domain.Attends;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.ShiftType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.treasury.TreasuryBridgeAPIFactory;
import org.fenixedu.academic.dto.ShiftToEnrol;
import org.fenixedu.academic.service.services.exceptions.FenixServiceException;
import org.joda.time.LocalDate;

public class ReadShiftsToEnroll {

    public static List<ShiftToEnrol> readWithStudentRestrictionsForShiftsEnrolments(Registration registration,
            ExecutionInterval executionSemester) throws FenixServiceException {

        checkStudentRestrictionsForShiftsEnrolments(registration, executionSemester);
        return read(registration, executionSemester);
    }

    public static List<ShiftToEnrol> read(Registration registration, ExecutionInterval executionSemester)
            throws FenixServiceException {

//        ClassEnrollmentAuthorizationFilter.instance.execute(registration, executionSemester);

        final List<ShiftToEnrol> result = new ArrayList<ShiftToEnrol>();
        for (final Attends attends : registration.readAttendsByExecutionPeriod(executionSemester)) {
            result.add(buildShiftToEnrol(attends));
        }
        return result;
    }

    private static void checkStudentRestrictionsForShiftsEnrolments(Registration registration,
            ExecutionInterval executionSemester) throws FenixServiceException {
        if (registration == null) {
            throw new FenixServiceException("errors.impossible.operation");
        }

//        if (executionSemester.getExecutionYear().getFirstExecutionPeriod() == executionSemester
//                && TreasuryBridgeAPIFactory.implementation().isAcademicalActsBlocked(registration.getPerson(), new LocalDate())) {
//            if (!registration.getInterruptedStudies()) {
//                throw new FenixServiceException("error.exception.notAuthorized.student.warningTuition");
//            }
//        }
//
//        if (registration.getFlunked()) {
//            throw new FenixServiceException("error.exception.notAuthorized.student.warningTuition");
//        }
    }

    private static ShiftToEnrol buildShiftToEnrol(Attends attends) {

        final ShiftToEnrol result = new ShiftToEnrol();

        findShiftTypesFromExecutionCourse(attends, result);
        findShiftsForExecutionCourseShiftTypesFromStudentEnroledShifts(attends, result);

        result.setExecutionCourse(attends.getExecutionCourse());
        result.setEnrolled(attends.getEnrolment() != null);

        return result;
    }

    private static void findShiftsForExecutionCourseShiftTypesFromStudentEnroledShifts(Attends attend, ShiftToEnrol result) {
        for (final Shift shift : attend.getRegistration().getShiftsSet()) {
            setShiftInformation(attend, result, shift);
        }
    }

    private static void findShiftTypesFromExecutionCourse(Attends attend, ShiftToEnrol result) {
        for (final Shift shift : attend.getExecutionCourse().getAssociatedShifts()) {
            setShiftTypeInformation(result, shift);
        }
    }

    private static void setShiftTypeInformation(ShiftToEnrol result, final Shift shift) {

        if (shift.containsType(ShiftType.TEORICA)) {
            result.setTheoricType(ShiftType.TEORICA);

//        } else if (shift.containsType(ShiftType.PRATICA)) {
//            result.setPraticType(ShiftType.PRATICA);

        } else if (shift.containsType(ShiftType.LABORATORIAL)) {
            result.setLaboratoryType(ShiftType.LABORATORIAL);

//        } else if (shift.containsType(ShiftType.TEORICO_PRATICA)) {
//            result.setTheoricoPraticType(ShiftType.TEORICO_PRATICA);

        } else if (shift.containsType(ShiftType.FIELD_WORK)) {
            result.setFieldWorkType(ShiftType.FIELD_WORK);

        } else if (shift.containsType(ShiftType.PROBLEMS)) {
            result.setProblemsType(ShiftType.PROBLEMS);

        } else if (shift.containsType(ShiftType.SEMINARY)) {
            result.setSeminaryType(ShiftType.SEMINARY);

        } else if (shift.containsType(ShiftType.TRAINING_PERIOD)) {
            result.setTrainingType(ShiftType.TRAINING_PERIOD);

        } else if (shift.containsType(ShiftType.TUTORIAL_ORIENTATION)) {
            result.setTutorialOrientationType(ShiftType.TUTORIAL_ORIENTATION);

        } else if (shift.containsType(ShiftType.OTHER)) {
            result.setOtherType(ShiftType.OTHER);
        }
    }

    private static void setShiftInformation(Attends attend, ShiftToEnrol result, final Shift shift) {

        if (shift.getExecutionCourse() == attend.getExecutionCourse() && shift.containsType(ShiftType.TEORICA)) {
            result.setTheoricShift(shift);

//        } else if (shift.getExecutionCourse() == attend.getExecutionCourse() && shift.containsType(ShiftType.PRATICA)) {
//            result.setPraticShift(shift);

        } else if (shift.getExecutionCourse() == attend.getExecutionCourse() && shift.containsType(ShiftType.LABORATORIAL)) {
            result.setLaboratoryShift(shift);

//        } else if (shift.getExecutionCourse() == attend.getExecutionCourse() && shift.containsType(ShiftType.TEORICO_PRATICA)) {
//            result.setTheoricoPraticShift(shift);

        } else if (shift.getExecutionCourse() == attend.getExecutionCourse() && shift.containsType(ShiftType.FIELD_WORK)) {
            result.setFieldWorkShift(shift);

        } else if (shift.getExecutionCourse() == attend.getExecutionCourse() && shift.containsType(ShiftType.PROBLEMS)) {
            result.setProblemsShift(shift);

        } else if (shift.getExecutionCourse() == attend.getExecutionCourse() && shift.containsType(ShiftType.SEMINARY)) {
            result.setSeminaryShift(shift);

        } else if (shift.getExecutionCourse() == attend.getExecutionCourse() && shift.containsType(ShiftType.TRAINING_PERIOD)) {
            result.setTrainingShift(shift);

        } else if (shift.getExecutionCourse() == attend.getExecutionCourse()
                && shift.containsType(ShiftType.TUTORIAL_ORIENTATION)) {
            result.setTutorialOrientationShift(shift);

        } else if (shift.getExecutionCourse() == attend.getExecutionCourse() && shift.containsType(ShiftType.OTHER)) {
            result.setOtherShift(shift);
        }
    }
}