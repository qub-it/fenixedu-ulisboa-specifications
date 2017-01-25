package org.fenixedu.ulisboa.specifications.domain.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.IEnrolment;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.ShiftType;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.academic.domain.student.curriculum.ICurriculum;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.Credits;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;
import org.fenixedu.academic.domain.studentCurriculum.Dismissal;
import org.fenixedu.academic.domain.studentCurriculum.EnrolmentWrapper;
import org.fenixedu.ulisboa.specifications.domain.student.RegistrationExtendedInformation;
import org.fenixedu.ulisboa.specifications.domain.student.curriculum.CurriculumConfigurationInitializer;
import org.fenixedu.ulisboa.specifications.domain.student.curriculum.CurriculumConfigurationInitializer.CurricularYearResult;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import pt.ist.fenixframework.dml.runtime.RelationAdapter;

public class RegistrationServices {

    static final private Logger logger = LoggerFactory.getLogger(RegistrationServices.class);

    static private RelationAdapter<DegreeModule, CurriculumModule> CREDITS_CREATION_DISABLE_ACCUMULATED =
            new RelationAdapter<DegreeModule, CurriculumModule>() {

                @Override
                public void afterAdd(final DegreeModule degreeModule, final CurriculumModule curriculumModule) {
                    if (degreeModule == null || curriculumModule == null) {
                        return;
                    }

                    if (!(curriculumModule instanceof Dismissal)) {
                        return;
                    }

                    final Dismissal dismissal = (Dismissal) curriculumModule;
                    final StudentCurricularPlan plan = dismissal.getStudentCurricularPlan();
                    final Registration registration = plan.getRegistration();

                    if (!isCurriculumAccumulated(registration)) {
                        return;
                    }

                    //  hasIntertwinedCredits
                    for (final IEnrolment iEnrolment : dismissal.getSourceIEnrolments()) {
                        if (iEnrolment instanceof Enrolment) {
                            final Enrolment enrolment = (Enrolment) iEnrolment;
                            if (!CurriculumLineServices.isExcludedFromCurriculum(enrolment)) {

                                if (plan != enrolment.getStudentCurricularPlan()) {
                                    setCurriculumAccumulated(registration, false);
                                    return;
                                }
                            }
                        }
                    }
                }
            };

    static {
        Dismissal.getRelationDegreeModuleCurriculumModule().addListener(CREDITS_CREATION_DISABLE_ACCUMULATED);
    }

    static final private Cache<String, ICurriculum> CACHE_CURRICULUMS =
            CacheBuilder.newBuilder().concurrencyLevel(4).maximumSize(300).expireAfterWrite(1, TimeUnit.MINUTES).build();

    static public ICurriculum getCurriculum(final Registration registration, final ExecutionYear executionYear) {
        final String key = String.format("%s#%s", registration.getExternalId(),
                executionYear == null ? "null" : executionYear.getExternalId());

        try {
            return CACHE_CURRICULUMS.get(key, new Callable<ICurriculum>() {
                @Override
                public ICurriculum call() {
                    logger.debug(String.format("Miss on Curriculum cache [%s %s]", new DateTime(), key));
                    return registration.getCurriculum(executionYear);
                }
            });

        } catch (final Throwable t) {
            throw new RuntimeException(t.getCause());
        }
    }

    static final private Cache<String, CurricularYearResult> CACHE_CURRICULAR_YEAR =
            CacheBuilder.newBuilder().concurrencyLevel(4).maximumSize(1500).expireAfterWrite(2, TimeUnit.MINUTES).build();

    static public CurricularYearResult getCurricularYear(final Registration registration, final ExecutionYear executionYear) {
        final String key = String.format("%s#%s", registration.getExternalId(),
                executionYear == null ? "null" : executionYear.getExternalId());

        try {
            return CACHE_CURRICULAR_YEAR.get(key, new Callable<CurricularYearResult>() {
                @Override
                public CurricularYearResult call() {
                    logger.warn(String.format("Miss on Registration CurricularYear cache [%s %s]", new DateTime(), key));
                    return CurriculumConfigurationInitializer
                            .calculateCurricularYear((Curriculum) getCurriculum(registration, executionYear));
                }
            });

        } catch (final Throwable t) {
            throw new RuntimeException(t.getCause());
        }
    }

