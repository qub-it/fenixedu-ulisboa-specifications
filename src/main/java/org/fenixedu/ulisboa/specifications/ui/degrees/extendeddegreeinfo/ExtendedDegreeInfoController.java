package org.fenixedu.ulisboa.specifications.ui.degrees.extendeddegreeinfo;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeInfo;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.academic.domain.degree.ExtendedDegreeInfo;
import org.fenixedu.ulisboa.specifications.domain.services.AuditingServices;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Strings;

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
        final Degree degree = bean.getDegree();
        final ExtendedDegreeInfo edi = ExtendedDegreeInfo.getMostRecent(bean.getExecutionYear(), degree);
        final DegreeInfo di = edi.getDegreeInfo();

        String degreeSiteUrl = degree.getSiteUrl();
        if (Strings.isNullOrEmpty(degreeSiteUrl)) {
            bean.setDegreeSitePublicUrl(null);
            bean.setDegreeSiteManagementUrl(null);
        } else {
            degreeSiteUrl = degreeSiteUrl.substring(degreeSiteUrl.lastIndexOf("/") + 1);
            bean.setDegreeSitePublicUrl("degrees/" + degreeSiteUrl);
            bean.setDegreeSiteManagementUrl("cms/sites/" + degreeSiteUrl);
        }
        bean.setAuditInfo(
                edi.getDegreeInfo().getExecutionYear() == bean.getExecutionYear() ? AuditingServices.getAuditInfo(edi) : null);

        bean.setName(di.getName() != null ? di.getName() : null);
        bean.setDescription(di.getDescription() != null ? edi.getDegreeInfo().getDescription() : null);
        bean.setHistory(di.getHistory() != null ? di.getHistory() : null);
        bean.setObjectives(di.getObjectives() != null ? di.getObjectives() : null);
        bean.setDesignedFor(di.getDesignedFor() != null ? di.getDesignedFor() : null);
        bean.setProfessionalExits(di.getProfessionalExits() != null ? di.getProfessionalExits() : null);
        bean.setOperationalRegime(di.getOperationalRegime() != null ? di.getOperationalRegime() : null);
        bean.setGratuity(di.getGratuity() != null ? di.getGratuity() : null);
        bean.setAdditionalInfo(di.getAdditionalInfo() != null ? di.getAdditionalInfo() : null);
        bean.setLinks(di.getLinks() != null ? di.getLinks() : null);
        bean.setTestIngression(di.getTestIngression() != null ? di.getTestIngression() : null);
        bean.setClassifications(di.getClassifications() != null ? di.getClassifications() : null);
        bean.setAccessRequisites(di.getAccessRequisites() != null ? di.getAccessRequisites() : null);
        bean.setCandidacyDocuments(di.getCandidacyDocuments() != null ? di.getCandidacyDocuments() : null);
        bean.setDriftsInitial(di.getDriftsInitial() != null ? di.getDriftsInitial() : null);
        bean.setDriftsFirst(di.getDriftsFirst() != null ? di.getDriftsFirst() : null);
        bean.setDriftsSecond(di.getDriftsSecond() != null ? di.getDriftsSecond() : null);
        bean.setMarkMin(di.getMarkMin() != null ? di.getMarkMin() : null);
        bean.setMarkMax(di.getMarkMax() != null ? di.getMarkMax() : null);
        bean.setMarkAverage(di.getMarkAverage() != null ? di.getMarkAverage() : null);
        bean.setQualificationLevel(di.getQualificationLevel() != null ? di.getQualificationLevel() : null);
        bean.setRecognitions(di.getRecognitions() != null ? di.getRecognitions() : null);
        bean.setPrevailingScientificArea(di.getPrevailingScientificArea());

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
        final ExtendedDegreeInfo edi = ExtendedDegreeInfo.getOrCreate(bean.getExecutionYear(), bean.getDegree());
        final DegreeInfo di = edi.getDegreeInfo();

        di.setName(bean.getName());
        di.setDescription(bean.getDescription());
        di.setHistory(bean.getHistory());
        di.setObjectives(bean.getObjectives());
        di.setDesignedFor(bean.getDesignedFor());
        di.setProfessionalExits(bean.getProfessionalExits());
        di.setOperationalRegime(bean.getOperationalRegime());
        di.setGratuity(bean.getGratuity());
        di.setAdditionalInfo(bean.getAdditionalInfo());
        di.setLinks(bean.getLinks());
        di.setTestIngression(bean.getTestIngression());
        di.setClassifications(bean.getClassifications());
        di.setAccessRequisites(bean.getAccessRequisites());
        di.setCandidacyDocuments(bean.getCandidacyDocuments());
        di.setDriftsInitial(bean.getDriftsInitial());
        di.setDriftsFirst(bean.getDriftsFirst());
        di.setDriftsSecond(bean.getDriftsSecond());
        di.setMarkMin(bean.getMarkMin());
        di.setMarkMax(bean.getMarkMax());
        di.setMarkAverage(bean.getMarkAverage());
        di.setQualificationLevel(bean.getQualificationLevel());
        di.setRecognitions(bean.getRecognitions());
        di.setPrevailingScientificArea(bean.getPrevailingScientificArea());

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
