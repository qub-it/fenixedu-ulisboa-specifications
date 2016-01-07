package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import java.util.List;
import java.util.Optional;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;

import edu.emory.mathcs.backport.java.util.Collections;
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
        StudentCandidacy candidacy = FirstTimeCandidacyController.getCandidacy();
        if (candidacy != null) {
            personalData =
                    FirstTimeCandidacyController.getOrCreatePersonalIngressionData(candidacy.getPrecedentDegreeInformation());
        } else {
            personalData = getPersonalDataFromRegistration();
        }
        return personalData;
    }

    private PrecedentDegreeInformation _getPrecedentDegreeInformation() {
        final StudentCandidacy candidacy = FirstTimeCandidacyController.getCandidacy();
        if (candidacy != null) {
            return candidacy.getPrecedentDegreeInformation();
        } else {
            return null;
//            return getPrecedentDegreeInformationFromRegistration(getMostCurrentRegistration(),
//                    ExecutionYear.readCurrentExecutionYear());
        }
    }

    @Atomic
    private PersonalIngressionData getPersonalDataFromRegistration() {
        Registration registration = _getMostCurrentRegistration();
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

    private Registration _getMostCurrentRegistration() {
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

    protected List<PrecedentDegreeInformation> findCompletePrecedentDegreeInformationsToFill() {
        final List<Registration> activeRegistrationsWithEnrolments =
                Raides.findActiveRegistrationsWithEnrolments(AccessControl.getPerson().getStudent());

        final List<PrecedentDegreeInformation> result = Lists.newArrayList();
        for (final Registration registration : activeRegistrationsWithEnrolments) {
            if (Raides.isCompletePrecedentDegreeInformationFieldsToBeFilledByStudent(registration)) {
                result.add(registration.getStudentCandidacy().getPrecedentDegreeInformation());
            }
        }

        return result;
    }

    protected List<PrecedentDegreeInformation> findPreviousDegreePrecedentDegreeInformationsToFill() {
        final List<Registration> activeRegistrationsWithEnrolments =
                Raides.findActiveRegistrationsWithEnrolments(AccessControl.getPerson().getStudent());
        final List<PrecedentDegreeInformation> result = Lists.newArrayList();

        for (final Registration registration : activeRegistrationsWithEnrolments) {
            if (!Raides.isDegreeChangeOrTransfer(RaidesInstance.getInstance(), registration.getIngressionType())) {
                continue;
            }

            if (Raides.isPreviousDegreePrecedentDegreeInformationFieldsToBeFilledByStudent(RaidesInstance.getInstance(),
                    registration)) {
                result.add(registration.getStudentCandidacy().getPrecedentDegreeInformation());
            }
        }

        return result;
    }

    @Override
    protected void addModelProperties(Model model) {
        super.addModelProperties(model);
        model.addAttribute("controllerURL", getControllerURL());
    }

    protected abstract String getControllerURL();

    protected abstract boolean isFormIsFilled(final Model model);
}