    static public boolean isFlunkedUsingCurricularYear(final Registration registration, final ExecutionYear executionYear) {
        final ExecutionYear previousExecutionYear = executionYear.getPreviousExecutionYear();

        if (registration.getStartExecutionYear().isAfterOrEquals(executionYear)
                || registration.getStudentCurricularPlan(previousExecutionYear) == null
                || registration.getStudentCurricularPlan(executionYear) == null) {

            return false;
        }

        final int currentYear = getCurricularYear(registration, executionYear).getResult();
        final int previousYear = getCurricularYear(registration, executionYear.getPreviousExecutionYear()).getResult();
        return previousYear == currentYear;
    }

    public static final String FULL_SCHOOL_CLASS_EXCEPTION_MSG = "label.schoolClassStudentEnrollment.fullSchoolClass";

    private static BiFunction<Registration, ExecutionSemester, Collection<SchoolClass>> initialSchoolClassesService =
            defaultInitialSchoolClassesService();

    public static Set<SchoolClass> getSchoolClassesToEnrolBy(final Registration registration,
            final DegreeCurricularPlan degreeCurricularPlan, final ExecutionSemester executionSemester) {

        return registration.getAssociatedAttendsSet().stream()
                .filter(attends -> attends.getExecutionPeriod() == executionSemester)
                .flatMap(attends -> attends.getExecutionCourse().getSchoolClassesBy(degreeCurricularPlan).stream())
                .collect(Collectors.toSet());
    }

    public static void registerInitialSchoolClassesService(
            final BiFunction<Registration, ExecutionSemester, Collection<SchoolClass>> service) {
        initialSchoolClassesService = service;
    }

    public static Collection<SchoolClass> getInitialSchoolClassesToEnrolBy(final Registration registration,
            final ExecutionSemester executionSemester) {
        return initialSchoolClassesService.apply(registration, executionSemester);
    }

    private static BiFunction<Registration, ExecutionSemester, Collection<SchoolClass>> defaultInitialSchoolClassesService() {
        return (r, es) -> {
            final ExecutionDegree executionDegree =
                    r.getActiveDegreeCurricularPlan().getExecutionDegreeByYear(es.getExecutionYear());
            if (executionDegree != null) {
                int curricularYear = getCurricularYear(r, es.getExecutionYear()).getResult();
                return executionDegree.getSchoolClassesSet().stream().filter(sc -> sc.getCurricularYear().equals(curricularYear))
                        .collect(Collectors.toSet());
            }
            return Collections.emptyList();
        };
    }

    public static Optional<SchoolClass> getSchoolClassBy(final Registration registration,
            final ExecutionSemester executionSemester) {
        return registration.getSchoolClassesSet().stream().filter(sc -> sc.getExecutionPeriod() == executionSemester).findFirst();
    }

    public static void replaceSchoolClass(final Registration registration, final SchoolClass schoolClass,
            final ExecutionSemester executionSemester) {

        final Optional<SchoolClass> currentSchoolClass = getSchoolClassBy(registration, executionSemester);
        if (currentSchoolClass.isPresent()) {
            currentSchoolClass.get().getAssociatedShiftsSet().forEach(s -> s.removeStudents(registration));
            registration.getSchoolClassesSet().remove(currentSchoolClass.get());
        }

        if (schoolClass != null) {
            final List<ExecutionCourse> attendingExecutionCourses =
                    registration.getAttendingExecutionCoursesFor(executionSemester);
            enrolInSchoolClassExecutionCoursesShifts(registration, schoolClass, attendingExecutionCourses);
            registration.getSchoolClassesSet().add(schoolClass);
        }
    }

    public static void enrolInSchoolClassExecutionCoursesShifts(final Registration registration, final SchoolClass schoolClass,
            final List<ExecutionCourse> attendingExecutionCourses) {
        if (!isSchoolClassFree(schoolClass, registration)) {
            throw new DomainException(FULL_SCHOOL_CLASS_EXCEPTION_MSG);
        }

        final Function<Shift, Integer> vacanciesCalculator = s -> s.getLotacao() - s.getStudentsSet().size();
        final Comparator<Shift> vacanciesComparator =
                (s1, s2) -> vacanciesCalculator.apply(s1).compareTo(vacanciesCalculator.apply(s2));

        final Set<Shift> schoolClassesShifts = schoolClass.getAssociatedShiftsSet().stream()
                .filter(s -> attendingExecutionCourses.contains(s.getExecutionCourse()) && vacanciesCalculator.apply(s) > 0)
                .collect(Collectors.toSet());

        final Multimap<ExecutionCourse, Shift> shiftsByExecutionCourse = ArrayListMultimap.create();
        schoolClassesShifts.forEach(s -> shiftsByExecutionCourse.put(s.getExecutionCourse(), s));
        for (final ExecutionCourse executionCourse : shiftsByExecutionCourse.keySet()) {
            final Multimap<ShiftType, Shift> shiftsTypesByShift = ArrayListMultimap.create();
            shiftsByExecutionCourse.get(executionCourse).forEach(s -> s.getTypes().forEach(st -> shiftsTypesByShift.put(st, s)));

            for (final ShiftType shiftType : shiftsTypesByShift.keySet()) {
                if (registration.getShiftFor(executionCourse, shiftType) == null) {
                    final List<Shift> shiftsOrderedByVacancies = new ArrayList<>(shiftsTypesByShift.get(shiftType));
                    shiftsOrderedByVacancies.sort(vacanciesComparator);
                    enrolInOneShift(shiftsOrderedByVacancies, registration);
                }
            }
        }
    }

