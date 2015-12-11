package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import java.util.Optional;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.ulisboa.specifications.domain.candidacy.FirstTimeCandidacy;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

public abstract class FirstTimeCandidacyAbstractController extends FenixeduUlisboaSpecificationsBaseController {

    public Optional<String> accessControlRedirect(Model model, RedirectAttributes redirectAttributes) {
        if (!FirstTimeCandidacyController.isPeriodOpen()) {
            return Optional.of(redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes));
        }
        return Optional.empty();
    }

    protected PersonalIngressionData getPersonalIngressionData() {
        PersonalIngressionData personalData;
        FirstTimeCandidacy candidacy = FirstTimeCandidacyController.getCandidacy();
        if (candidacy != null) {
            personalData =
                    FirstTimeCandidacyController.getOrCreatePersonalIngressionData(candidacy.getPrecedentDegreeInformation());
        } else {
            personalData = getPersonalDataFromRegistration();
        }
        return personalData;
    }

    protected PrecedentDegreeInformation getPrecedentDegreeInformation() {
        PersonalIngressionData personalData;
        FirstTimeCandidacy candidacy = FirstTimeCandidacyController.getCandidacy();
        if (candidacy != null) {
            return candidacy.getPrecedentDegreeInformation();
        } else {
            return getPrecedentDegreeInformationFromRegistration(getMostCurrentRegistration(),
                    ExecutionYear.readCurrentExecutionYear());
        }
    }

    @Atomic
    private PersonalIngressionData getPersonalDataFromRegistration() {
        Registration registration = getMostCurrentRegistration();
        ExecutionYear firstExecutionYear =
                registration.getRegistrationDataByExecutionYearSet().stream().map(x -> x.getExecutionYear())
                        .sorted(ExecutionYear.COMPARATOR_BY_YEAR).findFirst().orElse(ExecutionYear.readCurrentExecutionYear());

        PrecedentDegreeInformation precedentDegreeInformation =
                getPrecedentDegreeInformationFromRegistration(registration, firstExecutionYear);
        PersonalIngressionData personalIngressionData = precedentDegreeInformation.getPersonalIngressionData();
        if (personalIngressionData == null) {
            personalIngressionData = registration.getStudent().getPersonalIngressionDataByExecutionYear(firstExecutionYear);
            if (personalIngressionData != null) {
                precedentDegreeInformation.setPersonalIngressionData(personalIngressionData);
            } else {
                personalIngressionData = new PersonalIngressionData(firstExecutionYear, precedentDegreeInformation);
                personalIngressionData.setStudent(registration.getStudent());
            }
        }
        return personalIngressionData;
    }

    private Registration getMostCurrentRegistration() {
        Registration registration = AccessControl.getPerson().getStudent().getActiveRegistrations().get(0);
        return registration;
    }

    private PrecedentDegreeInformation getPrecedentDegreeInformationFromRegistration(Registration registration,
            ExecutionYear executionYear) {
        PrecedentDegreeInformation precedentDegreeInformation = registration.getPrecedentDegreeInformation(executionYear);
        if (precedentDegreeInformation == null) {
            precedentDegreeInformation = new PrecedentDegreeInformation();
            registration.setPrecedentDegreeInformation(precedentDegreeInformation);
            precedentDegreeInformation.setRegistration(registration);
        }
        return precedentDegreeInformation;
    }

    @Override
    protected void addModelProperties(Model model) {
        super.addModelProperties(model);
        model.addAttribute("controllerURL", getControllerURL());
    }

    protected abstract String getControllerURL();
}
