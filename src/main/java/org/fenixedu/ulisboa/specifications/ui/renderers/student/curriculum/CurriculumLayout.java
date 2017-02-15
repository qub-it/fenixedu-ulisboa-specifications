package org.fenixedu.ulisboa.specifications.ui.renderers.student.curriculum;

import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.OptionalEnrolment;
import org.fenixedu.academic.domain.accessControl.AcademicAuthorizationGroup;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.Dismissal;
import org.fenixedu.academic.domain.studentCurriculum.ExternalEnrolment;
import org.fenixedu.academic.ui.renderers.student.curriculum.CurriculumRenderer;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.ulisboa.specifications.domain.ects.CourseGradingTable;
import org.fenixedu.ulisboa.specifications.domain.ects.DefaultGradingTable;
import org.fenixedu.ulisboa.specifications.servlet.FenixeduUlisboaSpecificationsInitializer;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;

import pt.ist.fenixWebFramework.renderers.components.HtmlBlockContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlComponent;
import pt.ist.fenixWebFramework.renderers.components.HtmlContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlInlineContainer;
import pt.ist.fenixWebFramework.renderers.components.HtmlLink;
import pt.ist.fenixWebFramework.renderers.components.HtmlTable;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableCell;
import pt.ist.fenixWebFramework.renderers.components.HtmlTableRow;
import pt.ist.fenixWebFramework.renderers.components.HtmlText;
import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.contexts.PresentationContext;
import pt.ist.fenixWebFramework.renderers.layouts.Layout;

public class CurriculumLayout extends Layout {

    private static final int MAX_COL_SPAN_FOR_TEXT_ON_CURRICULUM_LINES = 14;
    private CurriculumRenderer renderer;
    private Curriculum curriculum;

    public CurriculumLayout(CurriculumRenderer renderer) {
        this.renderer = renderer;
    }

    // qubExtension
    public static void register() {
        CurriculumRenderer.setLayoutProvider(renderer -> new CurriculumLayout(renderer));
    }

    @Override
    public HtmlComponent createComponent(Object object, Class type) {
        this.curriculum = (Curriculum) object;

        final HtmlContainer container = new HtmlBlockContainer();

        if (this.curriculum == null) {
            container.addChild(createHtmlTextItalic(BundleUtil.getString(Bundle.STUDENT, "message.no.average")));

            return container;
        }

        if (this.curriculum.isEmpty()) {
            container.addChild(createHtmlTextItalic(BundleUtil.getString(Bundle.STUDENT, "message.no.approvals")));

            return container;
        }

        if (this.curriculum.getCurriculumEntries().isEmpty()) {
            container.addChild(createHtmlTextItalic(BundleUtil.getString(Bundle.STUDENT, "message.empty.curriculum")));
        } else {
            final HtmlContainer averageContainer = new HtmlBlockContainer();
            averageContainer.setStyle("padding-bottom: 3.5em;");
            container.addChild(averageContainer);
            final HtmlTable averageEntriesTable = new HtmlTable();
            averageContainer.addChild(averageEntriesTable);
            averageEntriesTable.setClasses(renderer.getTableClass());
            generateAverageRows(averageEntriesTable);
        }

        if (renderer.isVisibleCurricularYearEntries()) {
            final HtmlContainer curricularYearContainer = new HtmlBlockContainer();
            container.addChild(curricularYearContainer);
            final HtmlTable curricularYearTable = new HtmlTable();
            curricularYearContainer.addChild(curricularYearTable);
            curricularYearTable.setClasses(renderer.getTableClass());
            generateCurricularYearRows(curricularYearTable);
            generateCurricularYearSums(curricularYearTable);
        }

        return container;
    }

    private HtmlText createHtmlTextItalic(final String message) {
        final HtmlText htmlText = new HtmlText(message);
        htmlText.setClasses("italic");

        return htmlText;
    }

