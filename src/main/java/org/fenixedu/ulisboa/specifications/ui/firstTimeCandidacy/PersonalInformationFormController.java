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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.FenixEduAcademicConfiguration;
import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.GrantOwnerType;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.academic.domain.organizationalStructure.Party;
import org.fenixedu.academic.domain.organizationalStructure.PartySocialSecurityNumber;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.person.Gender;
import org.fenixedu.academic.domain.person.IDDocumentType;
import org.fenixedu.academic.domain.raides.DegreeDesignation;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.treasury.util.FiscalCodeValidation;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.domain.ProfessionTimeType;
import org.fenixedu.ulisboa.specifications.domain.candidacy.FirstTimeCandidacy;
import org.fenixedu.ulisboa.specifications.util.IdentityCardUtils;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.LocalDate;
import org.joda.time.YearMonthDay;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

@Component("org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.PersonalInformationFormController")
@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(PersonalInformationFormController.CONTROLLER_URL)
public abstract class PersonalInformationFormController extends FirstTimeCandidacyAbstractController {

    private static final String IDENTITY_CARD_CONTROL_DIGIT_FORMAT = "[0-9]";

    private static final String CITZEN_CARD_CHECK_DIGIT_FORMAT = "[0-9][a-zA-Z][a-zA-Z][0-9]";

    private static final String SOCIAL_SECURITY_NUMBER_FORMAT = "\\d{9}";

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/OLD/firsttimecandidacy/{executionYearId}/personalinformationform";

    protected static final String _FILLPERSONALINFORMATION_URI = "/fillpersonalinformation";
    public static final String FILLPERSONALINFORMATION_URL = CONTROLLER_URL + _FILLPERSONALINFORMATION_URI;

    @RequestMapping
    public String home(@PathVariable("executionYearId") final ExecutionYear executionYear, Model model) {
        addControllerURLToModel(executionYear, model);
        return "forward:" + getControllerURLWithExecutionYear(executionYear) + _FILLPERSONALINFORMATION_URI;
    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @RequestMapping(value = "/back", method = RequestMethod.GET)
    public String back(@PathVariable("executionYearId") ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        return redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes);
    }

    @RequestMapping(value = _FILLPERSONALINFORMATION_URI, method = RequestMethod.GET)
    public String fillpersonalinformation(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model, final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        if (isFormIsFilled(executionYear, model)) {
            return nextScreen(executionYear, model, redirectAttributes);
        }

        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }
        model.addAttribute("genderValues", Gender.values());
        model.addAttribute("partial", isPartialUpdate());

        final List<Country> countryHighSchoolValues = Lists.newArrayList(Country.readDistinctCountries());
        Collections.sort(countryHighSchoolValues, Country.COMPARATOR_BY_NAME);
        model.addAttribute("countryHighSchoolValues", countryHighSchoolValues);

        FirstTimeCandidacy candidacy = FirstTimeCandidacyController.getCandidacy();
        if (candidacy != null) {
            model.addAttribute("placingOption", candidacy.getPlacingOption());
        }

        PersonalInformationForm form = fillFormIfRequired(executionYear, model);
        model.addAttribute("personalInformationForm", form);

