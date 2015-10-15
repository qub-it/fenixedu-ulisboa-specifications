package org.fenixedu.ulisboa.specifications.domain;

import org.apache.commons.lang.StringEscapeUtils;
import org.fenixedu.academic.domain.student.Registration;

import pt.ist.fenixframework.FenixFramework;

public class RegistrationObservations extends RegistrationObservations_Base {

    public RegistrationObservations(Registration registration) {
        super();
        setRegistration(registration);
    }

    @Override
    public Registration getRegistration() {
        return super.getRegistration();
    }

    public static void setupDeleteListener() {
        FenixFramework.getDomainModel().registerDeletionListener(Registration.class, registration -> {
            if (registration.getRegistrationObservations() != null) {
                registration.getRegistrationObservations().delete();
            }
        });
    }

    private void delete() {
        setRegistration(null);
        deleteDomainObject();
    }

    public String getAsHtml() {
        return getValue() != null ? StringEscapeUtils.escapeHtml(getValue()).replaceAll("\r\n", "<br>") : null;
    }
}
