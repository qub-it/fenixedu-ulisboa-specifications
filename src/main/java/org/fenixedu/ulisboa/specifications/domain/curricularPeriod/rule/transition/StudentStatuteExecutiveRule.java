package org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.transition;

import java.math.BigDecimal;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.StatuteType;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.CurricularPeriodConfiguration;

import pt.ist.fenixframework.Atomic;

public class StudentStatuteExecutiveRule extends StudentStatuteExecutiveRule_Base {

    protected StudentStatuteExecutiveRule() {
        super();
    }

    @Override
    public boolean isExecutive() {
        return true;
    }

    @Atomic
    static public StudentStatuteExecutiveRule create(final CurricularPeriodConfiguration configuration,
            final StatuteType statuteType) {

        final StudentStatuteExecutiveRule result = new StudentStatuteExecutiveRule();
        result.init(configuration, statuteType);
        return result;
    }

    protected void init(final CurricularPeriodConfiguration configuration, final StatuteType statuteType) {
        super.init(
                configuration,
                (BigDecimal) BigDecimal.ZERO/* credits; 0 hack, this rule does not need ects, but its a superclass requirement  */,
                (Integer) null /* yearMin */, (Integer) null /* yearMax */);
        super.setStatuteType(statuteType);
        checkRules();
    }

    private void checkRules() {
        //
        //CHANGE_ME add more busines validations
        //
        if (getStatuteType() == null) {
            throw new DomainException("error." + this.getClass().getSimpleName() + ".statuteType.required");
        }
    }

    @Override
    public String getLabel() {
        return BundleUtil.getString(MODULE_BUNDLE, "label." + this.getClass().getSimpleName(), getStatuteType().getName()
                .getContent());
    }

    @Override
    public RuleResult execute(final Curriculum curriculum) {
        final Student student = curriculum.getStudentCurricularPlan().getRegistration().getStudent();
        final ExecutionYear executionYear =
                curriculum.getExecutionYear() == null ? ExecutionYear.readCurrentExecutionYear() : curriculum.getExecutionYear();

        for (final StatuteType iter : student.getStatutesTypesValidOnAnyExecutionSemesterFor(executionYear)) {
            if (getStatuteType() == iter) {
                return createTrue();
            }
        }

        return createFalseLabelled(getMessagesSuffix(executionYear));
    }

    @Atomic
    @Override
    public void delete() {

        super.setStatuteType(null);

        super.delete();
    }

    static private String getMessagesSuffix(final ExecutionYear executionYear) {
        return executionYear == null ? "" : BundleUtil.getString(MODULE_BUNDLE,
                "label." + StudentStatuteExecutiveRule.class.getSimpleName() + ".suffix", executionYear.toString());
    }

}
