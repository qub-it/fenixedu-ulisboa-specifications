package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import java.util.Optional;

import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;

import pt.ist.fenixframework.Atomic;

public class ProgramConclusionProcessor extends ProgramConclusionProcessor_Base {

    protected ProgramConclusionProcessor() {
        super();
    }

    protected ProgramConclusionProcessor(final LocalizedString name) {
        this();
        super.init(name);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(LocalizedString name) {
        return new ProgramConclusionProcessor(name);
    }

    @Override
    public void process(ULisboaServiceRequest request) {
        final Optional<CurriculumGroup> conclusionGroup = request.getProgramConclusion().groupFor(request.getRegistration());
        if (!conclusionGroup.isPresent() || !conclusionGroup.get().isConclusionProcessed()) {
            throw new ULisboaSpecificationsDomainException(
                    "error.serviceRequests.ULisboaServiceRequest.programConclusion.must.be.processed",
                    request.getProgramConclusion().getName().getContent());
        }
    }

}
