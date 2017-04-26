package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;

import pt.ist.fenixframework.Atomic;

public class AutomaticOnlineRequestProcessor extends AutomaticOnlineRequestProcessor_Base {

    protected AutomaticOnlineRequestProcessor() {
        super();
    }

    protected AutomaticOnlineRequestProcessor(final LocalizedString name) {
        this();
        super.init(name);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(final LocalizedString name) {
        return new AutomaticOnlineRequestProcessor(name);
    }

    @Override
    public void process(final ULisboaServiceRequest request, final boolean forceUpdate) {
        if (request.isNewRequest() && request.getRequestedOnline()) {
            request.transitToProcessState();
            request.generateDocument();
            request.transitToConcludedState();
            request.transitToDeliverState();
        }
    }

}
