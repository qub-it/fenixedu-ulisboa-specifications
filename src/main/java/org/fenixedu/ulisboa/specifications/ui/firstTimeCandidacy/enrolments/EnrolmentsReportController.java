package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.enrolments;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

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
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationStateType;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.candidacy.FirstTimeCandidacy;
import org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod.AcademicEnrolmentPeriod;
import org.fenixedu.ulisboa.specifications.domain.services.RegistrationServices;
import org.fenixedu.ulisboa.specifications.dto.enrolmentperiod.AcademicEnrolmentPeriodBean;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.ScheduleClassesController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.health.VaccionationFormController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.motivations.MotivationsExpectationsFormController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.misc.CgdDataAuthorizationController;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentProcess;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;
import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(EnrolmentsReportController.CONTROLLER_URL)
public class EnrolmentsReportController extends EnrolmentAbstractController {

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

        //in order to enrol, registration must be active
        activateRegistration(registration);

        //Enrol automatically if needed
        List<AcademicEnrolmentPeriodBean> periods = getAllAcademicEnrolmentPeriods(model, candidacy);
        if (enrolAutomaticallyInUCs(periods)) {
            System.out.println("Inscrever UCs automaticamente");
            createAutomaticEnrolments(registration, executionYear.getFirstExecutionPeriod(), studentCurricularPlan);
        }
        Collection<ExecutionSemester> executionSemesters = hasAnnualShifts(studentCurricularPlan) ? executionYear
                .getExecutionPeriodsSet() : Collections.singleton(executionYear.getFirstExecutionPeriod());
        if (enrolAutomaticallyInSchoolClasses(periods)) {
            System.out.println("Inscrever Turmas automaticamente");
//            Optional<SchoolClass> schoolClass = readFirstAlmostFilledClass(registration, executionSemesters);
//            if (!schoolClass.isPresent()) {
//                throw new RuntimeException("School Class not defined.");
//            }
//            schoolClass.get().addRegistrations(registration);
        }
        if (enrolAutomaticallyInShifts(periods)) {
            System.out.println("Inscrever Turnos automaticamente");
//            associateShiftsFor(studentCurricularPlan, executionYear, executionSemesters);
        }

        List<EnrolmentProcess> enrolmentProcesses =
                EnrolmentProcess.buildProcesses(getAllAcademicEnrolmentPeriodsEditable(periods));
        if (enrolmentProcesses == null || enrolmentProcesses.isEmpty()) {
            System.out.println("Processo vazio");
            System.out.println("Tem " + getAllAcademicEnrolmentPeriodsEditable(periods).size() + " periodos.");
            for (AcademicEnrolmentPeriodBean period : getAllAcademicEnrolmentPeriodsEditable(periods)) {
                System.out
                        .println(period.getEnrolmentPeriodType() + "||" + period.getStartDate().toString("yyyy-MM-dd HH:mm:ss"));
            }
            return redirect(SHOW_URL, model, redirectAttributes);
        }
        //TODOJN - 
        String url = enrolmentProcesses.iterator().next().getContinueURL(request);
        url = GenericChecksumRewriter.injectChecksumInUrl(request.getContextPath(), url, request.getSession());

        return redirect(url, model, redirectAttributes);

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

    public static final String SHOW_URL = CONTROLLER_URL + _SHOW_URI;

    @Override
    protected String showScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        FirstTimeCandidacy candidacy = FirstTimeCandidacyController.getCandidacy();
        Registration registration = candidacy.getRegistration();
        StudentCurricularPlan studentCurricularPlan = registration.getStudentCurricularPlan(candidacy.getDegreeCurricularPlan());

        if (!validUCsEnrolment(studentCurricularPlan, executionYear)) {
            redirect(ENROL_URL, model, redirectAttributes);
        }