    private void generateAverageRows(final HtmlTable mainTable) {

        final Set<ICurriculumEntry> sortedIAverageEntries =
                new TreeSet<ICurriculumEntry>(ICurriculumEntry.COMPARATOR_BY_EXECUTION_PERIOD_AND_NAME_AND_ID);
        sortedIAverageEntries.addAll(this.curriculum.getEnrolmentRelatedEntries());
        if (!sortedIAverageEntries.isEmpty()) {
            generateGroupRowWithText(mainTable, "Inscrições", true, 0);
            generateRows(mainTable, sortedIAverageEntries, 0);
        }

        final Set<ICurriculumEntry> sortedEquivalenceEntries =
                new TreeSet<ICurriculumEntry>(ICurriculumEntry.COMPARATOR_BY_EXECUTION_PERIOD_AND_NAME_AND_ID);
        final Set<ICurriculumEntry> sortedSubstitutionsEntries =
                new TreeSet<ICurriculumEntry>(ICurriculumEntry.COMPARATOR_BY_EXECUTION_PERIOD_AND_NAME_AND_ID);
        for (final ICurriculumEntry entry : this.curriculum.getDismissalRelatedEntries()) {
            if (entry instanceof Dismissal) {
                sortedEquivalenceEntries.add(entry);
            } else {
                sortedSubstitutionsEntries.add(entry);
            }
        }

        if (!sortedSubstitutionsEntries.isEmpty()) {
            generateGroupRowWithText(mainTable, "Substituições", true, 0);
            generateRows(mainTable, sortedSubstitutionsEntries, 0);
        }

        if (!sortedEquivalenceEntries.isEmpty()) {
            generateGroupRowWithText(mainTable, "Equivalências", true, 0);
            generateRows(mainTable, sortedEquivalenceEntries, 0);
        }
    }

    private void generateGroupRowWithText(final HtmlTable mainTable, final String text, boolean addHeaders, final int level) {

        final HtmlTableRow groupRow = mainTable.createRow();
        groupRow.setClasses(renderer.getHeaderRowClass());

        final HtmlTableCell textCell = groupRow.createCell();
        textCell.setText(text);
        textCell.setClasses(renderer.getLabelCellClass());
        textCell.setColspan(MAX_COL_SPAN_FOR_TEXT_ON_CURRICULUM_LINES);

        generateCellWithText(groupRow, BundleUtil.getString(Bundle.APPLICATION, "label.grade"), renderer.getGradeCellClass());
        generateCellWithText(groupRow,
                BundleUtil.getString(ULisboaSpecificationsUtil.BUNDLE, "label.gradingTables.curriculumRenderer.ectsGrade"),
                renderer.getGradeCellClass());
        generateCellWithText(groupRow, BundleUtil.getString(Bundle.APPLICATION, "label.weight"),
                renderer.getEctsCreditsCellClass());

        final HtmlTableCell executionYearCell = groupRow.createCell();
        executionYearCell.setText("Ano Lectivo");
        executionYearCell.setClasses(renderer.getGradeCellClass());
        executionYearCell.setColspan(2);
    }

    private void generateRows(HtmlTable mainTable, Set<ICurriculumEntry> entries, int level) {
        for (final ICurriculumEntry entry : entries) {
            generateRow(mainTable, entry, level, true);
        }
    }

    private void generateRow(HtmlTable mainTable, final ICurriculumEntry entry, int level, boolean allowSelection) {
        final HtmlTableRow enrolmentRow = mainTable.createRow();
        enrolmentRow.setClasses(renderer.getEnrolmentRowClass());

        generateCodeAndNameCell(enrolmentRow, entry, level, allowSelection);
        if (entry instanceof ExternalEnrolment) {
            generateExternalEnrolmentLabelCell(enrolmentRow, (ExternalEnrolment) entry, level);
        }
        generateGradeCell(enrolmentRow, entry);
        generateEctsGradeCell(enrolmentRow, entry);
        generateWeightCell(enrolmentRow, entry);
        generateExecutionYearCell(enrolmentRow, entry);
        generateSemesterCell(enrolmentRow, entry);
    }

    private void generateCodeAndNameCell(final HtmlTableRow enrolmentRow, final ICurriculumEntry entry, final int level,
            boolean allowSelection) {

        final HtmlInlineContainer inlineContainer = new HtmlInlineContainer();
        inlineContainer.addChild(new HtmlText(getPresentationNameFor(entry)));

        final HtmlTableCell cell = enrolmentRow.createCell();
        cell.setClasses(renderer.getLabelCellClass());
        cell.setColspan(MAX_COL_SPAN_FOR_TEXT_ON_CURRICULUM_LINES - (entry instanceof ExternalEnrolment ? 1 : 0) - level);
        cell.setBody(inlineContainer);
    }

    private String getPresentationNameFor(final ICurriculumEntry entry) {
        final String code = !StringUtils.isEmpty(entry.getCode()) ? entry.getCode() + " - " : "";

        if (entry instanceof OptionalEnrolment) {
            final OptionalEnrolment optionalEnrolment = (OptionalEnrolment) entry;
            return code + optionalEnrolment.getCurricularCourse().getNameI18N(entry.getExecutionPeriod()).getContent();
        } else {
            return code + entry.getName().getContent();
        }
    }

