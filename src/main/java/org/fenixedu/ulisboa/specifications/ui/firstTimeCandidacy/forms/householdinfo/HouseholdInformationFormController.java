package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.Set;

import org.fenixedu.academic.domain.ExecutionYear;
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
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.contacts.ContactsFormController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(HouseholdInformationFormController.CONTROLLER_URL)
public class HouseholdInformationFormController extends FormAbstractController {

    protected static final String SHOW_DISLOCATED_QUESTION = "showDislocated";

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/householdinformationform";

    public static final String FILL_URL = CONTROLLER_URL + _FILL_URI;

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    protected String getFormVariableName() {
        return "householdInformationForm";
    }

    @Override
    protected String fillGetScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {

        HouseholdInformationForm form = fillFormIfRequired(executionYear, model);

        if (!model.containsAttribute("postAction")) {
            model.addAttribute("postAction", "fill");
        }

        if (getForm(model) == null) {
            setForm(form, model);
        }

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillHouseHoldInformation.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/householdinformationform/fillhouseholdinformation";
    }

    public HouseholdInformationForm fillFormIfRequired(final ExecutionYear executionYear, final Model model) {
        HouseholdInformationForm form = (HouseholdInformationForm) getForm(model);
        if (form == null) {
            form = createHouseholdInformationForm(getStudent(model), executionYear, false);

            setForm(form, model);
        }
        return form;
    }

    protected HouseholdInformationForm createEmptyHouseholdInformationForm(final Student student, final Model model) {
        final HouseholdInformationForm form = new HouseholdInformationForm();

        return form;
    }

    protected HouseholdInformationForm createHouseholdInformationForm(final Student student, final ExecutionYear executionYear,
            final boolean create) {
        final PersonalIngressionData personalData = getPersonalIngressionData(student, executionYear, create);

        final HouseholdInformationForm form = new HouseholdInformationForm();

        if (personalData != null) {
            form.setFatherProfessionalCondition(personalData.getFatherProfessionalCondition());
            form.setFatherProfessionType(personalData.getFatherProfessionType());
            form.setFatherSchoolLevel(personalData.getFatherSchoolLevel());
            form.setMotherProfessionalCondition(personalData.getMotherProfessionalCondition());
            form.setMotherProfessionType(personalData.getMotherProfessionType());
            form.setMotherSchoolLevel(personalData.getMotherSchoolLevel());

            form.setDislocatedFromPermanentResidence(personalData.getDislocatedFromPermanentResidence());
        }

        PersonUlisboaSpecifications personUl = student.getPerson().getPersonUlisboaSpecifications();
        if (personUl != null) {
            PersonUlisboaSpecificationsByExecutionYear personUlExecutionYear =
                    personUl.getPersonUlisboaSpecificationsByExcutionYear(executionYear);
            if (personUlExecutionYear != null) {
                form.setLivesAlone(personUlExecutionYear.getLivesAlone());
                if (personUlExecutionYear.getLivesWithFather() != null && personUlExecutionYear.getLivesWithMother() != null) {
                    form.setLivesWithParents(
                            personUlExecutionYear.getLivesWithMother() && personUlExecutionYear.getLivesWithFather());
                } else {
                    form.setLivesWithParents(Boolean.FALSE);
                }
                form.setLivesWithBrothers(personUlExecutionYear.getLivesWithBrothers());
                form.setLivesWithChildren(personUlExecutionYear.getLivesWithChildren());
                form.setLivesWithLifemate(personUlExecutionYear.getLivesWithLifemate());
                form.setLivesWithOthers(personUlExecutionYear.getLivesWithOthers());
                form.setLivesWithOthersDesc(personUlExecutionYear.getLivesWithOthersDesc());

                form.setNumBrothers(personUlExecutionYear.getNumBrothers());
                form.setNumChildren(personUlExecutionYear.getNumChildren());

                form.setHouseholdSalarySpan(personUlExecutionYear.getHouseholdSalarySpan());
            }
        }

        return form;
    }

    @Override
    protected void fillPostScreen(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model,
            final RedirectAttributes redirectAttributes) {
        //nothing
    }

