/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: luis.egidio@qub-it.com
 *
 * 
 * This file is part of FenixEdu Specifications.
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
package org.fenixedu.ulisboa.specifications.ui.evaluation.managemarksheet.administrative;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.evaluation.markSheet.CompetenceCourseMarkSheet;
import org.fenixedu.academic.domain.evaluation.markSheet.CompetenceCourseMarkSheetChangeRequest;
import org.fenixedu.academic.domain.evaluation.markSheet.CompetenceCourseMarkSheetChangeRequestStateEnum;
import org.fenixedu.academic.domain.evaluation.markSheet.CompetenceCourseMarkSheetSnapshot;
import org.fenixedu.academic.domain.evaluation.markSheet.CompetenceCourseMarkSheetStateEnum;
import org.fenixedu.academic.domain.evaluation.season.EvaluationSeasonServices;
import org.fenixedu.academic.dto.evaluation.markSheet.report.CompetenceCourseSeasonReport;
import org.fenixedu.academic.services.evaluation.MarkSheetDocumentPrintService;
import org.fenixedu.academic.services.evaluation.MarkSheetStatusReportService;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.spreadsheet.SheetData;
import org.fenixedu.commons.spreadsheet.SpreadsheetBuilder;
import org.fenixedu.commons.spreadsheet.WorkbookExportFormat;
import org.fenixedu.ulisboa.specifications.domain.file.ULisboaSpecificationsTemporaryFile;
import org.fenixedu.ulisboa.specifications.dto.evaluation.markSheet.CompetenceCourseMarkSheetBean;
import org.fenixedu.ulisboa.specifications.service.evaluation.MarkSheetImportExportService;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Joiner;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@Component("org.fenixedu.ulisboa.specifications.evaluation.manageMarkSheet.administrative")
@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class,
        title = "label.title.evaluation.manageMarkSheet.administrative", accessGroup = "academic(MANAGE_MARKSHEETS)")
