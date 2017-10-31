package org.fenixedu.ulisboa.specifications.domain.services.enrollment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.Enrolment.EnrolmentPredicate;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.OptionalEnrolment;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.curriculum.EnrollmentState;
import org.fenixedu.academic.domain.curriculum.EnrolmentEvaluationContext;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.curriculum.ConclusionProcessVersion;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule;
import org.fenixedu.academic.domain.studentCurriculum.NoCourseGroupCurriculumGroup;
import org.fenixedu.ulisboa.specifications.domain.services.PersonServices;

import pt.ist.fenixframework.dml.runtime.RelationAdapter;
import pt.ist.fenixframework.dml.runtime.RelationListener;

public class EnrolmentServices {

    static private RelationListener<DegreeModule, CurriculumModule> ON_ENROLMENT_DELETION =
            new RelationAdapter<DegreeModule, CurriculumModule>() {

                @Override
                public void beforeRemove(final DegreeModule degreeModule, final CurriculumModule module) {
                    // avoid internal invocation with null 
                    if (module == null || degreeModule == null) {
                        return;
                    }

                    if (!(module instanceof Enrolment)) {
                        return;
                    }

                    final Enrolment enrolment = (Enrolment) module;
                    removeConclusionProcessVersionsExceptLast(enrolment);
                    checkForConclusionProcessVersions(enrolment);
                }
            };

    static {
        CurriculumModule.getRelationDegreeModuleCurriculumModule().addListener(ON_ENROLMENT_DELETION);
    }

    static public String getPresentationName(final Enrolment enrolment) {
        final String code =
                !StringUtils.isEmpty(enrolment.getCurricularCourse().getCode()) ? enrolment.getCurricularCourse().getCode()
                        + " - " : "";

        if (enrolment instanceof OptionalEnrolment) {
            final OptionalEnrolment optionalEnrolment = (OptionalEnrolment) enrolment;
            return optionalEnrolment.getOptionalCurricularCourse().getNameI18N(enrolment.getExecutionPeriod()).getContent() + " ("
                    + code
                    + optionalEnrolment.getCurricularCourse().getNameI18N(optionalEnrolment.getExecutionPeriod()).getContent()
                    + ")";
        } else {
            return code + enrolment.getName().getContent();
        }
    }

    static public Collection<Shift> getShiftsFor(final Enrolment enrolment, final ExecutionSemester executionSemester) {
        return enrolment.getRegistration().getShiftsFor(enrolment.getExecutionCourseFor(executionSemester));
    }

    static public boolean containsAnyShift(final Enrolment enrolment, final ExecutionSemester executionSemester,
            final Collection<Shift> shifts) {
        return getShiftsFor(enrolment, executionSemester).stream().anyMatch(s -> shifts.contains(s));
    }

    static public String getShiftsDescription(final Enrolment enrolment, final ExecutionSemester executionSemester) {
        return getShiftsFor(enrolment, executionSemester).stream().map(s -> s.getNome()).collect(Collectors.joining(", "));
    }

    static public EnrollmentState calculateState(final Enrolment enrolment) {
        final Grade finalGrade = enrolment.getGrade();
        return finalGrade.isEmpty() ? EnrollmentState.ENROLLED : finalGrade.getEnrolmentState();
    }

    static public void updateState(final Enrolment enrolment) {
        checkForConclusionProcessVersions(enrolment);

        if (!enrolment.isAnnulled()) {
            enrolment.setEnrollmentState(calculateState(enrolment));
        }
    }

    static public void checkForConclusionProcessVersions(final Enrolment enrolment) {
        if (enrolment.isApproved() && !enrolment.getConclusionProcessVersionsSet().isEmpty()) {
            final Registration registration = enrolment.getRegistration();

            throw new DomainException("error.conclusionProcess.revertion.required",
                    "\"" + registration.getNumber() + " - " + PersonServices.getDisplayName(registration.getPerson()) + "\"",
                    "\"" + getPresentationName(enrolment) + "\"",
                    enrolment.getConclusionProcessVersionsSet().stream()
                            .map(i -> "\"" + i.getConclusionProcess().getName().getContent() + "\"").distinct()
                            .collect(Collectors.joining("; ")));
        }
    }

    static private void removeConclusionProcessVersionsExceptLast(final Enrolment enrolment) {
        for (final Iterator<ConclusionProcessVersion> iter = enrolment.getConclusionProcessVersionsSet().iterator(); iter
                .hasNext();) {
            final ConclusionProcessVersion version = iter.next();

            if (version.getConclusionProcess().getLastVersion() != version) {
                iter.remove();
            }
        }
    }

    static public List<Enrolment> getEnrolmentsToEnrol(final StudentCurricularPlan studentCurricularPlan,
            final ExecutionSemester executionSemester, final EvaluationSeason evaluationSeason,
            final EnrolmentPredicate predicate) {
        final List<Enrolment> result = new ArrayList<Enrolment>();
        //Refresh curriculum groups set
        getCurriculumGroupsToEnrol(studentCurricularPlan.getRoot());

        List<Enrolment> allEnrolments = new ArrayList<Enrolment>();
        getEnrolmentsToEnrol(studentCurricularPlan.getRoot(), allEnrolments);
        for (Enrolment enrolment : allEnrolments) {
            if (predicate.fill(evaluationSeason, executionSemester, EnrolmentEvaluationContext.MARK_SHEET_EVALUATION)
                    .testExceptionless(enrolment)) {
                result.add(enrolment);
            }
        }

        return result;

    }

    static public Set<CurriculumGroup> getCurriculumGroupsToEnrol(CurriculumGroup curriculumGroup) {
        final Set<CurriculumGroup> curriculumGroupsToEnrol = curriculumGroup.getCurriculumGroupsToEnrolmentProcess();
        if (!curriculumGroup.isNoCourseGroupCurriculumGroup()) {
            for (final NoCourseGroupCurriculumGroup group : curriculumGroup.getNoCourseGroupCurriculumGroups()) {
                if (group.isVisible()) {
                    curriculumGroupsToEnrol.add(group);
                }
            }
        }
        return curriculumGroupsToEnrol;
    }

    static public List<Enrolment> getEnrolmentsToEnrol(CurriculumGroup curriculumGroup, List<Enrolment> enrolmentList) {
        for (CurriculumModule curriculumModule : curriculumGroup.getCurriculumModulesSet()) {
            if (curriculumModule.isEnrolment()) {
                final Enrolment enrolment = (Enrolment) curriculumModule;
                enrolmentList.add(enrolment);
            }
        }

        final Set<CurriculumGroup> curriculumGroupsToEnrolmentProcess = getCurriculumGroupsToEnrol(curriculumGroup);
        for (CurriculumGroup group : curriculumGroupsToEnrolmentProcess) {
            getEnrolmentsToEnrol(group, enrolmentList);
        }
        return enrolmentList;
    }

}
