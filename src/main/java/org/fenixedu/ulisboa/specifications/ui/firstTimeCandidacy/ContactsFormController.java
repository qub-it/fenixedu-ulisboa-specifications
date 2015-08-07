/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: xpto@qub-it.com
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

import java.util.List;
import java.util.function.Predicate;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.contacts.EmailAddress;
import org.fenixedu.academic.domain.contacts.MobilePhone;
import org.fenixedu.academic.domain.contacts.PartyContact;
import org.fenixedu.academic.domain.contacts.Phone;
import org.fenixedu.academic.domain.contacts.WebAddress;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = InstructionsController.class)
@RequestMapping(ContactsFormController.CONTROLLER_URL)
public class ContactsFormController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/firsttimecandidacy/contactsform";

    private static final String _FILLCONTACTS_URI = "/fillcontacts";
    public static final String FILLCONTACTS_URL = CONTROLLER_URL + _FILLCONTACTS_URI;

    @RequestMapping(value = _FILLCONTACTS_URI, method = RequestMethod.GET)
    public String fillcontacts(Model model) {
        fillFormIfRequired(model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/contactsform/fillcontacts";
    }

    private void fillFormIfRequired(Model model) {
        if (!model.containsAttribute("contactsForm")) {
            Person person = AccessControl.getPerson();
            ContactsForm form = new ContactsForm();

            Phone phone = getDefaultContact(person, Phone.class);
            if (phone != null) {
                form.setPhoneNumber(phone.getNumber());
            }

            MobilePhone mobilePhone = getDefaultContact(person, MobilePhone.class);
            if (mobilePhone != null) {
                form.setMobileNumber(person.getDefaultMobilePhoneNumber());
            }

            EmailAddress email = getDefaultContact(person, EmailAddress.class);
            if (email != null) {
                form.setEmail(email.getValue());
                form.setIsEmailAvailable(email.getVisibleToPublic());
            } else {
                form.setIsEmailAvailable(false);
            }

            WebAddress homepage = getDefaultContact(person, WebAddress.class);
            if (homepage != null) {
                form.setWebAddress(homepage.getUrl());
                form.setIsHomepageAvailable(homepage.getVisibleToPublic());
            } else {
                form.setIsHomepageAvailable(false);
            }

            model.addAttribute("contactsForm", form);
        }
    }

    private static <T extends PartyContact> T getDefaultContact(Person person, Class<T> partyContactClass) {
        T defaultContact = (T) person.getDefaultPartyContact(partyContactClass);
        if (defaultContact != null) {
            return defaultContact;
        }

        Predicate<PartyContact> contactIsToBeDefault =
                address -> !address.isActiveAndValid() && address.getPartyContactValidation().getToBeDefault();
        List<T> allContacts = (List<T>) person.getAllPartyContacts(partyContactClass);
        return allContacts.stream().filter(contactIsToBeDefault)
                .sorted(ResidenceInformationFormController.CONTACT_COMPARATOR_BY_MODIFIED_DATE).findFirst().orElse(null);
    }

    @RequestMapping(value = _FILLCONTACTS_URI, method = RequestMethod.POST)
    public String fillcontacts(ContactsForm form, Model model, RedirectAttributes redirectAttributes) {
        if (!validate(form, model)) {
            return fillcontacts(model);
        }

        try {
            writeData(form);
            model.addAttribute("contactsForm", form);
            return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/schoolspecificdata/create/", model,
                    redirectAttributes);
        } catch (DomainException domainEx) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, domainEx.getKey()),
                    model);
            return fillcontacts(model);
        } catch (Exception de) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "label.error.create")
                    + de.getLocalizedMessage(), model);
            return fillcontacts(model);
        }
    }

    private boolean validate(ContactsForm form, Model model) {
        return true;
    }

    @Atomic
    protected void writeData(ContactsForm form) {
        Person person = AccessControl.getPerson();

        Phone phone = getDefaultContact(person, Phone.class);
        if (phone != null) {
            phone.setNumber(form.getPhoneNumber());
        } else {
            person.setDefaultPhoneNumber(form.getPhoneNumber());
        }

        MobilePhone mobilePhone = getDefaultContact(person, MobilePhone.class);
        if (mobilePhone != null) {
            mobilePhone.setNumber(form.getMobileNumber());
        } else {
            person.setDefaultMobilePhoneNumber(form.getMobileNumber());
        }

        EmailAddress email = getDefaultContact(person, EmailAddress.class);
        if (email != null) {
            email.setValue(form.getEmail());
            email.setVisibleToPublic(form.getIsEmailAvailable());
        } else {
            person.setDefaultEmailAddressValue(form.getEmail(), false, form.getIsEmailAvailable());
        }

        WebAddress homepage = getDefaultContact(person, WebAddress.class);
        if (homepage != null) {
            homepage.setUrl(form.getWebAddress());
            homepage.setVisibleToPublic(form.getIsHomepageAvailable());
        } else {
            if (!StringUtils.isEmpty(form.getWebAddress())) {
                person.setDefaultWebAddressUrl(form.getWebAddress());
                homepage = getDefaultContact(person, WebAddress.class);
                homepage.setVisibleToPublic(form.getIsHomepageAvailable());
            }
        }
    }

    public static class ContactsForm {
        private String phoneNumber;
        private String mobileNumber;
        private String email;
        private String webAddress;
        private boolean isEmailAvailable;
        private boolean isHomepageAvailable;

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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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

    }
}
