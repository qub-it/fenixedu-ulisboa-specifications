package org.fenixedu.ulisboa.specifications.domain;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.ulisboa.specifications.domain.candidacy.FirstTimeCandidacy;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;

import pt.ist.fenixframework.Atomic;

public class FirstYearRegistrationConfiguration extends FirstYearRegistrationConfiguration_Base {

    public FirstYearRegistrationConfiguration(final Degree degree, final DegreeCurricularPlan degreeCurricularPlan,
            final boolean requiresVaccination) {
        super();
        setGlobalConfiguration(FirstYearRegistrationGlobalConfiguration.getInstance());
        setDegree(degree);
        setDegreeCurricularPlan(degreeCurricularPlan);
        setRequiresVaccination(requiresVaccination);
    }

    @Atomic
    public void delete() {
        setDegree(null);
        setDegreeCurricularPlan(null);
        setGlobalConfiguration(null);
        super.deleteDomainObject();
    }

    @Atomic
    public void edit(final DegreeCurricularPlan degreeCurricularPlan, final boolean requiresVaccination) {
        setDegreeCurricularPlan(degreeCurricularPlan);
        setRequiresVaccination(requiresVaccination);
    }

    public static boolean requiresVaccination(final Person person) {
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

    private static boolean requiresVaccination(final Degree degree) {
        return getDegreeConfiguration(degree) != null && getDegreeConfiguration(degree).getRequiresVaccination();
    }

    public static FirstYearRegistrationConfiguration getDegreeConfiguration(final Degree degree) {
        return degree.getFirstYearRegistrationConfigurationsSet().stream().findFirst().orElse(null);
    }
}
