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
package org.fenixedu.ulisboa.specifications.ui.evaluation.manageevaluationseasonperiod;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonPeriod;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonPeriod.EvaluationSeasonPeriodType;
import org.fenixedu.ulisboa.specifications.dto.evaluation.season.EvaluationSeasonPeriodBean;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class,
        title = "label.title.evaluation.manageEvaluationSeasonPeriod", accessGroup = "logged")
@RequestMapping(EvaluationSeasonPeriodController.CONTROLLER_URL)
public class EvaluationSeasonPeriodController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL =
            "/fenixedu-ulisboa-specifications/evaluation/manageevaluationseasonperiod/evaluationseasonperiod";

    private static final String JSP_PATH = CONTROLLER_URL.substring(1);

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

    @RequestMapping
    public String home(final Model model, final RedirectAttributes redirectAttributes) {
        return redirect(CONTROLLER_URL + "/search", model, redirectAttributes);
    }

    private EvaluationSeasonPeriodBean getBean(final Model model) {
        return (EvaluationSeasonPeriodBean) model.asMap().get("bean");
    }

    private void setBean(final EvaluationSeasonPeriodBean bean, final Model model) {
        model.addAttribute("beanJson", getBeanJson(bean));
        model.addAttribute("bean", bean);
    }

    private EvaluationSeasonPeriod getPeriod(final Model model) {
        return (EvaluationSeasonPeriod) model.asMap().get("period");
    }

    private void setPeriod(final EvaluationSeasonPeriod period, final Model model) {
        model.addAttribute("period", period);
    }

    private static final String _SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI)
    public String search(final Model model, final RedirectAttributes redirectAttributes) {

        EvaluationSeasonPeriodBean bean = new EvaluationSeasonPeriodBean();
        this.setBean(bean, model);

        return jspPage("search");
    }

    @RequestMapping(value = _SEARCH_URI, method = RequestMethod.POST)
    public String search(@RequestParam(value = "bean", required = false) final EvaluationSeasonPeriodBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {

        final List<EvaluationSeasonPeriod> searchResultsDataSet =
                filterSearch(bean.getExecutionYear(), bean.getPeriodType(), bean.getSeason());

        model.addAttribute("searchResultsDataSet", searchResultsDataSet);
        this.setBean(bean, model);

        return jspPage("search");
    }

    private Stream<EvaluationSeasonPeriod> getSearchUniverseSearchDataSet(final ExecutionYear executionYear,
            final EvaluationSeasonPeriodType periodType) {
        return EvaluationSeasonPeriod.findBy(executionYear, periodType).stream();
    }

    private List<EvaluationSeasonPeriod> filterSearch(final ExecutionYear executionYear,
            final EvaluationSeasonPeriodType periodType, final EvaluationSeason season) {

        return getSearchUniverseSearchDataSet(executionYear, periodType).filter(i -> season == null || season == i.getSeason())
                .sorted().collect(Collectors.toList());
    }

    private static final String _SEARCH_TO_VIEW_ACTION_URI = "/searchEvaluationSeasonPeriod/view/";
    public static final String SEARCH_TO_VIEW_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_VIEW_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_VIEW_ACTION_URI + "{oid}")
    public String processSearchToViewAction(@PathVariable("oid") final EvaluationSeasonPeriod period, final Model model,
            final RedirectAttributes redirectAttributes) {

        return redirect(READ_URL + period.getExternalId(), model, redirectAttributes);
    }

    private static final String _READ_URI = "/read/";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI + "{oid}")
    public String read(@PathVariable("oid") final EvaluationSeasonPeriod period, final Model model) {
        setPeriod(period, model);
        return jspPage("read");
    }

    private static final String _DELETE_URI = "/delete/";
    public static final String DELETE_URL = CONTROLLER_URL + _DELETE_URI;

    @RequestMapping(value = _DELETE_URI + "{oid}", method = RequestMethod.POST)
    public String delete(@PathVariable("oid") final EvaluationSeasonPeriod period, final Model model,
            final RedirectAttributes redirectAttributes) {

        setPeriod(period, model);
        try {
            period.delete();

            addInfoMessage(ULisboaSpecificationsUtil.bundle("label.success.delete"), model);

        } catch (Exception ex) {
            addErrorMessage(ULisboaSpecificationsUtil.bundle("label.error.delete") + "\"" + ex.getLocalizedMessage() + "\"",
                    model);
        }

        return redirect(SEARCH_URL, model, redirectAttributes);
    }

    private static final String _UPDATEINTERVALS_URI = "/updateintervals/";
    public static final String UPDATEINTERVALS_URL = CONTROLLER_URL + _UPDATEINTERVALS_URI;

    @RequestMapping(value = _UPDATEINTERVALS_URI + "{oid}", method = RequestMethod.GET)
    public String updateIntervals(@PathVariable("oid") final EvaluationSeasonPeriod period, final Model model) {
        setPeriod(period, model);

        final EvaluationSeasonPeriodBean bean = new EvaluationSeasonPeriodBean(period);
        this.setBean(bean, model);

        return jspPage("updateintervals");
    }

    @RequestMapping(value = _UPDATEINTERVALS_URI + "{oid}/add", method = RequestMethod.POST)
    public String addInterval(@PathVariable("oid") final EvaluationSeasonPeriod period,
            @RequestParam(value = "bean", required = false) final EvaluationSeasonPeriodBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {
        setPeriod(period, model);

        try {
            period.addInterval(bean.getStart(), bean.getEnd());

            return redirect(UPDATEINTERVALS_URL + getPeriod(model).getExternalId(), model, redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(ULisboaSpecificationsUtil.bundle("label.error.update") + "\"" + de.getLocalizedMessage() + "\"",
                    model);
            setPeriod(period, model);
            this.setBean(bean, model);

            return jspPage("updateintervals");
        }
    }

    @RequestMapping(value = _UPDATEINTERVALS_URI + "{oid}/remove/{start}/{end}", method = RequestMethod.POST)
    public String removeInterval(@PathVariable("oid") final EvaluationSeasonPeriod period, @PathVariable("start") String start,
            @PathVariable("end") String end, final Model model, final RedirectAttributes redirectAttributes) {
        setPeriod(period, model);

        try {
            final DateTimeFormatter pattern = DateTimeFormat.forPattern("yyyy-MM-dd");
            period.removeInterval(LocalDate.parse(start, pattern), LocalDate.parse(end, pattern));

            return redirect(UPDATEINTERVALS_URL + getPeriod(model).getExternalId(), model, redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(ULisboaSpecificationsUtil.bundle("label.error.update") + "\"" + de.getLocalizedMessage() + "\"",
                    model);
            setPeriod(period, model);
            this.setBean(new EvaluationSeasonPeriodBean(period), model);

            return jspPage("updateintervals");
        }
    }

    private static final String _UPDATEDEGREES_URI = "/updatedegrees/";
    public static final String UPDATEDEGREES_URL = CONTROLLER_URL + _UPDATEDEGREES_URI;

    @RequestMapping(value = _UPDATEDEGREES_URI + "{oid}", method = RequestMethod.GET)
    public String updateDegrees(@PathVariable("oid") final EvaluationSeasonPeriod period, final Model model) {
        setPeriod(period, model);

        final EvaluationSeasonPeriodBean bean = new EvaluationSeasonPeriodBean(period);
        this.setBean(bean, model);

        return jspPage("updatedegrees");
    }

    @RequestMapping(value = _UPDATEDEGREES_URI + "{oid}/add", method = RequestMethod.POST)
    public String addDegree(@PathVariable("oid") final EvaluationSeasonPeriod period,
            @RequestParam(value = "bean", required = false) final EvaluationSeasonPeriodBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {

        setPeriod(period, model);

        try {
            period.addDegree(bean.getExecutionDegree());

            return redirect(UPDATEDEGREES_URL + getPeriod(model).getExternalId(), model, redirectAttributes);
        } catch (Exception de) {
            addErrorMessage(ULisboaSpecificationsUtil.bundle("label.error.update") + "\"" + de.getLocalizedMessage() + "\"",
                    model);
            this.setBean(bean, model);

            return jspPage("updatedegrees");
        }
    }

    @RequestMapping(value = _UPDATEDEGREES_URI + "{oid}/remove/{executionDegreeId}", method = RequestMethod.POST)
    public String removeDegree(@PathVariable("oid") final EvaluationSeasonPeriod period,
            @PathVariable("executionDegreeId") final ExecutionDegree executionDegree, final Model model,
            final RedirectAttributes redirectAttributes) {

        setPeriod(period, model);

        try {
            period.removeDegree(executionDegree);

            return redirect(UPDATEDEGREES_URL + getPeriod(model).getExternalId(), model, redirectAttributes);
        } catch (Exception de) {
            addErrorMessage(ULisboaSpecificationsUtil.bundle("label.error.update") + "\"" + de.getLocalizedMessage() + "\"",
                    model);
            this.setBean(new EvaluationSeasonPeriodBean(period), model);

            return jspPage("updatedegrees");
        }
    }

    private static final String _CREATE_URI = "/create/";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.GET)
    public String create(final Model model) {
        final EvaluationSeasonPeriodBean bean = new EvaluationSeasonPeriodBean();
        this.setBean(bean, model);

        return jspPage("create");
    }

    private static final String _CREATEPOSTBACK_URI = "/createpostback";
    public static final String CREATEPOSTBACK_URL = CONTROLLER_URL + _CREATEPOSTBACK_URI;

    @RequestMapping(value = _CREATEPOSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> createpostback(
            @RequestParam(value = "bean", required = false) final EvaluationSeasonPeriodBean bean, final Model model) {

        this.setBean(bean, model);
        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
    public String create(@RequestParam(value = "bean", required = false) final EvaluationSeasonPeriodBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {

        try {

            final EvaluationSeasonPeriod period = EvaluationSeasonPeriod.create(bean.getExecutionSemester(), bean.getPeriodType(),
                    bean.getSeason(), bean.getDegreeTypes(), bean.getStart(), bean.getEnd());

            model.addAttribute("period", period);
            return redirect(SEARCH_URL, model, redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(ULisboaSpecificationsUtil.bundle("label.error.create") + "\"" + de.getLocalizedMessage() + "\"",
                    model);
            this.setBean(bean, model);
            return jspPage("create");

        }
    }

}
