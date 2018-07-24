package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.mobility;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityRegistrationInformation;
import org.fenixedu.ulisboa.specifications.dto.student.mobility.MobilityRegistrationInformationBean;
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
            form = createMobilityForm(executionYear, getStudent(model), model);

            setForm(form, model);
        }
        return form;
    }

    protected MobilityForm createMobilityForm(final ExecutionYear executionYear, final Student student, final Model model) {
        MobilityForm form = new MobilityForm();
        Registration registration = getRegistration(executionYear, model);
        List<MobilityRegistrationInformation> informations = registration.getMobilityRegistrationInformationsSet().stream()
                .filter(info -> info.getRemarks().equals(MobilityForm.CREATED_CANDIDACY_FLOW)).collect(Collectors.toList());

        if (informations.size() > 1) {
            //TODO: this shouldn't happen.
        }

        if (informations.size() == 1) {
            MobilityRegistrationInformation information = informations.iterator().next();

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

            form.setCountryUnit(information.getCountryUnit());
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
        final Set<String> result = validateForm(form, getStudent(model).getPerson());

        for (final String message : result) {
            addErrorMessage(message, model);
        }

        return result.isEmpty();
    }

    private Set<String> validateForm(MobilityForm form, final Person person) {
        final Set<String> result = Sets.newLinkedHashSet();

        if (form.getBegin() == null) {
            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.begin.required");
        }

        if (form.getEnd() == null) {
            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.end.required");
        }

        if (form.getBegin().isAfter(form.getEnd())) {
            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.end.must.be.after.begin");
        }

        if (form.getMobilityProgramType() == null) {
            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.mobilityProgramType.required");
        }

        if (form.getMobilityActivityType() == null) {
            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.mobilityActivityType.required");
        }

        if (form.getProgramDuration() == null) {
            throw new ULisboaSpecificationsDomainException("error.MobilityRegistrationInformation.programDuration.required");
        }

        if (form.getMobilityScientificArea() == null) {
            throw new ULisboaSpecificationsDomainException(
                    "error.MobilityRegistrationInformation.mobilityScientificArea.required");
        }

        if (form.getIncomingMobilityProgrammeLevel() == null) {
            throw new ULisboaSpecificationsDomainException(
                    "error.MobilityRegistrationInformation.incomingMobilityProgrammeLevel.required");
        }

        if (form.getOriginMobilityProgrammeLevel() == null) {
            throw new ULisboaSpecificationsDomainException(
                    "error.MobilityRegistrationInformation.originMobilityProgrammeLevel.required");
        }

        return result;
    }

    @Override
    protected void writeData(ExecutionYear executionYear, CandidancyForm candidancyForm, Model model) {
        writeData((MobilityForm) candidancyForm, executionYear, model);
    }

    @Atomic
    protected void writeData(MobilityForm form, final ExecutionYear executionYear, final Model model) {
        Registration registration = getRegistration(executionYear, model);
        MobilityRegistrationInformationBean bean = form.getBeanToCreate(registration);
        List<MobilityRegistrationInformation> informations = registration.getMobilityRegistrationInformationsSet().stream()
                .filter(info -> info.getRemarks().equals(MobilityForm.CREATED_CANDIDACY_FLOW)).collect(Collectors.toList());

        if (informations.size() == 1) {
            informations.iterator().next().edit(bean);
        } else {
            MobilityRegistrationInformation.create(bean);
        }

    }

    @Override
    protected String backScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        if (!VaccionationFormController.shouldBeSkipped(executionYear)) {
            return urlWithExecutionYear(VaccionationFormController.CONTROLLER_URL, executionYear);
        } else {
            return urlWithExecutionYear(MotivationsExpectationsFormController.CONTROLLER_URL, executionYear);
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
