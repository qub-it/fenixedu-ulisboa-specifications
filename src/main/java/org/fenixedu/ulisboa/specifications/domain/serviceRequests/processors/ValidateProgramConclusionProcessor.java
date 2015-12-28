package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import java.util.Optional;

import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;

import pt.ist.fenixframework.Atomic;

public class ValidateProgramConclusionProcessor extends ValidateProgramConclusionProcessor_Base {

    protected ValidateProgramConclusionProcessor() {
        super();
    }

    protected ValidateProgramConclusionProcessor(final LocalizedString name) {
        this();
        super.init(name);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(LocalizedString name) {
        return new ValidateProgramConclusionProcessor(name);
    }

    @Override
    public void process(ULisboaServiceRequest request) {
        Registration registration = request.getRegistration();
        ProgramConclusion programConclusion = request.getProgramConclusion();
        if (programConclusion == null) {
            throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.no.programConclusion");
        }
        for (StudentCurricularPlan studentCurricularPlan : registration.getStudentCurricularPlansSet()) {
            final Optional<CurriculumGroup> conclusionGroup = request.getProgramConclusion().groupFor(studentCurricularPlan);
            if (conclusionGroup.isPresent() && conclusionGroup.get().isConclusionProcessed()) {
                return;
            }
        }
        //There is no conclusionGroup valid
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.no.valid.programConclusion",
                request.getRegistration().getNumber().toString(), request.getProgramConclusion().getName().getContent());
    }
}
