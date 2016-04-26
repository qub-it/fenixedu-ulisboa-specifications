package org.fenixedu.ulisboa.specifications.domain.services;

import java.math.BigDecimal;

import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
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

}
