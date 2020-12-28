package org.fenixedu.ulisboa.specifications.ui.renderers.student.curriculum;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.OptionalEnrolment;
import org.fenixedu.academic.domain.accessControl.AcademicAuthorizationGroup;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.academic.domain.groups.PermissionService;
import org.fenixedu.academic.domain.student.curriculum.AverageEntry;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.student.gradingTable.CourseGradingTable;
import org.fenixedu.academic.domain.student.gradingTable.DefaultGradingTable;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.ExternalEnrolment;
import org.fenixedu.academic.service.AcademicPermissionService;
import org.fenixedu.academic.ui.renderers.student.curriculum.CurriculumRenderer;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
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
            return container;
        }

        container.setStyle("padding-bottom: 3.5em;");

        final HtmlTable table = new HtmlTable();
        container.addChild(table);
        table.setClasses(renderer.getTableClass());

        generateAverageRows(table);

        return container;
    }

    static private HtmlText createHtmlTextItalic(final String message) {
        final HtmlText htmlText = new HtmlText(message);
        htmlText.setClasses("italic");
        return htmlText;
    }

    private void generateAverageRows(final HtmlTable mainTable) {

        final List<AverageEntry> entries = AverageEntry.getAverageEntries(this.curriculum);

        if (!entries.isEmpty()) {
            generateAverageTableHeader(mainTable);

            for (final AverageEntry entry : entries) {
                generateAverageRow(mainTable, entry);
            }
        }
    }

    private void generateAverageTableHeader(final HtmlTable table) {
        final HtmlTableRow row = table.createRow();
        row.setClasses(renderer.getHeaderRowClass());

        final String classes = renderer.getGradeCellClass();
        generateCellWithText(row, ULisboaSpecificationsUtil.bundle("label.curricularUnit"), classes)
                .setColspan(MAX_COL_SPAN_FOR_TEXT_ON_CURRICULUM_LINES);
        generateCellWithText(row, ULisboaSpecificationsUtil.bundle("label.gradingTables.curriculumRenderer.ectsGrade"), classes)
                .setStyle("width: 100px");
        generateCellWithText(row, BundleUtil.getString(Bundle.APPLICATION, "label.grade"), classes).setStyle("width: 35px");
        generateCellWithText(row, BundleUtil.getString(Bundle.APPLICATION, "label.weight"), classes).setStyle("width: 35px");
        generateCellWithText(row, ULisboaSpecificationsUtil.bundle("label.approvalType"), classes).setStyle("width: 75px");
        generateCellWithText(row, ULisboaSpecificationsUtil.bundle("label.approvalInfo"), classes).setStyle("width: 120px");
        generateCellWithText(row, ULisboaSpecificationsUtil.bundle("label.creditsInfo"), classes).setStyle("width: 120px");
        generateCellWithText(row, ULisboaSpecificationsUtil.bundle("label.approvementDate"), classes).setStyle("width: 80px");;
    }

    private void generateAverageRow(final HtmlTable mainTable, final AverageEntry averageEntry) {
        final HtmlTableRow row = mainTable.createRow();
        row.setClasses(renderer.getEnrolmentRowClass());

        final ICurriculumEntry entry = averageEntry.getEntry();
        generateCodeAndNameCell(row, entry);
        if (entry instanceof ExternalEnrolment) {
            generateExternalEnrolmentLabelCell(row, (ExternalEnrolment) entry);
        }
        generateEctsGradeCell(row, entry);
        generateGradeCell(row, entry);
        generateWeightCell(row, entry);
        generateCellWithText(row, averageEntry.getApprovalTypeDescription(), null).setStyle("font-size: xx-small");
        generateCellWithText(row, averageEntry.getEntryInfo(), null).setStyle("font-size: xx-small");
        generateCellWithText(row, averageEntry.getTargetCurriculumLinesInfo(), null).setStyle("font-size: xx-small");

        StudentCurricularPlanLayout.generateDate(averageEntry.getConclusionDateOnTarget(), row, (String) null, (String) null);
    }

    private void generateCodeAndNameCell(final HtmlTableRow enrolmentRow, final ICurriculumEntry entry) {

        final HtmlInlineContainer inlineContainer = new HtmlInlineContainer();
        inlineContainer.addChild(new HtmlText(getPresentationNameFor(entry)));

        final HtmlTableCell cell = enrolmentRow.createCell();
        cell.setClasses(renderer.getLabelCellClass());
        cell.setColspan(MAX_COL_SPAN_FOR_TEXT_ON_CURRICULUM_LINES - (entry instanceof ExternalEnrolment ? 1 : 0));
        cell.setBody(inlineContainer);
    }

    static public String getPresentationNameFor(final ICurriculumEntry entry) {
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

                if (!isConclusionDocument() && (AcademicAuthorizationGroup.get(AcademicOperationType.MANAGE_CONCLUSION)
                        .isMember(Authenticate.getUser())
                        || AcademicPermissionService.hasAccess("ACADEMIC_OFFICE_CONCLUSION", Authenticate.getUser()))) {
                    generateCellWithLink(enrolmentRow, entry.getExecutionYear(),
                            ULisboaSpecificationsUtil.bundle("label.gradingTables.curriculumRenderer.generateInstitutionTable"));
                    return;
                }
            }

        } else if (entry instanceof CurriculumLine) {
            CurriculumLine line = (CurriculumLine) entry;
            if (line.getCurricularCourse() == null) {
                ectsGrade =
                        ULisboaSpecificationsUtil.bundle("label.gradingTables.curriculumRenderer.lineWithoutCurricularCourse");
            } else {
                CourseGradingTable table = CourseGradingTable.find(line);
                if (table != null) {
                    ectsGrade = table.getEctsGrade(grade);
                } else if (CourseGradingTable.isApplicable(line)) {
                    if (!isConclusionDocument() && (AcademicAuthorizationGroup.get(AcademicOperationType.MANAGE_CONCLUSION)
                            .isMember(Authenticate.getUser())
                            || PermissionService.hasAccess("ADMIN_OFFICE_CONCLUSION", Authenticate.getUser()))) {
                        generateCellWithLink(enrolmentRow, entry.getExecutionYear(),
                                ULisboaSpecificationsUtil.bundle("label.gradingTables.curriculumRenderer.generateCourseTable"));
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

    protected HtmlLink createGradingTableGeneratorLink(final String text, final ExecutionYear executionYear) {
        final HtmlLink result = new HtmlLink();
        result.setBody(new HtmlText(text));
        result.setParameter("executionYear", executionYear.getExternalId());
        result.setModuleRelative(false);
        result.setUrl("/ulisboaspecifications/ectsgradingtable/search/");
        return result;
    }

}