    @Override
    protected boolean validate(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model) {
        if (!(candidancyForm instanceof HouseholdInformationForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.HouseholdInformationFormController.wrong.form.type"), model);
        }

        return validate((HouseholdInformationForm) candidancyForm, model);
    }

    protected boolean validate(final HouseholdInformationForm form, final Model model) {
        final Set<String> messages = validateForm(form);

        for (final String message : messages) {
            addErrorMessage(message, model);
        }

        return messages.isEmpty();
    }

    protected Set<String> validateForm(final HouseholdInformationForm form) {
        final Set<String> messages = Sets.newLinkedHashSet();

        if (form.getFatherProfessionalCondition() == null || form.getFatherProfessionType() == null
                || form.getFatherSchoolLevel() == null) {
            messages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        }
        if (form.getMotherProfessionalCondition() == null || form.getMotherProfessionType() == null
                || form.getMotherSchoolLevel() == null) {
            messages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        }

        if (form.getHouseholdSalarySpan() == null) {
            messages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        }

        //DISLOCATED

        if (form.getDislocatedFromPermanentResidence() == null) {
            messages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        }

        // HOUSEHOLD

        if (form.getLivesAlone() != null && form.getLivesAlone() == Boolean.FALSE) {
            boolean livesAlone = true;

            livesAlone = livesAlone && (form.getLivesWithParents() == null || form.getLivesWithParents() == Boolean.FALSE);
            livesAlone = livesAlone && (form.getLivesWithBrothers() == null || form.getLivesWithBrothers() == Boolean.FALSE);
            livesAlone = livesAlone && (form.getLivesWithChildren() == null || form.getLivesWithChildren() == Boolean.FALSE);
            livesAlone = livesAlone && (form.getLivesWithLifemate() == null || form.getLivesWithLifemate() == Boolean.FALSE);
            livesAlone = livesAlone && (form.getLivesWithOthers() == null || form.getLivesWithOthers() == Boolean.FALSE);

            if (livesAlone) {
                messages.add(BundleUtil.getString(BUNDLE, "error.HouseholdInformationForm.livesWith.must.have.one"));
            }
        }

        if (form.getLivesWithOthers() && form.getLivesWithOthersDesc().isEmpty()) {
            messages.add(BundleUtil.getString(BUNDLE, "error.HouseholdInformationForm.livesWith.other.must.be.filled"));
        }

        if (form.getNumBrothers() == null) {
            messages.add(BundleUtil.getString(BUNDLE, "error.all.fields.required"));
        }
        if (form.getNumChildren() == null) {
            messages.add(BundleUtil.getString(BUNDLE, "error.all.fields.required"));
        }

        if (form.getNumBrothers() != null && form.getNumBrothers() < 0) {
            messages.add(BundleUtil.getString(BUNDLE, "error.HouseholdInformationForm.numBrothers.must.be.positive"));
        }
        if (form.getNumChildren() != null && form.getNumChildren() < 0) {
            messages.add(BundleUtil.getString(BUNDLE, "error.HouseholdInformationForm.numChildren.must.be.positive"));
        }

        return messages;
    }

    protected boolean isProfessionRequired() {
        return true;
    }

    @Override
    protected void writeData(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model) {
        writeData(getStudent(model), executionYear, (HouseholdInformationForm) candidancyForm, model);
    }

    @Atomic
    protected void writeData(final Student student, final ExecutionYear executionYear, final HouseholdInformationForm form,
            final Model model) {
        PersonalIngressionData personalData = getPersonalIngressionData(student, executionYear, false);
        PersonUlisboaSpecificationsByExecutionYear personUlExecutionYear =
                PersonUlisboaSpecificationsByExecutionYear.findOrCreate(student.getPerson(), executionYear);

        personalData.setFatherProfessionalCondition(form.getFatherProfessionalCondition());
        personalData.setFatherProfessionType(form.getFatherProfessionType());
        personalData.setFatherSchoolLevel(form.getFatherSchoolLevel());
        personalData.setMotherProfessionalCondition(form.getMotherProfessionalCondition());
        personalData.setMotherProfessionType(form.getMotherProfessionType());
        personalData.setMotherSchoolLevel(form.getMotherSchoolLevel());

        if (form.getHouseholdSalarySpan() != null) {
            personUlExecutionYear.setHouseholdSalarySpan(form.getHouseholdSalarySpan());
        }

        if (form.getDislocatedFromPermanentResidence() != null) {
            personalData.setDislocatedFromPermanentResidence(form.getDislocatedFromPermanentResidence());
        }

        if (form.getLivesAlone() != null) {
            personUlExecutionYear.setLivesAlone(form.getLivesAlone());
        }

        if (form.getLivesWithParents() != null) {
            personUlExecutionYear.setLivesWithMother(form.getLivesWithParents());
            personUlExecutionYear.setLivesWithFather(form.getLivesWithParents());
        }
        if (form.getLivesWithBrothers() != null) {
            personUlExecutionYear.setLivesWithBrothers(form.getLivesWithBrothers());
        }
        if (form.getLivesWithChildren() != null) {
            personUlExecutionYear.setLivesWithChildren(form.getLivesWithChildren());
        }
        if (form.getLivesWithLifemate() != null) {
            personUlExecutionYear.setLivesWithLifemate(form.getLivesWithLifemate());
        }
        if (form.getLivesWithOthers() != null) {
            personUlExecutionYear.setLivesWithOthers(form.getLivesWithOthers());
        }
        if (form.getLivesWithOthersDesc() != null) {
            personUlExecutionYear.setLivesWithOthersDesc(form.getLivesWithOthersDesc());
        }

        if (form.getNumBrothers() != null) {
            personUlExecutionYear.setNumBrothers(form.getNumBrothers());
        }
        if (form.getNumChildren() != null) {
            personUlExecutionYear.setNumChildren(form.getNumChildren());
        }

    }

    @Override
    protected String backScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(ContactsFormController.CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    @Override
    protected String nextScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(ProfessionalInformationFormController.CONTROLLER_URL, executionYear), model,
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
        model.addAttribute(SHOW_DISLOCATED_QUESTION, false);
    }

}
