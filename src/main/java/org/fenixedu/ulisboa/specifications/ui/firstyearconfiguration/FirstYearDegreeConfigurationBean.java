package org.fenixedu.ulisboa.specifications.ui.firstyearconfiguration;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationConfiguration;

public class FirstYearDegreeConfigurationBean implements IBean {

    private Degree degree;
    private String label;
    private String code;
    private boolean requiresVaccination;
    private DegreeCurricularPlan degreeCurricularPlan;
    private List<TupleDataSourceBean> degreeCurricularPlanDataSource;

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isRequiresVaccination() {
        return requiresVaccination;
    }

    public void setRequiresVaccination(boolean requiresVaccination) {
        this.requiresVaccination = requiresVaccination;
    }

    public DegreeCurricularPlan getDegreeCurricularPlan() {
        return degreeCurricularPlan;
    }

    public void setDegreeCurricularPlan(DegreeCurricularPlan degreeCurricularPlan) {
        this.degreeCurricularPlan = degreeCurricularPlan;
    }

    public List<TupleDataSourceBean> getDegreeCurricularPlanDataSource() {
        return degreeCurricularPlanDataSource;
    }

    public void setDegreeCurricularPlanDataSource(List<DegreeCurricularPlan> degreeCurricularPlanDataSource) {
        this.degreeCurricularPlanDataSource = degreeCurricularPlanDataSource.stream().map(plan ->
        {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(plan.getExternalId());
            tuple.setText(plan.getName());
            return tuple;
        }).collect(Collectors.toList());
    }

    public FirstYearDegreeConfigurationBean() {
    }

    public FirstYearDegreeConfigurationBean(FirstYearRegistrationConfiguration configuration) {
        this();
        setDegree(configuration.getDegree());
        setLabel("[" + configuration.getDegree().getCode() + "] " + configuration.getDegree().getPresentationName());
        setCode(configuration.getDegree().getMinistryCode());
        setRequiresVaccination(configuration.getRequiresVaccination());
        setDegreeCurricularPlan(configuration.getDegreeCurricularPlan());
        setDegreeCurricularPlanDataSource(degree.getDegreeCurricularPlansSet().stream()
                .filter(plan -> plan.getExecutionYears().contains(configuration.getExecutionYear()))
                .collect(Collectors.toList()));
    }
}
