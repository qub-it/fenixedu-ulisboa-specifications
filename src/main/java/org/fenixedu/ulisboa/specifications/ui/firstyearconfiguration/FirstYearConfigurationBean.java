package org.fenixedu.ulisboa.specifications.ui.firstyearconfiguration;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationConfiguration;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationGlobalConfiguration;

public class FirstYearConfigurationBean implements IBean {

    private List<FirstYearDegreeConfigurationBean> activeDegrees;
    private List<TupleDataSourceBean> activeDegreesDataSource;

    public List<FirstYearDegreeConfigurationBean> getActiveDegrees() {
        return activeDegrees;
    }

    public void setActiveDegrees(List<FirstYearRegistrationConfiguration> activeDegrees) {
        this.activeDegrees =
                activeDegrees.stream().map(c -> new FirstYearDegreeConfigurationBean(c)).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getActiveDegreesDataSource() {
        return activeDegreesDataSource;
    }

    public void setActiveDegreesDataSource(List<Degree> activeDegreesDataSource) {
        this.activeDegreesDataSource = activeDegreesDataSource.stream().map(d -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(d.getExternalId());
            tuple.setText("[" + d.getCode() + "] " + d.getPresentationName());
            return tuple;
        }).collect(Collectors.toList());
    }

    public FirstYearConfigurationBean() {
        updateLists();
    }

    public void updateLists() {
        setActiveDegreesDataSource(
                Bennu.getInstance().getDegreesSet().stream().filter(d -> d.isActive()).collect(Collectors.toList()));
        setActiveDegrees(FirstYearRegistrationGlobalConfiguration.getInstance().getFirstYearRegistrationConfigurationsSet()
                .stream().filter(c -> c.getDegree().isActive()).collect(Collectors.toList()));
    }
}
