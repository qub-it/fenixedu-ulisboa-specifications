package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import java.util.List;

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
    public static ULisboaServiceRequestProcessor create(final LocalizedString name) {
        return new FillStandAlonePropertyProcessor(name);
    }

    @Override
    @Atomic
    public void process(final ULisboaServiceRequest request, final boolean forceUpdate) {
        if (forceUpdate && request.hasApprovedStandaloneCurriculum()) {
            request.findProperty(ULisboaConstants.APPROVED_STANDALONE_CURRICULUM).delete();
        }

        if (!request.hasApprovedStandaloneCurriculum()) {
            if (request.getRegistration() == null) {
                return;
            }
            if (request.getRegistration().getLastStudentCurricularPlan() == null) {
                return;
            }
            if (request.getRegistration().getLastStudentCurricularPlan().getStandaloneCurriculumGroup() == null) {
                return;
            }
            List<ICurriculumEntry> enrolments = ULisboaConstants.getLastPlanStandaloneApprovements(request.getRegistration());
            ServiceRequestProperty.create(request, ServiceRequestSlot.getByCode(ULisboaConstants.APPROVED_STANDALONE_CURRICULUM),
                    enrolments);
        }
    }

}