    private void generateExternalEnrolmentLabelCell(final HtmlTableRow externalEnrolmentRow,
            final ExternalEnrolment externalEnrolment, final int level) {

        StudentCurricularPlanLayout.generateCellWithText(externalEnrolmentRow, externalEnrolment.getDescription(),
                renderer.getLabelCellClass(), 1);
    }

    private void generateGradeCell(HtmlTableRow enrolmentRow, final ICurriculumEntry entry) {
        final Grade grade = entry.getGrade();
        generateCellWithText(enrolmentRow, grade.isEmpty() ? "-" : grade.getValue(), renderer.getGradeCellClass());
    }

    private void generateEctsGradeCell(HtmlTableRow enrolmentRow, final ICurriculumEntry entry) {
        final String grade = entry.getGrade().isEmpty() ? "-" : entry.getGrade().getValue();
        String ectsGrade = null;
        if (entry instanceof ExternalEnrolment) {
            DefaultGradingTable table = DefaultGradingTable.getDefaultGradingTable();
            if (table != null) {
                ectsGrade = table.getEctsGrade(grade);
            } else {

                if (!isConclusionDocument() && AcademicAuthorizationGroup.get(AcademicOperationType.MANAGE_CONCLUSION)
                        .isMember(Authenticate.getUser())) {
                    generateCellWithLink(enrolmentRow, entry.getExecutionYear(),
                            BundleUtil.getString(FenixeduUlisboaSpecificationsInitializer.BUNDLE,
                                    "label.gradingTables.curriculumRenderer.generateInstitutionTable"));
                    return;
                }
            }

        } else if (entry instanceof CurriculumLine) {
            CurriculumLine line = (CurriculumLine) entry;
            if (line.getCurricularCourse() == null) {
                ectsGrade = BundleUtil.getString(FenixeduUlisboaSpecificationsInitializer.BUNDLE,
                        "label.gradingTables.curriculumRenderer.lineWithoutCurricularCourse");
            } else {
                CourseGradingTable table = CourseGradingTable.find(line);
                if (table != null) {
                    ectsGrade = table.getEctsGrade(grade);
                } else if (CourseGradingTable.isApplicable(line)) {
                    if (!isConclusionDocument() && AcademicAuthorizationGroup.get(AcademicOperationType.MANAGE_CONCLUSION)
                            .isMember(Authenticate.getUser())) {
                        generateCellWithLink(enrolmentRow, entry.getExecutionYear(),
                                BundleUtil.getString(FenixeduUlisboaSpecificationsInitializer.BUNDLE,
                                        "label.gradingTables.curriculumRenderer.generateCourseTable"));
                        return;
                    }
                }
            }
        }
        generateCellWithText(enrolmentRow, ectsGrade == null ? "-" : ectsGrade, renderer.getGradeCellClass());
    }

    /**
     * legidio, HUGE HACK to avoid links in document to be printed
     */
    private boolean isConclusionDocument() {
        final PresentationContext context = this.renderer == null ? null : renderer.getContext();
        final IViewState viewState = context == null ? null : context.getViewState();
        final HttpServletRequest request = viewState == null ? null : viewState.getRequest();
        final StringBuffer requestURL = request == null ? null : request.getRequestURL();
        return requestURL == null ? false : requestURL.toString().contains("egistrationConclusionDocument");
    }

    private void generateWeightCell(HtmlTableRow enrolmentRow, final ICurriculumEntry entry) {
        generateCellWithText(enrolmentRow, entry.getGrade().isNumeric() ? entry.getWeigthForCurriculum().toString() : "-",
                renderer.getWeightCellClass());
    }

    private void generateEctsCreditsCell(HtmlTableRow enrolmentRow, final ICurriculumEntry entry) {
        generateCellWithText(enrolmentRow, entry.getEctsCreditsForCurriculum().toString(), renderer.getEctsCreditsCellClass());
    }

    private void generateExecutionYearCell(HtmlTableRow enrolmentRow, final ICurriculumEntry entry) {
        generateCellWithText(enrolmentRow, entry.getExecutionYear() == null ? "-" : entry.getExecutionYear().getYear(),
                renderer.getEnrolmentExecutionYearCellClass());
    }

    private void generateSemesterCell(final HtmlTableRow row, final ICurriculumEntry entry) {

        StudentCurricularPlanLayout.generateCellWithText(row, StudentCurricularPlanLayout.getCurricularPeriodLabel(entry),
                this.renderer.getEnrolmentSemesterCellClass()).setStyle("font-size: xx-small");
    }

    private HtmlTableCell generateCellWithText(final HtmlTableRow row, final String text, final String cssClass) {
        return StudentCurricularPlanLayout.generateCellWithText(row, text, cssClass);
    }

