package org.fenixedu.ulisboa.specifications.ui.renderers.student.curriculum;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.curricularRules.CreditsLimit;
import org.fenixedu.academic.domain.curricularRules.CurricularRuleType;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule.ConclusionValue;
import org.fenixedu.academic.ui.renderers.student.curriculum.StudentCurricularPlanRenderer;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.evaluation.EvaluationComparator;
import org.fenixedu.ulisboa.specifications.domain.services.CurricularPeriodServices;

import pt.ist.fenixWebFramework.renderers.components.HtmlTable;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableRow;

import com.google.common.collect.Sets;

public class StudentCurricularPlanLayout extends StudentCurricularPlanRenderer.StudentCurricularPlanLayout {

    public StudentCurricularPlanLayout(StudentCurricularPlanRenderer renderer) {
        super(renderer);
    }

    public static void register() {
        StudentCurricularPlanRenderer.setLayoutProvider(renderer -> new StudentCurricularPlanLayout(renderer));
    }

    protected StringBuilder createGroupName(final String text, final CurriculumGroup curriculumGroup) {
        final StringBuilder groupName = new StringBuilder(text);
        if (curriculumGroup != null && curriculumGroup.getDegreeModule() != null) {

            final CreditsLimit creditsLimit =
                    (CreditsLimit) curriculumGroup.getMostRecentActiveCurricularRule(CurricularRuleType.CREDITS_LIMIT,
                            executionYearContext);

            if (creditsLimit != null) {
                groupName.append(" <span title=\"");
                groupName.append(BundleUtil.getString(Bundle.APPLICATION, "label.curriculum.credits.legend.minCredits"));
                groupName.append(" \">m(");
                groupName.append(creditsLimit.getMinimumCredits());
                groupName.append(")</span>,");
            }

            groupName.append(" <span title=\"");
            groupName.append(BundleUtil.getString(Bundle.APPLICATION, "label.curriculum.credits.legend.creditsConcluded"));
            groupName.append(" \"> c(");
            groupName.append(curriculumGroup.getCreditsConcluded(executionYearContext));
            groupName.append(")</span>");

            if (StudentCurricularPlanRenderer.isViewerAllowedToViewFullStudentCurriculum(studentCurricularPlan)) {
                groupName.append(" <span title=\"");
                groupName.append(BundleUtil.getString(Bundle.APPLICATION, "label.curriculum.credits.legend.approvedCredits"));
                groupName.append(" \">, ca(");
                groupName.append(curriculumGroup.getAprovedEctsCredits());
                groupName.append(")</span>");
            }

            if (creditsLimit != null) {
                groupName.append("<span title=\"");
                groupName.append(BundleUtil.getString(Bundle.APPLICATION, "label.curriculum.credits.legend.maxCredits"));
                groupName.append("\">, M(");
                groupName.append(creditsLimit.getMaximumCredits());
                groupName.append(")</span>");
            }

            if (StudentCurricularPlanRenderer.isViewerAllowedToViewFullStudentCurriculum(studentCurricularPlan)
                    && studentCurricularPlan.isBolonhaDegree() && creditsLimit != null) {

                // qubExtension, avoid strange "concluded" info when no credits are achived
                final ConclusionValue value = isConcluded(curriculumGroup, executionYearContext, creditsLimit);
                groupName.append(" <em style=\"background-color:" + getBackgroundColor(value) + "; color:" + getColor(value)
                        + "\"");
                groupName.append(">");
                groupName.append(value.getLocalizedName());
                groupName.append("</em>");
            }

        }
        return groupName;
    }

    /**
     * qubExtension, avoid strange "concluded" info when no credits are achived
     */
    static private ConclusionValue isConcluded(final CurriculumGroup group, final ExecutionYear executionYear,
            final CreditsLimit creditsRule) {

        final ConclusionValue concluded = group.isConcluded(executionYear);
        if (!concluded.value()) {
            return concluded;
        }

        if (!group.hasAnyApprovedCurriculumLines() && creditsRule != null && creditsRule.getMinimumCredits().doubleValue() == 0d) {
            return ConclusionValue.NOT_CONCLUDED;
        }

        return ConclusionValue.CONCLUDED;
    }

