package org.fenixedu.ulisboa.specifications.domain.evaluation;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.fenixedu.academic.domain.Attends;
import org.fenixedu.academic.domain.Evaluation;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Shift;

import com.google.common.collect.Sets;

abstract public class EvaluationServices {

    static private BiFunction<ExecutionCourse, EvaluationSeason, Set<Evaluation>> EVALUATION_FINDER = null;

    static private Function<Evaluation, Set<Shift>> SHIFT_FINDER = null;
    
    static private Function<Evaluation, Set<Attends>> ATTENDS_FINDER = null;

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

    static public void setCourseEvaluationAttendsFinder(final Function<Evaluation, Set<Attends>> input) {
        ATTENDS_FINDER = input;
    }

    static public Set<Attends> findCourseEvaluationAttends(final Evaluation input) {
        return ATTENDS_FINDER == null ? Sets.newHashSet() : ATTENDS_FINDER.apply(input);
    }

}
