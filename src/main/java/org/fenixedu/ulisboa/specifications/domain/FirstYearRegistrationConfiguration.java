package org.fenixedu.ulisboa.specifications.domain;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.ulisboa.specifications.domain.candidacy.FirstTimeCandidacy;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;

import pt.ist.fenixframework.consistencyPredicates.ConsistencyPredicate;

public class FirstYearRegistrationConfiguration extends FirstYearRegistrationConfiguration_Base {

    public FirstYearRegistrationConfiguration(Degree degree) {
        super();
        setGlobalConfiguration(FirstYearRegistrationGlobalConfiguration.getInstance());
        setDegree(degree);
    }

    public void delete() {
        setDegree(null);
        setGlobalConfiguration(null);
        super.deleteDomainObject();
    }

    @ConsistencyPredicate
    private boolean isOnlyShiftsOrClassesEnrolment() {
        return !(getRequiresClassesEnrolment() && getRequiresShiftsEnrolment());
    }

    public static boolean requiresVaccination(Person person) {
        FirstTimeCandidacy candidacy = FirstTimeCandidacyController.getCandidacy(person);
        if (candidacy != null && requiresVaccination(candidacy.getDegreeCurricularPlan().getDegree())) {
            return true;
        }

        Student student = person.getStudent();
        if (student != null) {
            for (Registration registration : student.getRegistrationsSet()) {
                if (requiresVaccination(registration.getDegree())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean requiresVaccination(Degree degree) {
        return degree.getFirstYearRegistrationConfiguration() != null
                && degree.getFirstYearRegistrationConfiguration().getRequiresVaccination();
    }
}
