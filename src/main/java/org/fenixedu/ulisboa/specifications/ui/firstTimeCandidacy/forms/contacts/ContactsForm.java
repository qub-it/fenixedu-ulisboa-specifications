package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.contacts;

import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;

public class ContactsForm implements CandidancyForm {
    private String phoneNumber;
    private String mobileNumber;
    private String personalEmail;
    private String webAddress;
    private boolean isEmailAvailable;
    private boolean isHomepageAvailable;
    private String emergencyContact;

    public ContactsForm() {
        updateLists();
    }

    @Override
    public void updateLists() {

    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public boolean getIsEmailAvailable() {
        return isEmailAvailable;
    }

    public void setIsEmailAvailable(boolean isEmailAvailable) {
        this.isEmailAvailable = isEmailAvailable;
    }

    public boolean getIsHomepageAvailable() {
        return isHomepageAvailable;
    }

    public void setIsHomepageAvailable(boolean isHomepageAvailable) {
        this.isHomepageAvailable = isHomepageAvailable;
    }

    public String getInstitutionalEmail() {
        return AccessControl.getPerson().getInstitutionalEmailAddressValue();
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
}
