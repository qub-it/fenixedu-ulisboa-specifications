package org.fenixedu.ulisboa.specifications.service.reports.providers;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituationType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;

import com.qubit.terra.docs.util.IDocumentFieldsData;
import com.qubit.terra.docs.util.IReportDataProvider;

public class DiplomaRequestDataProvider implements IReportDataProvider {

    protected static final String DIPLOMA_CODE = "REGISTRY_DIPLOMA_REQUEST";
    protected static final AcademicServiceRequestSituationType[] acceptedSituations =
            { AcademicServiceRequestSituationType.NEW, AcademicServiceRequestSituationType.PROCESSING,
                    AcademicServiceRequestSituationType.CONCLUDED, AcademicServiceRequestSituationType.DELIVERED };

    protected static final String KEY = "diplomaRequested";

    private final Registration registration;
    private boolean diplomaRequested;

    public DiplomaRequestDataProvider(final Registration registration) {
        this.registration = registration;
        initDiplomaRequested();
    }

    private void initDiplomaRequested() {
        if (registration == null) {
            diplomaRequested = false;
            return;
        }

        List<ULisboaServiceRequest> requests = registration.getULisboaServiceRequestsSet().stream()
                .filter(r -> r.getServiceRequestType().getCode().equals(DIPLOMA_CODE)).collect(Collectors.toList());
        requestLoop: for (ULisboaServiceRequest request : requests) {
            for (AcademicServiceRequestSituationType situationType : acceptedSituations) {
                if (request.getActiveSituation().getAcademicServiceRequestSituationType() == situationType) {
                    diplomaRequested = true;
                    break requestLoop;
                }
            }
        }
    }

    @Override
    public void registerFieldsAndImages(final IDocumentFieldsData documentFieldsData) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean handleKey(final String key) {
        return KEY.equals(key);
    }

    @Override
    public Object valueForKey(final String key) {
        if (KEY.equals(key)) {
            return diplomaRequested;
        }

        return null;
    }

}
