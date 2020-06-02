package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.health;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.FormAbstractController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo.HouseholdInformationUlisboaFormController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.motivations.MotivationsExpectationsFormController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(DisabilitiesFormController.CONTROLLER_URL)
public class DisabilitiesFormController extends FormAbstractController {

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/disabilitiesform";

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    protected String getFormVariableName() {
        return "disabilitiesForm";
    }

    @Override
    protected String fillGetScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        DisabilitiesForm form = fillFormIfRequired(executionYear, model);

        if (getForm(model) == null) {
            setForm(form, model);
        }

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillDisabilities.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/disabilitiesform/filldisabilities";
    }

    public DisabilitiesForm fillFormIfRequired(final ExecutionYear executionYear, final Model model) {
        DisabilitiesForm form = (DisabilitiesForm) getForm(model);

        if (form == null) {
            form = createDisabilitiesForm(executionYear, getStudent(model));

            setForm(form, model);
        }
        return form;
    }

    protected DisabilitiesForm createDisabilitiesForm(final ExecutionYear executionYear, final Student student) {
        return createDisabilitiesForm(executionYear, student, true);
    }

    protected DisabilitiesForm createDisabilitiesForm(final ExecutionYear executionYear, final Student student,
            final boolean initDTOs) {
        DisabilitiesForm form = new DisabilitiesForm(initDTOs);
        PersonUlisboaSpecifications personUlisboa = student.getPerson().getPersonUlisboaSpecifications();
        if (personUlisboa != null) {
            form.setHasDisabilities(personUlisboa.getHasDisabilities());
            if (personUlisboa.getDisabilityType() != null) {
                form.setDisabilityType(personUlisboa.getDisabilityType());
            }
            form.setOtherDisabilityType(personUlisboa.getOtherDisabilityType());
            form.setNeedsDisabilitySupport(personUlisboa.getNeedsDisabilitySupport());

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

        form.setAnswered(personUlisboa != null ? personUlisboa.getDisabilitiesFormAnswered() : false);

        return form;
    }

    @Override
    protected void fillPostScreen(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model,
            RedirectAttributes redirectAttributes) {
        // nothing
    }

    @Override
    protected boolean validate(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        if (!(candidancyForm instanceof DisabilitiesForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.DisabilitiesFormController.wrong.form.type"), model);
        }

        return validate((DisabilitiesForm) candidancyForm, model);
    }

    private boolean validate(DisabilitiesForm form, Model model) {
        final Set<String> result = validateForm(form, getStudent(model).getPerson());

        for (final String message : result) {
            addErrorMessage(message, model);
        }

        return result.isEmpty();
    }

    private Set<String> validateForm(DisabilitiesForm form, final Person person) {
        final Set<String> result = Sets.newLinkedHashSet();

        if (form.getHasDisabilities()) {
            if (form.getDisabilityType() == null
                    || form.getDisabilityType().isOther() && StringUtils.isEmpty(form.getOtherDisabilityType())) {
                result.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                        "error.candidacy.workflow.DisabilitiesForm.disabilityType.must.be.filled"));
            }

            if (form.getNeedsDisabilitySupport() == null) {
                result.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                        "error.candidacy.workflow.DisabilitiesForm.needsDisabilitySupport.must.be.filled"));
            }
        }

        return result;
    }

    @Override
    protected void writeData(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        writeData((DisabilitiesForm) candidancyForm, model);
    }

    @Atomic
    protected void writeData(DisabilitiesForm form, final Model model) {
        PersonUlisboaSpecifications personUlisboa = PersonUlisboaSpecifications.findOrCreate(getStudent(model).getPerson());

        personUlisboa.setHasDisabilities(form.getHasDisabilities());
        if (form.getHasDisabilities()) {
            personUlisboa.setDisabilityType(form.getDisabilityType());
            personUlisboa.setOtherDisabilityType(form.getOtherDisabilityType());
            personUlisboa.setNeedsDisabilitySupport(form.getNeedsDisabilitySupport());
        } else {
            personUlisboa.setDisabilityType(null);
        }

        personUlisboa.setDisabilitiesFormAnswered(true);
    }

    @Override
    protected String backScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(HouseholdInformationUlisboaFormController.CONTROLLER_URL, executionYear), model,
                redirectAttributes);
    }

    @Override
    protected String nextScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(MotivationsExpectationsFormController.CONTROLLER_URL, executionYear), model,
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
