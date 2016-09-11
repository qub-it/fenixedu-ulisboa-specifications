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
package org.fenixedu.ulisboa.specifications.ui.firstyearconfiguration;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationConfiguration;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationGlobalConfiguration;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.firstYearConfiguration",
        accessGroup = "logged")
@RequestMapping(FirstYearRegistrationConfigurationController.CONTROLLER_URL)
public class FirstYearRegistrationConfigurationController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL =
            "/fenixedu-ulisboa-specifications/firstyearconfiguration/firstyearregistrationconfiguration";

    private void setFirstYearConfigurationBean(FirstYearConfigurationBean bean, Model model) {
        bean.updateLists();
        model.addAttribute("firstYearConfigurationBeanJson", getBeanJson(bean));
        model.addAttribute("firstYearConfigurationBean", bean);
    }

    @RequestMapping
    public String home(Model model) {
        return "forward:" + CONTROLLER_URL + "/";
    }

    @Atomic
    public void deleteFirstYearRegistrationConfiguration(FirstYearRegistrationConfiguration firstYearRegistrationConfiguration) {
    }

    private static final String _SEARCH_URI = "/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI)
    public String search(Model model) {
        List<FirstYearRegistrationConfiguration> searchfirstyearregistrationconfigurationResultsDataSet =
                getSearchUniverseDataSet();

        model.addAttribute("searchfirstyearregistrationconfigurationResultsDataSet",
                searchfirstyearregistrationconfigurationResultsDataSet);
        model.addAttribute("firstYearRegistrationGlobalConfiguration", FirstYearRegistrationGlobalConfiguration.getInstance());
        return "fenixedu-ulisboa-specifications/firstyearconfiguration/firstyearregistrationconfiguration/search";
    }

    private static final String SEARCH_TO_EDIT_URI = "/search/edit";
    public static final String SEARCH_TO_EDIT_URL = CONTROLLER_URL + SEARCH_TO_EDIT_URI;

    @RequestMapping(value = SEARCH_TO_EDIT_URL)
    public String processSearchToEdit(Model model, RedirectAttributes redirectAttributes) {
        return redirect("/fenixedu-ulisboa-specifications/firstyearconfiguration/firstyearregistrationconfiguration/edit", model,
                redirectAttributes);
    }

    private static final String _EDIT_URI = "/edit";
    public static final String EDIT_URL = CONTROLLER_URL + _EDIT_URI;

    @RequestMapping(value = _EDIT_URI)
    public String edit(Model model) {
        List<FirstYearRegistrationConfiguration> editResultsDataSet = getSearchUniverseDataSet();

        model.addAttribute("editResultsDataSet", editResultsDataSet);
        model.addAttribute("firstYearRegistrationGlobalConfiguration", FirstYearRegistrationGlobalConfiguration.getInstance());
        return "fenixedu-ulisboa-specifications/firstyearconfiguration/firstyearregistrationconfiguration/edit";
    }

    private static final String _COURSES_URI = "/edit/courses";
    public static final String COURSES_URL = CONTROLLER_URL + _COURSES_URI;

    @RequestMapping(value = _COURSES_URI, method = RequestMethod.GET)
    public String editCourses(Model model, RedirectAttributes redirectAttributes) {
        setFirstYearConfigurationBean(new FirstYearConfigurationBean(), model);

        return "fenixedu-ulisboa-specifications/firstyearconfiguration/firstyearregistrationconfiguration/editDegrees";
    }

    private static final String ADD_DEGREE_CONFIGURATION_URI = "/add/degree/configuration";
    public static final String ADD_DEGREE_CONFIGURATION_URL = CONTROLLER_URL + ADD_DEGREE_CONFIGURATION_URI;

    @RequestMapping(value = ADD_DEGREE_CONFIGURATION_URI + "/{oid}/{executionYearOid}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody String addDegreeConfiguration(@PathVariable("oid") Degree degree,
            @PathVariable("executionYearOid") ExecutionYear executionYear,
            @RequestParam(value = "bean", required = true) FirstYearConfigurationBean bean,
            @RequestParam(value = "requiresVaccination", required = true) boolean requiresVaccination,
            @RequestParam(value = "automaticEnrolment", required = true) boolean automaticEnrolment,
            @RequestParam(value = "degreeCurricularPlan", required = true) DegreeCurricularPlan degreeCurricularPlan,
            Model model) {
        createDegreeConfiguration(degree, executionYear, degreeCurricularPlan, requiresVaccination);
        setFirstYearConfigurationBean(bean, model);
        return getBeanJson(bean);
    }

    @Atomic
    private void createDegreeConfiguration(Degree degree, ExecutionYear executionYear, DegreeCurricularPlan degreeCurricularPlan,
            boolean requiresVaccination) {
        if (FirstYearRegistrationConfiguration.getDegreeConfiguration(degree, executionYear) != null) {
            FirstYearRegistrationConfiguration.getDegreeConfiguration(degree, executionYear).edit(executionYear,
                    degreeCurricularPlan, requiresVaccination);
        } else {
            new FirstYearRegistrationConfiguration(degree, executionYear, degreeCurricularPlan, requiresVaccination);
        }
    }

    private static final String DELETE_DEGREE_CONFIGURATION_URI = "/delete/degree/configuration";
    public static final String DELETE_DEGREE_CONFIGURATION_URL = CONTROLLER_URL + DELETE_DEGREE_CONFIGURATION_URI;

    @RequestMapping(value = DELETE_DEGREE_CONFIGURATION_URI + "/{oid}/{executionYearOid}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody String deleteDegreeConfiguration(@PathVariable("oid") Degree degree,
            @PathVariable("executionYearOid") ExecutionYear executionYear,
            @RequestParam(value = "bean", required = true) FirstYearConfigurationBean bean, Model model) {
        deleteDegreeConfiguration(degree, executionYear);
        setFirstYearConfigurationBean(bean, model);
        return getBeanJson(bean);
    }

    @Atomic
    private void deleteDegreeConfiguration(Degree degree, ExecutionYear executionYear) {
        if (FirstYearRegistrationConfiguration.getDegreeConfiguration(degree, executionYear) != null) {
            FirstYearRegistrationConfiguration.getDegreeConfiguration(degree, executionYear).delete();
        }
    }

    private static final String EDIT_DEGREE_CONFIGURATION_URI = "/edit/degree/configuration";
    public static final String EDIT_DEGREE_CONFIGURATION_URL = CONTROLLER_URL + EDIT_DEGREE_CONFIGURATION_URI;

    @RequestMapping(value = EDIT_DEGREE_CONFIGURATION_URI + "/{executionYearOid}", method = RequestMethod.POST)
    public String editDegreeConfiguration(@PathVariable("executionYearOid") ExecutionYear executionYear,
            @RequestParam(value = "bean", required = true) FirstYearConfigurationBean bean, Model model,
            RedirectAttributes redirectAttributes) {
        editDegreeConfiguration(bean, executionYear);
        setFirstYearConfigurationBean(bean, model);
        return redirect(SEARCH_URL, model, redirectAttributes);
    }

    @Atomic
    private void editDegreeConfiguration(FirstYearConfigurationBean bean, ExecutionYear executionYear) {
        for (FirstYearDegreeConfigurationBean configurationBean : bean.getActiveDegrees()) {
            FirstYearRegistrationConfiguration configuration =
                    FirstYearRegistrationConfiguration.getDegreeConfiguration(configurationBean.getDegree(), executionYear);
            if (configuration != null) {
                configuration.edit(executionYear, configurationBean.getDegreeCurricularPlan(),
                        configurationBean.isRequiresVaccination());
            } else {
                new FirstYearRegistrationConfiguration(configurationBean.getDegree(), executionYear,
                        configurationBean.getDegreeCurricularPlan(), configurationBean.isRequiresVaccination());
            }
        }
    }

    @Atomic
    @RequestMapping(value = _EDIT_URI + "/uploadTemplate", headers = "content-type=multipart/*", method = RequestMethod.POST)
    public String uploadTemplate(@RequestParam(value = "mod43Template", required = true) MultipartFile mod43Template, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            String fileName = mod43Template.getOriginalFilename();
            byte[] fileContent = mod43Template.getBytes();
            FirstYearRegistrationGlobalConfiguration.getInstance().uploadMod43Template(fileName, fileContent);

            model.addAttribute("firstYearRegistrationGlobalConfiguration",
                    FirstYearRegistrationGlobalConfiguration.getInstance());
            return "fenixedu-ulisboa-specifications/firstyearconfiguration/firstyearregistrationconfiguration/search";
        } catch (Exception e) {
            addErrorMessage(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "label.error.uploadMod43")
                            + e.getLocalizedMessage(),
                    model);

            model.addAttribute("firstYearRegistrationGlobalConfiguration",
                    FirstYearRegistrationGlobalConfiguration.getInstance());
            return "fenixedu-ulisboa-specifications/firstyearconfiguration/firstyearregistrationconfiguration/search";
        }
    }

    @Atomic
    @RequestMapping(value = _SEARCH_URI + "/cleanTemplate", method = RequestMethod.GET)
    public String cleanTemplate(Model model, RedirectAttributes redirectAttributes) {

        FirstYearRegistrationGlobalConfiguration.getInstance().cleanTemplate();

        model.addAttribute("firstYearRegistrationGlobalConfiguration", FirstYearRegistrationGlobalConfiguration.getInstance());
        return "fenixedu-ulisboa-specifications/firstyearconfiguration/firstyearregistrationconfiguration/search";
    }

    private List<FirstYearRegistrationConfiguration> getSearchUniverseDataSet() {
        return FirstYearRegistrationGlobalConfiguration.getInstance().getFirstYearRegistrationConfigurationsSet().stream()
                .filter(c -> c.getDegree().isActive()).collect(Collectors.toList());
    }

    @Atomic
    @RequestMapping(value = _EDIT_URI + "/introductionText", method = RequestMethod.POST)
    public String writeIntroductionText(@RequestParam("introductionText") LocalizedString introductionText, Model model) {
        FirstYearRegistrationGlobalConfiguration.getInstance().setIntroductionText(introductionText);
        return search(model);
    }

}
