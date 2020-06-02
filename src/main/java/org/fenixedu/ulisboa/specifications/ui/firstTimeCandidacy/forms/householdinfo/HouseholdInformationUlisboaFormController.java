package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.GrantOwnerType;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecificationsByExecutionYear;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.FormAbstractController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.health.DisabilitiesFormController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.qualification.OriginInformationFormController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(HouseholdInformationUlisboaFormController.CONTROLLER_URL)
public class HouseholdInformationUlisboaFormController extends FormAbstractController {

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/householdinformationulisboaform";

    public static final String FILL_URL = CONTROLLER_URL + _FILL_URI;

    protected static final String SHOW_GRANT_OWNER_TYPE = "showGrantOwnerType";
    protected static final String SHOW_COUNTRY_HIGHSCHOOL = "showCountryHighschool";

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    protected String getFormVariableName() {
        return "householdInformationByExecutionYearForm";
    }

    @Override
    protected String fillGetScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {

        HouseholdInformationUlisboaForm form = fillFormIfRequired(executionYear, model);

        if (!model.containsAttribute("postAction")) {
            model.addAttribute("postAction", "fill");
        }

        if (getForm(model) == null) {
            setForm(form, model);
        }

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillHouseHoldInformationUlisboa.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/householdinformationulisboaform/fillhouseholdinformationulisboa";
    }

    public HouseholdInformationUlisboaForm fillFormIfRequired(final ExecutionYear executionYear, final Model model) {
        HouseholdInformationUlisboaForm form = (HouseholdInformationUlisboaForm) getForm(model);
        if (form == null) {
            form = createHouseholdInformationForm(getStudent(model), executionYear);

            setForm(form, model);
        }
        return form;
    }

    protected HouseholdInformationUlisboaForm createEmptyHouseholdInformationForm(final Student student,
            final ExecutionYear executionYear, final Model model) {
        final HouseholdInformationUlisboaForm form = new HouseholdInformationUlisboaForm();

        return form;
    }

    protected HouseholdInformationUlisboaForm createHouseholdInformationForm(final Student student,
            final ExecutionYear executionYear) {
        return createHouseholdInformationForm(student, executionYear, true);
    }

    protected HouseholdInformationUlisboaForm createHouseholdInformationForm(final Student student,
            final ExecutionYear executionYear, final boolean initDTOs) {
        final HouseholdInformationUlisboaForm form = new HouseholdInformationUlisboaForm(initDTOs);
        final PersonalIngressionData personalData = getPersonalIngressionData(student, executionYear, false);

        PersonUlisboaSpecifications personUl = student.getPerson().getPersonUlisboaSpecifications();
        Person person = student.getPerson();

        if (personalData != null) {
            form.setGrantOwnerType(personalData.getGrantOwnerType());
        }
        if (form.getGrantOwnerType() == null) {
            form.setGrantOwnerType(GrantOwnerType.STUDENT_WITHOUT_SCHOLARSHIP);
        }

        if (personUl != null) {
            form.setBestQualitiesInThisCicle(personUl.getBestQualitiesInThisCicle());

            form.setFlunkedBeforeUniversity(personUl.getFlunkedBeforeUniversity());
            form.setFlunkedHighSchool(personUl.getFlunkedHighSchool());
            form.setFlunkedHighSchoolTimes(personUl.getFlunkedHighSchoolTimes());
            form.setFlunkedPreHighSchool(personUl.getFlunkedPreHighSchool());
            form.setFlunkedPreHighSchoolTimes(personUl.getFlunkedPreHighSchoolTimes());

            form.setSocialBenefitsInHighSchool(personUl.getSocialBenefitsInHighSchool());
            form.setSocialBenefitsInHighSchoolDescription(personUl.getSocialBenefitsInHighSchoolDescription());

            form.setFirstTimeInPublicUniv(personUl.getFirstTimeInPublicUniv());
            form.setPublicUnivCandidacies(personUl.getPublicUnivCandidacies());
            form.setFirstTimeInUlisboa(personUl.getFirstTimeInUlisboa());
            PersonUlisboaSpecificationsByExecutionYear personUlExecutionYear =
                    personUl.getPersonUlisboaSpecificationsByExcutionYear(executionYear);
            if (personUlExecutionYear != null) {
                form.setFlunkedUniversity(personUlExecutionYear.getFlunkedUniversity());
                form.setFlunkedUniversityTimes(personUlExecutionYear.getFlunkedUniversityTimes());
            }

        }

        form.setCountryHighSchool(person.getCountryHighSchool());

        return form;
    }

