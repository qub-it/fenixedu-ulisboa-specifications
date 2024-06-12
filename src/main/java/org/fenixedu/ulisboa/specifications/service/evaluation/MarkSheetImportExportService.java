package org.fenixedu.ulisboa.specifications.service.evaluation;

import java.io.IOException;

import com.qubit.qubEdu.module.base.util.SheetProcessor;
import com.qubit.qubEdu.module.base.util.XLSxUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.fenixedu.academic.domain.evaluation.markSheet.CompetenceCourseMarkSheet;
import org.fenixedu.academic.dto.evaluation.markSheet.MarkBean;
import org.fenixedu.ulisboa.specifications.dto.evaluation.markSheet.CompetenceCourseMarkSheetBean;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import static com.qubit.qubEdu.module.base.util.XLSxUtil.*;

//TODO: improve errors
public class MarkSheetImportExportService {

    private static final int EXPECTED_COLUMNS = 3;

    static public final String XLSX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    static public byte[] exportToXLSX(final CompetenceCourseMarkSheetBean bean) {

        final XSSFWorkbook workbook = createWorkbook();
        final Sheet sheet = createSheet(workbook, ULisboaSpecificationsUtil.bundle("label.CompetenceCourseMarkSheet"));

        writeHeader(sheet);
        writeRows(bean, sheet);

        return writeFile(workbook);
    }

    private static void writeHeader(final Sheet sheet) {
        final Row row = sheet.createRow(0);
        final String[] headers = {

                ULisboaSpecificationsUtil.bundle("label.MarkBean.studentNumber"),

                ULisboaSpecificationsUtil.bundle("label.MarkBean.studentName"),

                ULisboaSpecificationsUtil.bundle("label.MarkBean.gradeValue")

        };

        final Workbook workbook = sheet.getWorkbook();
        final CellStyle style = workbook.createCellStyle();
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillBackgroundColor(IndexedColors.DARK_BLUE.getIndex());
        final Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = createTextCellWithValue(row, i, headers[i]);
            cell.setCellStyle(style);
        }

    }

    private static void writeRows(CompetenceCourseMarkSheetBean bean, final Sheet sheet) {
        int rowIndex = 1;
        for (final MarkBean markBean : bean.getUpdateGradeBeans()) {
            final String[] values =
                    { markBean.getStudentNumber().toString(), markBean.getStudentName(), markBean.getGradeValue() };
            final Row row = sheet.createRow(rowIndex++);
            for (int i = 0; i < values.length; i++) {
                createTextCellWithValue(row, i, values[i]);
            }
        }
    }

    private static byte[] writeFile(final Workbook workbook) {
        try {
            return writeToWorkbook(workbook).toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class MarkSheetImportSheetProcessor extends SheetProcessor {

        private final ImmutableMap<String, MarkBean> indexedMarkBeans;

        private final CompetenceCourseMarkSheetBean result;

        public MarkSheetImportSheetProcessor(CompetenceCourseMarkSheet competenceCourseMarkSheet) {
            super();
            this.result = new CompetenceCourseMarkSheetBean(competenceCourseMarkSheet);
            this.indexedMarkBeans = Maps.uniqueIndex(this.result.getUpdateGradeBeans(),
                    e -> buildMarkIndexKey(e.getStudentNumber(), e.getStudentName()));

            setRowProcessor(row -> {
                final Integer studentNumber = Double.valueOf(getCellValueAsString(row, 0)).intValue();
                final String studentName = getCellValueAsString(row, 1);
                final String gradeValue = getCellValueAsString(row, 2);

                final String key = buildMarkIndexKey(studentNumber, studentName);
                if (!this.indexedMarkBeans.containsKey(key)) {
                    throw new RuntimeException(ULisboaSpecificationsUtil.bundle(
                            "error.MarkSheetImportExportService.student.not.found", studentNumber.toString(), studentName));
                }

                this.indexedMarkBeans.get(key).setGradeValue(gradeValue);
            });
        }

        public CompetenceCourseMarkSheetBean getResult() {
            return this.result;
        }
    }

    static public CompetenceCourseMarkSheetBean importFromXLSX(CompetenceCourseMarkSheet competenceCourseMarkSheet,
            final String filename, byte[] content) {

        MarkSheetImportSheetProcessor markSheetImportSheetProcessor = new MarkSheetImportSheetProcessor(competenceCourseMarkSheet);
        XLSxUtil.parseXLSX(filename, content, EXPECTED_COLUMNS, markSheetImportSheetProcessor);
        return markSheetImportSheetProcessor.getResult();
    }

    private static String buildMarkIndexKey(final Integer number, final String name) {
        return number + "-" + name;
    }

}
