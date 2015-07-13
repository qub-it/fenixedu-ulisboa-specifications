package org.fenixedu.ulisboa.specifications.domain.student.access.importation.external;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Student;

public interface SyncPersonWithExternalServices {

    public boolean syncPersonToExternal(Person person);

    public boolean syncPersonFromExternal(Person person);

    public boolean syncStudentToExternal(Student student);
}
