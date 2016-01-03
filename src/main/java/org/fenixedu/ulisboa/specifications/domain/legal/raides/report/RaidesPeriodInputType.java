package org.fenixedu.ulisboa.specifications.domain.legal.raides.report;

public enum RaidesPeriodInputType {
    ENROLLED,
    GRADUATED,
    INTERNATIONAL_MOBILITY;

    public boolean isForEnrolled() {
        return this == ENROLLED;
    }
    
    public boolean isForGraduated() {
        return this == GRADUATED;
    }
    
    public boolean isForInternationalMobility() {
        return this == INTERNATIONAL_MOBILITY;
    }
}
