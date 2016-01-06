/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: nadir@qub-it.com
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
package org.fenixedu.ulisboa.specifications.ui.evaluation.config.managemarksheetsettings;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.evaluation.config.CompetenceCourseMarkSheetTemplateFile;
import org.fenixedu.ulisboa.specifications.domain.evaluation.config.MarkSheetSettings;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class,
        title = "label.title.evaluation.config.manageMarkSheetSettings", accessGroup = "logged")
@RequestMapping(MarkSheetSettingsController.CONTROLLER_URL)
public class MarkSheetSettingsController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL =
            "/fenixedu-ulisboa-specifications/evaluation/config/managemarksheetsettings/marksheetsettings";

    private static final String JSP_PATH = CONTROLLER_URL.substring(1);

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

    @RequestMapping
    public String home(Model model) {
        return "forward:" + READ_URL + "/" + MarkSheetSettings.getInstance().getExternalId();
    }

//    private MarkSheetSettingsBean getMarkSheetSettingsBean(Model model) {
//        return (MarkSheetSettingsBean) model.asMap().get("markSheetSettingsBean");
//    }
//
//    private void setMarkSheetSettingsBean(MarkSheetSettingsBean bean, Model model) {
//        model.addAttribute("markSheetSettingsBeanJson", getBeanJson(bean));
//        model.addAttribute("markSheetSettingsBean", bean);
//    }

    private MarkSheetSettings getMarkSheetSettings(Model model) {
        return (MarkSheetSettings) model.asMap().get("markSheetSettings");
    }

    private void setMarkSheetSettings(MarkSheetSettings markSheetSettings, Model model) {
        model.addAttribute("markSheetSettings", markSheetSettings);
    }

    private static final String _READ_URI = "/read/";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI + "{oid}")
    public String read(@PathVariable("oid") MarkSheetSettings markSheetSettings, Model model) {
        setMarkSheetSettings(markSheetSettings, model);
        return jspPage("read");
    }

    @RequestMapping(value = "/read/{oid}/updatetemplatefile")
    public String processReadToUpdateTemplateFile(@PathVariable("oid") MarkSheetSettings markSheetSettings, Model model,
            RedirectAttributes redirectAttributes) {
        setMarkSheetSettings(markSheetSettings, model);

        return redirect(UPDATETEMPLATEFILE_URL + getMarkSheetSettings(model).getExternalId(), model, redirectAttributes);
    }

    private static final String _UPDATETEMPLATEFILE_URI = "/updatetemplatefile/";
    public static final String UPDATETEMPLATEFILE_URL = CONTROLLER_URL + _UPDATETEMPLATEFILE_URI;

    @RequestMapping(value = _UPDATETEMPLATEFILE_URI + "{oid}", method = RequestMethod.GET)
    public String updateTemplateFile(@PathVariable("oid") MarkSheetSettings markSheetSettings, Model model) {
        setMarkSheetSettings(markSheetSettings, model);
        return jspPage("updatetemplatefile");

    }

    @RequestMapping(value = _UPDATETEMPLATEFILE_URI + "{oid}", method = RequestMethod.POST)
    public String updateTemplateFile(@PathVariable("oid") MarkSheetSettings markSheetSettings,
            @RequestParam(value = "templateFile", required = true) MultipartFile templateFile, Model model,
            RedirectAttributes redirectAttributes) {

        setMarkSheetSettings(markSheetSettings, model);

        try {
            markSheetSettings.editTemplateFile(FilenameUtils.getName(templateFile.getOriginalFilename()),
                    templateFile.getBytes());

            return redirect(READ_URL + "/" + markSheetSettings.getExternalId(), model, redirectAttributes);

        } catch (Exception de) {

            addErrorMessage(de.getLocalizedMessage(), model);

            return updateTemplateFile(markSheetSettings, model);

        }
    }

    private static final String _DOWNLOADTEMPLATEFILE_URI = "/downloadtemplatefile/";
    public static final String DOWNLOADTEMPLATEFILE_URL = CONTROLLER_URL + _DOWNLOADTEMPLATEFILE_URI;

    @RequestMapping(value = _DOWNLOADTEMPLATEFILE_URI + "{oid}", method = RequestMethod.GET)
    public void downloadTemplateFile(@PathVariable("oid") MarkSheetSettings settings, Model model,
            RedirectAttributes redirectAttributes, HttpServletResponse response) throws IOException {

        final CompetenceCourseMarkSheetTemplateFile template = settings.getTemplateFile();
        writeFile(response, template.getFilename(), template.getContentType(), template.getContent());

    }

}
