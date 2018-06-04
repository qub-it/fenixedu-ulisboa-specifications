package org.fenixedu.ulisboa.specifications.ui.blue_record.authentication;

import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.candidacy.Candidacy;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.ulisboa.specifications.authentication.IULisboaRedirectionHandler;
import org.fenixedu.ulisboa.specifications.domain.bluerecord.BlueRecordConfiguration;
import org.fenixedu.ulisboa.specifications.domain.candidacy.FirstTimeCandidacy;
import org.fenixedu.ulisboa.specifications.domain.services.student.StudentServices;
import org.fenixedu.ulisboa.specifications.ui.blue_record.BlueRecordEntryPoint;
import org.fenixedu.ulisboa.specifications.ui.blue_record.CgdDataAuthorizationControllerBlueRecord;
import org.fenixedu.ulisboa.specifications.ui.blue_record.DisabilitiesFormControllerBlueRecord;
import org.fenixedu.ulisboa.specifications.ui.blue_record.HouseholdInformationFormControllerBlueRecord;
import org.fenixedu.ulisboa.specifications.ui.blue_record.MotivationsExpectationsFormControllerBlueRecord;
import org.fenixedu.ulisboa.specifications.ui.blue_record.OriginInformationFormControllerBlueRecord;
import org.fenixedu.ulisboa.specifications.ui.blue_record.PersonalInformationFormControllerBlueRecord;
import org.fenixedu.ulisboa.specifications.ui.blue_record.PreviousDegreeOriginInformationFormControllerBlueRecord;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.CourseEnrolmentDA;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.EnrolmentManagementDA;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.SchoolClassPreferenceStudentEnrollmentDA;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.SchoolClassStudentEnrollmentDA;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.ShiftEnrolmentController;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;

public class BlueRecordRedirector implements IULisboaRedirectionHandler {

    @Override
    public boolean isToRedirect(final User user, final HttpServletRequest request) {
        if (user == null) {
            return false;
        }

        if (user.getPerson() == null) {
            return false;
        }

        if (user.getPerson().getStudent() == null) {
            return false;
        }

        if (isEnrolmentStep(request)) {
            return false;
        }

        if (isFirstTimeCandidacies(user, request)) {
            return false;
        }

        if (isDegreeExcluded(user, request)) {
            return false;
        }

        for (final ExecutionYear executionYear : getExecutionYearsToProcess()) {

            if (!hasSomeBlueRecordFormToFill(executionYear, user.getPerson().getStudent())) {
                continue;
            }

            if (!StudentServices
                    .findActiveFirstTimeRegistrationsOrWithEnrolments(executionYear, AccessControl.getPerson().getStudent())
                    .isEmpty()) {
                return true;
            }
        }

        return false;
    }

    private List<ExecutionYear> getExecutionYearsToProcess() {

        final List<ExecutionYear> result = new ArrayList<>();

        final ExecutionYear currentExecutionYear = ExecutionYear.readCurrentExecutionYear();
        result.add(currentExecutionYear);
        if (currentExecutionYear.getNextExecutionYear() != null) {
            result.add(currentExecutionYear.getNextExecutionYear());
        }

        return result;
    }

    private boolean hasSomeBlueRecordFormToFill(final ExecutionYear executionYear, final Student student) {
        boolean result = false;
        result |= !new DisabilitiesFormControllerBlueRecord().isFormIsFilled(executionYear, student);
        result |= !new HouseholdInformationFormControllerBlueRecord().isFormIsFilled(executionYear, student);
        result |= !new MotivationsExpectationsFormControllerBlueRecord().isFormIsFilled(executionYear, student);
        result |= !new OriginInformationFormControllerBlueRecord().isFormIsFilled(executionYear, student);
        result |= !new PersonalInformationFormControllerBlueRecord().isFormIsFilled(executionYear, student);
        result |= !new PreviousDegreeOriginInformationFormControllerBlueRecord().isFormIsFilled(executionYear, student);
        result |= !new CgdDataAuthorizationControllerBlueRecord().isFormIsFilled(executionYear, student);

        return result;
    }

    @Override
    public String redirectionPath(final User user, final HttpServletRequest request) {
        for (final ExecutionYear executionYear : getExecutionYearsToProcess()) {

            if (!hasSomeBlueRecordFormToFill(executionYear, user.getPerson().getStudent())) {
                continue;
            }

            if (!StudentServices
                    .findActiveFirstTimeRegistrationsOrWithEnrolments(executionYear, AccessControl.getPerson().getStudent())
                    .isEmpty()) {
                return BlueRecordEntryPoint.CONTROLLER_URL + "/" + executionYear.getExternalId();
            }
        }

        throw new RuntimeException("error");
    }

    static private boolean isEnrolmentStep(final HttpServletRequest request) {

        final List<String> stepsURLs =
                Lists.newArrayList(EnrolmentManagementDA.getEndURL(), CourseEnrolmentDA.getInstructionsEntryPointURL(),
                        CourseEnrolmentDA.getEntryPointURL(), SchoolClassStudentEnrollmentDA.getEntryPointURL(),
                        ShiftEnrolmentController.getEntryPointURL(), SchoolClassPreferenceStudentEnrollmentDA.getEntryPointURL());

        for (final String iter : stepsURLs) {

            String aux = iter;

            // NOTE: at this stage of filter chain, ActionMapping or something else is not available
            // TODO legidio, deal with ui layer requests...

            if (iter.contains("?")) {
                // action
                aux = iter.substring(0, iter.lastIndexOf("?"));

            } else if (StringUtils.countMatches(aux, "/") > 3) {
                // probably spring
                aux = aux.endsWith("/") ? aux.substring(0, aux.lastIndexOf("/")) : aux;
                aux = aux.substring(0, aux.lastIndexOf("/"));
            }

            if (request.getRequestURL().toString().contains(aux)) {
                return true;
            }
        }

        return false;
    }

    private boolean isFirstTimeCandidacies(final User user, final HttpServletRequest request) {
        String path = request.getRequestURL().toString();
        Optional<Candidacy> candidacy = user.getPerson().getCandidaciesSet().stream().filter(FirstTimeCandidacy.isFirstTime)
                .filter(FirstTimeCandidacy.isOpen).findAny();

        return candidacy.isPresent() || path.contains(FIRST_TIME_START_URL);
    }

    private boolean isDegreeExcluded(final User user, final HttpServletRequest request) {
        Set<Degree> excludedDegrees = BlueRecordConfiguration.getInstance().getExclusiveDegreesSet();

        return user.getPerson().getStudent().getRegistrationsSet().stream()
                .anyMatch(r -> r.isRegistered(new DateTime()) && excludedDegrees.contains(r.getDegree()));
    }
}
