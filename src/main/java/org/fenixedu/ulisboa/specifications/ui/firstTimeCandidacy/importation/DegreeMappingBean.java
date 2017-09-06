package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.importation;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationConfiguration;

public class DegreeMappingBean implements IBean {

    private Degree degree;
    private String label;
    private String code;
    private boolean requiresVaccination;
    private DegreeCurricularPlan degreeCurricularPlan;
    private List<TupleDataSourceBean> degreeCurricularPlanDataSource;
    private FirstYearRegistrationConfiguration configuration;

    public DegreeMappingBean() {
    }

    public DegreeMappingBean(final FirstYearRegistrationConfiguration configuration) {
        this();
        setDegree(configuration.getDegree());
        setLabel("[" + configuration.getDegree().getCode() + "] " + configuration.getDegree().getPresentationName());
        setCode(configuration.getDegree().getMinistryCode());
        setRequiresVaccination(configuration.getRequiresVaccination());
        setDegreeCurricularPlan(configuration.getDegreeCurricularPlan());
        setDegreeCurricularPlanDataSource(degree.getDegreeCurricularPlansSet().stream().collect(Collectors.toList()));
        setConfiguration(configuration);
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(final Degree degree) {
        this.degree = degree;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public boolean isRequiresVaccination() {
        return requiresVaccination;
    }

    public void setRequiresVaccination(final boolean requiresVaccination) {
        this.requiresVaccination = requiresVaccination;
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

    public void setDegreeCurricularPlanDataSource(final List<DegreeCurricularPlan> degreeCurricularPlanDataSource) {
        this.degreeCurricularPlanDataSource = degreeCurricularPlanDataSource.stream().map(plan -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(plan.getExternalId());
            tuple.setText(plan.getName());
            return tuple;
        }).collect(Collectors.toList());
    }

    public FirstYearRegistrationConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final FirstYearRegistrationConfiguration configuration) {
        this.configuration = configuration;
    }

    public void update() {
    }

}
