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
package org.fenixedu.ulisboa.specifications.ui.evaluation.manageevaluationseason;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices;
import org.fenixedu.ulisboa.specifications.dto.evaluation.season.EvaluationSeasonBean;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.ui.evaluation.manageevaluationseasonrule.EvaluationSeasonRuleController;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.evaluation.manageEvaluationSeason",
        accessGroup = "logged")
@RequestMapping(EvaluationSeasonController.CONTROLLER_URL)
public class EvaluationSeasonController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL =
            "/fenixedu-ulisboa-specifications/evaluation/manageevaluationseason/evaluationseason";

    private static final String JSP_PATH = CONTROLLER_URL.substring(1);

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

    @RequestMapping
    public String home(final Model model) {
        return "forward:" + CONTROLLER_URL + "/";
    }

    private EvaluationSeasonBean getEvaluationSeasonBean(final Model model) {
        return (EvaluationSeasonBean) model.asMap().get("evaluationSeasonBean");
    }

    private void setEvaluationSeasonBean(final EvaluationSeasonBean bean, final Model model) {
        model.addAttribute("evaluationSeasonBeanJson", getBeanJson(bean));
        model.addAttribute("evaluationSeasonBean", bean);
    }

    private EvaluationSeason getEvaluationSeason(final Model model) {
        return (EvaluationSeason) model.asMap().get("evaluationSeason");
    }

    private void setEvaluationSeason(final EvaluationSeason evaluationSeason, final Model model) {
        model.addAttribute("evaluationSeason", evaluationSeason);
    }

    private static final String _SEARCH_URI = "/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI)
    public String search(final Model model) {
        List<EvaluationSeason> searchevaluationseasonResultsDataSet = filterSearchEvaluationSeason();

        model.addAttribute("searchevaluationseasonResultsDataSet", searchevaluationseasonResultsDataSet);
        return jspPage("search");
    }

    private Stream<EvaluationSeason> getSearchUniverseSearchEvaluationSeasonDataSet() {
        return EvaluationSeasonServices.findAll().sorted(EvaluationSeasonServices.SEASON_ORDER_COMPARATOR);
    }

    private List<EvaluationSeason> filterSearchEvaluationSeason() {
        return getSearchUniverseSearchEvaluationSeasonDataSet().collect(Collectors.toList());
    }

    private static final String _SEARCH_TO_ORDER_UP_ACTION_URI = "/search/";
    public static final String SEARCH_TO_ORDER_UP_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_ORDER_UP_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_ORDER_UP_ACTION_URI + "{oid}" + "/orderup", method = RequestMethod.POST)
    public String processSearchToOrderUpAction(@PathVariable("oid") final EvaluationSeason evaluationSeason, final Model model,
            final RedirectAttributes redirectAttributes) {

        EvaluationSeasonServices.orderUp(evaluationSeason);
        return search(model);
    }

    private static final String _SEARCH_TO_ORDER_DOWN_ACTION_URI = "/search/";
    public static final String SEARCH_TO_ORDER_DOWN_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_ORDER_DOWN_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_ORDER_DOWN_ACTION_URI + "{oid}" + "/orderdown", method = RequestMethod.POST)
    public String processSearchToOrderDownAction(@PathVariable("oid") final EvaluationSeason evaluationSeason, final Model model,
            final RedirectAttributes redirectAttributes) {

        EvaluationSeasonServices.orderDown(evaluationSeason);
        return search(model);
    }

    private static final String _SEARCH_TO_VIEW_ACTION_URI = "/search/view/";
    public static final String SEARCH_TO_VIEW_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_VIEW_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_VIEW_ACTION_URI + "{oid}")
    public String processSearchToViewAction(@PathVariable("oid") final EvaluationSeason evaluationSeason, final Model model,
            final RedirectAttributes redirectAttributes) {

        return redirect(READ_URL + evaluationSeason.getExternalId(), model, redirectAttributes);
    }

    private static final String _READ_URI = "/read/";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI + "{oid}")
    public String read(@PathVariable("oid") final EvaluationSeason evaluationSeason, final Model model) {
        setEvaluationSeason(evaluationSeason, model);
        setEvaluationSeasonBean(new EvaluationSeasonBean(evaluationSeason), model);
        return jspPage("read");
    }

    private static final String _DELETE_URI = "/delete/";
    public static final String DELETE_URL = CONTROLLER_URL + _DELETE_URI;

    @RequestMapping(value = _DELETE_URI + "{oid}", method = RequestMethod.POST)
    public String delete(@PathVariable("oid") final EvaluationSeason evaluationSeason, final Model model,
            final RedirectAttributes redirectAttributes) {

        setEvaluationSeason(evaluationSeason, model);
        try {
            EvaluationSeasonServices.delete(evaluationSeason);

            addInfoMessage(ULisboaSpecificationsUtil.bundle("label.success.delete"), model);
            return redirect(CONTROLLER_URL, model, redirectAttributes);

        } catch (Exception ex) {
            addErrorMessage(ULisboaSpecificationsUtil.bundle("label.error.delete") + "\"" + ex.getLocalizedMessage() + "\"",
                    model);
        }

        return jspPage("read/") + getEvaluationSeason(model).getExternalId();
    }

    @RequestMapping(value = "/read/{oid}/readrules")
    public String processReadToReadRules(@PathVariable("oid") final EvaluationSeason evaluationSeason, final Model model,
            final RedirectAttributes redirectAttributes) {
        setEvaluationSeason(evaluationSeason, model);

        return redirect(EvaluationSeasonRuleController.SEARCH_URL + getEvaluationSeason(model).getExternalId(), model,
                redirectAttributes);
    }

    private static final String _UPDATE_URI = "/update/";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI + "{oid}", method = RequestMethod.GET)
    public String update(@PathVariable("oid") final EvaluationSeason evaluationSeason, final Model model) {
        setEvaluationSeason(evaluationSeason, model);

        final EvaluationSeasonBean bean = new EvaluationSeasonBean(evaluationSeason);
        this.setEvaluationSeasonBean(bean, model);

        return jspPage("update");
    }

    private static final String _UPDATEPOSTBACK_URI = "/updatepostback/";
    public static final String UPDATEPOSTBACK_URL = CONTROLLER_URL + _UPDATEPOSTBACK_URI;

    @RequestMapping(value = _UPDATEPOSTBACK_URI + "{oid}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> updatepostback(@PathVariable("oid") final EvaluationSeason evaluationSeason,
            @RequestParam(value = "bean", required = false) final EvaluationSeasonBean bean, final Model model) {

        this.setEvaluationSeasonBean(bean, model);
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    @RequestMapping(value = _UPDATE_URI + "{oid}", method = RequestMethod.POST)
    public String update(@PathVariable("oid") final EvaluationSeason evaluationSeason,
            @RequestParam(value = "bean", required = false) final EvaluationSeasonBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {
        setEvaluationSeason(evaluationSeason, model);

        try {
            EvaluationSeasonServices.edit(evaluationSeason, bean.getCode(), bean.getAcronym(), bean.getName(), bean.getNormal(),
                    bean.getImprovement(), bean.getSpecial(), bean.getSpecialAuthorization(), bean.getActive());

            return redirect(READ_URL + getEvaluationSeason(model).getExternalId(), model, redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(ULisboaSpecificationsUtil.bundle("label.error.update") + "\"" + de.getLocalizedMessage() + "\"",
                    model);
            setEvaluationSeason(evaluationSeason, model);
            this.setEvaluationSeasonBean(bean, model);

            return jspPage("update");
        }
    }

    private static final String _CREATE_URI = "/create/";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.GET)
    public String create(final Model model) {

        final EvaluationSeasonBean bean = new EvaluationSeasonBean();
        this.setEvaluationSeasonBean(bean, model);

        return jspPage("create");
    }

    private static final String _CREATEPOSTBACK_URI = "/createpostback/";
    public static final String CREATEPOSTBACK_URL = CONTROLLER_URL + _CREATEPOSTBACK_URI;

    @RequestMapping(value = _CREATEPOSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> createpostback(
            @RequestParam(value = "bean", required = false) final EvaluationSeasonBean bean, final Model model) {

        this.setEvaluationSeasonBean(bean, model);
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
    public String create(@RequestParam(value = "bean", required = false) EvaluationSeasonBean bean, Model model,
            final RedirectAttributes redirectAttributes) {

        try {

            final EvaluationSeason evaluationSeason =
                    EvaluationSeasonServices.create(bean.getCode(), bean.getAcronym(), bean.getName(), bean.getNormal(),
                            bean.getImprovement(), bean.getSpecial(), bean.getSpecialAuthorization(), bean.getActive());

            model.addAttribute("evaluationSeason", evaluationSeason);
            return redirect(READ_URL + getEvaluationSeason(model).getExternalId(), model, redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(ULisboaSpecificationsUtil.bundle("label.error.create") + "\"" + de.getLocalizedMessage() + "\"",
                    model);
            this.setEvaluationSeasonBean(bean, model);
            return jspPage("create");
        }
    }

}
