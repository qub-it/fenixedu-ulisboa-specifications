package org.fenixedu.ulisboa.specifications.service.reports.helpers;

import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.degreeStructure.CurricularPeriodServices;

import com.qubit.terra.docs.util.helpers.IDocumentHelper;

public class CurricularSpecificationHelper implements IDocumentHelper {

    public boolean isForCurricularYear(final ICurriculumEntry entry, final int year) {
        if (entry instanceof CurriculumLine) {
            int entryYear = CurricularPeriodServices.getCurricularYear((CurriculumLine) entry);
            return entryYear == year;
        }
        return false;
    }
}
