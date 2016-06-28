package org.fenixedu.ulisboa.specifications.domain.services;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.academic.domain.studentCurriculum.Credits;
import org.fenixedu.academic.domain.studentCurriculum.EnrolmentWrapper;
import org.fenixedu.ulisboa.specifications.domain.student.RegistrationExtendedInformation;
import org.joda.time.LocalDate;

import com.google.common.collect.Sets;

public class RegistrationServices {

    public static Set<SchoolClass> getSchoolClassesToEnrolBy(final Registration registration,
            final DegreeCurricularPlan degreeCurricularPlan, final ExecutionSemester executionSemester) {

        return registration.getAssociatedAttendsSet().stream()
                .filter(attends -> attends.getExecutionPeriod() == executionSemester)
                .flatMap(attends -> attends.getExecutionCourse().getSchoolClassesBy(degreeCurricularPlan).stream())
                .collect(Collectors.toSet());
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
            for (Shift shift : schoolClass.getAssociatedShiftsSet().stream()
                    .filter(s -> attendingExecutionCourses.contains(s.getExecutionCourse())).collect(Collectors.toSet())) {
                if (!shift.reserveForStudent(registration)) {
                    throw new DomainException("error.registration.replaceSchoolClass.shiftFull", shift.getNome(),
                            shift.getShiftTypesPrettyPrint(), shift.getExecutionCourse().getName());
                }
            }
            registration.getSchoolClassesSet().add(schoolClass);
        }
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

}
