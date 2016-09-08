package org.fenixedu.ulisboa.specifications.domain;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.ulisboa.specifications.domain.candidacy.FirstTimeCandidacy;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;

import pt.ist.fenixframework.Atomic;

public class FirstYearRegistrationConfiguration extends FirstYearRegistrationConfiguration_Base {

    public FirstYearRegistrationConfiguration(Degree degree, ExecutionYear executionYear,
            DegreeCurricularPlan degreeCurricularPlan, boolean requiresVaccination, boolean automaticEnrolment) {
        super();
        setGlobalConfiguration(FirstYearRegistrationGlobalConfiguration.getInstance());
        setDegree(degree);
        setExecutionYear(executionYear);
        setDegreeCurricularPlan(degreeCurricularPlan);
        setRequiresVaccination(requiresVaccination);
        setAutomaticEnrolment(automaticEnrolment);
    }

    @Atomic
    public void delete() {
        setDegree(null);
        setDegreeCurricularPlan(null);
        setExecutionYear(null);
        setGlobalConfiguration(null);
        super.deleteDomainObject();
    }

    @Atomic
    public void edit(final ExecutionYear executionYear, final DegreeCurricularPlan degreeCurricularPlan,
            final boolean requiresVaccination, final boolean automaticEnrolment) {
        setExecutionYear(executionYear);
        setDegreeCurricularPlan(degreeCurricularPlan);
        setRequiresVaccination(requiresVaccination);
        setAutomaticEnrolment(automaticEnrolment);
    }

    public static boolean requiresVaccination(Person person, ExecutionYear executionYear) {
        FirstTimeCandidacy candidacy = FirstTimeCandidacyController.getCandidacy(person);
        if (candidacy != null && requiresVaccination(candidacy.getDegreeCurricularPlan().getDegree(), executionYear)) {
            return true;
        }

        Student student = person.getStudent();
        if (student != null) {
            for (Registration registration : student.getRegistrationsSet()) {
                if (requiresVaccination(registration.getDegree(), executionYear)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isAutomaticEnrolment(Person person, ExecutionYear executionYear) {
        FirstTimeCandidacy candidacy = FirstTimeCandidacyController.getCandidacy(person);
        if (candidacy != null && isAutomaticEnrolment(candidacy.getDegreeCurricularPlan().getDegree(), executionYear)) {
            return true;
        }

        Student student = person.getStudent();
        if (student != null) {
            for (Registration registration : student.getRegistrationsSet()) {
                if (isAutomaticEnrolment(registration.getDegree(), executionYear)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isAutomaticEnrolment(Degree degree, ExecutionYear executionYear) {
        return getDegreeConfiguration(degree, executionYear) != null
                && getDegreeConfiguration(degree, executionYear).getAutomaticEnrolment();
    }

    private static boolean requiresVaccination(Degree degree, ExecutionYear executionYear) {
        return getDegreeConfiguration(degree, executionYear) != null
                && getDegreeConfiguration(degree, executionYear).getRequiresVaccination();
    }

    public static FirstYearRegistrationConfiguration getDegreeConfiguration(Degree degree, ExecutionYear executionYear) {
        return degree.getFirstYearRegistrationConfigurationsSet().stream().filter(c -> c.getExecutionYear() == executionYear)
                .findFirst().orElse(null);
    }
}