    private void generateCellWithLink(final HtmlTableRow row, final ExecutionYear executionYear, final String linkText) {
        final HtmlInlineContainer inlineContainer = new HtmlInlineContainer();
        inlineContainer.addChild(createGradingTableGeneratorLink(linkText, executionYear));
        final HtmlTableCell cell = row.createCell();
        cell.setClasses(renderer.getGradeCellClass());
        cell.setBody(inlineContainer);
    }

    private void generateCurricularYearRows(final HtmlTable table) {
        final Set<ICurriculumEntry> sortedEntries =
                new TreeSet<ICurriculumEntry>(ICurriculumEntry.COMPARATOR_BY_EXECUTION_PERIOD_AND_NAME_AND_ID);
        sortedEntries.addAll(this.curriculum.getCurricularYearEntries());
        if (!sortedEntries.isEmpty()) {
            generateCurricularYearHeaderRowWithText(table, "", true, 0);
            generateCurricularYearRows(table, sortedEntries, 0);
        }
    }

    private void generateCurricularYearHeaderRowWithText(final HtmlTable table, final String text, boolean addHeaders,
            final int level) {
        final HtmlTableRow groupRow = table.createRow();
        groupRow.setClasses(renderer.getHeaderRowClass());

        final HtmlTableCell textCell = groupRow.createCell();
        textCell.setText(text);
        textCell.setClasses(renderer.getLabelCellClass());
        textCell.setRowspan(2);
        textCell.setColspan(MAX_COL_SPAN_FOR_TEXT_ON_CURRICULUM_LINES);

        final HtmlTableCell curricularYearCell = groupRow.createCell();
        curricularYearCell.setText("Ano Curricular");
        curricularYearCell.setClasses(renderer.getGradeCellClass());
        curricularYearCell.setColspan(1);

        final HtmlTableCell executionYearCell = groupRow.createCell();
        executionYearCell.setText("Ano Lectivo");
        executionYearCell.setClasses(renderer.getGradeCellClass());
        executionYearCell.setColspan(2);
        executionYearCell.setRowspan(2);

        final HtmlTableRow groupSubRow = table.createRow();
        groupSubRow.setClasses(renderer.getHeaderRowClass());
        generateCellWithText(groupSubRow, BundleUtil.getString(Bundle.APPLICATION, "label.ects"),
                renderer.getEctsCreditsCellClass());
    }

    private void generateCurricularYearRows(HtmlTable mainTable, Set<ICurriculumEntry> entries, int level) {
        for (final ICurriculumEntry entry : entries) {
            generateCurricularYearRow(mainTable, entry, level, true);
        }
    }

    private void generateCurricularYearRow(HtmlTable mainTable, final ICurriculumEntry entry, int level, boolean allowSelection) {
        final HtmlTableRow enrolmentRow = mainTable.createRow();
        enrolmentRow.setClasses(renderer.getEnrolmentRowClass());

        generateCodeAndNameCell(enrolmentRow, entry, level, allowSelection);
        if (entry instanceof ExternalEnrolment) {
            generateExternalEnrolmentLabelCell(enrolmentRow, (ExternalEnrolment) entry, level);
        }
        generateEctsCreditsCell(enrolmentRow, entry);
        generateExecutionYearCell(enrolmentRow, entry);
        generateSemesterCell(enrolmentRow, entry);
    }

    private void generateCurricularYearSums(final HtmlTable mainTable) {
        final HtmlTableRow row = mainTable.createRow();
        row.setClasses(renderer.getHeaderRowClass());

        final HtmlTableCell sumsCell = row.createCell();
        sumsCell.setText("Somatórios");
        sumsCell.setStyle("text-align: right;");
        sumsCell.setColspan(14);

        final HtmlTableCell sumEctsCreditsCell = row.createCell();
        sumEctsCreditsCell.setText(this.curriculum.getSumEctsCredits().toString());
        sumEctsCreditsCell.setClasses(renderer.getGradeCellClass());

        final HtmlTableCell emptyCell = row.createCell();
        emptyCell.setClasses(renderer.getGradeCellClass());
        emptyCell.setColspan(2);
    }

    protected HtmlLink createGradingTableGeneratorLink(final String text, final ExecutionYear executionYear) {
        final HtmlLink result = new HtmlLink();
        result.setBody(new HtmlText(text));
        result.setParameter("executionYear", executionYear.getExternalId());
        result.setModuleRelative(false);
        result.setUrl("/ulisboaspecifications/ectsgradingtable/search/");
        return result;
    }

}
