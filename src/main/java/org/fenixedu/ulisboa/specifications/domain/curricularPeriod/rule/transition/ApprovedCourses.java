package org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.transition;

import java.math.BigDecimal;
import java.util.function.Predicate;

import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.CurricularPeriodConfiguration;
import org.fenixedu.ulisboa.specifications.domain.services.CurricularPeriodServices;

import pt.ist.fenixframework.Atomic;

public class ApprovedCourses extends ApprovedCourses_Base {

    protected ApprovedCourses() {
        super();
    }

    public BigDecimal getApprovals() {
        return super.getValue();
    }

    @Atomic
    static public ApprovedCourses create(final CurricularPeriodConfiguration configuration, final BigDecimal approvals) {
        return create(configuration, approvals, false);
    }

    @Atomic
    static public ApprovedCourses createForSemester(final CurricularPeriodConfiguration configuration, final BigDecimal approvals,
            final Integer semester) {

        final ApprovedCourses result = new ApprovedCourses();
        result.init(configuration, approvals, null /*yearMin*/, null /*yearMax*/);
        result.setSemester(semester);

        return result;
    }

    @Atomic
    static public ApprovedCourses create(final CurricularPeriodConfiguration configuration, final BigDecimal approvals,
            final boolean allowToCollectAllCurricularPlans) {
        final ApprovedCourses result = new ApprovedCourses();
        result.init(configuration, approvals, null /*yearMin*/, null /*yearMax*/);
        result.setAllowToCollectAllCurricularPlans(allowToCollectAllCurricularPlans);

        return result;
    }

    @Override
    public String getLabel() {
        return BundleUtil.getString(MODULE_BUNDLE, "label." + this.getClass().getSimpleName(), getApprovals().toString());
    }

    @Override
    public RuleResult execute(final Curriculum input) {
        final Curriculum curriculum = prepareCurriculum(input);

        final Predicate<ICurriculumEntry> previousYears = i -> CurricularPeriodServices
                .getCurricularYear((CurriculumLine) i) < getConfiguration().getCurricularPeriod().getChildOrder().intValue();

        return BigDecimal.valueOf(curriculum.getCurricularYearEntries().stream().filter(previousYears).count())
                .compareTo(getApprovals()) >= 0 ? createTrue() : createFalseLabelled(getApprovals());
    }

}
