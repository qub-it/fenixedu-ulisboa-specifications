package org.fenixedu.ulisboa.specifications.service.enrolment;

import java.util.Locale;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituationType;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentProcess;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

public class EnrolmentProcessService {

    //Default predicate is to NOT create/print enrolment proofs
    protected static Predicate<Object> enrolmentProofPredicate = (o) -> false;

    public static boolean isToAddEnrolmentProof() {
        return enrolmentProofPredicate.test(null);
    }

    public static void setEnrolmentProofPredicate(final Predicate<Object> predicate) {
        enrolmentProofPredicate = predicate;
    }

    @Atomic
    public static ULisboaServiceRequest createEnrolmentProof(final Registration registration, final ExecutionYear executionYear) {
        //TODO: if it is standalone enrolments
        ServiceRequestType srt = ServiceRequestType.findByCode("ENROLMENT_PROOF").findAny().orElse(null);
        ServiceRequestSlot serviceRequestSlot = ServiceRequestSlot.findByCode(ULisboaConstants.LANGUAGE).findAny().orElse(null);
        Locale locale = CoreConfiguration.supportedLocales().stream().filter(l -> l.getLanguage().toLowerCase().contains("pt"))
                .findAny().orElse(null);

        //Verify variables
        checkConstants(srt, serviceRequestSlot, locale);
        //Create request
        ULisboaServiceRequest request = ULisboaServiceRequest.create(srt, registration, false, new DateTime());
        ServiceRequestProperty.create(request, serviceRequestSlot, locale);

        //Process request
        request.addPrintVariables();
        request.transitToProcessState();
        request.generateDocument();
        request.transitToConcludedState();
        request.transitToDeliverState();

        return request;
    }

    //TODO: review this
    public static boolean isLastStep(final EnrolmentProcess process, final HttpServletRequest request) {
        String continueURL = process.getContinueURL(request);
        String afterProcessURL = process.getAfterProcessURL(request);

        return continueURL.equals(afterProcessURL);
    }

    protected static ULisboaServiceRequest getEnrolmentProof(final Registration registration, final ExecutionYear executionYear,
            final ServiceRequestType srt) {
        return registration.getULisboaServiceRequestsSet().stream()
                .filter(r -> r.getServiceRequestType() == srt && r.getExecutionYear() == executionYear)
                .filter(r -> r.getAcademicServiceRequestSituationType() == AcademicServiceRequestSituationType.DELIVERED)
                .findAny().orElse(null);
    }

    protected static void checkConstants(final Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                throw new ULisboaSpecificationsDomainException("error.EnrolmentProcessService.variable.is.null");
            }
        }
    }
}
