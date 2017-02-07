package org.fenixedu.ulisboa.specifications.ui.degrees.extendeddegreeinfo;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.DegreeInfo;
import org.fenixedu.academic.util.MultiLanguageString;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.ExtendedDegreeInfo;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.extendedDegreeInfo",
        accessGroup = "academic(MANAGE_DEGREE_CURRICULAR_PLANS) | #managers")
@RequestMapping(ExtendedDegreeInfoController.CONTROLLER_URL)
public class ExtendedDegreeInfoController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/degrees/extendeddegreeinfo";
    public static final String VIEWS_URL = "fenixedu-ulisboa-specifications/degrees/extendeddegreeinfo";

    @RequestMapping
    public String home() {
        return "forward:" + SEARCH_URL;
    }

    private void setExtendedDegreeInfoBean(final ExtendedDegreeInfoBean bean, final Model model) {
        load(bean);
        bean.setDegreeType(bean.getDegree().getDegreeTypeName());
        bean.setDegreeAcron(bean.getDegree().getSigla());
        model.addAttribute("extendedDegreeInfoBeanJson", getBeanJson(bean));
        model.addAttribute("extendedDegreeInfoBean", bean);
    }

    private void createExtendedDegreeInfoBean(final Model model) {
        final ExtendedDegreeInfoBean bean = new ExtendedDegreeInfoBean();
        setExtendedDegreeInfoBean(bean, model);
    }

    private static final String _SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(_SEARCH_URI)
    public String search(@RequestParam(value = "bean", required = false) final ExtendedDegreeInfoBean bean, final Model model) {
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
    public String update(@RequestParam(value = "bean", required = false) final ExtendedDegreeInfoBean bean, final Model model) {
        store(bean);
        setExtendedDegreeInfoBean(bean, model);
        return angular();
    }

    private String angular() {
        return VIEWS_URL + "/angular";
    }

    private void load(final ExtendedDegreeInfoBean bean) {
        ExtendedDegreeInfo edi = ExtendedDegreeInfo.getMostRecent(bean.getExecutionYear(), bean.getDegree());

        if (edi.getDegreeInfo().getName() != null) {
            bean.setName(edi.getDegreeInfo().getName().toLocalizedString());
        }
        if (edi.getDegreeInfo().getDescription() != null) {
            bean.setDescription(edi.getDegreeInfo().getDescription().toLocalizedString());
        }
        if (edi.getDegreeInfo().getHistory() != null) {
            bean.setHistory(edi.getDegreeInfo().getHistory().toLocalizedString());
        }
        if (edi.getDegreeInfo().getObjectives() != null) {
            bean.setObjectives(edi.getDegreeInfo().getObjectives().toLocalizedString());
        }
        if (edi.getDegreeInfo().getDesignedFor() != null) {
            bean.setDesignedFor(edi.getDegreeInfo().getDesignedFor().toLocalizedString());
        }
        if (edi.getDegreeInfo().getProfessionalExits() != null) {
            bean.setProfessionalExits(edi.getDegreeInfo().getProfessionalExits().toLocalizedString());
        }
        if (edi.getDegreeInfo().getOperationalRegime() != null) {
            bean.setOperationalRegime(edi.getDegreeInfo().getOperationalRegime().toLocalizedString());
        }
        if (edi.getDegreeInfo().getGratuity() != null) {
            bean.setGratuity(edi.getDegreeInfo().getGratuity().toLocalizedString());
        }
        if (edi.getDegreeInfo().getAdditionalInfo() != null) {
            bean.setAdditionalInfo(edi.getDegreeInfo().getAdditionalInfo().toLocalizedString());
        }
        if (edi.getDegreeInfo().getLinks() != null) {
            bean.setLinks(edi.getDegreeInfo().getLinks().toLocalizedString());
        }
        if (edi.getDegreeInfo().getTestIngression() != null) {
            bean.setTestIngression(edi.getDegreeInfo().getTestIngression().toLocalizedString());
        }
        if (edi.getDegreeInfo().getClassifications() != null) {
            bean.setClassifications(edi.getDegreeInfo().getClassifications().toLocalizedString());
        }
        if (edi.getDegreeInfo().getAccessRequisites() != null) {
            bean.setAccessRequisites(edi.getDegreeInfo().getAccessRequisites().toLocalizedString());
        }
        if (edi.getDegreeInfo().getCandidacyDocuments() != null) {
            bean.setCandidacyDocuments(edi.getDegreeInfo().getCandidacyDocuments().toLocalizedString());
        }
        if (edi.getDegreeInfo().getDriftsInitial() != null) {
            bean.setDriftsInitial(edi.getDegreeInfo().getDriftsInitial());
        }
        if (edi.getDegreeInfo().getDriftsFirst() != null) {
            bean.setDriftsFirst(edi.getDegreeInfo().getDriftsFirst());
        }
        if (edi.getDegreeInfo().getDriftsSecond() != null) {
            bean.setDriftsSecond(edi.getDegreeInfo().getDriftsSecond());
        }
        if (edi.getDegreeInfo().getMarkMin() != null) {
            bean.setMarkMin(edi.getDegreeInfo().getMarkMin());
        }
        if (edi.getDegreeInfo().getMarkMax() != null) {
            bean.setMarkMax(edi.getDegreeInfo().getMarkMax());
        }
        if (edi.getDegreeInfo().getMarkAverage() != null) {
            bean.setMarkAverage(edi.getDegreeInfo().getMarkAverage());
        }
        if (edi.getDegreeInfo().getQualificationLevel() != null) {
            bean.setQualificationLevel(edi.getDegreeInfo().getQualificationLevel().toLocalizedString());
        }
        if (edi.getDegreeInfo().getRecognitions() != null) {
            bean.setRecognitions(edi.getDegreeInfo().getRecognitions().toLocalizedString());
        }

        bean.setScientificAreas(edi.getScientificAreas());
        bean.setStudyProgrammeDuration(edi.getStudyProgrammeDuration());
        bean.setStudyRegime(edi.getStudyRegime());
        bean.setStudyProgrammeRequirements(edi.getStudyProgrammeRequirements());
        bean.setHigherEducationAccess(edi.getHigherEducationAccess());
        bean.setProfessionalStatus(edi.getProfessionalStatus());
        bean.setSupplementExtraInformation(edi.getSupplementExtraInformation());
        bean.setSupplementOtherSources(edi.getSupplementOtherSources());

        List<CourseGroupDegreeInfoBean> courseGroupInfos = edi.getCourseGroupDegreeInfosSet().stream()
                .map(info -> new CourseGroupDegreeInfoBean(info)).collect(Collectors.toList());
        bean.setCourseGroupInfos(courseGroupInfos);
    }

    @Atomic
    private void store(final ExtendedDegreeInfoBean bean) {
        ExtendedDegreeInfo edi = ExtendedDegreeInfo.getOrCreate(bean.getExecutionYear(), bean.getDegree());
        DegreeInfo di = edi.getDegreeInfo();

        di.setName(MultiLanguageString.fromLocalizedString(bean.getName()));
        di.setDescription(MultiLanguageString.fromLocalizedString(bean.getDescription()));
        di.setHistory(MultiLanguageString.fromLocalizedString(bean.getHistory()));
        di.setObjectives(MultiLanguageString.fromLocalizedString(bean.getObjectives()));
        di.setDesignedFor(MultiLanguageString.fromLocalizedString(bean.getDesignedFor()));
        di.setProfessionalExits(MultiLanguageString.fromLocalizedString(bean.getProfessionalExits()));
        di.setOperationalRegime(MultiLanguageString.fromLocalizedString(bean.getOperationalRegime()));
        di.setGratuity(MultiLanguageString.fromLocalizedString(bean.getGratuity()));
        di.setAdditionalInfo(MultiLanguageString.fromLocalizedString(bean.getAdditionalInfo()));
        di.setLinks(MultiLanguageString.fromLocalizedString(bean.getLinks()));
        di.setTestIngression(MultiLanguageString.fromLocalizedString(bean.getTestIngression()));
        di.setClassifications(MultiLanguageString.fromLocalizedString(bean.getClassifications()));
        di.setAccessRequisites(MultiLanguageString.fromLocalizedString(bean.getAccessRequisites()));
        di.setCandidacyDocuments(MultiLanguageString.fromLocalizedString(bean.getCandidacyDocuments()));
        di.setDriftsInitial(bean.getDriftsInitial());
        di.setDriftsFirst(bean.getDriftsFirst());
        di.setDriftsSecond(bean.getDriftsSecond());
        di.setMarkMin(bean.getMarkMin());
        di.setMarkMax(bean.getMarkMax());
        di.setMarkAverage(bean.getMarkAverage());
        di.setQualificationLevel(MultiLanguageString.fromLocalizedString(bean.getQualificationLevel()));
        di.setRecognitions(MultiLanguageString.fromLocalizedString(bean.getRecognitions()));

        edi.setScientificAreas(bean.getScientificAreas());
        edi.setStudyProgrammeDuration(bean.getStudyProgrammeDuration());
        edi.setStudyRegime(bean.getStudyRegime());
        edi.setStudyProgrammeRequirements(bean.getStudyProgrammeRequirements());
        edi.setHigherEducationAccess(bean.getHigherEducationAccess());
        edi.setProfessionalStatus(bean.getProfessionalStatus());
        edi.setSupplementExtraInformation(bean.getSupplementExtraInformation());
        edi.setSupplementOtherSources(bean.getSupplementOtherSources());
    }

}
