package org.fenixedu.ulisboa.specifications.domain.serviceRequests.validators;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituation;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;

import pt.ist.fenixframework.Atomic;

public class MockULisboaServiceRequestValidator extends MockULisboaServiceRequestValidator_Base {

    protected MockULisboaServiceRequestValidator() {
        super();
    }

    protected MockULisboaServiceRequestValidator(final LocalizedString name) {
        this();
        super.init(name);
    }

    @Override
    public boolean validate(ULisboaServiceRequest request) {
        System.out.println("ULisboaServiceRequest id: " + request.getExternalId());
        List<AcademicServiceRequestSituation> situations =
                request.getAcademicServiceRequestSituationsSet().stream()
                        .sorted(AcademicServiceRequestSituation.COMPARATOR_BY_MOST_RECENT_SITUATION_DATE_AND_ID)
                        .collect(Collectors.toList());
        System.out.println("Change situation from "
                + situations.get(1).getAcademicServiceRequestSituationType().getLocalizedName() + " to "
                + situations.get(0).getAcademicServiceRequestSituationType().getLocalizedName() + ".");
        System.out.println("History States :");
        request.getAcademicServiceRequestSituationsHistory()
                .stream()
                .forEach(
                        situation -> System.out.print(situation.getAcademicServiceRequestSituationType().getLocalizedName()
                                + " -> "));
        System.out.println("\nFiltered States : ");
        request.getFilteredAcademicServiceRequestSituations()
                .stream()
                .forEach(
                        situation -> System.out.print(situation.getAcademicServiceRequestSituationType().getLocalizedName()
                                + " -> "));
        return true;
    }

    @Atomic
    public static ULisboaServiceRequestValidator create(LocalizedString name) {
        return new MockULisboaServiceRequestValidator(name);
    }
}
