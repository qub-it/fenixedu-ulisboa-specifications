package org.fenixedu.ulisboa.specifications.service.report.registrationhistory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.contacts.PhysicalAddress;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.person.Gender;
import org.fenixedu.academic.domain.person.IDDocumentType;
import org.fenixedu.academic.domain.student.PrecedentDegreeInformation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationDataByExecutionYear;
import org.fenixedu.academic.domain.student.RegistrationProtocol;
import org.fenixedu.academic.domain.student.RegistrationRegimeType;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.domain.student.StudentDataShareAuthorization;
import org.fenixedu.academic.domain.student.StudentStatute;
import org.fenixedu.academic.domain.student.curriculum.ICurriculum;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.domain.treasury.ITreasuryBridgeAPI;
import org.fenixedu.academic.domain.treasury.ITuitionTreasuryEvent;
import org.fenixedu.academic.domain.treasury.TreasuryBridgeAPIFactory;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.academic.dto.student.RegistrationStateBean;
import org.fenixedu.academic.util.StudentPersonalDataAuthorizationChoice;
import org.fenixedu.academictreasury.domain.customer.PersonCustomer;
import org.fenixedu.ulisboa.specifications.domain.degree.prescription.PrescriptionConfig;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.services.RegistrationServices;
import org.fenixedu.ulisboa.specifications.domain.services.statute.StatuteServices;
import org.fenixedu.ulisboa.specifications.domain.student.curriculum.CurriculumConfigurationInitializer.CurricularYearResult;
import org.joda.time.LocalDate;
import org.joda.time.YearMonthDay;
import org.joda.time.format.DateTimeFormat;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class RegistrationHistoryReport implements Comparable<RegistrationHistoryReport> {

    private Collection<Enrolment> enrolments;

    private ExecutionYear executionYear;

    private Registration registration;

    private Set<ProgramConclusion> programConclusionsToReport = Sets.newHashSet();

    private Map<ProgramConclusion, RegistrationConclusionBean> conclusionReports = Maps.newHashMap();

    private Integer curricularYear;

    private Integer previousYearCurricularYear;

    private Integer nextYearCurricularYear;

    private Integer enrolmentsCount;

    private RegistrationStateBean lastRegistrationState;

    private BigDecimal enrolmentsCredits;

    private Integer extraCurricularEnrolmentsCount;

    private BigDecimal extraCurricularEnrolmentsCredits;

    private Integer standaloneEnrolmentsCount;

    private BigDecimal standaloneEnrolmentsCredits;

    private BigDecimal executionYearSimpleAverage;

    private BigDecimal executionYearWeightedAverage;

    private Boolean executionYearEnroledMandatoryFlunked;

    private Boolean executionYearEnroledMandatoryInAdvance;

    private BigDecimal executionYearCreditsMandatoryEnroled;

    private BigDecimal executionYearCreditsMandatoryApproved;

    private LocalDate executionYearConclusionDate;

    private BigDecimal currentAverage;

    public RegistrationHistoryReport(final Registration registration, final ExecutionYear executionYear) {
        this.executionYear = executionYear;
        this.registration = registration;

        if (getStudentCurricularPlan() == null) {

            throw new ULisboaSpecificationsDomainException(
                    "error.RegistrationHistoryReport.found.registration.without.student.curricular.plan",
                    getStudent().getNumber().toString(), getDegree().getCode(), executionYear.getQualifiedName());
        }
    }

    @Override
    public int compareTo(final RegistrationHistoryReport o) {

        final Comparator<RegistrationHistoryReport> byYear =
                (x, y) -> ExecutionYear.COMPARATOR_BY_BEGIN_DATE.compare(x.getExecutionYear(), y.getExecutionYear());
        final Comparator<RegistrationHistoryReport> byDegreeType = (x, y) -> x.getDegreeType().compareTo(y.getDegreeType());
        final Comparator<RegistrationHistoryReport> byDegree =
                (x, y) -> Degree.COMPARATOR_BY_NAME.compare(x.getRegistration().getDegree(), y.getRegistration().getDegree());
        final Comparator<RegistrationHistoryReport> byDegreeCurricularPlan =
                (x, y) -> x.getStudentCurricularPlanName().compareTo(y.getStudentCurricularPlanName());
        final Comparator<RegistrationHistoryReport> byRegistrationNumber =
                (x, y) -> x.getRegistrationNumber().compareTo(y.getRegistrationNumber());

        return byYear.thenComparing(byDegreeType).thenComparing(byDegree).thenComparing(byDegreeCurricularPlan)
                .thenComparing(byRegistrationNumber).compare(this, o);
    }

    public ExecutionYear getExecutionYear() {
        return this.executionYear;
    }

    public Registration getRegistration() {
        return registration;
    }

    protected Degree getDegree() {
        final Registration registration = getRegistration();
        return registration == null ? null : registration.getDegree();
    }

    private Student getStudent() {
        final Registration registration = getRegistration();
        return registration == null ? null : registration.getStudent();
    }

    private Person getPerson() {
        final Registration registration = getRegistration();
        return registration == null ? null : registration.getPerson();
    }

    public Collection<Enrolment> getEnrolments() {
        if (this.enrolments == null) {
            this.enrolments = Lists.newArrayList();

            final StudentCurricularPlan scp = getStudentCurricularPlan();
            if (scp != null) {
                scp.getEnrolmentsByExecutionYear(getExecutionYear()).stream().filter(e -> !e.isAnnulled())
                        .collect(Collectors.toCollection(() -> this.enrolments));
            }
        }

        return this.enrolments;
    }

    public StudentCurricularPlan getStudentCurricularPlan() {
        if (registration.getStudentCurricularPlansSet().size() == 1) {
            return registration.getLastStudentCurricularPlan();
        }

        StudentCurricularPlan studentCurricularPlan = registration.getStudentCurricularPlan(getExecutionYear());

        if (studentCurricularPlan != null) {
            return studentCurricularPlan;
        }

        studentCurricularPlan = registration.getFirstStudentCurricularPlan();

        if (studentCurricularPlan.getStartExecutionYear().isAfterOrEquals(getExecutionYear())) {
            return studentCurricularPlan;
        }

        return null;
    }

    private DegreeCurricularPlan getDegreeCurricularPlan() {
        final StudentCurricularPlan scp = getStudentCurricularPlan();
        return scp == null ? null : scp.getDegreeCurricularPlan();
    }

    public boolean isReingression() {
        return registration.hasReingression(getExecutionYear());
    }

    public boolean hasPreviousReingression() {
        return registration.getReingressions().stream().filter(ri -> ri.getExecutionYear().isBefore(getExecutionYear()))
                .count() > 0;
    }

    public String getStudentPersonalDataAuthorizationChoice() {
        final ExecutionYear executionYear = getExecutionYear();
        final Student student = getStudent();
        final StudentDataShareAuthorization auth = student == null || executionYear == null ? null : student
                .getPersonalDataAuthorizationAt(executionYear.getEndLocalDate().toDateTimeAtCurrentTime());
        final StudentPersonalDataAuthorizationChoice choice = auth == null ? null : auth.getAuthorizationChoice();

        return choice == null ? null : choice.getDescription();
    }

    public LocalDate getEnrolmentDate() {

        final Optional<RegistrationDataByExecutionYear> dataByYear = registration.getRegistrationDataByExecutionYearSet().stream()
                .filter(r -> r.getExecutionYear() == getExecutionYear()).findFirst();

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

        result.addAll(registration.getStudentStatutesSet().stream()
                .filter(s -> s.isValidOnAnyExecutionPeriodFor(getExecutionYear())).collect(Collectors.toSet()));
        result.addAll(getStudent().getStudentStatutesSet().stream()
                .filter(s -> s.isValidOnAnyExecutionPeriodFor(getExecutionYear())).collect(Collectors.toSet()));

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

        for (final ExecutionCourse executionCourse : getRegistration().getAttendingExecutionCoursesFor(getExecutionYear())) {
            if (!executionCourse.getAssociatedShifts().isEmpty() && registration.getShiftsFor(executionCourse).isEmpty()) {
                return true;
            }
        }

        return false;

    }

    public LocalDate getFirstRegistrationStateDate() {
        final Registration registration = getRegistration();
        final RegistrationState state = registration == null ? null : registration.getFirstRegistrationState();
        return state == null ? null : state.getStateDate().toLocalDate();
    }

    public RegistrationStateBean getLastRegistrationState() {
        if (lastRegistrationState == null) {
            lastRegistrationState = RegistrationServices.getLastRegistrationState(getRegistration(), getExecutionYear());
        }

        return lastRegistrationState;
    }

    public String getLastRegistrationStateType() {
        final RegistrationStateBean state = getLastRegistrationState();
        return state == null ? null : state.getStateType().getDescription();
    }

    public LocalDate getLastRegistrationStateDate() {
        final RegistrationStateBean state = getLastRegistrationState();
        return state == null ? null : state.getStateDate().toLocalDate();
    }

    public boolean hasAnyInactiveRegistrationStateForYear() {
        return getRegistration()
                .getRegistrationStates(getExecutionYear().getBeginLocalDate().toDateTimeAtStartOfDay(),
                        getExecutionYear().getEndLocalDate().plusDays(1).toDateTimeAtStartOfDay().minusSeconds(1))
                .stream().anyMatch(s -> !s.isActive());
    }

    protected void addConclusion(ProgramConclusion programConclusion, RegistrationConclusionBean bean) {
        this.conclusionReports.put(programConclusion, bean);
    }

    protected void addEmptyConclusion(ProgramConclusion programConclusion) {
        this.conclusionReports.put(programConclusion, null);
    }

    public List<ProgramConclusion> getProgramConclusions() {
        return getConclusionReports()
                .keySet().stream().sorted(Comparator.comparing(ProgramConclusion::getName)
                        .thenComparing(ProgramConclusion::getDescription).thenComparing(ProgramConclusion::getExternalId))
                .collect(Collectors.toList());
    }

    public RegistrationConclusionBean getConclusionReportFor(ProgramConclusion programConclusion) {
        return getConclusionReports().get(programConclusion);
    }

    private Map<ProgramConclusion, RegistrationConclusionBean> getConclusionReports() {
        if (conclusionReports.isEmpty()) {
            RegistrationHistoryReportService.addConclusion(this);
        }

        return conclusionReports;
    }

    protected Set<ProgramConclusion> getProgramConclusionsToReport() {
        return this.programConclusionsToReport;
    }

    protected void setProgramConclusionsToReport(final Set<ProgramConclusion> input) {
        this.programConclusionsToReport = input;
    }

    private ICurriculum getCurriculum() {
        return RegistrationServices.getCurriculum(getRegistration(), getExecutionYear());
    }

    public Integer getCurricularYear() {
        if (this.curricularYear == null) {
            final CurricularYearResult result = RegistrationServices.getCurricularYear(getRegistration(), getExecutionYear());
            this.curricularYear = result == null ? null : result.getResult();
        }

        return this.curricularYear;
    }

    public Integer getPreviousYearCurricularYear() {

        final ExecutionYear previous = getExecutionYear().getPreviousExecutionYear();
        if (registration.getStartExecutionYear().isAfterOrEquals(getExecutionYear())
                || registration.getStudentCurricularPlan(previous) == null
                || registration.getStudentCurricularPlan(getExecutionYear()) == null) {

            return null;
        }

        if (this.previousYearCurricularYear == null) {
            final CurricularYearResult result = RegistrationServices.getCurricularYear(getRegistration(), previous);
            this.previousYearCurricularYear = result == null ? null : result.getResult();
        }

        return this.previousYearCurricularYear;
    }

    public Integer getNextYearCurricularYear() {
        if (this.nextYearCurricularYear == null) {
            final ExecutionYear next = getExecutionYear().getNextExecutionYear();
            final CurricularYearResult result = RegistrationServices.getCurricularYear(getRegistration(), next);
            this.nextYearCurricularYear = result == null ? null : result.getResult();
        }

        return this.nextYearCurricularYear;
    }

    public BigDecimal getEctsCredits() {
        return getCurriculum().getSumEctsCredits();
    }

    public String getAverage() {
        final ICurriculum curriculum = getCurriculum();
        final Grade rawGrade = curriculum == null ? null : curriculum.getRawGrade();
        return rawGrade == null ? null : rawGrade.getValue();
    }

    public boolean hasDismissals() {
        return getStudentCurricularPlan().getCreditsSet().stream()
                .anyMatch(c -> c.getExecutionPeriod().getExecutionYear() == getExecutionYear());
    }

    public Collection<EnrolmentEvaluation> getImprovementEvaluations() {
        return RegistrationServices.getImprovementEvaluations(getRegistration(), getExecutionYear(), ev -> !ev.isAnnuled());
    }

    public boolean hasImprovementEvaluations() {
        return RegistrationServices.hasImprovementEvaluations(getRegistration(), getExecutionYear(), ev -> !ev.isAnnuled());
    }

    public boolean hasAnnulledEnrolments() {
        return getStudentCurricularPlan().getEnrolmentsSet().stream().filter(e -> e.getExecutionYear() == getExecutionYear())
                .anyMatch(e -> e.isAnnulled());
    }

    public int getEnrolmentsCount() {
        if (this.enrolmentsCount == null) {
            RegistrationHistoryReportService.addEnrolmentsAndCreditsCount(this);
        }

        return this.enrolmentsCount;
    }

    protected void setEnrolmentsCount(final int input) {
        this.enrolmentsCount = input;
    }

    public BigDecimal getEnrolmentsCredits() {
        if (this.enrolmentsCredits == null) {
            RegistrationHistoryReportService.addEnrolmentsAndCreditsCount(this);
        }

        return this.enrolmentsCredits;
    }

    protected void setEnrolmentsCredits(final BigDecimal input) {
        this.enrolmentsCredits = input;
    }

    public int getExtraCurricularEnrolmentsCount() {
        if (this.extraCurricularEnrolmentsCount == null) {
            RegistrationHistoryReportService.addEnrolmentsAndCreditsCount(this);
        }

        return this.extraCurricularEnrolmentsCount;
    }

    protected void setExtraCurricularEnrolmentsCount(final int input) {
        this.extraCurricularEnrolmentsCount = input;
    }

    public BigDecimal getExtraCurricularEnrolmentsCredits() {
        if (this.extraCurricularEnrolmentsCredits == null) {
            RegistrationHistoryReportService.addEnrolmentsAndCreditsCount(this);
        }

        return this.extraCurricularEnrolmentsCredits;
    }

    protected void setExtraCurricularEnrolmentsCredits(final BigDecimal input) {
        this.extraCurricularEnrolmentsCredits = input;
    }

    public int getStandaloneEnrolmentsCount() {
        if (this.standaloneEnrolmentsCount == null) {
            RegistrationHistoryReportService.addEnrolmentsAndCreditsCount(this);
        }

        return this.standaloneEnrolmentsCount;
    }

    protected void setStandaloneEnrolmentsCount(final int input) {
        this.standaloneEnrolmentsCount = input;
    }

    public BigDecimal getStandaloneEnrolmentsCredits() {
        if (this.standaloneEnrolmentsCredits == null) {
            RegistrationHistoryReportService.addEnrolmentsAndCreditsCount(this);
        }

        return this.standaloneEnrolmentsCredits;
    }

    protected void setStandaloneEnrolmentsCredits(final BigDecimal input) {
        this.standaloneEnrolmentsCredits = input;
    }

    public BigDecimal getExecutionYearSimpleAverage() {
        if (this.executionYearSimpleAverage == null) {
            this.executionYearSimpleAverage = RegistrationHistoryReportService.calculateExecutionYearSimpleAverage(this);
        }

        return this.executionYearSimpleAverage;
    }

    public BigDecimal getExecutionYearWeightedAverage() {
        if (this.executionYearWeightedAverage == null) {
            this.executionYearWeightedAverage = RegistrationHistoryReportService.calculateExecutionYearWeightedAverage(this);
        }

        return this.executionYearWeightedAverage;
    }

    public Boolean getExecutionYearEnroledMandatoryFlunked() {
        if (this.executionYearEnroledMandatoryFlunked == null) {
            RegistrationHistoryReportService.addExecutionYearMandatoryCoursesData(this);
        }

        return this.executionYearEnroledMandatoryFlunked;
    }

    protected void setExecutionYearEnroledMandatoryFlunked(final boolean input) {
        this.executionYearEnroledMandatoryFlunked = input;
    }

    public Boolean getExecutionYearEnroledMandatoryInAdvance() {
        if (this.executionYearEnroledMandatoryInAdvance == null) {
            RegistrationHistoryReportService.addExecutionYearMandatoryCoursesData(this);
        }

        return this.executionYearEnroledMandatoryInAdvance;
    }

    protected void setExecutionYearEnroledMandatoryInAdvance(final boolean input) {
        this.executionYearEnroledMandatoryInAdvance = input;
    }

    public BigDecimal getExecutionYearCreditsMandatoryEnroled() {
        if (this.executionYearCreditsMandatoryEnroled == null) {
            RegistrationHistoryReportService.addExecutionYearMandatoryCoursesData(this);
        }

        return this.executionYearCreditsMandatoryEnroled;
    }

    protected void setExecutionYearCreditsMandatoryEnroled(final BigDecimal input) {
        this.executionYearCreditsMandatoryEnroled = input;
    }

    public BigDecimal getExecutionYearCreditsMandatoryApproved() {
        if (this.executionYearCreditsMandatoryApproved == null) {
            RegistrationHistoryReportService.addExecutionYearMandatoryCoursesData(this);
        }

        return this.executionYearCreditsMandatoryApproved;
    }

    protected void setExecutionYearCreditsMandatoryApproved(final BigDecimal input) {
        this.executionYearCreditsMandatoryApproved = input;
    }

    public LocalDate getExecutionYearConclusionDate() {
        if (this.executionYearConclusionDate == null) {
            // WARNING: is approved should be enough, but unfortunately there are some cases (like in FM) where an enrolment only has evaluations with improvements
            final Enrolment enrolment =
                    getEnrolments().stream().filter(i -> i.isApproved() && i.calculateConclusionDate() != null)
                            .max((x, y) -> x.calculateConclusionDate().compareTo(y.calculateConclusionDate())).orElse(null);

            final YearMonthDay date = enrolment == null ? null : enrolment.calculateConclusionDate();
            this.executionYearConclusionDate = date == null ? null : new LocalDate(date);
        }

        return this.executionYearConclusionDate;
    }

    public String getCurrentAverage() {
        if (currentAverage == null) {
            currentAverage = RegistrationHistoryReportService.calculateCurrentAverage(getRegistration());
        }

        return currentAverage.toPlainString();
    }

    protected RegistrationRegimeType getRegimeType() {
        final Registration registration = getRegistration();
        return registration == null ? null : registration.getRegimeType(getExecutionYear());
    }

    public String getRegimeTypeName() {
        final RegistrationRegimeType regimeType = getRegimeType();
        return regimeType == null ? null : regimeType.getLocalizedName();
    }

    public boolean isFirstTime() {
        final Registration registration = getRegistration();
        return registration == null ? false : registration.getRegistrationYear() == getExecutionYear();
    }

    public String getStudentNumber() {
        final Student student = getStudent();
        return student == null ? null : student.getNumber().toString();
    }

    public String getRegistrationNumber() {
        final Registration registration = getRegistration();
        return registration == null ? null : registration.getNumber().toString();
    }

    public String getDegreeCode() {
        String result = null;

        final Degree degree = getDegree();
        final String ministryCode = degree == null ? null : degree.getMinistryCode();
        final String code = degree == null ? null : degree.getCode();

        if (ministryCode != null) {
            result = ministryCode;
        }

        if (code != null && !code.equals(ministryCode)) {
            result = result.isEmpty() ? code : (result + " [" + code + "]");
        }

        return result;
    }

    protected DegreeType getDegreeType() {
        final Degree degree = getDegree();
        return degree == null ? null : degree.getDegreeType();
    }

    public String getDegreeTypeName() {
        final DegreeType degreeType = getDegreeType();
        return degreeType == null ? null : degreeType.getName().getContent();
    }

    public String getDegreePresentationName() {
        final Degree degree = getDegree();
        return degree == null ? null : degree.getPresentationNameI18N().getContent();
    }

    protected IngressionType getIngressionType() {
        final Registration registration = getRegistration();
        return registration == null ? null : registration.getIngressionType();
    }

    public String getIngressionTypeDescription() {
        final IngressionType type = getIngressionType();
        return type == null ? null : type.getDescription().getContent();
    }

    protected RegistrationProtocol getRegistrationProtocol() {
        final Registration registration = getRegistration();
        return registration == null ? null : registration.getRegistrationProtocol();
    }

    public String getRegistrationProtocolDescription() {
        final RegistrationProtocol protocol = getRegistrationProtocol();
        return protocol == null ? null : protocol.getDescription().getContent();
    }

    public LocalDate getStartDate() {
        final Registration registration = getRegistration();
        return registration == null ? null : registration.getStartDate().toLocalDate();
    }

    public String getRegistrationYear() {
        final Registration registration = getRegistration();
        final ExecutionYear year = registration.getRegistrationYear();
        return year == null ? null : year.getQualifiedName();
    }

    public String getStudentCurricularPlanName() {
        final StudentCurricularPlan scp = getStudentCurricularPlan();
        return scp == null ? null : scp.getName();
    }

    public Integer getStudentCurricularPlanCount() {
        final Registration registration = getRegistration();
        return registration == null ? null : registration.getStudentCurricularPlansSet().size();
    }

    public String getLastEnrolmentExecutionYear() {
        final Registration registration = getRegistration();
        final ExecutionYear year = registration == null ? null : registration.getLastEnrolmentExecutionYear();
        return year == null ? null : year.getQualifiedName();
    }

    public String getRegistrationObservations() {
        final Registration registration = getRegistration();
        return registration == null ? null : registration.getRegistrationObservationsSet().stream()
                .map(o -> o.getVersioningUpdatedBy().getUsername() + ":" + o.getValue())
                .collect(Collectors.joining(" \n --------------\n "));
    }

    private PrecedentDegreeInformation getPrecedentInformation() {
        final Registration registration = getRegistration();
        final StudentCandidacy candidacy = registration == null ? null : registration.getStudentCandidacy();
        return candidacy == null ? null : candidacy.getPrecedentDegreeInformation();
    }

    public String getQualificationInstitutionName() {
        final PrecedentDegreeInformation info = getPrecedentInformation();
        return info == null ? null : info.getInstitutionName();
    }

    public String getQualificationSchoolLevel() {
        final PrecedentDegreeInformation info = getPrecedentInformation();
        final SchoolLevelType schoolLevel = info.getSchoolLevel();
        return schoolLevel == null ? null : schoolLevel.getLocalizedName();
    }

    public String getQualificationDegreeDesignation() {
        final PrecedentDegreeInformation info = getPrecedentInformation();
        return info == null ? null : info.getDegreeDesignation();
    }

    public String getOriginInstitutionName() {
        final PrecedentDegreeInformation info = getPrecedentInformation();
        final Unit precedentInstitution = info.getPrecedentInstitution();
        return precedentInstitution == null ? null : precedentInstitution.getName();
    }

    public String getOriginSchoolLevel() {
        final PrecedentDegreeInformation info = getPrecedentInformation();
        final SchoolLevelType schoolLevel = info.getPrecedentSchoolLevel();
        return schoolLevel == null ? null : schoolLevel.getLocalizedName();
    }

    public String getOriginDegreeDesignation() {
        final PrecedentDegreeInformation info = getPrecedentInformation();
        return info == null ? null : info.getPrecedentDegreeDesignation();
    }

    public String getUsername() {
        final Person person = getPerson();
        return person == null ? null : person.getUsername();
    }

    public String getPersonName() {
        final Person person = getPerson();
        return person == null ? null : person.getName();
    }

    public String getIdDocumentType() {
        final Person person = getPerson();
        final IDDocumentType type = person.getIdDocumentType();
        return type == null ? null : type.getLocalizedName();
    }

    public String getDocumentIdNumber() {
        final Person person = getPerson();
        return person == null ? null : person.getDocumentIdNumber();
    }

    public String getGender() {
        final Person person = getPerson();
        final Gender gender = person.getGender();
        return gender == null ? null : gender.getLocalizedName();
    }

    public YearMonthDay getDateOfBirthYearMonthDay() {
        final Person person = getPerson();
        return person == null ? null : person.getDateOfBirthYearMonthDay();
    }

    public String getNameOfFather() {
        final Person person = getPerson();
        return person == null ? null : person.getNameOfFather();
    }

    public String getNameOfMother() {
        final Person person = getPerson();
        return person == null ? null : person.getNameOfMother();
    }

    public String getNationality() {
        final Person person = getPerson();
        final Country country = person.getCountry();
        return country == null ? null : country.getName();
    }

    public String getCountryOfBirth() {
        final Person person = getPerson();
        final Country country = person.getCountryOfBirth();
        return country == null ? null : country.getName();
    }

    public String getFiscalNumber() {
        return PersonCustomer.uiPersonFiscalNumber(getPerson());
    }

    public String getDistrictOfBirth() {
        final Person person = getPerson();
        return person == null ? null : person.getDistrictOfBirth();
    }

    public String getDistrictSubdivisionOfBirth() {
        final Person person = getPerson();
        return person == null ? null : person.getDistrictSubdivisionOfBirth();
    }

    public String getParishOfBirth() {
        final Person person = getPerson();
        return person == null ? null : person.getParishOfBirth();
    }

    public String getDefaultEmailAddressValue() {
        final Person person = getPerson();
        return person == null ? null : person.getDefaultEmailAddressValue();
    }

    public String getInstitutionalEmailAddressValue() {
        final Person person = getPerson();
        return person == null ? null : person.getInstitutionalEmailAddressValue();
    }

    public String getOtherEmailAddresses() {
        final Person person = getPerson();
        return person == null ? null : person.getEmailAddresses().stream().map(e -> e.getValue())
                .collect(Collectors.joining(","));
    }

    public String getDefaultPhoneNumber() {
        final Person person = getPerson();
        return person == null ? null : person.getDefaultPhoneNumber();
    }

    public String getDefaultMobilePhoneNumber() {
        final Person person = getPerson();
        return person == null ? null : person.getDefaultMobilePhoneNumber();
    }

    public boolean hasDefaultPhysicalAddress() {
        final Person person = getPerson();
        return person == null ? false : person.hasDefaultPhysicalAddress();
    }

    private PhysicalAddress getDefaultPhysicalAddressObject() {
        final Person person = getPerson();
        return person == null ? null : person.getDefaultPhysicalAddress();
    }

    public String getDefaultPhysicalAddress() {
        final PhysicalAddress address = getDefaultPhysicalAddressObject();
        return address == null ? null : address.getAddress();
    }

    public String getDefaultPhysicalAddressDistrictOfResidence() {
        final PhysicalAddress address = getDefaultPhysicalAddressObject();
        return address == null ? null : address.getDistrictOfResidence();
    }

    public String getDefaultPhysicalAddressDistrictSubdivisionOfResidence() {
        final PhysicalAddress address = getDefaultPhysicalAddressObject();
        return address == null ? null : address.getDistrictSubdivisionOfResidence();
    }

    public String getDefaultPhysicalAddressParishOfResidence() {
        final PhysicalAddress address = getDefaultPhysicalAddressObject();
        return address == null ? null : address.getParishOfResidence();
    }

    public String getDefaultPhysicalAddressArea() {
        final PhysicalAddress address = getDefaultPhysicalAddressObject();
        return address == null ? null : address.getArea();
    }

    public String getDefaultPhysicalAddressAreaCode() {
        final PhysicalAddress address = getDefaultPhysicalAddressObject();
        return address == null ? null : address.getAreaCode();
    }

    public String getDefaultPhysicalAddressAreaOfAreaCode() {
        final PhysicalAddress address = getDefaultPhysicalAddressObject();
        return address == null ? null : address.getAreaOfAreaCode();
    }

    public String getDefaultPhysicalAddressCountryOfResidenceName() {
        final PhysicalAddress address = getDefaultPhysicalAddressObject();
        return address == null ? null : address.getCountryOfResidenceName();
    }

    public boolean isTuitionCharged() {
        final ITreasuryBridgeAPI treasuryBridgeAPI = TreasuryBridgeAPIFactory.implementation();
        if (treasuryBridgeAPI == null) {
            return false;
        }

        final ITuitionTreasuryEvent event =
                treasuryBridgeAPI.getTuitionForRegistrationTreasuryEvent(registration, getExecutionYear());

        return event != null && event.isCharged();
    }

    public BigDecimal getTuitionAmount() {
        final ITreasuryBridgeAPI treasuryBridgeAPI = TreasuryBridgeAPIFactory.implementation();
        if (treasuryBridgeAPI == null) {
            return BigDecimal.ZERO;
        }

        final ITuitionTreasuryEvent event =
                treasuryBridgeAPI.getTuitionForRegistrationTreasuryEvent(registration, getExecutionYear());

        if (event == null) {
            return BigDecimal.ZERO;
        }

        return event.getAmountToPay();
    }

    public Integer getEnrolmentYears() {
        return getEnrolmentExecutionYears().size();
    }

    public BigDecimal getEnrolmentYearsForPrescription() {

        final PrescriptionConfig config = PrescriptionConfig.findBy(getDegreeCurricularPlan());
        if (config == null) {
            return null;
        }

        //TODO: move logic to PrescriptionConfig?

        final Collection<ExecutionYear> executionYears = config.filterExecutionYears(registration, getEnrolmentExecutionYears());
        BigDecimal result = new BigDecimal(executionYears.size());
        BigDecimal bonification = BigDecimal.ZERO;
        for (final ExecutionYear iter : executionYears) {
            bonification = bonification.add(config.getBonification(StatuteServices.findStatuteTypes(getRegistration(), iter),
                    getRegistration().isPartialRegime(iter)));
        }

        return BigDecimal.ZERO.max(result.subtract(bonification));
    }

    private Set<ExecutionYear> getEnrolmentExecutionYears() {
        return RegistrationServices.getEnrolmentYears(registration).stream().filter(ey -> ey.isBeforeOrEquals(getExecutionYear()))
                .collect(Collectors.toSet());
    }

    public String getOtherConcludedRegistrationYears() {
        final StringBuilder result = new StringBuilder();

        getStudent().getRegistrationsSet().stream()

                .filter(r -> r != registration && r.isConcluded() && r.getLastStudentCurricularPlan() != null)

                .forEach(r -> {

                    final SortedSet<ExecutionYear> executionYears =
                            Sets.newTreeSet(ExecutionYear.COMPARATOR_BY_BEGIN_DATE.reversed());
                    executionYears.addAll(RegistrationServices.getEnrolmentYears(r));

                    if (!executionYears.isEmpty()) {
                        result.append(executionYears.first().getQualifiedName()).append("|");
                    }

                });

        return result.toString().endsWith("|") ? result.delete(result.length() - 1, result.length()).toString() : result
                .toString();
    }

}
