package org.fenixedu.ulisboa.specifications.domain.tuitionpenalty;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;

import pt.ist.fenixframework.Atomic;

public class TuitionPenaltyConfiguration extends TuitionPenaltyConfiguration_Base {

    private TuitionPenaltyConfiguration() {
        super();
        setBennu(Bennu.getInstance());
    }

    @Atomic
    public void edit(final ServiceRequestType tuitionPenaltyServiceRequestType,
            final ServiceRequestSlot tuitionInstallmentOrderSlot,
            final ServiceRequestSlot executionYearSlot) {
        setTuitionPenaltyServiceRequestType(tuitionPenaltyServiceRequestType);
        setTuitionInstallmentOrderSlot(tuitionInstallmentOrderSlot);
        setExecutionYearSlot(executionYearSlot);
    }

    @Atomic
    public static TuitionPenaltyConfiguration getInstance() {
        if (!Bennu.getInstance().getTuitionPenaltyConfigurationsSet().isEmpty()) {
            return Bennu.getInstance().getTuitionPenaltyConfigurationsSet().iterator().next();
        }

        return new TuitionPenaltyConfiguration();
    }

}
