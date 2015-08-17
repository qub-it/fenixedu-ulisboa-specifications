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

import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.util.CitzenCardValidation.validateNumeroDocumentoCC;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.util.FiscalCodeValidation.isValidcontrib;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.GrantOwnerType;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.academic.domain.organizationalStructure.Party;
import org.fenixedu.academic.domain.organizationalStructure.PartySocialSecurityNumber;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.organizationalStructure.UnitName;
import org.fenixedu.academic.domain.person.Gender;
import org.fenixedu.academic.domain.person.IDDocumentType;
import org.fenixedu.academic.domain.person.MaritalStatus;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.domain.ProfessionTimeType;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.util.UnitBean;
import org.joda.time.LocalDate;
import org.joda.time.YearMonthDay;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(PersonalInformationFormController.CONTROLLER_URL)
public class PersonalInformationFormController extends FenixeduUlisboaSpecificationsBaseController {

    private static final String CITZEN_CARD_CHECK_DIGIT_FORMAT = "[0-9][a-zA-Z][a-zA-Z][0-9]";

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
        model.addAttribute("maritalStatusValues", MaritalStatus.values());
        model.addAttribute("professionalConditionValues", ProfessionalSituationConditionType.values());
        model.addAttribute("professionTypeValues", ProfessionType.values());
        model.addAttribute("professionTimeTypeValues", ProfessionTimeType.readAll().collect(Collectors.toList()));
        model.addAttribute("grantOwnerTypeValues", GrantOwnerType.values());

        fillFormIfRequired(model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/personalinformationform/fillpersonalinformation";
    }

    private void fillFormIfRequired(Model model) {
        PersonalInformationForm form = (PersonalInformationForm) model.asMap().get("personalInformationForm");
        Person person = AccessControl.getPerson();
        if (form != null) {
            //Read-only fields
            form.setName(person.getName());
            form.setUsername(person.getUsername());
            form.setGender(person.getGender());
            form.setDocumentIdNumber(person.getDocumentIdNumber());
            form.setIdDocumentType(person.getIdDocumentType());
        } else {
            form = new PersonalInformationForm();

            //Read-only fields
            form.setName(person.getName());
            form.setUsername(person.getUsername());
            form.setGender(person.getGender());
            form.setDocumentIdNumber(person.getDocumentIdNumber());
            form.setIdDocumentType(person.getIdDocumentType());

            form.setIdentificationDocumentSeriesNumber(person.getIdentificationDocumentSeriesNumberValue() != null ? person
                    .getIdentificationDocumentSeriesNumberValue() : person.getIdentificationDocumentExtraDigitValue());
            form.setDocumentIdEmissionLocation(person.getEmissionLocationOfDocumentId());
            YearMonthDay emissionDateOfDocumentIdYearMonthDay = person.getEmissionDateOfDocumentIdYearMonthDay();
            YearMonthDay expirationDateOfDocumentIdYearMonthDay = person.getExpirationDateOfDocumentIdYearMonthDay();
            form.setDocumentIdEmissionDate(emissionDateOfDocumentIdYearMonthDay != null ? new LocalDate(person
                    .getEmissionDateOfDocumentIdYearMonthDay().toDateMidnight()) : null);
            form.setDocumentIdExpirationDate(expirationDateOfDocumentIdYearMonthDay != null ? new LocalDate(
                    expirationDateOfDocumentIdYearMonthDay.toDateMidnight()) : null);

            form.setSocialSecurityNumber(person.getSocialSecurityNumber());
            form.setMaritalStatus(person.getMaritalStatus());
            form.setProfession(person.getProfession());

            PersonUlisboaSpecifications personUl = person.getPersonUlisboaSpecifications();
            if (personUl != null) {
                form.setProfessionTimeType(personUl.getProfessionTimeType());
            }

            PersonalIngressionData personalData =
                    FirstTimeCandidacyController.getOrCreatePersonalIngressionData(FirstTimeCandidacyController
                            .getStudentCandidacy().getPrecedentDegreeInformation());
            form.setMaritalStatus(personalData.getMaritalStatus());
            if (form.getMaritalStatus() == null) {
                form.setMaritalStatus(MaritalStatus.SINGLE);
            }
            form.setProfessionType(personalData.getProfessionType());
            if (form.getProfessionType() == null) {
                form.setProfessionType(ProfessionType.OTHER);
            }
            form.setGrantOwnerType(personalData.getGrantOwnerType());
            if (form.getGrantOwnerType() == null) {
                form.setGrantOwnerType(GrantOwnerType.STUDENT_WITHOUT_SCHOLARSHIP);
            }
            form.setGrantOwnerProvider(personalData.getGrantOwnerProvider());
            form.setProfessionalCondition(personalData.getProfessionalCondition());
            if (form.getProfessionalCondition() == null) {
                form.setProfessionalCondition(ProfessionalSituationConditionType.STUDENT);
            }

            model.addAttribute("personalInformationForm", form);
        }
    }

