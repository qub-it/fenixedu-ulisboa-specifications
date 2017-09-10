package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import java.util.List;

import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import pt.ist.fenixframework.Atomic;

public class FillExtracurricularApprovementsPropertyProcessor extends FillExtracurricularApprovementsPropertyProcessor_Base {

    public FillExtracurricularApprovementsPropertyProcessor() {
        super();
    }

    public FillExtracurricularApprovementsPropertyProcessor(final LocalizedString name, final Boolean exclusiveTransation) {
        this();
        super.init(name, exclusiveTransation);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(final LocalizedString name, final Boolean exclusiveTransation) {
        return new FillExtracurricularApprovementsPropertyProcessor(name, exclusiveTransation);
    }

    @Override
    @Atomic
    public void process(final ULisboaServiceRequest request, final boolean forceUpdate) {
        if (forceUpdate && request.hasApprovedExtraCurriculum()) {
            request.findProperty(ULisboaConstants.APPROVED_EXTRA_CURRICULUM).delete();
        }

        if (!request.hasApprovedExtraCurriculum()) {
            if (request.getRegistration() == null) {
                return;
            }
            if (request.getRegistration().getLastStudentCurricularPlan() == null) {
                return;
            }
            if (request.getRegistration().getLastStudentCurricularPlan().getExtraCurriculumGroup() == null) {
                return;
            }
            List<ICurriculumEntry> approvedExtraCurriculum =
                    ULisboaConstants.getLastPlanExtracurricularApprovements(request.getRegistration());
            ServiceRequestProperty.create(request, ServiceRequestSlot.getByCode(ULisboaConstants.APPROVED_EXTRA_CURRICULUM),
                    approvedExtraCurriculum);
        }
    }

}
