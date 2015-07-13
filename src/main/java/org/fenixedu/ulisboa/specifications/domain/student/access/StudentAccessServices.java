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

    public static void triggerSyncPersonToExternal(Person person) {
        for (SyncPersonWithExternalServices syncExternalPerson : syncExternalPersons) {
            syncExternalPerson.syncPersonToExternal(person);
        }
    }

    public static void requestSyncPersonFromExternal(Person person) {
        for (SyncPersonWithExternalServices syncExternalPerson : syncExternalPersons) {
            syncExternalPerson.syncPersonFromExternal(person);
        }
    }

    public static void triggerSyncStudentToExternal(Student student) {
        for (SyncPersonWithExternalServices syncExternalPerson : syncExternalPersons) {
            syncExternalPerson.syncStudentToExternal(student);
        }
    }

    public static void triggerSyncRegistrationToExternal(Registration registration) {
        for (SyncRegistrationWithExternalServices syncExternalRegistration : syncExternalRegistrations) {
            syncExternalRegistration.syncRegistrationToExternal(registration);
        }
    }

}
