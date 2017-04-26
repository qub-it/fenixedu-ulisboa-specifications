package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituation;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;

import pt.ist.fenixframework.Atomic;

public class StateLoggerProcessor extends StateLoggerProcessor_Base {

    protected StateLoggerProcessor() {
        super();
    }

    protected StateLoggerProcessor(final LocalizedString name) {
        this();
        super.init(name);
    }

    @Override
    public void process(final ULisboaServiceRequest request, final boolean forceUpdate) {
        System.out.println("ULisboaServiceRequest id: " + request.getExternalId());
        List<AcademicServiceRequestSituation> situations = request.getAcademicServiceRequestSituationsSet().stream()
                .sorted(AcademicServiceRequestSituation.COMPARATOR_BY_MOST_RECENT_SITUATION_DATE_AND_ID)
                .collect(Collectors.toList());
        String previousSituation =
                situations.size() == 1 ? "------" : situations.get(1).getAcademicServiceRequestSituationType().getLocalizedName();
        String currentSituation = situations.get(0).getAcademicServiceRequestSituationType().getLocalizedName();
        System.out.println("Change situation from " + previousSituation + " to " + currentSituation + ".");
        System.out.println("History States :");
        request.getAcademicServiceRequestSituationsHistory().stream()
                .sorted(AcademicServiceRequestSituation.COMPARATOR_BY_MOST_RECENT_SITUATION_DATE_AND_ID)
                .forEach(situation -> System.out
                        .print(situation.getAcademicServiceRequestSituationType().getLocalizedName() + " -> "));
        System.out.println("\nFiltered States : ");
        request.getFilteredAcademicServiceRequestSituations().stream()
                .sorted(AcademicServiceRequestSituation.COMPARATOR_BY_MOST_RECENT_SITUATION_DATE_AND_ID)
                .forEach(situation -> System.out
                        .print(situation.getAcademicServiceRequestSituationType().getLocalizedName() + " -> "));
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(final LocalizedString name) {
        return new StateLoggerProcessor(name);
    }
}
