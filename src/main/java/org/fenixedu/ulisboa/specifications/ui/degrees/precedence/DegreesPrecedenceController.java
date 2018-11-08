package org.fenixedu.ulisboa.specifications.ui.degrees.precedence;

import java.util.List;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;

import edu.emory.mathcs.backport.java.util.Collections;
import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.degreePrecedences",
        accessGroup = "logged")
@RequestMapping(DegreesPrecedenceController.CONTROLLER_URL)
public class DegreesPrecedenceController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/degreesprecedence";
    public static final String JSP_PATH = "fenixedu-ulisboa-specifications/degreesprecedence";

    @RequestMapping
    public String home() {
        return "forward:" + SEARCH_URL;
    }

    private static final String _SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(_SEARCH_URI)
    public String search(final Model model) {
        final List<Degree> degrees = Lists.newArrayList(Bennu.getInstance().getDegreesSet());
        Collections.sort(degrees, Degree.COMPARATOR_BY_DEGREE_TYPE_AND_NAME_AND_ID);

        model.addAttribute("degrees", degrees);

        return jspPage(_SEARCH_URI);
    }

    private static final String _VIEW_URI = "/view";
    public static final String VIEW_URL = CONTROLLER_URL + _VIEW_URI;

    @RequestMapping(_VIEW_URI + "/{degreeId}")
    public String view(@PathVariable("degreeId") final Degree degree, final Model model) {
        model.addAttribute("degree", degree);

        return jspPage(_VIEW_URI);
    }

    private static final String _ADD_DEGREE_URI = "/adddegree";
    public static final String ADD_DEGREE_URL = CONTROLLER_URL + _ADD_DEGREE_URI;

    @RequestMapping(value = _ADD_DEGREE_URI + "/{degreeId}", method = RequestMethod.GET)
    public String adddegree(@PathVariable("degreeId") final Degree degree, final Model model) {
        return _adddegree(degree, model, new DegreePrecedencesBean());
    }

    private String _adddegree(final Degree degree, final Model model, final DegreePrecedencesBean bean) {
        model.addAttribute("degree", degree);
        model.addAttribute("bean", bean);
        model.addAttribute("beanJson", getBeanJson(bean));

        return jspPage(_ADD_DEGREE_URI);
    }

    @RequestMapping(value = _ADD_DEGREE_URI + "/{degreeId}", method = RequestMethod.POST)
    public String adddegreepost(@PathVariable("degreeId") final Degree degree,
            @RequestParam("precedentDegreeId") final Degree precedentDegree, final Model model) {

        try {

            if (!degree.getCode().equalsIgnoreCase(precedentDegree.getCode())) {
                addPrecedentDegree(degree, precedentDegree);
            } else {
                addErrorMessage(ULisboaSpecificationsUtil.bundle("error.DegreePrecedences.degree.and.precedent.degree.with.same.code"), model);
            }
            
            return "redirect:" + VIEW_URL + "/" + degree.getExternalId();

        } catch (final DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return jspPage(_ADD_DEGREE_URI);
    }

    @Atomic
    private void addPrecedentDegree(final Degree degree, final Degree precedentDegree) {
        degree.getPrecedentDegreesSet().add(precedentDegree);
    }

    @Atomic
    private void removePrecedentDegree(final Degree degree, final Degree precedentDegree) {
        degree.getPrecedentDegreesSet().remove(precedentDegree);
    }

    private static final String _ADD_DEGREE_POSTBACK_URI = "/adddegreepostback";
    public static final String ADD_DEGREE_POSTBACK_URL = CONTROLLER_URL + _ADD_DEGREE_POSTBACK_URI;

    @RequestMapping(value = _ADD_DEGREE_POSTBACK_URI + "/{degreeId}", method = RequestMethod.POST)
    public String adddegreepostback(@PathVariable("degreeId") final Degree degree,
            @RequestParam("bean") final DegreePrecedencesBean bean, final Model model) {
        return _adddegree(degree, model, bean);
    }

    private static final String _REMOVE_DEGREE_URI = "/removedegree";
    public static final String REMOVE_DEGREE_URL = CONTROLLER_URL + _REMOVE_DEGREE_URI;

    @RequestMapping(value = _REMOVE_DEGREE_URI + "/{degreeId}", method = RequestMethod.POST)
    public String removedegree(@PathVariable("degreeId") final Degree degree,
            @RequestParam("degreeToRemoveId") final Degree degreeToRemove, final Model model) {

        try {
            removePrecedentDegree(degree, degreeToRemove);
            return "redirect:" + VIEW_URL + "/" + degree.getExternalId();
        } catch (final DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return view(degreeToRemove, model);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page.substring(1, page.length());
    }

}