    @Override
    protected void fillPostScreen(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model,
            final RedirectAttributes redirectAttributes) {
        //nothing
    }

    @Override
    protected boolean validate(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model) {
        if (!(candidancyForm instanceof HouseholdInformationUlisboaForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.HouseholdInformationUlisboaFormController.wrong.form.type"),
                    model);
        }

        return validate((HouseholdInformationUlisboaForm) candidancyForm, model);
    }

    protected boolean validate(final HouseholdInformationUlisboaForm form, final Model model) {
        final Set<String> messages = validateForm(form);

        for (final String message : messages) {
            addErrorMessage(message, model);
        }

        return messages.isEmpty();
    }

    protected Set<String> validateForm(final HouseholdInformationUlisboaForm form) {
        final Set<String> messages = Sets.newLinkedHashSet();

        //FLUNKED BEFORE UNIV
        if (form.getFlunkedBeforeUniversity() != null) {
            if (Boolean.TRUE.equals(form.getFlunkedBeforeUniversity())) {
                //Check if values exists, otherwise put default
                if (form.getFlunkedHighSchoolTimes() == null) {
                    messages.add(BundleUtil.getString(BUNDLE, "error.HouseholdInformationForm.flunkedValues.required"));
                    form.setFlunkedHighSchoolTimes(0);
                }
                if (form.getFlunkedPreHighSchoolTimes() == null) {
                    messages.add(BundleUtil.getString(BUNDLE, "error.HouseholdInformationForm.flunkedValues.required"));
                    form.setFlunkedPreHighSchoolTimes(0);
                }

                if (form.getFlunkedHighSchoolTimes() < 1 && form.getFlunkedPreHighSchoolTimes() < 1) {
                    messages.add(BundleUtil.getString(BUNDLE, "error.HouseholdInformationForm.incorrect.flunked.value"));
                }

                if (form.getFlunkedHighSchoolTimes() == 0) {
                    form.setFlunkedHighSchool(Boolean.FALSE);
                } else if (form.getFlunkedHighSchoolTimes() < 0) {
                    messages.add(BundleUtil.getString(BUNDLE, "error.HouseholdInformationForm.flunkedHighSchoolTimes"));
                    form.setFlunkedHighSchool(Boolean.FALSE);
                } else {
                    form.setFlunkedHighSchool(Boolean.TRUE);
                }

                if (form.getFlunkedPreHighSchoolTimes() == 0) {
                    form.setFlunkedPreHighSchool(Boolean.FALSE);
                } else if (form.getFlunkedPreHighSchoolTimes() < 0) {
                    messages.add(BundleUtil.getString(BUNDLE, "error.HouseholdInformationForm.flunkedHighSchoolTimes"));
                    form.setFlunkedPreHighSchool(Boolean.FALSE);
                } else {
                    form.setFlunkedPreHighSchool(Boolean.TRUE);
                }
            } else {
                form.setFlunkedHighSchool(Boolean.FALSE);
                form.setFlunkedPreHighSchool(Boolean.FALSE);
                form.setFlunkedHighSchoolTimes(0);
                form.setFlunkedPreHighSchoolTimes(0);
            }
        } else {
            form.setFlunkedHighSchool(null);
            form.setFlunkedPreHighSchool(null);
            form.setFlunkedHighSchoolTimes(null);
            form.setFlunkedPreHighSchoolTimes(null);
        }

        // SOCIAL BENEFITS

        if (Boolean.TRUE.equals(form.getSocialBenefitsInHighSchool())
                && StringUtils.isBlank(form.getSocialBenefitsInHighSchoolDescription())) {
            messages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        }
        if (Boolean.FALSE.equals(form.getSocialBenefitsInHighSchool())) {
            form.setSocialBenefitsInHighSchoolDescription("");
        }

        // FIRST TIME APPLYING

        if (Boolean.FALSE.equals(form.getFirstTimeInPublicUniv()) && form.getPublicUnivCandidacies() == null) {
            messages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        } else if (Boolean.FALSE.equals(form.getFirstTimeInPublicUniv()) && form.getPublicUnivCandidacies() < 1) {
            messages.add(BundleUtil.getString(BUNDLE, "error.HouseholdInformationForm.publicUnivCandidacies"));
        } else if (Boolean.TRUE.equals(form.getFirstTimeInPublicUniv())) {
            form.setPublicUnivCandidacies(0);
        }

        // FLUNKED UNIVERSITY

        if (Boolean.TRUE.equals(form.getFlunkedUniversity()) && form.getFlunkedUniversityTimes() == null) {
            messages.add(BundleUtil.getString(BUNDLE, "error.all.fields.required"));
        } else if (Boolean.TRUE.equals(form.getFlunkedUniversity()) && form.getFlunkedUniversityTimes() < 1) {
            messages.add(BundleUtil.getString(BUNDLE, "error.HouseholdInformationForm.flunkedHighSchoolTimes"));
        } else if (Boolean.FALSE.equals(form.getFlunkedUniversity())) {
            form.setFlunkedUniversityTimes(0);
        }

        return messages;
    }

