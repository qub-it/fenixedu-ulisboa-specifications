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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(HouseholdInformationFormController.CONTROLLER_URL)
public class HouseholdInformationFormController extends FormAbstractController {

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
    protected String fillGetScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {

        HouseholdInformationForm form = fillFormIfRequired(executionYear, model);

        if (getForm(model) == null) {
            setForm(form, model);
        }

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillHouseHoldInformation.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/householdinformationform/fillhouseholdinformation";
    }

    public HouseholdInformationForm fillFormIfRequired(ExecutionYear executionYear, Model model) {
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
            form.setCountryHighSchool(personUl.getPerson().getCountryHighSchool());
        }

        return form;
    }

    @Override
    protected void fillPostScreen(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model,
            RedirectAttributes redirectAttributes) {
        //nothing
    }

    @Override
    protected boolean validate(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        if (!(candidancyForm instanceof HouseholdInformationForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.HouseholdInformationFormController.wrong.form.type"), model);
        }

        return validate((HouseholdInformationForm) candidancyForm, model);
    }

    protected boolean validate(HouseholdInformationForm form, Model model) {
        final Set<String> messages = validateHouseholdInformationForm(form);

        for (final String message : messages) {
            addErrorMessage(message, model);
        }

        return messages.isEmpty();
    }

    public Set<String> validateHouseholdInformationForm(HouseholdInformationForm form) {
        final Set<String> messages = Sets.newHashSet();

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
                if (StringUtils.isEmpty(otherGrantOwnerProvider)) {
                    messages.add(BundleUtil.getString(BUNDLE,
                            "error.candidacy.workflow.PersonalInformationForm.grant.owner.must.choose.granting.institution"));
                }
            } else if (!grantOwnerType.equals(GrantOwnerType.STUDENT_WITHOUT_SCHOLARSHIP)) {
                if (FenixFramework.getDomainObject(grantOwnerProvider) == null) {
                    messages.add(BundleUtil.getString(BUNDLE,
                            "error.candidacy.workflow.PersonalInformationForm.grant.owner.must.choose.granting.institution"));
                }
            }
        }

        if (isToFillCountryHighSchool() && form.getCountryHighSchool() == null) {
            messages.add(
                    BundleUtil.getString(BUNDLE, "error.candidacy.workflow.PersonalInformationForm.countryHighSchool.required"));
        }

        return messages;
    }

    protected boolean isProfessionRequired() {
        return true;
    }

    protected boolean isToFillCountryHighSchool() {
        return false;
    }

    @Override
    protected void writeData(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
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

        if (isToFillCountryHighSchool()) {
            personalData.getStudent().getPerson().setCountryHighSchool(form.getCountryHighSchool());
        }

    }

    @Override
    protected String backScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(FiliationFormController.CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    @Override
    protected String nextScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(ResidenceInformationFormController.CONTROLLER_URL, executionYear), model,
                redirectAttributes);
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
