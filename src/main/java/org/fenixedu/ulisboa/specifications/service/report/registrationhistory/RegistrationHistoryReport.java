package org.fenixedu.ulisboa.specifications.service.report.registrationhistory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationDataByExecutionYear;
import org.fenixedu.academic.domain.student.RegistrationRegimeType;
import org.fenixedu.academic.domain.student.StatuteType;
import org.fenixedu.academic.domain.student.StudentDataShareAuthorization;
import org.fenixedu.academic.domain.student.curriculum.ICurriculum;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.academic.util.StudentPersonalDataAuthorizationChoice;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.joda.time.LocalDate;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class RegistrationHistoryReport {

    private ExecutionYear executionYear;

    private Registration registration;

    private Map<ProgramConclusion, RegistrationConclusionBean> conclusionReports = Maps.newHashMap();

    private ICurriculum curriculum;

    private int enrolmentsCount;

    private BigDecimal enrolmentsCredits;

    private int extraCurricularEnrolmentsCount;

    private BigDecimal extraCurricularEnrolmentsCredits;

    private int standaloneEnrolmentsCount;

    private BigDecimal standaloneEnrolmentsCredits;

    private BigDecimal executionYearSimpleAverage;

    private BigDecimal executionYearWeightedAverage;

    public RegistrationHistoryReport(final Registration registration, final ExecutionYear executionYear) {
        this.executionYear = executionYear;
        this.registration = registration;

        if (getStudentCurricularPlan() == null) {
            throw new ULisboaSpecificationsDomainException(
                    "error.RegistrationHistoryReport.found.registration.without.student.curricular.plan",
                    registration.getStudent().getNumber().toString(), registration.getDegree().getCode(),
                    getExecutionYear().getQualifiedName());
        }

    }

    public ExecutionYear getExecutionYear() {
        return executionYear;
    }

    public Registration getRegistration() {
        return registration;
    }

    public StudentCurricularPlan getStudentCurricularPlan() {
        return registration.getStudentCurricularPlansSet().size() == 1 ? registration
                .getLastStudentCurricularPlan() : registration.getStudentCurricularPlan(executionYear);
    }

    public DegreeCurricularPlan getDegreeCurricularPlan() {
        return getStudentCurricularPlan().getDegreeCurricularPlan();
    }

    public boolean isReingression() {
        return registration.hasReingression(executionYear);
    }

    public StudentPersonalDataAuthorizationChoice getStudentPersonalDataAuthorizationChoice() {
        final StudentDataShareAuthorization dataAuthorization = registration.getStudent()
                .getPersonalDataAuthorizationAt(executionYear.getEndLocalDate().toDateTimeAtCurrentTime());

        return dataAuthorization != null ? dataAuthorization.getAuthorizationChoice() : null;
    }

    public LocalDate getEnrolmentDate() {

        final Optional<RegistrationDataByExecutionYear> dataByYear = registration.getRegistrationDataByExecutionYearSet().stream()
                .filter(r -> r.getExecutionYear() == executionYear).findFirst();

        return dataByYear.isPresent() ? dataByYear.get().getEnrolmentDate() : null;
    }

    public String getPrimaryBranchName() {
        return getStudentCurricularPlan().getMajorBranchCurriculumGroups().stream().map(b -> b.getName().getContent())
                .collect(Collectors.joining(","));
    }

    public String getSecondaryBranchName() {
        return getStudentCurricularPlan().getMinorBranchCurriculumGroups().stream().map(b -> b.getName().getContent())
                .collect(Collectors.joining(","));
    }

    public Collection<StatuteType> getStatuteTypes() {
        final Set<StatuteType> result = Sets.newHashSet();
        result.addAll(registration.getStudentStatutesSet().stream().filter(s -> s.isValidOnAnyExecutionPeriodFor(executionYear))
                .map(s -> s.getType()).collect(Collectors.toSet()));
        result.addAll(registration.getStudent().getStudentStatutesSet().stream()
                .filter(s -> s.isValidOnAnyExecutionPeriodFor(executionYear)).map(s -> s.getType()).collect(Collectors.toSet()));

        return result;
    }

    public boolean hasEnrolmentsWithoutShifts() {

        for (final ExecutionCourse executionCourse : getRegistration().getAttendingExecutionCoursesFor(executionYear)) {
            if (!executionCourse.getAssociatedShifts().isEmpty() && registration.getShiftsFor(executionCourse).isEmpty()) {
                return true;
            }
        }

        return false;

    }

    public RegistrationState getLastRegistrationState() {
        return getRegistration().getLastRegistrationState(executionYear);
    }

    public boolean hasAnyInactiveRegistrationStateForYear() {
        return getRegistration()
                .getRegistrationStates(executionYear.getBeginLocalDate().toDateTimeAtStartOfDay(),
                        executionYear.getEndLocalDate().plusDays(1).toDateTimeAtStartOfDay().minusSeconds(1))
                .stream().anyMatch(s -> !s.isActive());
    }

    public void addConclusion(ProgramConclusion programConclusion, RegistrationConclusionBean bean) {
        conclusionReports.put(programConclusion, bean);
    }

    public void addEmptyConclusion(ProgramConclusion programConclusion) {
        conclusionReports.put(programConclusion, null);
    }

    public Set<ProgramConclusion> getProgramConclusions() {
        return conclusionReports.keySet();
    }

    public RegistrationConclusionBean getConclusionReportFor(ProgramConclusion programConclusion) {
        return conclusionReports.get(programConclusion);
    }

    public ICurriculum getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(ICurriculum curriculum) {
        this.curriculum = curriculum;
    }

    public Integer getCurricularYear() {
        return curriculum.getCurricularYear();
    }

    public BigDecimal getEctsCredits() {
        return curriculum.getSumEctsCredits();
    }

    public Grade getAverage() {
        return curriculum.getRawGrade();
    }

    public boolean hasDismissals() {
        return getStudentCurricularPlan().getCreditsSet().stream()
                .anyMatch(c -> c.getExecutionPeriod().getExecutionYear() == executionYear);
    }

    public boolean hasImprovementEvaluations() {

        final Predicate<Enrolment> improvementEvaluationForYear = e -> e.getEvaluationsSet().stream().anyMatch(
                ev -> ev.getEvaluationSeason().isImprovement() && ev.getExecutionPeriod().getExecutionYear() == executionYear);

        return getRegistration().getStudentCurricularPlansSet().stream()
                .anyMatch(scp -> scp.getEnrolmentsSet().stream().anyMatch(improvementEvaluationForYear));
    }

    public boolean hasAnnulledEnrolments() {
        return getStudentCurricularPlan().getEnrolmentsSet().stream().filter(e -> e.getExecutionYear() == executionYear)
                .anyMatch(e -> e.isAnnulled());
    }

    public int getEnrolmentsCount() {
        return enrolmentsCount;
    }

    public void setEnrolmentsCount(int enrolmentsCount) {
        this.enrolmentsCount = enrolmentsCount;
    }

    public BigDecimal getEnrolmentsCredits() {
        return enrolmentsCredits;
    }

    public void setEnrolmentsCredits(BigDecimal enrolmentsCredits) {
        this.enrolmentsCredits = enrolmentsCredits;
    }

    public int getExtraCurricularEnrolmentsCount() {
        return extraCurricularEnrolmentsCount;
    }

    public void setExtraCurricularEnrolmentsCount(int extraCurricularEnrolmentsCount) {
        this.extraCurricularEnrolmentsCount = extraCurricularEnrolmentsCount;
    }

    public BigDecimal getExtraCurricularEnrolmentsCredits() {
        return extraCurricularEnrolmentsCredits;
    }

    public void setExtraCurricularEnrolmentsCredits(BigDecimal extraCurricularEnrolmentsCredits) {
        this.extraCurricularEnrolmentsCredits = extraCurricularEnrolmentsCredits;
    }

    public int getStandaloneEnrolmentsCount() {
        return standaloneEnrolmentsCount;
    }

    public void setStandaloneEnrolmentsCount(int standaloneEnrolmentsCount) {
        this.standaloneEnrolmentsCount = standaloneEnrolmentsCount;
    }

    public BigDecimal getStandaloneEnrolmentsCredits() {
        return standaloneEnrolmentsCredits;
    }

    public void setStandaloneEnrolmentsCredits(BigDecimal standaloneEnrolmentsCredits) {
        this.standaloneEnrolmentsCredits = standaloneEnrolmentsCredits;
    }

    public BigDecimal getExecutionYearSimpleAverage() {
        return executionYearSimpleAverage;
    }

    public void setExecutionYearSimpleAverage(BigDecimal executionYearSimpleAverage) {
        this.executionYearSimpleAverage = executionYearSimpleAverage;
    }

    public BigDecimal getExecutionYearWeightedAverage() {
        return executionYearWeightedAverage;
    }

    public void setExecutionYearWeightedAverage(BigDecimal executionYearWeightedAverage) {
        this.executionYearWeightedAverage = executionYearWeightedAverage;
    }

    public RegistrationRegimeType getRegimeType() {
        return getRegistration().getRegimeType(executionYear);
    }

    public boolean isFirstTime() {
        return getRegistration().getRegistrationYear() == executionYear;
    }

}
