package org.fenixedu.ulisboa.specifications.domain.services;

import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.ExecutionCourse;

public class ExecutionCourseServices {

    public static String getCode(final ExecutionCourse executionCourse) {
        return executionCourse.getCompetenceCourses().stream().map(cc -> cc.getCode()).distinct()
                .collect(Collectors.joining(", "));
    }

    static public String getDegreeCurricularPlanPresentation(final ExecutionCourse executionCourse, final boolean shortFormat) {

        return executionCourse == null ? null : executionCourse.getAssociatedCurricularCoursesSet().stream().map(i -> {

            if (shortFormat) {
                final String fullDcpName = i.getDegreeCurricularPlan().getName();
                final int index = fullDcpName.contains(" ") ? fullDcpName.indexOf(" ") : 6;
                final String shortDcpName = StringUtils.substring(fullDcpName, 0, index);

                return i.getDegree().getCode() + " - " + shortDcpName;

            } else {

                return String.format("%s - ", i.getDegree().getCode())
                        + i.getDegreeCurricularPlan().getPresentationName(executionCourse.getExecutionYear());
            }

        }).collect(Collectors.joining("; "));
    }

}
