package org.fenixedu.ulisboa.specifications.dto.enrolmentperiod;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.DomainObjectUtil;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.degree.degreeCurricularPlan.DegreeCurricularPlanState;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.StatuteType;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod.AcademicEnrolmentPeriod;
import org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod.AcademicEnrolmentPeriodType;
import org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod.AutomaticEnrolment;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.CourseEnrolmentDA;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.InitialSchoolClassStudentEnrollmentDA;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.SchoolClassStudentEnrollmentDA;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.ShiftEnrolmentController;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentStep;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;

public class AcademicEnrolmentPeriodBean implements IBean {

    public static Comparator<IngressionType> INGRESSION_TYPE_COMPARATOR_BY_DESCRIPTION =
            Comparator.comparing(IngressionType::getDescription).thenComparing(DomainObjectUtil.COMPARATOR_BY_ID);

    private DateTime startDate;
    private DateTime endDate;
    private Boolean firstTimeRegistration = null;
    private Boolean restrictToSelectedStatutes = Boolean.FALSE;
    private Boolean restrictToSelectedIngressionTypes = Boolean.FALSE;
    private Integer minStudentNumber;
    private Integer maxStudentNumber;
    private Integer curricularYear;
    private Boolean schoolClassSelectionMandatory = Boolean.FALSE;
    private Boolean allowEnrolWithDebts = Boolean.FALSE;
    private AcademicEnrolmentPeriodType enrolmentPeriodType;
    private List<TupleDataSourceBean> enrolmentPeriodTypeDataSource;
    private AutomaticEnrolment automaticEnrolment;
    private List<TupleDataSourceBean> automaticEnrolmentDataSource;
    private ExecutionSemester executionSemester;
    private List<TupleDataSourceBean> executionSemesterDataSource;
    private List<DegreeCurricularPlan> degreeCurricularPlans;
    private List<TupleDataSourceBean> degreeCurricularPlanDataSource;
    private List<StatuteType> statutesTypes;
    private List<TupleDataSourceBean> statuteTypeDataSource;
    private List<IngressionType> ingressionTypes;
    private List<TupleDataSourceBean> ingressionTypeDataSource;

    // slots for collecting periods of students
    private AcademicEnrolmentPeriod enrolmentPeriod;
    private StudentCurricularPlan studentCurricularPlan;
    private Set<StatuteType> studentStatuteTypes;
    private IngressionType studentIngressionType;

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public Boolean getFirstTimeRegistration() {
        return firstTimeRegistration;
    }

    public boolean isFirstTimeRegistration() {
        return firstTimeRegistration != null && firstTimeRegistration.booleanValue();
    }

    public void setFirstTimeRegistration(Boolean firstTimeRegistration) {
        this.firstTimeRegistration = firstTimeRegistration;
    }

    public Boolean getRestrictToSelectedStatutes() {
        return restrictToSelectedStatutes;
    }

    public void setRestrictToSelectedStatutes(Boolean restrictToSelectedStatutes) {
        this.restrictToSelectedStatutes = restrictToSelectedStatutes;
    }

    public Boolean getRestrictToSelectedIngressionTypes() {
        return restrictToSelectedIngressionTypes;
    }

    public void setRestrictToSelectedIngressionTypes(Boolean restrictToSelectedIngressionTypes) {
        this.restrictToSelectedIngressionTypes = restrictToSelectedIngressionTypes;
    }

    public Integer getMinStudentNumber() {
        return minStudentNumber;
    }

    public void setMinStudentNumber(Integer minStudentNumber) {
        this.minStudentNumber = minStudentNumber;
    }

    public Integer getMaxStudentNumber() {
        return maxStudentNumber;
    }

    public void setMaxStudentNumber(Integer maxStudentNumber) {
        this.maxStudentNumber = maxStudentNumber;
    }

    public Integer getCurricularYear() {
        return curricularYear;
    }

    public void setCurricularYear(Integer curricularYear) {
        this.curricularYear = curricularYear;
    }

