package org.fenixedu.ulisboa.specifications.service.report.registrationhistory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationDataByExecutionYear;
import org.fenixedu.academic.domain.student.RegistrationRegimeType;
import org.fenixedu.academic.domain.student.StudentDataShareAuthorization;
import org.fenixedu.academic.domain.student.StudentStatute;
import org.fenixedu.academic.domain.student.curriculum.ICurriculum;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.domain.treasury.ITreasuryBridgeAPI;
import org.fenixedu.academic.domain.treasury.ITuitionTreasuryEvent;
import org.fenixedu.academic.domain.treasury.TreasuryBridgeAPIFactory;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.academic.util.StudentPersonalDataAuthorizationChoice;
import org.fenixedu.ulisboa.specifications.domain.degree.prescription.PrescriptionConfig;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.services.RegistrationServices;
import org.fenixedu.ulisboa.specifications.domain.services.statute.StatuteServices;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class RegistrationHistoryReport {

    private Collection<Enrolment> enrolments = null;

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

    private BigDecimal currentAverage;

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

    public Collection<Enrolment> getEnrolments() {
        if (this.enrolments == null) {
            this.enrolments = Lists.newArrayList();

            final StudentCurricularPlan scp = getStudentCurricularPlan();
            if (scp != null) {
                scp.getEnrolmentsByExecutionYear(this.executionYear).stream().filter(e -> !e.isAnnulled())
                        .collect(Collectors.toCollection(() -> this.enrolments));
            }
        }

        return this.enrolments;
    }

    public StudentCurricularPlan getStudentCurricularPlan() {
        if (registration.getStudentCurricularPlansSet().size() == 1) {
            return registration.getLastStudentCurricularPlan();
        }

        StudentCurricularPlan studentCurricularPlan = registration.getStudentCurricularPlan(executionYear);

        if (studentCurricularPlan != null) {
            return studentCurricularPlan;
        }

        studentCurricularPlan = registration.getFirstStudentCurricularPlan();

        if (studentCurricularPlan.getStartExecutionYear().isAfterOrEquals(executionYear)) {
            return studentCurricularPlan;
        }

        return null;
    }

    public DegreeCurricularPlan getDegreeCurricularPlan() {
        return getStudentCurricularPlan().getDegreeCurricularPlan();
    }

    public boolean isReingression() {
        return registration.hasReingression(executionYear);
    }

    public boolean hasPreviousReingression() {
        return registration.getReingressions().stream().filter(ri -> ri.getExecutionYear().isBefore(executionYear)).count() > 0;
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

    public Collection<StudentStatute> getStudentStatutes() {
        final Set<StudentStatute> result = Sets.newHashSet();

        result.addAll(registration.getStudentStatutesSet().stream().filter(s -> s.isValidOnAnyExecutionPeriodFor(executionYear))
                .collect(Collectors.toSet()));
        result.addAll(registration.getStudent().getStudentStatutesSet().stream()
                .filter(s -> s.isValidOnAnyExecutionPeriodFor(executionYear)).collect(Collectors.toSet()));

        return result;
    }

    public String getStudentStatutesNames() {
        return getStudentStatutes().stream().map(s -> s.getType().getName().getContent()).collect(Collectors.joining(", "));
    }

    public String getStudentStatutesNamesAndDates() {
        return getStudentStatutes().stream().map(s -> {

            final String name = s.getType().getName().getContent();

            String dates = "";
            final ExecutionSemester beginSem = s.getBeginExecutionPeriod();
            if (beginSem != null) {

                final ExecutionSemester endSem = s.getEndExecutionPeriod();
                if (endSem == beginSem) {
                    dates = "S" + beginSem.getSemester();
                }

            } else {

                final LocalDate begin = s.getBeginDate();
                if (begin != null) {
                    dates = begin.toString(DateTimeFormat.forPattern("yyyy-MM-dd"));

                    final LocalDate end = s.getEndDate();
                    if (end != null) {
                        dates = dates + "<>" + end.toString(DateTimeFormat.forPattern("yyyy-MM-dd"));
                    }
                }
            }

            return name + (dates.isEmpty() ? "" : " [" + dates + "]");

        }).collect(Collectors.joining(", "));
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
        return RegistrationServices.getCurricularYear(registration, executionYear).getResult();
    }

    public Integer getPreviousYearCurricularYear() {

        if (registration.getStartExecutionYear().isAfterOrEquals(executionYear)
                || registration.getStudentCurricularPlan(executionYear.getPreviousExecutionYear()) == null
                || registration.getStudentCurricularPlan(executionYear) == null) {

            return null;
        }

        return RegistrationServices.getCurricularYear(registration, executionYear.getPreviousExecutionYear()).getResult();
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

    public Collection<EnrolmentEvaluation> getImprovementEvaluations() {
        return RegistrationServices.getImprovementEvaluations(getRegistration(), getExecutionYear(), ev -> !ev.isAnnuled());
    }

    public boolean hasImprovementEvaluations() {
        return RegistrationServices.hasImprovementEvaluations(getRegistration(), getExecutionYear(), ev -> !ev.isAnnuled());
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

    public BigDecimal getCurrentAverage() {
        return currentAverage;
    }

    public void setCurrentAverage(BigDecimal currentAverage) {
        this.currentAverage = currentAverage;
    }

    public RegistrationRegimeType getRegimeType() {
        return getRegistration().getRegimeType(executionYear);
    }

    public boolean isFirstTime() {
        return getRegistration().getRegistrationYear() == executionYear;
    }

    public boolean isTuitionCharged() {
        final ITreasuryBridgeAPI treasuryBridgeAPI = TreasuryBridgeAPIFactory.implementation();
        if (treasuryBridgeAPI == null) {
            return false;
        }

        final ITuitionTreasuryEvent event = treasuryBridgeAPI.getTuitionForRegistrationTreasuryEvent(registration, executionYear);

        return event != null && event.isCharged();
    }

    public BigDecimal getTuitionAmount() {
        final ITreasuryBridgeAPI treasuryBridgeAPI = TreasuryBridgeAPIFactory.implementation();
        if (treasuryBridgeAPI == null) {
            return BigDecimal.ZERO;
        }

        final ITuitionTreasuryEvent event = treasuryBridgeAPI.getTuitionForRegistrationTreasuryEvent(registration, executionYear);

        if (event == null) {
            return BigDecimal.ZERO;
        }

        return event.getAmountToPay();
    }

    public Integer getEnrolmentYears() {
        return getEnrolmentExecutionYears().size();
    }

    public BigDecimal getEnrolmentYearsForPrescription() {
        BigDecimal result = new BigDecimal(getEnrolmentYears());

        final PrescriptionConfig config = PrescriptionConfig.findBy(getDegreeCurricularPlan());
        if (config == null) {
            return null;
        }

        BigDecimal bonification = BigDecimal.ZERO;
        for (final ExecutionYear executionYear : getEnrolmentExecutionYears()) {
            bonification =
                    bonification.add(config.getBonification(StatuteServices.findStatuteTypes(getRegistration(), executionYear),
                            getRegistration().isPartialRegime(executionYear)));
        }

        return BigDecimal.ZERO.max(result.subtract(bonification));

    }

    private Set<ExecutionYear> getEnrolmentExecutionYears() {
        return RegistrationServices.getEnrolmentYears(registration).stream().filter(ey -> ey.isBeforeOrEquals(executionYear))
                .collect(Collectors.toSet());
    }

    public String getOtherConcludedRegistrationYears() {

        final StringBuilder result = new StringBuilder();

        registration.getStudent().getRegistrationsSet().stream()

                .filter(r -> r != registration && r.isConcluded() && r.getLastStudentCurricularPlan() != null)

                .forEach(r -> {

                    final SortedSet<ExecutionYear> executionYears =
                            Sets.newTreeSet(ExecutionYear.COMPARATOR_BY_BEGIN_DATE.reversed());
                    executionYears.addAll(RegistrationServices.getEnrolmentYearsWithDismissals(r));

                    if (!executionYears.isEmpty()) {
                        result.append(executionYears.first().getQualifiedName()).append("|");
                    }

                });

        return result.toString().endsWith("|") ? result.delete(result.length() - 1, result.length()).toString() : result
                .toString();
    }

}
