package org.fenixedu.ulisboa.specifications.domain;

import org.fenixedu.academic.domain.DegreeInfo;

public class ExtendedDegreeInfo extends ExtendedDegreeInfo_Base {

    public ExtendedDegreeInfo() {
        super();
    }

    public ExtendedDegreeInfo(DegreeInfo degreeInfo) {
        setDegreeInfo(degreeInfo);
    }

}
