/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 *
 * 
 * This file is part of FenixEdu fenixedu-ulisboa-specifications.
 *
 * FenixEdu Specifications is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Specifications is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Specifications.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.ulisboa.specifications.ui.renderers.student.curriculum;

import java.text.Collator;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.DomainObjectUtil;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.Evaluation;
import org.fenixedu.academic.domain.EvaluationConfiguration;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.IEnrolment;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.accessControl.AcademicAuthorizationGroup;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.academic.domain.curricularRules.CreditsLimit;
import org.fenixedu.academic.domain.curricularRules.CurricularRuleType;
import org.fenixedu.academic.domain.curriculum.EnrollmentState;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumModule.ConclusionValue;
import org.fenixedu.academic.domain.studentCurriculum.Dismissal;
import org.fenixedu.academic.domain.studentCurriculum.ExternalEnrolment;
import org.fenixedu.academic.ui.renderers.student.curriculum.StudentCurricularPlanRenderer;
import org.fenixedu.academic.ui.renderers.student.enrollment.bolonha.EnrolmentLayout;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.ulisboa.specifications.domain.evaluation.EvaluationServices;
import org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheet;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices;
import org.fenixedu.ulisboa.specifications.domain.services.CurricularPeriodServices;
import org.fenixedu.ulisboa.specifications.domain.services.CurriculumLineServices;
import org.fenixedu.ulisboa.specifications.domain.services.PersonServices;
import org.fenixedu.ulisboa.specifications.domain.services.enrollment.EnrolmentServices;
import org.fenixedu.ulisboa.specifications.domain.services.evaluation.EnrolmentEvaluationServices;
import org.fenixedu.ulisboa.specifications.ui.evaluation.managemarksheet.administrative.CompetenceCourseMarkSheetController;
import org.fenixedu.ulisboa.specifications.ui.renderers.student.curriculum.StudentCurricularPlanRenderer.DetailedType;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.LocalDate;
import org.joda.time.YearMonthDay;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlCheckBox;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlForm;
import pt.ist.fenixWebFramework.renderers.components.HtmlImage;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlLink;
import pt.ist.fenixWebFramework.renderers.components.HtmlTable;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableCell;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableRow;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.contexts.InputContext;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;
import pt.ist.fenixframework.DomainObject;

/**
 * @see {@link org.fenixedu.academic.ui.renderers.student.curriculum.StudentCurricularPlanRenderer.StudentCurricularPlanLayout}
 */
public class StudentCurricularPlanLayout extends Layout {

    static private String i18n(final String key) {
        return ULisboaSpecificationsUtil.bundle(key);
    }

    static private String i18n(final String bundle, final String key, final String... args) {
        return BundleUtil.getString(bundle, key, args);
    }

    // qubExtension, don't SHOW empty groups
    private Map<CurriculumGroup, Boolean> emptyGroups = Maps.newHashMap();
    private boolean emptyGroupsCollapsible = true;

    // qubExtension
    static final private String MINIMUM_CREDITS_CONCLUDED_IN_CURRICULUM_GROUP =
            "margin: 1em 0; padding: 0.2em 0.5em 0.2em 0.5em; background-color: #fbf8cc; color: #805500;";

    // qubExtension
    static final private String CURRICULUM_GROUP_CONCLUDED =
            "margin: 1em 0; padding: 0.2em 0.5em 0.2em 0.5em; background-color: #76f576; color: #146e14;";

    // qubExtension
    static final private String GRADE_APPROVED_STYLE = "width: 45px; background-color: #76f576; color: #146e14;";

    // qubExtension
    static final private String GRADE_NOT_APPROVED_STYLE = "width: 45px; background-color: #f58787; color: #6e1414;";

    static final private String GRADE_EMPTY_STYLE = "width: 45px; background-color: #d9d7d7;";

    static final private String EMPTY_INFO = "-";

    static final private String EMPTY_SPACE = " ";

    // qubExtension
    static final private String EMPTY_WIDTH = "width: 16px;";

    // qubExtension
    static final private String TOOLTIP_DOT = " border-bottom: 1px dotted #777";

    protected static final String SPACER_IMAGE_PATH = "/images/scp_spacer.gif";

    protected static final int MAX_LINE_SIZE = 26;

    protected static final int MAX_COL_SPAN_FOR_TEXT_ON_GROUPS_WITH_CHILDS = 17;

    // qubExtension, show shifts
    protected static final int MAX_COL_SPAN_FOR_TEXT_ON_CURRICULUM_LINES = 14 - 1;

    protected static final int HEADERS_SIZE = 3;

    // qubExtension, show shifts
    protected static final int COLUMNS_BETWEEN_TEXT_AND_GRADE = 3 + 1;

    // qubExtension, show shifts
    protected static final int COLUMNS_BETWEEN_TEXT_AND_ECTS = 5 + 1;

    protected static final int COLUMNS_FROM_ECTS_AND_ENROLMENT_EVALUATION_DATE = 4;

    protected static final int COLUMNS_BETWEEN_TEXT_AND_ENROLMENT_EVALUATION_DATE =
            COLUMNS_BETWEEN_TEXT_AND_ECTS + COLUMNS_FROM_ECTS_AND_ENROLMENT_EVALUATION_DATE;

    protected static final int LATEST_ENROLMENT_EVALUATION_COLUMNS = 3;

    protected static final String DATE_FORMAT = "yyyy-MM-dd";

    protected static final int GRADE_NEXT_COLUMN_SPAN = 3;

    private static Function<Enrolment, String> ENROLMENT_SEASON_EXTRA_INFORMATION_PROVIDER = (x) -> "";

    static final private String EVALUATION_DATE_LABEL = i18n(Bundle.APPLICATION, "label.data.avaliacao");

    static final private String AVAILABLE_DATE_LABEL = i18n("label.LooseEvaluationBean.availableDate") + " ";

    protected StudentCurricularPlan studentCurricularPlan;

    protected ExecutionYear executionYearContext;

    protected ExecutionSemester executionPeriodContext;

    protected StudentCurricularPlanRenderer renderer;

    public StudentCurricularPlanLayout(StudentCurricularPlanRenderer renderer) {
        this.renderer = renderer;
    }

    // qubExtension
    public static void register() {
        StudentCurricularPlanRenderer.setLayoutProvider(renderer -> new StudentCurricularPlanLayout(renderer));
    }

    // qubExtension
    private static boolean isViewerAllowedToViewFullStudentCurriculum(final StudentCurricularPlan studentCurricularPlan) {
        return StudentCurricularPlanRenderer.isViewerAllowedToViewFullStudentCurriculum(studentCurricularPlan);
    }

    // qubExtension
    public static void setEnrolmentSeasonExtraInformationProvider(Function<Enrolment, String> provider) {
        ENROLMENT_SEASON_EXTRA_INFORMATION_PROVIDER = provider;
    }

    // qubExtension
    private StudentCurricularPlan getPlan() {
        return this.studentCurricularPlan;
    }

    // qubExtension
    private ExecutionSemester getExecutionSemester() {
        return this.executionPeriodContext;
    }

    @Override
    public HtmlComponent createComponent(Object object, Class type) {
        final InputContext inputContext = renderer.getInputContext();
        final HtmlForm htmlForm = inputContext.getForm();
        htmlForm.getSubmitButton().setVisible(false);
        htmlForm.getCancelButton().setVisible(false);

        this.studentCurricularPlan = (StudentCurricularPlan) object;

        final HtmlContainer container = new HtmlBlockContainer();
        if (getPlan() == null) {
            container.addChild(createHtmlTextItalic(i18n(Bundle.STUDENT, "message.no.curricularplan")));

            return container;
        }

        this.executionYearContext = initializeExecutionYear();
        this.executionPeriodContext = executionYearContext.getLastExecutionPeriod();

        final HtmlTable mainTable = new HtmlTable();
        container.addChild(mainTable);
        mainTable.setClasses(renderer.getStudentCurricularPlanClass());

        if (renderer.isOrganizedByGroups()) {
            generateRowsForGroupsOrganization(mainTable, getPlan().getRoot(), 0);
        } else if (renderer.isOrganizedByExecutionYears()) {
            generateRowsForExecutionYearsOrganization(mainTable);
        } else {
            generateRowsForCurricularYearsOrganization(mainTable);
        }

        return container;
    }

