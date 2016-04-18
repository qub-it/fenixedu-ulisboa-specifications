package org.fenixedu.ulisboa.specifications.domain.services;

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

}
