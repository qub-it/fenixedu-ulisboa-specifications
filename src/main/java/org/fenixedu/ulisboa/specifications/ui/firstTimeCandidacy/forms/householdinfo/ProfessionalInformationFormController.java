package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.Set;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
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
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.qualification.OriginInformationFormController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(ProfessionalInformationFormController.CONTROLLER_URL)
public class ProfessionalInformationFormController extends FormAbstractController {

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/professionalinformationform";

    public static final String FILL_URL = CONTROLLER_URL + _FILL_URI;

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    protected String getFormVariableName() {
        return "professionalInformationForm";
    }

    @Override
    protected String fillGetScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {

        ProfessionalInformationForm form = fillFormIfRequired(executionYear, model);

        if (!model.containsAttribute("postAction")) {
            model.addAttribute("postAction", "fill");
        }

        if (getForm(model) == null) {
            setForm(form, model);
        }

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillProfessionalInformation.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/professionalinformationform/fillprofessionalinformation";
    }

    public ProfessionalInformationForm fillFormIfRequired(final ExecutionYear executionYear, final Model model) {
        ProfessionalInformationForm form = (ProfessionalInformationForm) getForm(model);
        if (form == null) {
            form = createProfissionalInformationForm(getStudent(model), executionYear, false);

            setForm(form, model);
        }
        return form;
    }

    protected ProfessionalInformationForm createEmptyProfissionalInformationForm(final Student student, final Model model) {
        final ProfessionalInformationForm form = new ProfessionalInformationForm();

        return form;
    }

    protected ProfessionalInformationForm createProfissionalInformationForm(final Student student,
            final ExecutionYear executionYear, final boolean create) {
        return createProfissionalInformationForm(student, executionYear, create, true);
    }

    protected ProfessionalInformationForm createProfissionalInformationForm(final Student student,
            final ExecutionYear executionYear, final boolean create, final boolean initDTOs) {
        final PersonalIngressionData personalData = getPersonalIngressionData(student, executionYear, create);

        final ProfessionalInformationForm form = new ProfessionalInformationForm(initDTOs);

        if (personalData != null) {
            form.setProfessionType(personalData.getProfessionType());
            form.setProfessionalCondition(personalData.getProfessionalCondition());
        }

        if (form.getProfessionType() == null) {
            form.setProfessionType(ProfessionType.OTHER);
        }

        if (form.getProfessionalCondition() == null) {
            form.setProfessionalCondition(ProfessionalSituationConditionType.STUDENT);
        }

        form.setProfession(student.getPerson().getProfession());

        PersonUlisboaSpecifications personUl = student.getPerson().getPersonUlisboaSpecifications();
        if (personUl != null) {
            PersonUlisboaSpecificationsByExecutionYear personUlExecutionYear =
                    personUl.getPersonUlisboaSpecificationsByExcutionYear(executionYear);
            if (personUlExecutionYear != null) {
                form.setProfessionTimeType(personUlExecutionYear.getProfessionTimeType());
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
        if (!(candidancyForm instanceof ProfessionalInformationForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.ProfessionalInformationFormController.wrong.form.type"), model);
        }

        return validate((ProfessionalInformationForm) candidancyForm, model);
    }

    protected boolean validate(final ProfessionalInformationForm form, final Model model) {
        final Set<String> messages = validateForm(form);

        for (final String message : messages) {
            addErrorMessage(message, model);
        }

        return messages.isEmpty();
    }

    protected Set<String> validateForm(final ProfessionalInformationForm form) {
        final Set<String> messages = Sets.newLinkedHashSet();

        if (form.getProfessionalCondition() == null || form.getProfessionType() == null) {
            messages.add(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.all.fields.required"));
        }

        //This questions are not required, therefore we can not force to answered
        // Joao Amaral 09-09-2020
//        if (form.isStudentWorking()) {
//            if (StringUtils.isEmpty(form.getProfession()) && isProfessionRequired()) {
//                messages.add(
//                        BundleUtil.getString(BUNDLE, "error.candidacy.workflow.PersonalInformationForm.profession.required"));
//            }
//
//            if (form.getProfessionTimeType() == null && isProfessionRequired()) {
//                messages.add(BundleUtil.getString(BUNDLE,
//                        "error.candidacy.workflow.PersonalInformationForm.professionTimeType.required"));
//            }
//        }

        return messages;
    }

    protected boolean isProfessionRequired() {
        return true;
    }

    @Override
    protected void writeData(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model) {
        writeData(getStudent(model), executionYear, (ProfessionalInformationForm) candidancyForm, model);
    }

    @Atomic
    protected void writeData(final Student student, final ExecutionYear executionYear, final ProfessionalInformationForm form,
            final Model model) {
        PersonalIngressionData personalData = getPersonalIngressionData(student, executionYear, false);
        PersonUlisboaSpecificationsByExecutionYear personUlExecutionYear =
                PersonUlisboaSpecificationsByExecutionYear.findOrCreate(student.getPerson(), executionYear);

        personalData.setProfessionalCondition(form.getProfessionalCondition());
        personalData.getStudent().getPerson().setProfession(form.getProfession());
        personalData.setProfessionType(form.getProfessionType());

        if (form.getProfessionTimeType() != null) {
            personUlExecutionYear.setProfessionTimeType(form.getProfessionTimeType());
        }

    }

    @Override
    protected String backScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(HouseholdInformationFormController.CONTROLLER_URL, executionYear), model,
                redirectAttributes);
    }

    @Override
    protected String nextScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(OriginInformationFormController.CONTROLLER_URL, executionYear), model,
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

}
