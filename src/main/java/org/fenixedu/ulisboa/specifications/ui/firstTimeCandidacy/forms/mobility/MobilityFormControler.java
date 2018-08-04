package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.mobility;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.Set;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.MobilityRegistatrionUlisboaInformation;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.enrolments.EnrolmentsController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.FormAbstractController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.health.VaccionationFormController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.motivations.MotivationsExpectationsFormController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(MobilityFormControler.CONTROLLER_URL)
public class MobilityFormControler extends FormAbstractController {

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/mobilityform";

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    protected String getFormVariableName() {
        return "mobilityForm";
    }

    @Override
    protected String fillGetScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        MobilityForm form = fillFormIfRequired(executionYear, model);

        if (getForm(model) == null) {
            setForm(form, model);
        }

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillMobility.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/mobilityform/fillmobility";
    }

    public MobilityForm fillFormIfRequired(final ExecutionYear executionYear, final Model model) {
        MobilityForm form = (MobilityForm) getForm(model);

        if (form == null) {
            form = createMobilityForm(executionYear, getStudent(model));

            setForm(form, model);
        }
        return form;
    }

    protected MobilityForm createMobilityForm(final ExecutionYear executionYear, final Student student) {
        MobilityForm form = new MobilityForm();
        PersonUlisboaSpecifications personUl = PersonUlisboaSpecifications.findOrCreate(student.getPerson());
        MobilityRegistatrionUlisboaInformation information = personUl.getMobilityRegistatrionUlisboaInformation();

        if (information != null) {
            form.setHasMobilityProgram(true);

            form.setBegin(information.getBegin());
            form.setEnd(information.getEnd());
            form.setBeginDate(information.getBeginDate());
            form.setEndDate(information.getEndDate());

            form.setMobilityProgramType(information.getMobilityProgramType());
            form.setMobilityActivityType(information.getMobilityActivityType());
            form.setMobilityScientificArea(information.getMobilityScientificArea());

            form.setProgramDuration(information.getProgramDuration());

            form.setOriginMobilityProgrammeLevel(information.getOriginMobilityProgrammeLevel());
            form.setIncomingMobilityProgrammeLevel(information.getIncomingMobilityProgrammeLevel());
            form.setOtherOriginMobilityProgrammeLevel(information.getOtherOriginMobilityProgrammeLevel());
            form.setOtherIncomingMobilityProgrammeLevel(information.getOtherIncomingMobilityProgrammeLevel());

            form.setOriginCountry(information.getOriginCountry());
            form.setIncomingCountry(information.getIncomingCountry());
        }

        return form;
    }

    @Override
    protected void fillPostScreen(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model,
            RedirectAttributes redirectAttributes) {
        // nothing
    }

    @Override
    protected boolean validate(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        if (!(candidancyForm instanceof MobilityForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.MobilityFormControler.wrong.form.type"), model);
        }

        return validate((MobilityForm) candidancyForm, model);
    }

    private boolean validate(MobilityForm form, Model model) {
        final Set<String> result = validateForm(form);

        for (final String message : result) {
            addErrorMessage(message, model);
        }

        return result.isEmpty();
    }

    protected Set<String> validateForm(MobilityForm form) {
        final Set<String> result = Sets.newLinkedHashSet();

        if (form.isHasMobilityProgram()) {
            if (form.getBegin() == null) {
                result.add(BundleUtil.getString(BUNDLE, "error.MobilityRegistrationInformation.begin.required"));
            }

            if (form.getEnd() == null) {
                result.add(BundleUtil.getString(BUNDLE, "error.MobilityRegistrationInformation.end.required"));
            }

            if (form.getBegin() != null && form.getEnd() != null && form.getBegin().isAfter(form.getEnd())) {
                result.add(BundleUtil.getString(BUNDLE, "error.MobilityRegistrationInformation.end.must.be.after.begin"));
            }

            if (form.getMobilityProgramType() == null) {
                result.add(BundleUtil.getString(BUNDLE, "error.MobilityRegistrationInformation.mobilityProgramType.required"));
            }

            if (form.getMobilityActivityType() == null) {
                result.add(BundleUtil.getString(BUNDLE, "error.MobilityRegistrationInformation.mobilityActivityType.required"));
            }

            if (form.getProgramDuration() == null) {
                result.add(BundleUtil.getString(BUNDLE, "error.MobilityRegistrationInformation.programDuration.required"));
            }

            if (form.getMobilityScientificArea() == null) {
                result.add(BundleUtil.getString(BUNDLE, "error.MobilityRegistrationInformation.mobilityScientificArea.required"));
            }

            if (form.getIncomingMobilityProgrammeLevel() == null) {
                result.add(BundleUtil.getString(BUNDLE,
                        "error.MobilityRegistrationInformation.incomingMobilityProgrammeLevel.required"));
            }

            if (form.getOriginMobilityProgrammeLevel() == null) {
                result.add(BundleUtil.getString(BUNDLE,
                        "error.MobilityRegistrationInformation.originMobilityProgrammeLevel.required"));
            }

            if (form.getOriginCountry() == null) {
                result.add(BundleUtil.getString(BUNDLE, "error.MobilityRegistrationInformation.OriginCountry.required"));
            }

            if (form.getIncomingCountry() == null) {
                result.add(BundleUtil.getString(BUNDLE, "error.MobilityRegistrationInformation.IncomingCountry.required"));
            }
        }

        return result;
    }

    @Override
    protected void writeData(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        writeData((MobilityForm) candidancyForm, executionYear, model);
    }

    @Atomic
    protected void writeData(MobilityForm form, final ExecutionYear executionYear, final Model model) {
        if (form.isHasMobilityProgram()) {

            PersonUlisboaSpecifications personUl = PersonUlisboaSpecifications.findOrCreate(getStudent(model).getPerson());
            MobilityRegistatrionUlisboaInformation information = personUl.getMobilityRegistatrionUlisboaInformation();

            if (information != null) {
                information.edit(form);
            } else {
                MobilityRegistatrionUlisboaInformation.create(personUl, form);
            }
        }
    }

    @Override
    protected String backScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        if (!VaccionationFormController.shouldBeSkipped(executionYear)) {
            return redirect(urlWithExecutionYear(VaccionationFormController.CONTROLLER_URL, executionYear), model,
                    redirectAttributes);
        } else {
            return redirect(urlWithExecutionYear(MotivationsExpectationsFormController.CONTROLLER_URL, executionYear), model,
                    redirectAttributes);
        }
    }

    @Override
    protected String nextScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(EnrolmentsController.CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    @Override
    public boolean isFormIsFilled(ExecutionYear executionYear, Student student) {
        return false;
    }

    @Override
    protected Student getStudent(Model model) {
        return AccessControl.getPerson().getStudent();
    }

    protected Registration getRegistration(final ExecutionYear executionYear, final Model model) {
        return FirstTimeCandidacyController.getCandidacy().getRegistration();
    }

}