    protected ExecutionYear initializeExecutionYear() {

        if (!studentCurricularPlan.getRegistration().hasConcluded()) {
            return ExecutionYear.readCurrentExecutionYear();
        }

        final ExecutionYear lastApprovementExecutionYear = studentCurricularPlan.getLastApprovementExecutionYear();
        if (lastApprovementExecutionYear != null) {
            return lastApprovementExecutionYear;
        }

        final ExecutionYear lastSCPExecutionYear = studentCurricularPlan.getLastExecutionYear();
        if (lastSCPExecutionYear != null) {
            return lastSCPExecutionYear;
        }

        return ExecutionYear.readCurrentExecutionYear();
    }

    protected void generateRowsForExecutionYearsOrganization(HtmlTable mainTable) {
        final Map<ExecutionSemester, Set<CurriculumLine>> collected =
                Maps.newTreeMap(ExecutionSemester.COMPARATOR_BY_SEMESTER_AND_YEAR.reversed());

        for (final CurriculumLine iter : getPlan().getAllCurriculumLines()) {
            if (isToShow(iter)) {

                final ExecutionSemester key = iter.getExecutionPeriod();
                if (key != null) {

                    Set<CurriculumLine> set = collected.get(key);
                    if (set == null) {
                        set = Sets.newTreeSet(CurriculumLineServices.COMPARATOR);
                    }
                    set.add(iter);
                    collected.put(key, set);
                }
            }
        }

        // qubExtension
        final int level = 1;
        generateGroupRowWithText(mainTable, getPresentationNameFor(getPlan().getRoot()), false, 0, (CurriculumGroup) null);

        for (final Entry<ExecutionSemester, Set<CurriculumLine>> entry : collected.entrySet()) {
            if (!entry.getValue().isEmpty()) {

                final ExecutionSemester semester = entry.getKey();
                generateGroupRowWithText(mainTable, semester.getYear() + ", " + semester.getName(), true, level,
                        (CurriculumGroup) null);

                for (final CurriculumLine iter : entry.getValue()) {
                    if (iter instanceof Enrolment) {
                        generateEnrolmentRow(mainTable, (Enrolment) iter, level);
                    }
                    if (iter instanceof Dismissal) {
                        generateDismissalRow(mainTable, (Dismissal) iter, level);
                    }
                }
            }
        }
    }

    /**
     * qubExtension, Curricular Years organization
     */
    protected void generateRowsForCurricularYearsOrganization(final HtmlTable mainTable) {
        final Map<String, Set<CurriculumLine>> collected = Maps.newTreeMap(Collections.reverseOrder());

        for (final CurriculumLine iter : getPlan().getAllCurriculumLines()) {
            if (isToShow(iter)) {

                final String key = iter instanceof Enrolment ? getCurricularPeriodLabel(
                        (Enrolment) iter) : getCurricularPeriodLabel((Dismissal) iter);
                if (!Strings.isNullOrEmpty(key)) {

                    Set<CurriculumLine> set = collected.get(key);
                    if (set == null) {
                        set = Sets.newTreeSet(CurriculumLineServices.COMPARATOR);
                    }
                    set.add(iter);
                    collected.put(key, set);
                }
            }
        }

        // qubExtension
        final int level = 1;
        generateGroupRowWithText(mainTable, getPresentationNameFor(getPlan().getRoot()), false, 0, (CurriculumGroup) null);

        for (final Entry<String, Set<CurriculumLine>> entry : collected.entrySet()) {
            if (!entry.getValue().isEmpty()) {

                generateGroupRowWithText(mainTable, entry.getKey(), true, level, (CurriculumGroup) null);

                for (final CurriculumLine iter : entry.getValue()) {
                    if (iter instanceof Enrolment) {
                        generateEnrolmentRow(mainTable, (Enrolment) iter, level);
                    }
                    if (iter instanceof Dismissal) {
                        generateDismissalRow(mainTable, (Dismissal) iter, level);
                    }
                }
            }
        }
    }

    protected HtmlText createHtmlTextItalic(final String message) {
        final HtmlText htmlText = new HtmlText(message);
        htmlText.setClasses("italic");

        return htmlText;
    }

    protected void generateRowsForGroupsOrganization(final HtmlTable mainTable, final CurriculumGroup curriculumGroup,
            final int level) {

        final HtmlTableRow groupRow = generateGroupRowWithText(mainTable, getPresentationNameFor(curriculumGroup),
                curriculumGroup.hasCurriculumLines(), level, curriculumGroup);

        // init
        setEmptyGroup(curriculumGroup, emptyGroupsCollapsible);

        generateCurriculumLineRows(mainTable, curriculumGroup, level + 1);
        generateChildGroupRows(mainTable, curriculumGroup, level + 1);

        // qubExtension, don't SHOW empty groups
        if (isEmptyGroup(curriculumGroup)) {
            String keepExistingStyle = Strings.isNullOrEmpty(groupRow.getStyle()) ? "" : groupRow.getStyle();
            groupRow.setStyle(keepExistingStyle + " display: none");
            String keepExistingClasses = Strings.isNullOrEmpty(groupRow.getClasses()) ? "" : groupRow.getClasses();
            groupRow.setClasses(keepExistingClasses + " emptyGroup");
        }
    }

    private void setEmptyGroup(final CurriculumGroup key, final boolean value) {

        if (!emptyGroups.containsKey(key)) {
            // init
            emptyGroups.put(key, emptyGroupsCollapsible);

        } else {

            // must check already previously calculated value 
            emptyGroups.put(key, isEmptyGroup(key) && value);
        }
    }

    // qubExtension, don't SHOW empty groups
    private Boolean isEmptyGroup(final CurriculumGroup group) {
        if (group.isRoot() || group.getCurriculumGroup().isRoot() || group.isBranchCurriculumGroup()) {
            return false;
        }

        final Boolean value = emptyGroups.get(group);
        return value != null && value;
    }

    protected HtmlTableRow generateGroupRowWithText(final HtmlTable mainTable, final String text, boolean addHeaders,
            final int level, final CurriculumGroup curriculumGroup) {

        final HtmlTableRow groupRow = mainTable.createRow();
        addTabsToRow(groupRow, level);
        generateExternalId(groupRow, curriculumGroup);
        groupRow.setClasses(renderer.getCurriculumGroupRowClass());

        final HtmlTableCell cell = groupRow.createCell();
        cell.setClasses(renderer.getLabelCellClass());

        final HtmlComponent body;
        if (curriculumGroup != null && curriculumGroup.isRoot()) {
            body = createDegreeCurricularPlanNameLink(curriculumGroup.getDegreeCurricularPlanOfDegreeModule(),
                    executionPeriodContext, text, true);
        } else {
            body = new HtmlText(createGroupName(text, curriculumGroup).toString(), false);
        }

        cell.setBody(body);

        if (!addHeaders) {
            cell.setColspan(MAX_LINE_SIZE - level);// - 2);
            // generateRulesInfo(groupRow, curriculumGroup);
        } else {
            cell.setColspan(MAX_COL_SPAN_FOR_TEXT_ON_GROUPS_WITH_CHILDS - level);

            generateHeadersForGradeWeightAndEctsCredits(groupRow);
            final HtmlTableCell cellAfterEcts = groupRow.createCell();
            cellAfterEcts.setColspan(MAX_LINE_SIZE - MAX_COL_SPAN_FOR_TEXT_ON_GROUPS_WITH_CHILDS - HEADERS_SIZE);// -
            // 2);

            // generateRulesInfo(groupRow, curriculumGroup);
        }

        return groupRow;
    }

