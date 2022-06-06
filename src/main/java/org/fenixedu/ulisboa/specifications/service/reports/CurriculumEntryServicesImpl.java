package org.fenixedu.ulisboa.specifications.service.reports;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularPeriod.CurricularPeriod;
import org.fenixedu.academic.domain.degreeStructure.CurricularPeriodServices;
import org.fenixedu.academic.domain.student.curriculum.ICurriculum;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.student.curriculum.creditstransfer.CreditsTransferRemarksCollection;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.qubdocs.academic.documentRequests.providers.ICreditsTransferRemarksCollection;
import org.fenixedu.qubdocs.util.CurriculumEntryServices;

public class CurriculumEntryServicesImpl implements CurriculumEntryServices {

    public static class CreditsTransferRemarksCollectionBridge implements ICreditsTransferRemarksCollection {

        private CreditsTransferRemarksCollection creditsTransferRemarks;

        public CreditsTransferRemarksCollectionBridge(CreditsTransferRemarksCollection creditsTransferRemarks) {
            this.creditsTransferRemarks = creditsTransferRemarks;
        }

        @Override
        public LocalizedString getFormattedRemarks(String separator) {
            return creditsTransferRemarks.getFormattedRemarks(separator);
        }

        @Override
        public Collection<String> getRemarkIds() {
            return creditsTransferRemarks.getRemarkIds();
        }

        @Override
        public String getRemarkIdsFor(ICurriculumEntry entry) {
            return creditsTransferRemarks.getRemarkIdsFor(entry);
        }

        @Override
        public LocalizedString getRemarkTextForId(String remarkId) {
            return creditsTransferRemarks.getRemarkTextForId(remarkId);
        }

    }

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

    @Override
    public ICreditsTransferRemarksCollection buildRemarksFor(Collection<ICurriculumEntry> entries,
            StudentCurricularPlan studentCurricularPlan) {
        return new CreditsTransferRemarksCollectionBridge(CreditsTransferRemarksCollection
                .build(entries == null ? Collections.emptySet() : entries, studentCurricularPlan));
    }

}
