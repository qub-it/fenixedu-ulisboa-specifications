package org.fenixedu.ulisboa.specifications.domain.student.access;

import java.util.ArrayList;
import java.util.List;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.ulisboa.specifications.domain.student.access.importation.external.SyncPersonWithExternalServices;

@Deprecated(forRemoval = true)
public class StudentAccessServices {

    private static List<SyncPersonWithExternalServices> syncExternalPersons = new ArrayList<>();

    public static void subscribeSyncPerson(SyncPersonWithExternalServices syncPersonWithExternalServices) {
        syncExternalPersons.add(syncPersonWithExternalServices);
    }

    public static boolean triggerSyncPersonToExternal(Person person) {
        boolean globalSuccess = true;
        for (SyncPersonWithExternalServices syncExternalPerson : syncExternalPersons) {
            boolean callSuccess = syncExternalPerson.syncPersonToExternal(person);
            if (!callSuccess) {
                globalSuccess = false;
            }
        }
        return globalSuccess;
    }

    public static boolean requestSyncPersonFromExternal(Person person) {
        boolean globalSuccess = true;
        for (SyncPersonWithExternalServices syncExternalPerson : syncExternalPersons) {
            boolean callSuccess = syncExternalPerson.syncPersonFromExternal(person);
            if (!callSuccess) {
                globalSuccess = false;
            }
        }
        return globalSuccess;
    }

    public static boolean triggerSyncStudentToExternal(Student student) {
        boolean globalSuccess = true;
        for (SyncPersonWithExternalServices syncExternalPerson : syncExternalPersons) {
            boolean callSuccess = syncExternalPerson.syncStudentToExternal(student);
            if (!callSuccess) {
                globalSuccess = false;
            }
        }
        return globalSuccess;
    }

}
