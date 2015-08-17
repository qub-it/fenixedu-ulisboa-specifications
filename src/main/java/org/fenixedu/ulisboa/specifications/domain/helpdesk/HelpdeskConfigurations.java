package org.fenixedu.ulisboa.specifications.domain.helpdesk;

import org.fenixedu.bennu.core.domain.Bennu;

public class HelpdeskConfigurations extends HelpdeskConfigurations_Base {

    private HelpdeskConfigurations() {
        super();
        setBennu(Bennu.getInstance());
    }

    public void addRecipient(String email) {
        if (getRecipientsSet().stream().filter(hr -> hr.matches(email)).count() == 0) {
            HelpdeskRecipient recipient = new HelpdeskRecipient(email);
            addRecipients(recipient);
        }
    }

    public void addCC(String email) {
        if (getCCsSet().stream().filter(hr -> hr.matches(email)).count() == 0) {
            HelpdeskRecipient cc = new HelpdeskRecipient(email);
            addCCs(cc);
        }
    }

    public void addBCC(String email) {
        if (getBCCsSet().stream().filter(hr -> hr.matches(email)).count() == 0) {
            HelpdeskRecipient bcc = new HelpdeskRecipient(email);
            addBCCs(bcc);
        }
    }

    public static HelpdeskConfigurations getInstance() {
        if (Bennu.getInstance().getHelpdeskConfiguration() == null) {
            new HelpdeskConfigurations();
        }
        return Bennu.getInstance().getHelpdeskConfiguration();
    }

}
