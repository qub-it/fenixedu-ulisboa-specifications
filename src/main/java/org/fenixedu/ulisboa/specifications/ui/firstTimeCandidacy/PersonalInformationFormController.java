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

import java.io.Serializable;

import org.fenixedu.academic.domain.GrantOwnerType;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.person.Gender;
import org.fenixedu.academic.domain.person.IDDocumentType;
import org.fenixedu.academic.domain.person.MaritalStatus;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.joda.time.YearMonthDay;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.firstTimeCandidacy",
        accessGroup = "logged")
@RequestMapping(PersonalInformationFormController.CONTROLLER_URL)
public class PersonalInformationFormController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/firsttimecandidacy/personalinformationform";

    private static final String _FILLPERSONALINFORMATION_URI = "/fillpersonalinformation";
    public static final String FILLPERSONALINFORMATION_URL = CONTROLLER_URL + _FILLPERSONALINFORMATION_URI;

    @RequestMapping
    public String home(Model model) {
        return "forward:" + CONTROLLER_URL + _FILLPERSONALINFORMATION_URI;
    }

    @RequestMapping(value = _FILLPERSONALINFORMATION_URI, method = RequestMethod.GET)
    public String fillpersonalinformation(Model model) {
        model.addAttribute("genderValues", Gender.values());
        model.addAttribute("idDocumentTypeValues", IDDocumentType.values());
        model.addAttribute("professionTypeValues", ProfessionType.values());
        model.addAttribute("professionalConditionValues", ProfessionalSituationConditionType.values());
        model.addAttribute("maritalStatusValues", MaritalStatus.values());
        model.addAttribute("grantOwnerTypeValues", GrantOwnerType.values());

        fillFormIfRequired(model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/personalinformationform/fillpersonalinformation";
    }

    private void fillFormIfRequired(Model model) {
        if (!model.containsAttribute("personalInformationForm")) {
            PersonalInformationForm personalInformationForm = new PersonalInformationForm();
            Person person = AccessControl.getPerson();
            personalInformationForm.setName(person.getName());
            personalInformationForm.setUsername(person.getUsername());
            personalInformationForm.setGender(person.getGender());
            personalInformationForm.setDocumentIdNumber(person.getDocumentIdNumber());
            personalInformationForm.setIdDocumentType(person.getIdDocumentType());
            personalInformationForm.setProfessionalCondition(ProfessionalSituationConditionType.STUDENT);
            personalInformationForm.setMaritalStatus(MaritalStatus.SINGLE);
            personalInformationForm.setGrantOwnerType(GrantOwnerType.STUDENT_WITHOUT_SCHOLARSHIP);
            personalInformationForm.setProfessionType(ProfessionType.OTHER);
            model.addAttribute("personalInformationForm", personalInformationForm);
        }
    }

    @RequestMapping(value = _FILLPERSONALINFORMATION_URI, method = RequestMethod.POST)
    public String fillpersonalinformation(PersonalInformationForm form, Model model, RedirectAttributes redirectAttributes) {

        try {
            model.addAttribute("personalInformationForm", form);
            return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/filiationform/fillfiliation/", model,
                    redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "label.error.create")
                    + de.getLocalizedMessage(), model);
            return fillpersonalinformation(model);
        }
    }

    public static class PersonalInformationForm implements Serializable {
        private static final long serialVersionUID = 1L;

        private String name;
        private String username;
        private Gender gender;
        private String documentIdNumber;
        private IDDocumentType idDocumentType;
        private String identificationDocumentExtraDigit;
        private String identificationDocumentSeriesNumber;
        private String documentIdEmissionLocation;
        private YearMonthDay documentIdEmissionDate;
        private YearMonthDay documentIdExpirationDate;
        private String socialSecurityNumber;
        private ProfessionType professionType;
        private ProfessionalSituationConditionType professionalCondition;
        private String profession;
        private MaritalStatus maritalStatus;
        private GrantOwnerType grantOwnerType;
        private Unit grantOwnerProvider;
        private String grantOwnerProviderName;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Gender getGender() {
            return gender;
        }

        public void setGender(Gender gender) {
            this.gender = gender;
        }

        public String getDocumentIdNumber() {
            return documentIdNumber;
        }

        public void setDocumentIdNumber(String documentIdNumber) {
            this.documentIdNumber = documentIdNumber;
        }

        public IDDocumentType getIdDocumentType() {
            return idDocumentType;
        }

        public void setIdDocumentType(IDDocumentType idDocumentType) {
            this.idDocumentType = idDocumentType;
        }

        public String getIdentificationDocumentExtraDigit() {
            return identificationDocumentExtraDigit;
        }

        public void setIdentificationDocumentExtraDigit(String identificationDocumentExtraDigit) {
            this.identificationDocumentExtraDigit = identificationDocumentExtraDigit;
        }

        public String getIdentificationDocumentSeriesNumber() {
            return identificationDocumentSeriesNumber;
        }

        public void setIdentificationDocumentSeriesNumber(String identificationDocumentSeriesNumber) {
            this.identificationDocumentSeriesNumber = identificationDocumentSeriesNumber;
        }

        public String getDocumentIdEmissionLocation() {
            return documentIdEmissionLocation;
        }

        public void setDocumentIdEmissionLocation(String documentIdEmissionLocation) {
            this.documentIdEmissionLocation = documentIdEmissionLocation;
        }

        public YearMonthDay getDocumentIdEmissionDate() {
            return documentIdEmissionDate;
        }

        public void setDocumentIdEmissionDate(YearMonthDay documentIdEmissionDate) {
            this.documentIdEmissionDate = documentIdEmissionDate;
        }

        public YearMonthDay getDocumentIdExpirationDate() {
            return documentIdExpirationDate;
        }

        public void setDocumentIdExpirationDate(YearMonthDay documentIdExpirationDate) {
            this.documentIdExpirationDate = documentIdExpirationDate;
        }

        public String getSocialSecurityNumber() {
            return socialSecurityNumber;
        }

        public void setSocialSecurityNumber(String socialSecurityNumber) {
            this.socialSecurityNumber = socialSecurityNumber;
        }

        public ProfessionType getProfessionType() {
            return professionType;
        }

        public void setProfessionType(ProfessionType professionType) {
            this.professionType = professionType;
        }

        public ProfessionalSituationConditionType getProfessionalCondition() {
            return professionalCondition;
        }

        public void setProfessionalCondition(ProfessionalSituationConditionType professionalCondition) {
            this.professionalCondition = professionalCondition;
        }

        public String getProfession() {
            return profession;
        }

        public void setProfession(String profession) {
            this.profession = profession;
        }

        public MaritalStatus getMaritalStatus() {
            return maritalStatus;
        }

        public void setMaritalStatus(MaritalStatus maritalStatus) {
            this.maritalStatus = maritalStatus;
        }

        public GrantOwnerType getGrantOwnerType() {
            return grantOwnerType;
        }

        public void setGrantOwnerType(GrantOwnerType grantOwnerType) {
            this.grantOwnerType = grantOwnerType;
        }

        public Unit getGrantOwnerProvider() {
            return grantOwnerProvider;
        }

        public void setGrantOwnerProvider(Unit grantOwnerProvider) {
            this.grantOwnerProvider = grantOwnerProvider;
        }

        public String getGrantOwnerProviderName() {
            return grantOwnerProviderName;
        }

        public void setGrantOwnerProviderName(String grantOwnerProviderName) {
            this.grantOwnerProviderName = grantOwnerProviderName;
        }

        public static long getSerialversionuid() {
            return serialVersionUID;
        }

    }
}
