package org.fenixedu.ulisboa.specifications.domain.student.access.importation.external;

import org.fenixedu.academic.domain.student.Registration;

public interface SyncRegistrationWithExternalServices {

    public boolean syncRegistrationToExternal(Registration registration);
}
