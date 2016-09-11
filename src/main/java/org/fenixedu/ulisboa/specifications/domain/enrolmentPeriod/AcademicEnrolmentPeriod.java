package org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.StatuteType;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.services.statute.StatuteServices;
import org.fenixedu.ulisboa.specifications.dto.enrolmentperiod.AcademicEnrolmentPeriodBean;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public class AcademicEnrolmentPeriod extends AcademicEnrolmentPeriod_Base {

    protected AcademicEnrolmentPeriod() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected AcademicEnrolmentPeriod(final DateTime startDate, final DateTime endDate, final Boolean firstTimeRegistration,
            final Boolean restrictToSelectedStatutes, final Boolean restrictToSelectedIngressionTypes,
            final Integer minStudentNumber, final Integer maxStudentNumber, final Integer curricularYear,
            final Boolean schoolClassSelectionMandatory, final AcademicEnrolmentPeriodType enrolmentPeriodType,
            final Boolean allowEnrolWithDebts, final AutomaticEnrolment automaticEnrolment,
            final ExecutionSemester executionSemester) {
        this();
        setStartDate(startDate);
        setEndDate(endDate);
        setFirstTimeRegistration(firstTimeRegistration);
        setRestrictToSelectedStatutes(restrictToSelectedStatutes);
        setRestrictToSelectedIngressionTypes(restrictToSelectedIngressionTypes);
        setMinStudentNumber(minStudentNumber);
        setMaxStudentNumber(maxStudentNumber);
        setCurricularYear(curricularYear);
        setSchoolClassSelectionMandatory(schoolClassSelectionMandatory);
        setEnrolmentPeriodType(enrolmentPeriodType);
        setAllowEnrolWithDebts(allowEnrolWithDebts);
        setAutomaticEnrolment(automaticEnrolment);
        setExecutionSemester(executionSemester);

        checkRules();
    }

    private void checkRules() {

        if (getStartDate() == null) {
            throw new ULisboaSpecificationsDomainException("error.AcademicEnrolmentPeriod.startDate.required");
        }

        if (getEndDate() == null) {
            throw new ULisboaSpecificationsDomainException("error.AcademicEnrolmentPeriod.endDate.required");
        }

        if (getEnrolmentPeriodType() == null) {
            throw new ULisboaSpecificationsDomainException("error.AcademicEnrolmentPeriod.enrolmentPeriodType.required");
        }

        if (getExecutionSemester() == null) {
            throw new ULisboaSpecificationsDomainException("error.AcademicEnrolmentPeriod.executionSemester.required");
        }

        if (getStartDate().isAfter(getEndDate())) {
            throw new ULisboaSpecificationsDomainException("error.AcademicEnrolmentPeriod.startDate.before.endDate");
        }

        if (getMaxStudentNumber() != null && getMinStudentNumber() != null && getMinStudentNumber() > getMaxStudentNumber()) {
            throw new ULisboaSpecificationsDomainException("error.AcademicEnrolmentPeriod.minNumber.must.be.lower.maxNumber");
        }

    }

    @Atomic
    public void edit(final DateTime startDate, final DateTime endDate, final Boolean firstTimeRegistration,
            final Boolean restrictToSelectedStatutes, final Boolean restrictToSelectedIngressionTypes,
            final Integer minStudentNumber, final Integer maxStudentNumber, final Integer curricularYear,
            final Boolean schoolClassSelectionMandatory, final AcademicEnrolmentPeriodType enrolmentPeriodType,
            final AutomaticEnrolment automaticEnrolment, final Boolean allowEnrolWithDebts,
            final ExecutionSemester executionSemester) {
        setStartDate(startDate);
        setEndDate(endDate);
        setFirstTimeRegistration(firstTimeRegistration);
        setRestrictToSelectedStatutes(restrictToSelectedStatutes);
        setRestrictToSelectedIngressionTypes(restrictToSelectedIngressionTypes);
        setMinStudentNumber(minStudentNumber);
        setMaxStudentNumber(maxStudentNumber);
        setCurricularYear(curricularYear);
        setSchoolClassSelectionMandatory(schoolClassSelectionMandatory);
        setEnrolmentPeriodType(enrolmentPeriodType);
        setAutomaticEnrolment(automaticEnrolment);
        setAllowEnrolWithDebts(allowEnrolWithDebts);
        setExecutionSemester(executionSemester);

        checkRules();
    }

    @Atomic
    public void edit(final AcademicEnrolmentPeriodBean bean) {
        edit(bean.getStartDate(), bean.getEndDate(), bean.getFirstTimeRegistration(), bean.getRestrictToSelectedStatutes(),
                bean.getRestrictToSelectedIngressionTypes(), bean.getMinStudentNumber(), bean.getMaxStudentNumber(),
                bean.getCurricularYear(), bean.getSchoolClassSelectionMandatory(), bean.getEnrolmentPeriodType(),
                bean.getAutomaticEnrolment(), bean.getAllowEnrolWithDebts(), bean.getExecutionSemester());
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);
    }

    @Atomic
    public void delete() {
        ULisboaSpecificationsDomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        setBennu(null);
        setExecutionSemester(null);
        getDegreeCurricularPlansSet().clear();
        getStatuteTypesSet().clear();

        deleteDomainObject();
    }

    public static Stream<AcademicEnrolmentPeriod> readAll() {
        return Bennu.getInstance().getAcademicEnrolmentPeriodsSet().stream();
    }

    public static Stream<AcademicEnrolmentPeriod> findByType(AcademicEnrolmentPeriodType type) {
        return readAll().filter(p -> p.getEnrolmentPeriodType() == type);
    }

    @Atomic
    public static AcademicEnrolmentPeriod create(final DateTime startDate, final DateTime endDate,
            final Boolean firstTimeRegistration, final Boolean restrictToSelectedStatutes,
            final Boolean restrictToSelectedIngressionTypes, final Integer minStudentNumber, final Integer maxStudentNumber,
            final Integer curricularYear, final Boolean schoolClassSelectionMandatory,
            final AcademicEnrolmentPeriodType enrolmentPeriodType, final Boolean allowEnrolWithDebts,
            final AutomaticEnrolment automaticEnrolment, final ExecutionSemester executionSemester) {
        AcademicEnrolmentPeriod period = new AcademicEnrolmentPeriod(startDate, endDate, firstTimeRegistration,
                restrictToSelectedStatutes, restrictToSelectedIngressionTypes, minStudentNumber, maxStudentNumber, curricularYear,
                schoolClassSelectionMandatory, enrolmentPeriodType, allowEnrolWithDebts, automaticEnrolment, executionSemester);

        return period;
    }

    @Atomic
    public static AcademicEnrolmentPeriod create(final AcademicEnrolmentPeriodBean bean) {
        return create(bean.getStartDate(), bean.getEndDate(), bean.getFirstTimeRegistration(),
                bean.getRestrictToSelectedStatutes(), bean.getRestrictToSelectedIngressionTypes(), bean.getMinStudentNumber(),
                bean.getMaxStudentNumber(), bean.getCurricularYear(), bean.getSchoolClassSelectionMandatory(),
                bean.getEnrolmentPeriodType(), bean.getAllowEnrolWithDebts(), bean.getAutomaticEnrolment(),
                bean.getExecutionSemester());
    }

    @Override
    public Boolean getSchoolClassSelectionMandatory() {
        return super.getSchoolClassSelectionMandatory() != null && super.getSchoolClassSelectionMandatory();
    }

    public ExecutionYear getExecutionYear() {
        return getExecutionSemester().getExecutionYear();
    }

    public boolean isForCurricularCourses() {
        return getEnrolmentPeriodType() == AcademicEnrolmentPeriodType.CURRICULAR_COURSE;
    }

    public boolean isForClasses() {
        return getEnrolmentPeriodType() == AcademicEnrolmentPeriodType.SCHOOL_CLASS;
    }

    public boolean isForShift() {
        return getEnrolmentPeriodType() == AcademicEnrolmentPeriodType.SHIFT;
    }

    public boolean isOpen() {
        return containsDate(new DateTime());
    }

    public boolean isUpcoming() {
        return getStartDate().isAfterNow();
    }

    public boolean isAutomatic() {
        return getAutomaticEnrolment() != null && getAutomaticEnrolment().isAutomatic();
    }

    public boolean isEditable() {
        return getAutomaticEnrolment() == null || getAutomaticEnrolment().isEditable();
    }

    private boolean containsDate(final DateTime date) {
        return !(getStartDate().isAfter(date) || getEndDate().isBefore(date));
    }

    private boolean isValidRegistration(final Registration input, final boolean skipRegistrationState) {
        if (!skipRegistrationState && !input.isActive()) {
            return false;
        }

        if (isForCurricularCourses() && !getRegistrationsToEnrolByStudent(input.getStudent()).contains(input)) {
            return false;
        }

        if (!skipRegistrationState && (isForShift() || isForClasses())
                && !input.getStudent().getRegistrationsToEnrolInShiftByStudent().contains(input)) {
            return false;
        }

        return true;
    }

    private boolean isValidStudentNumber(final Registration input) {
        final Integer number = input.getNumber();
        return (getMinStudentNumber() == null || number >= getMinStudentNumber())

                && (getMaxStudentNumber() == null || number <= getMaxStudentNumber());
    }

    /**
     * Returns the registrations that can enrol in curricular courses
     */
    private Collection<Registration> getRegistrationsToEnrolByStudent(Student student) {
        final List<Registration> result = new ArrayList<Registration>();
        for (final Registration registration : student.getRegistrationsSet()) {
            if (registration.getRegistrationProtocol().isEnrolmentByStudentAllowed()) {
                result.add(registration);
            }
        }

        return result;
    }

    /**
     * Returns StatuteTypes that grant access to this enrolment period
     */
    private boolean isValidStatuteTypes(final Set<StatuteType> input) {
        final Set<StatuteType> configured = getStatuteTypesSet();
        if (configured.isEmpty()) {
            return true;
        }

        if (getRestrictToSelectedStatutes()) {
            // mandatory
            return !Sets.intersection(input, configured).isEmpty();

        } else {
            // blocking
            return input.isEmpty() || !Sets.difference(input, configured).isEmpty();
        }
    }

    /**
     * Returns IngressionTypes that grant access to this enrolment period
     */
    private boolean isValidIngressionTypes(final IngressionType input) {
        final Set<IngressionType> configured = getIngressionTypesSet();
        if (configured.isEmpty()) {
            return true;
        }

        if (getRestrictToSelectedIngressionTypes()) {
            // mandatory
            return configured.contains(input);
        } else {
            // blocking
            return input == null || !configured.contains(input);
        }
    }

    private boolean isValidCurricularYear(final int input) {
        return getCurricularYear() == null || getCurricularYear().equals(input);
    }

    private boolean isValidFirstTimeStatus(final StudentCurricularPlan input) {
        return getFirstTimeRegistration() == null
                || getFirstTimeRegistration().equals(input.getRegistration().isFirstTime(getExecutionYear()));
    }

    private boolean isValidStudentCurricularPlan(final StudentCurricularPlan input) {
        return getDegreeCurricularPlansSet().contains(input.getDegreeCurricularPlan());
    }

    static public List<AcademicEnrolmentPeriodBean> getEnrolmentPeriodsOpenOrUpcoming(final Student student) {
        return getEnrolmentPeriodsOpenOrUpcoming(student, false,
                student.getRegistrationsSet().stream().map(i -> i.getLastDegreeCurricularPlan()).collect(Collectors.toSet()));
    }

    static public List<AcademicEnrolmentPeriodBean> getEnrolmentPeriodsOpenOrUpcoming(final Student student,
            final boolean skipRegistrationState) {
        return getEnrolmentPeriodsOpenOrUpcoming(student, skipRegistrationState,
                student.getRegistrationsSet().stream().map(i -> i.getLastDegreeCurricularPlan()).collect(Collectors.toSet()));
    }

    /**
     * Useful for checking open enrolment period of affinity cycles
     */
    static public List<AcademicEnrolmentPeriodBean> getEnrolmentPeriodsOpenOrUpcoming(final Student student,
            final DegreeCurricularPlan degreeCurricularPlan) {

        return getEnrolmentPeriodsOpenOrUpcoming(student, false, Sets.newHashSet(degreeCurricularPlan));
    }

    static public List<AcademicEnrolmentPeriodBean> getEnrolmentPeriodsOpenOrUpcoming(final Student student,
            final boolean skipRegistrationState, final DegreeCurricularPlan degreeCurricularPlan) {

        return getEnrolmentPeriodsOpenOrUpcoming(student, skipRegistrationState, Sets.newHashSet(degreeCurricularPlan));
    }

    static private List<AcademicEnrolmentPeriodBean> getEnrolmentPeriodsOpenOrUpcoming(final Student student,
            final boolean skipRegistrationState, final Set<DegreeCurricularPlan> degreeCurricularPlans) {

        final List<AcademicEnrolmentPeriodBean> result = Lists.newLinkedList();

        for (final DegreeCurricularPlan degreeCurricularPlan : degreeCurricularPlans) {
            for (final AcademicEnrolmentPeriod iter : degreeCurricularPlan.getAcademicEnrolmentPeriodsSet()) {
                if (iter.isOpen() || iter.isUpcoming()) {
                    result.addAll(iter.collectFor(degreeCurricularPlan, student, skipRegistrationState));
                }
            }
        }

        result.sort(Comparator.comparing(AcademicEnrolmentPeriodBean::getStartDate)
                .thenComparing(AcademicEnrolmentPeriodBean::getEnrolmentPeriodType));
        return result;
    }

    private Set<AcademicEnrolmentPeriodBean> collectFor(final DegreeCurricularPlan degreeCurricularPlan, final Student input,
            final boolean skipRegistrationState) {
        final Set<AcademicEnrolmentPeriodBean> result = Sets.newHashSet();

        input.getRegistrationsFor(degreeCurricularPlan).stream()
                .forEach(i -> result.addAll(collectFor(i, skipRegistrationState)));

        return result;
    }

    private Set<AcademicEnrolmentPeriodBean> collectFor(final Registration input, final boolean skipRegistrationState) {
        final Set<AcademicEnrolmentPeriodBean> result = Sets.newHashSet();

        if (!isValidRegistration(input, skipRegistrationState)) {
            return result;
        }

        if (!isValidStudentNumber(input)) {
            return result;
        }

        final Set<StatuteType> studentStatutes = Sets.newHashSet(StatuteServices.findStatuteTypes(input, getExecutionSemester()));
        if (!isValidStatuteTypes(studentStatutes)) {
            return result;
        }

        if (!isValidIngressionTypes(input.getIngressionType())) {
            return result;
        }

        final int studentCurricularYear = input.getCurricularYear(getExecutionYear());
        if (!isValidCurricularYear(studentCurricularYear)) {
            return result;
        }

        final StudentCurricularPlan studentCurricularPlan = input.getLastStudentCurricularPlan();
        if (!isValidFirstTimeStatus(studentCurricularPlan)) {
            return result;
        }

        if (!isValidStudentCurricularPlan(studentCurricularPlan)) {
            return result;
        }

        final AcademicEnrolmentPeriodBean bean = new AcademicEnrolmentPeriodBean(this);
        bean.setStudentCurricularPlan(studentCurricularPlan);
        bean.setStudentStatuteTypes(studentStatutes);
        bean.setStudentIngressionType(input.getIngressionType());
        bean.setCurricularYear(studentCurricularYear);
        result.add(bean);

        return result;
    }

}
