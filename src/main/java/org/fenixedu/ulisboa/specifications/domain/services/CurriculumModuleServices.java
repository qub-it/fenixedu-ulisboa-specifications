package org.fenixedu.ulisboa.specifications.domain.services;

import java.math.BigDecimal;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.curricularRules.CreditsLimit;
import org.fenixedu.academic.domain.curricularRules.CurricularRuleType;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;
import org.fenixedu.academic.domain.studentCurriculum.Dismissal;

public class CurriculumModuleServices {

    static public BigDecimal getCreditsConcluded(final CurriculumGroup toInspect, final ExecutionInterval interval) {
        if (interval instanceof ExecutionSemester) {
            return getCreditsConcluded(toInspect,
                    ExecutionInterval.assertExecutionIntervalType(ExecutionSemester.class, interval));
        } else {
            return BigDecimal.valueOf(
                    toInspect.getCreditsConcluded(ExecutionInterval.assertExecutionIntervalType(ExecutionYear.class, interval)));
        }
    }

    static private BigDecimal getCreditsConcluded(final CurriculumGroup toInspect, final ExecutionSemester semester) {

        BigDecimal result = BigDecimal.ZERO;

        if (toInspect.isNoCourseGroupCurriculumGroup()) {
            return result;
        }

        for (final CurriculumModule iter : toInspect.getCurriculumModulesSet()) {
            result = result.add(getCreditsConcluded(iter, semester));
        }

        final CreditsLimit rule =
                (CreditsLimit) toInspect.getMostRecentActiveCurricularRule(CurricularRuleType.CREDITS_LIMIT, semester);
        if (rule == null) {
            return result;
        } else {
            return result.min(BigDecimal.valueOf(rule.getMaximumCredits()));
        }
    }

    static private BigDecimal getCreditsConcluded(final CurriculumModule toInspect, final ExecutionSemester semester) {

        BigDecimal result = BigDecimal.ZERO;

        if (toInspect.getClass().isAssignableFrom(CurriculumGroup.class)) {
            result = getCreditsConcluded((CurriculumGroup) toInspect, semester);

        } else if (toInspect.getClass().isAssignableFrom(Enrolment.class)) {
            result = getCreditsConcluded((Enrolment) toInspect, semester);

        } else if (toInspect.getClass().isAssignableFrom(Dismissal.class)) {
            result = getCreditsConcluded((Dismissal) toInspect, semester);
        }

        return result;
    }

    static private BigDecimal getCreditsConcluded(final Enrolment toInspect, final ExecutionSemester semester) {
        return semester == null || toInspect.getExecutionPeriod().isBeforeOrEquals(semester) ? BigDecimal
                .valueOf(toInspect.getAprovedEctsCredits()) : BigDecimal.ZERO;
    }

    static private BigDecimal getCreditsConcluded(final Dismissal toInspect, final ExecutionSemester semester) {
        return semester == null || toInspect.getExecutionPeriod() == null
                || toInspect.getExecutionPeriod().isBeforeOrEquals(semester) && !toInspect.getCredits().isTemporary() ? BigDecimal
                        .valueOf(toInspect.getEctsCredits()) : BigDecimal.ZERO;
    }

    static public BigDecimal getEnroledAndNotApprovedEctsCreditsFor(final CurriculumGroup toInspect,
            final ExecutionInterval interval) {
        if (interval instanceof ExecutionSemester) {
            return getEnroledAndNotApprovedEctsCreditsFor(toInspect,
                    ExecutionInterval.assertExecutionIntervalType(ExecutionSemester.class, interval));
        } else {
            final ExecutionYear year = ExecutionInterval.assertExecutionIntervalType(ExecutionYear.class, interval);
            return year.getExecutionPeriodsSet().stream().map(i -> getEnroledAndNotApprovedEctsCreditsFor(toInspect, i))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    static private BigDecimal getEnroledAndNotApprovedEctsCreditsFor(final CurriculumGroup toInspect,
            final ExecutionSemester semester) {

        BigDecimal result = BigDecimal.ZERO;
        for (final CurriculumModule iter : toInspect.getCurriculumModulesSet()) {
            result = result.add(getEnroledAndNotApprovedEctsCreditsFor(iter, semester));
        }
        return result;
    }

    static private BigDecimal getEnroledAndNotApprovedEctsCreditsFor(final CurriculumModule toInspect,
            final ExecutionSemester semester) {

        BigDecimal result = BigDecimal.ZERO;

        if (toInspect.getClass().isAssignableFrom(CurriculumGroup.class)) {
            result = getEnroledAndNotApprovedEctsCreditsFor((CurriculumGroup) toInspect, semester);

        } else if (toInspect.getClass().isAssignableFrom(Enrolment.class)) {
            result = getEnroledAndNotApprovedEctsCreditsFor((Enrolment) toInspect, semester);
        }

        return result;
    }

    static private BigDecimal getEnroledAndNotApprovedEctsCreditsFor(final Enrolment toInspect,
            final ExecutionSemester semester) {

        return (toInspect.isEnroled() || toInspect.isNotEvaluated() || toInspect.isFlunked())
                && toInspect.isValid(semester) ? toInspect.getEctsCreditsForCurriculum() : BigDecimal.ZERO;
    }

}
