package org.fenixedu.ulisboa.specifications.domain.services;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.degreeStructure.Context;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;
import org.fenixedu.academic.domain.student.curriculum.ICurriculum;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.time.calendarStructure.AcademicPeriod;
import org.fenixedu.academic.dto.CurricularPeriodInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CurricularPeriodServices {

    static private final Logger logger = LoggerFactory.getLogger(CurricularPeriodServices.class);

    static public CurricularPeriod getCurricularPeriod(final DegreeCurricularPlan dcp, final int year, final Integer semester) {
        final CurricularPeriod result;

        if (semester == null) {
            result = getCurricularPeriod(dcp, year);

        } else {
            result = dcp.getCurricularPeriodFor(year, semester);

            if (result == null) {
                logger.info("Unsupported Curricular Period [Y{},S{}], DCP [{}] ", dcp.getPresentationName(), year, semester);
            }
        }

        return result;
    }

    static public CurricularPeriod getCurricularPeriod(final DegreeCurricularPlan dcp, final int year) {
        CurricularPeriod result = null;

        final CurricularPeriodInfoDTO dto = new CurricularPeriodInfoDTO(year, AcademicPeriod.YEAR);

        final CurricularPeriod degreeStructure = dcp.getDegreeStructure();
        if (degreeStructure != null) {
            result = degreeStructure.getCurricularPeriod(dto);
        }

        if (result == null) {
            logger.info("Unsupported Curricular Period [{},{}], DCP [{}]", dcp.getPresentationName(), dto.getPeriodType()
                    .getName(), dto.getOrder());
        }

        return result;
    }

    static public int getCurricularYear(final CurriculumLine input) {
        final DegreeModule degreeModule = input.getDegreeModule();
        final ExecutionYear executionYear = input.getExecutionYear();
        final Set<Context> contexts =
                input.getCurriculumGroup().isNoCourseGroupCurriculumGroup() ? Collections.emptySet() : input.getCurriculumGroup()
                        .getDegreeModule().getChildContextsSet();

        final String report = input.print(StringUtils.EMPTY).toString();

        return getCurricularYear(report, degreeModule, executionYear, contexts);
    }

    static public int getCurricularYear(final IDegreeModuleToEvaluate input) {
        final DegreeModule degreeModule = input.getDegreeModule();
        final ExecutionYear executionYear = input.getExecutionPeriod().getExecutionYear();
        final Set<Context> contexts = input.getCurriculumGroup().getDegreeModule().getChildContextsSet();

        final StringBuilder report = new StringBuilder();
        input.getDegreeModule().print(report, StringUtils.EMPTY, input.getContext());

        return getCurricularYear(report.toString(), degreeModule, executionYear, contexts);
    }

    static private int getCurricularYear(final String report, final DegreeModule degreeModule, final ExecutionYear executionYear,
            final Set<Context> contexts) {

        final List<Integer> curricularYears = Lists.newArrayList();
        for (final Context context : contexts) {

            if (!context.getChildDegreeModule().isLeaf()) {
                continue;
            }

            if (context.isValid(executionYear) && (degreeModule == null || context.getChildDegreeModule() == degreeModule)) {
                curricularYears.add(context.getCurricularYear());
            }
        }

        if (!curricularYears.isEmpty()) {
            return Collections.min(curricularYears);
        } else {
            logger.warn("Unable to guess curricular year for [{}], returning 1", report);
            return 1;
        }
    }

    static public Map<CurricularPeriod, BigDecimal> mapYearCredits(final ICurriculum curriculum) {
        final Map<CurricularPeriod, BigDecimal> result = Maps.newHashMap();

        final DegreeCurricularPlan dcp = curriculum.getStudentCurricularPlan().getDegreeCurricularPlan();

        for (final ICurriculumEntry iter : curriculum.getCurricularYearEntries()) {

            final int year = CurricularPeriodServices.getCurricularYear((CurriculumLine) iter);
            final CurricularPeriod curricularPeriod = CurricularPeriodServices.getCurricularPeriod(dcp, year);

            if (curricularPeriod != null) {

                final BigDecimal credits = iter.getEctsCreditsForCurriculum();
                addYearCredits(result, curricularPeriod, credits);
            }
        }

        for (final Map.Entry<CurricularPeriod, BigDecimal> entry : result.entrySet()) {
            logger.info("{} - {} ECTS", entry.getKey().getFullLabel(), entry.getValue().toPlainString());
        }

        return result;
    }

    static public void addYearCredits(final Map<CurricularPeriod, BigDecimal> result, final CurricularPeriod curricularPeriod,
            final BigDecimal credits) {

        final BigDecimal creditsYear = result.get(curricularPeriod);
        result.put(curricularPeriod, creditsYear != null ? creditsYear.add(credits) : credits);
    }

}
