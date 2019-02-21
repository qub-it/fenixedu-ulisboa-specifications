package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.importation;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.EntryPhase;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.bennu.FenixeduQubdocsReportsSpringConfiguration;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.spaces.domain.Space;
import org.fenixedu.ulisboa.specifications.domain.CgdMod43Template;
import org.fenixedu.ulisboa.specifications.domain.ContingentToIngression;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationConfiguration;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationGlobalConfiguration;
import org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.student.access.importation.DgesStudentImportationFile;
import org.fenixedu.ulisboa.specifications.domain.student.access.importation.DgesStudentImportationProcess;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "link.dgesStudentImportationProcess",
        accessGroup = "(academic(MANAGE_CANDIDACY_PROCESSES) | academic(MANAGE_INDIVIDUAL_CANDIDACIES))")
@RequestMapping(DgesImportationProcessController.CONTROLLER_URL)
public class DgesImportationProcessController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/ulisboaspecifications/firsttimecandidacy/dgesimportationprocess";

    public static final String VIEW_URL = "fenixedu-ulisboa-specifications/firsttimecandidacy/dgesimportationprocess";
    private static final String ERROR_MESSAGE_ATTRIBUTE = "sessionErrorMessages";

    private DgesImportProcessBean getDgesBaseProcessBean(final Model model) {
        return (DgesImportProcessBean) model.asMap().get("dgesBaseProcessBean");
    }

    private void setDgesBaseProcessBean(final DgesImportProcessBean bean, final Model model) {
        if (bean != null) {
            bean.updateLists();
        }
        model.addAttribute("dgesBaseProcessBeanJson", getBeanJson(bean));
        model.addAttribute("dgesBaseProcessBean", bean);
    }

    private DgesImportProcessConfigurationBean getDgesImportProcessConfigurationBean(final Model model) {
        return (DgesImportProcessConfigurationBean) model.asMap().get("dgesImportProcessConfigurationBean");
    }

    private void setDgesImportProcessConfigurationBean(final DgesImportProcessConfigurationBean bean, final Model model) {
        if (bean != null) {
            bean.updateLists();
        }
        model.addAttribute("dgesImportProcessConfigurationBeanJson", getBeanJson(bean));
        model.addAttribute("dgesImportProcessConfigurationBean", bean);
    }

    @RequestMapping
    public String home(final Model model) {
        return "forward:" + SEARCH_URL;
    }

    private static final String _SEARCH_URI = "/search/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI)
    public String search(@RequestParam(value = "executionYear", required = false) ExecutionYear executionYear,
            @ModelAttribute("selectedExecutionYearId") final String flashExecutionYearId, final Model model,
            final RedirectAttributes redirectAttributes, final HttpServletRequest request) {
        if (request.getSession().getAttribute(ERROR_MESSAGE_ATTRIBUTE) != null) {
            List<String> errorMessages = (List<String>) request.getSession().getAttribute(ERROR_MESSAGE_ATTRIBUTE);
            for (String errorMessage : errorMessages) {
                addErrorMessage(errorMessage, model);
            }
            request.getSession().removeAttribute(ERROR_MESSAGE_ATTRIBUTE);
        }

        if (flashExecutionYearId != null && !flashExecutionYearId.isEmpty()) {
            executionYear = (ExecutionYear) FenixFramework.getDomainObject(flashExecutionYearId);
        }
        if (executionYear == null) {
            executionYear = ExecutionYear.readCurrentExecutionYear();
        }

        //Creating a bean to provide a sorted execution year set
        if (getDgesBaseProcessBean(model) == null) {
            setDgesBaseProcessBean(new DgesImportProcessBean(executionYear), model);
        }

        List<DgesStudentImportationProcess> importationJobsDone = DgesStudentImportationProcess.readDoneJobs(executionYear)
                .stream().sorted(DgesStudentImportationProcess.COMPARATOR_BY_BEGIN_DATE.reversed()).collect(Collectors.toList());
        List<DgesStudentImportationProcess> importationJobsPending = DgesStudentImportationProcess.readUndoneJobs(executionYear)
                .stream().sorted(DgesStudentImportationProcess.COMPARATOR_BY_BEGIN_DATE.reversed()).collect(Collectors.toList());

        model.addAttribute("importationJobsDone", importationJobsDone);
        model.addAttribute("importationJobsPending", importationJobsPending);

        return VIEW_URL + "/search";
    }

    private static final String _CREATE_URI = "/create";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.GET)
    public String create(final Model model) {
        if (getDgesBaseProcessBean(model) == null) {
            setDgesBaseProcessBean(new DgesImportProcessBean(ExecutionYear.readCurrentExecutionYear()), model);
        }

        return VIEW_URL + "/create";
    }

    @RequestMapping(value = _CREATE_URI, method = RequestMethod.POST)
    public String create(@RequestParam(value = "bean", required = true) final DgesImportProcessBean bean,
            @RequestParam(value = "importationFile", required = false) final MultipartFile importationFile, final Model model,
            final RedirectAttributes redirectAttributes) {
        setDgesBaseProcessBean(bean, model);

        try {
            createDgesImportationProcess(bean, importationFile);

            if (bean.getExecutionYear() != null) {
                redirectAttributes.addFlashAttribute("selectedExecutionYearId", bean.getExecutionYear().getExternalId());
            }
            addInfoMessage(BundleUtil.getString(ULisboaConstants.BUNDLE,
                    "info.DgesStudentImportationProcess.create.new.job.restriction"), model);

            return redirect(SEARCH_URL, model, redirectAttributes);
        } catch (DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        } catch (IOException e) {
            addErrorMessage(BundleUtil.getString(FenixeduQubdocsReportsSpringConfiguration.BUNDLE, "label.error.create") + "-"
                    + e.toString() + "-" + e.getLocalizedMessage(), model);
        }
        return VIEW_URL + "/create";
    }

    private void createDgesImportationProcess(final DgesImportProcessBean bean, final MultipartFile importationFile)
            throws IOException {
        if (importationFile.isEmpty()) {
            throw new ULisboaSpecificationsDomainException("error.DgesStudentImportationProcess.importationfile.null");
        }
        if (bean.getExecutionYear() == null) {
            throw new ULisboaSpecificationsDomainException("error.DgesStudentImportationProcess.executionYear.null");
        }
        if (bean.getSpace() == null) {
            throw new ULisboaSpecificationsDomainException("error.DgesStudentImportationProcess.space.null");
        }
        if (bean.getPhase() == null) {
            throw new ULisboaSpecificationsDomainException("error.DgesStudentImportationProcess.phase.null");
        }
        if (!DgesStudentImportationProcess.canRequestJob()) {
            throw new ULisboaSpecificationsDomainException("error.DgesStudentImportationProcess.cant.create.new.job");
        }

        DgesStudentImportationFile file = DgesStudentImportationFile.create(importationFile.getBytes(),
                importationFile.getOriginalFilename(), bean.getExecutionYear(), bean.getSpace(), bean.getPhase());
        launchImportation(bean.getExecutionYear(), bean.getSpace(), bean.getPhase(), file);

    }

    @Atomic
    private DgesStudentImportationProcess launchImportation(final ExecutionYear executionYear, final Space space,
            final EntryPhase phase, final DgesStudentImportationFile file) {
        return new DgesStudentImportationProcess(executionYear, space, phase, file);
    }

    private static final String _CREATE_POSTBACK_URI = "/createpostback/";
    public static final String CREATE_POSTBACK_URL = CONTROLLER_URL + _CREATE_POSTBACK_URI;

    @RequestMapping(value = _CREATE_POSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String createpostback(@RequestParam(value = "bean", required = true) final DgesImportProcessBean bean,
            final Model model) {
        setDgesBaseProcessBean(bean, model);
        return getBeanJson(bean);
    }

    private static final String _CANCEL_URI = "/cancel";
    public static final String CANCEL_URL = CONTROLLER_URL + _CANCEL_URI;

    @RequestMapping(value = _CANCEL_URI + "/{oid}", method = RequestMethod.POST)
    public String cancel(@PathVariable("oid") final DgesStudentImportationProcess process, final Model model,
            final RedirectAttributes redirectAttributes) {
        ExecutionYear executionYear = process.getExecutionYear();
        try {
            cancelImportationProcessJob(process);

            addInfoMessage(
                    BundleUtil.getString(ULisboaConstants.BUNDLE, "message.DgesStudentImportationProcess.cancelled.with.success"),
                    model);
        } catch (DomainException ex) {
            addErrorMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, ex.getKey()), model);
        }

        if (executionYear != null) {
            redirectAttributes.addFlashAttribute("selectedExecutionYearId", executionYear.getExternalId());
        }
        return redirect(SEARCH_URL, model, redirectAttributes);
    }

    @Atomic
    private void cancelImportationProcessJob(final DgesStudentImportationProcess process) {
        if (process.getDone()) {
            throw new ULisboaSpecificationsDomainException("error.DgesStudentImportationProcess.cancelled.done.job",
                    process.getDescription());
        }

        process.cancel();
    }

    private static final String _RESEND_URI = "/resend";
    public static final String RESEND_URL = CONTROLLER_URL + _RESEND_URI;

    @RequestMapping(value = _RESEND_URI + "/{oid}", method = RequestMethod.POST)
    public String resend(@PathVariable("oid") final DgesStudentImportationProcess process, final Model model,
            final RedirectAttributes redirectAttributes) {
        ExecutionYear executionYear = process.getExecutionYear();
        try {
            resendImportationProcessJob(process);

            addInfoMessage(
                    BundleUtil.getString(ULisboaConstants.BUNDLE, "message.DgesStudentImportationProcess.resended.with.success"),
                    model);
        } catch (DomainException ex) {
            addErrorMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, ex.getKey()), model);
        }

        if (executionYear != null) {
            redirectAttributes.addFlashAttribute("selectedExecutionYearId", executionYear.getExternalId());
        }
        return redirect(SEARCH_URL, model, redirectAttributes);
    }

    @Atomic
    private void resendImportationProcessJob(final DgesStudentImportationProcess process) {
        if (process.getDone()) {
            throw new ULisboaSpecificationsDomainException("error.DgesStudentImportationProcess.resend.done.job",
                    process.getDescription());
        }

        if (process.getIsNotDoneAndNotCancelled()) {
            throw new ULisboaSpecificationsDomainException("error.DgesStudentImportationProcess.resend.not.cancelled.job",
                    process.getDescription());
        }

        process.resend();
    }

    private static final String _DOWNLOAD_LOG_URI = "/download/log";
    public static final String DOWNLOAD_LOG_URL = CONTROLLER_URL + _DOWNLOAD_LOG_URI;

    @RequestMapping(value = _DOWNLOAD_LOG_URI + "/{oid}", method = RequestMethod.GET)
    public void processSearchToDownloadLogAction(@PathVariable("oid") final DgesStudentImportationProcess process,
            final Model model, final HttpServletRequest request, final HttpServletResponse response) {
        try {
            if (process.getDone() != null && process.getDone()) {
                response.setContentType(process.getContentType());
                String filename = URLEncoder.encode(StringNormalizer
                        .normalizePreservingCapitalizedLetters(process.getFile().getDisplayName()).replaceAll("\\s", "_"),
                        "UTF-8");
                response.setHeader("Content-disposition", "attachment; filename=" + filename);
                response.getOutputStream().write(process.getFile().getContent());
            } else {
                addErrorMessage(BundleUtil.getString(ULisboaConstants.BUNDLE, "error.job.not.done"), model);
                request.getSession().setAttribute(ERROR_MESSAGE_ATTRIBUTE, model.asMap().get(ERROR_MESSAGES));
                response.sendRedirect(request.getContextPath() + SEARCH_URL);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String _DOWNLOAD_URI = "/download";
    public static final String DOWNLOAD_URL = CONTROLLER_URL + _DOWNLOAD_URI;

    @RequestMapping(value = _DOWNLOAD_URI + "/{oid}", method = RequestMethod.GET)
    public void processSearchToDownloadAction(@PathVariable("oid") final DgesStudentImportationProcess process,
            final HttpServletResponse response) {
        try {

            response.setContentType(process.getContentType());
            String filename = URLEncoder.encode(StringNormalizer
                    .normalizePreservingCapitalizedLetters(process.getDgesStudentImportationFile().getDisplayName())
                    .replaceAll("\\s", "_"), "UTF-8");
            response.setHeader("Content-disposition", "attachment; filename=" + filename);
            response.getOutputStream().write(process.getDgesStudentImportationFile().getContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String _DOWNLOAD_CGD_URI = "/download/cgd";
    public static final String DOWNLOAD_CGD_URL = CONTROLLER_URL + _DOWNLOAD_CGD_URI;

    @RequestMapping(value = _DOWNLOAD_CGD_URI + "/{oid}", method = RequestMethod.GET)
    public void downloadCGDTemplate(@PathVariable("oid") final CgdMod43Template template, final HttpServletResponse response) {
        try {

            response.setContentType(template.getContentType());
            String filename = URLEncoder.encode(
                    StringNormalizer.normalizePreservingCapitalizedLetters(template.getDisplayName()).replaceAll("\\s", "_"),
                    "UTF-8");
            response.setHeader("Content-disposition", "attachment; filename=" + filename);
            response.getOutputStream().write(template.getContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String _CONFIGURATION_READ_URI = "/configuration/read";
    public static final String CONFIGURATION_READ_URL = CONTROLLER_URL + _CONFIGURATION_READ_URI;

    @RequestMapping(value = _CONFIGURATION_READ_URI, method = RequestMethod.GET)
    public String readConfiguration(final Model model) {
        ULisboaSpecificationsRoot specificationsRoot = ULisboaSpecificationsRoot.getInstance();

        final Predicate<Degree> isDegreeActive =
                d -> d.getExecutionDegrees().stream().anyMatch(ed -> ed.getExecutionYear().isCurrent());

        FirstYearRegistrationGlobalConfiguration globalConfiguration = FirstYearRegistrationGlobalConfiguration.getInstance();
        List<FirstYearRegistrationConfiguration> activeDegreesMappings =
                globalConfiguration.getFirstYearRegistrationConfigurationsSet().stream()
                        .filter(c -> isDegreeActive.test(c.getDegree())).collect(Collectors.toList());

        model.addAttribute("defaultRegistrationProtocol", specificationsRoot.getDefaultRegistrationProtocol());
        model.addAttribute("contingentMappings", specificationsRoot.getContingentToIngressionsSet());
        model.addAttribute("globalConfiguration", globalConfiguration);
        model.addAttribute("activeDegreesMappings", activeDegreesMappings);

        return VIEW_URL + "/readConfiguration";
    }

    private static final String _CONFIGURATION_UPDATE_URI = "/configuration/update";
    public static final String CONFIGURATION_UPDATE_URL = CONTROLLER_URL + _CONFIGURATION_UPDATE_URI;

    @RequestMapping(value = _CONFIGURATION_UPDATE_URI, method = RequestMethod.GET)
    public String updateConfiguration(final Model model) {
        if (getDgesImportProcessConfigurationBean(model) == null) {
            setDgesImportProcessConfigurationBean(new DgesImportProcessConfigurationBean(), model);
        }

        return VIEW_URL + "/updateConfiguration";
    }

    @RequestMapping(value = _CONFIGURATION_UPDATE_URI, method = RequestMethod.POST)
    public String updateConfiguration(
            @RequestParam(value = "bean", required = true) final DgesImportProcessConfigurationBean bean,
            @RequestParam(value = "cgdTemplateFile", required = true) final MultipartFile cgdTemplateFile, final Model model,
            final RedirectAttributes redirectAttributes) {

        try {
            updateConfiguration(bean, cgdTemplateFile);
            setDgesImportProcessConfigurationBean(bean, model);

            return redirect(CONFIGURATION_UPDATE_URL, model, redirectAttributes);
        } catch (DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        } catch (IOException e) {
            addErrorMessage(
                    BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "label.error.uploadMod43")
                            + e.getLocalizedMessage(),
                    model);
        }
        return VIEW_URL + "/updateConfiguration";
    }

    @Atomic
    public void updateConfiguration(final DgesImportProcessConfigurationBean bean, final MultipartFile cgdTemplateFile)
            throws IOException {
        ULisboaSpecificationsRoot specificationsRoot = ULisboaSpecificationsRoot.getInstance();
        FirstYearRegistrationGlobalConfiguration globalConfiguration = FirstYearRegistrationGlobalConfiguration.getInstance();

        specificationsRoot.setDefaultRegistrationProtocol(bean.getDefaultRegistrationProtocol());
        globalConfiguration.setIntroductionText(bean.getIntroductionText());
        if (!cgdTemplateFile.isEmpty()) {
            String fileName = cgdTemplateFile.getOriginalFilename();
            byte[] fileContent = cgdTemplateFile.getBytes();
            globalConfiguration.uploadMod43Template(fileName, fileContent);
        }

    }

    private static final String _MANAGE_CONTINGENT_MAPPING_URI = "/configuration/manage/contingent";
    public static final String MANAGE_CONTINGENT_MAPPING_URL = CONTROLLER_URL + _MANAGE_CONTINGENT_MAPPING_URI;

    @RequestMapping(value = _MANAGE_CONTINGENT_MAPPING_URI, method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody String manageContingentMapping(
            @RequestParam(value = "bean", required = true) final DgesImportProcessConfigurationBean bean,
            @RequestParam(value = "contingent", required = false) final String contingent,
            @RequestParam(value = "ingressionType", required = false) final IngressionType ingressionType,
            @RequestParam(value = "contingentMappingId", required = false) final ContingentToIngression contingentMapping,
            final Model model) {

        manageContingentMappings(contingent, ingressionType, contingentMapping);

        setDgesImportProcessConfigurationBean(bean, model);
        return getBeanJson(bean);
    }

    @Atomic
    private void manageContingentMappings(final String contingent, final IngressionType ingressionType,
            final ContingentToIngression contingentMapping) {
        //This is a call to delete
        if (contingentMapping != null) {
            contingentMapping.delete();
            return;
        }
        //This is a call to create
        if (contingent != null && ingressionType != null) {
            ULisboaSpecificationsRoot.getInstance().setIngressionType(contingent, ingressionType);
        }
    }

    private static final String _MANAGE_DEGREE_CONFIGURATION_URI = "/configuration/manage/degree";
    public static final String MANAGE_DEGREE_CONFIGURATION_URL = CONTROLLER_URL + _MANAGE_DEGREE_CONFIGURATION_URI;

    @RequestMapping(value = _MANAGE_DEGREE_CONFIGURATION_URI, method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody String manageDegreeMapping(
            @RequestParam(value = "bean", required = true) final DgesImportProcessConfigurationBean bean,
            @RequestParam(value = "degree", required = true) final Degree degree,
            @RequestParam(value = "requiresVaccination", required = false) final boolean requiresVaccination,
            @RequestParam(value = "degreeCurricularPlan", required = false) final DegreeCurricularPlan degreeCurricularPlan,
            final Model model) {
        createDegreeConfiguration(degree, degreeCurricularPlan, requiresVaccination);

        setDgesImportProcessConfigurationBean(bean, model);
        return getBeanJson(bean);
    }

    @Atomic
    private void createDegreeConfiguration(final Degree degree, final DegreeCurricularPlan degreeCurricularPlan,
            final boolean requiresVaccination) {
        if (FirstYearRegistrationConfiguration.getDegreeConfiguration(degree) != null) {
            FirstYearRegistrationConfiguration.getDegreeConfiguration(degree).edit(degreeCurricularPlan, requiresVaccination);
        } else {
            new FirstYearRegistrationConfiguration(degree, degreeCurricularPlan, requiresVaccination);
        }
    }

    private static final String _DELETE_DEGREE_CONFIGURATION_URI = "/configuration/delete/degree";
    public static final String DELETE_DEGREE_CONFIGURATION_URL = CONTROLLER_URL + _DELETE_DEGREE_CONFIGURATION_URI;

    @RequestMapping(value = _DELETE_DEGREE_CONFIGURATION_URI, method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody String deleteDegreeConfiguration(
            @RequestParam(value = "bean", required = true) final DgesImportProcessConfigurationBean bean,
            @RequestParam(value = "configuration", required = true) final FirstYearRegistrationConfiguration configuration,
            final Model model) {

        deleteDegreeConfiguration(configuration);

        setDgesImportProcessConfigurationBean(bean, model);
        return getBeanJson(bean);
    }

    @Atomic
    private void deleteDegreeConfiguration(final FirstYearRegistrationConfiguration configuration) {
        configuration.delete();
    }

}
