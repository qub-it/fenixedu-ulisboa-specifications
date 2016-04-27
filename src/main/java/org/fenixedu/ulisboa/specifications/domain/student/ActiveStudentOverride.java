package org.fenixedu.ulisboa.specifications.domain.student;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;

// Hack class that allows to no matter what allow a student to be seen as active
// in the integrations. This is used by org.fenixedu.ulisboa.specifications.service.StudentActive.isActiveStudent(Student)
// sadly this class is needed during the migrations, where not everything is yet migrated but we already still need
// to provide information about active students to several systems
//
// 21 April 2016 - Paulo Abrantes (paulo.abrantes@qub-it.com)
public class ActiveStudentOverride extends ActiveStudentOverride_Base {

    protected ActiveStudentOverride() {
        super();
        setBennu(Bennu.getInstance());
    }

    public void delete() {
        setBennu(null);
        super.deleteDomainObject();
    }

    public static ActiveStudentOverride getOverrideFor(Person person) {
        return Bennu
                .getInstance()
                .getActiveStudentOverridesSet()
                .stream()
                .filter(override -> override.getIdDocumentNumber() != null
                        && override.getIdDocumentNumber().equals(person.getDocumentIdNumber())
                        && override.getIdDocumentType() != null
                        && override.getIdDocumentType().equals(person.getIdDocumentType())).findAny().orElse(null);
    }

    public static boolean isOverrideAvailablefor(Person person) {
        return getOverrideFor(person) != null;
    }

    @Atomic
    public static void clearOverrides() {
        for (; !Bennu.getInstance().getActiveStudentOverridesSet().isEmpty(); Bennu.getInstance().getActiveStudentOverridesSet()
                .iterator().next().delete());
    }

    @Atomic
    public static ActiveStudentOverride createActiveStudentOverride(Person person) {
        ActiveStudentOverride activeStudentOverride = new ActiveStudentOverride();
        activeStudentOverride.setIdDocumentNumber(person.getDocumentIdNumber());
        activeStudentOverride.setIdDocumentType(person.getIdDocumentType());
        return activeStudentOverride;
    }
}
