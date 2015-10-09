package org.fenixedu.ulisboa.specifications.domain.serviceRequests;

import org.fenixedu.academic.domain.AcademicProgram;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.accounting.EventType;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.AcademicServiceRequestType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.serviceRequests.ITreasuryServiceRequest;

public class ULisboaServiceRequest extends ULisboaServiceRequest_Base implements ITreasuryServiceRequest {

    public ULisboaServiceRequest() {
        super();
    }

    @Override
    public boolean isToPrint() {
        // TODO Delegar no ServiceRequestType.isPrintable()
        return getServiceRequestType().getPayable();
    }

    @Override
    public Person getPerson() {
        return getRegistration().getPerson();
    }

    @Override
    public Registration getRegistration() {
        // TODO Delegar na ServiceRequestProperty correspondente.
        return null;
    }

    @Override
    public boolean hasRegistation() {
        // TODO Delegar na ServiceRequestProperty correspondente.
        return false;
    }

    @Override
    public boolean hasLanguage() {
        // TODO Delegar na ServiceRequestProperty correspondente.
        return false;
    }

    @Override
    public boolean hasNumberOfUnits() {
        // TODO Delegar na ServiceRequestProperty correspondente.
        return false;
    }

    @Override
    public boolean isUrgent() {
        // TODO Delegar na ServiceRequestProperty correspondente.
        return false;
    }

    @Override
    public boolean hasNumberOfPages() {
        // TODO Delegar na ServiceRequestProperty correspondente.
        return false;
    }

    @Override
    public CycleType getCycleType() {
        // TODO Delegar na ServiceRequestProperty correspondente.
        return null;
    }

    @Override
    public boolean hasCycleType() {
        // TODO Delegar na ServiceRequestProperty correspondente.
        return false;
    }

    @Deprecated
    @Override
    public boolean isPayedUponCreation() {
        return false;
    }

    @Deprecated
    @Override
    public boolean isPossibleToSendToOtherEntity() {
        // TODO Auto-generated method stub
        return false;
    }

    @Deprecated
    @Override
    public boolean isManagedWithRectorateSubmissionBatch() {
        // TODO Auto-generated method stub
        return false;
    }

    @Deprecated
    @Override
    public EventType getEventType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    @Override
    public AcademicServiceRequestType getAcademicServiceRequestType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    @Override
    public boolean hasPersonalInfo() {
        // TODO Auto-generated method stub
        return false;
    }

    @Deprecated
    @Override
    public AcademicProgram getAcademicProgram() {
        // TODO Auto-generated method stub
        return null;
    }

}
