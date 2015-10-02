package org.fenixedu.ulisboa.specifications.task;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentPeriod;
import org.fenixedu.academic.domain.EnrolmentPeriodInCurricularCoursesCandidate;
import org.fenixedu.academic.domain.ExecutionDegree;
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

    private void cancelAllCandidaciesPastPeriods() {
        for (DegreeCurricularPlan dcp : DegreeCurricularPlan.readBolonhaDegreeCurricularPlans()) {
            Set<EnrolmentPeriod> pastPeriods = getPastCandidacyPeriods(dcp);
            if (pastPeriods.isEmpty()) {
                continue;
            }
            DateTime mostRecentEndDate = null;
            for (EnrolmentPeriod period : pastPeriods) {
                if (mostRecentEndDate == null) {
                    mostRecentEndDate = period.getEndDateDateTime();
                } else if (mostRecentEndDate.isBefore(period.getEndDateDateTime())) {
                    mostRecentEndDate = period.getEndDateDateTime();
                }
            }

            cancelAllCandidaciesBefore(mostRecentEndDate, dcp);
        }
    }

    private static Set<EnrolmentPeriod> getPastCandidacyPeriods(DegreeCurricularPlan dcp) {
        Predicate<EnrolmentPeriod> isForCandidates = ep -> ep instanceof EnrolmentPeriodInCurricularCoursesCandidate;
        Predicate<EnrolmentPeriod> isPast = ep -> ep.getEndDateDateTime().isBefore(new DateTime());
        Stream<EnrolmentPeriod> periods = dcp.getEnrolmentPeriodsSet().stream().filter(isForCandidates).filter(isPast);
        return periods.collect(Collectors.toSet());
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
    private void cancelAllCandidaciesBefore(DateTime endDate, DegreeCurricularPlan dcp) {
        taskLog("Looking for out of date FirstTimeCandidacies in DCP: " + dcp.getName() + " before: " + endDate);
        Authenticate.mock(getPossibleManager());

        int count = 0;
        for (FirstTimeCandidacy candidacy : getAllFirstTimeCandidacies(dcp)) {
            if (!candidacy.getCandidacyDate().isBefore(endDate)) {
                continue;
            }

            if (candidacy.getActiveCandidacySituationType().equals(CandidacySituationType.REGISTERED)
                    || candidacy.getActiveCandidacySituationType().equals(CandidacySituationType.CANCELLED)) {
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
                if (!registration.getActiveState().getStateType().equals(RegistrationStateType.CANCELED)) {
                    RegistrationState.createRegistrationState(registration, AccessControl.getPerson(), new DateTime(),
                            RegistrationStateType.CANCELED);
                }
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
