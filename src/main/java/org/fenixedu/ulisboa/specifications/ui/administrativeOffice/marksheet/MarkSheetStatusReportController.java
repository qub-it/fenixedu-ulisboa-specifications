/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and ServiÃ§os Partilhados da
 * Universidade de Lisboa:
 *  - Copyright Â© 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright Â© 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: xpto@qub-it.com
 *
 * 
 * This file is part of FenixEdu Academic.
 *
 * FenixEdu Academic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.marksheet;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.spreadsheet.SheetData;
import org.fenixedu.commons.spreadsheet.SpreadsheetBuilder;
import org.fenixedu.commons.spreadsheet.WorkbookExportFormat;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.joda.time.DateTime;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Joiner;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.generateMarkSheetStatusReport",
        accessGroup = "#managers")
@RequestMapping(MarkSheetStatusReportController.CONTROLLER_URL)
public class MarkSheetStatusReportController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/ulisboaspecifications/marksheet/statusreport";
    public static final String JSP_PATH = "fenixedu-ulisboa-specifications/marksheet/statusreport";

    @RequestMapping(method = GET)
    public String home(Model model, RedirectAttributes redirectAttributes) {
        return redirect(SEARCH_URL, model, redirectAttributes);
    }

    private String jspPage(final String page) {
        return JSP_PATH + page;
    }

    private static final String _SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI, method = RequestMethod.GET)
    public String search(Model model) {
        List<ExecutionInterval> executionIntervals = ExecutionSemester.readNotClosedExecutionPeriods().stream()
                .sorted((e1, e2) -> ExecutionSemester.COMPARATOR_BY_BEGIN_DATE.compare(e2, e1)).collect(Collectors.toList());
        model.addAttribute("executionIntervals", executionIntervals);
        return jspPage(_SEARCH_URI);
    }

    @RequestMapping(value = _SEARCH_URI, method = RequestMethod.POST)
    public void search(@RequestParam(value = "executionInterval", required = true) ExecutionInterval executionInterval,
            Model model, RedirectAttributes redirectAttributes, HttpServletResponse response) {
        final MarkSheetStatusReportService service = new MarkSheetStatusReportService();
        final List<CurricularCourseSeasonReport> entries = service.generateCurricularCourseReport(executionInterval);

        final SpreadsheetBuilder builder = new SpreadsheetBuilder();
        builder.addSheet(reportLabelFor("sheetTitle"), new SheetData<CurricularCourseSeasonReport>(entries) {
            @Override
            protected void makeLine(CurricularCourseSeasonReport entry) {
                addCell(reportLabelFor("period"), entry.getExecutionSemester().getQualifiedName());
                addCell(reportLabelFor("season"), entry.getSeason().getName().getContent());
                addCell(reportLabelFor("evaluationDate"), entry.getEvaluationDate());
                addCell(reportLabelFor("curricularCourseCode"), entry.getCurricularCourse().getCode());
                addCell(reportLabelFor("curricularCourseName"), entry.getCurricularCourse().getName());
                addCell(reportLabelFor("degreeCode"), entry.getCurricularCourse().getDegree().getCode());
                addCell(reportLabelFor("degreeName"), entry.getCurricularCourse().getDegree().getNameI18N().getContent());
                addCell(reportLabelFor("degreeType"), entry.getCurricularCourse().getDegreeType().getName().getContent());
                addCell(reportLabelFor("degreeCurricularPlan"), entry.getCurricularCourse().getDegreeCurricularPlan().getName());
                addCell(reportLabelFor("notEvaluatedStudents"), entry.getNotEvaluatedStudents());
                addCell(reportLabelFor("evaluatedStudents"), entry.getEvaluatedStudents());
                addCell(reportLabelFor("marksheetsToConfirm"), entry.getMarksheetsToConfirm());
                addCell(reportLabelFor("responsibleName"), Joiner.on(';').join(entry.getResponsibleNames()));
                addCell(reportLabelFor("responsibleContact"), Joiner.on(';').join(entry.getResponsibleEmails()));
            }
        });

        BufferedOutputStream bufferedOutputStream = null;
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);

            builder.build(WorkbookExportFormat.EXCEL, bufferedOutputStream);

            writeFile(response, "MarksheetStatusReport_" + new DateTime().toString("yyyy-MM-dd_HH-mm-ss") + ".xls",
                    "application/octet-stream", byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (bufferedOutputStream != null) {
                IOUtils.closeQuietly(bufferedOutputStream);
            }
        }
        return;
    }

    private String reportLabelFor(final String field) {
        return BundleUtil.getString(ULisboaConstants.BUNDLE, "label.MarksheetStatusReport.report." + field);
    }

}
