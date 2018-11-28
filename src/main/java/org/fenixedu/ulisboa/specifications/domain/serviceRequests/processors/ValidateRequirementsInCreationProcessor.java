package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituationType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public class ValidateRequirementsInCreationProcessor extends ValidateRequirementsInCreationProcessor_Base {

    protected static final AcademicServiceRequestSituationType[] acceptedSituations =
            { AcademicServiceRequestSituationType.NEW, AcademicServiceRequestSituationType.PROCESSING,
                    AcademicServiceRequestSituationType.CONCLUDED, AcademicServiceRequestSituationType.DELIVERED };

    protected ValidateRequirementsInCreationProcessor() {
        super();
    }

    protected ValidateRequirementsInCreationProcessor(final LocalizedString name, final Boolean exclusiveTransation) {
        this();
        super.init(name, exclusiveTransation);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(final LocalizedString name, final Boolean exclusiveTransation) {
        return new ValidateRequirementsInCreationProcessor(name, exclusiveTransation);
    }

    public Set<String> getServiceRequestTypesNeededSet() {
        String typesNeeded = super.getServiceRequestTypesNeeded();
        if (StringUtils.isBlank(typesNeeded)) {
            return new HashSet<>();
        }

        String[] tokens = typesNeeded.split("\\|");
        return Sets.newHashSet(tokens);
    }

    public void setServiceRequestTypesNeeded(final Set<String> srtCodes) {
        Set<String> result = new HashSet<>();
        result.addAll(srtCodes);
        super.setServiceRequestTypesNeeded(result.stream().collect(Collectors.joining("|")));
    }

    public void addServiceRequestTypeNeeded(final String srtCode) {
        Set<String> result = getServiceRequestTypesNeededSet();
        result.add(srtCode);
        super.setServiceRequestTypesNeeded(result.stream().collect(Collectors.joining("|")));
    }

    public void addServiceRequestTypeNeeded(final Set<String> multipleCodesForSameSRT) {
        Set<String> result = getServiceRequestTypesNeededSet();
        result.add(multipleCodesForSameSRT.stream().collect(Collectors.joining(";")));
        super.setServiceRequestTypesNeeded(result.stream().collect(Collectors.joining("|")));
    }

    @Override
    public void setServiceRequestTypesNeeded(String serviceRequestTypesNeeded) {
        throw new RuntimeException("Use the setServiceRequestTypesNeeded(Set<String>)");
    }

    @Override
    public String getServiceRequestTypesNeeded() {
        throw new RuntimeException("Use the getServiceRequestTypesNeededSet()");
    }

    @Override
    public void process(ULisboaServiceRequest newRequest, boolean forceUpdate) {
        //Only run when the request is being created
        if (newRequest.isNewRequest()) {
            Registration registration = newRequest.getRegistration();
            Set<String> allSRTCodesNeeded = getServiceRequestTypesNeededSet().stream()
                    .flatMap(s -> Sets.newHashSet(s.split(";")).stream()).collect(Collectors.toSet());
            List<ULisboaServiceRequest> requests = registration.getULisboaServiceRequestsSet().stream()
                    .filter(r -> allSRTCodesNeeded.contains(r.getServiceRequestType().getCode())).collect(Collectors.toList());

            //Check for each code a request in a valid situation (state)
            Set<String> notValidCodes = new HashSet<>();
            for (String srtCode : getServiceRequestTypesNeededSet()) {
                Set<String> multipleNames = Sets.newHashSet(srtCode.split(";"));
                List<ULisboaServiceRequest> subList = requests.stream()
                        .filter(r -> multipleNames.contains(r.getServiceRequestType().getCode())).collect(Collectors.toList());
                boolean hasValidRequest = false;
                requestLoop: for (ULisboaServiceRequest request : subList) {
                    for (AcademicServiceRequestSituationType situationType : acceptedSituations) {
                        if (request.getActiveSituation().getAcademicServiceRequestSituationType() == situationType) {
                            hasValidRequest = true;
                            break requestLoop;
                        }
                    }
                }
                if (!hasValidRequest) {
                    String outputAllMultipleCodes = srtCode.replaceAll(";", " ou ");
                    notValidCodes.add(outputAllMultipleCodes);
                }
            }

            //Check if all types has a request associated, that is in a valid situation
            if (!notValidCodes.isEmpty()) {
                String args = notValidCodes.stream().collect(Collectors.joining(","));
                throw new RuntimeException(BundleUtil.getString(ULisboaConstants.BUNDLE,
                        "error.ValidateRequirementsInCreationProcessor.not.valid", args));
            }
        }
    }

}
