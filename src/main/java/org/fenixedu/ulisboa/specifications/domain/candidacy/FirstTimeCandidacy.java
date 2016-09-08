package org.fenixedu.ulisboa.specifications.domain.candidacy;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EntryPhase;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.ShiftEnrolment;
import org.fenixedu.academic.domain.candidacy.CancelledCandidacySituation;
import org.fenixedu.academic.domain.candidacy.CandidacyOperationType;
import org.fenixedu.academic.domain.candidacy.CandidacySituation;
import org.fenixedu.academic.domain.candidacy.CandidacySituationType;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.candidacy.StandByCandidacySituation;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationStateType;
import org.fenixedu.academic.domain.util.workflow.Operation;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.DateTime;

import pt.ist.fenixframework.Atomic;

public class FirstTimeCandidacy extends FirstTimeCandidacy_Base {

    public static Comparator<StudentCandidacy> COMPARATOR_BY_DATE = new Comparator<StudentCandidacy>() {
        @Override
        public int compare(StudentCandidacy candidacy1, StudentCandidacy candidacy2) {
            return candidacy1.getCandidacyDate().compareTo(candidacy2.getCandidacyDate());
        }
    };

    public FirstTimeCandidacy(Person person, ExecutionDegree executionDegree, Person creator, Double entryGrade, String contigent,
            IngressionType ingressionType, EntryPhase entryPhase, Integer placingOption) {
        super();
        init(person, executionDegree, creator, entryGrade, contigent, ingressionType, entryPhase, placingOption);
    }

    @Override
    public String getDescription() {
        return BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy") + " - "
                + getExecutionDegree().getDegreeCurricularPlan().getName() + " - "
                + getExecutionDegree().getExecutionYear().getYear();
    }

    @Override
    protected Set<Operation> getOperations(CandidacySituation candidacySituation) {
        return Collections.emptySet();
    }

    @Override
    protected void moveToNextState(CandidacyOperationType candidacyOperationType, Person person) {
    }

    @Override
    public Map<String, Set<String>> getStateMapping() {
        return null;
    }

    @Override
    public String getDefaultState() {
        return null;
    }

    @Override
    public boolean isFirstCycleCandidacy() {
        return true;
    }

    @Override
    public boolean cancelCandidacy() {
        if (isConcluded()) {
            return false;
        }

        Registration registration = getRegistration();
        if (registration != null) {
            for (Enrolment enrolment : registration.getEnrolments(getExecutionYear())) {
                enrolment.delete();
            }

            for (ShiftEnrolment shiftEnrolment : registration.getShiftEnrolmentsSet()) {
                shiftEnrolment.delete();
            }
            registration.getShiftsSet().clear();

            if (registration.getActiveState().getStateType().equals(RegistrationStateType.INACTIVE)) {
                RegistrationState registeredState = RegistrationState.createRegistrationState(registration,
                        AccessControl.getPerson(), new DateTime(), RegistrationStateType.REGISTERED);
                registeredState.setStateDate(registeredState.getStateDate().minusMinutes(1));
            }
            if (!registration.getActiveState().getStateType().equals(RegistrationStateType.CANCELED)) {
                RegistrationState.createRegistrationState(registration, AccessControl.getPerson(), new DateTime(),
                        RegistrationStateType.CANCELED);
            }
        }

        new CancelledCandidacySituation(this);
        return true;
    }

    @Atomic
    public boolean revertCancel() {
        if (!isCancelled()) {
            return false;
        }

        Registration registration = getRegistration();
        if (registration != null) {
            RegistrationState state = registration.getActiveState();
            if (state.getStateType().equals(RegistrationStateType.CANCELED)) {
                state.delete();
            }
        }
        new StandByCandidacySituation(this);
        return true;
    }

    private boolean isCancelled() {
        return CandidacySituationType.CANCELLED.equals(getActiveCandidacySituationType());
    }
}
