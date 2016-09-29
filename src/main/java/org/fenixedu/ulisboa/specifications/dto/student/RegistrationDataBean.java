package org.fenixedu.ulisboa.specifications.dto.student;

import java.io.Serializable;
import java.math.BigDecimal;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationDataByExecutionYear;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationStateType;
import org.fenixedu.academic.domain.studentCurriculum.RootCurriculumGroup;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.ulisboa.specifications.domain.curricularPeriod.rule.CurricularPeriodRule;
import org.fenixedu.ulisboa.specifications.domain.services.CurriculumModuleServices;
import org.fenixedu.ulisboa.specifications.domain.services.RegistrationServices;
import org.joda.time.LocalDate;
import org.joda.time.YearMonthDay;

@SuppressWarnings("serial")
public class RegistrationDataBean implements Serializable {

    private RegistrationDataByExecutionYear data;

    public RegistrationDataBean(final RegistrationDataByExecutionYear data) {
        this.setData(data);
    }

    public RegistrationDataByExecutionYear getData() {
        return data;
    }

    public void setData(RegistrationDataByExecutionYear data) {
        this.data = data;
    }

    public String getExternalId() {
        return getData() == null ? null : getData().getExternalId();
    }

    public Registration getRegistration() {
        return getData() == null ? null : getData().getRegistration();
    }

    public ExecutionYear getExecutionYear() {
        return getData() == null ? null : getData().getExecutionYear();
    }

    public LocalDate getEnrolmentDate() {
        return getData() == null ? null : getData().getEnrolmentDate();
    }

    public boolean isReingression() {
        return getData() == null ? null : getData().getReingression();
    }

    public StudentCurricularPlan getStudentCurricularPlan() {
        final Registration registration = getRegistration();
        return registration == null ? null : registration.getStudentCurricularPlan(getExecutionYear());
    }

    private SchoolClass getSchoolClass() {
        final ExecutionSemester executionSemester = getExecutionYear().isCurrent() ? ExecutionSemester
                .readActualExecutionSemester() : getExecutionYear().getFirstExecutionPeriod();

        return RegistrationServices.getSchoolClassBy(getRegistration(), executionSemester).orElse(null);
    }

    public String getSchoolClassPresentation() {
        final String result = "";

        SchoolClass schoolClass = getSchoolClass();
        if (schoolClass != null && schoolClass.getEditablePartOfName() != null) {
            return schoolClass.getEditablePartOfName().toString();
        }

        return result;
    }

    public String getCurricularYearPresentation() {
        final Registration registration = getRegistration();
        final ExecutionYear executionYear = getExecutionYear();
        final int curricularYear = RegistrationServices.getCurricularYear(registration, executionYear).getResult();

        return String.valueOf(curricularYear);
    }

    public String getCurricularYearJustificationPresentation() {
        final Registration registration = getRegistration();
        final ExecutionYear executionYear = getExecutionYear();
        final RuleResult ruleResult = RegistrationServices.getCurricularYear(registration, executionYear).getJustification();

        return CurricularPeriodRule.getMessages(ruleResult).replace("Aluno do", "Falhou");
    }

    public boolean getNotApproved() {
        final Registration registration = getRegistration();
        final ExecutionYear executionYear = getExecutionYear();
        return RegistrationServices.isFlunkedUsingCurricularYear(registration, executionYear);
    }

    public Integer getEnrolmentsCount() {
        return getRegistration().getEnrolments(getExecutionYear()).size();
    }

    public Double getCreditsConcluded() {
        return Math.max(0,
                getCreditsConcluded(getExecutionYear()) - getCreditsConcluded(getExecutionYear().getPreviousExecutionYear()));
    }

    private Double getCreditsConcluded(final ExecutionYear input) {
        Double result = 0d;

        for (final StudentCurricularPlan iter : getRegistration().getStudentCurricularPlansSet()) {
            final RootCurriculumGroup curriculumGroup = iter.getRoot();
            result += curriculumGroup.getCreditsConcluded(input);
        }

        return result;
    }

    public BigDecimal getEnroledEcts() {
        return getEnroledEcts(getExecutionYear());
    }

    private BigDecimal getEnroledEcts(final ExecutionYear input) {
        BigDecimal result = BigDecimal.ZERO;

        if (RegistrationServices.isCurriculumAccumulated(getRegistration())) {
            for (final StudentCurricularPlan iter : getRegistration().getStudentCurricularPlansSet()) {
                final RootCurriculumGroup curriculumGroup = iter.getRoot();
                result = result.add(CurriculumModuleServices.getEnroledAndNotApprovedEctsCreditsFor(curriculumGroup, input));
            }

        } else {
            final StudentCurricularPlan plan = getStudentCurricularPlan();
            final RootCurriculumGroup curriculumGroup = plan.getRoot();
            result = result.add(CurriculumModuleServices.getEnroledAndNotApprovedEctsCreditsFor(curriculumGroup, input));
        }

        return result;
    }

    public String getRegimePresentation() {
        return getRegistration().getRegimeType(getExecutionYear()).getLocalizedName();
    }

    public String getLastRegistrationStatePresentation() {
        RegistrationStateType result = null;

        final Registration registration = getRegistration();
        if (registration.isConcluded() && getExecutionYear().isAfterOrEquals(
                new RegistrationConclusionBean(registration, registration.getLastStudentCurricularPlan().getRoot())
                        .getConclusionYear())) {
            // ATTENTION: conclusion state year != conclusion year
            result = RegistrationStateType.CONCLUDED;
        } else {
            final RegistrationState state = registration.getLastRegistrationState(getExecutionYear());
            result = state == null ? null : state.getStateType();
        }

        return result == null ? null : result.getDescription();
    }

    public YearMonthDay getLastAcademicActDate() {
        final StudentCurricularPlan scp = getStudentCurricularPlan();
        return scp == null ? null : CurriculumModuleServices.calculateLastAcademicActDate(scp.getRoot(), getExecutionYear());
    }

}