    public Boolean getSchoolClassSelectionMandatory() {
        return schoolClassSelectionMandatory;
    }

    public void setSchoolClassSelectionMandatory(Boolean schoolClassSelectionMandatory) {
        this.schoolClassSelectionMandatory = schoolClassSelectionMandatory;
    }

    public Boolean getAllowEnrolWithDebts() {
        return allowEnrolWithDebts;
    }

    public void setAllowEnrolWithDebts(Boolean allowEnrolWithDebts) {
        this.allowEnrolWithDebts = allowEnrolWithDebts;
    }

    public AcademicEnrolmentPeriodType getEnrolmentPeriodType() {
        return enrolmentPeriodType;
    }

    public void setEnrolmentPeriodType(AcademicEnrolmentPeriodType enrolmentPeriodType) {
        this.enrolmentPeriodType = enrolmentPeriodType;
    }

    public List<TupleDataSourceBean> getEnrolmentPeriodTypeDataSource() {
        return enrolmentPeriodTypeDataSource;
    }

    public void setEnrolmentPeriodTypeDataSource(List<AcademicEnrolmentPeriodType> enrolmentPeriodTypes) {
        this.enrolmentPeriodTypeDataSource = enrolmentPeriodTypes.stream().map(t -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(t.toString());
            tuple.setText(t.getDescriptionI18N().getContent());
            return tuple;
        }).collect(Collectors.toList());
    }

    public AutomaticEnrolment getAutomaticEnrolment() {
        return automaticEnrolment;
    }

    public void setAutomaticEnrolment(AutomaticEnrolment automaticEnrolment) {
        this.automaticEnrolment = automaticEnrolment;
    }

    public List<TupleDataSourceBean> getAutomaticEnrolmentDataSource() {
        return automaticEnrolmentDataSource;
    }

    public void setAutomaticEnrolmentDataSource(List<AutomaticEnrolment> automaticEnrolments) {
        this.automaticEnrolmentDataSource = automaticEnrolments.stream().map(ae -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(ae.toString());
            tuple.setText(ae.getDescriptionI18N().getContent());
            return tuple;
        }).collect(Collectors.toList());
    }

    public ExecutionYear getExecutionYear() {
        return getExecutionSemester().getExecutionYear();
    }

    public ExecutionSemester getExecutionSemester() {
        return executionSemester;
    }

    public void setExecutionSemester(ExecutionSemester executionSemester) {
        this.executionSemester = executionSemester;
    }

    public List<TupleDataSourceBean> getExecutionSemesterDataSource() {
        return executionSemesterDataSource;
    }

    public void setExecutionSemesterDataSource(List<ExecutionSemester> executionSemesters) {
        this.executionSemesterDataSource =
                executionSemesters.stream().sorted(ExecutionSemester.COMPARATOR_BY_BEGIN_DATE.reversed()).map(semester -> {
                    TupleDataSourceBean tuple = new TupleDataSourceBean();
                    tuple.setId(semester.getExternalId());
                    tuple.setText(semester.getQualifiedName());
                    return tuple;
                }).collect(Collectors.toList());
    }

    public List<DegreeCurricularPlan> getDegreeCurricularPlans() {
        return degreeCurricularPlans;
    }

    public void setDegreeCurricularPlans(List<DegreeCurricularPlan> degreeCurricularPlans) {
        this.degreeCurricularPlans = degreeCurricularPlans;
    }

    public List<TupleDataSourceBean> getDegreeCurricularPlanDataSource() {
        return degreeCurricularPlanDataSource;
    }

    public void setDegreeCurricularPlanDataSource(List<DegreeCurricularPlan> degreeCurricularPlans) {
        this.degreeCurricularPlanDataSource = degreeCurricularPlans.stream().map(plan -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(plan.getExternalId());
            tuple.setText("[" + plan.getDegree().getCode() + "] " + plan.getPresentationName());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<StatuteType> getStatuteTypes() {
        return statutesTypes;
    }

    public void setStatuteTypes(List<StatuteType> studentStatutes) {
        this.statutesTypes = studentStatutes;
    }

    public List<TupleDataSourceBean> getStatuteTypeDataSource() {
        return statuteTypeDataSource;
    }

    public void setStatuteTypeDataSource(List<StatuteType> statuteTypes) {
        this.statuteTypeDataSource = statuteTypes.stream().map(statute -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(statute.getExternalId());
            tuple.setText("[" + statute.getCode() + "] " + statute.getName().getContent());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public boolean isForCurricularCourses() {
        return getEnrolmentPeriodType() == AcademicEnrolmentPeriodType.CURRICULAR_COURSE;
    }

    public boolean isForClasses() {
        return getEnrolmentPeriodType() == AcademicEnrolmentPeriodType.SCHOOL_CLASS;
    }

    public boolean isForInitialClasses() {
        return getEnrolmentPeriodType() == AcademicEnrolmentPeriodType.INITIAL_SCHOOL_CLASS;
    }

    public boolean isForShift() {
        return getEnrolmentPeriodType() == AcademicEnrolmentPeriodType.SHIFT;
    }

    public boolean isOpen() {
        return getEnrolmentPeriod() == null ? false : getEnrolmentPeriod().isOpen();
    }

    public boolean isUpcoming() {
        return getEnrolmentPeriod() == null ? false : getEnrolmentPeriod().isUpcoming();
    }

    public AcademicEnrolmentPeriod getEnrolmentPeriod() {
        return enrolmentPeriod;
    }

    public void setEnrolmentPeriod(final AcademicEnrolmentPeriod input) {
        this.enrolmentPeriod = input;
    }

    public Registration getRegistration() {
        return getStudentCurricularPlan().getRegistration();
    }

    public StudentCurricularPlan getStudentCurricularPlan() {
        return studentCurricularPlan;
    }

    public StudentCurricularPlan setStudentCurricularPlan(final StudentCurricularPlan input) {
        return this.studentCurricularPlan = input;
    }

    public Set<StatuteType> getStudentStatuteTypes() {
        return studentStatuteTypes;
    }

    public void setStudentStatuteTypes(final Set<StatuteType> input) {
        this.studentStatuteTypes = input;
    }

    public IngressionType getStudentIngressionType() {
        return studentIngressionType;
    }

    public void setStudentIngressionType(IngressionType studentIngressionType) {
        this.studentIngressionType = studentIngressionType;
    }

    public List<IngressionType> getIngressionTypes() {
        return ingressionTypes;
    }

    public void setIngressionTypes(List<IngressionType> ingressionTypes) {
        this.ingressionTypes = ingressionTypes;
    }

    public List<TupleDataSourceBean> getIngressionTypeDataSource() {
        return ingressionTypeDataSource;
    }

    public void setIngressionTypeDataSource(List<IngressionType> ingressionTypeDataSource) {
        this.ingressionTypeDataSource = ingressionTypeDataSource.stream().map(ingression -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(ingression.getExternalId());
            tuple.setText("[" + ingression.getCode() + "] " + ingression.getDescription().getContent());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public AcademicEnrolmentPeriodBean() {
        setEnrolmentPeriodTypeDataSource(Arrays.asList(AcademicEnrolmentPeriodType.values()));
        setAutomaticEnrolmentDataSource(Arrays.asList(AutomaticEnrolment.values()));
        setExecutionSemesterDataSource(ExecutionSemester.readNotClosedExecutionPeriods());

        setDegreeCurricularPlans(Collections.emptyList());
        setStatuteTypes(Lists.newArrayList());
        setIngressionTypes(Lists.newArrayList());
        if (executionSemester != null) {
            setDegreeCurricularPlanDataSource(DegreeCurricularPlan.readByDegreeTypesAndStateWithExecutionDegreeForYear(dt -> true,
                    DegreeCurricularPlanState.ACTIVE, executionSemester.getExecutionYear()));
        } else {
            setDegreeCurricularPlanDataSource(Collections.emptyList());
        }
        setStatuteTypeDataSource(Bennu.getInstance().getStatuteTypesSet().stream().collect(Collectors.toList()));
        setIngressionTypeDataSource(Bennu.getInstance().getIngressionTypesSet().stream().collect(Collectors.toList()));
    }

    public AcademicEnrolmentPeriodBean(AcademicEnrolmentPeriod academicEnrolmentPeriod) {
        this();
        setEnrolmentPeriod(academicEnrolmentPeriod);
        setStartDate(academicEnrolmentPeriod.getStartDate());
        setEndDate(academicEnrolmentPeriod.getEndDate());
        setFirstTimeRegistration(academicEnrolmentPeriod.getFirstTimeRegistration());
        setRestrictToSelectedStatutes(academicEnrolmentPeriod.getRestrictToSelectedStatutes());
        setRestrictToSelectedIngressionTypes(academicEnrolmentPeriod.getRestrictToSelectedIngressionTypes());
        setMinStudentNumber(academicEnrolmentPeriod.getMinStudentNumber());
        setMaxStudentNumber(academicEnrolmentPeriod.getMaxStudentNumber());
        setCurricularYear(academicEnrolmentPeriod.getCurricularYear());
        setSchoolClassSelectionMandatory(academicEnrolmentPeriod.getSchoolClassSelectionMandatory());
        setAllowEnrolWithDebts(academicEnrolmentPeriod.getAllowEnrolWithDebts());
        setAutomaticEnrolment(academicEnrolmentPeriod.getAutomaticEnrolment());
        setEnrolmentPeriodType(academicEnrolmentPeriod.getEnrolmentPeriodType());
        setExecutionSemester(academicEnrolmentPeriod.getExecutionSemester());
        setDegreeCurricularPlans(academicEnrolmentPeriod.getDegreeCurricularPlansSet().stream().collect(Collectors.toList()));
        setStatuteTypes(academicEnrolmentPeriod.getStatuteTypesSet().stream().collect(Collectors.toList()));
        setIngressionTypes(academicEnrolmentPeriod.getIngressionTypesSet().stream().collect(Collectors.toList()));
    }

    public void updateLists() {
        if (executionSemester != null) {
            setDegreeCurricularPlanDataSource(DegreeCurricularPlan.readByDegreeTypesAndStateWithExecutionDegreeForYear(dt -> true,
                    DegreeCurricularPlanState.ACTIVE, executionSemester.getExecutionYear()));
        } else {
            setDegreeCurricularPlanDataSource(Collections.emptyList());
        }
        setStatuteTypeDataSource(Bennu.getInstance().getStatuteTypesSet().stream().collect(Collectors.toList()));
        setIngressionTypeDataSource(Bennu.getInstance().getIngressionTypesSet().stream().collect(Collectors.toList()));
    }

    public boolean isAutomatic() {
        return getAutomaticEnrolment() != null && getAutomaticEnrolment().isAutomatic();
    }

    public boolean isEditable() {
        return getAutomaticEnrolment() == null || getAutomaticEnrolment().isEditable();
    }

    public String getEntryPointURL() {
        String result = "";

        final String argsStruts = EnrolmentStep.buildArgsStruts(getExecutionSemester(), getStudentCurricularPlan());

        switch (getEnrolmentPeriodType()) {
        case INITIAL_SCHOOL_CLASS:
            result = EnrolmentStep.prepareURL(null, InitialSchoolClassStudentEnrollmentDA.getEntryPointURL(), argsStruts);
            break;

        case CURRICULAR_COURSE:
            result = EnrolmentStep.prepareURL(null, CourseEnrolmentDA.getEntryPointURL(), argsStruts);
            break;

        case SCHOOL_CLASS:
            result = EnrolmentStep.prepareURL(null, SchoolClassStudentEnrollmentDA.getEntryPointURL(), argsStruts);
            break;

        case SHIFT:
            result = ShiftEnrolmentController.getEntryPointURL() + getRegistration().getExternalId() + "/"
                    + getEnrolmentPeriod().getExternalId();
            break;

        default:
            break;
        }

        return result;
    }

}
