package org.fenixedu.ulisboa.specifications.domain;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;

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
        Student student = person.getStudent();
        return student != null && student.getRegistrationsSet().stream().map(Registration::getDegree)
                .anyMatch(d -> requiresVaccination(d));
    }

    private static boolean requiresVaccination(final Degree degree) {
        return getDegreeConfiguration(degree) != null && getDegreeConfiguration(degree).getRequiresVaccination();
    }

    public static FirstYearRegistrationConfiguration getDegreeConfiguration(final Degree degree) {
        return degree.getFirstYearRegistrationConfigurationsSet().stream().findFirst().orElse(null);
    }
}
