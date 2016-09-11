/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: joao.roxo@qub-it.com 
 *               nuno.pinheiro@qub-it.com
 *
 * 
 * This file is part of FenixEdu Specifications.
 *
 * FenixEdu Specifications is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Specifications is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Specifications.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.fenixedu.academic.domain.EmergencyContact;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.contacts.EmailAddress;
import org.fenixedu.academic.domain.contacts.MobilePhone;
import org.fenixedu.academic.domain.contacts.PartyContact;
import org.fenixedu.academic.domain.contacts.PartyContactType;
import org.fenixedu.academic.domain.contacts.Phone;
import org.fenixedu.academic.domain.contacts.WebAddress;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.student.access.StudentAccessServices;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@Component("org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.ContactsFormController")
@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(ContactsFormController.CONTROLLER_URL)
public class ContactsFormController extends FenixeduUlisboaSpecificationsBaseController {

    private static final String PHONE_PATTERN = "(\\d{4,15})";

    private static final String URL_PATTERN = "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?";

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/OLD/firsttimecandidacy/contactsform/";

    private static final String _FILLCONTACTS_URI = "/fillcontacts";
    public static final String FILLCONTACTS_URL = CONTROLLER_URL + _FILLCONTACTS_URI;

    @RequestMapping(value = "/back", method = RequestMethod.GET)
    public String back(Model model, RedirectAttributes redirectAttributes) {
        return redirect(ResidenceInformationFormController.FILLRESIDENCEINFORMATION_URL, model, redirectAttributes);
    }

