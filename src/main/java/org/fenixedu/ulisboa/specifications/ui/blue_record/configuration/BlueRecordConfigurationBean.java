package org.fenixedu.ulisboa.specifications.ui.blue_record.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.domain.bluerecord.BlueRecordConfiguration;

public class BlueRecordConfigurationBean implements IBean {

    private List<DegreeType> degreeTypes;
    private List<TupleDataSourceBean> degreeTypeDataSource;
    private List<Degree> degrees;
    private List<TupleDataSourceBean> degreeDataSource;

    public List<DegreeType> getDegreeTypes() {
        return degreeTypes;
    }

    public void setDegreeTypes(final Collection<DegreeType> degreeTypes) {
        this.degreeTypes = new ArrayList<>(degreeTypes);
    }

    public List<TupleDataSourceBean> getDegreeTypeDataSource() {
        return degreeTypeDataSource;
    }

    public void setDegreeTypeDataSource(final Collection<DegreeType> degreeTypes) {
        this.degreeTypeDataSource = degreeTypes.stream().map(dT -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(dT.getExternalId());
            tuple.setText(dT.getName().getContent());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public List<Degree> getDegrees() {
        return degrees;
    }

    public void setDegrees(final Collection<Degree> degrees) {
        this.degrees = new ArrayList<>(degrees);
    }

    public List<TupleDataSourceBean> getDegreeDataSource() {
        return degreeDataSource;
    }

    public void setDegreeDataSource(final Collection<Degree> degrees) {
        this.degreeDataSource = degrees.stream().map(d -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(d.getExternalId());
            tuple.setText(d.getPresentationName());
            return tuple;
        }).sorted(TupleDataSourceBean.COMPARE_BY_TEXT).collect(Collectors.toList());
    }

    public BlueRecordConfigurationBean() {
        setDegrees(new ArrayList<>());
        setDegreeTypes(new ArrayList<>());

        setDegreeTypeDataSource(Bennu.getInstance().getDegreeTypeSet());
        setDegreeDataSource(new ArrayList<>());
    }

    public BlueRecordConfigurationBean(final BlueRecordConfiguration configuration) {
        this();
        setDegrees(configuration.getExclusiveDegreesSet());
        setDegreeTypes(getDegrees().stream().map(d -> d.getDegreeType()).collect(Collectors.toSet()));

        setDegreeDataSource(getDegreeTypes().stream().flatMap(dT -> dT.getDegreeSet().stream()).collect(Collectors.toSet()));
    }

    public void updateLists() {
        setDegreeDataSource(getDegreeTypes().stream().flatMap(dT -> dT.getDegreeSet().stream()).collect(Collectors.toSet()));
    }
}
