package org.fenixedu.ulisboa.specifications.task;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;
import org.fenixedu.ulisboa.specifications.domain.candidacy.FirstTimeCandidacy;
import org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod.AcademicEnrolmentPeriod;
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
            Set<AcademicEnrolmentPeriod> pastPeriods = getPastCandidacyPeriods(dcp);
            if (pastPeriods.isEmpty()) {
                continue;
            }
            DateTime mostRecentEndDate = null;
            for (AcademicEnrolmentPeriod period : pastPeriods) {
                if (mostRecentEndDate == null) {
                    mostRecentEndDate = period.getEndDate();
                } else if (mostRecentEndDate.isBefore(period.getEndDate())) {
                    mostRecentEndDate = period.getEndDate();
                }
            }

            cancelAllCandidaciesBefore(mostRecentEndDate, dcp);
        }
    }

    private static Set<AcademicEnrolmentPeriod> getPastCandidacyPeriods(DegreeCurricularPlan dcp) {
        Predicate<AcademicEnrolmentPeriod> isForCandidates = ep -> ep.getFirstTimeRegistration();
        Predicate<AcademicEnrolmentPeriod> isPast = ep -> ep.getEndDate().isBefore(new DateTime());
        Stream<AcademicEnrolmentPeriod> periods =
                dcp.getAcademicEnrolmentPeriodsSet().stream().filter(isForCandidates).filter(isPast);
        return periods.collect(Collectors.toSet());
    }

    private static final List<String> possibleManagers =
            Arrays.asList("manager", "admin", "qubit_admin", "qubIT_admin", "qubIt_admin");

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

            if (candidacy.cancelCandidacy()) {
                count++;
            }
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
