package org.fenixedu.ulisboa.specifications.ui.degrees.extendeddegreeinfo;

import org.fenixedu.academic.domain.DegreeInfo;
import org.fenixedu.academic.util.MultiLanguageString;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.i18n.LocalizedString;
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

    private void setExtendedDegreeInfoBean(ExtendedDegreeInfoBean bean, Model model) {
        load(bean);
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

    private void load(ExtendedDegreeInfoBean bean) {
        ExtendedDegreeInfo edi = ExtendedDegreeInfo.getMostRecent(bean.getExecutionYear(), bean.getDegree());

        bean.setName(edi.getDegreeInfo().getName().toLocalizedString());
        if (edi.getDegreeInfo().getDescription() != null)
            bean.setDescription(edi.getDegreeInfo().getDescription().toLocalizedString());
        bean.setHistory(edi.getDegreeInfo().getHistory().toLocalizedString());
        bean.setObjectives(edi.getDegreeInfo().getObjectives().toLocalizedString());
        bean.setDesignedFor(edi.getDegreeInfo().getDesignedFor().toLocalizedString());
        bean.setProfessionalExits(edi.getDegreeInfo().getProfessionalExits().toLocalizedString());
        bean.setOperationalRegime(edi.getDegreeInfo().getOperationalRegime().toLocalizedString());
        bean.setGratuity(edi.getDegreeInfo().getGratuity().toLocalizedString());
        bean.setAdditionalInfo(edi.getDegreeInfo().getAdditionalInfo().toLocalizedString());
        bean.setLinks(edi.getDegreeInfo().getLinks().toLocalizedString());
        bean.setTestIngression(edi.getDegreeInfo().getTestIngression().toLocalizedString());
        bean.setClassifications(edi.getDegreeInfo().getClassifications().toLocalizedString());
        bean.setAccessRequisites(edi.getDegreeInfo().getAccessRequisites().toLocalizedString());
        bean.setCandidacyDocuments(edi.getDegreeInfo().getCandidacyDocuments().toLocalizedString());
        bean.setDriftsInitial(edi.getDegreeInfo().getDriftsInitial());
        bean.setDriftsFirst(edi.getDegreeInfo().getDriftsFirst());
        bean.setDriftsSecond(edi.getDegreeInfo().getDriftsSecond());
        bean.setMarkMin(edi.getDegreeInfo().getMarkMin());
        bean.setMarkMax(edi.getDegreeInfo().getMarkMax());
        bean.setMarkAverage(edi.getDegreeInfo().getMarkAverage());
        bean.setQualificationLevel(edi.getDegreeInfo().getQualificationLevel().toLocalizedString());
        bean.setRecognitions(edi.getDegreeInfo().getRecognitions().toLocalizedString());

        bean.setScientificAreas(edi.getScientificAreas());
        bean.setStudyRegime(edi.getStudyRegime());
        bean.setStudyProgrammeRequirements(edi.getStudyProgrammeRequirements());
        bean.setHigherEducationAccess(edi.getHigherEducationAccess());
        bean.setProfessionalStatus(edi.getProfessionalStatus());
        bean.setSupplementExtraInformation(edi.getSupplementExtraInformation());
        bean.setSupplementOtherSources(edi.getSupplementOtherSources());
    }

    @Atomic
    private void store(ExtendedDegreeInfoBean bean) {
        ExtendedDegreeInfo edi = ExtendedDegreeInfo.getOrCreate(bean.getExecutionYear(), bean.getDegree());
        DegreeInfo di = edi.getDegreeInfo();

        di.setName(MultiLanguageString.fromLocalizedString(bean.getName()));
        if (!bean.getDescription().isEmpty())
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
        edi.setStudyRegime(bean.getStudyRegime());
        edi.setStudyProgrammeRequirements(bean.getStudyProgrammeRequirements());
        edi.setHigherEducationAccess(bean.getHigherEducationAccess());
        edi.setProfessionalStatus(bean.getProfessionalStatus());
        edi.setSupplementExtraInformation(bean.getSupplementExtraInformation());
        edi.setSupplementOtherSources(bean.getSupplementOtherSources());
    }

}
