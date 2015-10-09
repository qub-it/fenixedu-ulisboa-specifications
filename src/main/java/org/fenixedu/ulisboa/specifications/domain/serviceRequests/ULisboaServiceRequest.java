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

import java.util.Locale;
import java.util.Optional;

import org.fenixedu.academic.domain.AcademicProgram;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.accounting.EventType;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.AcademicServiceRequestType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.serviceRequests.ITreasuryServiceRequest;
import org.fenixedu.ulisboa.specifications.dto.ServiceRequestPropertyBean;
import org.fenixedu.ulisboa.specifications.dto.ULisboaServiceRequestBean;
import org.fenixedu.ulisboa.specifications.util.Constants;
import org.joda.time.DateTime;

public class ULisboaServiceRequest extends ULisboaServiceRequest_Base implements ITreasuryServiceRequest {

    protected ULisboaServiceRequest() {
        super();
    }

    protected ULisboaServiceRequest(ServiceRequestType serviceRequestType, Registration registration) {
        this();
        setRequestDate(new DateTime());
        setServiceRequestType(serviceRequestType);
        setRegistration(registration);
    }

    static public ULisboaServiceRequest createULisboaServiceRequest(ULisboaServiceRequestBean bean) {
        ULisboaServiceRequest request = new ULisboaServiceRequest(bean.getServiceRequestType(), bean.getRegistration());
        for (ServiceRequestPropertyBean propertyBean : bean.getServiceRequestPropertyBeans()) {
            ServiceRequestProperty property = ServiceRequestSlot.createProperty(propertyBean.getCode(), propertyBean.getValue());
            request.addServiceRequestProperties(property);
        }
        return request;
    }

    @Override
    protected void disconnect() {
        // TODO Ver qual o processo mais correcto para apagar/editar estas instâncias.
        //      Rever fields do ASR que possam fazer sentido utilizar aqui (e.g. AcademicServiceRequestYear)
        super.disconnect();
    }

    /*
     * REMINDER
     * Upon transition: Signal.emit(ITreasuryBridgeAPI.ACADEMIC_SERVICE_REQUEST_NEW_SITUATION_EVENT, new DomainObjectEvent<ITreasuryServiceRequest>(this));
     * When cancelling: Signal.emit(ITreasuryBridgeAPI.ACADEMIC_SERVICE_REQUEST_REJECT_OR_CANCEL_EVENT, new DomainObjectEvent<ITreasuryServiceRequest>(this));
     */

    @Override
    public boolean isToPrint() {
        // TODO Delegar no ServiceRequestType.isPrintable()
        return false;
    }

    @Override
    public Person getPerson() {
        return hasRegistation() ? getRegistration().getPerson() : null;
    }

    @Override
    public boolean hasRegistation() {
        return getRegistration() != null;
    }

    @Override
    public Locale getLanguage() {
        ServiceRequestProperty languageProperty = findProperty(Constants.LANGUAGE).orElse(null);
        return languageProperty != null ? languageProperty.getLocale() : null;
    }

    @Override
    public boolean hasLanguage() {
        return findProperty(Constants.LANGUAGE).isPresent();
    }

    @Override
    public boolean isDetailed() {
        ServiceRequestProperty detailedProperty = findProperty(Constants.IS_DETAILED).orElse(null);
        return detailedProperty != null && detailedProperty.getBooleanValue() != null ? detailedProperty.getBooleanValue() : false;
    }

    @Override
    public Integer getNumberOfUnits() {
        ServiceRequestProperty unitsProperty = findProperty(Constants.NUMBER_OF_UNITS).orElse(null);
        return unitsProperty != null ? unitsProperty.getInteger() : null;
    }

    @Override
    public boolean hasNumberOfUnits() {
        return findProperty(Constants.NUMBER_OF_UNITS).isPresent();
    }

    @Override
    public boolean isUrgent() {
        ServiceRequestProperty urgentProperty = findProperty(Constants.IS_URGENT).orElse(null);
        return urgentProperty != null && urgentProperty.getBooleanValue() != null ? urgentProperty.getBooleanValue() : false;
    }

    @Override
    public Integer getNumberOfPages() {
        ServiceRequestProperty pagesProperty = findProperty(Constants.NUMBER_OF_PAGES).orElse(null);
        return pagesProperty != null ? pagesProperty.getInteger() : null;
    }

    @Override
    public boolean hasNumberOfPages() {
        return findProperty(Constants.NUMBER_OF_PAGES).isPresent();
    }

    @Override
    public CycleType getCycleType() {
        ServiceRequestProperty cycleProperty = findProperty(Constants.CYCLE_TYPE).orElse(null);
        return cycleProperty != null ? cycleProperty.getCycleType() : null;
    }

    @Override
    public boolean hasCycleType() {
        return findProperty(Constants.CYCLE_TYPE).isPresent();
    }

    @Override
    public String getDescription() {
        return getServiceRequestType().getName().getContent();
    }

    private Optional<ServiceRequestProperty> findProperty(String slotCode) {
        return getServiceRequestPropertiesSet().stream()
                .filter(property -> property.getServiceRequestSlot().getCode().equals(slotCode)).findFirst();
    }

    /*
     * ****************
     * <Deprecated API>
     * ****************
     */
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
    /*
     * *****************
     * </Deprecated API>
     * *****************
     */

}
