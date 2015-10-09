package org.fenixedu.ulisboa.specifications.task;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentPeriodInCurricularCoursesCandidate;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ShiftEnrolment;
import org.fenixedu.academic.domain.candidacy.CancelledCandidacySituation;
import org.fenixedu.academic.domain.candidacy.CandidacySituationType;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationStateType;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;
import org.fenixedu.ulisboa.specifications.domain.candidacy.FirstTimeCandidacy;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

@Task(englishTitle = "Cancel Out Of Date Candidacies", readOnly = true)
public class CancelOutOfDateCandidacies extends CronTask {
    @Override
    public void runTask() throws Exception {
        taskLog("Started looking for out of date FirstTimeCandidacies");
        cancelAllCandidaciesPastPeriods();
        taskLog("Finished looking for out of date FirstTimeCandidacies");
    }

    private static boolean isPeriodPast(EnrolmentPeriodInCurricularCoursesCandidate period) {
        return period.getEndDateDateTime().isBefore(new DateTime());
    }

    public void cancelAllCandidaciesPastPeriods() {
        for (DegreeCurricularPlan dcp : DegreeCurricularPlan.readBolonhaDegreeCurricularPlans()) {
            EnrolmentPeriodInCurricularCoursesCandidate period = FirstTimeCandidacyController.getCandidacyPeriod(dcp);
            if (period == null || !isPeriodPast(period)) {
                continue;
            }

            // For scalability, only clear the candidacies from the current, and last semesters
            ExecutionSemester thisSemester = ExecutionSemester.readActualExecutionSemester();
            ExecutionSemester lastSemester = thisSemester.getPreviousExecutionPeriod();
            if (!period.getExecutionPeriod().equals(thisSemester)
                    && !(lastSemester != null && period.getExecutionPeriod().equals(lastSemester))) {
                continue;
            }

            cancelAllCandidacies(dcp);
        }
    }

    private static final List<String> possibleManagers = Arrays.asList("manager", "admin", "qubit_admin", "qubIT_admin",
            "qubIt_admin");

    private User getPossibleManager() {
        for (String possibleManager : possibleManagers) {
            User manager = User.findByUsername(possibleManager);
            if (manager != null) {
                return manager;
            }
        }

        return null;
    }

    @Atomic
    private void cancelAllCandidacies(DegreeCurricularPlan dcp) {
        taskLog("Looking for out of date FirstTimeCandidacies in DCP: " + dcp.getName());
        Authenticate.mock(getPossibleManager());

        int count = 0;
        for (FirstTimeCandidacy candidacy : getAllFirstTimeCandidacies(dcp)) {
            if (candidacy.getActiveCandidacySituationType().equals(CandidacySituationType.REGISTERED)) {
                continue;
            }

            Registration registration = candidacy.getRegistration();
            if (registration != null) {
                for (Enrolment enrolment : registration.getEnrolments(candidacy.getExecutionYear())) {
                    enrolment.delete();
                }

                for (ShiftEnrolment shiftEnrolment : registration.getShiftEnrolmentsSet()) {
                    shiftEnrolment.delete();
                }
                registration.getShiftsSet().clear();

                if (registration.getActiveState().getStateType().equals(RegistrationStateType.INACTIVE)) {
                    RegistrationState registeredState =
                            RegistrationState.createRegistrationState(registration, AccessControl.getPerson(), new DateTime(),
                                    RegistrationStateType.REGISTERED);
                    registeredState.setStateDate(registeredState.getStateDate().minusMinutes(1));
                }
                RegistrationState.createRegistrationState(registration, AccessControl.getPerson(), new DateTime(),
                        RegistrationStateType.CANCELED);
            }

            new CancelledCandidacySituation(candidacy);
            count++;
        }
        if (count > 0) {
            taskLog("Successfully cancelled " + count + " FirstTimeCandidacies.");
        }
    }

    private static Set<FirstTimeCandidacy> getAllFirstTimeCandidacies(DegreeCurricularPlan dcp) {
        Set<FirstTimeCandidacy> candidacies = new HashSet<>();
        for (ExecutionDegree degree : dcp.getExecutionDegreesSet()) {
            for (StudentCandidacy candidacy : degree.getStudentCandidaciesSet()) {
                if (candidacy instanceof FirstTimeCandidacy) {
                    candidacies.add((FirstTimeCandidacy) candidacy);
                }
            }
        }
        return candidacies;
    }
}
