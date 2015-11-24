package org.fenixedu.ulisboa.specifications.domain.serviceRequests;

import java.util.function.Predicate;

import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.student.Registration;

public class ServiceRequestRestriction extends ServiceRequestRestriction_Base {

    public ServiceRequestRestriction() {
        super();
    }

    public void delete() {
        setDegreeType(null);
        setDegree(null);
        setProgramConclusion(null);
        setServiceRequestType(null);
        deleteDomainObject();
    }

    public static Predicate<ServiceRequestType> restrictionFilter(Registration registration) {
        Predicate<ServiceRequestType> predicate = new Predicate<ServiceRequestType>() {
            @Override
            public boolean test(ServiceRequestType srt) {
                for (ServiceRequestRestriction srr : srt.getServiceRequestRestrictionsSet()) {
                    if (!srr.validate(registration)) {
                        return false;
                    }
                }
                return true;
            }

        };
        return predicate;
    }

    private boolean validate(Registration registration) {
        if (getDegreeType() != null && getDegreeType() != registration.getDegreeType()) {
            return false;
        }
        if (getDegree() != null && getDegree() != registration.getDegree()) {
            return false;
        }
        if (getProgramConclusion() != null
                && ProgramConclusion.conclusionsFor(registration).noneMatch(pc -> pc == getProgramConclusion())) {
            return false;
        }
        return true;
    }
}
