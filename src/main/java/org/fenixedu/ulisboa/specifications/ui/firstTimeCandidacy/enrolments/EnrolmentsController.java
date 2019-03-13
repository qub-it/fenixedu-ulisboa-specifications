package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.enrolments;

import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.curricularRules.CurricularRuleValidationType;
import org.fenixedu.academic.domain.curriculum.EnrollmentCondition;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationStateType;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.candidacy.FirstTimeCandidacy;
import org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod.AcademicEnrolmentPeriod;
import org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod.AcademicEnrolmentPeriodType;
import org.fenixedu.ulisboa.specifications.dto.enrolmentperiod.AcademicEnrolmentPeriodBean;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.mobility.MobilityFormControler;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentProcess;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(EnrolmentsController.CONTROLLER_URL)
public class EnrolmentsController extends EnrolmentAbstractController {

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/enrolments";

    Logger logger = LoggerFactory.getLogger(EnrolmentsController.class);

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    public static final String ENROL_URL = CONTROLLER_URL + _ENROL_URI;

    @Override
    protected String enrolScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes, final HttpServletRequest request) {
        FirstTimeCandidacy candidacy = FirstTimeCandidacyController.getCandidacy();
        Registration registration = candidacy.getRegistration();
        StudentCurricularPlan studentCurricularPlan = registration.getStudentCurricularPlan(candidacy.getDegreeCurricularPlan());

        //In order to enrol, registration must be active
        activateRegistration(registration, executionYear.getFirstExecutionPeriod());

        //Enrol automatically if needed
        List<AcademicEnrolmentPeriodBean> periods = getAllAcademicEnrolmentPeriods(model, candidacy);

        //Enrol in UCs
        if (enrolAutomaticallyInUCs(periods)) {
            createAutomaticEnrolments(registration, executionYear.getFirstExecutionPeriod(), studentCurricularPlan);
        } else {
            return redirectToEnrolmentProcess(periods, AcademicEnrolmentPeriodType.CURRICULAR_COURSE, model, redirectAttributes,
                    request);
        }

        //Enrol in School Classes
        if (isToEnrolInSchoolClasses(periods)) {
            return redirectToEnrolmentProcess(periods, AcademicEnrolmentPeriodType.SCHOOL_CLASS, model, redirectAttributes,
                    request);
        }

        //Enrol in School Classes
        if (isToSelectSchoolClassesPreferences(periods)) {
            return redirectToEnrolmentProcess(periods, AcademicEnrolmentPeriodType.SCHOOL_CLASS_PREFERENCE, model,
                    redirectAttributes, request);
        }

        //Enrol in Shifts
        if (isToEnrolInShifts(periods)) {
            return redirectToEnrolmentProcess(periods, AcademicEnrolmentPeriodType.SHIFT, model, redirectAttributes, request);
        }

        return nextScreen(executionYear, model, redirectAttributes);
    }

    protected String redirectToEnrolmentProcess(final List<AcademicEnrolmentPeriodBean> periods,
            final AcademicEnrolmentPeriodType type, final Model model, final RedirectAttributes redirectAttributes,
            final HttpServletRequest request) {
        List<AcademicEnrolmentPeriodBean> filteredPeriods =
                periods.stream().filter(p -> p.getEnrolmentPeriodType() == type).collect(Collectors.toList());
        List<EnrolmentProcess> enrolmentProcesses = EnrolmentProcess.buildProcesses(filteredPeriods);
        if (enrolmentProcesses == null || enrolmentProcesses.isEmpty()) {
            throw new RuntimeException("Redefine periods.");
        }

        final String url = enrolmentProcesses.iterator().next().getContinueURL(request);
        return redirect(url.replaceFirst(request.getContextPath(), ""), model, redirectAttributes);

    }

    private List<AcademicEnrolmentPeriodBean> getAllAcademicEnrolmentPeriods(final Model model,
            final FirstTimeCandidacy candidacy) {
        return AcademicEnrolmentPeriod.getEnrolmentPeriodsOpenOrUpcoming(getStudent(model), candidacy.getDegreeCurricularPlan())
                .stream().filter(p -> p.isOpen() && p.isFirstTimeRegistration()).collect(Collectors.toList());
    }

    private List<AcademicEnrolmentPeriodBean> getAllAcademicEnrolmentPeriodsEditable(
            final List<AcademicEnrolmentPeriodBean> periods) {
        return periods.stream().filter(p -> p.isEditable()).collect(Collectors.toList());
    }

    private boolean enrolAutomaticallyInUCs(final List<AcademicEnrolmentPeriodBean> periods) {
        return periods.stream().filter(p -> p.isForCurricularCourses() && p.isAutomatic()).findFirst().isPresent();
    }

    private boolean isToEnrolInSchoolClasses(final List<AcademicEnrolmentPeriodBean> periods) {
        return periods.stream().filter(p -> p.isForClasses()).findFirst().isPresent();
    }

    private boolean isToSelectSchoolClassesPreferences(final List<AcademicEnrolmentPeriodBean> periods) {
        return periods.stream().filter(p -> p.isForClassesPreference()).findFirst().isPresent();
    }

    private boolean isToEnrolInShifts(final List<AcademicEnrolmentPeriodBean> periods) {
        return periods.stream().filter(p -> p.isForShift()).findFirst().isPresent();
    }

    @Atomic
    private void activateRegistration(final Registration registration, final ExecutionSemester executionSemester) {
        RegistrationState state = registration.getActiveState();
        if (state.getStateType().equals(RegistrationStateType.INACTIVE)) {
            RegistrationState.createRegistrationState(registration, AccessControl.getPerson(), new DateTime(),
                    RegistrationStateType.REGISTERED, executionSemester);
        }
    }

    /*
     * ------------------------------------------------------
     *                Enrol in UCs
     * ------------------------------------------------------
     */
    @Atomic
    public void createAutomaticEnrolments(final Registration registration, final ExecutionSemester executionSemester,
            final StudentCurricularPlan studentCurricularPlan) {
        if (studentCurricularPlan.getEnrolmentsSet().isEmpty()) {
            createFirstTimeStudentEnrolmentsFor(studentCurricularPlan, studentCurricularPlan.getRoot(),
                    studentCurricularPlan.getDegreeCurricularPlan().getCurricularPeriodFor(1, 1), executionSemester,
                    AccessControl.getPerson().getUsername());
            registration.updateEnrolmentDate(executionSemester.getExecutionYear());
            if (hasAnnualEnrollments(studentCurricularPlan)) {
                createFirstTimeStudentEnrolmentsFor(studentCurricularPlan, studentCurricularPlan.getRoot(),
                        studentCurricularPlan.getDegreeCurricularPlan().getCurricularPeriodFor(1, 2),
                        executionSemester.getNextExecutionPeriod(), AccessControl.getPerson().getUsername());
            }
        }
    }

    void createFirstTimeStudentEnrolmentsFor(final StudentCurricularPlan studentCurricularPlan,
            final CurriculumGroup curriculumGroup, final CurricularPeriod curricularPeriod,
            final ExecutionSemester executionSemester, final String createdBy) {

        if (curriculumGroup.getDegreeModule() != null) {
            for (final Context context : curriculumGroup.getDegreeModule()
                    .getContextsWithCurricularCourseByCurricularPeriod(curricularPeriod, executionSemester)) {
                new Enrolment(studentCurricularPlan, curriculumGroup, (CurricularCourse) context.getChildDegreeModule(),
                        executionSemester, EnrollmentCondition.FINAL, createdBy);
            }
        }

        if (!curriculumGroup.getCurriculumModulesSet().isEmpty()) {
            for (final CurriculumModule curriculumModule : curriculumGroup.getCurriculumModulesSet()) {
                if (!curriculumModule.isLeaf()) {
                    createFirstTimeStudentEnrolmentsFor(studentCurricularPlan, (CurriculumGroup) curriculumModule,
                            curricularPeriod, executionSemester, createdBy);
                }
            }
        }
    }

    boolean hasAnnualEnrollments(final StudentCurricularPlan studentCurricularPlan) {
        return studentCurricularPlan.getDegreeCurricularPlan()
                .getCurricularRuleValidationType() == CurricularRuleValidationType.YEAR;
    }

    boolean hasAnnualEnrollments(final Registration registration) {
        return hasAnnualEnrollments(registration.getStudentCurricularPlan(
                ExecutionYear.findCurrent(registration.getDegree().getCalendar()).getFirstExecutionPeriod()));
    }

    @Override
    protected String backScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(getBackUrl(executionYear), model, redirectAttributes);
    }

    public static String getBackUrl(final ExecutionYear executionYear) {
        return urlWithExecutionYear(MobilityFormControler.CONTROLLER_URL, executionYear);
    }

    public static boolean shouldBeSkipped(final ExecutionYear executionYear) {
        //TODOJN
        return false;
    }

    @Override
    protected String nextScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(getNextUrl(executionYear), model, redirectAttributes);
    }

    public static String getNextUrl(final ExecutionYear executionYear) {
        return urlWithExecutionYear(CurricularCoursesController.CONTROLLER_URL, executionYear);
    }

    @Override
    protected Student getStudent(final Model model) {
        return AccessControl.getPerson().getStudent();
    }

    @Override
    public boolean isFormIsFilled(final ExecutionYear executionYear, final Student student) {
        throw new RuntimeException("Error you should not call this method.");
    }

}
