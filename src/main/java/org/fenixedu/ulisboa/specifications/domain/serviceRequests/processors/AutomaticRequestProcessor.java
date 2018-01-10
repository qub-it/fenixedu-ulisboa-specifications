package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;

import pt.ist.fenixframework.Atomic;

public class AutomaticRequestProcessor extends AutomaticRequestProcessor_Base {

    protected AutomaticRequestProcessor() {
        super();
    }

    protected AutomaticRequestProcessor(final LocalizedString name, final Boolean exclusiveTransation) {
        this();
        super.init(name, exclusiveTransation);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(final LocalizedString name, final Boolean exclusiveTransation) {
        return new AutomaticRequestProcessor(name, exclusiveTransation);
    }

    @Override
    public void process(final ULisboaServiceRequest request, final boolean forceUpdate) {
        if (request.isNewRequest()) {
            request.addPrintVariables();
            request.transitToProcessState();
            if (request.isToPrint()) {
                request.generateDocument();
            }
            request.transitToConcludedState();
            request.transitToDeliverState();
        }
    }
}
