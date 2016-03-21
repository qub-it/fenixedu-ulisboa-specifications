package org.fenixedu.ulisboa.specifications.ui.degrees.extendeddegreeinfo;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.extendedDegreeInfo",
        accessGroup = "academic(MANAGE_DEGREE_CURRICULAR_PLANS) | #managers")
@RequestMapping(ExtendedDegreeInfoController.CONTROLLER_URL)
public class ExtendedDegreeInfoController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/degrees/extendeddegreeinfo";
    public static final String VIEWS_URL = "fenixedu-ulisboa-specifications/degrees/extendeddegreeinfo";

    public static final Map<String, DummyEDI> cache = new HashMap<String, DummyEDI>();

    @RequestMapping
    public String home() {
        return "forward:" + SEARCH_URL;
    }

    private void setExtendedDegreeInfoBean(ExtendedDegreeInfoBean bean, Model model) {
        map(bean);
        bean.setDegreeType(bean.getDegree().getDegreeTypeName());
        bean.setDegreeAcron(bean.getDegree().getSigla());
        model.addAttribute("extendedDegreeInfoBeanJson", getBeanJson(bean));
        model.addAttribute("extendedDegreeInfoBean", bean);
    }

    private void createExtendedDegreeInfoBean(Model model) {
        final ExtendedDegreeInfoBean bean = new ExtendedDegreeInfoBean();
        setExtendedDegreeInfoBean(bean, model);
    }

    private static final String _SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(_SEARCH_URI)
    public String search(@RequestParam(value = "bean", required = false) ExtendedDegreeInfoBean bean, Model model) {
        if (bean == null) {
            createExtendedDegreeInfoBean(model);
        } else {
            setExtendedDegreeInfoBean(bean, model);
        }
        return angular();
    }

    private static final String _UPDATE_URI = "/update";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(_UPDATE_URI)
    public String update(@RequestParam(value = "bean", required = false) ExtendedDegreeInfoBean bean, Model model) {
        store(bean);
        setExtendedDegreeInfoBean(bean, model);
        return angular();
    }

    private String angular() {
        return VIEWS_URL + "/angular";
    }

    private void map(ExtendedDegreeInfoBean bean) {
        String key = bean.getExecutionYear().getExternalId() + "+" + bean.getDegree().getExternalId();
        if (!cache.containsKey(key)) {
            DummyEDI edi = new DummyEDI();
            edi.scientificAreas =
                    new LocalizedString(new Locale("pt", "PT"), bean.getExecutionYear().getBeginCivilYear() + "/"
                            + bean.getExecutionYear().getEndCivilYear());
            cache.put(key, edi);
        }
        DummyEDI edi = cache.get(key);
        bean.setScientificAreas(edi.scientificAreas);
        bean.setStudyRegime(edi.studyRegime);
        bean.setStudyProgrammeRequirements(edi.studyProgrammeRequirements);
        bean.setHigherEducationAccess(edi.higherEducationAccess);
        bean.setProfessionalStatus(edi.professionalStatus);
        bean.setSupplementExtraInformation(edi.supplementExtraInformation);
        bean.setSupplementOtherSources(edi.supplementOtherSources);
    }

    private void store(ExtendedDegreeInfoBean bean) {
        String key = bean.getExecutionYear().getExternalId() + "+" + bean.getDegree().getExternalId();
        DummyEDI edi = cache.get(key);
        edi.scientificAreas = bean.getScientificAreas();
        edi.studyRegime = bean.getStudyRegime();
        edi.studyProgrammeRequirements = bean.getStudyProgrammeRequirements();
        edi.higherEducationAccess = bean.getHigherEducationAccess();
        edi.professionalStatus = bean.getProfessionalStatus();
        edi.supplementExtraInformation = bean.getSupplementExtraInformation();
        edi.supplementOtherSources = bean.getSupplementOtherSources();
    }

    private static class DummyEDI {
        LocalizedString scientificAreas;
        LocalizedString studyRegime;
        LocalizedString studyProgrammeRequirements;
        LocalizedString higherEducationAccess;
        LocalizedString professionalStatus;
        LocalizedString supplementExtraInformation;
        LocalizedString supplementOtherSources;
    }
}
