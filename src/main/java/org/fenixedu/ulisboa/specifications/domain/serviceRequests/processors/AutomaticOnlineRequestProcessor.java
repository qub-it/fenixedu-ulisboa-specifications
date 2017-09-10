package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;

import pt.ist.fenixframework.Atomic;

public class AutomaticOnlineRequestProcessor extends AutomaticOnlineRequestProcessor_Base {

    protected AutomaticOnlineRequestProcessor() {
        super();
    }

    protected AutomaticOnlineRequestProcessor(final LocalizedString name, final Boolean exclusiveTransation) {
        this();
        super.init(name, exclusiveTransation);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(final LocalizedString name, final Boolean exclusiveTransation) {
        return new AutomaticOnlineRequestProcessor(name, exclusiveTransation);
    }

    @Override
    public void process(final ULisboaServiceRequest request, final boolean forceUpdate) {
        if (request.isNewRequest() && request.getRequestedOnline()) {
            request.addPrintVariables();
            request.transitToProcessState();
            request.generateDocument();
            request.transitToConcludedState();
            request.transitToDeliverState();
        }
    }

}
