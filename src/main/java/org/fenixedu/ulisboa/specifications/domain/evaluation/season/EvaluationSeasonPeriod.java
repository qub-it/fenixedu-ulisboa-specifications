package org.fenixedu.ulisboa.specifications.domain.evaluation.season;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fenixedu.academic.domain.CurricularYearList;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.OccupationPeriod;
import org.fenixedu.academic.domain.OccupationPeriodReference;
import org.fenixedu.academic.domain.OccupationPeriodType;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.util.date.IntervalTools;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.YearMonthDay;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public class EvaluationSeasonPeriod extends EvaluationSeasonPeriod_Base implements Comparable<EvaluationSeasonPeriod> {

    static private Comparator<Interval> COMPARATOR_INTERVAL = new Comparator<Interval>() {
        @Override
        public int compare(Interval x, Interval y) {
            return x.getStart().compareTo(y.getStart());
        }
    };;

    protected EvaluationSeasonPeriod() {
        super();
    }

    @Atomic
    static public EvaluationSeasonPeriod create(final ExecutionSemester executionSemester,
            final EvaluationSeasonPeriodType periodType, final EvaluationSeason evaluationSeason,
            final Set<DegreeType> degreeTypes, final LocalDate start, final LocalDate end) {

        final EvaluationSeasonPeriod result = new EvaluationSeasonPeriod();
        result.setExecutionSemester(executionSemester);
        result.setSeason(evaluationSeason);
        result.setOccupationPeriod(new OccupationPeriod(IntervalTools.getInterval(start, end)));
        result.createInitialReferences(degreeTypes, periodType);
        result.checkRules();
        return result;
    }

    private void createInitialReferences(final Set<DegreeType> degreeTypes, final EvaluationSeasonPeriodType periodType) {

        final ExecutionYear executionYear = getExecutionYear();
        final List<ExecutionDegree> executionDegrees = ExecutionDegree.getAllByExecutionYearAndDegreeType(executionYear,
                degreeTypes.toArray(new DegreeType[degreeTypes.size()]));

        createReferences(Sets.newHashSet(executionDegrees), periodType);
    }

    private void createReferences(final Set<ExecutionDegree> executionDegrees, final EvaluationSeasonPeriodType periodType) {
        final int semester = getExecutionSemester().getSemester();
        final CurricularYearList all = CurricularYearList.internalize("-1");

        for (final ExecutionDegree executionDegree : executionDegrees) {

            final OccupationPeriodReference reference =
                    new OccupationPeriodReference(getOccupationPeriod(), executionDegree, periodType.translate(), semester, all);

            // remove all evaluation seasons wrongly deduced by the type given to the constructor...
            // ...and set the correct evaluation season
            reference.getEvaluationSeasonSet().clear();
            reference.addEvaluationSeason(getSeason());
        }
    }

    private void checkRules() {
        checkConsistencyExecutionDegrees();
        checkConsistencySeason();
        checkConsistencyExecutionYear();
        checkDuplicates();
    }

    private void checkConsistencyExecutionDegrees() {
        if (getExecutionDegrees().isEmpty()) {
            throw new ULisboaSpecificationsDomainException("error.EvaluationSeasonPeriod.executionDegrees.required");
        }
    }

    /**
     * All OccupationPeriodReference must have exactly one season
     */
    private void checkConsistencySeason() {
        for (final OccupationPeriodReference reference : getReferences()) {
            for (final EvaluationSeason season : reference.getEvaluationSeasonSet()) {
                if (season != getSeason()) {
                    throw new ULisboaSpecificationsDomainException("error.EvaluationSeasonPeriod.evaluationSeason.inconsistent");
                }
            }
        }
    }

    /**
     * All OccupationPeriodReference must have ExecutionDegrees of exactly one ExecutionYear
     */
    private void checkConsistencyExecutionYear() {
        for (final OccupationPeriodReference reference : getReferences()) {
            if (reference.getExecutionDegree().getExecutionYear() != getExecutionYear()) {
                throw new ULisboaSpecificationsDomainException("error.EvaluationSeasonPeriod.executionYear.inconsistent");
            }
        }
    }

    /**
     * For a given ExecutionYear and EvaluationSeasonPeriodType, one OccupationPeriod (considering all of it's Intervals) can only
     * be duplicated if the EvaluationSeason is different
     */
    private void checkDuplicates() {
        for (final EvaluationSeasonPeriod iter : findBy(getExecutionYear(), getPeriodType())) {
            if (iter != this && iter.getSeason() == getSeason()) {

                if (iter.getOccupationPeriod().isEqualTo(getOccupationPeriod())) {
                    throw new ULisboaSpecificationsDomainException("error.EvaluationSeasonPeriod.occupationPeriod.duplicate");
                }
            }
        }
    }

    @Atomic
    public void addDegree(final ExecutionDegree input) {
        final Set<ExecutionDegree> changed = getExecutionDegrees();
        changed.add(input);
        editDegrees(changed);
        checkRules();
    }

    @Atomic
    public void removeDegree(final ExecutionDegree input) {
        final Set<ExecutionDegree> changed = getExecutionDegrees();
        changed.remove(input);
        editDegrees(changed);
        checkRules();
    }

    private void editDegrees(final Set<ExecutionDegree> input) {

        // Step 1, Remove unwanted references
        final Set<OccupationPeriodReference> references = getReferences();
        for (final Iterator<OccupationPeriodReference> iterator = references.iterator(); iterator.hasNext();) {
            final OccupationPeriodReference reference = iterator.next();
            final ExecutionDegree executionDegree = reference.getExecutionDegree();

            if (input.contains(executionDegree)) {
                // nothing to be done, existing references will not be updated 
                input.remove(executionDegree);

            } else {
                iterator.remove();
                reference.delete();
            }
        }

        // Step 2, Add new references
        if (!input.isEmpty()) {
            createReferences(input, getPeriodType());
        }
    }

    @Atomic
    public void addInterval(final LocalDate start, final LocalDate end) {
        final List<Interval> changed = getIntervals();
        changed.add(IntervalTools.getInterval(start, end));
        editIntervals(changed);
        checkRules();
    }

    @Atomic
    public void removeInterval(final LocalDate start, final LocalDate end) {
        final List<Interval> changed = getIntervals();

        final Interval intervalToRemove = IntervalTools.getInterval(start, end);

        for (final Iterator<Interval> iterator = changed.iterator(); iterator.hasNext();) {
            final Interval interval = iterator.next();

            if (equals(interval, intervalToRemove)) {
                iterator.remove();
            }
        }

        if (changed.size() == getIntervals().size()) {
            throw new ULisboaSpecificationsDomainException("error.EvaluationSeasonPeriod.interval.not.found");
        }

        editIntervals(changed);
        checkRules();
    }

    static private boolean equals(final Interval o1, final Interval o2) {
        return o2.getStart().equals(o1.getStart()) && o2.getEnd().equals(o1.getEnd());
    }

    private void editIntervals(final List<Interval> input) {

        for (final Interval interval : input) {
            for (final Interval toCheck : input) {

                if (interval.equals(toCheck)) {
                    continue;
                }

                if (interval.getStart().equals(toCheck.getStart())) {
                    throw new ULisboaSpecificationsDomainException("error.EvaluationSeasonPeriod.interval.duplicate.start");
                }
            }
        }

        Collections.sort(input, COMPARATOR_INTERVAL);
        getOccupationPeriod().editDates(input.iterator());
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);
    }

    @Atomic
    public void delete() {
        editDegrees(Collections.emptySet());
        super.setExecutionSemester(null);
        super.setSeason(null);

        final OccupationPeriod occupationPeriod = getOccupationPeriod();
        super.setOccupationPeriod(null);
        occupationPeriod.delete();

        ULisboaSpecificationsDomainException.throwWhenDeleteBlocked(getDeletionBlockers());
        deleteDomainObject();
    }

    static public Set<EvaluationSeasonPeriod> findBy(final ExecutionYear executionYear,
            final EvaluationSeasonPeriodType periodType) {

        final Set<EvaluationSeasonPeriod> result = Sets.<EvaluationSeasonPeriod> newHashSet();
        if (executionYear != null && periodType != null) {

            for (final ExecutionSemester semester : executionYear.getExecutionPeriodsSet()) {
                for (final EvaluationSeasonPeriod period : semester.getEvaluationSeasonPeriodSet()) {

                    if (period.getPeriodType() == periodType) {
                        result.add(period);
                    }
                }
            }
        }

        return result;
    }

    @SuppressWarnings("deprecation")
    static public enum EvaluationSeasonPeriodType {

        GRADE_SUBMISSION {

            @Override
            protected OccupationPeriodType translate() {
                // Note: OccupationPeriodType.XPTO_SPECIAL_SEASON is never persisted
                return OccupationPeriodType.GRADE_SUBMISSION;
            }
        },

        EXAMS {

            @Override
            protected OccupationPeriodType translate() {
                // Note: OccupationPeriodType.XPTO_SPECIAL_SEASON is never persisted
                return OccupationPeriodType.EXAMS;
            }
        };

        public LocalizedString getDescriptionI18N() {
            return ULisboaSpecificationsUtil.bundleI18N(name());
        }

        abstract protected OccupationPeriodType translate();

        static protected EvaluationSeasonPeriodType get(final OccupationPeriod input) {
            switch (input.getExecutionDegreesSet().iterator().next().getPeriodType()) {

            case EXAMS:
            case EXAMS_SPECIAL_SEASON:
                return EvaluationSeasonPeriodType.EXAMS;
            case GRADE_SUBMISSION:
            case GRADE_SUBMISSION_SPECIAL_SEASON:
                return EvaluationSeasonPeriodType.GRADE_SUBMISSION;
            default:
                throw new RuntimeException();
            }
        }

    }

    public EvaluationSeasonPeriodType getPeriodType() {
        return EvaluationSeasonPeriodType.get(getOccupationPeriod());
    }

    private ExecutionYear getExecutionYear() {
        return getExecutionSemester().getExecutionYear();
    }

    static public String getIntervalsDescription(final Set<EvaluationSeasonPeriod> input) {
        final List<Interval> intervals = Lists.newLinkedList();

        for (final EvaluationSeasonPeriod period : input) {
            // TODO legidio, make sure it's unique
            intervals.addAll(period.getIntervals());
        }

        Collections.sort(intervals, COMPARATOR_INTERVAL);
        return getIntervalsDescription(intervals);
    }

    public String getIntervalsDescription() {
        return getIntervalsDescription(getIntervals());
    }

    static private String getIntervalsDescription(final List<Interval> intervals) {
        final StringBuilder result = new StringBuilder();

        for (final Iterator<Interval> iterator = intervals.iterator(); iterator.hasNext();) {

            result.append(getIntervalDescription(iterator.next()));

            if (iterator.hasNext()) {
                result.append(" ; ");
            }
        }

        return result.toString();
    }

    static private String getIntervalDescription(final Interval interval) {
        final StringBuilder result = new StringBuilder();
        final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd").withLocale(I18N.getLocale());

        result.append(formatter.print(interval.getStart()));
        result.append(" <-> ");
        result.append(formatter.print(interval.getEnd()));

        return result.toString();
    }

    @Override
    public int compareTo(final EvaluationSeasonPeriod other) {
        final OccupationPeriod o1 = this.getOccupationPeriod();
        final OccupationPeriod o2 = other.getOccupationPeriod();

        return ComparisonChain.start().compare(o1.getPeriodInterval().getStartMillis(), o2.getPeriodInterval().getStartMillis())
                .compare(o1.getExecutionDegreesSet().size(), o2.getExecutionDegreesSet().size())
                .compare(o1.getExternalId(), o2.getExternalId()).result();
    }

    public String getDegreesDescription() {
        final StringBuilder result = new StringBuilder();

        final Map<DegreeType, Set<ExecutionDegree>> mapped = Maps.<DegreeType, Set<ExecutionDegree>> newLinkedHashMap();
        getExecutionDegrees().stream()
                .sorted(ExecutionDegree.EXECUTION_DEGREE_COMPARATORY_BY_DEGREE_TYPE_AND_NAME_AND_EXECUTION_YEAR).forEach(i -> {

                    final DegreeType key = i.getDegreeType();
                    if (!mapped.containsKey(key)) {
                        mapped.put(key, Sets.<ExecutionDegree> newLinkedHashSet());
                    }

                    mapped.get(key).add(i);
                });

        for (final Iterator<Entry<DegreeType, Set<ExecutionDegree>>> iterator = mapped.entrySet().iterator(); iterator
                .hasNext();) {
            final Entry<DegreeType, Set<ExecutionDegree>> entry = iterator.next();

            int size = entry.getValue().size();
            if (size != 0) {
                result.append(size);
                result.append(" - ");
                result.append(entry.getKey().getName().getContent());

                if (iterator.hasNext()) {
                    result.append(", ");
                }
            }
        }

        return result.toString();
    }

    public Set<ExecutionDegree> getExecutionDegrees() {
        final Set<ExecutionDegree> result = Sets.<ExecutionDegree> newHashSet();

        for (final OccupationPeriodReference iter : getReferences()) {
            result.add(iter.getExecutionDegree());
        }

        return result;
    }

    private Set<OccupationPeriodReference> getReferences() {
        return getOccupationPeriod().getExecutionDegreesSet();
    }

    public List<Interval> getIntervals() {
        return getOccupationPeriod().getIntervals();
    }

    public boolean isContainingDate(final LocalDate date) {
        return getOccupationPeriod().nestedOccupationPeriodsContainsDay(new YearMonthDay(date));
    }

}
