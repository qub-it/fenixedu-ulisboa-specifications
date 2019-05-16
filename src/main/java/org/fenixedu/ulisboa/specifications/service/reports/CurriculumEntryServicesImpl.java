package org.fenixedu.ulisboa.specifications.service.reports;

import java.math.BigDecimal;
import java.util.Map;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.student.curriculum.ICurriculum;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.qubdocs.util.CurriculumEntryServices;
import org.fenixedu.ulisboa.specifications.domain.services.CurricularPeriodServices;
import org.fenixedu.ulisboa.specifications.domain.services.CurriculumLineServices;
import org.fenixedu.ulisboa.specifications.domain.student.curriculum.AverageEntry;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;

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
    public LocalizedString getCurriculumEntryDescription(final ICurriculumEntry input,
            final StudentCurricularPlan studentCurricularPlan) {
        LocalizedString result = CurriculumLineServices.getCurriculumEntryDescription(input, studentCurricularPlan, false, false);

        // null forces hidden; empty forces fallback
        if (result != null) {

            // for reports, we don't want a translation of a class (Enrolment/Substitution/Equivalence) but something else
            final String content = result.getContent();
            if (content.equals(AverageEntry.ENTRY_INFO_EMPTY) || content.equals(AverageEntry.ENTRY_INFO_EQUALS)
                    || content.equals(ULisboaSpecificationsUtil.bundleI18N("label.approvalType.Enrolment").getContent())
                    || content.equals(ULisboaSpecificationsUtil.bundleI18N("label.approvalType.Substitution").getContent())
                    || content.equals(ULisboaSpecificationsUtil.bundleI18N("label.approvalType.Equivalence").getContent())) {

                result = new LocalizedString();
            }
        }

        return result;
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
