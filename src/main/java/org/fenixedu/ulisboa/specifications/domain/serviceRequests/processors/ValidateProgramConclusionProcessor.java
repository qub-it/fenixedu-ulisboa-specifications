package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;

import pt.ist.fenixframework.Atomic;

public class ValidateProgramConclusionProcessor extends ValidateProgramConclusionProcessor_Base {

    protected ValidateProgramConclusionProcessor() {
        super();
    }

    protected ValidateProgramConclusionProcessor(final LocalizedString name, final Boolean exclusiveTransation) {
        this();
        super.init(name, exclusiveTransation);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(final LocalizedString name, final Boolean exclusiveTransation) {
        return new ValidateProgramConclusionProcessor(name, exclusiveTransation);
    }

    @Override
    public void process(final ULisboaServiceRequest request, final boolean forceUpdate) {
        Registration registration = request.getRegistration();
        ProgramConclusion programConclusion = request.getProgramConclusion();
        if (request.isNewRequest() || request.isCancelled() || request.isRejected()) {
            return; // Letting the request being created or cancelled/rejected without verification.
        }
        if (programConclusion == null) {
            throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.no.programConclusion");
        }
        for (StudentCurricularPlan studentCurricularPlan : registration.getStudentCurricularPlansSet()) {
            final RegistrationConclusionBean conclusionBean =
                    new RegistrationConclusionBean(studentCurricularPlan, programConclusion);
            if (conclusionBean.isConcluded()) {
                return;
            }
        }
        // This registration is not concluded yet
        throw new ULisboaSpecificationsDomainException("error.serviceRequests.ULisboaServiceRequest.no.valid.programConclusion",
                request.getRegistration().getNumber().toString(), request.getProgramConclusion().getName().getContent());
    }
}
