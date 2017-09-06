package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.importation;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.EntryPhase;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.space.SpaceUtils;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.spaces.domain.Space;

public class DgesImportProcessBean implements IBean {
    private ExecutionYear executionYear;
    private List<TupleDataSourceBean> executionYearDataSource;
    private Space space;
    private List<TupleDataSourceBean> spaceDataSource;
    private EntryPhase phase;
    private List<TupleDataSourceBean> phaseDataSource;

    public DgesImportProcessBean() {
        setExecutionYearDataSource(ExecutionYear.readNotClosedExecutionYears());
        setSpaceDataSource(Space.getAllCampus());
        setPhaseDataSource(Arrays.asList(EntryPhase.values()));
    }

    public DgesImportProcessBean(final ExecutionYear executionYear) {
        this();

        this.executionYear = executionYear;
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

    public void setExecutionYearDataSource(final List<ExecutionYear> executionYears) {
        this.executionYearDataSource =
                executionYears.stream().sorted(ExecutionYear.COMPARATOR_BY_BEGIN_DATE.reversed()).map(exY -> {
                    TupleDataSourceBean tuple = new TupleDataSourceBean();
                    tuple.setId(exY.getExternalId());
                    tuple.setText(exY.getQualifiedName());
                    return tuple;
                }).collect(Collectors.toList());
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(final Space space) {
        this.space = space;
    }

    public List<TupleDataSourceBean> getSpaceDataSource() {
        return spaceDataSource;
    }

    public void setSpaceDataSource(final Set<Space> spaces) {
        this.spaceDataSource = spaces.stream().sorted(SpaceUtils.COMPARATOR_BY_PRESENTATION_NAME).map(s -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(s.getExternalId());
            tuple.setText(s.getPresentationName());
            return tuple;
        }).collect(Collectors.toList());
    }

    public EntryPhase getPhase() {
        return phase;
    }

    public void setPhase(final EntryPhase phase) {
        this.phase = phase;
    }

    public List<TupleDataSourceBean> getPhaseDataSource() {
        return phaseDataSource;
    }

    public void setPhaseDataSource(final List<EntryPhase> phases) {
        this.phaseDataSource = phases.stream().map(p -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(p.getName());
            tuple.setText(p.getLocalizedName());
            return tuple;
        }).collect(Collectors.toList());
    }

    public void updateLists() {
        //Force update lists when they depend on each other
    }

}
