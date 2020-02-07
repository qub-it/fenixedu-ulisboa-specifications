package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.personalinfo;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.person.IDDocumentType;
import org.fenixedu.academic.domain.person.MaritalStatus;
import org.fenixedu.academic.domain.raides.DegreeDesignation;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
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

        StudentCandidacy candidacy = FirstTimeCandidacyController.getCandidacy();
        if (candidacy != null) {
            model.addAttribute("placingOption", candidacy.getPlacingOption());
        }

        PersonalInformationForm form = fillFormIfRequired(executionYear, model);

        if (getForm(model) == null) {
            setForm(form, model);
        }

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillPersonalInformation.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/personalinformationform/fillpersonalinformation";
    }

    public PersonalInformationForm fillFormIfRequired(final ExecutionYear executionYear, final Model model) {
        Person person = getStudent(model).getPerson();

        PersonalInformationForm form = (PersonalInformationForm) getForm(model);
        if (form != null) {
            form.setDocumentIdNumber(person.getDocumentIdNumber());
            form.setIdDocumentType(person.getIdDocumentType());

            if (person.getIdDocumentType() == IDDocumentType.IDENTITY_CARD) {
                final String digitControl = IdentityCardUtils.getDigitControlFromPerson(person);
                if (StringUtils.isNotBlank(digitControl)) {
                    form.setIdentificationDocumentSeriesNumber(digitControl);
                }
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
        final PersonalIngressionData personalData = getPersonalIngressionData(student, executionYear, true);

        form.setDocumentIdEmissionLocation(person.getEmissionLocationOfDocumentId());
        YearMonthDay emissionDateOfDocumentIdYearMonthDay = person.getEmissionDateOfDocumentIdYearMonthDay();
        YearMonthDay expirationDateOfDocumentIdYearMonthDay = person.getExpirationDateOfDocumentIdYearMonthDay();
        form.setDocumentIdEmissionDate(emissionDateOfDocumentIdYearMonthDay != null ? new LocalDate(
                person.getEmissionDateOfDocumentIdYearMonthDay().toDateMidnight()) : null);
        form.setDocumentIdExpirationDate(expirationDateOfDocumentIdYearMonthDay != null ? new LocalDate(
                expirationDateOfDocumentIdYearMonthDay.toDateMidnight()) : null);

        YearMonthDay dateOfBirthYearMonthDay = person.getDateOfBirthYearMonthDay();
        form.setDateOfBirth(dateOfBirthYearMonthDay != null ? dateOfBirthYearMonthDay.toLocalDate() : null);

        form.setDocumentIdNumber(person.getDocumentIdNumber());
        form.setIdDocumentType(person.getIdDocumentType());

        final String digitControl = IdentityCardUtils.getDigitControlFromPerson(person);
        if (!Strings.isNullOrEmpty(digitControl)) {
            form.setIdentificationDocumentSeriesNumber(digitControl);
        }

        if (personalData != null) {
            form.setMaritalStatus(personalData.getMaritalStatus());

            if (form.getMaritalStatus() == null) {
                form.setMaritalStatus(MaritalStatus.SINGLE);
            }
        }

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

    protected Set<String> validateForm(final PersonalInformationForm form, final Person person) {
        final Set<String> result = Sets.newLinkedHashSet();

        if (person.getIdDocumentType() != null && person.getIdDocumentType() == IDDocumentType.IDENTITY_CARD
                && !IdentityCardUtils.validate(person.getDocumentIdNumber(), IdentityCardUtils.getDigitControlFromPerson(person))
                && !IdentityCardUtils.validate(person.getDocumentIdNumber(), form.getIdentificationDocumentSeriesNumber())) {
            result.add(ULisboaSpecificationsUtil
                    .bundle("error.candidacy.workflow.PersonalInformationForm.incorrect.identificationSeriesNumber"));
        }

        final Collection<Person> found = Person.readByDocumentIdNumber(form.getDocumentIdNumber());
        if (!found.isEmpty()) {
            if (found.size() != 1 || found.iterator().next() != Authenticate.getUser().getPerson()) {
                result.add(ULisboaSpecificationsUtil
                        .bundle("error.candidacy.workflow.PersonalInformationForm.identificationDocumentNumber.exists"));
            }
        }

        if (form.getDateOfBirth() == null) {
            result.add(BundleUtil.getString(BUNDLE, "error.birthDate.required"));

        } else if (form.getDateOfBirth().isAfter(new LocalDate())) {
            result.add(BundleUtil.getString(BUNDLE, "error.birthDate.invalid"));
        }

        if (!isPartialUpdate()) {
            IDDocumentType idType = form.getIdDocumentType();
            if (form.getIsForeignStudent()) {
                if (idType == null) {
                    result.add(BundleUtil.getString(BUNDLE, "error.documentIdType.required"));
                }

                if (StringUtils.isBlank(form.getDocumentIdNumber())) {
                    result.add(BundleUtil.getString(BUNDLE, "error.documentIdNumber.required"));
                }
            }

            if (form.getDocumentIdExpirationDate() == null) {
                result.add(BundleUtil.getString(BUNDLE, "error.expirationDate.required"));
            }

            if (form.getDocumentIdExpirationDate() != null && form.getDocumentIdEmissionDate() != null
                    && !form.getDocumentIdExpirationDate().isAfter(form.getDocumentIdEmissionDate())) {
                result.add(BundleUtil.getString(BUNDLE, "error.expirationDate.is.after.emissionDate"));
            }

        }

        if (form.getCountryHighSchool() == null) {
            result.add(
                    BundleUtil.getString(BUNDLE, "error.candidacy.workflow.PersonalInformationForm.countryHighSchool.required"));
        }

        return result;
    }

    @Override
    public void fillPostScreen(final ExecutionYear executionYear, final CandidancyForm form, final Model model,
            final RedirectAttributes redirectAttributes) {
        //nothing
    }

    @Override
    protected boolean validate(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model) {
        if (!(candidancyForm instanceof PersonalInformationForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.FormPersonalInformationController.wrong.form.type"), model);
            return false;
        }
        return validate((PersonalInformationForm) candidancyForm, model);
    }

    private boolean validate(final PersonalInformationForm form, final Model model) {
        final Set<String> result = validateForm(form, getStudent(model).getPerson());

        for (final String message : result) {
            addErrorMessage(message, model);
        }

        return result.isEmpty();
    }

    @Override
    protected void writeData(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model) {
        writeData(executionYear, (PersonalInformationForm) candidancyForm, model);
    }

    @Atomic
    private void writeData(final ExecutionYear executionYear, final PersonalInformationForm form, final Model model) {
        Person person = AccessControl.getPerson();
        PersonUlisboaSpecifications personUl = PersonUlisboaSpecifications.findOrCreate(person);
        PersonalIngressionData personalData = getPersonalIngressionData(getStudent(model), executionYear, false);

        person.setDateOfBirthYearMonthDay(new YearMonthDay(form.getDateOfBirth()));
        if (!isPartialUpdate()) {
            person.setEmissionLocationOfDocumentId(form.getDocumentIdEmissionLocation());
            LocalDate documentIdEmissionDate = form.getDocumentIdEmissionDate();
            LocalDate documentIdExpirationDate = form.getDocumentIdExpirationDate();
            person.setEmissionDateOfDocumentIdYearMonthDay(
                    documentIdEmissionDate != null ? new YearMonthDay(documentIdEmissionDate.toDate()) : null);
            person.setExpirationDateOfDocumentIdYearMonthDay(
                    documentIdExpirationDate != null ? new YearMonthDay(documentIdExpirationDate.toDate()) : null);
        }

        StudentCandidacy candidacy = FirstTimeCandidacyController.getCandidacy();
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

        if (person.getIdDocumentType() != null && person.getIdDocumentType() == IDDocumentType.IDENTITY_CARD && !IdentityCardUtils
                .validate(person.getDocumentIdNumber(), IdentityCardUtils.getDigitControlFromPerson(person))) {
            setIdentityCardControlNumber(person, form.getIdentificationDocumentSeriesNumber());
        }

        person.setGender(form.getGender());

        personalData.getStudent().getPerson().setMaritalStatus(form.getMaritalStatus());
        personalData.setMaritalStatus(form.getMaritalStatus());

    }

    @Override
    protected String backScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        return redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes);
    }

    @Override
    protected String nextScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(FiliationFormController.CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    protected void setIdentityCardControlNumber(final Person person, final String number) {
        person.setIdentificationDocumentSeriesNumber(number);
    }

    public boolean isPartialUpdate() {
        //TODOJN - how to process this
        return false;
    }

    @Override
    public boolean isFormIsFilled(final ExecutionYear executionYear, final Student student) {
        return false;
    }

    @Override
    protected Student getStudent(final Model model) {
        return AccessControl.getPerson().getStudent();
    }

}
