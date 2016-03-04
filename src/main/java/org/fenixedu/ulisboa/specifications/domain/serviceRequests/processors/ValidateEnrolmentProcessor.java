package org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResult;
import org.fenixedu.academic.domain.curricularRules.executors.RuleResultMessage;
import org.fenixedu.academic.domain.curricularRules.executors.ruleExecutors.CurricularRuleLevel;
import org.fenixedu.academic.domain.enrolment.EnroledCurriculumModuleWrapper;
import org.fenixedu.academic.domain.enrolment.IDegreeModuleToEvaluate;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

import pt.ist.fenixframework.Atomic;

public class ValidateEnrolmentProcessor extends ValidateEnrolmentProcessor_Base {

    protected ValidateEnrolmentProcessor() {
        super();
    }

    protected ValidateEnrolmentProcessor(final LocalizedString name, final CurricularRuleLevel curricularRuleLevel,
            final String curricularEntriesPropertyName) {
        this();
        super.init(name);
        setCurricularRuleLevel(curricularRuleLevel);
        setCurricularEntriesPropertyName(curricularEntriesPropertyName);
    }

    @Atomic
    public static ULisboaServiceRequestProcessor create(LocalizedString name, CurricularRuleLevel curricularRuleLevel,
            String curricularEntriesPropertyName) {
        return new ValidateEnrolmentProcessor(name, curricularRuleLevel, curricularEntriesPropertyName);
    }

    @Override
    public void process(ULisboaServiceRequest request) {
        if (request.isNewRequest()) {
            //TODOJN : check if slots are present
            ExecutionSemester executionSemester = request.findProperty(ULisboaConstants.EXECUTION_SEMESTER).getValue();
            StudentCurricularPlan studentCurricularPlan = request.getStudentCurricularPlan();
            EvaluationSeason evaluationSeason = request.findProperty(ULisboaConstants.EVALUATION_SEASON).getValue();
            List<ICurriculumEntry> curriculumEntries = request.findProperty(getCurricularEntriesPropertyName()).getValue();

            Set<IDegreeModuleToEvaluate> degreeModulesToEnrol = curriculumEntries.stream().map(e -> (CurriculumLine) e)
                    .map(entry -> new EnroledCurriculumModuleWrapper(entry, executionSemester)).collect(Collectors.toSet());

            RuleResult ruleResult = studentCurricularPlan.enrol(executionSemester, degreeModulesToEnrol, Collections.emptyList(),
                    getCurricularRuleLevel(), evaluationSeason);

            if (ruleResult.isWarning()) {
                StringBuilder sb = new StringBuilder();
                for (RuleResultMessage message : ruleResult.getMessages()) {
                    if (message.isToTranslate()) {
                        sb.append(BundleUtil.getString(ULisboaConstants.BUNDLE, message.getMessage(), message.getArgs()));
                    } else {
                        sb.append(message.getMessage());
                    }
                }
                //TODOJN : which exception is to throw
                throw new RuntimeException(sb.toString());
            }
        }
        // TODOJN : Unrol??
        if (request.isCancelled() || request.isRejected()) {

        }
    }
}
