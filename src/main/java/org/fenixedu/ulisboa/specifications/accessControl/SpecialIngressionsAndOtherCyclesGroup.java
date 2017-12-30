package org.fenixedu.ulisboa.specifications.accessControl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationDataByExecutionYear;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.CustomGroup;
import org.fenixedu.ulisboa.specifications.domain.SpecialIngressionsAndOtherCyclesPersistentGroup;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * This is a TEMPORARY class to control the access to the enrolments to a limited set of students.
 *
 * @author nuno.pinheiro@qub-it.com
 *
 */

@Deprecated
@GroupOperator(SpecialIngressionsAndOtherCyclesGroup.GROUP_OPERATOR)
public class SpecialIngressionsAndOtherCyclesGroup extends CustomGroup {

    public static final String GROUP_OPERATOR = "specialIngressionsAndOtherCycles";
    static Collection<IngressionType> ingressionTypes;
    static Collection<DegreeType> degreeTypes;
    static {
        ingressionTypes = new HashSet<>();
        ingressionTypes.add(IngressionType.findIngressionTypeByCode("45").get());
        ingressionTypes.add(IngressionType.findIngressionTypeByCode("3").get());
        ingressionTypes.add(IngressionType.findIngressionTypeByCode("2").get());
        ingressionTypes.add(IngressionType.findIngressionTypeByCode("46").get());
        ingressionTypes.add(IngressionType.findIngressionTypeByCode("7").get());
        ingressionTypes.add(IngressionType.findIngressionTypeByCode("6").get());

        degreeTypes = new ArrayList<>();
        degreeTypes.add(DegreeType.matching(x -> x.getCode().equals("BOLONHA_POST_DOCTORAL_DEGREE")).get());
        degreeTypes.add(DegreeType.matching(x -> x.getCode().equals("BOLONHA_SPECIALIZATION_DEGREE")).get());
        degreeTypes.add(DegreeType.matching(x -> x.getCode().equals("BOLONHA_ADVANCED_FORMATION_DIPLOMA")).get());
        degreeTypes.add(DegreeType.matching(x -> x.getCode().equals("BOLONHA_PHD")).get());
        degreeTypes.add(DegreeType.matching(x -> x.getCode().equals("BOLONHA_MASTER_DEGREE")).get());

    }

    @Override
    public boolean equals(final Object arg0) {
        return arg0 instanceof SpecialIngressionsAndOtherCyclesGroup;
    }

    @Override
    public Stream<User> getMembers() {
        Set<Registration> registrations = new HashSet<>();
        registrations.addAll(ExecutionYear.readCurrentExecutionYear().getStudentsSet());
        registrations.addAll(ExecutionYear.readCurrentExecutionYear().getPreviousExecutionYear().getStudentsSet());
        return registrations.stream().filter(isValid).map(registration -> registration.getStudent().getPerson().getUser());
    }

    @Override
    public Stream<User> getMembers(final DateTime arg0) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public String getPresentationName() {
        return GROUP_OPERATOR;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean isMember(final User user) {
        if (user == null) {
            return false;
        }
        Student student = user.getPerson().getStudent();
        if (student != null) {
            return isMemberStudent(student);
        }
        return false;
    }

    @Override
    public boolean isMember(final User arg0, final DateTime arg1) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public PersistentGroup toPersistentGroup() {
        return SpecialIngressionsAndOtherCyclesPersistentGroup.getInstance();
    }

    Predicate<Registration> isValid = registration -> {

//

        if (degreeTypes.contains(registration.getDegree().getDegreeType())) {
            return true;
        }
        IngressionType ingressionType = registration.getIngressionType();
        if (registration.getStartExecutionYear() == ExecutionYear.readCurrentExecutionYear()
                && ingressionTypes.contains(ingressionType)) {
            return true;
        }

        return registration.getRegistrationDataByExecutionYearSet().stream()
                .filter(rdby -> rdby.getExecutionYear() == ExecutionYear.readCurrentExecutionYear()
                        || rdby.getExecutionYear() == ExecutionYear.readCurrentExecutionYear().getPreviousExecutionYear())
                .anyMatch(rdby -> wasReingression(rdby));
    };

    private boolean wasReingression(final RegistrationDataByExecutionYear registrationDataByExecutionYear) {
        LocalDate reingressionDate = registrationDataByExecutionYear.getReingressionDate();

        //re-ingressions date @ FL
        return registrationDataByExecutionYear.getReingression() && reingressionDate != null
                && reingressionDate.isAfter(new DateTime(2015, 7, 15, 0, 0).toLocalDate())
                && reingressionDate.isBefore(new DateTime(2015, 9, 17, 23, 59).toLocalDate());
    }

    boolean isMemberStudent(final Student student) {
        return student.getActiveRegistrationsIn(ExecutionSemester.readActualExecutionSemester()).stream().anyMatch(isValid);
    }

}
