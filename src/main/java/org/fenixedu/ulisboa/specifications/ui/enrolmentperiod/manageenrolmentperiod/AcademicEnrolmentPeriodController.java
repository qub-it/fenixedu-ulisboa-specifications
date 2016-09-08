package org.fenixedu.ulisboa.specifications.ui.enrolmentperiod.manageenrolmentperiod;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.student.StatuteType;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod.AcademicEnrolmentPeriod;
import org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod.AcademicEnrolmentPeriodType;
import org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod.AutomaticEnrolment;
import org.fenixedu.ulisboa.specifications.dto.enrolmentperiod.AcademicEnrolmentPeriodBean;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.manageAcademicEnrolmentPeriod",
        accessGroup = "logged")
@RequestMapping(AcademicEnrolmentPeriodController.CONTROLLER_URL)
public class AcademicEnrolmentPeriodController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/enrolmentperiod/manageacademicenrolmentperiods";
    public static final String JSP_PATH = "fenixedu-ulisboa-specifications/enrolmentperiod/manageacademicenrolmentperiods";

    @RequestMapping
    public String home(Model model, RedirectAttributes redirectAttributes) {
        return redirect(SEARCH_URL, model, redirectAttributes);
    }

    private String jspPage(final String page) {
        return JSP_PATH + page;
    }

    private AcademicEnrolmentPeriodBean getAcademicEnrolmentPeriodBean(Model model) {
        return (AcademicEnrolmentPeriodBean) model.asMap().get("academicEnrolmentPeriodBean");
    }

    private void setAcademicEnrolmentPeriodBean(AcademicEnrolmentPeriodBean bean, Model model) {
        bean.updateLists();
        model.addAttribute("academicEnrolmentPeriodBeanJson", getBeanJson(bean));
        model.addAttribute("academicEnrolmentPeriodBean", bean);
    }

    private static final String _SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI, method = GET)
    public String search(@RequestParam(value = "executionSemester", required = false) ExecutionSemester executionSemester,
            @RequestParam(value = "enrolmentPeriodType", required = false) AcademicEnrolmentPeriodType enrolmentPeriodType,
            @RequestParam(value = "automaticEnrolment", required = false) AutomaticEnrolment automaticEnrolment, Model model) {
        //TODOJN - this list is not going correctly sorted to the screen
        List<ExecutionSemester> semesters = ExecutionSemester.readNotClosedExecutionPeriods().stream()
                .sorted(ExecutionSemester.COMPARATOR_BY_BEGIN_DATE.reversed()).collect(Collectors.toList());
        model.addAttribute("executionSemesters", semesters);
        model.addAttribute("enrolmentPeriodTypes", Arrays.asList(AcademicEnrolmentPeriodType.values()));
        model.addAttribute("automaticEnrolments", Arrays.asList(AutomaticEnrolment.values()));

        model.addAttribute("academicEnrolmentPeriodsResult",
                filterSearchAcademicEnrolmentPeriod(executionSemester, enrolmentPeriodType, automaticEnrolment));
        return jspPage(_SEARCH_URI);
    }

    private List<AcademicEnrolmentPeriod> filterSearchAcademicEnrolmentPeriod(final ExecutionSemester executionSemester,
            final AcademicEnrolmentPeriodType enrolmentPeriodType, final AutomaticEnrolment automaticEnrolment) {
        return AcademicEnrolmentPeriod.readAll()
                .filter(p -> executionSemester == null || p.getExecutionSemester() == executionSemester)
                .filter(p -> enrolmentPeriodType == null || p.getEnrolmentPeriodType() == enrolmentPeriodType)
                .filter(p -> automaticEnrolment == null || p.getAutomaticEnrolment() == automaticEnrolment)
                .collect(Collectors.toList());
    }

    private static final String SEARCH_VIEW_URI = "/search/view";
    public static final String SEARCH_VIEW_URL = CONTROLLER_URL + SEARCH_VIEW_URI;

    @RequestMapping(value = SEARCH_VIEW_URI + "/{academicEnrolmentPeriodId}")
    public String processSearchToViewAction(
            @PathVariable("academicEnrolmentPeriodId") final AcademicEnrolmentPeriod academicEnrolmentPeriod, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(READ_URL + "/" + academicEnrolmentPeriod.getExternalId(), model, redirectAttributes);
    }

    private static final String _DELETE_URI = "/delete";
    public static final String DELETE_URL = CONTROLLER_URL + _DELETE_URI;

    @RequestMapping(value = _DELETE_URI + "/{academicEnrolmentPeriodId}", method = RequestMethod.POST)
    public String processSearchToDeleteAction(
            @PathVariable("academicEnrolmentPeriodId") final AcademicEnrolmentPeriod academicEnrolmentPeriod, final Model model,
            final RedirectAttributes redirectAttributes) {
        try {
            deleteAcademicEnrolmentPeriod(academicEnrolmentPeriod);
            addInfoMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, "message.AcademicEnrolmentPeriod.removed.with.success"),
                    model);
            return redirect(SEARCH_URL, model, redirectAttributes);
        } catch (DomainException ex) {
            addErrorMessage(ex.getLocalizedMessage(), model);
        }
        return read(academicEnrolmentPeriod, model);
    }

    @Atomic
    public void deleteAcademicEnrolmentPeriod(AcademicEnrolmentPeriod academicEnrolmentPeriod) {
        academicEnrolmentPeriod.delete();
    }

    private static final String _CREATE_URI = "/create";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.GET)
    public String create(Model model) {
        if (getAcademicEnrolmentPeriodBean(model) == null) {
            setAcademicEnrolmentPeriodBean(new AcademicEnrolmentPeriodBean(), model);
        }
        return jspPage(_CREATE_URI);
    }

    private static final String _CREATE_POSTBACK_URI = "/create/postback";
    public static final String CREATE_POSTBACK_URL = CONTROLLER_URL + _CREATE_POSTBACK_URI;

    @RequestMapping(value = _CREATE_POSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String createPostBack(@RequestParam(value = "bean", required = true) AcademicEnrolmentPeriodBean bean,
            Model model) {
        setAcademicEnrolmentPeriodBean(bean, model);
        return getBeanJson(bean);
    }

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
    public String create(@RequestParam(value = "bean", required = true) AcademicEnrolmentPeriodBean bean, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            AcademicEnrolmentPeriod academicEnrolmentPeriod = createAcademicEnrolmentPeriod(bean);
            return redirect(READ_URL + "/" + academicEnrolmentPeriod.getExternalId(), model, redirectAttributes);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }
        setAcademicEnrolmentPeriodBean(bean, model);
        return create(model);
    }

    @Atomic
    public AcademicEnrolmentPeriod createAcademicEnrolmentPeriod(final AcademicEnrolmentPeriodBean bean) {
        return AcademicEnrolmentPeriod.create(bean);
    }

    private static final String _UPDATE_URI = "/update";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI + "/{academicEnrolmentPeriodId}", method = RequestMethod.GET)
    public String update(@PathVariable("academicEnrolmentPeriodId") AcademicEnrolmentPeriod academicEnrolmentPeriod,
            Model model) {
        if (getAcademicEnrolmentPeriodBean(model) == null) {
            setAcademicEnrolmentPeriodBean(new AcademicEnrolmentPeriodBean(academicEnrolmentPeriod), model);
        }
        model.addAttribute("academicEnrolmentPeriod", academicEnrolmentPeriod);
        return jspPage(_UPDATE_URI);
    }

    @RequestMapping(value = _UPDATE_URI + "/{academicEnrolmentPeriodId}", method = RequestMethod.POST)
    public String update(@PathVariable("academicEnrolmentPeriodId") final AcademicEnrolmentPeriod academicEnrolmentPeriod,
            @RequestParam(value = "bean", required = true) AcademicEnrolmentPeriodBean bean, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            academicEnrolmentPeriod.edit(bean);
            return redirect(READ_URL + "/" + academicEnrolmentPeriod.getExternalId(), model, redirectAttributes);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }
        return update(academicEnrolmentPeriod, model);
    }

    private static final String _READ_URI = "/read";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI + "/{academicEnrolmentPeriodId}", method = RequestMethod.GET)
    public String read(@PathVariable("academicEnrolmentPeriodId") final AcademicEnrolmentPeriod academicEnrolmentPeriod,
            final Model model) {
        model.addAttribute("academicEnrolmentPeriod", academicEnrolmentPeriod);
        if (getAcademicEnrolmentPeriodBean(model) == null) {
            setAcademicEnrolmentPeriodBean(new AcademicEnrolmentPeriodBean(academicEnrolmentPeriod), model);
        }
        return jspPage(_READ_URI);
    }

    private static final String _ADD_ALL_CURRICULAR_PLAN_URI = "/add/allcurricularplan";
    public static final String ADD_ALL_CURRICULAR_PLAN_URL = CONTROLLER_URL + _ADD_ALL_CURRICULAR_PLAN_URI;

    @RequestMapping(value = _ADD_ALL_CURRICULAR_PLAN_URI + "/{academicEnrolmentPeriodId}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody String addDegreeCurricularPlan(
            @PathVariable("academicEnrolmentPeriodId") AcademicEnrolmentPeriod academicEnrolmentPeriod,
            @RequestParam(value = "plansToAdd", required = true) List<DegreeCurricularPlan> degreeCurricularPlans,
            @RequestParam(value = "bean", required = true) AcademicEnrolmentPeriodBean bean, Model model) {
        addAllDegreeCurricularPlans(academicEnrolmentPeriod, degreeCurricularPlans);

        bean.setDegreeCurricularPlans(academicEnrolmentPeriod.getDegreeCurricularPlansSet().stream()
                .sorted(DegreeCurricularPlan.COMPARATOR_BY_PRESENTATION_NAME).collect(Collectors.toList()));
        setAcademicEnrolmentPeriodBean(bean, model);
        return getBeanJson(bean);
    }

    @Atomic
    public void addAllDegreeCurricularPlans(AcademicEnrolmentPeriod academicEnrolmentPeriod,
            List<DegreeCurricularPlan> degreeCurricularPlans) {
        for (DegreeCurricularPlan degreeCurricularPlan : degreeCurricularPlans) {
            academicEnrolmentPeriod.addDegreeCurricularPlans(degreeCurricularPlan);
        }
    }

    private static final String _REMOVE_CURRICULAR_PLAN_URI = "/remove/curricularplan";
    public static final String REMOVE_CURRICULAR_PLAN_URL = CONTROLLER_URL + _REMOVE_CURRICULAR_PLAN_URI;

    @RequestMapping(value = _REMOVE_CURRICULAR_PLAN_URI + "/{academicEnrolmentPeriodId}/{degreeCurricularPlanId}",
            method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String removeDegreeCurricularPlan(
            @PathVariable("academicEnrolmentPeriodId") AcademicEnrolmentPeriod academicEnrolmentPeriod,
            @PathVariable("degreeCurricularPlanId") DegreeCurricularPlan degreeCurricularPlan,
            @RequestParam(value = "bean", required = true) AcademicEnrolmentPeriodBean bean, Model model) {
        removeDegreeCurricularPlan(academicEnrolmentPeriod, degreeCurricularPlan);

        bean.setDegreeCurricularPlans(academicEnrolmentPeriod.getDegreeCurricularPlansSet().stream()
                .sorted(DegreeCurricularPlan.COMPARATOR_BY_PRESENTATION_NAME).collect(Collectors.toList()));
        setAcademicEnrolmentPeriodBean(bean, model);
        return getBeanJson(bean);
    }

    @Atomic
    public void removeDegreeCurricularPlan(AcademicEnrolmentPeriod academicEnrolmentPeriod,
            DegreeCurricularPlan degreeCurricularPlan) {
        academicEnrolmentPeriod.removeDegreeCurricularPlans(degreeCurricularPlan);
    }

    private static final String _REMOVE_ALL_CURRICULAR_PLAN_URI = "/remove/allcurricularplans";
    public static final String REMOVE_ALL_CURRICULAR_PLAN_URL = CONTROLLER_URL + _REMOVE_ALL_CURRICULAR_PLAN_URI;

    @RequestMapping(value = _REMOVE_ALL_CURRICULAR_PLAN_URI + "/{academicEnrolmentPeriodId}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody String removeAllDegreeCurricularPlans(
            @PathVariable("academicEnrolmentPeriodId") AcademicEnrolmentPeriod academicEnrolmentPeriod,
            @RequestParam(value = "plansToRemove") List<DegreeCurricularPlan> degreeCurricularPlans,
            @RequestParam(value = "bean", required = true) AcademicEnrolmentPeriodBean bean, Model model) {
        removeAllDegreeCurricularPlans(academicEnrolmentPeriod, degreeCurricularPlans);

        bean.setDegreeCurricularPlans(academicEnrolmentPeriod.getDegreeCurricularPlansSet().stream()
                .sorted(DegreeCurricularPlan.COMPARATOR_BY_PRESENTATION_NAME).collect(Collectors.toList()));
        setAcademicEnrolmentPeriodBean(bean, model);
        return getBeanJson(bean);
    }

    @Atomic
    public void removeAllDegreeCurricularPlans(AcademicEnrolmentPeriod academicEnrolmentPeriod,
            List<DegreeCurricularPlan> degreeCurricularPlans) {
        for (DegreeCurricularPlan degreeCurricularPlan : degreeCurricularPlans) {
            academicEnrolmentPeriod.removeDegreeCurricularPlans(degreeCurricularPlan);
        }
    }

    private static final String _ADD_ALL_STATUTE_TYPE_URI = "/add/allstatutetype";
    public static final String ADD_ALL_STATUTE_TYPE_URL = CONTROLLER_URL + _ADD_ALL_STATUTE_TYPE_URI;

    @RequestMapping(value = _ADD_ALL_STATUTE_TYPE_URI + "/{academicEnrolmentPeriodId}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody String addStatuteType(
            @PathVariable("academicEnrolmentPeriodId") AcademicEnrolmentPeriod academicEnrolmentPeriod,
            @RequestParam(value = "statutesToAdd", required = true) List<StatuteType> statuteTypes,
            @RequestParam(value = "bean", required = true) AcademicEnrolmentPeriodBean bean, Model model) {
        addAllStatuteTypes(academicEnrolmentPeriod, statuteTypes);

        bean.setStatuteTypes(academicEnrolmentPeriod.getStatuteTypesSet().stream().sorted(StatuteType.COMPARATOR_BY_NAME)
                .collect(Collectors.toList()));
        setAcademicEnrolmentPeriodBean(bean, model);
        return getBeanJson(bean);
    }

    @Atomic
    public void addAllStatuteTypes(AcademicEnrolmentPeriod academicEnrolmentPeriod, List<StatuteType> statuteTypes) {
        for (StatuteType statuteType : statuteTypes) {
            academicEnrolmentPeriod.addStatuteTypes(statuteType);
        }
    }

    private static final String _REMOVE_STATUTE_TYPE_URI = "/remove/statutetype";
    public static final String REMOVE_STATUTE_TYPE_URL = CONTROLLER_URL + _REMOVE_STATUTE_TYPE_URI;

    @RequestMapping(value = _REMOVE_STATUTE_TYPE_URI + "/{academicEnrolmentPeriodId}/{statuteTypeId}",
            method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String removeStatuteType(
            @PathVariable("academicEnrolmentPeriodId") AcademicEnrolmentPeriod academicEnrolmentPeriod,
            @PathVariable("statuteTypeId") StatuteType statuteType,
            @RequestParam(value = "bean", required = true) AcademicEnrolmentPeriodBean bean, Model model) {
        removeStatuteType(academicEnrolmentPeriod, statuteType);

        bean.setStatuteTypes(academicEnrolmentPeriod.getStatuteTypesSet().stream().sorted(StatuteType.COMPARATOR_BY_NAME)
                .collect(Collectors.toList()));
        setAcademicEnrolmentPeriodBean(bean, model);
        return getBeanJson(bean);
    }

    @Atomic
    public void removeStatuteType(AcademicEnrolmentPeriod academicEnrolmentPeriod, StatuteType statuteType) {
        academicEnrolmentPeriod.removeStatuteTypes(statuteType);
    }

    private static final String _REMOVE_ALL_STATUTE_TYPE_URI = "/remove/allstatutetype";
    public static final String REMOVE_ALL_STATUTE_TYPE_URL = CONTROLLER_URL + _REMOVE_ALL_STATUTE_TYPE_URI;

    @RequestMapping(value = _REMOVE_ALL_STATUTE_TYPE_URI + "/{academicEnrolmentPeriodId}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody String removeAllStatuteTypes(
            @PathVariable("academicEnrolmentPeriodId") AcademicEnrolmentPeriod academicEnrolmentPeriod,
            @RequestParam(value = "statutesToRemove", required = true) List<StatuteType> statuteTypes,
            @RequestParam(value = "bean", required = true) AcademicEnrolmentPeriodBean bean, Model model) {
        removeAllStatuteTypes(academicEnrolmentPeriod, statuteTypes);

        bean.setStatuteTypes(academicEnrolmentPeriod.getStatuteTypesSet().stream().sorted(StatuteType.COMPARATOR_BY_NAME)
                .collect(Collectors.toList()));
        setAcademicEnrolmentPeriodBean(bean, model);
        return getBeanJson(bean);
    }

    @Atomic
    public void removeAllStatuteTypes(AcademicEnrolmentPeriod academicEnrolmentPeriod, List<StatuteType> statuteTypes) {
        for (StatuteType statuteType : statuteTypes) {
            academicEnrolmentPeriod.removeStatuteTypes(statuteType);
        }
    }

    private static final String _ADD_ALL_INGRESSION_TYPE_URI = "/add/allingressiontype";
    public static final String ADD_ALL_INGRESSION_TYPE_URL = CONTROLLER_URL + _ADD_ALL_INGRESSION_TYPE_URI;

    @RequestMapping(value = _ADD_ALL_INGRESSION_TYPE_URI + "/{academicEnrolmentPeriodId}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody String addIngressionType(
            @PathVariable("academicEnrolmentPeriodId") AcademicEnrolmentPeriod academicEnrolmentPeriod,
            @RequestParam(value = "ingressionsToAdd", required = true) List<IngressionType> ingressionTypes,
            @RequestParam(value = "bean", required = true) AcademicEnrolmentPeriodBean bean, Model model) {
        addAllIngressionTypes(academicEnrolmentPeriod, ingressionTypes);

        bean.setIngressionTypes(academicEnrolmentPeriod.getIngressionTypesSet().stream()
                .sorted(AcademicEnrolmentPeriodBean.INGRESSION_TYPE_COMPARATOR_BY_DESCRIPTION).collect(Collectors.toList()));
        setAcademicEnrolmentPeriodBean(bean, model);
        return getBeanJson(bean);
    }

    @Atomic
    public void addAllIngressionTypes(AcademicEnrolmentPeriod academicEnrolmentPeriod, List<IngressionType> ingressionTypes) {
        for (IngressionType ingressionType : ingressionTypes) {
            academicEnrolmentPeriod.addIngressionTypes(ingressionType);
        }
    }

    private static final String _REMOVE_INGRESSION_TYPE_URI = "/remove/ingressiontype";
    public static final String REMOVE_INGRESSION_TYPE_URL = CONTROLLER_URL + _REMOVE_INGRESSION_TYPE_URI;

    @RequestMapping(value = _REMOVE_INGRESSION_TYPE_URI + "/{academicEnrolmentPeriodId}/{ingressionTypeId}",
            method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String removeIngressionType(
            @PathVariable("academicEnrolmentPeriodId") AcademicEnrolmentPeriod academicEnrolmentPeriod,
            @PathVariable("ingressionTypeId") IngressionType ingressionType,
            @RequestParam(value = "bean", required = true) AcademicEnrolmentPeriodBean bean, Model model) {
        removeIngressionType(academicEnrolmentPeriod, ingressionType);

        bean.setIngressionTypes(academicEnrolmentPeriod.getIngressionTypesSet().stream()
                .sorted(AcademicEnrolmentPeriodBean.INGRESSION_TYPE_COMPARATOR_BY_DESCRIPTION).collect(Collectors.toList()));
        setAcademicEnrolmentPeriodBean(bean, model);
        return getBeanJson(bean);
    }

    @Atomic
    public void removeIngressionType(AcademicEnrolmentPeriod academicEnrolmentPeriod, IngressionType ingressionType) {
        academicEnrolmentPeriod.removeIngressionTypes(ingressionType);
    }

    private static final String _REMOVE_ALL_INGRESSION_TYPE_URI = "/remove/allingressiontype";
    public static final String REMOVE_ALL_INGRESSION_TYPE_URL = CONTROLLER_URL + _REMOVE_ALL_INGRESSION_TYPE_URI;

    @RequestMapping(value = _REMOVE_ALL_INGRESSION_TYPE_URI + "/{academicEnrolmentPeriodId}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody String removeAllIngressionTypes(
            @PathVariable("academicEnrolmentPeriodId") AcademicEnrolmentPeriod academicEnrolmentPeriod,
            @RequestParam(value = "ingressionsToRemove", required = true) List<IngressionType> ingressionTypes,
            @RequestParam(value = "bean", required = true) AcademicEnrolmentPeriodBean bean, Model model) {
        removeAllIngressionTypes(academicEnrolmentPeriod, ingressionTypes);

        bean.setIngressionTypes(academicEnrolmentPeriod.getIngressionTypesSet().stream()
                .sorted(AcademicEnrolmentPeriodBean.INGRESSION_TYPE_COMPARATOR_BY_DESCRIPTION).collect(Collectors.toList()));
        setAcademicEnrolmentPeriodBean(bean, model);
        return getBeanJson(bean);
    }

    @Atomic
    public void removeAllIngressionTypes(AcademicEnrolmentPeriod academicEnrolmentPeriod, List<IngressionType> ingressionTypes) {
        for (IngressionType ingressionType : ingressionTypes) {
            academicEnrolmentPeriod.removeIngressionTypes(ingressionType);
        }
    }

}
