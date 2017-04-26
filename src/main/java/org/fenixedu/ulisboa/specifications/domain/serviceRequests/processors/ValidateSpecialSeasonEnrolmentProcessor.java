package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import java.util.Collections;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors.CurricularRuleLevel;
import org.fenixedu.academic.domain.curriculum.EnrolmentEvaluationContext;
import org.fenixedu.academic.domain.enrolment.EnroledCurriculumModuleWrapper;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;
import org.fenixedu.academic.dto.student.enrollment.bolonha.NoCourseGroupEnroledCurriculumModuleWrapper;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import pt.ist.fenixframework.Atomic;

public class ValidateSpecialSeasonEnrolmentProcessor extends ValidateSpecialSeasonEnrolmentProcessor_Base {

    protected ValidateSpecialSeasonEnrolmentProcessor() {
        super();
    }

    protected ValidateSpecialSeasonEnrolmentProcessor(final LocalizedString name, final EvaluationSeason evaluationSeason,
            final String curricularEntriesPropertyName) {
        this();
        super.init(name);
        setEvaluationSeason(evaluationSeason);
        setCurricularEntriesPropertyName(curricularEntriesPropertyName);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(final LocalizedString name, final EvaluationSeason evaluationSeason,
            final String curricularEntriesPropertyName) {
        return new ValidateSpecialSeasonEnrolmentProcessor(name, evaluationSeason, curricularEntriesPropertyName);
    }

    @Override
    public void process(final ULisboaServiceRequest request, final boolean forceUpdate) {
        if (request.isNewRequest()) {
            checkProcessorRules(request);
        }

        if (request.isConcluded()) {
            enrolCurricularCourse(request);
        }
    }

    private void checkProcessorRules(final ULisboaServiceRequest request) {
        ServiceRequestProperty curriculumLineSlot = request.findProperty(getCurricularEntriesPropertyName());
        ServiceRequestProperty executionSemesterSlot = request.findProperty(ULisboaConstants.EXECUTION_SEMESTER);
        ServiceRequestProperty studentCurricularPlanSlot = request.findProperty(ULisboaConstants.CURRICULAR_PLAN);

        if (curriculumLineSlot == null || curriculumLineSlot.getValue() == null
                || !(curriculumLineSlot.getValue() instanceof Enrolment)) {
            throw new ULisboaSpecificationsDomainException(
                    "error.serviceRequest.ValidateEnrolmentProcessor.curriculumLineSlot.required");
        }
        if (executionSemesterSlot == null || executionSemesterSlot.getValue() == null
                || !(executionSemesterSlot.getValue() instanceof ExecutionSemester)) {
            throw new ULisboaSpecificationsDomainException(
                    "error.serviceRequest.ValidateEnrolmentProcessor.executionSemester.required");
        }
        if (studentCurricularPlanSlot == null || studentCurricularPlanSlot.getValue() == null
                || !(studentCurricularPlanSlot.getValue() instanceof StudentCurricularPlan)) {
            throw new ULisboaSpecificationsDomainException(
                    "error.serviceRequest.ValidateEnrolmentProcessor.studentCurricularPlan.required");
        }
    }

    private void enrolCurricularCourse(final ULisboaServiceRequest request) {
        Enrolment enrolment = request.findProperty(getCurricularEntriesPropertyName()).getValue();
        IDegreeModuleToEvaluate enrolmentWrapper = null;
        ExecutionSemester executionSemester = request.findProperty(ULisboaConstants.EXECUTION_SEMESTER).getValue();
        StudentCurricularPlan studentCurricularPlan = request.getStudentCurricularPlan();
        //check if the course is valid to enrol in improvement
        if (Enrolment.getPredicateSpecialSeason()
                .fill(getEvaluationSeason(), executionSemester, EnrolmentEvaluationContext.MARK_SHEET_EVALUATION)
                .test(enrolment)) {
            if (enrolment.parentCurriculumGroupIsNoCourseGroupCurriculumGroup()) {
                enrolmentWrapper = new NoCourseGroupEnroledCurriculumModuleWrapper(enrolment, enrolment.getExecutionPeriod());
            } else {
                enrolmentWrapper = new EnroledCurriculumModuleWrapper(enrolment, enrolment.getExecutionPeriod());
            }
        }
        //enrolmentWrapper at this stage has a value because if the enrolment is not valid for special season an exception will be thrown
        studentCurricularPlan.enrol(executionSemester, Collections.singleton(enrolmentWrapper), Collections.emptyList(),
                CurricularRuleLevel.SPECIAL_SEASON_ENROLMENT, getEvaluationSeason());
    }
}
