package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.enrolments;

import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotSupportedException;

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.SchoolClass;
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
import org.fenixedu.ulisboa.specifications.domain.services.RegistrationServices;
import org.fenixedu.ulisboa.specifications.dto.enrolmentperiod.AcademicEnrolmentPeriodBean;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.ScheduleClassesController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.health.VaccionationFormController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.motivations.MotivationsExpectationsFormController;
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

    Logger logger = LoggerFactory.getLogger(ScheduleClassesController.class);

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    public static final String ENROL_URL = CONTROLLER_URL + _ENROL_URI;

    @Override
    protected String enrolScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        FirstTimeCandidacy candidacy = FirstTimeCandidacyController.getCandidacy();
        Registration registration = candidacy.getRegistration();
        StudentCurricularPlan studentCurricularPlan = registration.getStudentCurricularPlan(candidacy.getDegreeCurricularPlan());

        //In order to enrol, registration must be active
        activateRegistration(registration);

        //Enrol automatically if needed
        List<AcademicEnrolmentPeriodBean> periods = getAllAcademicEnrolmentPeriods(model, candidacy);

        //Enrol in UCs
        if (enrolAutomaticallyInUCs(periods)) {
            System.out.println("Inscrever UCs automaticamente");
            createAutomaticEnrolments(registration, executionYear.getFirstExecutionPeriod(), studentCurricularPlan);
        }

        //Enrol in School Classes

        //Enrol in Shifts

