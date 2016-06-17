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

    public FillExtracurricularApprovementsPropertyProcessor(final LocalizedString name) {
        this();
        super.init(name);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(LocalizedString name) {
        return new FillExtracurricularApprovementsPropertyProcessor(name);
    }

    @Override
    public void process(ULisboaServiceRequest request) {
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
            ServiceRequestProperty property =
                    ServiceRequestProperty.create(ServiceRequestSlot.getByCode(ULisboaConstants.APPROVED_EXTRA_CURRICULUM),
                            approvedExtraCurriculum);
            request.addServiceRequestProperties(property);
        }
    }

}