    protected StringBuilder createGroupName(final String text, final CurriculumGroup curriculumGroup) {
        final StringBuilder groupName = new StringBuilder();
        groupName.append("<span class=\"bold\">").append(text).append("</span>");

        if (curriculumGroup != null && curriculumGroup.getDegreeModule() != null) {
            groupName.append(" [");

            final CreditsLimit creditsLimit = (CreditsLimit) curriculumGroup
                    .getMostRecentActiveCurricularRule(CurricularRuleType.CREDITS_LIMIT, executionYearContext);

            if (creditsLimit != null) {
                groupName.append(" <span title=\"");
                groupName.append(i18n(Bundle.APPLICATION, "label.curriculum.credits.legend.minCredits"));
                groupName.append(" \">m(");
                groupName.append(creditsLimit.getMinimumCredits());
                groupName.append(")</span>,");
            }

            groupName.append(" <span title=\"");
            groupName.append(i18n(Bundle.APPLICATION, "label.curriculum.credits.legend.creditsConcluded",
                    executionYearContext.getQualifiedName()));
            groupName.append(" \"> c(");
            groupName.append(curriculumGroup.getCreditsConcluded(executionYearContext));
            groupName.append(")</span>");

            if (isViewerAllowedToViewFullStudentCurriculum(studentCurricularPlan)) {
                groupName.append(" <span title=\"");
                groupName.append(i18n(Bundle.APPLICATION, "label.curriculum.credits.legend.approvedCredits"));
                groupName.append(" \">, ca(");
                groupName.append(curriculumGroup.getAprovedEctsCredits());
                groupName.append(")</span>");
            }

            if (creditsLimit != null) {
                groupName.append("<span title=\"");
                groupName.append(i18n(Bundle.APPLICATION, "label.curriculum.credits.legend.maxCredits"));
                groupName.append("\">, M(");
                groupName.append(creditsLimit.getMaximumCredits());
                groupName.append(")</span>");
            }

            groupName.append(" ]");

            if (isConcluded(curriculumGroup, getExecutionSemester().getExecutionYear(), creditsLimit).value()) {
                groupName.append(" - <span style=\"" + CURRICULUM_GROUP_CONCLUDED + "\">")
                        .append(i18n(Bundle.APPLICATION, "label.curriculumGroup.concluded")).append("</span>");
            } else if (!EnrolmentLayout.isStudentLogged(curriculumGroup.getStudentCurricularPlan())
                    && EnrolmentLayout.hasMinimumCredits(curriculumGroup, getExecutionSemester())) {
                groupName.append(" - <span style=\"" + MINIMUM_CREDITS_CONCLUDED_IN_CURRICULUM_GROUP + "\">")
                        .append(i18n(Bundle.APPLICATION, "label.curriculumGroup.minimumCreditsConcluded")).append("</span>");
            }

            if (AcademicAccessRule.isProgramAccessibleToFunction(AcademicOperationType.ENROLMENT_WITHOUT_RULES,
                    curriculumGroup.getStudentCurricularPlan().getDegree(), Authenticate.getUser())) {
                EnrolmentLayout.addCreditsDistributionMessage(curriculumGroup, getExecutionSemester(), groupName);
            }
        }
        return groupName;
    }

    /**
     * qubExtension, avoid strange "concluded" info when no credits are achived
     */
    static public ConclusionValue isConcluded(final CurriculumGroup group, final ExecutionYear executionYear,
            final CreditsLimit creditsRule) {

        final ConclusionValue concluded = group.isConcluded(executionYear);
        if (!concluded.value()) {
            return concluded;
        }

        if (creditsRule != null
                && creditsRule.getMaximumCredits().doubleValue() != group.getCreditsConcluded(executionYear).doubleValue()) {
            return ConclusionValue.NOT_CONCLUDED;
        }

        /* TODO legidio
        final DegreeModulesSelectionLimit limitRule = (DegreeModulesSelectionLimit) group
                .getMostRecentActiveCurricularRule(CurricularRuleType.DEGREE_MODULES_SELECTION_LIMIT, executionYear);
        if (limitRule != null && limitRule.getMinimumLimit().intValue() != limitRule.getMaximumLimit().intValue()) {
            return ConclusionValue.NOT_CONCLUDED;
        }
        */

        return ConclusionValue.CONCLUDED;
    }

    protected String getBackgroundColor(ConclusionValue value) {
        switch (value) {
        case CONCLUDED:
            return "#dfb";

        case UNKNOWN:
            return "#fff7bb";

        case NOT_CONCLUDED:
            return "#ffeadd";

        default:
            return "";
        }
    }

    protected String getColor(ConclusionValue value) {
        switch (value) {
        case CONCLUDED:
            return "rgb(85, 85, 85)";

        case UNKNOWN:
            return "rgb(85, 85, 85)";

        case NOT_CONCLUDED:
            return "#c00";

        default:
            return "";
        }
    }

    protected void generateCurriculumLineRows(HtmlTable mainTable, CurriculumGroup curriculumGroup, int level) {
        boolean isEmpty = true;

        // qubExtension, mingle dismissals and enrolments
        final List<CurriculumLine> lines = curriculumGroup.getCurriculumModulesSet().stream().filter(i -> i.isLeaf())
                .map(i -> (CurriculumLine) i).sorted(CurriculumLineServices.COMPARATOR).collect(Collectors.toList());

        for (final CurriculumLine iter : lines) {
            if (isToShow(iter)) {
                isEmpty = false;

                if (iter instanceof Enrolment) {
                    generateEnrolmentRow(mainTable, (Enrolment) iter, level);
                }
                if (iter instanceof Dismissal) {
                    generateDismissalRow(mainTable, (Dismissal) iter, level);
                }
            }
        }

        // qubExtension, don't SHOW empty groups
        setEmptyGroup(curriculumGroup, isEmpty);
    }

    protected void generateDismissalRow(HtmlTable mainTable, Dismissal dismissal, int level) {
        final HtmlTableRow dismissalRow = mainTable.createRow();
        addTabsToRow(dismissalRow, level);
        generateExternalId(dismissalRow, dismissal);
        dismissalRow.setClasses(dismissal.getCredits().isTemporary() ? renderer.getTemporaryDismissalRowClass() : renderer
                .getDismissalRowClass());

        generateDismissalLabelCell(mainTable, dismissalRow, dismissal, level);
        generateCellsBetweenLabelAndGradeCell(dismissalRow);
        generateDismissalGradeCell(dismissalRow, dismissal);
        generateDismissalWeightCell(dismissalRow, dismissal);
        generateDismissalEctsCell(dismissalRow, dismissal);
        // qubExtension, empty space
        generateCellWithText(dismissalRow, EMPTY_SPACE, renderer.getLastEnrolmentEvaluationTypeCellClass()).setStyle(EMPTY_WIDTH);
        generateExecutionYearCell(dismissalRow, dismissal);
        generateSemesterCell(dismissalRow, dismissal);
        if (isViewerAllowedToViewFullStudentCurriculum(studentCurricularPlan)) {
            generateApprovementDate(dismissal, dismissalRow, getEvaluationDateCellClass());
        }
        generateCreatorIfRequired(dismissalRow, dismissal.getCreatedBy());
        generateRemarksCell(dismissalRow, dismissal);
        generateSpacerCellsIfRequired(dismissalRow);
    }

    protected void generateDismissalWeightCell(HtmlTableRow dismissalRow, Dismissal dismissal) {
        generateCellWithText(dismissalRow, dismissal.getWeigth() != null ? dismissal.getWeigth().toString() : EMPTY_INFO,
                renderer.getWeightCellClass());

    }

    protected void generateDismissalGradeCell(HtmlTableRow dismissalRow, Dismissal dismissal) {
        final Grade grade = dismissal.getCredits().getGrade();
        final String gradeValue = grade == null || grade.isEmpty() ? null : grade.getValue();

        final String gradeString;
        if (gradeValue != null && NumberUtils.isNumber(gradeValue)) {
            final DecimalFormat decimalFormat = new DecimalFormat("##.##");
            gradeString = decimalFormat.format(Double.valueOf(gradeValue));
        } else {
            gradeString = gradeValue != null ? gradeValue : EMPTY_INFO;
        }

        String title = getGradeDescription(grade);

        final YearMonthDay available = dismissal.getApprovementDate();
        if (available != null) {
            title = title + AVAILABLE_DATE_LABEL + available.toString(DATE_FORMAT);
        }

        // qubExtension
        final HtmlTableCell cell =
                generateCellWithSpan(dismissalRow, new HtmlText(gradeString), title, null, !Strings.isNullOrEmpty(title));
        cell.setStyle(GRADE_APPROVED_STYLE);
    }

    protected void generateCellsBetweenLabelAndGradeCell(final HtmlTableRow row) {
        // qubExtension, ignore CSS on empty info cells
        for (int i = 0; i < COLUMNS_BETWEEN_TEXT_AND_GRADE; i++) {
            // qubExtension, empty space
            generateCellWithText(row, EMPTY_SPACE, "").setStyle(EMPTY_WIDTH);
        }
    }

    protected void generateDismissalEctsCell(HtmlTableRow dismissalRow, Dismissal dismissal) {
        generateCellWithText(dismissalRow,
                dismissal.getEctsCredits() != null ? dismissal.getEctsCredits().toString() : EMPTY_INFO,
                renderer.getEctsCreditsCellClass());
    }

    static protected HtmlTableCell generateApprovementDate(final ICurriculumEntry input, final HtmlTableRow row,
            final String classes) {
        return generateDate(input.getApprovementDate(), row, classes, EVALUATION_DATE_LABEL);
    }

    static protected HtmlTableCell generateEvaluationDate(final ExternalEnrolment input, final HtmlTableRow row,
            final String classes) {
        return generateDate(input.getEvaluationDate(), row, classes, "creationDate");
    }