//        Collection<ExecutionSemester> executionSemesters = hasAnnualShifts(studentCurricularPlan) ? executionYear
//                .getExecutionPeriodsSet() : Collections.singleton(executionYear.getFirstExecutionPeriod());
//        if (enrolAutomaticallyInSchoolClasses(periods)) {
//            System.out.println("Inscrever Turmas automaticamente");
//            Optional<SchoolClass> schoolClass = readFirstAlmostFilledClass(registration, executionSemesters);
//            if (!schoolClass.isPresent()) {
//                throw new RuntimeException("School Class not defined.");
//            }
//            enrolInSchoolClass();
//        }
//        if (enrolAutomaticallyInShifts(periods)) {
//            System.out.println("Inscrever Turnos automaticamente");
//            associateShiftsFor(studentCurricularPlan, executionYear, executionSemesters);
//        }

        List<EnrolmentProcess> enrolmentProcesses =
                EnrolmentProcess.buildProcesses(getAllAcademicEnrolmentPeriodsEditable(periods));
        if (enrolmentProcesses == null || enrolmentProcesses.isEmpty()) {
            System.out.println("Processo vazio");
            System.out.println("Tem " + getAllAcademicEnrolmentPeriodsEditable(periods).size() + " periodos.");
            for (AcademicEnrolmentPeriodBean period : getAllAcademicEnrolmentPeriodsEditable(periods)) {
                System.out
                        .println(period.getEnrolmentPeriodType() + "||" + period.getStartDate().toString("yyyy-MM-dd HH:mm:ss"));
            }
            return nextScreen(executionYear, model, redirectAttributes);
        }

        final String url = enrolmentProcesses.iterator().next().getContinueURL(request);
        return redirect(url, model, redirectAttributes);
    }

    private void enrolInSchoolClass(Registration registration, SchoolClass schoolClass, ExecutionSemester executionSemester) {
        RegistrationServices.replaceSchoolClass(registration, schoolClass, executionSemester);
    }

    private List<AcademicEnrolmentPeriodBean> getAllAcademicEnrolmentPeriods(Model model, FirstTimeCandidacy candidacy) {
        return AcademicEnrolmentPeriod.getEnrolmentPeriodsOpenOrUpcoming(getStudent(model), candidacy.getDegreeCurricularPlan())
                .stream().filter(p -> p.isOpen() && p.isFirstTimeRegistration()).collect(Collectors.toList());
    }

    private List<AcademicEnrolmentPeriodBean> getAllAcademicEnrolmentPeriodsEditable(List<AcademicEnrolmentPeriodBean> periods) {
        return periods.stream().filter(p -> p.isEditable()).collect(Collectors.toList());
    }

    private boolean enrolAutomaticallyInUCs(List<AcademicEnrolmentPeriodBean> periods) {
        return periods.stream().filter(p -> p.isForCurricularCourses() && p.isAutomatic()).findFirst().isPresent();
    }

    private boolean enrolAutomaticallyInSchoolClasses(List<AcademicEnrolmentPeriodBean> periods) {
        return periods.stream().filter(p -> p.isForClasses() && p.isAutomatic()).findFirst().isPresent();
    }

    private boolean enrolAutomaticallyInShifts(List<AcademicEnrolmentPeriodBean> periods) {
        return periods.stream().filter(p -> p.isForShift() && p.isAutomatic()).findFirst().isPresent();
    }

    @Atomic
    private void activateRegistration(Registration registration) {
        RegistrationState state = registration.getActiveState();
        if (state.getStateType().equals(RegistrationStateType.INACTIVE)) {
            RegistrationState.createRegistrationState(registration, AccessControl.getPerson(), new DateTime(),
                    RegistrationStateType.REGISTERED);
        }
    }

    /*
     * ------------------------------------------------------
     *                Enrol in UCs
     * ------------------------------------------------------
     */
    @Atomic
    public void createAutomaticEnrolments(Registration registration, ExecutionSemester executionSemester,
            StudentCurricularPlan studentCurricularPlan) {
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

    void createFirstTimeStudentEnrolmentsFor(StudentCurricularPlan studentCurricularPlan, CurriculumGroup curriculumGroup,
            CurricularPeriod curricularPeriod, ExecutionSemester executionSemester, String createdBy) {

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

    boolean hasAnnualEnrollments(StudentCurricularPlan studentCurricularPlan) {
        return studentCurricularPlan.getDegreeCurricularPlan()
                .getCurricularRuleValidationType() == CurricularRuleValidationType.YEAR;
    }

    boolean hasAnnualEnrollments(Registration registration) {
        return hasAnnualEnrollments(
                registration.getStudentCurricularPlan(ExecutionYear.readCurrentExecutionYear().getFirstExecutionPeriod()));
    }

    /*
     * ------------------------------------------------------
     *                Enrol in SchoolClasses
     * ------------------------------------------------------
     */
    private Optional<SchoolClass> readFirstAlmostFilledClass(Registration registration, ExecutionYear executionYear,
            final Collection<ExecutionSemester> executionSemesters) {
        ExecutionDegree executionDegree =
                registration.getDegree().getExecutionDegreesForExecutionYear(executionYear).iterator().next();
        return executionDegree.getSchoolClassesSet().stream()
                .filter(sc -> sc.getAnoCurricular().equals(1) && executionSemesters.contains(sc.getExecutionPeriod())
                        && AlmostFilledClassesComparator.getFreeVacancies(sc) > 0)
                .sorted(new AlmostFilledClassesComparator()).findFirst();
    }

    private boolean hasAnnualShifts(StudentCurricularPlan studentCurricularPlan) {
        return studentCurricularPlan.getDegreeCurricularPlan()
                .getCurricularRuleValidationType() == CurricularRuleValidationType.YEAR;
    }

    /*
     * ------------------------------------------------------
     *                Enrol in Shifts
     * ------------------------------------------------------
     */

    @Atomic
    public void associateShiftsFor(final Registration registration, final ExecutionSemester executionSemester) {
        Optional<SchoolClass> optional = RegistrationServices.getSchoolClassBy(registration, executionSemester);
        if (!optional.isPresent()) {
            throw new NotSupportedException("Automatic Shift Enrolments not supported without school classes.");
        }

        RegistrationServices.replaceSchoolClass(registration, optional.get(), executionSemester);

    }

    @Override
    protected String backScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(getBackUrl(executionYear), model, redirectAttributes);
    }

    public static String getBackUrl() {
        return getBackUrl(ExecutionYear.readCurrentExecutionYear());
    }

    public static String getBackUrl(ExecutionYear executionYear) {
        if (!VaccionationFormController.shouldBeSkipped(executionYear)) {
            return urlWithExecutionYear(VaccionationFormController.CONTROLLER_URL, executionYear);
        } else {
            return urlWithExecutionYear(MotivationsExpectationsFormController.CONTROLLER_URL, executionYear);
        }
    }

    public static boolean shouldBeSkipped(ExecutionYear executionYear) {
        //TODOJN
        return false;
    }

    @Override
    protected String nextScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(getNextUrl(executionYear), model, redirectAttributes);
    }

    public static String getNextUrl() {
        return getNextUrl(ExecutionYear.readCurrentExecutionYear());
    }

    public static String getNextUrl(ExecutionYear executionYear) {
        return urlWithExecutionYear(CurricularCoursesController.CONTROLLER_URL, executionYear);
    }

    @Override
    protected Student getStudent(Model model) {
        return AccessControl.getPerson().getStudent();
    }

    @Override
    public boolean isFormIsFilled(ExecutionYear executionYear, Student student) {
        throw new RuntimeException("Error you should not call this method.");
    }

}
