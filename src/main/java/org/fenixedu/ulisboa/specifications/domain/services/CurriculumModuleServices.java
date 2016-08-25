package org.fenixedu.ulisboa.specifications.domain.services;

import java.math.BigDecimal;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;
import org.fenixedu.academic.domain.studentCurriculum.Dismissal;

public class CurriculumModuleServices {

    static public BigDecimal getEnroledAndNotApprovedEctsCreditsFor(final ExecutionYear executionYear,
            final CurriculumGroup toInspect) {
        BigDecimal result = BigDecimal.ZERO;
        for (final CurriculumModule curriculumModule : toInspect.getCurriculumModulesSet()) {
            result = result.add(getEnroledAndNotApprovedEctsCreditsFor(executionYear, curriculumModule));
        }
        return result;
    }

    static public BigDecimal getEnroledAndNotApprovedEctsCreditsFor(final ExecutionYear executionYear,
            final CurriculumModule toInspect) {
        BigDecimal result = BigDecimal.ZERO;
        if (toInspect.getClass().isAssignableFrom(Enrolment.class)) {
            result = getEnroledAndNotApprovedEctsCreditsFor(executionYear, (Enrolment) toInspect);
        }
        return result;
    }

    static public BigDecimal getEnroledAndNotApprovedEctsCreditsFor(final ExecutionYear executionYear,
            final Enrolment toInspect) {
        return (toInspect.isEnroled() || toInspect.isNotEvaluated() || toInspect.isFlunked())
                && toInspect.isValid(executionYear) ? toInspect.getEctsCreditsForCurriculum() : BigDecimal.ZERO;
    }

}
