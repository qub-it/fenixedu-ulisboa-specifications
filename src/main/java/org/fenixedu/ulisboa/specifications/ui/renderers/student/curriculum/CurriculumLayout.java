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
import org.joda.time.YearMonthDay;

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

        // qubExtension, SCPLayout is better for this purpose
        this.renderer.setVisibleCurricularYearEntries(false);
    }

    // qubExtension
    public static void register() {
        CurriculumRenderer.setLayoutProvider(renderer -> new CurriculumLayout(renderer));
    }

    @Override
    @SuppressWarnings("rawtypes")
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

            generateAverageTableRows(averageEntriesTable);
        }

        if (renderer.isVisibleCurricularYearEntries()) {
            final HtmlContainer curricularYearContainer = new HtmlBlockContainer();
            container.addChild(curricularYearContainer);
            final HtmlTable curricularYearTable = new HtmlTable();
            curricularYearContainer.addChild(curricularYearTable);
            curricularYearTable.setClasses(renderer.getTableClass());
            generateCurricularYearTableRows(curricularYearTable);
            generateCurricularYearSums(curricularYearTable);
        }

        return container;
    }

    private HtmlText createHtmlTextItalic(final String message) {
        final HtmlText htmlText = new HtmlText(message);
        htmlText.setClasses("italic");

        return htmlText;
    }

    private void generateAverageTableRows(final HtmlTable mainTable) {

        final Set<ICurriculumEntry> enrolmentEntries =
                new TreeSet<ICurriculumEntry>(ICurriculumEntry.COMPARATOR_BY_EXECUTION_PERIOD_AND_NAME_AND_ID);
        enrolmentEntries.addAll(this.curriculum.getEnrolmentRelatedEntries());
        if (!enrolmentEntries.isEmpty()) {
            generateAverageTableHeader(mainTable, ULisboaSpecificationsUtil.bundle("label.enrolment.approvals"));
            generateAverageRows(mainTable, enrolmentEntries);
        }

        final Set<ICurriculumEntry> equivalenceEntries =
                new TreeSet<ICurriculumEntry>(ICurriculumEntry.COMPARATOR_BY_EXECUTION_PERIOD_AND_NAME_AND_ID);
        final Set<ICurriculumEntry> substitutionsEntries =
                new TreeSet<ICurriculumEntry>(ICurriculumEntry.COMPARATOR_BY_EXECUTION_PERIOD_AND_NAME_AND_ID);
        for (final ICurriculumEntry entry : this.curriculum.getDismissalRelatedEntries()) {
            if (entry instanceof Dismissal) {
                equivalenceEntries.add(entry);
            } else {
                substitutionsEntries.add(entry);
            }
        }

        if (!substitutionsEntries.isEmpty()) {
            generateAverageTableHeader(mainTable, ULisboaSpecificationsUtil.bundle("label.substitution.approvals"));
            generateAverageRows(mainTable, substitutionsEntries);
        }

        if (!equivalenceEntries.isEmpty()) {
            generateAverageTableHeader(mainTable, ULisboaSpecificationsUtil.bundle("label.equivalence.approvals"));
            generateAverageRows(mainTable, equivalenceEntries);
        }
    }

    private void generateAverageTableHeader(final HtmlTable mainTable, final String text) {
        final HtmlTableRow row = mainTable.createRow();
        row.setClasses(renderer.getHeaderRowClass());

        final HtmlTableCell textCell = row.createCell();
        textCell.setText(text);
        textCell.setClasses(renderer.getGradeCellClass());
        textCell.setColspan(MAX_COL_SPAN_FOR_TEXT_ON_CURRICULUM_LINES);

        generateCellWithText(row, ULisboaSpecificationsUtil.bundle("label.gradingTables.curriculumRenderer.ectsGrade"),
                renderer.getGradeCellClass());
        generateCellWithText(row, BundleUtil.getString(Bundle.APPLICATION, "label.grade"), renderer.getGradeCellClass());
        generateCellWithText(row, BundleUtil.getString(Bundle.APPLICATION, "label.weight"), renderer.getGradeCellClass());
        generateCellWithText(row, ULisboaSpecificationsUtil.bundle("label.executionInterval"), renderer.getGradeCellClass());
        generateCellWithText(row, ULisboaSpecificationsUtil.bundle("label.curricularPeriod"), renderer.getGradeCellClass());
        generateCellWithText(row, ULisboaSpecificationsUtil.bundle("label.approvementDate"), renderer.getGradeCellClass());
    }

    private void generateAverageRows(HtmlTable mainTable, Set<ICurriculumEntry> entries) {
        for (final ICurriculumEntry entry : entries) {
            generateAverageRow(mainTable, entry);
        }
    }

    private void generateAverageRow(HtmlTable mainTable, final ICurriculumEntry entry) {
        final HtmlTableRow row = mainTable.createRow();
        row.setClasses(renderer.getEnrolmentRowClass());

        generateCodeAndNameCell(row, entry);
        if (entry instanceof ExternalEnrolment) {
            generateExternalEnrolmentLabelCell(row, (ExternalEnrolment) entry);
        }
        generateEctsGradeCell(row, entry);
        generateGradeCell(row, entry);
        generateWeightCell(row, entry);
        generateExecutionYearCell(row, entry);
        generateSemesterCell(row, entry);
        StudentCurricularPlanLayout.generateDate(
                entry.getCurriculumLinesForCurriculum().stream().filter(i -> i.getApprovementDate() != null)
                        .map(i -> i.getApprovementDate()).max(YearMonthDay::compareTo).orElse(null),
                row, (String) null, (String) null);
    }

    private void generateCodeAndNameCell(final HtmlTableRow enrolmentRow, final ICurriculumEntry entry) {

        final HtmlInlineContainer inlineContainer = new HtmlInlineContainer();
        inlineContainer.addChild(new HtmlText(getPresentationNameFor(entry)));

        final HtmlTableCell cell = enrolmentRow.createCell();
        cell.setClasses(renderer.getLabelCellClass());
        cell.setColspan(MAX_COL_SPAN_FOR_TEXT_ON_CURRICULUM_LINES - (entry instanceof ExternalEnrolment ? 1 : 0));
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
            final ExternalEnrolment externalEnrolment) {

        generateCellWithText(externalEnrolmentRow, externalEnrolment.getDescription(), renderer.getLabelCellClass());
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

        generateCellWithText(row, StudentCurricularPlanLayout.getCurricularPeriodLabel(entry),
                this.renderer.getEnrolmentSemesterCellClass()).setStyle("font-size: xx-small");
    }

    static private HtmlTableCell generateCellWithText(final HtmlTableRow row, final String text, final String cssClass) {
        return StudentCurricularPlanLayout.generateCellWithText(row, text, cssClass);
    }

    private void generateCellWithLink(final HtmlTableRow row, final ExecutionYear executionYear, final String linkText) {
        final HtmlInlineContainer inlineContainer = new HtmlInlineContainer();
        inlineContainer.addChild(createGradingTableGeneratorLink(linkText, executionYear));
        final HtmlTableCell cell = row.createCell();
        cell.setClasses(renderer.getGradeCellClass());
        cell.setBody(inlineContainer);
    }

    private void generateCurricularYearTableRows(final HtmlTable table) {
        final Set<ICurriculumEntry> sortedEntries =
                new TreeSet<ICurriculumEntry>(ICurriculumEntry.COMPARATOR_BY_EXECUTION_PERIOD_AND_NAME_AND_ID);
        sortedEntries.addAll(this.curriculum.getCurricularYearEntries());
        if (!sortedEntries.isEmpty()) {
            generateCurricularYearTableHeader(table);
            generateCurricularYearRows(table, sortedEntries);
        }
    }

    private void generateCurricularYearTableHeader(final HtmlTable table) {
        final HtmlTableRow row = table.createRow();
        row.setClasses(renderer.getHeaderRowClass());

        final HtmlTableCell textCell = row.createCell();
        textCell.setClasses(renderer.getGradeCellClass());
        textCell.setColspan(MAX_COL_SPAN_FOR_TEXT_ON_CURRICULUM_LINES);

        generateCellWithText(row, BundleUtil.getString(Bundle.APPLICATION, "label.ects"), renderer.getGradeCellClass());
        generateCellWithText(row, ULisboaSpecificationsUtil.bundle("label.executionInterval"), renderer.getGradeCellClass());
        generateCellWithText(row, ULisboaSpecificationsUtil.bundle("label.curricularPeriod"), renderer.getGradeCellClass());
    }

    private void generateCurricularYearRows(HtmlTable mainTable, Set<ICurriculumEntry> entries) {
        for (final ICurriculumEntry entry : entries) {
            generateCurricularYearRow(mainTable, entry);
        }
    }

    private void generateCurricularYearRow(HtmlTable mainTable, final ICurriculumEntry entry) {
        final HtmlTableRow enrolmentRow = mainTable.createRow();
        enrolmentRow.setClasses(renderer.getEnrolmentRowClass());

        generateCodeAndNameCell(enrolmentRow, entry);
        if (entry instanceof ExternalEnrolment) {
            generateExternalEnrolmentLabelCell(enrolmentRow, (ExternalEnrolment) entry);
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
