package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.health;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.Set;

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
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.FormAbstractController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.mobility.MobilityFormControler;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.motivations.MotivationsExpectationsFormController;
import org.joda.time.LocalDate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Sets;

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
    protected String fillGetScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
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

    public static boolean shouldBeSkipped(final ExecutionYear executionYear) {
        Degree degree = FirstTimeCandidacyController.getCandidacy().getDegreeCurricularPlan().getDegree();
        return FirstYearRegistrationConfiguration.getDegreeConfiguration(degree) == null
                || !FirstYearRegistrationConfiguration.getDegreeConfiguration(degree).getRequiresVaccination();
    }

    public VaccinationForm fillFormIfRequired(final Model model) {
        Person person = AccessControl.getPerson();
        VaccinationForm form = new VaccinationForm();

        if (person.getPersonUlisboaSpecifications() != null) {
            form.setVaccinationValidity(person.getPersonUlisboaSpecifications().getVaccinationValidity());
        }

        return form;
    }

    @Override
    protected void fillPostScreen(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model,
            final RedirectAttributes redirectAttributes) {

    }

    @Override
    protected boolean validate(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model) {
        if (!(candidancyForm instanceof VaccinationForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.VaccinationFormController.wrong.form.type"), model);
        }
        return validate((VaccinationForm) candidancyForm, model);
    }

    private boolean validate(final VaccinationForm form, final Model model) {
        final Set<String> result = validateForm(form, getStudent(model).getPerson());

        for (final String message : result) {
            addErrorMessage(message, model);
        }

        return result.isEmpty();
    }

    private Set<String> validateForm(final VaccinationForm form, final Person person) {
        final Set<String> result = Sets.newLinkedHashSet();

        if (form.getVaccinationValidity() == null) {
            result.add(BundleUtil.getString(BUNDLE, "label.error.fillVaccionatioValidity"));
        }

        if (form.getVaccinationValidity() != null && !form.getVaccinationValidity().isAfter(new LocalDate())) {
            result.add(BundleUtil.getString(BUNDLE, "label.error.fillVaccionatioValidity.is.invalid"));
        }

        return result;
    }

    @Override
    protected void writeData(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model) {
        writeData((VaccinationForm) candidancyForm, model);
    }

    @Atomic
    protected void writeData(final VaccinationForm vaccinationForm, final Model model) {
        PersonUlisboaSpecifications personUlisboa = PersonUlisboaSpecifications.findOrCreate(getStudent(model).getPerson());

        personUlisboa.setVaccinationValidity(vaccinationForm.getVaccinationValidity());
    }

    @Override
    protected String backScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(MotivationsExpectationsFormController.CONTROLLER_URL, executionYear), model,
                redirectAttributes);
    }

    @Override
    protected String nextScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(MobilityFormControler.CONTROLLER_URL, executionYear), model, redirectAttributes);
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
