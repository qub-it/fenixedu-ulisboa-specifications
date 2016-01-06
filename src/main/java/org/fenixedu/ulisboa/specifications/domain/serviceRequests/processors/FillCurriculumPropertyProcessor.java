package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import java.util.List;

import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import pt.ist.fenixframework.Atomic;

public class FillCurriculumPropertyProcessor extends FillCurriculumPropertyProcessor_Base {

    protected FillCurriculumPropertyProcessor() {
        super();
    }

    protected FillCurriculumPropertyProcessor(final LocalizedString name) {
        this();
        super.init(name);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(LocalizedString name) {
        return new FillCurriculumPropertyProcessor(name);
    }

    @Override
    public void process(ULisboaServiceRequest request) {
        if (!request.hasApprovedEnrolments()) {
            List<ICurriculumEntry> curriculum =
                    ULisboaConstants.getConclusionCurriculum(request.getRegistration(), request.getProgramConclusion());
            ServiceRequestProperty property =
                    ServiceRequestProperty.create(ServiceRequestSlot.getByCode(ULisboaConstants.CURRICULUM), curriculum);
            request.addServiceRequestProperties(property);
        }
    }

}
