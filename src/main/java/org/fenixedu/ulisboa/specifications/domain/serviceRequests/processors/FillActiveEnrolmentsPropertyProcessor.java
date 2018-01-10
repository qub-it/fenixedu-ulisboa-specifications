package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import java.util.List;

import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import pt.ist.fenixframework.Atomic;

public class FillActiveEnrolmentsPropertyProcessor extends FillActiveEnrolmentsPropertyProcessor_Base {

    protected FillActiveEnrolmentsPropertyProcessor() {
        super();
    }

    protected FillActiveEnrolmentsPropertyProcessor(final LocalizedString name, final Boolean exclusiveTransation) {
        this();
        super.init(name, exclusiveTransation);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(final LocalizedString name, final Boolean exclusiveTransation) {
        return new FillActiveEnrolmentsPropertyProcessor(name, exclusiveTransation);
    }

    @Override
    @Atomic
    public void process(final ULisboaServiceRequest request, final boolean forceUpdate) {
        if (forceUpdate && request.findProperty(ULisboaConstants.ACTIVE_ENROLMENTS) != null) {
            request.findProperty(ULisboaConstants.ACTIVE_ENROLMENTS).delete();
        }

        if (request.findProperty(ULisboaConstants.ACTIVE_ENROLMENTS) == null) {
            if (request.getRegistration() == null) {
                return;
            }
            if (request.getRegistration().getLastStudentCurricularPlan() == null) {
                return;
            }

            List<CurriculumLine> enrolments = ULisboaConstants.getEnrolmentsInEnrolledState(request.getRegistration());
            ServiceRequestProperty.create(request, ServiceRequestSlot.getByCode(ULisboaConstants.ACTIVE_ENROLMENTS), enrolments);
        }
    }
}
