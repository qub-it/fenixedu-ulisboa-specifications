package org.fenixedu.ulisboa.specifications.domain.student.access;

import java.util.ArrayList;
import java.util.List;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.ulisboa.specifications.domain.student.access.importation.external.SyncPersonWithExternalServices;
import org.fenixedu.ulisboa.specifications.domain.student.access.importation.external.SyncRegistrationWithExternalServices;

public class StudentAccessServices {

    private static List<SyncPersonWithExternalServices> syncExternalPersons = new ArrayList<>();
    private static List<SyncRegistrationWithExternalServices> syncExternalRegistrations = new ArrayList<>();

    public static void subscribeSyncPerson(SyncPersonWithExternalServices syncPersonWithExternalServices) {
        syncExternalPersons.add(syncPersonWithExternalServices);
    }

    public static void subscribeSyncRegistration(SyncRegistrationWithExternalServices syncRegistrationWithExternalServices) {
        syncExternalRegistrations.add(syncRegistrationWithExternalServices);
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

    public static boolean triggerSyncRegistrationToExternal(Registration registration) {
        boolean globalSuccess = true;
        for (SyncRegistrationWithExternalServices syncExternalRegistration : syncExternalRegistrations) {
            boolean callSuccess = syncExternalRegistration.syncRegistrationToExternal(registration);
            if (!callSuccess) {
                globalSuccess = false;
            }
        }
        return globalSuccess;
    }
}
