/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: diogo.simoes@qub-it.com
 *               jnpa@reitoria.ulisboa.pt
 *
 * 
 * This file is part of FenixEdu QubDocs.
 *
 * FenixEdu QubDocs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu QubDocs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu QubDocs.  If not, see <http://www.gnu.org/licenses/>.
 */

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

    /*
     * REMINDER
     * Upon transition: Signal.emit(ITreasuryBridgeAPI.ACADEMIC_SERVICE_REQUEST_NEW_SITUATION_EVENT, new DomainObjectEvent<ITreasuryServiceRequest>(this));
     * When cancelling: Signal.emit(ITreasuryBridgeAPI.ACADEMIC_SERVICE_REQUEST_REJECT_OR_CANCEL_EVENT, new DomainObjectEvent<ITreasuryServiceRequest>(this));
     */

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
