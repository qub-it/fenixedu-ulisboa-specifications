package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.motivations;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.domain.UniversityChoiceMotivationAnswer;
import org.fenixedu.ulisboa.specifications.domain.UniversityDiscoveryMeansAnswer;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.FormAbstractController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.health.DisabilitiesFormController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.health.VaccionationFormController;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(MotivationsExpectationsFormController.CONTROLLER_URL)
public class MotivationsExpectationsFormController extends FormAbstractController {

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/motivationsexpectationsform";

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    protected String getFormVariableName() {
        return "motivationsExpectationsForm";
    }

    @Override
    protected String fillGetScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        MotivationsExpectationsForm form = fillFormIfRequired(executionYear, model);

        if (getForm(model) == null) {
            setForm(form, model);
        }

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillMotivationsExpectations.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/motivationsexpectationsform/fillmotivationsexpectations";
    }

    private MotivationsExpectationsForm fillFormIfRequired(final ExecutionYear executionYear, final Model model) {
        MotivationsExpectationsForm form = (MotivationsExpectationsForm) getForm(model);

        if (form == null) {
            form = createMotivationsExpectationsForm(executionYear, getStudent(model));

            setForm(form, model);
        }
        return form;
    }

    protected MotivationsExpectationsForm createMotivationsExpectationsForm(final ExecutionYear executionYear,
            final Student student) {
        MotivationsExpectationsForm form = new MotivationsExpectationsForm();
        PersonUlisboaSpecifications personUlisboa = student.getPerson().getPersonUlisboaSpecifications();
        if (personUlisboa != null) {
            form.getUniversityDiscoveryMeansAnswers().addAll(personUlisboa.getUniversityDiscoveryMeansAnswersSet());
            form.getUniversityChoiceMotivationAnswers().addAll(personUlisboa.getUniversityChoiceMotivationAnswersSet());

            form.setOtherUniversityChoiceMotivation(personUlisboa.getOtherUniversityChoiceMotivation());
            form.setOtherUniversityDiscoveryMeans(personUlisboa.getOtherUniversityDiscoveryMeans());
        }

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

        form.setAnswered(personUlisboa != null ? personUlisboa.getMotivationsExpectationsFormAnswered() : false);

        return form;
    }

    @Override
    protected void fillPostScreen(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model,
            RedirectAttributes redirectAttributes) {
        // nothing
    }

    @Override
    protected boolean validate(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        if (!(candidancyForm instanceof MotivationsExpectationsForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.MotivationsExpectationsFormController.wrong.form.type"), model);
        }

        return validate((MotivationsExpectationsForm) candidancyForm, model);
    }

    private boolean validate(MotivationsExpectationsForm form, Model model) {
        if (form.getUniversityChoiceMotivationAnswers().size() > 3) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                    "error.candidacy.workflow.MotivationsExpectationsForm.max.three.choices"), model);
            return false;
        }
        if (form.getUniversityDiscoveryMeansAnswers().size() > 3) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                    "error.candidacy.workflow.MotivationsExpectationsForm.max.three.choices"), model);
            return false;
        }

        for (UniversityChoiceMotivationAnswer answer : form.getUniversityChoiceMotivationAnswers()) {
            if (answer.isOther() && StringUtils.isEmpty(form.getOtherUniversityChoiceMotivation())) {
                addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                        "error.candidacy.workflow.MotivationsExpectationsForm.other.must.be.filled"), model);
                return false;
            }
        }

        for (UniversityDiscoveryMeansAnswer answer : form.getUniversityDiscoveryMeansAnswers()) {
            if (answer.isOther() && StringUtils.isEmpty(form.getOtherUniversityDiscoveryMeans())) {
                addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                        "error.candidacy.workflow.MotivationsExpectationsForm.other.must.be.filled"), model);
                return false;
            }
        }
        return true;
    }

    @Override
    protected void writeData(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        writeData((MotivationsExpectationsForm) candidancyForm, model);
    }

    @Atomic
    protected void writeData(MotivationsExpectationsForm form, final Model model) {
        PersonUlisboaSpecifications personUlisboa = PersonUlisboaSpecifications.findOrCreate(getStudent(model).getPerson());
        personUlisboa.getUniversityChoiceMotivationAnswersSet().clear();
        personUlisboa.getUniversityDiscoveryMeansAnswersSet().clear();

        for (UniversityChoiceMotivationAnswer answer : form.getUniversityChoiceMotivationAnswers()) {
            personUlisboa.addUniversityChoiceMotivationAnswers(answer);
        }
        for (UniversityDiscoveryMeansAnswer answer : form.getUniversityDiscoveryMeansAnswers()) {
            personUlisboa.addUniversityDiscoveryMeansAnswers(answer);
        }

        personUlisboa.setOtherUniversityChoiceMotivation(form.getOtherUniversityChoiceMotivation());
        personUlisboa.setOtherUniversityDiscoveryMeans(form.getOtherUniversityDiscoveryMeans());
        personUlisboa.setMotivationsExpectationsFormAnswered(true);
    }

    @Override
    protected String backScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(DisabilitiesFormController.CONTROLLER_URL, executionYear), model,
                redirectAttributes);
    }

    @Override
    protected String nextScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(VaccionationFormController.CONTROLLER_URL, executionYear), model,
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
