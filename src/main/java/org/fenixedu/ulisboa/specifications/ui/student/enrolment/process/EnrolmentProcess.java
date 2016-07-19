package org.fenixedu.ulisboa.specifications.ui.student.enrolment.process;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.IBean;
import org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod.AcademicEnrolmentPeriod;
import org.fenixedu.ulisboa.specifications.dto.enrolmentperiod.AcademicEnrolmentPeriodBean;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.CourseEnrolmentDA;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.EnrolmentManagementDA;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class EnrolmentProcess implements IBean {

    private ExecutionSemester executionSemester;
    private StudentCurricularPlan curricularPlan;
    private List<AcademicEnrolmentPeriodBean> enrolmentPeriods = Lists.newArrayList();

    private List<EnrolmentStep> steps = Lists.newArrayList();
    private EnrolmentStep currentStep;

    private EnrolmentProcess(final ExecutionSemester executionSemester, final StudentCurricularPlan curricularPlan) {
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

    public ExecutionSemester getExecutionSemester() {
        return executionSemester;
    }

    private void setExecutionSemester(final ExecutionSemester input) {
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

    public List<EnrolmentStep> getSteps() {
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

    private void addBeginEnrolmentSteps() {
        addExtraEnrolmentSteps(getBeginEnrolmentSteps());
    }

    private void addEndEnrolmentSteps() {
        addExtraEnrolmentSteps(getEndEnrolmentSteps());
    }

    private void addExtraEnrolmentSteps(final List<EnrolmentStepTemplate> input) {
        for (final EnrolmentStepTemplate iter : input) {
            if (iter.appliesTo(this)) {
                iter.setProcess(this);

                addStep(new EnrolmentStep(iter.getDescription(), iter.getEntryPointURL()));
            }
        }
    }

    private void addExtraEnrolmentSteps() {
        final List<EnrolmentStep> periodSteps = Lists.newArrayList(getSteps());
        getSteps().clear();

        addBeginEnrolmentSteps();
        addSteps(periodSteps);
        addEndEnrolmentSteps();
    }

    public List<String> getStepsDescriptions() {
        List<String> result = Lists.newArrayList();

        for (final EnrolmentStep iter : getSteps()) {
            result.add(iter.getDescription().getContent());
        }

        return result;
    }

    public String getReturnURL(final HttpServletRequest request) {
        String result = request.getContextPath();

        final EnrolmentStep currentStep = getCurrentStep(request);
        if (currentStep != null) {

            if (currentStep.getPrevious() != null) {
                result += currentStep.getPrevious().getEntryPointURL();

            } else {
                result += EnrolmentManagementDA.getEntryPointURL(request);
            }
        }

        return result;
    }

    public String getContinueURL(final HttpServletRequest request) {
        String result = request.getContextPath();

        final EnrolmentStep currentStep = getCurrentStep(request);
        if (currentStep == null) {

            // kick start
            result += getSteps().stream().map(i -> i.getEntryPointURL()).findFirst().orElse(null);

        } else {

            if (currentStep.getNext() != null) {
                result += currentStep.getNext().getEntryPointURL();

            } else {
                result += EnrolmentManagementDA.getExitURL(request);
            }
        }

        return result;
    }

    private EnrolmentStep getCurrentStep(final HttpServletRequest request) {
        if (this.currentStep == null || !this.currentStep.isEntryPointURL(request)) {

            for (final EnrolmentStep iter : getSteps()) {
                if (iter.isEntryPointURL(request)) {
                    this.currentStep = iter;
                    break;
                }
            }
        }

        return this.currentStep;
    }

    static public EnrolmentProcess find(final ExecutionSemester semester, final StudentCurricularPlan scp) {
        EnrolmentProcess result = null;

        final EnrolmentProcess indexer = new EnrolmentProcess(semester, scp);
        for (final EnrolmentProcess iter : buildProcesses(scp.getRegistration().getStudent())) {
            if (iter.equals(indexer)) {
                result = iter;
                break;
            }
        }

        return result;
    }

    static private List<EnrolmentProcess> buildProcesses(final Student student) {
        // find enrolment periods
        final List<AcademicEnrolmentPeriodBean> periods = AcademicEnrolmentPeriod.getEnrolmentPeriodsOpenOrUpcoming(student);

        return buildProcesses(periods);
    }

    static public List<EnrolmentProcess> buildProcesses(final List<AcademicEnrolmentPeriodBean> periods) {
        final Set<EnrolmentProcess> builder = Sets.newHashSet();

        // essential sort
        periods.removeIf(i -> !i.isOpen());
        periods.sort(Comparator.comparing(AcademicEnrolmentPeriodBean::getEnrolmentPeriodType)
                .thenComparing(AcademicEnrolmentPeriodBean::getExecutionSemester)
                .thenComparing(AcademicEnrolmentPeriodBean::getStartDate));

        // collect steps from enrolment periods
        for (final AcademicEnrolmentPeriodBean iter : periods) {
            if (iter.isOpen()) {

                // elements to add
                final EnrolmentProcess process = new EnrolmentProcess(iter, periods);
                process.addStep(new EnrolmentStep(iter));

                if (builder.contains(process)) {
                    builder.stream().filter(i -> i.equals(process)).findFirst().get().addSteps(process.getSteps());
                } else {
                    builder.add(process);
                }
            }
        }

        // consolidate steps within processes
        for (final EnrolmentProcess process : builder) {
            process.addExtraEnrolmentSteps();

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

    static final private List<EnrolmentStepTemplate> beginEnrolmentSteps = Lists.newArrayList();
    static final private List<EnrolmentStepTemplate> endEnrolmentSteps = Lists.newArrayList();

    static final public List<EnrolmentStepTemplate> getBeginEnrolmentSteps() {
        return beginEnrolmentSteps;
    }

    static final public List<EnrolmentStepTemplate> getEndEnrolmentSteps() {
        return endEnrolmentSteps;
    }

    static final public List<EnrolmentStepTemplate> addBeginEnrolmentStep(final int index, final EnrolmentStepTemplate input) {
        if (!getBeginEnrolmentSteps().contains(input)) {
            getBeginEnrolmentSteps().add(index, input);
        }

        return getBeginEnrolmentSteps();
    }

    static final public List<EnrolmentStepTemplate> addEndEnrolmentStep(final int index, final EnrolmentStepTemplate input) {
        if (!getEndEnrolmentSteps().contains(input)) {
            getEndEnrolmentSteps().add(index, input);
        }

        return getEndEnrolmentSteps();
    }

}