    protected void generateEnrolmentRow(HtmlTable mainTable, Enrolment enrolment, int level, boolean allowSelection,
            boolean isFromDetail, boolean isDismissal) {
        final HtmlTableRow enrolmentRow = mainTable.createRow();
        addTabsToRow(enrolmentRow, level);
        enrolmentRow.setClasses(renderer.getEnrolmentRowClass());

        if (enrolment.isEnroled()) {
            generateEnrolmentWithStateEnroled(enrolmentRow, enrolment, level, allowSelection);
        } else {
            generateCurricularCourseCodeAndNameCell(enrolmentRow, enrolment, level, allowSelection);
            generateDegreeCurricularPlanCell(enrolmentRow, enrolment);
            generateEnrolmentTypeCell(enrolmentRow, enrolment);
            generateEnrolmentStateCell(enrolmentRow, enrolment);
            generateEnrolmentGradeCell(enrolmentRow, enrolment);
            generateEnrolmentWeightCell(enrolmentRow, enrolment, isFromDetail);
            generateEnrolmentEctsCell(enrolmentRow, enrolment, isFromDetail);
            generateEnrolmentLastEnrolmentEvaluationTypeCell(enrolmentRow, enrolment);
            generateExecutionYearCell(enrolmentRow, enrolment);
            generateSemesterCell(enrolmentRow, enrolment);
            generateStatisticsLinkCell(enrolmentRow, enrolment);
            generateLastEnrolmentEvaluationExamDateCellIfRequired(enrolmentRow, enrolment);
            generateGradeResponsibleIfRequired(enrolmentRow, enrolment);
            generateSpacerCellsIfRequired(enrolmentRow);
        }

        if (!isDismissal && renderer.isDetailed()
                && StudentCurricularPlanRenderer.isViewerAllowedToViewFullStudentCurriculum(studentCurricularPlan)) {

            // qubExtension, show temporary evaluations for improvements and special seasons
            for (final EnrolmentEvaluation iter : getDetailedEvaluations(enrolment)) {
                generateEnrolmentEvaluationRows(mainTable, iter, level + 1);
            }
        }
    }

    /**
     * qubExtension, show temporary evaluations for improvements and special seasons
     */
    static private Set<EnrolmentEvaluation> getDetailedEvaluations(final Enrolment enrolment) {
        final Set<EnrolmentEvaluation> result = Sets.newTreeSet(new EvaluationComparator());

        for (final Iterator<EvaluationSeason> iterator = EvaluationSeason.all().sorted().iterator(); iterator.hasNext();) {
            final EvaluationSeason season = iterator.next();

            final Optional<EnrolmentEvaluation> finalEvaluation = enrolment.getFinalEnrolmentEvaluationBySeason(season);
            if (finalEvaluation.isPresent()) {
                result.add(finalEvaluation.get());

            } else if (season.isImprovement() || season.isSpecial()) {

                final EnrolmentEvaluation latestEvaluation = enrolment.getLatestEnrolmentEvaluationBySeason(season);
                if (latestEvaluation != null) {
                    result.add(latestEvaluation);
                }
            }
        }

        return result;
    }

    @Override
    protected void generateSemesterCell(final HtmlTableRow row, final ICurriculumEntry entry) {

        // qubExtension, show curricularYear
        final Integer curricularYear = getCurricularYearFor(entry);
        final String yearPart =
                curricularYear != null ? curricularYear + " " + BundleUtil.getString(Bundle.APPLICATION, "label.curricular.year")
                        + " " : "";

        final String semester;
        if (entry.hasExecutionPeriod()) {
            semester =
                    entry.getExecutionPeriod().getSemester().toString() + " "
                            + BundleUtil.getString(Bundle.APPLICATION, "label.semester.short");
        } else {
            semester = EMPTY_INFO;
        }

        generateCellWithText(row, yearPart + semester, this.renderer.getEnrolmentSemesterCellClass());
    }

    /**
     * qubExtension, show curricularYear
     */
    static private Integer getCurricularYearFor(final ICurriculumEntry entry) {

        if (!(entry instanceof CurriculumLine)) {
            return null;
        }
        return CurricularPeriodServices.getCurricularYear((CurriculumLine) entry);
    }

}
