package org.fenixedu.ulisboa.specifications.service.reports;

import java.math.BigDecimal;
import java.util.Map;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.student.curriculum.ICurriculum;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.qubdocs.util.CurriculumEntryServices;
import org.fenixedu.ulisboa.specifications.domain.services.CurricularPeriodServices;

public class CurriculumEntryServicesImpl implements CurriculumEntryServices {

    @Override
    public CurricularPeriod getCurricularPeriod(final DegreeCurricularPlan dcp, final int year, final Integer semester) {
        return CurricularPeriodServices.getCurricularPeriod(dcp, year, semester);
    }

    @Override
    public CurricularPeriod getCurricularPeriod(final DegreeCurricularPlan dcp, final int year) {
        return CurricularPeriodServices.getCurricularPeriod(dcp, year);
    }

    @Override
    public int getCurricularYear(final CurriculumLine input) {
        return CurricularPeriodServices.getCurricularYear(input);
    }

    @Override
    public Map<CurricularPeriod, BigDecimal> mapYearCredits(final ICurriculum curriculum) {
        return CurricularPeriodServices.mapYearCredits(curriculum);
    }

    @Override
    public void mapYearCreditsLogger(final Map<CurricularPeriod, BigDecimal> input) {
        CurricularPeriodServices.mapYearCreditsLogger(input);
    }

    @Override
    public void addYearCredits(final Map<CurricularPeriod, BigDecimal> result, final CurricularPeriod curricularPeriod,
            final BigDecimal credits, final String code) {
        CurricularPeriodServices.addYearCredits(result, curricularPeriod, credits, code);
    }

    @Override
    public int getCurricularSemester(final CurriculumLine curriculumLine) {
        return CurricularPeriodServices.getCurricularSemester(curriculumLine);
    }

}
