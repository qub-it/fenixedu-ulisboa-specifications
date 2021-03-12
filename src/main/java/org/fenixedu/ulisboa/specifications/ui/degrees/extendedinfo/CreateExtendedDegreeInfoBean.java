package org.fenixedu.ulisboa.specifications.ui.degrees.extendedinfo;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;

public class CreateExtendedDegreeInfoBean implements IBean {

    private Degree degree;
    private List<TupleDataSourceBean> degreesDataSource;
    private ExecutionYear executionInterval;
    private List<TupleDataSourceBean> executionIntervalsDataSource;

    public void updateLists() {
        setDegreesDataSource(Degree.readBolonhaDegrees());

        if (degree != null) {
            final Set<ExecutionYear> existingYears =
                    degree.getDegreeInfosSet().stream().map(di -> di.getExecutionYear()).collect(Collectors.toSet());
            final List<ExecutionYear> yearsOptions = ExecutionYear.readNotClosedExecutionYears().stream()
                    .filter(y -> !existingYears.contains(y)).sorted(Collections.reverseOrder()).collect(Collectors.toList());
            setExecutionIntervalsDataSource(yearsOptions);
        } else {
            setExecutionIntervalsDataSource(ExecutionYear.readNotClosedExecutionYears());
        }
    }

    public CreateExtendedDegreeInfoBean() {
        super();
        setDegreesDataSource(Degree.readBolonhaDegrees());
        setExecutionIntervalsDataSource(ExecutionYear.readNotClosedExecutionYears());
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public List<TupleDataSourceBean> getDegreesDataSource() {
        return degreesDataSource;
    }

    public void setDegreesDataSource(List<Degree> degreesDataSource) {
        this.degreesDataSource = degreesDataSource.stream().map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.getExternalId());
            tuple.setText(x.getCode() + " - " + x.getPresentationNameI18N().getContent());
            return tuple;
        }).collect(Collectors.toList());
    }

    public ExecutionYear getExecutionInterval() {
        return executionInterval;
    }

    public void setExecutionInterval(ExecutionYear executionInterval) {
        this.executionInterval = executionInterval;
    }

    public List<TupleDataSourceBean> getExecutionIntervalsDataSource() {
        return executionIntervalsDataSource;
    }

    public void setExecutionIntervalsDataSource(List<ExecutionYear> executionIntervalsDataSource) {
        this.executionIntervalsDataSource = executionIntervalsDataSource.stream().sorted(Collections.reverseOrder()).map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.getExternalId());
            tuple.setText(x.getQualifiedName());
            return tuple;
        }).collect(Collectors.toList());;
    }
}
