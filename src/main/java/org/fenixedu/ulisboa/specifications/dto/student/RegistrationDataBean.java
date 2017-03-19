package org.fenixedu.ulisboa.specifications.dto.student;

import java.io.Serializable;
import java.math.BigDecimal;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationDataByExecutionYear;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationStateType;
import org.fenixedu.academic.domain.studentCurriculum.RootCurriculumGroup;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.academic.dto.student.RegistrationCurriculumBean;
import org.fenixedu.ulisboa.specifications.domain.services.CurriculumModuleServices;
import org.fenixedu.ulisboa.specifications.domain.services.RegistrationServices;
import org.fenixedu.ulisboa.specifications.domain.services.student.RegistrationDataServices;
import org.fenixedu.ulisboa.specifications.domain.student.curriculum.CurriculumConfigurationInitializer.CurricularYearResult;
import org.joda.time.LocalDate;
import org.joda.time.YearMonthDay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class RegistrationDataBean implements Serializable {

    static final private Logger logger = LoggerFactory.getLogger(RegistrationDataBean.class);

    private RegistrationDataByExecutionYear data;

    private LocalDate enrolmentDate;

    public RegistrationDataBean(final RegistrationDataByExecutionYear data) {
        this.setData(data);
        this.setEnrolmentDate(data == null ? null : data.getEnrolmentDate());
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
        return enrolmentDate;
    }

    public void setEnrolmentDate(LocalDate enrolmentDate) {
        this.enrolmentDate = enrolmentDate;
    }

    public boolean isReingression() {
        return getData() == null ? null : getData().getReingression();
    }

    public StudentCurricularPlan getStudentCurricularPlan() {
        final Registration registration = getRegistration();
        final StudentCurricularPlan plan =
                registration == null ? null : registration.getStudentCurricularPlan(getExecutionYear());

        // better to return last than have a NPE
        return plan == null ? registration.getLastStudentCurricularPlan() : plan;
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

    public CurricularYearResult getCurricularYearResult() {
        return RegistrationServices.getCurricularYear(getData().getRegistration(), getData().getExecutionYear());
    }

    public String getCurricularYearPresentation() {
        return String.valueOf(getCurricularYearResult().getResult());
    }

    public String getCurricularYearJustificationPresentation() {
        return getCurricularYearResult().getJustificationPresentation();
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
        // don't forget: Curriculum reports ECTS at the beginning of the year
        final ExecutionYear current = getExecutionYear();
        final ExecutionYear next = current.getNextExecutionYear();

        return Math.max(0, getCreditsConcluded(next) - getCreditsConcluded(current));
    }

    private Double getCreditsConcluded(final ExecutionYear input) {
        return new RegistrationCurriculumBean(getRegistration()).getCurriculum(input).getSumEctsCredits().doubleValue();
    }

    public BigDecimal getEnroledEcts() {
        BigDecimal result = BigDecimal.ZERO;

        final ExecutionYear year = getExecutionYear();
        
        if (RegistrationServices.isCurriculumAccumulated(getRegistration())) {
            for (final StudentCurricularPlan iter : getRegistration().getStudentCurricularPlansSet()) {
                final RootCurriculumGroup curriculumGroup = iter.getRoot();
                result = result.add(CurriculumModuleServices.getEnroledAndNotApprovedEctsCreditsFor(curriculumGroup, year));
            }

        } else {
            final StudentCurricularPlan plan = getStudentCurricularPlan();
            final RootCurriculumGroup curriculumGroup = plan.getRoot();
            result = result.add(CurriculumModuleServices.getEnroledAndNotApprovedEctsCreditsFor(curriculumGroup, year));
        }

        return result;
    }

    public String getRegimePresentation() {
        return getRegistration().getRegimeType(getExecutionYear()).getLocalizedName();
    }

    public String getLastRegistrationStatePresentation() {
        RegistrationStateType result = null;

        final Registration registration = getRegistration();
        if (registration.isConcluded() && getData() == RegistrationDataServices.getLastRegistrationData(registration)) {

            ExecutionYear conclusionYear = null;
            try {

                final ProgramConclusion programConclusion =
                        ProgramConclusion.conclusionsFor(getRegistration()).filter(i -> i.isTerminal()).findFirst().get();

                conclusionYear = new RegistrationConclusionBean(registration, programConclusion).getConclusionYear();
            } catch (final Throwable t) {
                logger.error("Error trying to determine ConclusionYear: {}#{}", getData().toString(), t.getMessage());
            }

            // ATTENTION: conclusion state year != conclusion year
            if (conclusionYear != null && getExecutionYear().isAfterOrEquals(conclusionYear)) {
                result = RegistrationStateType.CONCLUDED;
            }
        }

        // what it should be in a perfect world
        if (result == null) {
            final RegistrationState state = registration.getLastRegistrationState(getExecutionYear());
            result = state == null ? null : state.getStateType();
        }

        return result == null ? "-" : result.getDescription();
    }

    public YearMonthDay getLastAcademicActDate() {
        final StudentCurricularPlan scp = getStudentCurricularPlan();
        return scp == null ? null : CurriculumModuleServices.calculateLastAcademicActDate(scp.getRoot(), getExecutionYear());
    }

}
