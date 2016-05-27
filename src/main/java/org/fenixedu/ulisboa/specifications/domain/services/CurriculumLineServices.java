package org.fenixedu.ulisboa.specifications.domain.services;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.OptionalEnrolment;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregator;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorServices;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumLineExtendedInformation;

public class CurriculumLineServices {

    static public void setRemarks(final CurriculumLine curriculumLine, final String remarks) {
        CurriculumLineExtendedInformation.findOrCreate(curriculumLine).setRemarks(remarks);
    }

    static public String getRemarks(final CurriculumLine curriculumLine) {
        return curriculumLine.getExtendedInformation() == null ? null : curriculumLine.getExtendedInformation().getRemarks();
    }

    static public void setCurricularYear(final CurriculumLine curriculumLine, final Integer curricularYear) {
        CurriculumLineExtendedInformation.findOrCreate(curriculumLine).setCurricularYear(curricularYear);
    }

    static public Integer getCurricularYear(final CurriculumLine curriculumLine) {
        return curriculumLine.getExtendedInformation() == null ? null : curriculumLine.getExtendedInformation()
                .getCurricularYear();
    }

    static public void updateAggregatorEvaluation(final CurriculumLine curriculumLine) {
        final CurriculumAggregator aggregator =
                CurriculumAggregatorServices.getAggregationRoot(CurriculumAggregatorServices.getContext(curriculumLine));
        if (aggregator != null) {
            aggregator.updateEvaluation(curriculumLine.getStudentCurricularPlan());
        }
    }

    static public void setExcludedFromAverage(CurriculumLine curriculumLine, Boolean excludedFromAverage) {
        CurriculumLineExtendedInformation.findOrCreate(curriculumLine).setExcludedFromAverage(excludedFromAverage);
    }

    static public Boolean isExcludedFromAverage(CurriculumLine curriculumLine) {
        return curriculumLine.getExtendedInformation() != null
                && curriculumLine.getExtendedInformation().getExcludedFromAverage() != null
                && curriculumLine.getExtendedInformation().getExcludedFromAverage().booleanValue();
    }

    static public void setEctsCredits(CurriculumLine curriculumLine, BigDecimal ectsCredits) {
        CurriculumLineExtendedInformation.findOrCreate(curriculumLine).setEctsCredits(ectsCredits);
    }

    static public BigDecimal getEctsCredits(CurriculumLine curriculumLine) {
        return curriculumLine.getExtendedInformation() == null ? null : curriculumLine.getExtendedInformation().getEctsCredits();
    }

    static public void setWeight(CurriculumLine curriculumLine, BigDecimal weight) {
        CurriculumLineExtendedInformation.findOrCreate(curriculumLine).setWeight(weight);
    }

    static public BigDecimal getWeight(CurriculumLine curriculumLine) {
        return curriculumLine.getExtendedInformation() == null ? null : curriculumLine.getExtendedInformation().getWeight();
    }

    static public Collection<Context> getParentContexts(final CurriculumLine curriculumLine) {

        if (curriculumLine.getDegreeModule() == null) {
            return Collections.emptySet();
        }

        final CurricularCourse curricularCourse = curriculumLine.isOptional() ? ((OptionalEnrolment) curriculumLine)
                .getOptionalCurricularCourse() : curriculumLine.getCurricularCourse();

        return curricularCourse.getParentContextsByExecutionYear(curriculumLine.getExecutionYear()).stream()
                .filter(c -> c.getParentCourseGroup() == curriculumLine.getCurriculumGroup().getDegreeModule())
                .collect(Collectors.toSet());

    }

}