@RequestMapping(CompetenceCourseMarkSheetController.CONTROLLER_URL)
public class CompetenceCourseMarkSheetController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL =
            "/fenixedu-ulisboa-specifications/evaluation/managemarksheet/administrative/competencecoursemarksheet";

    private static final String JSP_PATH = CONTROLLER_URL.substring(1);

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

    @RequestMapping
    public String home(final Model model) {
        return "forward:" + CONTROLLER_URL + "/";
    }

    private CompetenceCourseMarkSheetBean getCompetenceCourseMarkSheetBean(final Model model) {
        return (CompetenceCourseMarkSheetBean) model.asMap().get("competenceCourseMarkSheetBean");
    }

    private void setCompetenceCourseMarkSheetBean(final CompetenceCourseMarkSheetBean bean, final Model model) {
        model.addAttribute("competenceCourseMarkSheetBeanJson", getBeanJson(bean));
        model.addAttribute("competenceCourseMarkSheetBean", bean);
    }

    private CompetenceCourseMarkSheet getCompetenceCourseMarkSheet(final Model model) {
        return (CompetenceCourseMarkSheet) model.asMap().get("competenceCourseMarkSheet");
    }

    private void setCompetenceCourseMarkSheet(final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model) {
        model.addAttribute("competenceCourseMarkSheet", competenceCourseMarkSheet);
    }

    private static final String _SEARCH_URI = "/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI)
    public String search(@RequestParam(value = "executionsemester", required = false) ExecutionInterval executionSemester,
            @RequestParam(value = "competencecourse", required = false) CompetenceCourse competenceCourse, final Model model) {
        final List<CompetenceCourseMarkSheet> searchResultsDataSet =
                filterSearch(executionSemester, competenceCourse, (CompetenceCourseMarkSheetStateEnum) null,
                        (EvaluationSeason) null, (CompetenceCourseMarkSheetChangeRequestStateEnum) null);

        final CompetenceCourseMarkSheetBean bean = new CompetenceCourseMarkSheetBean();
        bean.update();
        setCompetenceCourseMarkSheetBean(bean, model);

        model.addAttribute("searchcompetencecoursemarksheetResultsDataSet", searchResultsDataSet);
        return jspPage("search");
    }

    @RequestMapping(value = _SEARCH_URI, method = RequestMethod.POST)
    public String search(@RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean,
            final Model model) {

        setCompetenceCourseMarkSheetBean(bean, model);

        model.addAttribute("searchcompetencecoursemarksheetResultsDataSet", filterSearch(bean.getExecutionSemester(),
                bean.getCompetenceCourse(), bean.getMarkSheetState(), bean.getEvaluationSeason(), bean.getChangeRequestState()));

        return jspPage("search");
    }

    private static final String _SEARCHPOSTBACK_URI = "/searchpostback/";
    public static final String SEARCHPOSTBACK_URL = CONTROLLER_URL + _SEARCHPOSTBACK_URI;

    @RequestMapping(value = _SEARCHPOSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> searchpostback(
            @RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean, final Model model) {

        bean.update();
        this.setCompetenceCourseMarkSheetBean(bean, model);
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    private Stream<CompetenceCourseMarkSheet> getSearchUniverseSearchDataSet(final ExecutionInterval interval,
            final CompetenceCourse competence, final CompetenceCourseMarkSheetStateEnum markSheetState,
            final EvaluationSeason season, CompetenceCourseMarkSheetChangeRequestStateEnum changeRequestState) {

        return CompetenceCourseMarkSheet.findBy(interval, competence, (ExecutionCourse) null, season, (DateTime) null,
                (Set<Shift>) null, markSheetState, changeRequestState);
    }

    private List<CompetenceCourseMarkSheet> filterSearch(final ExecutionInterval executionInterval,
            final CompetenceCourse competenceCourse, final CompetenceCourseMarkSheetStateEnum markSheetState,
            final EvaluationSeason evaluationSeason, final CompetenceCourseMarkSheetChangeRequestStateEnum changeRequestState) {

        return getSearchUniverseSearchDataSet(executionInterval, competenceCourse, markSheetState, evaluationSeason,
                changeRequestState).collect(Collectors.toList());
    }

    private static final String _SEARCH_TO_VIEW_ACTION_URI = "/search/view/";
    public static final String SEARCH_TO_VIEW_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_VIEW_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_VIEW_ACTION_URI + "{oid}")
    public String processSearchToViewAction(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            final Model model, final RedirectAttributes redirectAttributes) {

        return redirect(READ_URL + competenceCourseMarkSheet.getExternalId(), model, redirectAttributes);
    }

    private static final String _READ_URI = "/read/";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI + "{oid}")
    public String read(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model) {
        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);
        return jspPage("read");
    }

    private static final String _DELETE_URI = "/delete/";
    public static final String DELETE_URL = CONTROLLER_URL + _DELETE_URI;

    @RequestMapping(value = _DELETE_URI + "{oid}", method = RequestMethod.POST)
    public String delete(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model,
            final RedirectAttributes redirectAttributes) {

        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);
        try {
            competenceCourseMarkSheet.delete();

            addInfoMessage(ULisboaSpecificationsUtil.bundle("label.success.delete"), model);
            return redirect(CONTROLLER_URL, model, redirectAttributes);

        } catch (Exception ex) {
            addErrorMessage(ex.getLocalizedMessage(), model);
        }

        return jspPage("read/" + getCompetenceCourseMarkSheet(model).getExternalId());
    }

    @RequestMapping(value = "/read/{oid}/updateevaluations")
    public String processReadToUpdateEvaluations(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            final Model model, final RedirectAttributes redirectAttributes) {

        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        return redirect(UPDATEEVALUATIONS_URL + getCompetenceCourseMarkSheet(model).getExternalId(), model, redirectAttributes);
    }

    private static final String _UPDATE_URI = "/update/";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI + "{oid}", method = RequestMethod.GET)
    public String update(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model) {
        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        final CompetenceCourseMarkSheetBean bean = new CompetenceCourseMarkSheetBean(competenceCourseMarkSheet);
        this.setCompetenceCourseMarkSheetBean(bean, model);

        return jspPage("update");
    }

    private static final String _UPDATEPOSTBACK_URI = "/updatepostback/";
    public static final String UPDATEPOSTBACK_URL = CONTROLLER_URL + _UPDATEPOSTBACK_URI;

    @RequestMapping(value = _UPDATEPOSTBACK_URI + "{oid}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> updatepostback(
            @PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            @RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean, final Model model) {

        bean.update();
        this.setCompetenceCourseMarkSheetBean(bean, model);
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    @RequestMapping(value = _UPDATE_URI + "{oid}", method = RequestMethod.POST)
    public String update(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            @RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {

        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        try {

            competenceCourseMarkSheet.edit(bean.getEvaluationDate(), bean.getGradeScale(), bean.getCertifier(),
                    bean.getExpireDate());

            return redirect(READ_URL + getCompetenceCourseMarkSheet(model).getExternalId(), model, redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(de.getLocalizedMessage(), model);
            setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);
            this.setCompetenceCourseMarkSheetBean(bean, model);

            return jspPage("update");
        }
    }

    private static final String _CREATE_URI = "/create/";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.GET)
    public String create(final Model model) {
        final CompetenceCourseMarkSheetBean bean = new CompetenceCourseMarkSheetBean();
        this.setCompetenceCourseMarkSheetBean(bean, model);

        return jspPage("create");
    }

    private static final String _CREATEPOSTBACK_URI = "/createpostback/";
    public static final String CREATEPOSTBACK_URL = CONTROLLER_URL + _CREATEPOSTBACK_URI;

    @RequestMapping(value = _CREATEPOSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> createpostback(
            @RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean, final Model model) {

        bean.update();
        this.setCompetenceCourseMarkSheetBean(bean, model);
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
    public String create(@RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean,
            final Model model, final RedirectAttributes redirectAttributes) {

        try {
            final CompetenceCourseMarkSheet markSheet = CompetenceCourseMarkSheet.create(bean.getExecutionSemester(),
                    bean.getCompetenceCourse(), bean.getExecutionCourse(), bean.getEvaluationSeason(), bean.getCourseEvaluation(),
                    bean.getEvaluationDate(), bean.getCertifier(), bean.getShifts(), false);

            model.addAttribute("competenceCourseMarkSheet", markSheet);
            return redirect(UPDATEEVALUATIONS_URL + getCompetenceCourseMarkSheet(model).getExternalId(), model,
                    redirectAttributes);

        } catch (Exception de) {

            addErrorMessage(de.getLocalizedMessage(), model);
            this.setCompetenceCourseMarkSheetBean(bean, model);
            return jspPage("create");
        }
    }

    private static final String _UPDATEEVALUATIONS_URI = "/updateevaluations/";
    public static final String UPDATEEVALUATIONS_URL = CONTROLLER_URL + _UPDATEEVALUATIONS_URI;

    @RequestMapping(value = _UPDATEEVALUATIONS_URI + "{oid}", method = RequestMethod.GET)
    public String updateevaluations(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            final Model model) {
        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        final CompetenceCourseMarkSheetBean bean = new CompetenceCourseMarkSheetBean(competenceCourseMarkSheet);
        bean.getUpdateGradeBeans().forEach(i -> i.updateViewStudentCurriculum(request));

        this.setCompetenceCourseMarkSheetBean(bean, model);
        return jspPage("updateevaluations");
    }

    private static final String _UPDATEEVALUATIONSPOSTBACK_URI = "/updateevaluationspostback/";
    public static final String UPDATEEVALUATIONSPOSTBACK_URL = CONTROLLER_URL + _UPDATEEVALUATIONSPOSTBACK_URI;

    @RequestMapping(value = _UPDATEEVALUATIONSPOSTBACK_URI + "{oid}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> updateevaluationspostback(
            @PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            @RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean, final Model model) {

        this.setCompetenceCourseMarkSheetBean(bean, model);
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    @RequestMapping(value = _UPDATEEVALUATIONS_URI + "{oid}", method = RequestMethod.POST)
    public String updateevaluations(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            @RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {
        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        try {
            bean.updateGrades();

            return redirect(READ_URL + getCompetenceCourseMarkSheet(model).getExternalId(), model, redirectAttributes);
        } catch (Exception de) {
            addErrorMessage(de.getLocalizedMessage(), model);
            this.setCompetenceCourseMarkSheetBean(bean, model);

            return jspPage("updateevaluations");
        }
    }

    @RequestMapping(value = "/read/{oid}/updategradeavailabledates")
    public String processReadToUpdateGradeAvailableDates(
            @PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model,
            final RedirectAttributes redirectAttributes) {

        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        return redirect(UPDATEGRADEAVAILABLEDATES_URL + getCompetenceCourseMarkSheet(model).getExternalId(), model,
                redirectAttributes);
    }

    private static final String _UPDATEGRADEAVAILABLEDATES_URI = "/updategradeavailabledates/";
    public static final String UPDATEGRADEAVAILABLEDATES_URL = CONTROLLER_URL + _UPDATEGRADEAVAILABLEDATES_URI;

    @RequestMapping(value = _UPDATEGRADEAVAILABLEDATES_URI + "{oid}", method = RequestMethod.GET)
    public String updategradeavailabledates(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            final Model model) {
        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        final CompetenceCourseMarkSheetBean bean = new CompetenceCourseMarkSheetBean(competenceCourseMarkSheet);
        bean.getUpdateGradeAvailableDateBeans().forEach(i -> i.updateViewStudentCurriculum(request));

        this.setCompetenceCourseMarkSheetBean(bean, model);
        return jspPage("updategradeavailabledates");
    }

    private static final String _UPDATEGRADEAVAILABLEDATESPOSTBACK_URI = "/updategradeavailabledatespostback/";
    public static final String UPDATEGRADEAVAILABLEDATESPOSTBACK_URL = CONTROLLER_URL + _UPDATEGRADEAVAILABLEDATESPOSTBACK_URI;

    @RequestMapping(value = _UPDATEGRADEAVAILABLEDATESPOSTBACK_URI + "{oid}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> updategradeavailabledatespostback(
            @PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            @RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean, final Model model) {

        this.setCompetenceCourseMarkSheetBean(bean, model);
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    @RequestMapping(value = _UPDATEGRADEAVAILABLEDATES_URI + "{oid}", method = RequestMethod.POST)
    public String updategradeavailabledates(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            @RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {
        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        try {
            bean.updateGradeAvailableDates();

            return redirect(READ_URL + getCompetenceCourseMarkSheet(model).getExternalId(), model, redirectAttributes);
        } catch (Exception de) {
            addErrorMessage(de.getLocalizedMessage(), model);
            this.setCompetenceCourseMarkSheetBean(bean, model);

            return jspPage("updategradeavailabledates");
        }
    }

    private static final String _PRINT_URI = "/print/";
    public static final String PRINT_URL = CONTROLLER_URL + _PRINT_URI;

    @RequestMapping(value = _PRINT_URI + "{oid}")
    public void print(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model,
            final HttpServletResponse response) throws IOException {

        final CompetenceCourse competenceCourse = competenceCourseMarkSheet.getCompetenceCourse();
        final String filename = competenceCourse.getCode() + "_"
                + competenceCourse.getName().replace(' ', '_').replace('/', '-').replace('\\', '-')
                + competenceCourseMarkSheet.getEvaluationDatePresentation().replace(' ', '_').replace(':', '-') + ".pdf";

        writeFile(response, filename, MarkSheetDocumentPrintService.PDF,
                MarkSheetDocumentPrintService.print(competenceCourseMarkSheet));
    }

    private static final String _PRINT_SNAPSHOT_URI = "/printsnapshot/";
    public static final String PRINT_SNAPSHOT_URL = CONTROLLER_URL + _PRINT_SNAPSHOT_URI;

    @RequestMapping(value = _PRINT_SNAPSHOT_URI + "{oid}")
    public void printSnapshot(@PathVariable("oid") final CompetenceCourseMarkSheetSnapshot snapshot, final Model model,
            final HttpServletResponse response) throws IOException {

        final String filename = snapshot.getCompetenceCourseCode() + "_"
                + snapshot.getCompetenceCourseName().getContent().replace(' ', '_').replace('/', '-').replace('\\', '-')
                + snapshot.getEvaluationDatePresentation().replace(' ', '_').replace(':', '-') + ".pdf";

        writeFile(response, filename, MarkSheetDocumentPrintService.PDF, MarkSheetDocumentPrintService.print(snapshot));
    }

    private static final String _CONFIRM_URI = "/confirm/";
    public static final String CONFIRM_URL = CONTROLLER_URL + _CONFIRM_URI;

    @RequestMapping(value = _CONFIRM_URI + "{oid}", method = RequestMethod.POST)
    public String confirm(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model,
            final RedirectAttributes redirectAttributes) {

        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        try {
            competenceCourseMarkSheet.confirm(false);

        } catch (Exception de) {
            addErrorMessage(de.getLocalizedMessage(), model);

            return jspPage("read");

        }

        return redirect(READ_URL + competenceCourseMarkSheet.getExternalId(), model, redirectAttributes);
    }

    private static final String _SUBMIT_URI = "/submit/";
    public static final String SUBMIT_URL = CONTROLLER_URL + _SUBMIT_URI;

    @RequestMapping(value = _SUBMIT_URI + "{oid}", method = RequestMethod.POST)
    public String submit(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model,
            final RedirectAttributes redirectAttributes) {

        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        try {
            new CompetenceCourseMarkSheetBean(competenceCourseMarkSheet).checkRulesForSubmission();
            competenceCourseMarkSheet.submit(false);

        } catch (Exception de) {
            addErrorMessage(de.getLocalizedMessage(), model);

            return jspPage("read");

        }

        return redirect(READ_URL + competenceCourseMarkSheet.getExternalId(), model, redirectAttributes);
    }

    private static final String _REVERT_TO_EDITION_URI = "/reverttoedition/";
    public static final String REVERT_TO_EDITION_URL = CONTROLLER_URL + _REVERT_TO_EDITION_URI;

    @RequestMapping(value = _REVERT_TO_EDITION_URI + "{oid}", method = RequestMethod.POST)
    public String revertToEdition(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            final Model model, final RedirectAttributes redirectAttributes) {

        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        try {
            // TODO: add reason
            competenceCourseMarkSheet.revertToEdition(false, null);

        } catch (Exception de) {
            addErrorMessage(de.getLocalizedMessage(), model);

            return jspPage("read");

        }

        return redirect(READ_URL + competenceCourseMarkSheet.getExternalId(), model, redirectAttributes);
    }

    private static final String _EXPORT_EXCEL_URI = "/exportexcel/";
    public static final String EXPORT_EXCEL_URL = CONTROLLER_URL + _EXPORT_EXCEL_URI;

    @RequestMapping(value = _EXPORT_EXCEL_URI + "{oid}", method = RequestMethod.GET)
    public void exportExcel(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model,
            final HttpServletResponse response) throws IOException {

        final CompetenceCourse competenceCourse = competenceCourseMarkSheet.getCompetenceCourse();
        final String filename = competenceCourse.getCode() + "_"
                + competenceCourse.getName().replace(' ', '_').replace('/', '-').replace('\\', '-')
                + competenceCourseMarkSheet.getEvaluationDatePresentation().replace(' ', '_').replace(':', '-')
                + MarkSheetImportExportService.XLSX_EXTENSION;

        writeFile(response, filename, MarkSheetImportExportService.XLSX_MIME_TYPE,
                MarkSheetImportExportService.exportToXLSX(new CompetenceCourseMarkSheetBean(competenceCourseMarkSheet)));
    }

    private static final String _IMPORT_EXCEL_URI = "/importexcel/";
    public static final String IMPORT_EXCEL_URL = CONTROLLER_URL + _IMPORT_EXCEL_URI;

    @RequestMapping(value = _IMPORT_EXCEL_URI + "{oid}", method = RequestMethod.POST)
    public String importExcel(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            @RequestParam(value = "file", required = true) MultipartFile markSheetFile, final Model model,
            final RedirectAttributes redirectAttributes) throws IOException {

        CompetenceCourseMarkSheetBean bean = null;
        try {
            bean = MarkSheetImportExportService.importFromXLSX(competenceCourseMarkSheet,
                    FilenameUtils.getName(markSheetFile.getOriginalFilename()), markSheetFile.getBytes());
            bean.updateGrades();

            addInfoMessage(ULisboaSpecificationsUtil.bundle("label.event.evaluation.manageMarkSheet.importExcel.success"), model);

        } catch (Exception e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);
        setCompetenceCourseMarkSheetBean(bean == null ? new CompetenceCourseMarkSheetBean(competenceCourseMarkSheet) : bean,
                model);

        return jspPage("updateevaluations");

    }

    private static final String _SEARCH_CHANGE_REQUESTS_URI = "/searchchangerequests/";
    public static final String SEARCH_CHANGE_REQUESTS_URL = CONTROLLER_URL + _SEARCH_CHANGE_REQUESTS_URI;

    @RequestMapping(value = _SEARCH_CHANGE_REQUESTS_URI + "{oid}", method = RequestMethod.GET)
    public String searchChangeRequests(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            final Model model, final RedirectAttributes redirectAttributes) throws IOException {

        setCompetenceCourseMarkSheetBean(new CompetenceCourseMarkSheetBean(competenceCourseMarkSheet), model);
        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        return jspPage("searchchangerequests");
    }

    private static final String _AUTHORIZE_CHANGE_REQUEST_URI = "/authorizechangerequest/";
    public static final String AUTHORIZE_CHANGE_REQUEST_URL = CONTROLLER_URL + _AUTHORIZE_CHANGE_REQUEST_URI;

    @RequestMapping(value = _AUTHORIZE_CHANGE_REQUEST_URI + "{oid}/{changeRequestId}", method = RequestMethod.POST)
    public String authorizeChangeRequest(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            @PathVariable("changeRequestId") final CompetenceCourseMarkSheetChangeRequest changeRequest,
            @RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean, final Model model,
            final RedirectAttributes redirectAttributes) throws IOException {

        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        try {

            changeRequest.authorize(Authenticate.getUser().getPerson(), bean.getChangeRequestComments(), bean.getExpireDate());

            return redirect(SEARCH_CHANGE_REQUESTS_URL + getCompetenceCourseMarkSheet(model).getExternalId(), model,
                    redirectAttributes);
        } catch (Exception de) {
            addErrorMessage(de.getLocalizedMessage(), model);
            this.setCompetenceCourseMarkSheetBean(bean, model);

            return jspPage("searchchangerequests");
        }
    }

    private static final String _CLOSE_CHANGE_REQUESTS_URI = "/closechangerequest/";
    public static final String CLOSE_CHANGE_REQUESTS_URL = CONTROLLER_URL + _CLOSE_CHANGE_REQUESTS_URI;

    @RequestMapping(value = _CLOSE_CHANGE_REQUESTS_URI + "{oid}/{changeRequestId}", method = RequestMethod.POST)
    public String closeChangeRequest(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            @PathVariable("changeRequestId") final CompetenceCourseMarkSheetChangeRequest changeRequest,
            @RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean, final Model model,
            final RedirectAttributes redirectAttributes) throws IOException {

        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        try {

            changeRequest.close(Authenticate.getUser().getPerson(), bean.getChangeRequestComments());

            return redirect(SEARCH_CHANGE_REQUESTS_URL + getCompetenceCourseMarkSheet(model).getExternalId(), model,
                    redirectAttributes);
        } catch (Exception de) {
            addErrorMessage(de.getLocalizedMessage(), model);
            this.setCompetenceCourseMarkSheetBean(bean, model);

            return jspPage("searchchangerequests");
        }
    }

    private byte[] exportReportToXLS(final CompetenceCourseMarkSheetBean bean) {

        final List<CompetenceCourseSeasonReport> entries = MarkSheetStatusReportService
                .getReportsForCompetenceCourses(bean.getExecutionSemester(), bean.getEvaluationSeasonsToReport());

        final SpreadsheetBuilder builder = new SpreadsheetBuilder();
        builder.addSheet(reportLabelFor("sheetTitle"), new SheetData<CompetenceCourseSeasonReport>(entries) {
            @Override
            protected void makeLine(CompetenceCourseSeasonReport entry) {
                addCell(reportLabelFor("period"), entry.getExecutionSemester().getQualifiedName());
                addCell(reportLabelFor("curricularCourseCode"), entry.getCompetenceCourse().getCode());
                addCell(reportLabelFor("curricularCourseName"), entry.getCompetenceCourse().getName());
                addCell(reportLabelFor("executionCourses"), entry.getExecutionCourses());
                addCell(reportLabelFor("season"), EvaluationSeasonServices.getDescriptionI18N(entry.getSeason()).getContent());
                addCell(reportLabelFor("totalStudents"), entry.getTotalStudents());
                addCell(reportLabelFor("notEvaluatedStudents"), entry.getNotEvaluatedStudents());
                addCell(reportLabelFor("evaluatedStudents"), entry.getEvaluatedStudents());
                addCell(reportLabelFor("marksheetsTotal"), entry.getMarksheetsTotal());
                addCell(reportLabelFor("marksheetsToConfirm"), entry.getMarksheetsToConfirm());
                addCell(reportLabelFor("responsibleName"), Joiner.on("; ").join(entry.getResponsibleNames()));
                addCell(reportLabelFor("responsibleContact"), Joiner.on("; ").join(entry.getResponsibleEmails()));
            }
        });

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            builder.build(WorkbookExportFormat.EXCEL, byteArrayOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }

    static private String reportLabelFor(final String field) {
        return BundleUtil.getString(ULisboaConstants.BUNDLE, "label.MarksheetStatusReport.report." + field);
    }

    @RequestMapping(value = "/exportreport", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> exportReport(
            @RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean, final Model model) {

        final String reportId = UUID.randomUUID().toString();
        new Thread(() -> processReport(this::exportReportToXLS, bean, reportId)).start();

        return new ResponseEntity<String>(reportId, HttpStatus.OK);
    }

    @Atomic(mode = TxMode.READ)
    protected void processReport(final Function<CompetenceCourseMarkSheetBean, byte[]> reportProcessor,
            final CompetenceCourseMarkSheetBean bean, final String reportId) {

        byte[] content = null;
        try {
            content = reportProcessor.apply(bean);
        } catch (Throwable e) {
            content = createXLSWithError((e instanceof DomainException) ? ((DomainException) e)
                    .getLocalizedMessage() : ExceptionUtils.getFullStackTrace(e));
        }

        ULisboaSpecificationsTemporaryFile.create(reportId, content, Authenticate.getUser());
    }

    private byte[] createXLSWithError(String error) {

        try {

            final SpreadsheetBuilder builder = new SpreadsheetBuilder();
            builder.addSheet("Registrations", new SheetData<String>(Collections.singleton(error)) {
                @Override
                protected void makeLine(String item) {
                    addCell(ULisboaSpecificationsUtil.bundle("label.unexpected.error.occured"), item);
                }
            });

            final ByteArrayOutputStream result = new ByteArrayOutputStream();
            builder.build(WorkbookExportFormat.EXCEL, result);

            return result.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/exportstatus/{reportId}", method = RequestMethod.GET, produces = "text/plain;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> exportStatus(@PathVariable(value = "reportId") final String reportId,
            final Model model) {
        return new ResponseEntity<String>(
                String.valueOf(
                        ULisboaSpecificationsTemporaryFile.findByUserAndFilename(Authenticate.getUser(), reportId).isPresent()),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/downloadreport/{reportId}", method = RequestMethod.GET)
    public void downloadReport(@RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean,
            @PathVariable("reportId") String reportId, final Model model, RedirectAttributes redirectAttributes,
            HttpServletResponse response) throws IOException {
        final Optional<ULisboaSpecificationsTemporaryFile> temporaryFile =
                ULisboaSpecificationsTemporaryFile.findByUserAndFilename(Authenticate.getUser(), reportId);
        writeFile(response, getFileName(null/* TODO legidio bean.getExecutionSemester() */), "application/vnd.ms-excel",
                temporaryFile.get().getContent());
    }

    private String getFileName(final ExecutionInterval executionInterval) {
        final org.fenixedu.academic.domain.organizationalStructure.Unit institutionUnit =
                Bennu.getInstance().getInstitutionUnit();
        final String acronym = institutionUnit.getAcronym();

        final String title =
                acronym + "_" + ULisboaSpecificationsUtil.bundle("label.MarkSheetStatusReport.create").replace(" ", "-") + "_";

        final String period =
                executionInterval == null ? "" : executionInterval.getQualifiedName().replace("/", "-").replace(" ", "-") + "_";

        return title + period + new DateTime().toString("yyyy-MM-dd_HH-mm-ss") + ".xls";
    }

}