    private static void enrolInOneShift(Collection<Shift> shiftsOrderedByVacancies, Registration registration) {
        String shiftName = null;
        String shiftTypes = null;
        String executionCourseName = null;

        for (final Shift shift : shiftsOrderedByVacancies) {
            shiftName = shift.getNome();
            shiftTypes = shift.getShiftTypesPrettyPrint();
            executionCourseName = shift.getExecutionCourse().getName();

            if (shift.getStudentsSet().contains(registration) || shift.reserveForStudent(registration)) {
                return;
            }
        }

        throw new DomainException("error.registration.replaceSchoolClass.shiftFull", shiftName, shiftTypes, executionCourseName);
    }

    public static boolean isSchoolClassFree(final SchoolClass schoolClass, final Registration registration) {
        if (schoolClass != null) {
//            return !attendingShifts.stream().anyMatch(s -> s.getLotacao().intValue() <= s.getStudentsSet().size());

            final List<Shift> attendingShifts = getAttendingShifts(schoolClass, registration);

            final Multimap<ExecutionCourse, Shift> shiftsByExecutionCourse = ArrayListMultimap.create();
            attendingShifts.forEach(s -> shiftsByExecutionCourse.put(s.getExecutionCourse(), s));
            for (final ExecutionCourse executionCourse : shiftsByExecutionCourse.keySet()) {
                final Multimap<ShiftType, Shift> shiftsTypesByShift = ArrayListMultimap.create();
                shiftsByExecutionCourse.get(executionCourse)
                        .forEach(s -> s.getTypes().forEach(st -> shiftsTypesByShift.put(st, s)));

                for (final ShiftType shiftType : shiftsTypesByShift.keySet()) {
                    if (registration.getShiftFor(executionCourse, shiftType) == null && shiftsTypesByShift.get(shiftType).stream()
                            .allMatch(s -> s.getLotacao().intValue() <= s.getStudentsSet().size())) {
                        return false;
                    }
                }
            }

            return true;
        }
        return false;
    }

    public static List<Shift> getAttendingShifts(final SchoolClass schoolClass, final Registration registration) {
        final List<ExecutionCourse> attendingExecutionCourses =
                registration.getAttendingExecutionCoursesFor(schoolClass.getExecutionPeriod());
        return schoolClass.getAssociatedShiftsSet().stream()
                .filter(s -> attendingExecutionCourses.contains(s.getExecutionCourse())).collect(Collectors.toList());
    }

    public static Collection<EnrolmentEvaluation> getImprovementEvaluations(final Registration registration,
            final ExecutionYear executionYear) {
        final Collection<EnrolmentEvaluation> result = Sets.newHashSet();

        for (final ExecutionSemester executionSemester : executionYear.getExecutionPeriodsSet()) {
            for (final EnrolmentEvaluation evaluation : executionSemester.getEnrolmentEvaluationsSet()) {
                if (evaluation.getEvaluationSeason().isImprovement() && evaluation.getRegistration() == registration) {
                    result.add(evaluation);
                }
            }
        }

        return result;
    }

    public static boolean hasCreditsBetweenPlans(final Registration registration) {
        for (final StudentCurricularPlan scp : registration.getStudentCurricularPlansSet()) {
            for (final Credits credits : scp.getCreditsSet()) {
                for (EnrolmentWrapper wrapper : credits.getEnrolmentsSet()) {
                    if (wrapper.getIEnrolment().isExternalEnrolment()) {
                        continue;
                    }

                    final Enrolment e = (Enrolment) wrapper.getIEnrolment();

                    if (registration.getStudentCurricularPlansSet().contains(e.getStudentCurricularPlan())) {
                        return true;
                    }
                }
            }

        }

        return false;
    }

