package org.fenixedu.ulisboa.specifications.accessControl;

import java.util.stream.Stream;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.groups.CustomGroup;
import org.fenixedu.ulisboa.specifications.domain.SecondCycleFirstYearPersistentGroup;
import org.joda.time.DateTime;

@Deprecated
@GroupOperator(SecondCycleFirstYearGroup.GROUP_OPERATOR)
public class SecondCycleFirstYearGroup extends CustomGroup {
    public static final String GROUP_OPERATOR = "secondCycleFirstYear";
    static DegreeType masterBolonha = DegreeType.matching(x -> x.getCode().equals("BOLONHA_MASTER_DEGREE")).get();
    static DegreeType phdBolonha = DegreeType.matching(x -> x.getCode().equals("BOLONHA_PHD")).get();

    @Override
    public String getPresentationName() {
        return GROUP_OPERATOR;
    }

    @Override
    public PersistentGroup toPersistentGroup() {
        return SecondCycleFirstYearPersistentGroup.getInstance();
    }

    @Override
    public Stream<User> getMembers() {
        return ExecutionYear.findCurrents().stream().flatMap(ey -> ey.getStudentsSet().stream())
                .filter(r -> r.getDegreeType() == masterBolonha || r.getDegreeType() == phdBolonha)
                .map(r -> r.getPerson().getUser());
    }

    @Override
    public Stream<User> getMembers(final DateTime when) {
        throw new RuntimeException("Unsupported");
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

    private boolean isMemberStudent(final Student student) {
        return student.getActiveRegistrations().stream().anyMatch(r -> r.getStartExecutionYear().isCurrent()
                && (r.getDegreeType() == masterBolonha || r.getDegreeType() == phdBolonha));
    }

    @Override
    public boolean isMember(final User user, final DateTime when) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public boolean equals(final Object object) {
        return object instanceof SecondCycleFirstYearGroup;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}
