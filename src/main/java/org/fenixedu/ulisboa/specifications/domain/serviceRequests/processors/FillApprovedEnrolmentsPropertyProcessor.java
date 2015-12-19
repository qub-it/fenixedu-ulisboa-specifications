package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

public class FillApprovedEnrolmentsPropertyProcessor extends FillApprovedEnrolmentsPropertyProcessor_Base {

    protected FillApprovedEnrolmentsPropertyProcessor() {
        super();
    }

    protected FillApprovedEnrolmentsPropertyProcessor(final LocalizedString name) {
        this();
        super.init(name);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(LocalizedString name) {
        return new FillApprovedEnrolmentsPropertyProcessor(name);
    }

    @Override
    public void process(ULisboaServiceRequest request) {
        if (!request.hasApprovedEnrolments()) {
            List<ICurriculumEntry> enrolments =
                    request.getRegistration().getLastStudentCurricularPlan().getCurriculum(new DateTime(), null)
                            .getCurriculumEntries().stream().filter(e -> e instanceof Enrolment).collect(Collectors.toList());
            ServiceRequestProperty property =
                    ServiceRequestProperty.create(ServiceRequestSlot.getByCode(ULisboaConstants.APPROVED_ENROLMENTS), enrolments);
            request.addServiceRequestProperties(property);
        }
    }

}