    @RequestMapping(value = _FILLPERSONALINFORMATION_URI, method = RequestMethod.POST)
    public String fillpersonalinformation(PersonalInformationForm form, Model model, RedirectAttributes redirectAttributes) {
        if (!validate(form, model)) {
            return fillpersonalinformation(model);
        }

        try {
            writeData(form);
            model.addAttribute("personalInformationForm", form);
            return redirect("/fenixedu-ulisboa-specifications/firsttimecandidacy/filiationform/fillfiliation/", model,
                    redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "label.error.create")
                    + de.getLocalizedMessage(), model);
            return fillpersonalinformation(model);
        }
    }

    private boolean validate(PersonalInformationForm form, Model model) {
        Person person = AccessControl.getPerson();
        if (form.getIdentificationDocumentSeriesNumber().matches(CITZEN_CARD_CHECK_DIGIT_FORMAT)
                && !validateNumeroDocumentoCC(person.getDocumentIdNumber() + form.getIdentificationDocumentSeriesNumber())
                && !testsMode()) {

            //This will never throw an error 
            addErrorMessage(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.wrongCheckDigit"), model);
            return false;
        }
        if (form.getDocumentIdExpirationDate() == null) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                    "error.expirationDate.required"), model);
            return false;
        }

        if (!StringUtils.isEmpty(form.getSocialSecurityNumber()) && !isValidcontrib(form.getSocialSecurityNumber())) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                    "error.candidacy.workflow.PersonalInformationForm.socialSecurityNumber.invalid"), model);
            return false;
        }
        Party party = PartySocialSecurityNumber.readPartyBySocialSecurityNumber(form.getSocialSecurityNumber());
        if (party != null && party != person) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                    "error.candidacy.workflow.PersonalInformationForm.socialSecurityNumber.already.exists"), model);
            return false;
        }

        if (form.isStudentWorking()) {
            if (StringUtils.isEmpty(form.getProfession())) {
                addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                        "error.candidacy.workflow.PersonalInformationForm.profession.required"), model);
                return false;
            }
            if (form.getProfessionTimeType() == null) {
                addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                        "error.candidacy.workflow.PersonalInformationForm.professionTimeType.required"), model);
                return false;
            }
        }

        if (form.getGrantOwnerType().equals(GrantOwnerType.OTHER_INSTITUTION_GRANT_OWNER) && form.getGrantOwnerProvider() == null) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                    "error.candidacy.workflow.PersonalInformationForm.grant.owner.must.choose.granting.institution"), model);
            return false;
        }

        return true;
    }

    private boolean testsMode() {
        return CoreConfiguration.getConfiguration().developmentMode() || ULisboaConfiguration.getConfiguration().isQualityMode();
    }

    @Atomic
    private void writeData(PersonalInformationForm form) {
        Person person = AccessControl.getPerson();
        PersonUlisboaSpecifications personUl = PersonUlisboaSpecifications.findOrCreate(person);
        PersonalIngressionData personalData =
                FirstTimeCandidacyController.getOrCreatePersonalIngressionData(FirstTimeCandidacyController.getStudentCandidacy()
                        .getPrecedentDegreeInformation());

        String seriesNumerOrExtraDigit = form.getIdentificationDocumentSeriesNumber();
        if (seriesNumerOrExtraDigit.matches("[0-9]|")) {
            person.setIdentificationDocumentExtraDigit(seriesNumerOrExtraDigit);
            person.setIdentificationDocumentSeriesNumber(null);
        } else if (seriesNumerOrExtraDigit.matches(CITZEN_CARD_CHECK_DIGIT_FORMAT)) {
            person.setIdentificationDocumentSeriesNumber(seriesNumerOrExtraDigit);
            person.setIdentificationDocumentExtraDigit(null);
        }
        person.setEmissionLocationOfDocumentId(form.getDocumentIdEmissionLocation());
        LocalDate documentIdEmissionDate = form.getDocumentIdEmissionDate();
        LocalDate documentIdExpirationDate = form.getDocumentIdExpirationDate();
        person.setEmissionDateOfDocumentIdYearMonthDay(documentIdEmissionDate != null ? new YearMonthDay(documentIdEmissionDate
                .toDate()) : null);
        person.setExpirationDateOfDocumentIdYearMonthDay(documentIdExpirationDate != null ? new YearMonthDay(
                documentIdExpirationDate.toDate()) : null);

        person.setSocialSecurityNumber(form.getSocialSecurityNumber());
        person.setMaritalStatus(form.getMaritalStatus());
        personalData.setMaritalStatus(form.getMaritalStatus());

        personalData.setProfessionalCondition(form.getProfessionalCondition());
        person.setProfession(form.getProfession());
        personalData.setProfessionType(form.getProfessionType());
        personUl.setProfessionTimeType(form.getProfessionTimeType());
        personalData.setGrantOwnerType(form.getGrantOwnerType());
        personalData.setGrantOwnerProvider(form.getGrantOwnerProvider());
    }

    @RequestMapping(value = "/unit", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody List<UnitBean> readUnits(@RequestParam("namePart") String namePart, Model model) {
        return UnitName.findExternalUnit(namePart, 50).stream()
                .map(un -> new UnitBean(un.getUnit().getExternalId(), un.getUnit().getName())).collect(Collectors.toList());
    }

    public static class PersonalInformationForm implements Serializable {
        private static final long serialVersionUID = 1L;

        private String name;
        private String username;
        private Gender gender;
        private String documentIdNumber;
        private IDDocumentType idDocumentType;
        //can be either the series number or the extra digit
        private String identificationDocumentSeriesNumber;
        private String documentIdEmissionLocation;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate documentIdEmissionDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate documentIdExpirationDate;
        private String socialSecurityNumber;
        private MaritalStatus maritalStatus;
        private ProfessionalSituationConditionType professionalCondition;
        private String profession;
        private ProfessionType professionType;
        private ProfessionTimeType professionTimeType;
        private GrantOwnerType grantOwnerType;
        private Unit grantOwnerProvider;

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

        public String getIdentificationDocumentSeriesNumber() {
            return identificationDocumentSeriesNumber;
        }

        public void setIdentificationDocumentSeriesNumber(String identificationDocumentSeriesNumber) {
            this.identificationDocumentSeriesNumber =
                    identificationDocumentSeriesNumber != null ? identificationDocumentSeriesNumber.toUpperCase() : null;
        }

        public String getDocumentIdEmissionLocation() {
            return documentIdEmissionLocation;
        }

        public void setDocumentIdEmissionLocation(String documentIdEmissionLocation) {
            this.documentIdEmissionLocation = documentIdEmissionLocation;
        }

        public LocalDate getDocumentIdEmissionDate() {
            return documentIdEmissionDate;
        }

        public void setDocumentIdEmissionDate(LocalDate documentIdEmissionDate) {
            this.documentIdEmissionDate = documentIdEmissionDate;
        }

        public LocalDate getDocumentIdExpirationDate() {
            return documentIdExpirationDate;
        }

        public void setDocumentIdExpirationDate(LocalDate documentIdExpirationDate) {
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

        public static long getSerialversionuid() {
            return serialVersionUID;
        }

        public ProfessionTimeType getProfessionTimeType() {
            return professionTimeType;
        }

        public void setProfessionTimeType(ProfessionTimeType professionTimeType) {
            this.professionTimeType = professionTimeType;
        }

        public boolean isStudentWorking() {
            if (isWorkingCondition()) {
                return true;
            }
            if (!StringUtils.isEmpty(getProfession())) {
                return true;
            }
            if (getProfessionTimeType() != null) {
                return true;
            }
            if (isWorkingProfessionType()) {
                return true;
            }
            return false;
        }

        private boolean isWorkingCondition() {
            switch (getProfessionalCondition()) {
            case WORKS_FOR_OTHERS:
                return true;
            case EMPLOYEER:
                return true;
            case INDEPENDENT_WORKER:
                return true;
            case WORKS_FOR_FAMILY_WITHOUT_PAYMENT:
                return true;
            case HOUSEWIFE:
                return true;
            case MILITARY_SERVICE:
                return true;
            default:
                return false;
            }
        }

        private boolean isWorkingProfessionType() {
            switch (getProfessionType()) {
            case UNKNOWN:
                return false;
            case OTHER:
                return false;
            default:
                return true;
            }
        }
    }
}
