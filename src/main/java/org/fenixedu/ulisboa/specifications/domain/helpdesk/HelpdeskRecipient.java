package org.fenixedu.ulisboa.specifications.domain.helpdesk;

public class HelpdeskRecipient extends HelpdeskRecipient_Base {

    public HelpdeskRecipient(String email) {
        super();
        setEmail(email);
    }

    public boolean matches(String email) {
        return getEmail().equals(email);
    }

}
