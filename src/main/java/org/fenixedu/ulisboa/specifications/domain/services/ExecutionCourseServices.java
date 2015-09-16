package org.fenixedu.ulisboa.specifications.domain.services;

import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionCourse;

public class ExecutionCourseServices {

    public static String getCode(final ExecutionCourse executionCourse) {
        return executionCourse.getCompetenceCourses().stream().map(cc -> cc.getCode()).distinct()
                .collect(Collectors.joining(", "));
    }

}
