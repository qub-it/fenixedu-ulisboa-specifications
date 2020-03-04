package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.candidacy.CandidacySituationType;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.student.PersonalIngressionData;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.ulisboa.specifications.domain.services.candidacy.IngressionServices;
import org.fenixedu.ulisboa.specifications.domain.services.student.StudentServices;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public abstract class FirstTimeCandidacyAbstractController extends FenixeduUlisboaSpecificationsBaseController {

    public Optional<String> accessControlRedirect(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        List<String> errorMessages = FirstTimeCandidacyController.isValidForFirstTimeCandidacy();

        if (!errorMessages.isEmpty()) {
            for (String errorMessage : errorMessages) {
                addErrorMessage(errorMessage, model);
            }
            return Optional.of(redirect(FirstTimeCandidacyController.CONTROLLER_URL, model, redirectAttributes));
        }

        return Optional.empty();
    }

    @Atomic
    public static PersonalIngressionData getOrCreatePersonalIngressionDataForCurrentExecutionYear(
            final ExecutionYear executionYear, final Student student) {
        return getPersonalIngressionData(student, executionYear, true);
    }

    @Atomic
    public static PersonalIngressionData getPersonalIngressionData(final Student student, final ExecutionYear executionYear,
            final boolean create) {
        PersonalIngressionData personalData = student.getPersonalIngressionDataByExecutionYear(executionYear);

        if (personalData != null) {
            // Found one
            return personalData;
        }

        if (!create) {
            return null;
        }
        
        return new PersonalIngressionData(student, executionYear);

//        // Create personal ingression data with one precedentDegreeInformation from candidacy if possible
//        final Set<PrecedentDegreeInformation> pdiSetFromCandidacy = Sets.newHashSet();
//        final Set<StudentCandidacy> candidacies = student.getPerson().getCandidaciesSet();
//        for (final StudentCandidacy candidacy : candidacies) {
//            final CandidacySituationType candidacyState = candidacy.getState();
//            if (candidacyState == null || !candidacyState.isActive()) {
//                continue;
//            }
//
//            if (candidacy.getExecutionYear() != executionYear) {
//                continue;
//            }
//
//            pdiSetFromCandidacy.add(candidacy.getPrecedentDegreeInformation());
//        }
//
//        if (!pdiSetFromCandidacy.isEmpty()) {
//            return new PersonalIngressionData(student, executionYear /*, pdiSetFromCandidacy.iterator().next()*/);
//        }
//
//        if (!student.getRegistrationsSet().isEmpty()) {
////            final PrecedentDegreeInformation pid = new PrecedentDegreeInformation();
////            pid.setRegistration(student.getRegistrationsSet().iterator().next());
//
//            return new PersonalIngressionData(student, executionYear /*, pid*/);
//        }
//
//        // Cannot create personal ingression data
//        return null;
    }

    protected List<PrecedentDegreeInformation> findCompletePrecedentDegreeInformationsToFill(final ExecutionYear executionYear,
            final Student student) {
        final List<Registration> activeRegistrationsWithEnrolments =
                Lists.newArrayList(StudentServices.findActiveFirstTimeRegistrationsOrWithEnrolments(executionYear, student));

        final List<PrecedentDegreeInformation> result = Lists.newArrayList();
        for (final Registration registration : activeRegistrationsWithEnrolments) {
            if (IngressionServices.isCompletePrecedentDegreeInformationMissing(registration)) {
                result.add(registration.getStudentCandidacy().getPrecedentDegreeInformation());
            }
        }

        return result;
    }

    protected List<PrecedentDegreeInformation> findPreviousDegreePrecedentDegreeInformationsToFill(
            final ExecutionYear executionYear, final Student student) {
        final List<Registration> activeRegistrationsWithEnrolments =
                StudentServices.findActiveFirstTimeRegistrationsOrWithEnrolments(executionYear, student);
        final List<PrecedentDegreeInformation> result = Lists.newArrayList();

        for (final Registration registration : activeRegistrationsWithEnrolments) {
            if (IngressionServices.isUnfinishedSourceDegreeInformationMissing(registration)) {
                result.add(registration.getStudentCandidacy().getPrecedentDegreeInformation());
            }
        }

        return result;
    }

    @Override
    protected void addModelProperties(final Model model) {
        super.addModelProperties(model);
    }

    protected void addControllerURLToModel(final ExecutionYear executionYear, final Model model) {
        model.addAttribute("controllerURL", getControllerURLWithExecutionYear(executionYear));
    }

    public static final String urlWithExecutionYear(final String url, final ExecutionYear executionYear) {
        return url.replace("{executionYearId}", executionYear.getExternalId());
    }

    protected final String getControllerURLWithExecutionYear(final ExecutionYear executionYear) {
        return urlWithExecutionYear(getControllerURL(), executionYear);
    }

    protected boolean isFormIsFilled(final ExecutionYear executionYear, final Model model) {
        return isFormIsFilled(executionYear, getStudent(model));
    }

    public abstract boolean isFormIsFilled(final ExecutionYear executionYear, final Student student);

    protected abstract Student getStudent(final Model model);

    protected abstract String getControllerURL();

}
