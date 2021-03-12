package org.fenixedu.ulisboa.specifications.ui.degrees.extendedinfo;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeInfo;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degree.ExtendedDegreeInfo;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
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

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.extendedDegreeInfo",
        accessGroup = "academic(MANAGE_DEGREE_CURRICULAR_PLANS) | #managers")
@RequestMapping(ExtendedDegreeInformationController.CONTROLLER_URL)
public class ExtendedDegreeInformationController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/ulisboaspecifications/degrees/extendeddegreeinfo";

    @RequestMapping
    public String home(final Model model) {
        return "forward:" + SEARCH_URL;
    }

    private ExtendedDegreeInfoBean getExtendedDegreeInfoBean(final Model model) {
        return (ExtendedDegreeInfoBean) model.asMap().get("extendedDegreeInfoBean");
    }

    private void setExtendedDegreeInfoBean(final ExtendedDegreeInfoBean bean, final Model model) {
        bean.updateLists();
        model.addAttribute("extendedDegreeInfoBeanJson", getBeanJson(bean));
        model.addAttribute("extendedDegreeInfoBean", bean);
    }

    private CreateExtendedDegreeInfoBean getCreateExtendedDegreeInfoBean(final Model model) {
        return (CreateExtendedDegreeInfoBean) model.asMap().get("createExtendedDegreeInfoBean");
    }

    private void setCreateExtendedDegreeInfoBean(final CreateExtendedDegreeInfoBean bean, final Model model) {
        bean.updateLists();
        model.addAttribute("createExtendedDegreeInfoBeanJson", getBeanJson(bean));
        model.addAttribute("createExtendedDegreeInfoBean", bean);
    }

    private static final String _SEARCH_URI = "/search/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI)
    public String search(@RequestParam(value = "degree", required = false) final Degree degree, final Model model) {

        List<Degree> degrees = Degree.readBolonhaDegrees();
        Collections.sort(degrees, Degree.COMPARATOR_BY_NAME_AND_ID);
        model.addAttribute("degreesList", degrees);

        List<DegreeInfo> result = filterSearchDegreeInfo(degree);
        model.addAttribute("searchDegreeInfosSet", result);

        return "fenixedu-ulisboa-specifications/degrees/extendeddegreeinfo/search";
    }

    private List<DegreeInfo> filterSearchDegreeInfo(final Degree degree) {
        if (degree == null) {
            return Collections.emptyList();
        }
        return degree.getDegreeInfosSet().stream().collect(Collectors.toList());
    }

    private static final String _CREATE_URI = "/create/";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.GET)
    public String createAcademicRequest(final Model model) {
        if (getCreateExtendedDegreeInfoBean(model) == null) {
            setCreateExtendedDegreeInfoBean(new CreateExtendedDegreeInfoBean(), model);
        }
        return "fenixedu-ulisboa-specifications/degrees/extendeddegreeinfo/create";
    }

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
    public String createAcademicRequest(@RequestParam(value = "bean", required = true) final CreateExtendedDegreeInfoBean bean,
            final Model model, final RedirectAttributes redirectAttributes) {
        setCreateExtendedDegreeInfoBean(bean, model);

        try {
            ExtendedDegreeInfo extendedDegreeInfo = createExtendedDegreeInfo(bean);
            return redirect(READ_URL + extendedDegreeInfo.getDegreeInfo().getExternalId(), model, redirectAttributes);
        } catch (DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        } catch (org.fenixedu.bennu.core.domain.exceptions.DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                if (e.getCause() != null) {
                    Throwable cause = e.getCause();
                    if (cause instanceof DomainException
                            || cause instanceof org.fenixedu.bennu.core.domain.exceptions.DomainException) {
                        String message =
                                BundleUtil.getString(ULisboaConstants.BUNDLE, "error.serviceRequests.ulisboarequest.create");
                        addErrorMessage(message + " - " + cause.getLocalizedMessage(), model);
                    }
                } else {
                    addErrorMessage(e.getMessage(), model);
                }
            } else {
                addErrorMessage("Unknow exception: " + e.getClass().getSimpleName() + " - " + e.getLocalizedMessage(), model);
            }
        }
        return "fenixedu-ulisboa-specifications/degrees/extendeddegreeinfo/create";
    }

    @Atomic
    private ExtendedDegreeInfo createExtendedDegreeInfo(final CreateExtendedDegreeInfoBean bean) {
        if (bean != null) {
            Degree degree = bean.getDegree();
            ExecutionYear executionInterval = bean.getExecutionInterval();

            if (degree == null) {
                throw new RuntimeException(BundleUtil.getString(ULisboaConstants.BUNDLE, "message.DegreeInfo.selectOneDegree"));
            }
            if (executionInterval == null) {
                throw new RuntimeException(
                        BundleUtil.getString(ULisboaConstants.BUNDLE, "message.DegreeInfo.selectOneExecutionYear"));
            }

            if (degree.getDegreeInfoFor(executionInterval) != null) {
                throw new RuntimeException(BundleUtil.getString(ULisboaConstants.BUNDLE, "message.DegreeInfo.alreadyExists"));
            }

            //ExtendedDegreeInfo create also DegreeInfo if it does not exists
            ExtendedDegreeInfo extendedDegreeInfo = ExtendedDegreeInfo.getOrCreate(executionInterval, degree);

            return extendedDegreeInfo;
        }
        return null;
    }

    private static final String _CREATE_POSTBACK_URI = "/createpostback/";
    public static final String CREATE_POSTBACK_URL = CONTROLLER_URL + _CREATE_POSTBACK_URI;

    @RequestMapping(value = _CREATE_POSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String createpostback(
            @RequestParam(value = "bean", required = true) final CreateExtendedDegreeInfoBean bean, final Model model) {
        setCreateExtendedDegreeInfoBean(bean, model);
        return getBeanJson(bean);
    }

    private static final String DELETE_URI = "/delete/";
    public static final String DELETE_URL = CONTROLLER_URL + DELETE_URI;

    @RequestMapping(value = DELETE_URI + "{degreeInfoId}", method = RequestMethod.POST)
    public String processSearchToDeleteAction(@PathVariable("degreeInfoId") final DegreeInfo degreeInfo, final Model model,
            final RedirectAttributes redirectAttributes) {
        try {
            deleteDegreeInfo(degreeInfo);

            addInfoMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, "message.DegreeInfo.removed.with.success"), model);
            return redirect(SEARCH_URL, model, redirectAttributes);
        } catch (DomainException ex) {
            addErrorMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, ex.getKey()), model);
        }

        return search(null, model);
    }

    @Atomic
    private void deleteDegreeInfo(DegreeInfo degreeInfo) {
        if (degreeInfo != null) {
            degreeInfo.delete();
        }
    }

    private static final String _READ_URI = "/read/";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI + "{oid}", method = RequestMethod.GET)
    public String read(@PathVariable(value = "oid") final DegreeInfo degreeInfo, final Model model,
            final RedirectAttributes redirectAttributes, final HttpServletRequest request) {
        if (degreeInfo == null) {
            addErrorMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, "message.DegreeInfo.error"), model);
            return redirect(SEARCH_URL, model, redirectAttributes);
        }

        model.addAttribute("degreeInfo", degreeInfo);
        model.addAttribute("extendedDegreeInfo", degreeInfo.getExtendedDegreeInfo());

        return "fenixedu-ulisboa-specifications/degrees/extendeddegreeinfo/read";
    }

    private static final String _UPDATE_URI = "/update/";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI + "{oid}", method = RequestMethod.GET)
    public String updateAcademicRequest(@PathVariable(value = "oid") final DegreeInfo degreeInfo, final Model model) {
        if (getExtendedDegreeInfoBean(model) == null) {
            ExtendedDegreeInfoBean bean = new ExtendedDegreeInfoBean(degreeInfo);
            setExtendedDegreeInfoBean(bean, model);
        }

        model.addAttribute("degreeInfo", degreeInfo);
        return "fenixedu-ulisboa-specifications/degrees/extendeddegreeinfo/update";
    }

    @RequestMapping(value = _UPDATE_URI + "{oid}", method = RequestMethod.POST)
    public String updateAcademicRequest(@PathVariable(value = "oid") final DegreeInfo degreeInfo,
            @RequestParam(value = "bean", required = true) final ExtendedDegreeInfoBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {
        setExtendedDegreeInfoBean(bean, model);
        model.addAttribute("degreeInfo", degreeInfo);

        try {
            updateDegreeInfo(degreeInfo, bean);
            return redirect(READ_URL + degreeInfo.getExternalId(), model, redirectAttributes);
        } catch (DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        } catch (org.fenixedu.bennu.core.domain.exceptions.DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        } catch (Exception e) {
            if (e instanceof RuntimeException && e.getCause() != null) {
                Throwable cause = e.getCause();
                if (cause instanceof DomainException
                        || cause instanceof org.fenixedu.bennu.core.domain.exceptions.DomainException) {
                    String message = BundleUtil.getString(ULisboaConstants.BUNDLE, "error.serviceRequests.ulisboarequest.update");
                    addErrorMessage(message + " - " + cause.getLocalizedMessage(), model);
                }
            } else {
                addErrorMessage("Unknow exception: " + e.getClass().getSimpleName() + " - " + e.getLocalizedMessage(), model);
            }

        }
        return "fenixedu-ulisboa-specifications/degrees/extendeddegreeinfo/update";
    }

    @Atomic
    public void updateDegreeInfo(DegreeInfo degreeInfo, ExtendedDegreeInfoBean bean) {
        final ExtendedDegreeInfo extendedDegreeInfo = degreeInfo.getExtendedDegreeInfo();

        degreeInfo.setName(bean.getName());
        degreeInfo.setDescription(bean.getDescription());
        degreeInfo.setHistory(bean.getHistory());
        degreeInfo.setObjectives(bean.getObjectives());
        degreeInfo.setDesignedFor(bean.getDesignedFor());
        degreeInfo.setProfessionalExits(bean.getProfessionalExits());
        degreeInfo.setOperationalRegime(bean.getOperationalRegime());
        degreeInfo.setGratuity(bean.getGratuity());
        degreeInfo.setAdditionalInfo(bean.getAdditionalInfo());
        degreeInfo.setLinks(bean.getLinks());
        degreeInfo.setTestIngression(bean.getTestIngression());
        degreeInfo.setClassifications(bean.getClassifications());
        degreeInfo.setAccessRequisites(bean.getAccessRequisites());
        degreeInfo.setCandidacyDocuments(bean.getCandidacyDocuments());
        degreeInfo.setDriftsInitial(bean.getDriftsInitial());
        degreeInfo.setDriftsFirst(bean.getDriftsFirst());
        degreeInfo.setDriftsSecond(bean.getDriftsSecond());
        degreeInfo.setMarkMin(bean.getMarkMin());
        degreeInfo.setMarkMax(bean.getMarkMax());
        degreeInfo.setMarkAverage(bean.getMarkAverage());
        degreeInfo.setQualificationLevel(bean.getQualificationLevel());
        degreeInfo.setRecognitions(bean.getRecognitions());
        degreeInfo.setPrevailingScientificArea(bean.getPrevailingScientificArea());

        extendedDegreeInfo.setScientificAreas(bean.getScientificAreas());
        extendedDegreeInfo.setStudyProgrammeDuration(bean.getStudyProgrammeDuration());
        extendedDegreeInfo.setStudyRegime(bean.getStudyRegime());
        extendedDegreeInfo.setStudyProgrammeRequirements(bean.getStudyProgrammeRequirements());
        extendedDegreeInfo.setHigherEducationAccess(bean.getHigherEducationAccess());
        extendedDegreeInfo.setProfessionalStatus(bean.getProfessionalStatus());
        extendedDegreeInfo.setSupplementExtraInformation(bean.getSupplementExtraInformation());
        extendedDegreeInfo.setSupplementOtherSources(bean.getSupplementOtherSources());
    }
}
