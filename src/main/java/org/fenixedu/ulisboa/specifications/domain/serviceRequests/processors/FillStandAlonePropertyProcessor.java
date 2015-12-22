package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import pt.ist.fenixframework.Atomic;

public class FillStandAlonePropertyProcessor extends FillStandAlonePropertyProcessor_Base {

    protected FillStandAlonePropertyProcessor() {
        super();
    }

    protected FillStandAlonePropertyProcessor(final LocalizedString name) {
        this();
        super.init(name);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(LocalizedString name) {
        return new FillStandAlonePropertyProcessor(name);
    }

    @Override
    public void process(ULisboaServiceRequest request) {
        if (!request.hasApprovedStandaloneCurriculum()) {
            List<ICurriculumEntry> enrolments = ULisboaConstants.getLastPlanStandaloneApprovements(request.getRegistration());
            ServiceRequestProperty property =
                    ServiceRequestProperty.create(ServiceRequestSlot.getByCode(ULisboaConstants.APPROVED_STANDALONE_CURRICULUM),
                            enrolments);
            request.addServiceRequestProperties(property);
        }
    }

}
