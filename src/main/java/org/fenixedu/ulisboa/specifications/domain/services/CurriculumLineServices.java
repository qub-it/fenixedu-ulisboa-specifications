package org.fenixedu.ulisboa.specifications.domain.services;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.comparators.ComparatorChain;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.OptionalEnrolment;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregator;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorServices;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumLineExtendedInformation;

import com.google.common.collect.Lists;

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
        if (CurriculumAggregatorServices.isAggregationsActive(curriculumLine.getExecutionYear())) {

            final CurriculumAggregator aggregator =
                    CurriculumAggregatorServices.getAggregationRoot(CurriculumAggregatorServices.getContext(curriculumLine));
            if (aggregator != null) {
                aggregator.updateEvaluation(curriculumLine.getStudentCurricularPlan());
            }
        }
    }

    static public void setExcludedFromAverage(CurriculumLine curriculumLine, Boolean excludedFromAverage) {
        CurriculumLineExtendedInformation.findOrCreate(curriculumLine).setExcludedFromAverage(excludedFromAverage);
    }

    static public boolean isExcludedFromAverage(CurriculumLine curriculumLine) {
        return curriculumLine.getExtendedInformation() != null
                && curriculumLine.getExtendedInformation().getExcludedFromAverage() != null
                && curriculumLine.getExtendedInformation().getExcludedFromAverage().booleanValue();
    }

    static public void setExcludedFromCurriculum(final CurriculumLine input, final boolean value) {
        CurriculumLineExtendedInformation.findOrCreate(input).setExcludedFromCurriculum(value);
    }

    static public boolean isExcludedFromCurriculum(final CurriculumLine input) {
        return input.getExtendedInformation() != null && input.getExtendedInformation().getExcludedFromCurriculum();
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

    static private Comparator<CurriculumLine> COMPARATOR_CONTEXT = (o1, o2) -> {
        final Optional<Context> c1 = CurriculumLineServices.getParentContexts(o1).stream().sorted(Context::compareTo).findFirst();
        final Optional<Context> c2 = CurriculumLineServices.getParentContexts(o2).stream().sorted(Context::compareTo).findFirst();

        if (c1.isPresent() && !c2.isPresent()) {
            return -1;
        }

        if (!c1.isPresent() && c2.isPresent()) {
            return 1;
        }

        if (!c1.isPresent() && !c2.isPresent()) {
            return 0;
        }

        return c1.get().compareTo(c2.get());
    };

    static private Comparator<CurriculumLine> COMPARATOR_FULL_PATH = (o1, o2) -> {
        final List<CurriculumGroup> fullPath1 = collectFullPath(o1.getCurriculumGroup());
        final List<CurriculumGroup> fullPath2 = collectFullPath(o2.getCurriculumGroup());

        for (int i = 0; i < Math.max(fullPath1.size(), fullPath2.size()); i++) {
            final CurriculumGroup group1 = i >= fullPath1.size() ? null : fullPath1.get(i);
            final CurriculumGroup group2 = i >= fullPath2.size() ? null : fullPath2.get(i);

            if (group1 == null && group2 != null) {
                return -1;
            }

            if (group1 != null && group2 == null) {
                return 1;
            }

            if (group1 == group2) {
                continue;
            }

            if (group1.getDegreeModule() == null && group2.getDegreeModule() != null) {
                return 1;
            }

            if (group1.getDegreeModule() != null && group2.getDegreeModule() == null) {
                return -1;
            }

            if (group1.getDegreeModule() == null && group2.getDegreeModule() == null) {
                return 0;
            }

            final Context context1 = group1.getDegreeModule().getParentContextsSet().iterator().next();
            final Context context2 = group2.getDegreeModule().getParentContextsSet().iterator().next();
            return context1.compareTo(context2);
        }

        return 0;
    };

    static private List<CurriculumGroup> collectFullPath(final CurriculumGroup input) {
        final List<CurriculumGroup> result = Lists.newArrayList();

        if (input != null) {
            result.addAll(collectFullPath(input.getCurriculumGroup()));
            result.add(input);
        }

        return result;
    }

    static public Comparator<CurriculumLine> COMPARATOR = (o1, o2) -> {
        final ComparatorChain comparatorChain = new ComparatorChain();
        comparatorChain.addComparator(COMPARATOR_FULL_PATH);
        comparatorChain.addComparator(COMPARATOR_CONTEXT);
        comparatorChain.addComparator(ICurriculumEntry.COMPARATOR_BY_EXECUTION_PERIOD_AND_NAME_AND_ID);

        return comparatorChain.compare(o1, o2);
    };

    static public Collection<Context> getParentContexts(final CurriculumLine curriculumLine) {

        if (curriculumLine.getDegreeModule() == null) {
            return Collections.emptySet();
        }

        final CurricularCourse curricularCourse =
                curriculumLine instanceof OptionalEnrolment ? ((OptionalEnrolment) curriculumLine)
                        .getOptionalCurricularCourse() : curriculumLine.getCurricularCourse();

        return curricularCourse.getParentContextsByExecutionYear(curriculumLine.getExecutionYear()).stream()
                .filter(c -> c.getParentCourseGroup() == curriculumLine.getCurriculumGroup().getDegreeModule())
                .collect(Collectors.toSet());

    }

}
