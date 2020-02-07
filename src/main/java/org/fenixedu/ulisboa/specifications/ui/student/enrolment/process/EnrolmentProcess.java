package org.fenixedu.ulisboa.specifications.ui.student.enrolment.process;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.candidacy.CandidacySituationType;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.IBean;
import org.fenixedu.ulisboa.specifications.dto.enrolmentperiod.AcademicEnrolmentPeriodBean;
import org.fenixedu.ulisboa.specifications.ui.enrolmentRedirects.StudentPortalRedirectController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.enrolments.EnrolmentsController;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.CourseEnrolmentDA;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.EnrolmentManagementDA;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class EnrolmentProcess implements IBean {

    private ExecutionInterval executionSemester;
    private StudentCurricularPlan curricularPlan;
    private List<AcademicEnrolmentPeriodBean> enrolmentPeriods = Lists.newArrayList();

    private String beforeProcessURL;
    private String afterProcessURL;

    private final List<EnrolmentStep> steps = Lists.newArrayList();
    private EnrolmentStep currentStep;
    private EnrolmentStepTemplate lastStep = null;

    static final private List<EnrolmentStepTemplate> beginSteps = Lists.newArrayList();
    static final private List<EnrolmentStepTemplate> endSteps = Lists.newArrayList();

    private EnrolmentProcess(final ExecutionInterval executionSemester, final StudentCurricularPlan curricularPlan) {
        setExecutionSemester(executionSemester);
        setStudentCurricularPlan(curricularPlan);
    }

    private EnrolmentProcess(final AcademicEnrolmentPeriodBean input, final List<AcademicEnrolmentPeriodBean> periods) {
        this(input.getExecutionSemester(), input.getStudentCurricularPlan());
        setEnrolmentPeriods(periods);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EnrolmentProcess other = (EnrolmentProcess) obj;
        if (!getExecutionYear().equals(other.getExecutionYear())) {
            // legidio, altough we keep a semester attribute, we want to reduce processes to a year level
            return false;
        }
        if (!getStudentCurricularPlan().equals(other.getStudentCurricularPlan())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + getExecutionYear().hashCode();
        result = 31 * result + getStudentCurricularPlan().hashCode();
        return result;
    }

    public ExecutionYear getExecutionYear() {
        return getExecutionSemester().getExecutionYear();
    }

    public ExecutionInterval getExecutionSemester() {
        return executionSemester;
    }

    private void setExecutionSemester(final ExecutionInterval input) {
        this.executionSemester = input;
    }

    public Registration getRegistration() {
        return getStudentCurricularPlan().getRegistration();
    }

    public StudentCurricularPlan getStudentCurricularPlan() {
        return curricularPlan;
    }

    private StudentCurricularPlan setStudentCurricularPlan(final StudentCurricularPlan input) {
        return this.curricularPlan = input;
    }

    public List<AcademicEnrolmentPeriodBean> getEnrolmentPeriods() {
        return this.enrolmentPeriods;
    }

    private void setEnrolmentPeriods(final List<AcademicEnrolmentPeriodBean> input) {
        this.enrolmentPeriods = input.stream()
                .filter(i -> i.isOpen() && i.getStudentCurricularPlan() == getStudentCurricularPlan()
                        && i.getExecutionYear() == getExecutionYear())
                .sorted(Comparator.comparing(AcademicEnrolmentPeriodBean::getExecutionSemester)).collect(Collectors.toList());
    }

    private List<EnrolmentStep> getSteps() {
        return steps;
    }

    private void addStep(final EnrolmentStep input) {
        addSteps(Lists.newArrayList(input));
    }

    private void addSteps(final List<EnrolmentStep> input) {
        for (final EnrolmentStep iter : input) {
            if (!getSteps().contains(iter)) {
                getSteps().add(iter);
            }
        }
    }

    private void addBeginSteps() {
        addExtraSteps(getBeginSteps());
    }

    private void addEndSteps() {
        addExtraSteps(getEndSteps());
        final EnrolmentStepTemplate lastStep = getLastStep();
        if (lastStep != null) {
            addExtraSteps(Lists.newArrayList(lastStep));
        }
    }

    private void addExtraSteps(final List<EnrolmentStepTemplate> input) {
        for (final EnrolmentStepTemplate iter : input) {
            if (iter.appliesTo(this)) {
                iter.setProcess(this);

                addStep(new EnrolmentStep(iter.getDescription(), iter.getEntryPointURL()));
            }
        }
    }

    private void addExtraEnrolmentSteps() {
        // save steps from enrolment periods
        final List<EnrolmentStep> periodSteps = Lists.newArrayList(getSteps());
        getSteps().clear();

        // add extra steps
        addBeginSteps();
        addSteps(periodSteps);
        addEndSteps();
    }

    public List<String> getStepsDescriptions() {
        List<String> result = Lists.newArrayList();

        for (final EnrolmentStep iter : getSteps()) {
            result.add(iter.getDescription().getContent());
        }

        return result;
    }

    /**
     * Where process began
     */
    private String getBeforeProcessURL(final HttpServletRequest request) {
        return EnrolmentStep.prepareURL(request, this.beforeProcessURL);
    }

    private void setBeforeProcessURL(final String input) {
        this.beforeProcessURL = input;
    }

    /**
     * Where process exits
     */
    public String getAfterProcessURL(final HttpServletRequest request) {
        return EnrolmentStep.prepareURL(request, this.afterProcessURL);
    }

    private void setAfterProcessURL(final String input) {
        this.afterProcessURL = input;
    }

    /**
     * Previous button
     */
    public String getReturnURL(final HttpServletRequest request) {
        return getReturnURL(request, getCurrentStep(request));
    }

    public String getReturnURL(final HttpServletRequest request, final String currentStepUrl) {
        return getReturnURL(request, getCurrentStep(request, currentStepUrl));
    }

    private String getReturnURL(final HttpServletRequest request, final EnrolmentStep currentStep) {
        if (currentStep != null && currentStep.getPrevious() != null) {
            return EnrolmentStep.prepareURL(request, currentStep.getPrevious().getEntryPointURL());
        }

        return getBeforeProcessURL(request);
    }

    /**
     * Next button
     */
    public String getContinueURL(final HttpServletRequest request) {
        return getContinueURL(request, getCurrentStep(request));
    }

    public String getContinueURL(final HttpServletRequest request, final String currentStepUrl) {
        return getContinueURL(request, getCurrentStep(request, currentStepUrl));
    }

    private String getContinueURL(final HttpServletRequest request, final EnrolmentStep currentStep) {

        String result = null;

        if (currentStep == null) {
            // kick start
            result = getSteps().stream().map(i -> i.getEntryPointURL()).findFirst().orElse("");
            if (!Strings.isNullOrEmpty(result)) {
                result = EnrolmentStep.prepareURL(request, result);
            }

        } else if (currentStep.getNext() != null) {
            result = EnrolmentStep.prepareURL(request, currentStep.getNext().getEntryPointURL());

        } else {
            result = getAfterProcessURL(request);
        }

        if (Strings.isNullOrEmpty(result)) {
            result = getBeforeProcessURL(request);
        }

        return result;
    }

    private EnrolmentStep getCurrentStep(final HttpServletRequest request, final String currentStepUrl) {
        return getSteps().stream().filter(s -> EnrolmentStep.prepareURL(request, s.getEntryPointURL()).equals(currentStepUrl))
                .findFirst().get();
    }

    private EnrolmentStep getCurrentStep(final HttpServletRequest request) {
        if (this.currentStep == null || !this.currentStep.isRequested(request)) {

            for (final EnrolmentStep iter : getSteps()) {
                if (iter.isRequested(request)) {
                    this.currentStep = iter;
                    break;
                }
            }
        }

        return this.currentStep;
    }

    private EnrolmentStepTemplate getLastStep() {
        return lastStep;
    }

    private void setLastStep(final EnrolmentStepTemplate input) {
        this.lastStep = input;
    }

    static public EnrolmentProcess find(final ExecutionInterval interval, final StudentCurricularPlan scp) {
        EnrolmentProcess result = null;

        if (interval != null && scp != null) {

            final EnrolmentProcess indexer = new EnrolmentProcess(interval, scp);
            for (final EnrolmentProcess iter : buildProcesses(scp.getRegistration().getStudent())) {
                if (iter.equals(indexer)) {
                    result = iter;
                    break;
                }
            }
        }

        return result;
    }

    static private List<EnrolmentProcess> buildProcesses(final Student student) {
        // find enrolment periods
        final List<AcademicEnrolmentPeriodBean> periods = AcademicEnrolmentPeriodBean.getEnrolmentPeriodsOpenOrUpcoming(student);

        return buildProcesses(periods);
    }

    static public List<EnrolmentProcess> buildProcesses(final List<AcademicEnrolmentPeriodBean> periods) {
        final List<EnrolmentProcess> result = Lists.newArrayList();

        final StudentCandidacy candidacy = periods.stream()
                .map(i -> i.getStudentCurricularPlan().getRegistration().getStudentCandidacy()).findFirst().orElse(null);

        if (candidacy != null) {
            final String beforeProcessURL;
            final EnrolmentStepTemplate lastEnrolmentStep;
            final String afterProcessURL;

            final CandidacySituationType state = candidacy.getState();
            if (CandidacySituationType.REGISTERED.equals(state) || CandidacySituationType.CANCELLED.equals(state)) { // candidacy.isConcluded
                beforeProcessURL = EnrolmentManagementDA.getEntryPointURL();
                lastEnrolmentStep = EnrolmentManagementDA.createEnrolmentStepEndProcess();
                afterProcessURL = StudentPortalRedirectController.getEntryPointURL();
            } else {
                final Registration registration = candidacy.getRegistration();
                final ExecutionYear executionYear =
                        ExecutionYear.findCurrent(registration != null ? registration.getDegree().getCalendar() : null);
                beforeProcessURL = EnrolmentsController.getBackUrl(executionYear);
                afterProcessURL = EnrolmentsController.getNextUrl(executionYear);
                lastEnrolmentStep = null;
            }

            result.addAll(buildProcesses(beforeProcessURL, periods, lastEnrolmentStep, afterProcessURL));
        }

        return result;
    }

    static private List<EnrolmentProcess> buildProcesses(final String beforeProcessURL,
            final List<AcademicEnrolmentPeriodBean> periods, final EnrolmentStepTemplate lastEnrolmentStep,
            final String afterProcessURL) {

        final Set<EnrolmentProcess> builder = Sets.newHashSet();

        // essential sort
        periods.sort(Comparator.comparing(AcademicEnrolmentPeriodBean::getEnrolmentPeriodType)
                .thenComparing(AcademicEnrolmentPeriodBean::getExecutionSemester)
                .thenComparing(AcademicEnrolmentPeriodBean::getStartDate));

        // collect steps from enrolment periods
        for (final AcademicEnrolmentPeriodBean iter : periods) {
            if (iter.isOpen()) {

                // elements to add
                final EnrolmentProcess process = new EnrolmentProcess(iter, periods);
                process.setBeforeProcessURL(beforeProcessURL);
                process.setAfterProcessURL(afterProcessURL);
                process.setLastStep(lastEnrolmentStep);
                process.addStep(new EnrolmentStep(iter));

                if (builder.contains(process)) {
                    builder.stream().filter(i -> i.equals(process)).findFirst().get().addSteps(process.getSteps());
                } else {
                    builder.add(process);
                }
            }
        }

        for (final EnrolmentProcess process : builder) {
            process.addExtraEnrolmentSteps();

            // consolidate steps within processes
            for (final ListIterator<EnrolmentStep> iterator = process.getSteps().listIterator(); iterator.hasNext();) {
                final EnrolmentStep current = iterator.next();
                current.setProcess(process);

                if (iterator.hasNext()) {
                    // peek
                    final EnrolmentStep next = iterator.next();
                    current.setNext(next);
                    next.setPrevious(current);

                    // after peek, reset iterator
                    iterator.previous();
                }
            }
        }

        // sort built processes
        final List<EnrolmentProcess> result = Lists.newArrayList();
        result.addAll(builder);
        result.sort((o1, o2) -> {
            int compare = o1.getExecutionSemester().compareTo(o2.getExecutionSemester());

            if (compare == 0) {
                compare = DegreeCurricularPlan.COMPARATOR_BY_PRESENTATION_NAME.compare(
                        o1.getStudentCurricularPlan().getDegreeCurricularPlan(),
                        o2.getStudentCurricularPlan().getDegreeCurricularPlan());
            }

            return compare;
        });

        return result;
    }

    static public void init() {
        addBeginEnrolmentStep(0, CourseEnrolmentDA.createEnrolmentStepShowEnrollmentInstructions());
    }

    static final private List<EnrolmentStepTemplate> getBeginSteps() {
        return beginSteps;
    }

    static final private List<EnrolmentStepTemplate> getEndSteps() {
        return endSteps;
    }

    synchronized static final public List<EnrolmentStepTemplate> addBeginEnrolmentStep(final int index,
            final EnrolmentStepTemplate input) {
        if (!getBeginSteps().contains(input)) {
            getBeginSteps().add(index, input);
        }

        return getBeginSteps();
    }

    synchronized static final public List<EnrolmentStepTemplate> addEndEnrolmentStep(final int index,
            final EnrolmentStepTemplate input) {
        if (!getEndSteps().contains(input)) {
            getEndSteps().add(index, input);
        }

        return getEndSteps();
    }

}
