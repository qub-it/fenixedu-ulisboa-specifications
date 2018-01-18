package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.GrantOwnerType;
import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.FormAbstractController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.filiation.FiliationFormController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

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

        PersonUlisboaSpecifications personUl = student.getPerson().getPersonUlisboaSpecifications();
        if (personUl != null) {
            form.setProfessionTimeType(personUl.getProfessionTimeType());
            form.setHouseholdSalarySpan(personUl.getHouseholdSalarySpan());
        }

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

            form.setProfessionType(personalData.getProfessionType());
            form.setGrantOwnerType(personalData.getGrantOwnerType());

            Unit grantOwnerProvider = personalData.getGrantOwnerProvider();
            form.setGrantOwnerProvider(grantOwnerProvider != null ? grantOwnerProvider.getExternalId() : null);
            form.setProfessionalCondition(personalData.getProfessionalCondition());
        }

        if (form.getProfessionType() == null) {
            form.setProfessionType(ProfessionType.OTHER);
        }
        if (form.getGrantOwnerType() == null) {
            form.setGrantOwnerType(GrantOwnerType.STUDENT_WITHOUT_SCHOLARSHIP);
        }

        if (form.getProfessionalCondition() == null) {
            form.setProfessionalCondition(ProfessionalSituationConditionType.STUDENT);
        }

        form.setProfession(student.getPerson().getProfession());

        PersonUlisboaSpecifications personUl = student.getPerson().getPersonUlisboaSpecifications();
        if (personUl != null) {
            form.setProfessionTimeType(personUl.getProfessionTimeType());
            form.setHouseholdSalarySpan(personUl.getHouseholdSalarySpan());
        }

        form.setDislocatedFromPermanentResidence(personalData.getDislocatedFromPermanentResidence());

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

        if (form.getProfessionalCondition() == null || form.getProfessionType() == null) {
            messages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        }

        if (form.getHouseholdSalarySpan() == null) {
            messages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        }

        if (form.isStudentWorking()) {
            if (StringUtils.isEmpty(form.getProfession()) && isProfessionRequired()) {
                messages.add(
                        BundleUtil.getString(BUNDLE, "error.candidacy.workflow.PersonalInformationForm.profession.required"));
            }

            if (form.getProfessionTimeType() == null && isProfessionRequired()) {
                messages.add(BundleUtil.getString(BUNDLE,
                        "error.candidacy.workflow.PersonalInformationForm.professionTimeType.required"));
            }
        }

        GrantOwnerType grantOwnerType = form.getGrantOwnerType();
        if (grantOwnerType != null) {
            String grantOwnerProvider = form.getGrantOwnerProvider();
            String otherGrantOwnerProvider = form.getOtherGrantOwnerProvider();

            if (grantOwnerProvider == null && (grantOwnerType.equals(GrantOwnerType.OTHER_INSTITUTION_GRANT_OWNER)
                    || grantOwnerType.equals(GrantOwnerType.ORIGIN_COUNTRY_GRANT_OWNER))) {
                if (StringUtils.isBlank(otherGrantOwnerProvider)) {
                    messages.add(BundleUtil.getString(BUNDLE,
                            "error.candidacy.workflow.PersonalInformationForm.grant.owner.must.choose.granting.institution"));
                }
            } else if (!grantOwnerType.equals(GrantOwnerType.STUDENT_WITHOUT_SCHOLARSHIP)
                    && !grantOwnerType.equals(GrantOwnerType.HIGHER_EDUCATION_SAS_GRANT_OWNER_CANDIDATE)) {
                if (FenixFramework.getDomainObject(grantOwnerProvider) == null) {
                    messages.add(BundleUtil.getString(BUNDLE,
                            "error.candidacy.workflow.PersonalInformationForm.grant.owner.must.choose.granting.institution"));
                }
            }
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
        PersonUlisboaSpecifications personUlisboa = PersonUlisboaSpecifications.findOrCreate(student.getPerson());

        personalData.setFatherProfessionalCondition(form.getFatherProfessionalCondition());
        personalData.setFatherProfessionType(form.getFatherProfessionType());
        personalData.setFatherSchoolLevel(form.getFatherSchoolLevel());
        personalData.setMotherProfessionalCondition(form.getMotherProfessionalCondition());
        personalData.setMotherProfessionType(form.getMotherProfessionType());
        personalData.setMotherSchoolLevel(form.getMotherSchoolLevel());
        personalData.setProfessionalCondition(form.getProfessionalCondition());
        personalData.getStudent().getPerson().setProfession(form.getProfession());
        personalData.setProfessionType(form.getProfessionType());
        personUlisboa.setProfessionTimeType(form.getProfessionTimeType());

        GrantOwnerType grantOwnerType = form.getGrantOwnerType();
        personalData.setGrantOwnerType(grantOwnerType);
        if (grantOwnerType != null && !grantOwnerType.equals(GrantOwnerType.STUDENT_WITHOUT_SCHOLARSHIP)) {
            Unit grantOwnerProvider = FenixFramework.getDomainObject(form.getGrantOwnerProvider());
            if (grantOwnerProvider == null && (grantOwnerType == GrantOwnerType.OTHER_INSTITUTION_GRANT_OWNER
                    || grantOwnerType == GrantOwnerType.ORIGIN_COUNTRY_GRANT_OWNER)) {
                //We accept new institutions for these 2 cases
                grantOwnerProvider = Unit.createNewNoOfficialExternalInstitution(form.getOtherGrantOwnerProvider());
            }

            personalData.setGrantOwnerProvider(grantOwnerProvider);
        } else {
            personalData.setGrantOwnerProvider(null);
        }

        personUlisboa.setHouseholdSalarySpan(form.getHouseholdSalarySpan());

        if (form.getDislocatedFromPermanentResidence() != null) {
            personalData.setDislocatedFromPermanentResidence(form.getDislocatedFromPermanentResidence());
        }

    }

    @Override
    protected String backScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(FiliationFormController.CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    @Override
    protected String nextScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(ResidenceInformationFormController.CONTROLLER_URL, executionYear), model,
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
