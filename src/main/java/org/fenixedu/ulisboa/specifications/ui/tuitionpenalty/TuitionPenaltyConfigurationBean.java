package org.fenixedu.ulisboa.specifications.ui.tuitionpenalty;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.tuitionpenalty.TuitionPenaltyConfiguration;

public class TuitionPenaltyConfigurationBean {

    private ServiceRequestType tuitionPenaltyServiceRequestType;
    private ServiceRequestSlot tuitionInstallmentOrderSlot;
    private ServiceRequestSlot executionYearSlot;
    
    public TuitionPenaltyConfigurationBean() {
    }
    
    public TuitionPenaltyConfigurationBean(final TuitionPenaltyConfiguration configuration) {
        setTuitionPenaltyServiceRequestType(configuration.getTuitionPenaltyServiceRequestType());
        setTuitionInstallmentOrderSlot(configuration.getTuitionInstallmentOrderSlot());
        setExecutionYearSlot(configuration.getExecutionYearSlot());
    }
    
    // @formatter:off
    /* *****************
     * GETTERS & SETTERS
     * *****************
     */
    // @formatter:on

    public ServiceRequestType getTuitionPenaltyServiceRequestType() {
        return tuitionPenaltyServiceRequestType;
    }
    
    public void setTuitionPenaltyServiceRequestType(ServiceRequestType tuitionPenaltyServiceRequestType) {
        this.tuitionPenaltyServiceRequestType = tuitionPenaltyServiceRequestType;
    }
    
    public ServiceRequestSlot getTuitionInstallmentOrderSlot() {
        return tuitionInstallmentOrderSlot;
    }
    
    public void setTuitionInstallmentOrderSlot(ServiceRequestSlot tuitionInstallmentOrderSlot) {
        this.tuitionInstallmentOrderSlot = tuitionInstallmentOrderSlot;
    }
    
    public ServiceRequestSlot getExecutionYearSlot() {
        return executionYearSlot;
    }
    
    public void setExecutionYearSlot(ServiceRequestSlot executionYearSlot) {
        this.executionYearSlot = executionYearSlot;
    }
    
}
