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
package org.fenixedu.ulisboa.specifications.ui.evaluation.managemarksheet.teacher;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FilenameUtils;
import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.ui.struts.action.teacher.ManageExecutionCourseDA;
import org.fenixedu.bennu.core.domain.exceptions.AuthorizationException;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.domain.MenuFunctionality;
import org.fenixedu.bennu.portal.model.Functionality;
import org.fenixedu.bennu.portal.servlet.BennuPortalDispatcher;
import org.fenixedu.bennu.portal.servlet.PortalLayoutInjector;
import org.fenixedu.bennu.struts.portal.RenderersAnnotationProcessor;
import org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheet;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.dto.evaluation.markSheet.CompetenceCourseMarkSheetBean;
import org.fenixedu.ulisboa.specifications.service.evaluation.MarkSheetDocumentPrintService;
import org.fenixedu.ulisboa.specifications.service.evaluation.MarkSheetImportExportService;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Maps;

@Controller
@RequestMapping(CompetenceCourseMarkSheetController.CONTROLLER_URL)
public class CompetenceCourseMarkSheetController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL =
            "/fenixedu-ulisboa-specifications/evaluation/managemarksheet/teacher/competencecoursemarksheet";

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

    @ModelAttribute
    private void setFunctionalityAndExecutionCourseContext(
            final @PathVariable("executionCourseId") ExecutionCourse executionCourse, final Model model,
            final HttpServletRequest request) {

        final Functionality functionality = RenderersAnnotationProcessor.getFunctionalityForType(ManageExecutionCourseDA.class);
        final MenuFunctionality menuItem =
                MenuFunctionality.findFunctionality(functionality.getProvider(), functionality.getKey());
        if (menuItem == null || !menuItem.isAvailableForCurrentUser()) {
            throw AuthorizationException.unauthorized();
        }

        BennuPortalDispatcher.selectFunctionality(request, menuItem);

        final Map<String, Object> requestContext = Maps.newHashMap();
        requestContext.put("professorship", findProfessorship(executionCourse));
        requestContext.put("executionCourse", executionCourse);
        PortalLayoutInjector.addContextExtension(requestContext);

        model.addAttribute("executionCourse", executionCourse);
    }

    private Professorship findProfessorship(final ExecutionCourse executionCourse) {
        final Professorship result = Authenticate.getUser().getPerson().getProfessorshipByExecutionCourse(executionCourse);
        if (result == null || !result.getPermissions().getEvaluationFinal()) {
            throw new ULisboaSpecificationsDomainException(Status.FORBIDDEN, "message.error.notAuthorized");
        }

        return result;
    }

    private static final String _SEARCH_URI = "/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI + "{executionCourseId}", method = RequestMethod.GET)
    public String search(@PathVariable("executionCourseId") final ExecutionCourse executionCourse, final Model model) {
        final List<CompetenceCourseMarkSheet> searchResultsDataSet = filterSearch(executionCourse);

        final CompetenceCourseMarkSheetBean bean = new CompetenceCourseMarkSheetBean();
        bean.update();
        setCompetenceCourseMarkSheetBean(bean, model);

        model.addAttribute("searchcompetencecoursemarksheetResultsDataSet", searchResultsDataSet);
        return jspPage("search");
    }

    private static final String _SEARCHPOSTBACK_URI = "/searchpostback/";
    public static final String SEARCHPOSTBACK_URL = CONTROLLER_URL + _SEARCHPOSTBACK_URI;

    @RequestMapping(value = _SEARCHPOSTBACK_URI + "{executionCourseId}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> searchpostback(
            @RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean, final Model model) {

        bean.update();
        this.setCompetenceCourseMarkSheetBean(bean, model);
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    private Stream<CompetenceCourseMarkSheet> getSearchUniverseSearchDataSet(final ExecutionCourse executionCourse) {
        return CompetenceCourseMarkSheet.findBy(executionCourse);
    }

    private List<CompetenceCourseMarkSheet> filterSearch(final ExecutionCourse executionCourse) {

        return getSearchUniverseSearchDataSet(executionCourse).collect(Collectors.toList());
    }

    private static final String _SEARCH_TO_VIEW_ACTION_URI = "/search/view/";
    public static final String SEARCH_TO_VIEW_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_VIEW_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_VIEW_ACTION_URI + "{executionCourseId}/{oid}")
    public String processSearchToViewAction(@PathVariable("executionCourseId") final ExecutionCourse executionCourse,
            @PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model,
            final RedirectAttributes redirectAttributes) {

        return redirect(READ_URL + executionCourse.getExternalId() + "/" + competenceCourseMarkSheet.getExternalId(), model,
                redirectAttributes);
    }

    private static final String _READ_URI = "/read/";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI + "{executionCourseId}/{oid}")
    public String read(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model) {
        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);
        return jspPage("read");
    }

    private static final String _DELETE_URI = "/delete/";
    public static final String DELETE_URL = CONTROLLER_URL + _DELETE_URI;

    @RequestMapping(value = _DELETE_URI + "{executionCourseId}/{oid}", method = RequestMethod.POST)
    public String delete(@PathVariable("executionCourseId") final ExecutionCourse executionCourse,
            @PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model,
            final RedirectAttributes redirectAttributes) {

        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);
        try {
            competenceCourseMarkSheet.delete();

            addInfoMessage(ULisboaSpecificationsUtil.bundle("label.success.delete"), model);
            return redirect(SEARCH_URL + executionCourse.getExternalId(), model, redirectAttributes);

        } catch (Exception ex) {
            addErrorMessage(ex.getLocalizedMessage(), model);
        }

        return jspPage("read/" + executionCourse.getExternalId() + "/" + getCompetenceCourseMarkSheet(model).getExternalId());
    }

    @RequestMapping(value = "/read/{executionCourseId}/{oid}/updateevaluations")
    public String processReadToUpdateEvaluations(@PathVariable("executionCourseId") ExecutionCourse executionCourse,
            @PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model,
            final RedirectAttributes redirectAttributes) {

        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        return redirect(UPDATEEVALUATIONS_URL + executionCourse.getExternalId() + "/"
                + getCompetenceCourseMarkSheet(model).getExternalId(), model, redirectAttributes);
    }

    private static final String _UPDATE_URI = "/update/";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI + "{executionCourseId}/{oid}", method = RequestMethod.GET)
    public String update(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model) {
        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        final CompetenceCourseMarkSheetBean bean = new CompetenceCourseMarkSheetBean(competenceCourseMarkSheet);
        bean.setByTeacher(true);
        this.setCompetenceCourseMarkSheetBean(bean, model);

        return jspPage("update");
    }

    private static final String _UPDATEPOSTBACK_URI = "/updatepostback/";
    public static final String UPDATEPOSTBACK_URL = CONTROLLER_URL + _UPDATEPOSTBACK_URI;

    @RequestMapping(value = _UPDATEPOSTBACK_URI + "{executionCourseId}/{oid}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> updatepostback(
            @PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            @RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean, final Model model) {

        bean.update();
        this.setCompetenceCourseMarkSheetBean(bean, model);
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    @RequestMapping(value = _UPDATE_URI + "{executionCourseId}/{oid}", method = RequestMethod.POST)
    public String update(@PathVariable("executionCourseId") final ExecutionCourse executionCourse,
            @PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            @RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {

        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        try {

            competenceCourseMarkSheet.edit(bean.getEvaluationDate(), bean.getGradeScale(), bean.getCertifier());

            return redirect(
                    READ_URL + executionCourse.getExternalId() + "/" + getCompetenceCourseMarkSheet(model).getExternalId(), model,
                    redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(de.getLocalizedMessage(), model);
            setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);
            this.setCompetenceCourseMarkSheetBean(bean, model);

            return jspPage("update");
        }
    }

    private static final String _CREATE_URI = "/create/";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI + "{executionCourseId}", method = RequestMethod.GET)
    public String create(@PathVariable("executionCourseId") final ExecutionCourse executionCourse, final Model model) {
        final CompetenceCourseMarkSheetBean bean =
                new CompetenceCourseMarkSheetBean(executionCourse, Authenticate.getUser().getPerson());
        bean.setByTeacher(true);
        this.setCompetenceCourseMarkSheetBean(bean, model);

        return jspPage("create");
    }

    private static final String _CREATEPOSTBACK_URI = "/createpostback/";
    public static final String CREATEPOSTBACK_URL = CONTROLLER_URL + _CREATEPOSTBACK_URI;

    @RequestMapping(value = _CREATEPOSTBACK_URI + "{executionCourseId}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> createpostback(
            @RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean, final Model model) {

        bean.update();
        this.setCompetenceCourseMarkSheetBean(bean, model);
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    @RequestMapping(value = _CREATE_URI + "{executionCourseId}", method = RequestMethod.POST)
    public String create(@PathVariable("executionCourseId") final ExecutionCourse executionCourse,
            @RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {

        try {
            final CompetenceCourseMarkSheet markSheet = CompetenceCourseMarkSheet.create(bean.getExecutionSemester(),
                    bean.getCompetenceCourse(), bean.getExecutionCourse(), bean.getEvaluationSeason(), bean.getEvaluationDate(),
                    bean.getCertifier(), bean.getShifts(), true);

            model.addAttribute("competenceCourseMarkSheet", markSheet);
            return redirect(UPDATEEVALUATIONS_URL + executionCourse.getExternalId() + "/"
                    + getCompetenceCourseMarkSheet(model).getExternalId(), model, redirectAttributes);

        } catch (Exception de) {

            addErrorMessage(de.getLocalizedMessage(), model);
            this.setCompetenceCourseMarkSheetBean(bean, model);
            return jspPage("create");
        }
    }

    private static final String _UPDATEEVALUATIONS_URI = "/updateevaluations/";
    public static final String UPDATEEVALUATIONS_URL = CONTROLLER_URL + _UPDATEEVALUATIONS_URI;

    @RequestMapping(value = _UPDATEEVALUATIONS_URI + "{executionCourseId}/{oid}", method = RequestMethod.GET)
    public String updateevaluations(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            final Model model) {
        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        final CompetenceCourseMarkSheetBean bean = new CompetenceCourseMarkSheetBean(competenceCourseMarkSheet);
        this.setCompetenceCourseMarkSheetBean(bean, model);

        return jspPage("updateevaluations");
    }

    private static final String _UPDATEEVALUATIONSPOSTBACK_URI = "/updateevaluationspostback/";
    public static final String UPDATEEVALUATIONSPOSTBACK_URL = CONTROLLER_URL + _UPDATEEVALUATIONSPOSTBACK_URI;

    @RequestMapping(value = _UPDATEEVALUATIONSPOSTBACK_URI + "{executionCourseId}/{oid}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> updateevaluationspostback(
            @PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            @RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean, final Model model) {

        this.setCompetenceCourseMarkSheetBean(bean, model);
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    @RequestMapping(value = _UPDATEEVALUATIONS_URI + "{executionCourseId}/{oid}", method = RequestMethod.POST)
    public String updateevaluations(@PathVariable("executionCourseId") final ExecutionCourse executionCourse,
            @PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            @RequestParam(value = "bean", required = false) final CompetenceCourseMarkSheetBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {
        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        try {
            bean.updateEnrolmentEvaluations();

            return redirect(
                    READ_URL + executionCourse.getExternalId() + "/" + getCompetenceCourseMarkSheet(model).getExternalId(), model,
                    redirectAttributes);
        } catch (Exception de) {
            addErrorMessage(de.getLocalizedMessage(), model);
            this.setCompetenceCourseMarkSheetBean(bean, model);

            return jspPage("updateevaluations");
        }
    }

    private static final String _PRINT_URI = "/print/";
    public static final String PRINT_URL = CONTROLLER_URL + _PRINT_URI;

    @RequestMapping(value = _PRINT_URI + "{executionCourseId}/{oid}")
    public void print(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model,
            final HttpServletResponse response) throws IOException {

        final CompetenceCourse competenceCourse = competenceCourseMarkSheet.getCompetenceCourse();
        final String filename = competenceCourse.getCode() + "_"
                + competenceCourse.getName().replace(' ', '_').replace('/', '-').replace('\\', '-')
                + competenceCourseMarkSheet.getEvaluationDate().toString("yyyy-MM-dd") + ".pdf";

        writeFile(response, filename, MarkSheetDocumentPrintService.PDF,
                MarkSheetDocumentPrintService.print(competenceCourseMarkSheet));
    }

    private static final String _CONFIRM_URI = "/confirm/";
    public static final String CONFIRM_URL = CONTROLLER_URL + _CONFIRM_URI;

    @RequestMapping(value = _CONFIRM_URI + "{executionCourseId}/{oid}", method = RequestMethod.POST)
    public String confirm(@PathVariable("executionCourseId") final ExecutionCourse executionCourse,
            @PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model,
            final RedirectAttributes redirectAttributes) {

        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        try {
            competenceCourseMarkSheet.confirm(true);

        } catch (Exception de) {
            addErrorMessage(de.getLocalizedMessage(), model);

            return jspPage("read");

        }

        return redirect(READ_URL + executionCourse.getExternalId() + "/" + competenceCourseMarkSheet.getExternalId(), model,
                redirectAttributes);
    }

    private static final String _SUBMIT_URI = "/submit/";
    public static final String SUBMIT_URL = CONTROLLER_URL + _SUBMIT_URI;

    @RequestMapping(value = _SUBMIT_URI + "{executionCourseId}/{oid}", method = RequestMethod.POST)
    public String submit(@PathVariable("executionCourseId") final ExecutionCourse executionCourse,
            @PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model,
            final RedirectAttributes redirectAttributes) {

        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);

        try {
            competenceCourseMarkSheet.submit(true);

        } catch (Exception de) {
            addErrorMessage(de.getLocalizedMessage(), model);

            return jspPage("read");

        }

        return redirect(READ_URL + executionCourse.getExternalId() + "/" + competenceCourseMarkSheet.getExternalId(), model,
                redirectAttributes);
    }

    private static final String _EXPORT_EXCEL_URI = "/exportexcel/";
    public static final String EXPORT_EXCEL_URL = CONTROLLER_URL + _EXPORT_EXCEL_URI;

    @RequestMapping(value = _EXPORT_EXCEL_URI + "{executionCourseId}/{oid}", method = RequestMethod.GET)
    public void exportExcel(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet, final Model model,
            final HttpServletResponse response) throws IOException {

        final CompetenceCourse competenceCourse = competenceCourseMarkSheet.getCompetenceCourse();
        final String filename = competenceCourse.getCode() + "_"
                + competenceCourse.getName().replace(' ', '_').replace('/', '-').replace('\\', '-')
                + competenceCourseMarkSheet.getEvaluationDate().toString("yyyy-MM-dd")
                + MarkSheetImportExportService.XLSX_EXTENSION;

        writeFile(response, filename, MarkSheetImportExportService.XLSX_MIME_TYPE,
                MarkSheetImportExportService.exportToXLSX(new CompetenceCourseMarkSheetBean(competenceCourseMarkSheet)));
    }

    private static final String _IMPORT_EXCEL_URI = "/importexcel/";
    public static final String IMPORT_EXCEL_URL = CONTROLLER_URL + _IMPORT_EXCEL_URI;

    @RequestMapping(value = _IMPORT_EXCEL_URI + "{executionCourseId}/{oid}", method = RequestMethod.POST)
    public String importExcel(@PathVariable("oid") final CompetenceCourseMarkSheet competenceCourseMarkSheet,
            @RequestParam(value = "file", required = true) MultipartFile markSheetFile, final Model model,
            final RedirectAttributes redirectAttributes) throws IOException {

        CompetenceCourseMarkSheetBean bean = null;
        try {
            bean = MarkSheetImportExportService.importFromXLSX(competenceCourseMarkSheet,
                    FilenameUtils.getName(markSheetFile.getOriginalFilename()), markSheetFile.getBytes());
            bean.updateEnrolmentEvaluations();

            addInfoMessage(ULisboaSpecificationsUtil.bundle("label.event.evaluation.manageMarkSheet.importExcel.success"), model);

        } catch (Exception e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        setCompetenceCourseMarkSheet(competenceCourseMarkSheet, model);
        setCompetenceCourseMarkSheetBean(bean == null ? new CompetenceCourseMarkSheetBean(competenceCourseMarkSheet) : bean,
                model);

        return jspPage("updateevaluations");

    }

}
