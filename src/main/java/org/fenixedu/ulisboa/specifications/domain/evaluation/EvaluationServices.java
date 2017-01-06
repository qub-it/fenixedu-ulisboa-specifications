package org.fenixedu.ulisboa.specifications.domain.evaluation;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.Evaluation;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Shift;

import com.google.common.collect.Sets;

abstract public class EvaluationServices {

    @FunctionalInterface
    static public interface IEnrolmentCourseEvaluationFinder {
        public Collection<Evaluation> findBy(final Enrolment enrolment, final EvaluationSeason evaluationSeason,
                final ExecutionSemester semester);
    }

    static private BiFunction<ExecutionCourse, EvaluationSeason, Set<Evaluation>> EVALUATION_FINDER = null;

    static private Function<Evaluation, Set<Shift>> SHIFT_FINDER = null;

    private static IEnrolmentCourseEvaluationFinder ENROLMENT_COURSE_EVALUATION_FINDER;

    static public void setCourseEvaluationsFinder(final BiFunction<ExecutionCourse, EvaluationSeason, Set<Evaluation>> input) {
        EVALUATION_FINDER = input;
    }

    static public Set<Evaluation> findCourseEvaluations(final ExecutionCourse executionCourse, final EvaluationSeason season) {
        return EVALUATION_FINDER == null ? Sets.newHashSet() : EVALUATION_FINDER.apply(executionCourse, season);
    }

    static public void setCourseEvaluationShiftFinder(final Function<Evaluation, Set<Shift>> input) {
        SHIFT_FINDER = input;
    }

    static public Set<Shift> findCourseEvaluationShifts(final Evaluation input) {
        return SHIFT_FINDER == null ? Sets.newHashSet() : SHIFT_FINDER.apply(input);
    }

    static public void setEnrolmentCourseEvaluationFinder(final IEnrolmentCourseEvaluationFinder finder) {
        ENROLMENT_COURSE_EVALUATION_FINDER = finder;
    }

    static public boolean isEnroledInAnyCourseEvaluation(final Enrolment enrolment, final EvaluationSeason evaluationSeason,
            final ExecutionSemester executionSemester) {
        return !ENROLMENT_COURSE_EVALUATION_FINDER.findBy(enrolment, evaluationSeason, executionSemester).isEmpty();
    }

    static public boolean isEnroledInCourseEvaluation(final Enrolment enrolment, final EvaluationSeason evaluationSeason,
            final ExecutionSemester executionSemester, final Evaluation evaluation) {
        return ENROLMENT_COURSE_EVALUATION_FINDER.findBy(enrolment, evaluationSeason, executionSemester).contains(evaluation);
    }

}