    static protected HtmlTableCell generateDate(final YearMonthDay date, final HtmlTableRow row, final String classes,
            final String titleLabel) {
        return generateCellWithSpan(row, date != null ? date.toString(DATE_FORMAT) : EMPTY_INFO, titleLabel, classes);
    }

    protected void generateCreatorIfRequired(HtmlTableRow enrolmentRow, String createdBy) {
        if (isViewerAllowedToViewFullStudentCurriculum(studentCurricularPlan)) {
            if (!StringUtils.isEmpty(createdBy)) {
                final Person person = Person.findByUsername(createdBy);
                final String displayName = PersonServices.getDisplayName(person);
                generateCellWithSpan(enrolmentRow, createdBy,
                        i18n(Bundle.APPLICATION, "creator") + (Strings.isNullOrEmpty(displayName) ? "" : ": " + displayName),
                        renderer.getCreatorCellClass());
            } else {
                // qubExtension, show tooltip
                generateCellWithSpan(enrolmentRow, EMPTY_INFO, i18n(Bundle.APPLICATION, "creator"),
                        renderer.getCreatorCellClass());
            }
        }
    }

    protected void generateDismissalLabelCell(final HtmlTable mainTable, HtmlTableRow dismissalRow, Dismissal dismissal,
            int level) {
        // if (dismissal.hasCurricularCourse() || loggedPersonIsManager()) {
        final HtmlTableCell cell = dismissalRow.createCell();
        cell.setColspan(MAX_COL_SPAN_FOR_TEXT_ON_CURRICULUM_LINES - level);
        cell.setClasses(renderer.getLabelCellClass());
        final HtmlInlineContainer container = new HtmlInlineContainer();
        cell.setBody(container);

        if (renderer.isSelectable()) {
            final HtmlCheckBox checkBox = new HtmlCheckBox();
            checkBox.setName(renderer.getSelectionName());
            checkBox.setUserValue(dismissal.getExternalId().toString());
            container.addChild(checkBox);
        }

        final HtmlText text =
                new HtmlText(i18n(Bundle.STUDENT, "label.dismissal." + dismissal.getCredits().getClass().getSimpleName()));
        container.addChild(text);

        final CurricularCourse curricularCourse = dismissal.getCurricularCourse();
        if (curricularCourse != null) {

            String codeAndName = "";
            if (!StringUtils.isEmpty(curricularCourse.getCode())) {
                codeAndName += curricularCourse.getCode() + " - ";
            }
            codeAndName += dismissal.getName().getContent();

            ExecutionCourse executionCourse = dismissal.getCurricularCourse()
                    .getExecutionCoursesByExecutionPeriod(dismissal.getExecutionPeriod()).stream().findAny().orElse(null);

            final HtmlComponent executionCourseLink = createExecutionCourseLink(codeAndName, executionCourse);

            container.addChild(new HtmlText(": "));
            container.addChild(executionCourseLink);
            // qubExtension, Aggregation Info
            container.addChild(EnrolmentLayout.generateAggregationInfo(dismissal));
        }

        // } else {
        // generateCellWithText(dismissalRow,
        // i18n(Bundle.STUDENT, "label.dismissal." +
        // dismissal.getCredits().getClass().getSimpleName()),
        // getLabelCellClass(),
        // MAX_COL_SPAN_FOR_TEXT_ON_CURRICULUM_LINES - level);
        // }

        if (isDetailed(dismissal)) {
            generateDismissalDetails(mainTable, dismissal, level);
        }
    }

    protected void generateDismissalDetails(final HtmlTable mainTable, Dismissal dismissal, int level) {
        for (final IEnrolment enrolment : dismissal.getSourceIEnrolments()) {
            if (enrolment.isExternalEnrolment()) {
                generateExternalEnrolmentRow(mainTable, (ExternalEnrolment) enrolment, level + 1, true);
            } else {
                generateEnrolmentRow(mainTable, (Enrolment) enrolment, level + 1, false, true, true);
            }
        }
    }

    protected void generateExternalEnrolmentRow(HtmlTable mainTable, ExternalEnrolment externalEnrolment, int level,
            boolean isFromDetail) {

        final HtmlTableRow externalEnrolmentRow = mainTable.createRow();
        addTabsToRow(externalEnrolmentRow, level);
        generateExternalId(externalEnrolmentRow, externalEnrolment);
        externalEnrolmentRow.setClasses(renderer.getEnrolmentRowClass());

        generateExternalEnrolmentLabelCell(externalEnrolmentRow, externalEnrolment, level);
        generateCellsBetweenLabelAndGradeCell(externalEnrolmentRow);
        generateEnrolmentGradeCell(externalEnrolmentRow, externalEnrolment, isFromDetail);
        generateEnrolmentWeightCell(externalEnrolmentRow, externalEnrolment, isFromDetail);
        generateExternalEnrolmentEctsCell(externalEnrolmentRow, externalEnrolment);
        // qubExtension, empty space
        generateCellWithText(externalEnrolmentRow, EMPTY_SPACE, renderer.getLastEnrolmentEvaluationTypeCellClass())
                .setStyle(EMPTY_WIDTH);
        generateExecutionYearCell(externalEnrolmentRow, externalEnrolment);
        generateSemesterCell(externalEnrolmentRow, externalEnrolment);
        if (isViewerAllowedToViewFullStudentCurriculum(studentCurricularPlan)) {
            generateEvaluationDate(externalEnrolment, externalEnrolmentRow, getEvaluationDateCellClass());
        }
        generateCreatorIfRequired(externalEnrolmentRow, externalEnrolment.getCreatedBy());
        generateSpacerCellsIfRequired(externalEnrolmentRow);

    }

    protected void generateExternalEnrolmentEctsCell(HtmlTableRow externalEnrolmentRow, ExternalEnrolment externalEnrolment) {
        generateCellWithText(externalEnrolmentRow, externalEnrolment.getEctsCredits().toString(),
                renderer.getEctsCreditsCellClass());
    }

    protected void generateExternalEnrolmentLabelCell(final HtmlTableRow externalEnrolmentRow,
            final ExternalEnrolment externalEnrolment, final int level) {
        // qubExtension, show code
        generateCellWithText(externalEnrolmentRow, externalEnrolment.getDescription() + " (" + externalEnrolment.getCode() + ")",
                renderer.getLabelCellClass(), MAX_COL_SPAN_FOR_TEXT_ON_CURRICULUM_LINES - level);
    }

    private void generateEnrolmentRow(final HtmlTable mainTable, final Enrolment enrolment, final int level) {
        generateEnrolmentRow(mainTable, enrolment, level, true, false, false);
    }

    // qubExtension
    private boolean isToShow(final CurriculumLine input) {
        if (input instanceof Enrolment && renderer.isToShowEnrolments()) {

            if (renderer.isToShowAllEnrolmentStates()) {
                return true;
            }

            if (renderer.isToShowApprovedOnly()) {
                return input.isApproved();
            }

            if (renderer.isToShowApprovedOrEnroledStatesOnly()) {
                return input.isApproved() || ((Enrolment) input).isEnroled() || input.getExecutionYear().isCurrent();
            }

            if (isToShowEnroledStatesOnly()) {
                return ((Enrolment) input).isEnroled();
            }
        }

        if (input instanceof Dismissal && renderer.isToShowDismissals()) {
            return !isToShowEnroledStatesOnly();
        }

        return false;
    }

    private boolean isToShowEnroledStatesOnly() {
        return !renderer.isToShowAllEnrolmentStates() && !renderer.isToShowApprovedOnly()
                && !renderer.isToShowApprovedOrEnroledStatesOnly();
    }

