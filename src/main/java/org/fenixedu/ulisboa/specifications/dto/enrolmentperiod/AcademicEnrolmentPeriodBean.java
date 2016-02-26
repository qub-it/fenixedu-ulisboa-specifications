package org.fenixedu.ulisboa.specifications.dto.enrolmentperiod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.student.StatuteType;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod.AcademicEnrolmentPeriod;
import org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod.AcademicEnrolmentPeriodType;
import org.joda.time.DateTime;

public class AcademicEnrolmentPeriodBean implements IBean {

    private DateTime startDate;
    private DateTime endDate;
    private Boolean specialSeason = Boolean.FALSE;
    private Boolean firstTimeRegistration = Boolean.FALSE;
    private Boolean restrictToSelectedStatutes = Boolean.FALSE;
    private Integer minStudentNumber;
    private Integer maxStudentNumber;
    private Integer curricularYear;
    private AcademicEnrolmentPeriodType enrolmentPeriodType;
    private List<TupleDataSourceBean> enrolmentPeriodTypeDataSource;
    private ExecutionSemester executionSemester;
    private List<TupleDataSourceBean> executionSemesterDataSource;
    private List<DegreeCurricularPlan> degreeCurricularPlans;
    private List<TupleDataSourceBean> degreeCurricularPlanDataSource;
    private List<StatuteType> statutesTypes;
    private List<TupleDataSourceBean> statuteTypeDataSource;

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

    public Boolean getSpecialSeason() {
        return specialSeason;
    }

    public void setSpecialSeason(Boolean specialSeason) {
        this.specialSeason = specialSeason;
    }

    public Boolean getFirstTimeRegistration() {
        return firstTimeRegistration;
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
            tuple.setText(plan.getPresentationName());
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
            tuple.setText(statute.getName().getContent());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public AcademicEnrolmentPeriodBean() {
        setEnrolmentPeriodTypeDataSource(Arrays.asList(AcademicEnrolmentPeriodType.values()));
        setExecutionSemesterDataSource(ExecutionSemester.readNotClosedExecutionPeriods());

        setDegreeCurricularPlans(Collections.emptyList());
        setStatuteTypes(Collections.emptyList());
        //TODOJN : ver como popular
        setDegreeCurricularPlanDataSource(DegreeCurricularPlan.readNotEmptyDegreeCurricularPlans());
        setStatuteTypeDataSource(Bennu.getInstance().getStatuteTypesSet().stream().collect(Collectors.toList()));
    }

    public AcademicEnrolmentPeriodBean(AcademicEnrolmentPeriod academicEnrolmentPeriod) {
        this();
        setStartDate(academicEnrolmentPeriod.getStartDate());
        setEndDate(academicEnrolmentPeriod.getEndDate());
        setSpecialSeason(academicEnrolmentPeriod.getSpecialSeason());
        setFirstTimeRegistration(academicEnrolmentPeriod.getFirstTimeRegistration());
        setRestrictToSelectedStatutes(academicEnrolmentPeriod.getRestrictToSelectedStatutes());
        setMinStudentNumber(academicEnrolmentPeriod.getMinStudentNumber());
        setMaxStudentNumber(academicEnrolmentPeriod.getMaxStudentNumber());
        setCurricularYear(academicEnrolmentPeriod.getCurricularYear());
        setEnrolmentPeriodType(academicEnrolmentPeriod.getEnrolmentPeriodType());
        setExecutionSemester(academicEnrolmentPeriod.getExecutionSemester());
        setDegreeCurricularPlans(academicEnrolmentPeriod.getDegreeCurricularPlansSet().stream().collect(Collectors.toList()));
        setStatuteTypes(academicEnrolmentPeriod.getStatuteTypesSet().stream().collect(Collectors.toList()));
    }

    public void updateLists() {
        //TODOJN : ver como popular
        setDegreeCurricularPlanDataSource(DegreeCurricularPlan.readNotEmptyDegreeCurricularPlans());
        setStatuteTypeDataSource(Bennu.getInstance().getStatuteTypesSet().stream().collect(Collectors.toList()));
    }

}