    @Override
    protected void writeData(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model) {
        writeData(getStudent(model), executionYear, (HouseholdInformationUlisboaForm) candidancyForm, model);
    }

    @Atomic
    protected void writeData(final Student student, final ExecutionYear executionYear, final HouseholdInformationUlisboaForm form,
            final Model model) {
        Person person = student.getPerson();
        boolean grantOwner = (boolean) model.asMap().get(SHOW_GRANT_OWNER_TYPE);

        if (grantOwner) {
            final PersonalIngressionData personalData = getPersonalIngressionData(student, executionYear, false);
            if (personalData == null) {
                throw new DomainException("error.no.PersonalIngressionData.created.for", student.getPerson().getUsername());
            }
            GrantOwnerType grantOwnerType = form.getGrantOwnerType();
            personalData.setGrantOwnerType(grantOwnerType);
        }

        PersonUlisboaSpecifications personUlisboa = PersonUlisboaSpecifications.findOrCreate(student.getPerson());
        if (form.getFlunkedHighSchool() != null) {
            personUlisboa.setFlunkedHighSchool(form.getFlunkedHighSchool());
        }
        if (form.getFlunkedHighSchoolTimes() != null) {
            personUlisboa.setFlunkedHighSchoolTimes(form.getFlunkedHighSchoolTimes());
        }
        if (form.getFlunkedPreHighSchool() != null) {
            personUlisboa.setFlunkedPreHighSchool(form.getFlunkedPreHighSchool());
        }
        if (form.getFlunkedPreHighSchoolTimes() != null) {
            personUlisboa.setFlunkedPreHighSchoolTimes(form.getFlunkedPreHighSchoolTimes());
        }

        if (form.getSocialBenefitsInHighSchool() != null) {
            personUlisboa.setSocialBenefitsInHighSchool(form.getSocialBenefitsInHighSchool());
        }
        if (form.getSocialBenefitsInHighSchoolDescription() != null) {
            personUlisboa.setSocialBenefitsInHighSchoolDescription(form.getSocialBenefitsInHighSchoolDescription());
        }

        if (form.getFirstTimeInPublicUniv() != null) {
            personUlisboa.setFirstTimeInPublicUniv(form.getFirstTimeInPublicUniv());
        }
        if (form.getPublicUnivCandidacies() != null) {
            personUlisboa.setPublicUnivCandidacies(form.getPublicUnivCandidacies());
        }
        if (form.getFirstTimeInUlisboa() != null) {
            personUlisboa.setFirstTimeInUlisboa(form.getFirstTimeInUlisboa());
        }

        if (form.getBestQualitiesInThisCicle() != null) {
            personUlisboa.setBestQualitiesInThisCicle(form.getBestQualitiesInThisCicle());
        }

        PersonUlisboaSpecificationsByExecutionYear personUlExecutionYear =
                PersonUlisboaSpecificationsByExecutionYear.findOrCreate(student.getPerson(), executionYear);
        if (form.getFlunkedUniversity() != null) {
            personUlExecutionYear.setFlunkedUniversity(form.getFlunkedUniversity());
        }
        if (form.getFlunkedUniversityTimes() != null) {
            personUlExecutionYear.setFlunkedUniversityTimes(form.getFlunkedUniversityTimes());
        }

        if (form.getCountryHighSchool() != null) {
            person.setCountryHighSchool(form.getCountryHighSchool());
        }

    }

    @Override
    protected String backScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(OriginInformationFormController.CONTROLLER_URL, executionYear), model,
                redirectAttributes);
    }

    @Override
    protected String nextScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(DisabilitiesFormController.CONTROLLER_URL, executionYear), model,
                redirectAttributes);
    }

    @Override
    public boolean isFormIsFilled(final ExecutionYear executionYear, final Student student) {
        return false;
    }

    @Override
    protected Student getStudent(final Model model) {
        return AccessControl.getPerson().getStudent();
    }

    @ModelAttribute
    public void addDefaultAttributes(final Model model) {
        model.addAttribute(SHOW_GRANT_OWNER_TYPE, true);
        model.addAttribute(SHOW_COUNTRY_HIGHSCHOOL, false);
    }

}
