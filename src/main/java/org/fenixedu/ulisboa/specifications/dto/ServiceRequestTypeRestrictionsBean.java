package org.fenixedu.ulisboa.specifications.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestRestriction;

import edu.emory.mathcs.backport.java.util.Collections;

public class ServiceRequestTypeRestrictionsBean implements IBean {

    private DegreeType degreeType;
    private Degree degree;
    private ProgramConclusion programConclusion;
    private List<TupleDataSourceBean> degreeTypeDataSource;
    private List<TupleDataSourceBean> degreeDataSource;
    private List<TupleDataSourceBean> programConclusionDataSource;
    private List<ServiceRequestRestrictionBean> restrictions;

    public DegreeType getDegreeType() {
        return degreeType;
    }

    public void setDegreeType(DegreeType degreeType) {
        this.degreeType = degreeType;
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public ProgramConclusion getProgramConclusion() {
        return programConclusion;
    }

    public void setProgramConclusion(ProgramConclusion programConclusion) {
        this.programConclusion = programConclusion;
    }

    public List<TupleDataSourceBean> getDegreeTypeDataSource() {
        return degreeTypeDataSource;
    }

    public void setDegreeTypeDataSource(List<DegreeType> value) {
        this.degreeTypeDataSource = value.stream().map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.getExternalId());
            tuple.setText(x.getName().getContent());
            return tuple;
        }).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getDegreeDataSource() {
        return degreeDataSource;
    }

    public void setDegreeDataSource(List<Degree> value) {
        this.degreeDataSource = value.stream().map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.getExternalId());
            tuple.setText(x.getPresentationNameI18N().getContent());
            return tuple;
        }).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getProgramConclusionDataSource() {
        return programConclusionDataSource;
    }

    public void setProgramConclusionDataSource(List<ProgramConclusion> value) {
        this.programConclusionDataSource = value.stream().map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.getExternalId());
            tuple.setText(x.getName().getContent() + " - " + x.getDescription().getContent());
            return tuple;
        }).collect(Collectors.toList());
    }

    public List<ServiceRequestRestrictionBean> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(List<ServiceRequestRestrictionBean> restrictions) {
        this.restrictions = restrictions;
    }

    public ServiceRequestTypeRestrictionsBean() {
        this.setDegreeTypeDataSource(Bennu.getInstance().getDegreeTypeSet().stream()
                .sorted((dt1, dt2) -> dt1.getName().compareTo(dt2.getName())).collect(Collectors.toList()));
        this.setDegreeDataSource(new ArrayList<Degree>());
        this.setProgramConclusionDataSource(Bennu.getInstance().getProgramConclusionSet().stream()
                .sorted((pc1, pc2) -> pc1.getName().compareTo(pc2.getName())).collect(Collectors.toList()));
    }

    public ServiceRequestTypeRestrictionsBean(ServiceRequestType serviceRequestType) {
        this();
        this.setRestrictions(serviceRequestType.getServiceRequestRestrictionsSet().stream()
                .map(srr -> (new ServiceRequestRestrictionBean(srr))).collect(Collectors.toList()));
    }
}
