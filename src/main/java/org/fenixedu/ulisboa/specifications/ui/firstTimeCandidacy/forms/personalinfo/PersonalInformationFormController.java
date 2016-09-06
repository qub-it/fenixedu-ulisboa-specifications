package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.personalinfo;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.FenixEduAcademicConfiguration;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.organizationalStructure.Party;
import org.fenixedu.academic.domain.organizationalStructure.PartySocialSecurityNumber;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.person.IDDocumentType;
import org.fenixedu.academic.domain.raides.DegreeDesignation;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.treasury.util.FiscalCodeValidation;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.domain.candidacy.FirstTimeCandidacy;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.FormAbstractController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.filiation.FiliationFormController;
import org.fenixedu.ulisboa.specifications.util.IdentityCardUtils;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.LocalDate;
import org.joda.time.YearMonthDay;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(PersonalInformationFormController.CONTROLLER_URL)
public class PersonalInformationFormController extends FormAbstractController {

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/personalinformationform";

    public static final String FILL_URL = CONTROLLER_URL + _FILL_URI;

    private static final String SOCIAL_SECURITY_NUMBER_FORMAT = "\\d{9}";

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    protected String getFormVariableName() {
        return "personalInformationForm";
    }

    @Override
    public String fillGetScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {

        model.addAttribute("partial", isPartialUpdate());

        FirstTimeCandidacy candidacy = FirstTimeCandidacyController.getCandidacy();
        if (candidacy != null) {
            model.addAttribute("placingOption", candidacy.getPlacingOption());
        }

        PersonalInformationForm form = fillFormIfRequired(executionYear, model);

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillPersonalInformation.info"), model);

        if (getForm(model) == null) {
            setForm(form, model);
        }

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/personalinformationform/fillpersonalinformation";
    }

    public PersonalInformationForm fillFormIfRequired(final ExecutionYear executionYear, Model model) {
        Person person = getStudent(model).getPerson();

        model.addAttribute("identityCardExtraDigitRequired", person.getIdDocumentType() == IDDocumentType.IDENTITY_CARD
                && !isIdentityCardControlNumberValid(person.getDocumentIdNumber(), getIdentityCardControlNumber(person)));

        PersonalInformationForm form = (PersonalInformationForm) getForm(model);
        if (form != null) {
            form.setDocumentIdNumber(person.getDocumentIdNumber());
            form.setIdDocumentType(person.getIdDocumentType());

            if (person.getIdDocumentType() == IDDocumentType.IDENTITY_CARD) {
                form.setIdentificationDocumentSeriesNumber(getIdentityCardControlNumber(person));
            }

            fillstaticformdata(executionYear, getStudent(model), form);

            setForm(form, model);
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

    private void fillstaticformdata(final ExecutionYear executionYear, final Student student,
            final PersonalInformationForm form) {
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
                && !isIdentityCardControlNumberValid(person.getDocumentIdNumber(), getIdentityCardControlNumber(person))
                && !isIdentityCardControlNumberValid(person.getDocumentIdNumber(),
                        form.getIdentificationDocumentSeriesNumber())) {
            result.add(ULisboaSpecificationsUtil
                    .bundle("error.candidacy.workflow.PersonalInformationForm.incorrect.identificationSeriesNumber"));
        }

        return result;
    }

    @Override
    public void fillPostScreen(final ExecutionYear executionYear, CandidancyForm form, Model model,
            RedirectAttributes redirectAttributes) {
        //nothing
    }

    @Override
    protected boolean validate(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        if (!(candidancyForm instanceof PersonalInformationForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.FormPersonalInformationController.wrong.form.type"), model);
            return false;
        }
        return validate((PersonalInformationForm) candidancyForm, model);
    }

    private boolean validate(PersonalInformationForm form, Model model) {

        final Set<String> result = validateForm(form, getStudent(model).getPerson());

        for (final String message : result) {
            addErrorMessage(message, model);
        }

        return result.isEmpty();
    }

    @Override
    protected void writeData(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        writeData(executionYear, (PersonalInformationForm) candidancyForm, model);
    }

    @Atomic
    private void writeData(final ExecutionYear executionYear, final PersonalInformationForm form, final Model model) {
        Person person = AccessControl.getPerson();
        PersonUlisboaSpecifications personUl = PersonUlisboaSpecifications.findOrCreate(person);
        PersonalIngressionData personalData =
                getOrCreatePersonalIngressionDataForCurrentExecutionYear(executionYear, getStudent(model));
        //TODOJN - dirty hack to go around PrecedentDegreeInformation.checkHasAllRegistrationOrPhdInformation()
        FirstTimeCandidacyController.getOrCreatePersonalIngressionData(executionYear,
                personalData.getPrecedentDegreesInformationsSet().iterator().next());

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
                && !isIdentityCardControlNumberValid(person.getDocumentIdNumber(), getIdentityCardControlNumber(person))) {
            setIdentityCardControlNumber(person, form.getIdentificationDocumentSeriesNumber());
        }

    }

    @Override
    protected String backScreen(final ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        return redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes);
    }

    @Override
    protected String nextScreen(final ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(FiliationFormController.CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    protected String getIdentityCardControlNumber(final Person person) {
        if (!Strings.isNullOrEmpty(person.getIdentificationDocumentSeriesNumberValue())) {
            return person.getIdentificationDocumentSeriesNumberValue();
        }

        return person.getIdentificationDocumentExtraDigitValue();
    }

    protected boolean isIdentityCardControlNumberValid(final String idDocumentNumber, final String extraValue) {
        return IdentityCardUtils.isIdentityCardDigitControlFormatValid(extraValue)
                && IdentityCardUtils.validate(idDocumentNumber, extraValue);
    }

    protected void setIdentityCardControlNumber(final Person person, final String number) {
        person.setIdentificationDocumentSeriesNumber(number);
    }

    public boolean isPartialUpdate() {
        //TODOJN - how to process this
        return false;
    }

    @Override
    public boolean isFormIsFilled(ExecutionYear executionYear, Student student) {
        return false;
    }

    @Override
    protected Student getStudent(Model model) {
        return AccessControl.getPerson().getStudent();
    }

}
