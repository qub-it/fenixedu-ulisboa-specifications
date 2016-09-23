package org.fenixedu.ulisboa.specifications.dto.student;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.stream.Collectors;

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
import org.fenixedu.ulisboa.specifications.domain.services.CurriculumModuleServices;
import org.fenixedu.ulisboa.specifications.domain.services.RegistrationServices;
import org.fenixedu.ulisboa.specifications.domain.student.curriculum.CurriculumConfigurationInitializer.CurricularYearResult;
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
        SchoolClass schoolClass = getSchoolClass();
        if (schoolClass != null) {
            return schoolClass.getNome();
        }
        return null;
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
        if (ruleResult == null) {
            return "";
        }

        return ruleResult.getMessages().stream().map(i -> i.getMessage().replace("Aluno do ", ""))
                .collect(Collectors.joining(";    "));
    }

    public boolean getNotApproved() {
        final Registration registration = getRegistration();
        final ExecutionYear executionYear = getExecutionYear();
        return RegistrationServices.isFlunked(registration, executionYear);
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

        for (final StudentCurricularPlan iter : getRegistration().getStudentCurricularPlansSet()) {
            final RootCurriculumGroup curriculumGroup = iter.getRoot();
            result = result.add(CurriculumModuleServices.getEnroledAndNotApprovedEctsCreditsFor(curriculumGroup, input));
        }

        return result;
    }

    public String getRegimePresentation() {
        return getRegistration().getRegimeType(getExecutionYear()).getLocalizedName();
    }

    public String getLastRegistrationStatePresentation() {
        RegistrationStateType result = null;

        if (getRegistration().isConcluded()
                && getExecutionYear().isAfterOrEquals(new RegistrationConclusionBean(getRegistration()).getConclusionYear())) {
            // ATTENTION: conclusion state year != conclusion year
            result = RegistrationStateType.CONCLUDED;
        } else {
            final RegistrationState state = getRegistration().getLastRegistrationState(getExecutionYear());
            result = state == null ? null : state.getStateType();
        }

        return result == null ? null : result.getDescription();
    }

    public YearMonthDay getLastAcademicActDate() {
        final StudentCurricularPlan scp = getStudentCurricularPlan();
        return scp == null ? null : CurriculumModuleServices.calculateLastAcademicActDate(scp.getRoot(), getExecutionYear());
    }

}