    protected void generateEnrolmentRow(HtmlTable mainTable, Enrolment enrolment, int level, boolean allowSelection,
            boolean isFromDetail, boolean isDismissalOrigin) {
        final HtmlTableRow enrolmentRow = mainTable.createRow();
        addTabsToRow(enrolmentRow, level);
        generateExternalId(enrolmentRow, enrolment);
        enrolmentRow.setClasses(renderer.getEnrolmentRowClass());

        if (enrolment.isEnroled()) {
            generateEnrolmentWithStateEnroled(enrolmentRow, enrolment, level, allowSelection);
        } else {
            generateCurricularCourseCodeAndNameCell(enrolmentRow, enrolment, level, allowSelection);

            // qubExtension, show shifts
            generateEnrolmentShiftsCell(enrolmentRow, enrolment);

            generateDegreeCurricularPlanCell(enrolmentRow, enrolment);
            generateEnrolmentTypeCell(enrolmentRow, enrolment);
            generateEnrolmentStateCell(enrolmentRow, enrolment);
            generateEnrolmentGradeCell(enrolmentRow, enrolment, isFromDetail);
            generateEnrolmentWeightCell(enrolmentRow, enrolment, isFromDetail);
            generateEnrolmentEctsCell(enrolmentRow, enrolment, isFromDetail);
            generateEnrolmentLastEnrolmentEvaluationTypeCell(enrolmentRow, enrolment);
            generateExecutionYearCell(enrolmentRow, enrolment);
            generateSemesterCell(enrolmentRow, enrolment);
            generateStatisticsLinkCell(enrolmentRow, enrolment);
            generateLastEnrolmentEvaluationExamDateCellIfRequired(enrolmentRow, enrolment);
            generateGradeResponsibleIfRequired(enrolmentRow, enrolment);
            generateRemarksCell(enrolmentRow, enrolment);
            generateSpacerCellsIfRequired(enrolmentRow);
        }

        if (isDetailed(enrolment, isDismissalOrigin)
        // qubExtension
        // && isViewerAllowedToViewFullStudentCurriculum(studentCurricularPlan)
        ) {

            // qubExtension, show temporary evaluations for improvements and special seasons
            for (final EnrolmentEvaluation iter : getDetailedEvaluations(enrolment)) {
                generateEnrolmentEvaluationRows(mainTable, iter, level + 1);
            }
        }
    }

    // qubExtension
    private DetailedType getDetailedType() {
        final org.fenixedu.ulisboa.specifications.ui.renderers.student.curriculum.StudentCurricularPlanRenderer specific =
                (org.fenixedu.ulisboa.specifications.ui.renderers.student.curriculum.StudentCurricularPlanRenderer) renderer;
        final String value = specific.getDetailedType();
        return Strings.isNullOrEmpty(value) ? null : DetailedType.valueOf(value);
    }

    // qubExtension
    private boolean isDetailed(final Dismissal dismissal) {
        if (dismissal != null) {

            final DetailedType detailedType = getDetailedType();
            return detailedType == DetailedType.TRUE || (detailedType == DetailedType.CURRENT

                    && dismissal.getExecutionYear().isCurrent());
        }

        return false;
    }

    // qubExtension
    private boolean isDetailed(final Enrolment enrolment, final boolean isDismissalOrigin) {
        if (!isDismissalOrigin && enrolment != null) {

            final DetailedType detailedType = getDetailedType();
            return detailedType == DetailedType.TRUE || (detailedType == DetailedType.CURRENT

                    && (enrolment.getExecutionYear().isCurrent() || enrolment.getEvaluationsSet().stream()
                            .anyMatch(evaluation -> evaluation.getExecutionPeriod() != null
                                    && evaluation.getExecutionPeriod().getExecutionYear().isCurrent())));
        }

        return false;
    }

    // qubExtension
    static private HtmlTableCell generateExternalId(final HtmlTableRow row, final DomainObject domainObject) {
        return generateExternalId(row, domainObject == null ? "" : domainObject.getExternalId());
    }

    // qubExtension
    static private HtmlTableCell generateExternalId(final HtmlTableRow row, final String text) {
        HtmlTableCell result = generateCellWithText(row, text, "");
        result.setStyle("display: none");
        return result;
    }

    // qubExtension
    private void generateRemarksCell(HtmlTableRow row, CurriculumLine curriculumLine) {
        generateRemarksCell(row, CurriculumLineServices.getRemarks(curriculumLine));
    }

    // qubExtension
    private void generateRemarksCell(HtmlTableRow row, EnrolmentEvaluation evaluation) {
        generateRemarksCell(row, EnrolmentEvaluationServices.getRemarks(evaluation));
    }

    // qubExtension
    private void generateRemarksCell(HtmlTableRow row, String remarks) {
        if (isViewerAllowedToViewFullStudentCurriculum(studentCurricularPlan)) {

            final String text;
            if (StringUtils.isNotBlank(remarks)) {
                text = remarks;
            } else {
                text = EMPTY_SPACE;
            }

            final HtmlTableCell cell = generateCellWithText(row, text, "");
            if (StringUtils.isNotBlank(remarks)) {
                cell.setStyle("font-size: xx-small");
            }
        }
    }

    /**
     * qubExtension, show shifts
     */
    private void generateEnrolmentShiftsCell(final HtmlTableRow enrolmentRow, final Enrolment enrolment) {
        final ExecutionSemester executionSemester = enrolment.getExecutionPeriod();
        final Collection<Shift> shifts = EnrolmentServices.getShiftsFor(enrolment, executionSemester);

        final String text;
        if (!shifts.isEmpty()) {
            text = shifts.stream().map(s -> s.getNome()).collect(Collectors.joining(", "));
        } else {
            text = EMPTY_INFO;
        }

        final HtmlTableCell cell =
                generateCellWithSpan(enrolmentRow, new HtmlText(text), i18n("label.Enrolment.shifts"), null, true);
        if (!shifts.isEmpty()) {
            cell.setStyle("font-size: xx-small");
        }
    }

    /**
     * qubExtension, show temporary evaluations for improvements and special seasons
     */
    private Set<EnrolmentEvaluation> getDetailedEvaluations(final Enrolment enrolment) {
        final Set<EnrolmentEvaluation> result = Sets.newLinkedHashSet();

        for (final Iterator<EvaluationSeason> iterator =
                EvaluationSeasonServices.findAll().sorted(EvaluationSeasonServices.SEASON_ORDER_COMPARATOR).iterator(); iterator
                        .hasNext();) {
            final EvaluationSeason season = iterator.next();

            // either the person has access or this season should always show it's enrolment evaluations
            if (EvaluationConfiguration.getInstance().getDefaultEvaluationSeason() == season
                    || EvaluationSeasonServices.isRequiredEnrolmentEvaluation(season)
                    || StudentCurricularPlanRenderer.isViewerAllowedToViewFullStudentCurriculum(studentCurricularPlan)) {

                final Optional<EnrolmentEvaluation> finalEvaluation = enrolment.getFinalEnrolmentEvaluationBySeason(season);
                if (finalEvaluation.isPresent()) {

                    result.add(finalEvaluation.get());

                } else {

                    final EnrolmentEvaluation latestEvaluation = enrolment.getLatestEnrolmentEvaluationBySeason(season);
                    if (latestEvaluation != null) {

                        // we want to show the temporary enrolment evaluation
                        if (EvaluationSeasonServices.isRequiredEnrolmentEvaluation(season)
                                || latestEvaluation.getCompetenceCourseMarkSheet() != null) {
                            result.add(latestEvaluation);
                        }
                    }
                }
            }
        }

        return result;

    }

    /**
     * List the enrollment evaluations bounded to an enrollment
     * 
     * @param mainTable
     *            - Main HTML Table
     * @param evaluations
     *            - List of enrollment evaluations
     * @param level
     *            - The level of the evaluation rows
     */

