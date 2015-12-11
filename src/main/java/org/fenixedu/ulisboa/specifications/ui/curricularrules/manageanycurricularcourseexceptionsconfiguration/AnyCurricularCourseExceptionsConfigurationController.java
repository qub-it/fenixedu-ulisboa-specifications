/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: xpto@qub-it.com
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
package org.fenixedu.ulisboa.specifications.ui.curricularrules.manageanycurricularcourseexceptionsconfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.spreadsheet.SheetData;
import org.fenixedu.commons.spreadsheet.SpreadsheetBuilder;
import org.fenixedu.commons.spreadsheet.WorkbookExportFormat;
import org.fenixedu.ulisboa.specifications.domain.curricularRules.AnyCurricularCourseExceptionsConfiguration;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.dto.AnyCurricularCourseExceptionsConfigurationBean;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.DateTime;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.opensagres.xdocreport.core.io.internal.ByteArrayOutputStream;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class,
        title = "label.title.curricularRules.manageAnyCurricularCourseExceptionsConfiguration", accessGroup = "logged")
@RequestMapping(AnyCurricularCourseExceptionsConfigurationController.CONTROLLER_URL)
public class AnyCurricularCourseExceptionsConfigurationController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL =
            "/fenixedu-ulisboa-specifications/curricularrules/manageanycurricularcourseexceptionsconfiguration/anycurricularcourseexceptionsconfiguration";

    private static final String JSP_PATH = CONTROLLER_URL.substring(1);

    @RequestMapping
    public String home(Model model, RedirectAttributes redirectAttributes) {
        return redirect(READ_URL + AnyCurricularCourseExceptionsConfiguration.getInstance().getExternalId(), model,
                redirectAttributes);
    }

    private AnyCurricularCourseExceptionsConfigurationBean getAnyCurricularCourseExceptionsConfigurationBean(Model model) {
        return (AnyCurricularCourseExceptionsConfigurationBean) model.asMap().get(
                "anyCurricularCourseExceptionsConfigurationBean");
    }

    private void setAnyCurricularCourseExceptionsConfigurationBean(AnyCurricularCourseExceptionsConfigurationBean bean,
            Model model) {
        model.addAttribute("anyCurricularCourseExceptionsConfigurationBeanJson", getBeanJson(bean));
        model.addAttribute("anyCurricularCourseExceptionsConfigurationBean", bean);
    }

    private AnyCurricularCourseExceptionsConfiguration getAnyCurricularCourseExceptionsConfiguration(Model model) {
        return (AnyCurricularCourseExceptionsConfiguration) model.asMap().get("anyCurricularCourseExceptionsConfiguration");
    }

    private void setAnyCurricularCourseExceptionsConfiguration(
            AnyCurricularCourseExceptionsConfiguration anyCurricularCourseExceptionsConfiguration, Model model) {
        model.addAttribute("anyCurricularCourseExceptionsConfiguration", anyCurricularCourseExceptionsConfiguration);
    }

    private static final String _READ_URI = "/read/";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI + "{oid}")
    public String read(
            @PathVariable("oid") AnyCurricularCourseExceptionsConfiguration anyCurricularCourseExceptionsConfiguration,
            Model model) {
        setAnyCurricularCourseExceptionsConfiguration(anyCurricularCourseExceptionsConfiguration, model);
        setAnyCurricularCourseExceptionsConfigurationBean(new AnyCurricularCourseExceptionsConfigurationBean(), model);

        return jspPage("read");
    }

    @RequestMapping(value = _READ_URI + "{oid}/addcompetencecourse", method = RequestMethod.POST)
    public String processReadToAddCompetenceCourse(
            @PathVariable("oid") AnyCurricularCourseExceptionsConfiguration anyCurricularCourseExceptionsConfiguration,
            @RequestParam("bean") AnyCurricularCourseExceptionsConfigurationBean bean, Model model,
            RedirectAttributes redirectAttributes) {

        try {
            anyCurricularCourseExceptionsConfiguration.addCompetenceCourse(bean.getCompetenceCourse());

            addInfoMessage(
                    ULisboaSpecificationsUtil
                            .bundle("label.event.curricularRules.manageAnyCurricularCourseExceptionsConfiguration.addCompetenceCourse.success"),
                    model);

            return redirect(READ_URL + anyCurricularCourseExceptionsConfiguration.getExternalId(), model, redirectAttributes);

        } catch (ULisboaSpecificationsDomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);

            return read(anyCurricularCourseExceptionsConfiguration, model);
        }

    }

    @RequestMapping(value = _READ_URI + "{oid}/removecompetencecourse/{competenceCourseId}", method = RequestMethod.POST)
    public String processReadToRemoveCompetenceCourse(
            @PathVariable("oid") AnyCurricularCourseExceptionsConfiguration anyCurricularCourseExceptionsConfiguration,
            @PathVariable("competenceCourseId") CompetenceCourse competenceCourse, Model model,
            RedirectAttributes redirectAttributes) {

        try {
            anyCurricularCourseExceptionsConfiguration.removeCompetenceCourse(competenceCourse);

            addInfoMessage(
                    ULisboaSpecificationsUtil
                            .bundle("label.event.curricularRules.manageAnyCurricularCourseExceptionsConfiguration.removeCompetenceCourse.success"),
                    model);

            return redirect(READ_URL + anyCurricularCourseExceptionsConfiguration.getExternalId(), model, redirectAttributes);

        } catch (ULisboaSpecificationsDomainException e) {

            addErrorMessage(e.getLocalizedMessage(), model);
            return read(anyCurricularCourseExceptionsConfiguration, model);
        }

    }

    @RequestMapping(value = _READ_URI + "{oid}/importcompetencecourses")
    public String processReadToImportCompetenceCourses(
            @PathVariable("oid") AnyCurricularCourseExceptionsConfiguration anyCurricularCourseExceptionsConfiguration,
            Model model, RedirectAttributes redirectAttributes) {
        setAnyCurricularCourseExceptionsConfiguration(anyCurricularCourseExceptionsConfiguration, model);

        return redirect(IMPORTCOMPETENCECOURSES_URL + anyCurricularCourseExceptionsConfiguration.getExternalId(), model,
                redirectAttributes);

    }

    @RequestMapping(value = _READ_URI + "{oid}/exportcompetencecourses")
    public void processReadToExportCompetenceCourses(
            @PathVariable("oid") AnyCurricularCourseExceptionsConfiguration anyCurricularCourseExceptionsConfiguration,
            Model model, RedirectAttributes redirectAttributes, HttpServletResponse response) throws IOException {
        setAnyCurricularCourseExceptionsConfiguration(anyCurricularCourseExceptionsConfiguration, model);

        writeFile(response, CompetenceCourse.class.getSimpleName() + new DateTime().toString("yyyy-MM-dd_HH-mm-ss") + ".xls",
                "application/vnd.ms-excel",
                exportCompetenceCoursesToXLS(anyCurricularCourseExceptionsConfiguration.getCompetenceCoursesSet()));

    }

    @RequestMapping(value = _READ_URI + "{oid}/clearcompetencecourses", method = RequestMethod.POST)
    public String processReadToClearCompetenceCourses(
            @PathVariable("oid") AnyCurricularCourseExceptionsConfiguration anyCurricularCourseExceptionsConfiguration,
            Model model, RedirectAttributes redirectAttributes) {
        setAnyCurricularCourseExceptionsConfiguration(anyCurricularCourseExceptionsConfiguration, model);

        try {
            anyCurricularCourseExceptionsConfiguration.clearCompetenceCourses();

            addInfoMessage(
                    ULisboaSpecificationsUtil
                            .bundle("label.event.curricularRules.manageAnyCurricularCourseExceptionsConfiguration.clearCompetenceCourses.success"),
                    model);

            return redirect(READ_URL + anyCurricularCourseExceptionsConfiguration.getExternalId(), model, redirectAttributes);

        } catch (ULisboaSpecificationsDomainException e) {

            addErrorMessage(e.getLocalizedMessage(), model);
            return read(anyCurricularCourseExceptionsConfiguration, model);
        }
    }

    private static final String _IMPORTCOMPETENCECOURSES_URI = "/importcompetencecourses/";
    public static final String IMPORTCOMPETENCECOURSES_URL = CONTROLLER_URL + _IMPORTCOMPETENCECOURSES_URI;

    @RequestMapping(value = _IMPORTCOMPETENCECOURSES_URI + "{oid}", method = RequestMethod.GET)
    public String importcompetencecourses(
            @PathVariable("oid") AnyCurricularCourseExceptionsConfiguration anyCurricularCourseExceptionsConfiguration,
            Model model) {
        setAnyCurricularCourseExceptionsConfiguration(anyCurricularCourseExceptionsConfiguration, model);

        return jspPage("importcompetencecourses");
    }

    @RequestMapping(value = _IMPORTCOMPETENCECOURSES_URI + "{oid}", method = RequestMethod.POST)
    public String importcompetencecourses(
            @PathVariable("oid") AnyCurricularCourseExceptionsConfiguration anyCurricularCourseExceptionsConfiguration,
            @RequestParam(value = "competenceCoursesFile", required = true) MultipartFile competenceCoursesFile, Model model,
            RedirectAttributes redirectAttributes) {

        setAnyCurricularCourseExceptionsConfiguration(anyCurricularCourseExceptionsConfiguration, model);

        try {

            anyCurricularCourseExceptionsConfiguration
                    .addAllCompetenceCourses(parseCompetenceCoursesFromXLS(competenceCoursesFile));

            return redirect(READ_URL + anyCurricularCourseExceptionsConfiguration.getExternalId(), model, redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(de.getLocalizedMessage(), model);
            return importcompetencecourses(anyCurricularCourseExceptionsConfiguration, model);

        }
    }

    private Collection<CompetenceCourse> parseCompetenceCoursesFromXLS(MultipartFile competenceCoursesFile) throws IOException {

        if (!competenceCoursesFile.getOriginalFilename().endsWith(".xls")) {
            throw new ULisboaSpecificationsDomainException(
                    "error.curricularRules.manageAnyCurricularCourseExceptionsConfiguration.importCompetenceCourses.invalid.file.format");
        }

        final Set<CompetenceCourse> result = new HashSet<CompetenceCourse>();

        InputStream inputStream = null;
        HSSFWorkbook workbook = null;
        try {

            inputStream = competenceCoursesFile.getInputStream();
            workbook = new HSSFWorkbook(competenceCoursesFile.getInputStream());

            final HSSFSheet sheet = workbook.getSheetAt(0);
            final Iterator<Row> rowIterator = sheet.iterator();

            //header
            rowIterator.next();

            while (rowIterator.hasNext()) {

                final Row row = rowIterator.next();
                final String code = row.getCell(0).getStringCellValue();
                final CompetenceCourse competenceCourse = CompetenceCourse.find(code);

                if (competenceCourse == null) {
                    throw new ULisboaSpecificationsDomainException(
                            "error.curricularRules.manageAnyCurricularCourseExceptionsConfiguration.importCompetenceCourses.competenceCourse.not.found",
                            code);
                }

                result.add(competenceCourse);

            }

            return result;

        } catch (IOException e) {
            throw new ULisboaSpecificationsDomainException("label.unexpected.error.occured");
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private byte[] exportCompetenceCoursesToXLS(Collection<CompetenceCourse> toExport) throws IOException {

        final SpreadsheetBuilder builder = new SpreadsheetBuilder();
        builder.addSheet("CompetencesCourses", new SheetData<CompetenceCourse>(toExport) {

            @Override
            protected void makeLine(CompetenceCourse competenceCourse) {
                addCell(ULisboaSpecificationsUtil.bundle("label.CompetenceCourse.code"), competenceCourse.getCode());
                addCell(ULisboaSpecificationsUtil.bundle("label.CompetenceCourse.name"), competenceCourse.getName());
                addCell(ULisboaSpecificationsUtil.bundle("label.CompetenceCourse.ectsCredits"),
                        String.valueOf(competenceCourse.getEctsCredits()));
            }

        });

        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        builder.build(WorkbookExportFormat.EXCEL, result);

        return result.toByteArray();

    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

}
