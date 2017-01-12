package org.fenixedu.ulisboa.specifications.ui.degrees.extendeddegreeinfo;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.CourseGroupDegreeInfo;
import org.fenixedu.ulisboa.specifications.domain.ExtendedDegreeInfo;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
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

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.courseGroupDegreeInfo",
        accessGroup = "academic(MANAGE_DEGREE_CURRICULAR_PLANS) | #managers")
@RequestMapping(CourseGroupDegreeInfoController.CONTROLLER_URL)
public class CourseGroupDegreeInfoController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/degrees/coursegroupdegreeinfo";
    public static final String JSP_PATH = "fenixedu-ulisboa-specifications/degrees/coursegroupdegreeinfo";

    @RequestMapping
    public String home(Model model, RedirectAttributes redirectAttributes) {
        return redirect(SEARCH_URL, model, redirectAttributes);
    }

    private String jspPage(final String page) {
        return JSP_PATH + page;
    }

    private CourseGroupDegreeInfoBean getCourseGroupDegreeInfoBean(Model model) {
        return (CourseGroupDegreeInfoBean) model.asMap().get("courseGroupDegreeInfoBean");
    }

    private void setCourseGroupDegreeInfoBean(CourseGroupDegreeInfoBean bean, Model model) {
        bean.updateLists();
        model.addAttribute("courseGroupDegreeInfoBeanJson", getBeanJson(bean));
        model.addAttribute("courseGroupDegreeInfoBean", bean);
    }

    private static final String _SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI, method = RequestMethod.GET)
    public String search(@RequestParam(value = "executionYear", required = false) ExecutionYear executionYear,
            @RequestParam(value = "degree", required = false) Degree degree, Model model) {

        List<ExecutionYear> executionYears = ExecutionYear.readNotClosedExecutionYears();
        executionYears =
                executionYears.stream().sorted(ExecutionYear.COMPARATOR_BY_BEGIN_DATE.reversed()).collect(Collectors.toList());

        Set<Degree> allDegrees = new TreeSet<Degree>(Degree.COMPARATOR_BY_DEGREE_TYPE_AND_NAME_AND_ID);
        allDegrees.addAll(Bennu.getInstance().getDegreesSet());

        model.addAttribute("executionYears", executionYears);
        model.addAttribute("degrees", allDegrees);

        List<CourseGroupDegreeInfo> degreeDocumentInfoSet = filterCourseGroupDegreeInfos(executionYear, degree);
        model.addAttribute("degreeDocumentInfoResult", degreeDocumentInfoSet);

        return jspPage(_SEARCH_URI);
    }

    private List<CourseGroupDegreeInfo> filterCourseGroupDegreeInfos(final ExecutionYear executionYear, final Degree degree) {
        return CourseGroupDegreeInfo.findAll().stream()
                .filter(info -> executionYear == null
                        || info.getExtendedDegreeInfo().getDegreeInfo().getExecutionYear() == executionYear)
                .filter(info -> degree == null || info.getExtendedDegreeInfo().getDegreeInfo().getDegree() == degree)
                .collect(Collectors.toList());
    }

    private static final String _SEARCH_VIEW_URI = "/search/view";
    public static final String SEARCH_VIEW_URL = CONTROLLER_URL + _SEARCH_VIEW_URI;

    @RequestMapping(value = _SEARCH_VIEW_URI + "/{oid}")
    public String processSearchToViewAction(@PathVariable("oid") final CourseGroupDegreeInfo degreeDocumentInfo,
            final Model model, final RedirectAttributes redirectAttributes) {
        return redirect(READ_URL + "/" + degreeDocumentInfo.getExternalId(), model, redirectAttributes);
    }

    private static final String _DELETE_URI = "/delete";
    public static final String DELETE_URL = CONTROLLER_URL + _DELETE_URI;

    @RequestMapping(value = _DELETE_URI + "/{oid}", method = RequestMethod.POST)
    public String delete(@PathVariable("oid") CourseGroupDegreeInfo info, Model model, RedirectAttributes redirectAttributes) {
        try {
            String degreeName = "";
            String courseGroupName = "";
            if (info.getExtendedDegreeInfo() != null) {
                degreeName = info.getExtendedDegreeInfo().getDegreeInfo().getName().getContent();
            }
            if (info.getCourseGroup() != null) {
                courseGroupName = info.getCourseGroup().getName();
            }
            deleteDocumentInfo(info);

            addInfoMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, "label.info.CourseGroupDegreeInfo.successfulDelete",
                    degreeName, courseGroupName), model);
            return redirect(SEARCH_URL, model, redirectAttributes);
        } catch (Throwable ex) {
            addErrorMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, "label.error.delete") + ex.getLocalizedMessage(),
                    model);
        }
        return read(info, model);
    }

    @Atomic
    public void deleteDocumentInfo(CourseGroupDegreeInfo info) {
        info.delete();
    }

    private static final String _CREATE_URI = "/create";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.GET)
    public String create(Model model) {
        if (getCourseGroupDegreeInfoBean(model) == null) {
            setCourseGroupDegreeInfoBean(new CourseGroupDegreeInfoBean(), model);
        }
        return jspPage(_CREATE_URI);
    }

    private static final String _CREATE_POSTBACK_URI = "/create/postback";
    public static final String CREATE_POSTBACK_URL = CONTROLLER_URL + _CREATE_POSTBACK_URI;

    @RequestMapping(value = _CREATE_POSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String createPostBack(@RequestParam(value = "bean", required = true) CourseGroupDegreeInfoBean bean,
            Model model) {
        setCourseGroupDegreeInfoBean(bean, model);
        return getBeanJson(bean);
    }

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
    public String create(@RequestParam(value = "bean", required = true) CourseGroupDegreeInfoBean bean, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            CourseGroupDegreeInfo degreeDocumentInfo = createDegreeDocumentInfo(bean);
            return redirect(READ_URL + "/" + degreeDocumentInfo.getExternalId(), model, redirectAttributes);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        } catch (org.fenixedu.bennu.core.domain.exceptions.DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }
        setCourseGroupDegreeInfoBean(bean, model);
        return create(model);
    }

    @Atomic
    public CourseGroupDegreeInfo createDegreeDocumentInfo(final CourseGroupDegreeInfoBean bean) {
        if (bean.getExecutionYear() == null || bean.getDegree() == null) {
            throw new ULisboaSpecificationsDomainException("error.CourseGroupDegreeInfo.executionYear.degree.all.required");
        }
        ExtendedDegreeInfo extendedDegreeInfo = ExtendedDegreeInfo.getOrCreate(bean.getExecutionYear(), bean.getDegree());
        return CourseGroupDegreeInfo.create(bean.getName(), extendedDegreeInfo, bean.getCourseGroup());
    }

    private static final String _UPDATE_URI = "/update";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI + "/{oid}", method = RequestMethod.GET)
    public String update(@PathVariable("oid") CourseGroupDegreeInfo degreeDocumentInfo, Model model) {
        if (getCourseGroupDegreeInfoBean(model) == null) {
            setCourseGroupDegreeInfoBean(new CourseGroupDegreeInfoBean(degreeDocumentInfo), model);
        }
        model.addAttribute("degreeDocumentInfo", degreeDocumentInfo);
        return jspPage(_UPDATE_URI);
    }

    @RequestMapping(value = _UPDATE_URI + "/{oid}", method = RequestMethod.POST)
    public String update(@PathVariable("oid") final CourseGroupDegreeInfo degreeDocumentInfo,
            @RequestParam(value = "bean", required = true) CourseGroupDegreeInfoBean bean, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            degreeDocumentInfo.edit(bean.getName(), bean.getCourseGroup());
            return redirect(READ_URL + "/" + degreeDocumentInfo.getExternalId(), model, redirectAttributes);
        } catch (DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
        }
        return update(degreeDocumentInfo, model);
    }

    private static final String _READ_URI = "/read";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI + "/{oid}", method = RequestMethod.GET)
    public String read(@PathVariable("oid") final CourseGroupDegreeInfo degreeDocumentInfo, final Model model) {
        model.addAttribute("degreeDocumentInfo", degreeDocumentInfo);
        if (getCourseGroupDegreeInfoBean(model) == null) {
            setCourseGroupDegreeInfoBean(new CourseGroupDegreeInfoBean(degreeDocumentInfo), model);
        }
        return jspPage(_READ_URI);
    }
}