    protected void generateEnrolmentEvaluationRows(HtmlTable mainTable, EnrolmentEvaluation evaluation, int level) {

        if (evaluation == null) {
            return;
        }

        final HtmlTableRow row = mainTable.createRow();
        addTabsToRow(row, level);
        final EvaluationSeason season = evaluation.getEvaluationSeason();
        final String otherEvaluations = evaluation.getEnrolment().getEvaluationsSet().stream()
                .filter(i -> i != evaluation && i.getEvaluationSeason() == season
                        && EvaluationConfiguration.getInstance().getDefaultEvaluationSeason() != season)
                .map(i -> i.getExternalId()).collect(Collectors.joining(" ; "));
        final String externalIdText =
                evaluation.getExternalId() + (otherEvaluations.isEmpty() ? "" : String.format(" [!! %s !!]", otherEvaluations));
        generateExternalId(row, externalIdText);
        row.setClasses(renderer.getEnrolmentRowClass());

        final ExecutionSemester semester = evaluation.getExecutionPeriod();
        generateCellWithText(row, season.getName().getContent(), renderer.getLabelCellClass(),
                MAX_COL_SPAN_FOR_TEXT_ON_CURRICULUM_LINES - level);

        // qubExtension, show course evaluations
        final Set<Evaluation> courseEvaluations =
                EvaluationServices.findEnrolmentCourseEvaluations(evaluation.getEnrolment(), season, semester);
        if (!courseEvaluations.isEmpty()) {
            final String courseEvaluationsPresentation =
                    courseEvaluations.stream().sorted(DomainObjectUtil.COMPARATOR_BY_ID.reversed())
                            .map(x -> x.getPresentationName()).collect(Collectors.joining(", "));

            generateCellWithSpan(row, courseEvaluationsPresentation, i18n("label.CompetenceCourseMarkSheet.courseEvaluation"),
                    renderer.getCreatorCellClass());
        } else {
            generateCellWithSpan(row, EMPTY_INFO, i18n("label.CompetenceCourseMarkSheet.courseEvaluation"),
                    renderer.getCreatorCellClass());
        }

        final String stateText = !evaluation.isAnnuled() ? "" : EnrollmentState.ANNULED.getDescription();

        // qubExtension, removed unnecessary columns
        generateCellWithText(row, "", "",
                COLUMNS_BETWEEN_TEXT_AND_GRADE - 1 /* course evaluation */ - (stateText.isEmpty() ? 0 : 1));

        // qubExtension, EnrolmentEvaluationState
        if (!stateText.isEmpty()) {
            generateCellWithText(row, stateText, renderer.getEnrolmentStateCellClass()).setStyle(EMPTY_WIDTH);
        }

        final boolean isToShow = generateGradeForEnrolmentEvaluation(evaluation, row);

        generateCellWithText(row, "", renderer.getEctsCreditsCellClass(), GRADE_NEXT_COLUMN_SPAN);

        if (semester != null && EvaluationSeasonServices.isDifferentEvaluationSemesterAccepted(season)) {
            generateCellWithText(row, semester.getExecutionYear().getYear(), renderer.getEnrolmentExecutionYearCellClass());
            generateCellWithText(row, semester.getSemester().toString() + " " + i18n(Bundle.APPLICATION, "label.semester.short"),
                    renderer.getEnrolmentSemesterCellClass());
        } else {
            generateCellWithText(row, EMPTY_SPACE, renderer.getEnrolmentSemesterCellClass(), 2);
        }

        final String examDatePresentation = EnrolmentEvaluationServices.getExamDatePresentation(evaluation);
        if ((isToShow || !courseEvaluations.isEmpty()) && !Strings.isNullOrEmpty(examDatePresentation)) {
            generateCellWithSpan(row, examDatePresentation, EVALUATION_DATE_LABEL, getEvaluationDateCellClass());
        } else {
            // qubExtension, show tooltip
            generateCellWithSpan(row, EMPTY_INFO, EVALUATION_DATE_LABEL, getEvaluationDateCellClass());
        }

        if (isToShow && evaluation.getPersonResponsibleForGrade() != null
                && isViewerAllowedToViewFullStudentCurriculum(studentCurricularPlan)) {
            final Person person = evaluation.getPersonResponsibleForGrade();
            final String username = person.getUsername();
            generateCellWithSpan(row, username,
                    i18n(Bundle.APPLICATION, "label.grade.responsiblePerson") + ": " + PersonServices.getDisplayName(person),
                    renderer.getCreatorCellClass());
        } else {
            // qubExtension, show tooltip
            generateCellWithSpan(row, EMPTY_INFO, i18n(Bundle.APPLICATION, "label.grade.responsiblePerson"),
                    renderer.getCreatorCellClass());
        }

        generateRemarksCell(row, evaluation);
        generateSpacerCellsIfRequired(row);
    }

    // qubExtension
    private boolean generateGradeForEnrolmentEvaluation(final EnrolmentEvaluation evaluation, final HtmlTableRow row) {
        final Grade grade = evaluation.getGrade();
        // qubExtension, evaluation info may not yet be available to the public
        final YearMonthDay availableDate = evaluation.getGradeAvailableDateYearMonthDay();
        final boolean isToShow =
                !grade.isEmpty() && !evaluation.isTemporary() && availableDate != null && !availableDate.isAfter(new LocalDate());
        final boolean markSheetAccess =
                AcademicAuthorizationGroup.get(AcademicOperationType.MANAGE_MARKSHEETS).isMember(Authenticate.getUser());

        // qubExtension, show grade available date as a tooltip
        final CompetenceCourseMarkSheet markSheet = evaluation.getCompetenceCourseMarkSheet();
        final String text = isToShow ? grade.getValue() : markSheetAccess && markSheet != null ? String.format("(%s)",
                i18n(Bundle.ACADEMIC, "label.markSheet")) : EMPTY_INFO;
        String title = null;
        if (isToShow) {
            title = getGradeDescription(grade);

            if (availableDate != null) {
                title = title + AVAILABLE_DATE_LABEL + availableDate.toString(DATE_FORMAT);
            }
        }

        // qubExtension, link to marksheet
        final HtmlComponent component;
        if (markSheet == null || !markSheetAccess) {
            component = new HtmlText(text);
        } else {
            component = new HtmlLink();

            ((HtmlLink) component).setText(text);
            ((HtmlLink) component).setModuleRelative(false);
            ((HtmlLink) component).setTarget("_blank");
            ((HtmlLink) component).setUrl(CompetenceCourseMarkSheetController.READ_URL + markSheet.getExternalId());
        }
        generateCellWithSpan(row, component, title, renderer.getGradeCellClass(), true);
        return isToShow;
    }

    // qubExtension
    static protected String getGradeDescription(final Grade grade) {
        String result = "";

        if (grade != null && !grade.isEmpty()) {

            if (grade.isNumeric()) {
                result = grade.getGradeScale().getDescription();

            } else {
                result = StringUtils.capitalize(grade.getExtendedValue().getContent());
            }

            result = "(" + result + ") ";
        }

        return result;
    }

    protected void generateEnrolmentWithStateEnroled(HtmlTableRow enrolmentRow, Enrolment enrolment, int level,
            boolean allowSelection) {
        generateCurricularCourseCodeAndNameCell(enrolmentRow, enrolment, level, allowSelection);

        // qubExtension, show shifts
        generateEnrolmentShiftsCell(enrolmentRow, enrolment);

        generateDegreeCurricularPlanCell(enrolmentRow, enrolment);
        generateEnrolmentTypeCell(enrolmentRow, enrolment);
        generateEnrolmentStateCell(enrolmentRow, enrolment);
        generateCellWithText(enrolmentRow, EMPTY_INFO, renderer.getGradeCellClass()); // grade
        generateCellWithText(enrolmentRow, EMPTY_INFO, renderer.getWeightCellClass()); // weight
        generateEnrolmentEctsCell(enrolmentRow, enrolment, false);
        generateEnrolmentEvaluationTypeCell(enrolmentRow, enrolment);
        generateExecutionYearCell(enrolmentRow, enrolment);
        generateSemesterCell(enrolmentRow, enrolment);
        if (isViewerAllowedToViewFullStudentCurriculum(studentCurricularPlan)) {
            // enrolment evaluation date
            // qubExtension, show tooltip
            generateCellWithSpan(enrolmentRow, EMPTY_INFO, EVALUATION_DATE_LABEL, getEvaluationDateCellClass());

            // grade responsible
            // qubExtension, show tooltip
            generateCellWithSpan(enrolmentRow, EMPTY_INFO, i18n(Bundle.APPLICATION, "creator"), renderer.getCreatorCellClass());
            generateRemarksCell(enrolmentRow, enrolment);
        }
        generateSpacerCellsIfRequired(enrolmentRow);
    }

    protected void generateGradeResponsibleIfRequired(HtmlTableRow enrolmentRow, Enrolment enrolment) {
        if (isViewerAllowedToViewFullStudentCurriculum(studentCurricularPlan)) {
            final EnrolmentEvaluation lastEnrolmentEvaluation = enrolment.getFinalEnrolmentEvaluation();
            if (lastEnrolmentEvaluation != null && lastEnrolmentEvaluation.getPersonResponsibleForGrade() != null) {

                final Person person = lastEnrolmentEvaluation.getPersonResponsibleForGrade();
                final String username = person.getUsername();
                generateCellWithSpan(enrolmentRow, username,
                        i18n(Bundle.APPLICATION, "label.grade.responsiblePerson") + ": " + PersonServices.getDisplayName(person),
                        renderer.getCreatorCellClass());

            } else {
                // qubExtension, show span
                generateCellWithSpan(enrolmentRow, EMPTY_INFO, i18n(Bundle.APPLICATION, "label.grade.responsiblePerson"),
                        renderer.getCreatorCellClass());
            }
        }

    }

