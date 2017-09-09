package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import java.util.List;

import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import pt.ist.fenixframework.Atomic;

public class FillAllPlansApprovementsPropertyProcessor extends FillAllPlansApprovementsPropertyProcessor_Base {

    protected FillAllPlansApprovementsPropertyProcessor() {
        super();
    }

    protected FillAllPlansApprovementsPropertyProcessor(final LocalizedString name) {
        this();
        super.init(name);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(final LocalizedString name) {
        return new FillAllPlansApprovementsPropertyProcessor(name);
    }

    @Override
    @Atomic
    public void process(final ULisboaServiceRequest request, final boolean forceUpdate) {
        if (forceUpdate && request.hasApprovedEnrolments()) {
            request.findProperty(ULisboaConstants.APPROVED_ENROLMENTS).delete();
        }

        if (!request.hasApprovedEnrolments()) {
            List<ICurriculumEntry> approvements = ULisboaConstants.getAllPlansApprovements(request.getRegistration());
            ServiceRequestProperty.create(request, ServiceRequestSlot.getByCode(ULisboaConstants.APPROVED_ENROLMENTS),
                    approvements);
        }
    }

}
