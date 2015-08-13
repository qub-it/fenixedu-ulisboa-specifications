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

import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = PersonalInformationFormController.class)
@RequestMapping(ContactsFormController.CONTROLLER_URL)
public class ContactsFormController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/firsttimecandidacy/contactsform";

    private static final String _FILLCONTACTS_URI = "/fillcontacts";
    public static final String FILLCONTACTS_URL = CONTROLLER_URL + _FILLCONTACTS_URI;

    @RequestMapping(value = _FILLCONTACTS_URI, method = RequestMethod.GET)
    public String fillcontacts(Model model) {

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/contactsform/fillcontacts";
    }

    @RequestMapping(value = _FILLCONTACTS_URI, method = RequestMethod.POST)
    public String fillcontacts(ContactsForm contactsForm, Model model, RedirectAttributes redirectAttributes) {

        try {
            model.addAttribute("contactsForm", contactsForm);
            return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/origininformationform/fillorigininformation/", model,
                    redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "label.error.create")
                    + de.getLocalizedMessage(), model);
            return fillcontacts(model);
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

        public boolean isEmailAvailable() {
            return isEmailAvailable;
        }

        public void setEmailAvailable(boolean isEmailAvailable) {
            this.isEmailAvailable = isEmailAvailable;
        }

        public boolean isHomepageAvailable() {
            return isHomepageAvailable;
        }

        public void setHomepageAvailable(boolean isHomepageAvailable) {
            this.isHomepageAvailable = isHomepageAvailable;
        }

    }
}