        ExecutionSemester firstSemester = executionYear.getFirstExecutionPeriod();
        ExecutionSemester secondSemester = firstSemester.getNextExecutionPeriod();
        Collection<Enrolment> firstSemEnrolments = studentCurricularPlan.getEnrolmentsByExecutionPeriod(firstSemester).stream()
                .filter(e -> e.getEctsCredits() > 0).collect(Collectors.toList());
        Double firstSemCredits = 0d;
        for (Enrolment enrolment : firstSemEnrolments) {
            firstSemCredits += enrolment.getEctsCredits();
        }
        Collection<Enrolment> secondSemEnrolments = studentCurricularPlan.getEnrolmentsByExecutionPeriod(secondSemester).stream()
                .filter(e -> e.getEctsCredits() > 0).collect(Collectors.toList());
        Double secondSemCredits = 0d;
        for (Enrolment enrolment : secondSemEnrolments) {
            secondSemCredits += enrolment.getEctsCredits();
        }

        model.addAttribute("currentYear", ExecutionYear.readCurrentExecutionYear().getYear());
        model.addAttribute("firstSemesterEnrolments", firstSemEnrolments);
        model.addAttribute("firstSemesterCredits", firstSemCredits);
        model.addAttribute("secondSemesterEnrolments", secondSemEnrolments);
        model.addAttribute("secondSemesterCredits", secondSemCredits);

        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.showSelectedCourses.info"), model);

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/showenrolments/showselectedcourses";
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
     *                Check UCs enrolled
     * ------------------------------------------------------
     */

    private boolean validUCsEnrolment(StudentCurricularPlan studentCurricularPlan, ExecutionYear executionYear) {
        Double ectsEnrolled = studentCurricularPlan.getEnrolmentsByExecutionYear(executionYear).stream()
                .map(e -> e.getEctsCredits()).reduce(Double::sum).orElse(0d);
        return ectsEnrolled > 30d;
    }

    /*
     * ------------------------------------------------------
     *                Enrol in SchoolClasses
     * ------------------------------------------------------
     */
    private Optional<SchoolClass> readFirstAlmostFilledClass(Registration registration,
            final Collection<ExecutionSemester> executionSemesters) {
        ExecutionDegree executionDegree = registration.getDegree()
                .getExecutionDegreesForExecutionYear(ExecutionYear.readCurrentExecutionYear()).iterator().next();
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

    public void associateShiftsFor(final StudentCurricularPlan studentCurricularPlan, final ExecutionYear executionYear,
            final Collection<ExecutionSemester> executionSemesters) {
        if (!studentCurricularPlan.getRegistration().getShiftsFor(executionYear.getFirstExecutionPeriod()).isEmpty()) {
            return;
        }

        Optional<SchoolClass> firstUnfilledClass =
                readFirstAlmostFilledClass(studentCurricularPlan.getRegistration(), executionSemesters);
        if (firstUnfilledClass.isPresent()) {
            logger.warn("Registering student " + studentCurricularPlan.getPerson().getUsername() + " to class "
                    + firstUnfilledClass.get().getNome());
            enrolOnShifts(firstUnfilledClass.get(), studentCurricularPlan.getRegistration());
        } else {
            logger.warn("No classes present. Skipping classes allocation for " + studentCurricularPlan.getPerson().getUsername()
                    + ". If this is expected, ignore this message.");
        }
    }

    @Atomic
    protected void enrolOnShifts(final SchoolClass schoolClass, final Registration registration) {
        if (schoolClass == null) {
            throw new DomainException("error.RegistrationOperation.avaliable.schoolClass.not.found");
        }

        RegistrationServices.replaceSchoolClass(registration, schoolClass, schoolClass.getExecutionPeriod());
    }

    @Override
    protected String backFromShowScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        if (shouldBeSkipped(executionYear)) {
            return redirect(backFromEnrolScreen(executionYear, model, redirectAttributes), model, redirectAttributes);
        }
        return redirect(ENROL_URL, model, redirectAttributes);
    }

    @Override
    protected String backFromEnrolScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        if (!VaccionationFormController.shouldBeSkipped(executionYear)) {
            return redirect(urlWithExecutionYear(VaccionationFormController.CONTROLLER_URL, executionYear), model,
                    redirectAttributes);
        } else {
            return redirect(urlWithExecutionYear(MotivationsExpectationsFormController.CONTROLLER_URL, executionYear), model,
                    redirectAttributes);
        }
    }

    protected boolean shouldBeSkipped(ExecutionYear executionYear) {
        //TODOJN
        return false;
    }

    @Override
    protected String nextScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(CgdDataAuthorizationController.CONTROLLER_URL, model, redirectAttributes);
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