        List<IDDocumentType> idDocumentTypeValues = new ArrayList<>();
        idDocumentTypeValues.addAll(Arrays.asList(IDDocumentType.values()));
        idDocumentTypeValues.remove(IDDocumentType.CITIZEN_CARD);
        model.addAttribute("idDocumentTypeValues", idDocumentTypeValues);

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillPersonalInformation.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/personalinformationform/fillpersonalinformation";
    }

    public PersonalInformationForm fillFormIfRequired(final ExecutionYear executionYear, Model model) {
        Person person = getStudent(model).getPerson();

        model.addAttribute("identityCardExtraDigitRequired", person.getIdDocumentType() == IDDocumentType.IDENTITY_CARD
                && !isIdentityCardControlNumberValid(getIdentityCardControlNumber(person)));

        PersonalInformationForm form = (PersonalInformationForm) model.asMap().get("personalInformationForm");
        if (form != null) {
            form.setDocumentIdNumber(person.getDocumentIdNumber());
            form.setIdDocumentType(person.getIdDocumentType());

            if (person.getIdDocumentType() == IDDocumentType.IDENTITY_CARD) {
                form.setIdentificationDocumentSeriesNumber(getIdentityCardControlNumber(person));
            }

            fillstaticformdata(executionYear, getStudent(model), form);

            model.addAttribute("personalInformationForm", form);
            return form;
        }

        return createPersonalInformationForm(executionYear, getStudent(model));
    }

    protected PersonalInformationForm createPersonalInformationForm(final ExecutionYear executionYear, final Student student) {
        final Person person = student.getPerson();
        PersonalInformationForm form;
        form = new PersonalInformationForm();

        form.setDocumentIdEmissionLocation(person.getEmissionLocationOfDocumentId());
        YearMonthDay emissionDateOfDocumentIdYearMonthDay = person.getEmissionDateOfDocumentIdYearMonthDay();
        YearMonthDay expirationDateOfDocumentIdYearMonthDay = person.getExpirationDateOfDocumentIdYearMonthDay();
        form.setDocumentIdEmissionDate(emissionDateOfDocumentIdYearMonthDay != null ? new LocalDate(
                person.getEmissionDateOfDocumentIdYearMonthDay().toDateMidnight()) : null);
        form.setDocumentIdExpirationDate(expirationDateOfDocumentIdYearMonthDay != null ? new LocalDate(
                expirationDateOfDocumentIdYearMonthDay.toDateMidnight()) : null);

        form.setSocialSecurityNumber(person.getSocialSecurityNumber());

        form.setDocumentIdNumber(person.getDocumentIdNumber());
        form.setIdDocumentType(person.getIdDocumentType());
        form.setIdentificationDocumentSeriesNumber(getIdentityCardControlNumber(person));

        PersonUlisboaSpecifications personUl = person.getPersonUlisboaSpecifications();
        if (personUl != null) {

            Unit institution = personUl.getFirstOptionInstitution();
            if (institution != null) {
                form.setFirstOptionInstitution(institution);

                String degreeDesignationName = personUl.getFirstOptionDegreeDesignation();
                Predicate<DegreeDesignation> matchesName = dd -> dd.getDescription().equalsIgnoreCase(degreeDesignationName);
                DegreeDesignation degreeDesignation =
                        institution.getDegreeDesignationSet().stream().filter(matchesName).findFirst().orElse(null);
                form.setFirstOptionDegreeDesignation(degreeDesignation);
            }
        }

        form.setCountryHighSchool(person.getCountryHighSchool());

        form.setGender(student.getPerson().getGender());

        fillstaticformdata(executionYear, student, form);

        return form;
    }

    private void fillstaticformdata(final ExecutionYear executionYear, final Student student, final PersonalInformationForm form) {
        form.setFirstYearRegistration(false);
        for (final Registration registration : student.getRegistrationsSet()) {
            if (!registration.isActive()) {
                continue;
            }

            if (registration.getRegistrationYear() != executionYear) {
                continue;
            }

            form.setFirstYearRegistration(true);
        }

        form.setName(student.getPerson().getName());
        form.setUsername(student.getPerson().getUser().getUsername());
    }

    @RequestMapping(value = _FILLPERSONALINFORMATION_URI, method = RequestMethod.POST)
    public String fillpersonalinformation(@PathVariable("executionYearId") final ExecutionYear executionYear, PersonalInformationForm form, Model model, RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }
        if (!validate(form, model)) {
            return fillpersonalinformation(executionYear, model, redirectAttributes);
        }

        try {
            writeData(executionYear, form, model);
            model.addAttribute("personalInformationForm", form);
            return nextScreen(executionYear, model, redirectAttributes);
        } catch (Exception de) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "label.error.create") + de.getLocalizedMessage(), model);
            LoggerFactory.getLogger(this.getClass()).error("Exception for user " + AccessControl.getPerson().getUsername());
            de.printStackTrace();

            return fillpersonalinformation(executionYear, model, redirectAttributes);
        }
    }

    protected String nextScreen(final ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(FiliationFormController.FILLFILIATION_URL + "/" + executionYear.getExternalId(), model, redirectAttributes);
    }

    private boolean validate(PersonalInformationForm form, Model model) {

        final Set<String> result = validateForm(form, getStudent(model).getPerson());

        for (final String message : result) {
            addErrorMessage(message, model);
        }

        return result.isEmpty();
    }

    public Set<String> validateForm(PersonalInformationForm form, final Person person) {
        final Set<String> result = Sets.newHashSet();
        if (!isPartialUpdate()) {
            IDDocumentType idType = form.getIdDocumentType();
            if (form.getIsForeignStudent()) {
                if (idType == null) {
                    result.add(BundleUtil.getString(BUNDLE, "error.documentIdType.required"));
                }

                if (StringUtils.isEmpty(form.getDocumentIdNumber())) {
                    result.add(BundleUtil.getString(BUNDLE, "error.documentIdNumber.required"));
                }
            }

            if (!form.getIsForeignStudent()) {
                if (StringUtils.isEmpty(form.getSocialSecurityNumber())
                        || !form.getSocialSecurityNumber().matches(SOCIAL_SECURITY_NUMBER_FORMAT)) {
                    result.add(BundleUtil.getString(BUNDLE,
                            "error.candidacy.workflow.PersonalInformationForm.incorrect.socialSecurityNumber"));
                }
            } else {
                if (!StringUtils.isEmpty(form.getSocialSecurityNumber())
                        && !form.getSocialSecurityNumber().matches(SOCIAL_SECURITY_NUMBER_FORMAT)) {
                    result.add(BundleUtil.getString(BUNDLE,
                            "error.candidacy.workflow.PersonalInformationForm.incorrect.socialSecurityNumber"));
                }
            }

            if (form.getDocumentIdExpirationDate() == null) {
                result.add(BundleUtil.getString(BUNDLE, "error.expirationDate.required"));
            }

            if (!StringUtils.isEmpty(form.getSocialSecurityNumber())
                    && !FiscalCodeValidation.isValidcontrib(form.getSocialSecurityNumber())) {
                result.add(BundleUtil.getString(BUNDLE,
                        "error.candidacy.workflow.PersonalInformationForm.socialSecurityNumber.invalid"));
            }
            String defaultSocialSecurityNumber =
                    FenixEduAcademicConfiguration.getConfiguration().getDefaultSocialSecurityNumber();
            if (!defaultSocialSecurityNumber.equals(form.getSocialSecurityNumber())) {
                Party party = PartySocialSecurityNumber.readPartyBySocialSecurityNumber(form.getSocialSecurityNumber());
                if (party != null && party != person) {
                    result.add(BundleUtil.getString(BUNDLE,
                            "error.candidacy.workflow.PersonalInformationForm.socialSecurityNumber.already.exists"));
                }
            }
        }

        if (form.getCountryHighSchool() == null) {
            result.add(
                    BundleUtil.getString(BUNDLE, "error.candidacy.workflow.PersonalInformationForm.countryHighSchool.required"));
        }

        if (person.getIdDocumentType() != null && person.getIdDocumentType() == IDDocumentType.IDENTITY_CARD
                && !isIdentityCardControlNumberValid(getIdentityCardControlNumber(person))
                && !isIdentityCardControlNumberValid(form.getIdentificationDocumentSeriesNumber())) {
            result.add(ULisboaSpecificationsUtil
                    .bundle("error.candidacy.workflow.PersonalInformationForm.incorrect.identificationSeriesNumber"));
        }

        return result;
    }

    private String getIdentityCardControlNumber(final Person person) {
        if (!Strings.isNullOrEmpty(person.getIdentificationDocumentSeriesNumberValue())) {
            return person.getIdentificationDocumentSeriesNumberValue();
        }

        return person.getIdentificationDocumentExtraDigitValue();
    }

    private void setIdentityCardControlNumber(final Person person, final String number) {
        person.setIdentificationDocumentSeriesNumber(number);
    }

    private boolean isIdentityCardControlNumberValid(final String extraValue) {
        return IdentityCardUtils.isIdentityCardDigitControlFormatValid(extraValue);
    }

    private boolean testsMode() {
        Boolean devMode = CoreConfiguration.getConfiguration().developmentMode();
        Boolean qualityMode = ULisboaConfiguration.getConfiguration().isQualityMode();
        return (devMode != null && devMode == true) || (qualityMode != null && qualityMode == true);
    }

    @Atomic
    private void writeData(final ExecutionYear executionYear, final PersonalInformationForm form, final Model model) {
        Person person = AccessControl.getPerson();
        PersonUlisboaSpecifications personUl = PersonUlisboaSpecifications.findOrCreate(person);
        PersonalIngressionData personalData = getOrCreatePersonalIngressionDataForCurrentExecutionYear(executionYear, getStudent(model));
        if (!isPartialUpdate()) {
            person.setEmissionLocationOfDocumentId(form.getDocumentIdEmissionLocation());
            LocalDate documentIdEmissionDate = form.getDocumentIdEmissionDate();
            LocalDate documentIdExpirationDate = form.getDocumentIdExpirationDate();
            person.setEmissionDateOfDocumentIdYearMonthDay(
                    documentIdEmissionDate != null ? new YearMonthDay(documentIdEmissionDate.toDate()) : null);
            person.setExpirationDateOfDocumentIdYearMonthDay(
                    documentIdExpirationDate != null ? new YearMonthDay(documentIdExpirationDate.toDate()) : null);

            String socialSecurityNumber = form.getSocialSecurityNumber();
            if (StringUtils.isEmpty(socialSecurityNumber)) {
                socialSecurityNumber = FenixEduAcademicConfiguration.getConfiguration().getDefaultSocialSecurityNumber();
            }
            person.setSocialSecurityNumber(socialSecurityNumber);
        }

        FirstTimeCandidacy candidacy = FirstTimeCandidacyController.getCandidacy();
        if (candidacy != null) {
            if (1 < candidacy.getPlacingOption()) {
                personUl.setFirstOptionInstitution(form.getFirstOptionInstitution());
                if (form.getFirstOptionDegreeDesignation() != null) {
                    personUl.setFirstOptionDegreeDesignation(form.getFirstOptionDegreeDesignation().getDescription());
                }
            }
        }

        if (!isPartialUpdate()) {
            if (form.getIsForeignStudent()) {
                person.setIdDocumentType(form.getIdDocumentType());
                person.setDocumentIdNumber(form.getDocumentIdNumber());
                personUl.setDgesTempIdCode("");
            }
        }

        person.setCountryHighSchool(form.getCountryHighSchool());

        if (person.getIdDocumentType() != null && person.getIdDocumentType() == IDDocumentType.IDENTITY_CARD
                && !isIdentityCardControlNumberValid(getIdentityCardControlNumber(person))) {
            setIdentityCardControlNumber(person, form.getIdentificationDocumentSeriesNumber());
        }

    }

    public boolean isPartialUpdate() {
        return false;
    }

    public static class DegreeDesignationBean {
        private final String degreeDesignationText;
        private final String degreeDesignationId;

        public DegreeDesignationBean(String degreeDesignationText, String degreeDesignationId) {
            super();
            this.degreeDesignationText = degreeDesignationText;
            this.degreeDesignationId = degreeDesignationId;
        }

        public String getDegreeDesignationText() {
            return degreeDesignationText;
        }

        public String getDegreeDesignationId() {
            return degreeDesignationId;
        }
    }

    public static class PersonalInformationForm implements Serializable {
        private static final long serialVersionUID = 1L;

        //can be either the series number or the extra digit
        private String identificationDocumentSeriesNumber;
        private String documentIdEmissionLocation;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate documentIdEmissionDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate documentIdExpirationDate;
        private String socialSecurityNumber;
        private ProfessionalSituationConditionType professionalCondition;
        private String profession;
        private ProfessionType professionType;
        private ProfessionTimeType professionTimeType;
        private GrantOwnerType grantOwnerType;
        private String grantOwnerProvider;

        private Unit firstOptionInstitution;
        private DegreeDesignation firstOptionDegreeDesignation;
        private String documentIdNumber;

        private IDDocumentType idDocumentType;

        private Country countryHighSchool;

        private boolean firstYearRegistration;

        /* Read only */
        private String name;
        private String username;
        private Gender gender;

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

        public static long getSerialversionuid() {
            return serialVersionUID;
        }

        public Country getCountryHighSchool() {
            return countryHighSchool;
        }

        public void setCountryHighSchool(Country countryHighSchool) {
            this.countryHighSchool = countryHighSchool;
        }

        public Unit getFirstOptionInstitution() {
            return firstOptionInstitution;
        }

        public void setFirstOptionInstitution(Unit firstOptionInstitution) {
            this.firstOptionInstitution = firstOptionInstitution;
        }

        public DegreeDesignation getFirstOptionDegreeDesignation() {
            return firstOptionDegreeDesignation;
        }

        public void setFirstOptionDegreeDesignation(DegreeDesignation firstOptionDegreeDesignation) {
            this.firstOptionDegreeDesignation = firstOptionDegreeDesignation;
        }

        public boolean getIsForeignStudent() {
            Country nationality = AccessControl.getPerson().getCountry();
            return nationality == null || !nationality.isDefaultCountry();
        }

        public boolean isFirstYearRegistration() {
            return firstYearRegistration;
        }

        public void setFirstYearRegistration(boolean firstYearRegistration) {
            this.firstYearRegistration = firstYearRegistration;
        }

        public String getIdentificationDocumentSeriesNumber() {
            return identificationDocumentSeriesNumber;
        }

        public void setIdentificationDocumentSeriesNumber(String identificationDocumentSeriesNumber) {
            this.identificationDocumentSeriesNumber = identificationDocumentSeriesNumber;
        }
    }
}