    protected void generateLastEnrolmentEvaluationExamDateCellIfRequired(HtmlTableRow enrolmentRow, Enrolment enrolment) {
        if (isViewerAllowedToViewFullStudentCurriculum(studentCurricularPlan)) {
            final EnrolmentEvaluation lastEnrolmentEvaluation = enrolment.getFinalEnrolmentEvaluation();

            final String text;
            if (lastEnrolmentEvaluation != null && lastEnrolmentEvaluation.getExamDateYearMonthDay() != null) {
                text = EnrolmentEvaluationServices.getExamDatePresentation(lastEnrolmentEvaluation);
            } else {
                text = EMPTY_INFO;
            }

            // qubExtension, show tooltip
            generateCellWithSpan(enrolmentRow, text, EVALUATION_DATE_LABEL, getEvaluationDateCellClass());
        }
    }

    // qubExtension
    private String getEvaluationDateCellClass() {
        return "nowrap " + renderer.getCreationDateCellClass();
    }

    protected void generateSpacerCellsIfRequired(final HtmlTableRow row) {
        final int spacerColspan = calculateSpacerColspan();
        if (spacerColspan > 0) {
            final HtmlTableCell spaceCells = row.createCell();
            spaceCells.setColspan(spacerColspan);
            spaceCells.setText("");
        }
    }

    protected int calculateSpacerColspan() {
        return MAX_LINE_SIZE - MAX_COL_SPAN_FOR_TEXT_ON_CURRICULUM_LINES - COLUMNS_BETWEEN_TEXT_AND_ENROLMENT_EVALUATION_DATE
                - (isViewerAllowedToViewFullStudentCurriculum(studentCurricularPlan) ? LATEST_ENROLMENT_EVALUATION_COLUMNS : 0);
    }

    protected void generateSemesterCell(final HtmlTableRow row, final ICurriculumEntry entry) {

        generateCellWithText(row, getCurricularPeriodLabel(entry), this.renderer.getEnrolmentSemesterCellClass())
                .setStyle("font-size: xx-small");
    }