    public static final boolean canCollectAllPlansForCurriculum(final Registration registration) {
        return registration.getStudentCurricularPlansSet().size() > 1 && !hasCreditsBetweenPlans(registration);
    }

    public static Curriculum getAllPlansCurriculum(final Registration registration, final ExecutionYear executionYear) {
        Curriculum curriculumSum = Curriculum.createEmpty(executionYear);
        for (final StudentCurricularPlan studentCurricularPlan : registration.getStudentCurricularPlansSet()) {
            curriculumSum.add(studentCurricularPlan.getRoot().getCurriculum(executionYear));
        }

        return curriculumSum;
    }

    static public Curriculum filterCurricularYearEntriesBySemester(final Curriculum input, final Integer semester) {

        final Curriculum result = Curriculum.createEmpty(input.getCurriculumModule(), input.getExecutionYear());
        result.add(input);

        for (final Iterator<ICurriculumEntry> iterator = result.getCurricularYearEntries().iterator(); iterator.hasNext();) {
            final ICurriculumEntry iter = iterator.next();
            if (semester.intValue() != CurricularPeriodServices.getCurricularSemester((CurriculumLine) iter)) {
                iterator.remove();
            }
        }

        return result;
    }

    public static void setIngressionGradeA(Registration registration, BigDecimal grade) {
        RegistrationExtendedInformation.findOrCreate(registration).setIngressionGradeA(grade);
    }

    public static BigDecimal getIngressionGradeA(Registration registration) {
        return registration.getExtendedInformation() != null ? registration.getExtendedInformation().getIngressionGradeA() : null;
    }

    public static void setIngressionGradeB(Registration registration, BigDecimal grade) {
        RegistrationExtendedInformation.findOrCreate(registration).setIngressionGradeB(grade);
    }

    public static BigDecimal getIngressionGradeB(Registration registration) {
        return registration.getExtendedInformation() != null ? registration.getExtendedInformation().getIngressionGradeB() : null;
    }

    public static void setIngressionGradeC(Registration registration, BigDecimal grade) {
        RegistrationExtendedInformation.findOrCreate(registration).setIngressionGradeC(grade);
    }

    public static BigDecimal getIngressionGradeC(Registration registration) {
        return registration.getExtendedInformation() != null ? registration.getExtendedInformation().getIngressionGradeC() : null;
    }

    public static void setIngressionGradeD(Registration registration, BigDecimal grade) {
        RegistrationExtendedInformation.findOrCreate(registration).setIngressionGradeD(grade);
    }

    public static BigDecimal getIngressionGradeD(Registration registration) {
        return registration.getExtendedInformation() != null ? registration.getExtendedInformation().getIngressionGradeD() : null;
    }

    public static void setInternshipGrade(Registration registration, BigDecimal grade) {
        RegistrationExtendedInformation.findOrCreate(registration).setInternshipGrade(grade);
    }

    public static BigDecimal getInternshipGrade(Registration registration) {
        return registration.getExtendedInformation() != null ? registration.getExtendedInformation().getInternshipGrade() : null;
    }

    public static void setInternshipConclusionDate(Registration registration, LocalDate conclusionDate) {
        RegistrationExtendedInformation.findOrCreate(registration).setInternshipConclusionDate(conclusionDate);
    }

    public static LocalDate getInternshipConclusionDate(Registration registration) {
        return registration.getExtendedInformation() != null ? registration.getExtendedInformation()
                .getInternshipConclusionDate() : null;
    }

    static public void setCurriculumAccumulated(final Registration input, final boolean value) {
        RegistrationExtendedInformation.findOrCreate(input).setCurriculumAccumulated(value);
    }

    static public boolean isCurriculumAccumulated(final Registration input) {
        return input.getExtendedInformation() != null && input.getExtendedInformation().getCurriculumAccumulated();
    }

    public static Collection<ExecutionYear> getEnrolmentYears(Registration registration, boolean includeDismissals) {

        final Set<ExecutionYear> result = Sets.newHashSet();

        for (final ExecutionYear executionYear : registration.getSortedEnrolmentsExecutionYears()) {
            if (registration.getEnrolments(executionYear).stream().anyMatch(x -> !x.isAnnulled())) {
                result.add(executionYear);
            }
        }

        if (includeDismissals) {

            for (final StudentCurricularPlan studentCurricularPlan : registration.getStudentCurricularPlansSet()) {
                for (final Credits credits : studentCurricularPlan.getCreditsSet()) {
                    result.add(credits.getExecutionPeriod().getExecutionYear());
                }
            }
        }

        return result;
    }

}
