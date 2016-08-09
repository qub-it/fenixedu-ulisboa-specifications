package org.fenixedu.ulisboa.specifications.domain.tuitionpenalty.debtGeneration.strategies;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academictreasury.domain.debtGeneration.AcademicDebtGenerationRule;
import org.fenixedu.academictreasury.domain.debtGeneration.IAcademicDebtGenerationRuleStrategy;
import org.fenixedu.academictreasury.domain.event.AcademicTreasuryEvent;
import org.fenixedu.academictreasury.domain.exceptions.AcademicTreasuryDomainException;
import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.academictreasury.services.EmolumentServices;
import org.fenixedu.academictreasury.services.TuitionServices;
import org.fenixedu.treasury.domain.document.DebitEntry;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.domain.tuitionpenalty.TuitionPenaltyConfiguration;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatePenaltyTaxOnLateTuitionPaymentStrategy implements IAcademicDebtGenerationRuleStrategy {

    private static Logger logger = LoggerFactory.getLogger(CreatePenaltyTaxOnLateTuitionPaymentStrategy.class);

    @Override
    public boolean isAppliedOnAcademicTaxDebitEntries() {
        return false;
    }

    @Override
    public boolean isAppliedOnOtherDebitEntries() {
        return false;
    }

    @Override
    public boolean isAppliedOnTuitionDebitEntries() {
        return false;
    }

    @Override
    public boolean isToAggregateDebitEntries() {
        return false;
    }

    @Override
    public boolean isToCloseDebitNote() {
        return false;
    }

    @Override
    public boolean isToCreateDebitEntries() {
        return false;
    }

    @Override
    public boolean isToCreatePaymentReferenceCodes() {
        return false;
    }

    @Override
    public boolean isEntriesRequired() {
        return false;
    }

    @Override
    public void process(AcademicDebtGenerationRule rule) {

        if (!rule.isActive()) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRule.not.active.to.process");
        }

        if (TuitionPenaltyConfiguration.getInstance().getTuitionPenaltyServiceRequestType() == null) {
            return;
        }

        if (TuitionPenaltyConfiguration.getInstance().getTuitionPenaltyServiceRequestType() == null) {
            return;
        }

        for (final DegreeCurricularPlan degreeCurricularPlan : rule.getDegreeCurricularPlansSet()) {
            for (final Registration registration : degreeCurricularPlan.getRegistrations()) {

                if (registration.getStudentCurricularPlan(rule.getExecutionYear()) == null) {
                    continue;
                }

                if (!rule.getDegreeCurricularPlansSet()
                        .contains(registration.getStudentCurricularPlan(rule.getExecutionYear()).getDegreeCurricularPlan())) {
                    continue;
                }

                // Discard registrations not active and with no enrolments
                if (!registration.hasAnyActiveState(rule.getExecutionYear())) {
                    continue;
                }

                try {
                    processPenaltiesForRegistration(rule, registration);
                } catch (final AcademicTreasuryDomainException e) {
                    logger.info(e.getMessage());
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void process(final AcademicDebtGenerationRule rule, final Registration registration) {
        if (!rule.isActive()) {
            throw new AcademicTreasuryDomainException("error.AcademicDebtGenerationRule.not.active.to.process");
        }

        if (TuitionPenaltyConfiguration.getInstance().getTuitionPenaltyServiceRequestType() == null) {
            return;
        }

        if (TuitionPenaltyConfiguration.getInstance().getTuitionPenaltyServiceRequestType() == null) {
            return;
        }

        if (TuitionPenaltyConfiguration.getInstance().getExecutionYearSlot() == null) {
            return;
        }

        if (registration.getStudentCurricularPlan(rule.getExecutionYear()) == null) {
            return;
        }

        if (!rule.getDegreeCurricularPlansSet()
                .contains(registration.getStudentCurricularPlan(rule.getExecutionYear()).getDegreeCurricularPlan())) {
            return;
        }

        // Discard registrations not active and with no enrolments
        if (!registration.hasAnyActiveState(rule.getExecutionYear())) {
            return;
        }

        processPenaltiesForRegistration(rule, registration);
    }

    private void processPenaltiesForRegistration(final AcademicDebtGenerationRule rule, final Registration registration) {
        Optional<? extends AcademicTreasuryEvent> tuitionEventOptional =
                AcademicTreasuryEvent.findUniqueForRegistrationTuition(registration, rule.getExecutionYear());

        if (!tuitionEventOptional.isPresent()) {
            return;
        }

        final AcademicTreasuryEvent tuitionEvent = tuitionEventOptional.get();

        if (!tuitionEvent.isCharged()) {
            return;
        }

        outer: for (final DebitEntry debitEntry : DebitEntry.find(tuitionEvent).collect(Collectors.<DebitEntry> toSet())) {
            if (debitEntry.getProduct().getProductGroup() != AcademicTreasurySettings.getInstance().getTuitionProductGroup()) {
                continue;
            }

            if (debitEntry.getProduct().getTuitionInstallmentOrder() <= 0) {
                continue;
            }

            if (debitEntry.isInDebt()) {
                continue;
            }

            final DateTime lastPaymentDate = debitEntry.getLastPaymentDate();

            if (lastPaymentDate == null) {
                continue;
            }

            if (!debitEntry.getDueDate().isBefore(lastPaymentDate.toLocalDate())) {
                continue;
            }

            // Find academic service request for 
            final ServiceRequestType type = TuitionPenaltyConfiguration.getInstance().getTuitionPenaltyServiceRequestType();
            final ServiceRequestSlot installmentOrderSlot =
                    TuitionPenaltyConfiguration.getInstance().getTuitionInstallmentOrderSlot();
            final ServiceRequestSlot executionYearSlot = TuitionPenaltyConfiguration.getInstance().getExecutionYearSlot();

            if (type == null || installmentOrderSlot == null || executionYearSlot == null) {
                throw new RuntimeException("error");
            }

            final Set<ULisboaServiceRequest> tuitionPenaltyRequests = ULisboaServiceRequest.findByRegistration(registration)
                    .filter(s -> s.getServiceRequestType() == type && s.getExecutionYear() == rule.getExecutionYear())
                    .collect(Collectors.toSet());

            boolean isToCreateServiceRequest = false;
            for (final ULisboaServiceRequest request : tuitionPenaltyRequests) {
                for (final ServiceRequestProperty property : ServiceRequestProperty.find(request, installmentOrderSlot)
                        .collect(Collectors.toSet())) {
                    if (property.getInteger() == null) {
                        continue;
                    }

                    if (!property.getInteger().equals(debitEntry.getProduct().getTuitionInstallmentOrder())) {
                        continue;
                    }

                    // Found! Get academic treasury event to check if it is charged
                    Optional<? extends AcademicTreasuryEvent> academicServiceRequest = AcademicTreasuryEvent.findUnique(request);

                    if (!academicServiceRequest.isPresent() || !academicServiceRequest.get().isCharged()) {
                        // Charge
                        EmolumentServices.createAcademicServiceRequestEmolument(request);
                    }

                    continue outer;
                }
            }

            createPenaltyRule(rule, registration, debitEntry, type, installmentOrderSlot, executionYearSlot);
        }
    }

    private ULisboaServiceRequest createPenaltyRule(final AcademicDebtGenerationRule rule, final Registration registration,
            final DebitEntry debitEntry, final ServiceRequestType type, final ServiceRequestSlot installmentOrderSlot,
            final ServiceRequestSlot executionYearSlot) {
        final ULisboaServiceRequest serviceRequest = ULisboaServiceRequest.create(type, registration, false, new DateTime());
        ServiceRequestProperty.create(serviceRequest, installmentOrderSlot, debitEntry.getProduct().getTuitionInstallmentOrder());

        if (ServiceRequestProperty.find(serviceRequest, executionYearSlot).count() > 0) {
            ServiceRequestProperty.find(serviceRequest, executionYearSlot).iterator().next()
                    .setExecutionYear(rule.getExecutionYear());
        } else {
            ServiceRequestProperty.create(serviceRequest, executionYearSlot, rule.getExecutionYear());
        }

        return serviceRequest;
    }

}
