package org.fenixedu.ulisboa.specifications.domain.candidacy;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.EnrolmentPeriod;
import org.fenixedu.academic.domain.EnrolmentPeriodInCurricularCoursesCandidate;
import org.fenixedu.academic.domain.EntryPhase;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.candidacy.CandidacyOperationType;
import org.fenixedu.academic.domain.candidacy.CandidacySituation;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.util.workflow.Operation;
import org.fenixedu.bennu.core.i18n.BundleUtil;

public class FirstTimeCandidacy extends FirstTimeCandidacy_Base {

    public static Comparator<StudentCandidacy> COMPARATOR_BY_DATE = new Comparator<StudentCandidacy>() {
        @Override
        public int compare(StudentCandidacy candidacy1, StudentCandidacy candidacy2) {
            return candidacy1.getCandidacyDate().compareTo(candidacy2.getCandidacyDate());
        }
    };

    public FirstTimeCandidacy(Person person, ExecutionDegree executionDegree, Person creator, Double entryGrade,
            String contigent, IngressionType ingressionType, EntryPhase entryPhase, Integer placingOption) {
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

    public EnrolmentPeriodInCurricularCoursesCandidate findCandidacyPeriod() {
        Predicate<EnrolmentPeriod> isForCandidates = ep -> ep instanceof EnrolmentPeriodInCurricularCoursesCandidate;
        Predicate<EnrolmentPeriod> endsAfterThisCandidacy = ep -> ep.getEndDateDateTime().isAfter(getCandidacyDate());
        Comparator<EnrolmentPeriod> periodComparatorByEndDate = new Comparator<EnrolmentPeriod>() {
            @Override
            public int compare(EnrolmentPeriod period1, EnrolmentPeriod period2) {
                return period1.getEndDateDateTime().compareTo(period2.getEndDateDateTime());
            }
        };

        Stream<EnrolmentPeriod> periods = getExecutionDegree().getDegreeCurricularPlan().getEnrolmentPeriodsSet().stream();
        periods = periods.filter(isForCandidates).filter(endsAfterThisCandidacy).sorted(periodComparatorByEndDate);
        return (EnrolmentPeriodInCurricularCoursesCandidate) periods.findFirst().orElse(null);
    }
}
