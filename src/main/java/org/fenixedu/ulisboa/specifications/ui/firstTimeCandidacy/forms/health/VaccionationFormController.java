package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.health;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationConfiguration;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.enrolments.EnrolmentsReportController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.FormAbstractController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.motivations.MotivationsExpectationsFormController;
import org.joda.time.LocalDate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(VaccionationFormController.CONTROLLER_URL)
public class VaccionationFormController extends FormAbstractController {

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/vaccionation";

    @Override
    protected String getFormVariableName() {
        return "vaccinationForm";
    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    protected String fillGetScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        if (shouldBeSkipped(executionYear)) {
            return nextScreen(executionYear, model, redirectAttributes);
        }

        VaccinationForm form = fillFormIfRequired(model);

        if (getForm(model) == null) {
            setForm(form, model);
        }

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.createSchoolSpecificData.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/vaccinationform/fillvaccinationform";
    }

    public static boolean shouldBeSkipped(ExecutionYear executionYear) {
        Degree degree = FirstTimeCandidacyController.getCandidacy().getDegreeCurricularPlan().getDegree();
        return FirstYearRegistrationConfiguration.getDegreeConfiguration(degree, executionYear) == null
                || !FirstYearRegistrationConfiguration.getDegreeConfiguration(degree, executionYear).getRequiresVaccination();
    }

    public VaccinationForm fillFormIfRequired(Model model) {
        Person person = AccessControl.getPerson();
        VaccinationForm form = new VaccinationForm();

        if (person.getPersonUlisboaSpecifications() != null) {
            form.setVaccinationValidity(person.getPersonUlisboaSpecifications().getVaccinationValidity());
        }

        return form;
    }

    @Override
    protected void fillPostScreen(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model,
            RedirectAttributes redirectAttributes) {

    }

    @Override
    protected boolean validate(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        if (!(candidancyForm instanceof VaccinationForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.VaccinationFormController.wrong.form.type"), model);
        }
        return validate((VaccinationForm) candidancyForm, model);
    }

    private boolean validate(VaccinationForm form, Model model) {
        if (form.getVaccinationValidity() == null) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "label.error.fillVaccionatioValidity"), model);
            return false;
        }

        if (form.getVaccinationValidity() != null && !form.getVaccinationValidity().isAfter(new LocalDate())) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "label.error.fillVaccionatioValidity.is.invalid"), model);
            return false;
        }
        return true;
    }

    @Override
    protected void writeData(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        writeData((VaccinationForm) candidancyForm);
    }

    @Atomic
    protected void writeData(VaccinationForm vaccinationForm) {
        PersonUlisboaSpecifications.findOrCreate(AccessControl.getPerson())
                .setVaccinationValidity(vaccinationForm.getVaccinationValidity());
    }

    @Override
    protected String backScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(MotivationsExpectationsFormController.CONTROLLER_URL, executionYear), model,
                redirectAttributes);
    }

    @Override
    protected String nextScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(EnrolmentsReportController.CONTROLLER_URL, executionYear), model,
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
