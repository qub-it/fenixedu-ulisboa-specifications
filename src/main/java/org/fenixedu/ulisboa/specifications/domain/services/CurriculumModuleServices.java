package org.fenixedu.ulisboa.specifications.domain.services;

import java.math.BigDecimal;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;

public class CurriculumModuleServices {

    static public BigDecimal getEnroledAndNotApprovedEctsCreditsFor(final ExecutionSemester semester,
            final CurriculumGroup toInspect) {

        BigDecimal result = BigDecimal.ZERO;
        for (final CurriculumModule curriculumModule : toInspect.getCurriculumModulesSet()) {
            result = result.add(getEnroledAndNotApprovedEctsCreditsFor(semester, curriculumModule));
        }
        return result;
    }

    static public BigDecimal getEnroledAndNotApprovedEctsCreditsFor(final ExecutionSemester semester,
            final CurriculumModule toInspect) {

        BigDecimal result = BigDecimal.ZERO;
        if (toInspect.getClass().isAssignableFrom(Enrolment.class)) {
            result = getEnroledAndNotApprovedEctsCreditsFor(semester, (Enrolment) toInspect);
        }
        return result;
    }

    static public BigDecimal getEnroledAndNotApprovedEctsCreditsFor(final ExecutionSemester semester, final Enrolment toInspect) {

        return (toInspect.isEnroled() || toInspect.isNotEvaluated() || toInspect.isFlunked())
                && toInspect.isValid(semester) ? toInspect.getEctsCreditsForCurriculum() : BigDecimal.ZERO;
    }

}