    // qubExtension
    static public String getCurricularPeriodLabel(final ICurriculumEntry entry) {

        // qubExtension, show curricularYear
        final Integer curricularYear = getCurricularYearFor(entry);
        final String year =
                curricularYear == null ? "" : curricularYear + " " + i18n(Bundle.APPLICATION, "label.curricular.year");

        final Integer curricularSemester = getCurricularSemesterFor(entry);
        final String semester = curricularSemester == null ? "" : curricularSemester.toString() + " "
                + i18n(Bundle.APPLICATION, "label.semester.short");

        return Strings.isNullOrEmpty(year) ? semester : Strings.isNullOrEmpty(semester) ? year : year + ", " + semester;
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

    /**
     * qubExtension, show curricularSemester
     */
    static private Integer getCurricularSemesterFor(final ICurriculumEntry entry) {
        if (entry instanceof CurriculumLine) {
            final CurriculumLine line = (CurriculumLine) entry;
            final CurricularCourse curricularCourse = line.getCurricularCourse();
            if (curricularCourse != null && curricularCourse.isAnual()) {
                return null;
            }

            return CurricularPeriodServices.getCurricularSemester(line);

        } else {
            return entry.getExecutionPeriod().getSemester();
        }
    }

    protected void generateStatisticsLinkCell(final HtmlTableRow row, final Enrolment enrolment) {
        //qubextension functionality not available
//        if (enrolment.getStudent() == AccessControl.getPerson().getStudent()
//                && enrolment.getStudent().hasAnyActiveRegistration()) {
//            ExecutionCourse executionCourse = enrolment.getExecutionCourseFor(enrolment.getExecutionPeriod());
//            if (executionCourse != null) {
//                final HtmlInlineContainer inlineContainer = new HtmlInlineContainer();
//                inlineContainer.addChild(createExecutionCourseStatisticsLink(
//                        i18n(Bundle.APPLICATION, "label.statistics"), executionCourse));
//                final HtmlTableCell cell = row.createCell();
//                cell.setClasses(renderer.getStatisticsLinkCellClass());
//                cell.setBody(inlineContainer);
//            }
//        }
    }

    protected void generateExecutionYearCell(HtmlTableRow row, final ICurriculumEntry entry) {
        generateCellWithText(row, entry.hasExecutionPeriod() ? entry.getExecutionYear().getYear() : EMPTY_INFO,
                renderer.getEnrolmentExecutionYearCellClass());
    }

    // qubExtension
    protected void generateEnrolmentLastEnrolmentEvaluationTypeCell(HtmlTableRow enrolmentRow, Enrolment enrolment) {
        final EnrolmentEvaluation lastEnrolmentEvaluation = enrolment.getFinalEnrolmentEvaluation();
        if (lastEnrolmentEvaluation != null && lastEnrolmentEvaluation.getEvaluationSeason() != null) {
            generateCellWithText(enrolmentRow,
                    lastEnrolmentEvaluation.getEvaluationSeason().getAcronym().getContent()
                            + ENROLMENT_SEASON_EXTRA_INFORMATION_PROVIDER.apply(enrolment),
                    renderer.getLastEnrolmentEvaluationTypeCellClass());
        } else {
            generateCellWithText(enrolmentRow, EMPTY_INFO + ENROLMENT_SEASON_EXTRA_INFORMATION_PROVIDER.apply(enrolment),
                    renderer.getLastEnrolmentEvaluationTypeCellClass());
        }

    }

    // qubExtension
    protected void generateEnrolmentEvaluationTypeCell(HtmlTableRow enrolmentRow, Enrolment enrolment) {
//        final EvaluationSeason season = enrolment.getEvaluationSeason();
//        if (season != null) {
//            generateCellWithSpan(enrolmentRow, season.getAcronym().getContent(),
//                    renderer.getLastEnrolmentEvaluationTypeCellClass());
//        } else {
//            generateCellWithText(enrolmentRow, EMPTY_INFO, renderer.getLastEnrolmentEvaluationTypeCellClass());
//        }

        generateCellWithText(enrolmentRow, EMPTY_INFO + ENROLMENT_SEASON_EXTRA_INFORMATION_PROVIDER.apply(enrolment),
                renderer.getLastEnrolmentEvaluationTypeCellClass());

    }

    protected void generateEnrolmentEctsCell(final HtmlTableRow enrolmentRow, final Enrolment enrolment,
            final boolean isFromDetail) {
        final String ectsCredits = String.valueOf(enrolment.getEctsCredits());
        generateCellWithText(enrolmentRow, ectsCredits, renderer.getEctsCreditsCellClass());
    }

    protected void generateEnrolmentWeightCell(HtmlTableRow enrolmentRow, IEnrolment enrolment, boolean isFromDetail) {
        // qubExtension
        generateCellWithText(enrolmentRow, enrolment.getWeigth() != null ? String.valueOf(enrolment.getWeigth()) : EMPTY_INFO,
                renderer.getWeightCellClass());
    }

    protected void generateEnrolmentGradeCell(HtmlTableRow enrolmentRow, IEnrolment enrolment, final boolean isFromDetail) {
        final Grade grade = enrolment.getGrade();

        // qubExtension, show grade available as a tooltip
        final String text = grade.isEmpty() ? EMPTY_INFO : grade.getValue();
        String title = null;
        if (enrolment instanceof Enrolment) {
            final Enrolment specific = (Enrolment) enrolment;
            final EnrolmentEvaluation evaluation = specific.getFinalEnrolmentEvaluation();

            title = getGradeDescription(grade);

            final YearMonthDay available = evaluation != null ? evaluation.getGradeAvailableDateYearMonthDay() : null;
            if (available != null) {
                title = title + AVAILABLE_DATE_LABEL + available.toString(DATE_FORMAT);
            }
        }

        final HtmlTableCell cell =
                generateCellWithSpan(enrolmentRow, new HtmlText(text), title, null, !Strings.isNullOrEmpty(title));
        cell.setStyle(enrolment.isApproved()
                && !isFromDetail ? GRADE_APPROVED_STYLE : grade.isNotApproved() ? GRADE_NOT_APPROVED_STYLE : GRADE_EMPTY_STYLE);
    }

    protected void generateEnrolmentStateCell(HtmlTableRow enrolmentRow, Enrolment enrolment) {
        // qubExtension, empty space
        generateCellWithText(enrolmentRow, enrolment.isApproved() ? EMPTY_SPACE : enrolment.getEnrollmentState().getDescription(),
                renderer.getEnrolmentStateCellClass()).setStyle(EMPTY_WIDTH);
    }

    protected void generateEnrolmentTypeCell(HtmlTableRow enrolmentRow, Enrolment enrolment) {
        // qubExtension, empty space
        generateCellWithText(enrolmentRow,
                enrolment.isEnrolmentTypeNormal() ? EMPTY_SPACE : i18n(Bundle.ENUMERATION, enrolment.getEnrolmentTypeName()),
                renderer.getEnrolmentTypeCellClass()).setStyle(EMPTY_WIDTH);
    }

    protected void generateDegreeCurricularPlanCell(final HtmlTableRow enrolmentRow, final Enrolment enrolment) {

        // qubExtension, generate not only when enrolment's registration is different but also when the degree module's degree is different
        final DegreeCurricularPlan plan = enrolment.getDegreeCurricularPlanOfDegreeModule();
        if (plan == null || plan.getDegree() == studentCurricularPlan.getDegree()) {

            // qubExtension, empty space
            generateCellWithText(enrolmentRow, EMPTY_SPACE, renderer.getDegreeCurricularPlanCellClass()).setStyle(EMPTY_WIDTH);

        } else {
            final HtmlTableCell cell = enrolmentRow.createCell();
            cell.setClasses(renderer.getDegreeCurricularPlanCellClass());
            // qubExtension, show degree code
            final String text = plan.getDegree().getCode();
            cell.setBody(createDegreeCurricularPlanNameLink(plan, enrolment.getExecutionPeriod(), text, false));
        }

    }

    protected HtmlComponent createDegreeCurricularPlanNameLink(final DegreeCurricularPlan degreeCurricularPlan,
            ExecutionSemester executionSemester, final String text, final boolean bold) {

        HtmlComponent result = new HtmlText(text);

        final String siteUrl = degreeCurricularPlan.getDegree().getSiteUrl();
        if (!Strings.isNullOrEmpty(siteUrl)) {
            result = new HtmlLink();
            ((HtmlLink) result).setText(text);
            ((HtmlLink) result).setModuleRelative(false);
            ((HtmlLink) result).setContextRelative(false);
            ((HtmlLink) result).setTarget("_blank");
            ((HtmlLink) result).setUrl(siteUrl);
        }

        if (bold) {
            result.setStyle("font-weight: bold !important; color: #4b565c;");
        }

        return result;
    }

    protected void generateCurricularCourseCodeAndNameCell(final HtmlTableRow enrolmentRow, final Enrolment enrolment,
            final int level, boolean allowSelection) {

        final HtmlInlineContainer inlineContainer = new HtmlInlineContainer();

        if (renderer.isSelectable() && allowSelection) {
            final HtmlCheckBox checkBox = new HtmlCheckBox();
            checkBox.setName(renderer.getSelectionName());
            checkBox.setUserValue(enrolment.getExternalId().toString());
            inlineContainer.addChild(checkBox);
        }

        ExecutionCourse executionCourse = enrolment.getExecutionCourseFor(enrolment.getExecutionPeriod());

        final HtmlComponent executionCourseLink = createExecutionCourseLink(getPresentationNameFor(enrolment), executionCourse);

        inlineContainer.addChild(executionCourseLink);
        // qubExtension, Aggregation Info
        inlineContainer.addChild(EnrolmentLayout.generateAggregationInfo(enrolment));

        final HtmlTableCell cell = enrolmentRow.createCell();
        cell.setClasses(renderer.getLabelCellClass());
        cell.setColspan(MAX_COL_SPAN_FOR_TEXT_ON_CURRICULUM_LINES - level);
        cell.setBody(inlineContainer);
    }

    protected String getPresentationNameFor(final Enrolment enrolment) {
        return EnrolmentServices.getPresentationName(enrolment);
    }

    // qubExtension
    protected String getPresentationNameFor(final CurriculumGroup curriculumGroup) {
        if (curriculumGroup.isRoot()) {
            final Registration registration = curriculumGroup.getRegistration();
            return "[" + registration.getDegree().getCode() + "] " + registration.getDegreeNameWithDescription() + ", "
                    + curriculumGroup.getName().getContent() + " - "
                    + curriculumGroup.getStudentCurricularPlan().getStartDateYearMonthDay().toString("yyyy-MM-dd");
        }

        return curriculumGroup.getName().getContent();
    }

    protected HtmlComponent createExecutionCourseLink(final String text, final ExecutionCourse executionCourse) {

        if (executionCourse != null && executionCourse.getSiteUrl() != null) {
            final HtmlLink result = new HtmlLink();
            result.setText(text);
            result.setModuleRelative(false);
            result.setContextRelative(false);
            result.setTarget(HtmlLink.Target.BLANK);
            result.setUrl(executionCourse.getSiteUrl());
            return result;
        }

        return new HtmlText(text);
    }

    protected HtmlLink createExecutionCourseStatisticsLink(final String text, final ExecutionCourse executionCourse) {
        final HtmlLink result = new HtmlLink();
        result.setBody(new HtmlText(text));
        result.setParameter("executionCourseId", executionCourse.getExternalId());
        result.setParameter("method", "showExecutionCourseStatistics");
        result.setModuleRelative(false);
        result.setUrl("/student/showStudentStatistics.do");
        return result;
    }

    protected void generateChildGroupRows(HtmlTable mainTable, CurriculumGroup parentGroup, int level) {
        final Set<CurriculumGroup> groups = new TreeSet<CurriculumGroup>(new Comparator<CurriculumGroup>() {
            @Override
            public int compare(CurriculumGroup o1, CurriculumGroup o2) {
                int result = o1.getChildOrder().compareTo(o2.getChildOrder());
                if (result != 0) {
                    return result;
                }

                // qubExtension, respect order of DCP
                if (o1.getDegreeModule() != null && o2.getDegreeModule() != null) {
                    final DegreeModule d1 = o1.getDegreeModule();
                    final DegreeModule d2 = o2.getDegreeModule();
                    return Collator.getInstance().compare(d1.getName(), d2.getName());
                }

                return o1.getExternalId().compareTo(o2.getExternalId());
            }
        });

        groups.addAll(parentGroup.getCurriculumGroups().stream()
                .filter(i -> StudentCurricularPlanRenderer.canGenerate(i, studentCurricularPlan)).collect(Collectors.toSet()));

        for (final CurriculumGroup childGroup : groups) {
            generateRowsForGroupsOrganization(mainTable, childGroup, level);
        }

        // qubExtension, don't SHOW empty groups
        // notice this must be tested after group generation
        setEmptyGroup(parentGroup, groups.isEmpty() || groups.stream().allMatch(i -> isEmptyGroup(i)));
    }

    protected void addTabsToRow(final HtmlTableRow row, final int level) {
        for (int i = 0; i < level; i++) {
            HtmlLink link = new HtmlLink();
            link.setModuleRelative(false);
            link.setUrl(StudentCurricularPlanLayout.SPACER_IMAGE_PATH);

            final HtmlImage spacerImage = new HtmlImage();
            spacerImage.setSource(link.calculateUrl());

            final HtmlTableCell tabCell = row.createCell();
            tabCell.setClasses(renderer.getTabCellClass());
            tabCell.setBody(spacerImage);

            // qubExtension, bug fix on CSS width with percentage
            tabCell.setStyle(EMPTY_WIDTH);
        }
    }

    protected void generateHeadersForGradeWeightAndEctsCredits(final HtmlTableRow groupRow) {
        generateCellWithText(groupRow, i18n(Bundle.APPLICATION, "label.grade"), renderer.getGradeCellClass());

        generateCellWithText(groupRow, i18n(Bundle.APPLICATION, "label.weight"), renderer.getWeightCellClass());

        generateCellWithText(groupRow, i18n(Bundle.APPLICATION, "label.ects"), renderer.getEctsCreditsCellClass());

    }

    static protected HtmlTableCell generateCellWithText(final HtmlTableRow row, final String text, final String cssClass) {
        return generateCellWithText(row, text, cssClass, 1);
    }

    static protected HtmlTableCell generateCellWithText(final HtmlTableRow row, final String text, final String cssClass,
            Integer colSpan) {
        final HtmlTableCell cell = row.createCell();
        cell.setClasses(cssClass);
        cell.setText(text);
        cell.setColspan(colSpan);
        return cell;
    }

    static protected HtmlTableCell generateCellWithSpan(final HtmlTableRow row, final String text, final String title,
            final String cssClass) {
        return generateCellWithSpan(row, new HtmlText(text), title, cssClass, false);
    }

    static protected HtmlTableCell generateCellWithSpan(final HtmlTableRow row, final HtmlComponent component, final String title,
            final String cssClass, final boolean forceDots) {

        final HtmlTableCell cell = row.createCell();
        cell.setClasses(cssClass);

        final HtmlInlineContainer span = new HtmlInlineContainer();
        span.addChild(component);
        span.setTitle(title);
        if (forceDots) {
            span.setStyle(TOOLTIP_DOT);
        }

        cell.setBody(span);

        // qubExtension, return cell
        return cell;
    }

}