    @RequestMapping(value = _FILLCONTACTS_URI, method = RequestMethod.GET)
    public String fillcontacts(Model model, RedirectAttributes redirectAttributes) {
        if (!FirstTimeCandidacyController.isPeriodOpen()) {
            return redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes);
        }
        fillFormIfRequired(model);
        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillContacts.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/contactsform/fillcontacts";
    }

    private void fillFormIfRequired(Model model) {
        if (!model.containsAttribute("contactsForm")) {
            Person person = AccessControl.getPerson();
            ContactsForm form = new ContactsForm();

            Phone phone = getDefaultPersonalContact(person, Phone.class);
            if (phone != null) {
                form.setPhoneNumber(phone.getNumber());
            }

            MobilePhone mobilePhone = getDefaultPersonalContact(person, MobilePhone.class);
            if (mobilePhone != null) {
                form.setMobileNumber(mobilePhone.getNumber());
            }

            EmailAddress email = getDefaultPersonalContact(person, EmailAddress.class);
            if (email != null) {
                form.setPersonalEmail(email.getValue());
                form.setIsEmailAvailable(email.getVisibleToPublic());
            } else {
                form.setIsEmailAvailable(false);
            }

            WebAddress homepage = getDefaultPersonalContact(person, WebAddress.class);
            if (homepage != null) {
                form.setWebAddress(homepage.getUrl());
                form.setIsHomepageAvailable(homepage.getVisibleToPublic());
            } else {
                form.setIsHomepageAvailable(false);
            }

            form.setEmergencyContact(Optional.ofNullable(person.getProfile().getEmergencyContact())
                    .map(EmergencyContact::getContact).orElse(null));

            model.addAttribute("contactsForm", form);
        }
    }

    public static <T extends PartyContact> T getDefaultPersonalContact(Person person, Class<T> partyContactClass) {
        T defaultContact = (T) person.getDefaultPartyContact(partyContactClass);
        if (defaultContact != null && defaultContact.getType().equals(PartyContactType.PERSONAL)) {
            return defaultContact;
        }

        Predicate<PartyContact> contactIsPersonal = address -> address.getType().equals(PartyContactType.PERSONAL);
        Predicate<PartyContact> contactIsToBeDefault =
                address -> !address.isActiveAndValid() && address.getPartyContactValidation().getToBeDefault();
        List<T> allContacts = (List<T>) person.getAllPartyContacts(partyContactClass);
        return allContacts.stream().filter(contactIsPersonal).filter(contactIsToBeDefault)
                .sorted(ResidenceInformationFormController.CONTACT_COMPARATOR_BY_MODIFIED_DATE).findFirst().orElse(null);
    }

    @RequestMapping(value = _FILLCONTACTS_URI, method = RequestMethod.POST)
    public String fillcontacts(ContactsForm form, Model model, RedirectAttributes redirectAttributes) {
        if (!FirstTimeCandidacyController.isPeriodOpen()) {
            return redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes);
        }
        if (!validate(form, model)) {
            return fillcontacts(model, redirectAttributes);
        }

        try {
            writeData(form);
            StudentAccessServices.triggerSyncPersonToExternal(AccessControl.getPerson());
            model.addAttribute("contactsForm", form);
            return redirect(OriginInformationFormController.FILLORIGININFORMATION_URL, model, redirectAttributes);
        } catch (DomainException domainEx) {
            LoggerFactory.getLogger(this.getClass()).error("Exception for user " + AccessControl.getPerson().getUsername());
            domainEx.printStackTrace();
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, domainEx.getKey()),
                    model);
            return fillcontacts(model, redirectAttributes);
        } catch (Exception de) {
            LoggerFactory.getLogger(this.getClass()).error("Exception for user " + AccessControl.getPerson().getUsername());
            de.printStackTrace();
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "label.error.create")
                    + de.getLocalizedMessage(), model);
            return fillcontacts(model, redirectAttributes);
        }
    }

    private boolean validate(ContactsForm form, Model model) {
        if (StringUtils.isEmpty(form.getPersonalEmail())) {
            addErrorMessage(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.personalEmail.required"),
                    model);
            return false;
        }

        if (!StringUtils.isEmpty(form.getPhoneNumber()) && !form.getPhoneNumber().matches(PHONE_PATTERN)) {
            addErrorMessage(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.incorrect.phone"), model);
            return false;
        }
        if (!StringUtils.isEmpty(form.getMobileNumber()) && !form.getMobileNumber().matches(PHONE_PATTERN)) {
            addErrorMessage(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.incorrect.phone"), model);
            return false;
        }

        if (!StringUtils.isEmpty(form.getWebAddress()) && !form.getWebAddress().matches(URL_PATTERN)) {
            addErrorMessage(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.incorrect.webAddress"),
                    model);
            return false;
        }
        if (StringUtils.isEmpty(form.getEmergencyContact()) || !form.getEmergencyContact().matches(PHONE_PATTERN)) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                    "error.incorrect.emergencyContact"), model);
            return false;
        }
        return true;
    }

    @Atomic
    protected void writeData(ContactsForm form) {
        Person person = AccessControl.getPerson();

        Phone phone = getDefaultPersonalContact(person, Phone.class);
        if (phone != null) {
            phone.setNumber(form.getPhoneNumber());
        } else {
            phone = Phone.createPhone(person, form.getPhoneNumber(), PartyContactType.PERSONAL, true);
        }

        MobilePhone mobilePhone = getDefaultPersonalContact(person, MobilePhone.class);
        if (mobilePhone != null) {
            mobilePhone.setNumber(form.getMobileNumber());
        } else {
            mobilePhone = MobilePhone.createMobilePhone(person, form.getMobileNumber(), PartyContactType.PERSONAL, true);
        }

        EmailAddress email = getDefaultPersonalContact(person, EmailAddress.class);
        if (email != null) {
            email.setValue(form.getPersonalEmail());
            email.setVisibleToPublic(form.getIsEmailAvailable());
        } else {
            email = EmailAddress.createEmailAddress(person, form.getPersonalEmail(), PartyContactType.PERSONAL, true);
            email.setVisibleToPublic(form.getIsEmailAvailable());
        }

        WebAddress homepage = getDefaultPersonalContact(person, WebAddress.class);
        if (homepage != null) {
            homepage.setUrl(form.getWebAddress());
            homepage.setVisibleToPublic(form.getIsHomepageAvailable());
        } else {
            if (!StringUtils.isEmpty(form.getWebAddress())) {
                homepage = WebAddress.createWebAddress(person, form.getWebAddress(), PartyContactType.PERSONAL, true);
                homepage.setVisibleToPublic(form.getIsHomepageAvailable());
            }
        }

        EmergencyContact.updateEmergencyContact(person.getProfile(), form.getEmergencyContact());
    }

    public static class ContactsForm {
        private String phoneNumber;
        private String mobileNumber;
        private String personalEmail;
        private String webAddress;
        private boolean isEmailAvailable;
        private boolean isHomepageAvailable;
        private String emergencyContact;

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
}
