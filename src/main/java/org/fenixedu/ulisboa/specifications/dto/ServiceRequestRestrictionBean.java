package org.fenixedu.ulisboa.specifications.dto;

import org.fenixedu.bennu.IBean;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestRestriction;

public class ServiceRequestRestrictionBean implements IBean {

    private ServiceRequestRestriction restriction;
    private String degreeType;
    private String degree;
    private String programConclusion;

    public ServiceRequestRestrictionBean(ServiceRequestRestriction restriction) {
        super();
        this.restriction = restriction;
        degreeType = restriction.getDegreeType() != null ? restriction.getDegreeType().getName().getContent() : "-";
        degree = restriction.getDegree() != null ? restriction.getDegree().getPresentationName() : "-";
        programConclusion =
                restriction.getProgramConclusion() != null ? restriction.getProgramConclusion().getName().getContent() : "-";
    }

    public ServiceRequestRestriction getRestriction() {
        return restriction;
    }

    public String getDegreeType() {
        return degreeType;
    }

    public String getDegree() {
        return degree;
    }

    public String getProgramConclusion() {
        return programConclusion;
    }

}
