package org.fenixedu.ulisboa.specifications.domain;

import org.fenixedu.academic.domain.Degree;

public class FirstYearRegistrationConfiguration extends FirstYearRegistrationConfiguration_Base {

    public FirstYearRegistrationConfiguration(Degree degree) {
        super();
        setGlobalConfiguration(FirstYearRegistrationGlobalConfiguration.getInstance());
        setDegree(degree);
    }

    public void delete() {
        setDegree(null);
        super.deleteDomainObject();
    }

}
