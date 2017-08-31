package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.util;

public class DegreeDesignationBean {
    private final String degreeDesignationText;
    private final String degreeDesignationId;

    public DegreeDesignationBean(final String degreeDesignationText, final String degreeDesignationId) {
        super();
        this.degreeDesignationText = degreeDesignationText;
        this.degreeDesignationId = degreeDesignationId;
    }

    public String getDegreeDesignationText() {
        return degreeDesignationText;
    }

    public String getDegreeDesignationId() {
        return degreeDesignationId;
    }
}
