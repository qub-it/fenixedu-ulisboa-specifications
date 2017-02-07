package org.fenixedu.ulisboa.specifications.ui.degrees.extendeddegreeinfo;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.DegreeInfo;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.CourseGroupDegreeInfo;

public class CourseGroupDegreeInfoBean implements IBean {

    private ExecutionYear executionYear;
    private List<TupleDataSourceBean> executionYearDataSource;
    private Degree degree;
    private List<TupleDataSourceBean> degreeDataSource;
    private DegreeCurricularPlan degreeCurricularPlan;
    private List<TupleDataSourceBean> degreeCurricularPlanDataSource;
    private CourseGroup courseGroup;
    private List<TupleDataSourceBean> courseGroupDataSource;
    private LocalizedString name;

    // ReadOnly Objects
    private String courseGroupName;
    private String externalId;

    public CourseGroupDegreeInfoBean() {
        updateLists();
    }

    public CourseGroupDegreeInfoBean(final CourseGroupDegreeInfo degreeDocumentInfo) {
        DegreeInfo info = degreeDocumentInfo.getExtendedDegreeInfo().getDegreeInfo();
        CourseGroup courseGroup = degreeDocumentInfo.getCourseGroup();
        if (info != null) {
            setExecutionYear(info.getExecutionYear());
            setDegree(info.getDegree());
        }
        if (courseGroup != null) {
            setDegreeCurricularPlan(courseGroup.getParentDegreeCurricularPlan());
            setCourseGroup(courseGroup);
        }
        setName(degreeDocumentInfo.getDegreeName());
        this.externalId = degreeDocumentInfo.getExternalId();

        updateLists();
    }

    public ExecutionYear getExecutionYear() {
        return executionYear;
    }

    public void setExecutionYear(final ExecutionYear executionYear) {
        this.executionYear = executionYear;
    }

    public List<TupleDataSourceBean> getExecutionYearDataSource() {
        return executionYearDataSource;
    }

    public void setExecutionYearDataSource(final Collection<ExecutionYear> executionYearDataSource) {
        this.executionYearDataSource = executionYearDataSource.stream().map(execYear -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(execYear.getExternalId());
            tuple.setText(execYear.getQualifiedName());
            return tuple;
        }).collect(Collectors.toList());
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(final Degree degree) {
        this.degree = degree;
    }

    public List<TupleDataSourceBean> getDegreeDataSource() {
        return degreeDataSource;
    }

    public void setDegreeDataSource(final Collection<Degree> degreeDataSource) {
        this.degreeDataSource = degreeDataSource.stream().map(degree -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(degree.getExternalId());
            tuple.setText(degree.getCode() + " - "
                    + degree.getMostRecentDegreeInfo(executionYear.getAcademicInterval()).getName().getContent());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public DegreeCurricularPlan getDegreeCurricularPlan() {
        return degreeCurricularPlan;
    }

    public void setDegreeCurricularPlan(final DegreeCurricularPlan degreeCurricularPlan) {
        this.degreeCurricularPlan = degreeCurricularPlan;
    }

    public List<TupleDataSourceBean> getDegreeCurricularPlanDataSource() {
        return degreeCurricularPlanDataSource;
    }

    public void setDegreeCurricularPlanDataSource(final Collection<DegreeCurricularPlan> degreeCurricularPlanDataSource) {
        this.degreeCurricularPlanDataSource = degreeCurricularPlanDataSource.stream().map(plan -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(plan.getExternalId());
            tuple.setText(plan.getPresentationName());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public CourseGroup getCourseGroup() {
        return courseGroup;
    }

    public void setCourseGroup(final CourseGroup courseGroup) {
        this.courseGroup = courseGroup;
        if (courseGroup != null) {
            this.courseGroupName = courseGroup.getName();
        }
    }

    public String getCourseGroupName() {
        return courseGroupName;
    }

    public List<TupleDataSourceBean> getCourseGroupDataSource() {
        return courseGroupDataSource;
    }

    public void setCourseGroupDataSource(final Collection<CourseGroup> courseGroupDataSource) {
        this.courseGroupDataSource = courseGroupDataSource.stream().map(group -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(group.getExternalId());
            tuple.setText(group.getName());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public LocalizedString getName() {
        return name;
    }

    public void setName(final LocalizedString name) {
        this.name = name;
    }

    public void updateLists() {
        List<ExecutionYear> executionYears = ExecutionYear.readNotClosedExecutionYears();
        executionYears =
                executionYears.stream().sorted(ExecutionYear.COMPARATOR_BY_BEGIN_DATE.reversed()).collect(Collectors.toList());
        setExecutionYearDataSource(executionYears);
        if (executionYear != null) {
            Set<Degree> allDegrees = new TreeSet<>(Degree.COMPARATOR_BY_DEGREE_TYPE_AND_NAME_AND_ID);
            allDegrees.addAll(Bennu.getInstance().getDegreesSet().stream()
                    .filter(d -> d.getExecutionDegreesForExecutionYear(executionYear).size() > 0).collect(Collectors.toList()));
            setDegreeDataSource(allDegrees);
            if (degree != null) {
                List<DegreeCurricularPlan> plans = degree.getDegreeCurricularPlansForYear(executionYear);
                setDegreeCurricularPlanDataSource(plans);
                if (degreeCurricularPlan != null) {
                    List<CourseGroup> courseGroups = degreeCurricularPlan.getAllCoursesGroups().stream()
                            .filter(cg -> cg.getProgramConclusion() != null).collect(Collectors.toList());
                    setCourseGroupDataSource(courseGroups);
                }
            }
        }
    }

    public String getExternalId() {
        return externalId;
    }

}
