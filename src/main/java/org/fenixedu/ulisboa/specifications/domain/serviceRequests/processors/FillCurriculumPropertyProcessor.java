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

    protected FillCurriculumPropertyProcessor(final LocalizedString name, final Boolean exclusiveTransation) {
        this();
        super.init(name, exclusiveTransation);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(final LocalizedString name, final Boolean exclusiveTransation) {
        return new FillCurriculumPropertyProcessor(name, exclusiveTransation);
    }

    @Override
    @Atomic
    public void process(final ULisboaServiceRequest request, final boolean forceUpdate) {
        if (forceUpdate && request.hasCurriculum()) {
            request.findProperty(ULisboaConstants.CURRICULUM).delete();
        }

        if (!request.hasCurriculum()) {
            List<ICurriculumEntry> curriculum =
                    ULisboaConstants.getConclusionCurriculum(request.getRegistration(), request.getProgramConclusion());
            ServiceRequestProperty.create(request, ServiceRequestSlot.getByCode(ULisboaConstants.CURRICULUM), curriculum);
        }
    }

}
