package org.fenixedu.ulisboa.specifications.ui.ectsgradingtable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.ects.CourseGradingTable;
import org.fenixedu.ulisboa.specifications.domain.ects.DegreeGradingTable;
import org.fenixedu.ulisboa.specifications.domain.ects.GradingTable;
import org.fenixedu.ulisboa.specifications.domain.ects.GradingTableSettings;
import org.fenixedu.ulisboa.specifications.domain.ects.InstitutionGradingTable;
import org.fenixedu.ulisboa.specifications.domain.ects.GradingTableData.GradeConversion;
import org.fenixedu.ulisboa.specifications.servlet.FenixeduUlisboaSpecificationsInitializer;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.manageECTSGradingTables",
        accessGroup = "academic(MANAGE_CONCLUSION)| #managers")
@RequestMapping(EctsGradingTableBackofficeController.CONTROLLER_URL)
@SessionAttributes("sectoken")
public class EctsGradingTableBackofficeController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/ulisboaspecifications/ectsgradingtable";

    public static final String VIEW_URL = "fenixedu-ulisboa-specifications/managegradingtable/ectsgradingtable/";

    private static final Map<Long, Thread> workers = new HashMap<Long, Thread>();

    @RequestMapping
    public String home(Model model) {
        return "forward:" + SEARCH_URL;
    }

    private void loadModel(Model model) {
        List<ExecutionYear> years = new ArrayList<ExecutionYear>(ExecutionYear.readNotClosedExecutionYears());
        Collections.sort(years, ExecutionYear.REVERSE_COMPARATOR_BY_YEAR);
        model.addAttribute("executionYearsList", years);
    }

    private void loadModel(final Model model, final ExecutionYear executionYear) {
        loadModel(model);
        model.addAttribute("gradingTableSettings", GradingTableSettings.getInstance());
        model.addAttribute("selectedYear", executionYear);
        model.addAttribute("institutionGradeTable", InstitutionGradingTable.find(executionYear));
        model.addAttribute("degreeGradeTableHeaders", calculateHeaders(DegreeGradingTable.find(executionYear)));
        model.addAttribute("degreeGradeTable", DegreeGradingTable.find(executionYear));
        model.addAttribute("courseGradeTableHeaders", calculateHeaders(CourseGradingTable.find(executionYear)));
        model.addAttribute("courseGradeTable", CourseGradingTable.find(executionYear));
        model.addAttribute("sectoken", SecToken.generate());
    }

    private synchronized boolean hasFinished(long workerId) {
        if (workers.get(workerId) != null) {
            if (workers.get(workerId).isAlive()) {
                return false;
            } else {
                workers.remove(workerId);
                return true;
            }
        }
        return false;
    }

    private static final String _SEARCH_URI = "/search/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI)
    public String search(@RequestParam(value = "executionYear", required = false) ExecutionYear executionYear, Model model) {
        if (executionYear == null) {
            executionYear = ExecutionYear.readCurrentExecutionYear();
        }
        loadModel(model, executionYear);
        return VIEW_URL + "search";
    }

    @RequestMapping(value = _SEARCH_URI + "{oid}", method = RequestMethod.GET)
    public String backToSearch(@PathVariable(value = "oid") ExecutionYear executionYear, Model model) {
        if (executionYear == null) {
            executionYear = ExecutionYear.readCurrentExecutionYear();
        }
        loadModel(model, executionYear);
        return VIEW_URL + "search";
    }

    private static final String _UPDATE_SETTINGS_URI = "/settings/update/";
    public static final String UPDATE_SETTINGS_URL = CONTROLLER_URL + _UPDATE_SETTINGS_URI;

    @RequestMapping(value = _UPDATE_SETTINGS_URI + "{oid}", method = RequestMethod.GET)
    public String updateSettings(@PathVariable(value = "oid") ExecutionYear executionYear, Model model) {
        loadModel(model, executionYear);
        model.addAttribute("degreeTypeOptions", DegreeType.all().collect(Collectors.toList()));
        return VIEW_URL + "updateSettings";
    }

    @RequestMapping(value = _UPDATE_SETTINGS_URI + "{oid}", method = RequestMethod.POST)
    public String updateSettings(@PathVariable(value = "oid") ExecutionYear executionYear, @RequestParam(value = "minSampleSize",
            required = false) Integer minSampleSize,
            @RequestParam(value = "minPastYears", required = false) Integer minPastYears, @RequestParam(value = "degreeTypes",
                    required = false) List<DegreeType> degreeTypes, Model model) {
        try {
            updateSettings(minSampleSize, minPastYears, degreeTypes);
        } catch (Exception e) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsInitializer.BUNDLE,
                    "label.gradingTables.errorUpdatingSettings"), model);
            return updateSettings(executionYear, model);
        }
        loadModel(model, executionYear);
        return VIEW_URL + "search";
    }

    @Atomic
    private void updateSettings(Integer minSampleSize, Integer minPastYears, List<DegreeType> degreeTypes) {
        GradingTableSettings.getInstance().setMinSampleSize(minSampleSize);
        GradingTableSettings.getInstance().setMinPastYears(minPastYears);
        for (DegreeType degreeType : GradingTableSettings.getInstance().getApplicableDegreeTypesSet()) {
            degreeType.setGradingTableSettings(null);
        }
        for (DegreeType degreeType : degreeTypes) {
            GradingTableSettings.getInstance().addApplicableDegreeTypes(degreeType);
        }
    }

    private static final String _CREATE_INSTITUTIONAL_URI = "/createinstitutional/";
    public static final String CREATE_INSTITUTIONAL_URL = CONTROLLER_URL + _CREATE_INSTITUTIONAL_URI;

    @RequestMapping(value = _CREATE_INSTITUTIONAL_URI + "{oid}" + "/" + "{token}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> createInstitutional(@PathVariable(value = "oid") ExecutionYear executionYear,
            @PathVariable(value = "token") String token, @ModelAttribute("sectoken") String sectoken, Model model) {
        if (!token.equals(sectoken)) {
            return new ResponseEntity<String>(BundleUtil.getString(FenixeduUlisboaSpecificationsInitializer.BUNDLE,
                    "label.gradingTables.unauthorizedOperation"), HttpStatus.UNAUTHORIZED);
        }
        Thread worker = new Thread(() -> generateInstitutionGradingTable(executionYear));
        workers.put(worker.getId(), worker);
        worker.start();
        return new ResponseEntity<String>(worker.getId() + "/" + token, HttpStatus.OK);
    }

    @Atomic
    private void generateInstitutionGradingTable(ExecutionYear executionYear) {
        InstitutionGradingTable.generate(executionYear);
    }

    private static final String _CREATE_DEGREES_URI = "/createdegrees/";
    public static final String CREATE_DEGREES_URL = CONTROLLER_URL + _CREATE_DEGREES_URI;

    @RequestMapping(value = _CREATE_DEGREES_URI + "{oid}" + "/" + "{token}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> createDegrees(@PathVariable(value = "oid") ExecutionYear executionYear,
            @PathVariable(value = "token") String token, @ModelAttribute("sectoken") String sectoken, Model model) {
        if (!token.equals(sectoken)) {
            return new ResponseEntity<String>(BundleUtil.getString(FenixeduUlisboaSpecificationsInitializer.BUNDLE,
                    "label.gradingTables.unauthorizedOperation"), HttpStatus.UNAUTHORIZED);
        }
        Thread worker = new Thread(() -> generateDegreeGradingTables(executionYear));
        workers.put(worker.getId(), worker);
        worker.start();
        return new ResponseEntity<String>(worker.getId() + "/" + token, HttpStatus.OK);
    }

    @Atomic
    private void generateDegreeGradingTables(ExecutionYear executionYear) {
        DegreeGradingTable.generate(executionYear);
    }

    private static final String _CREATE_COURSESS_URI = "/createcourses/";
    public static final String CREATE_COURSESS_URL = CONTROLLER_URL + _CREATE_COURSESS_URI;

    @RequestMapping(value = _CREATE_COURSESS_URI + "{oid}" + "/" + "{token}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> createCourses(@PathVariable(value = "oid") ExecutionYear executionYear,
            @PathVariable(value = "token") String token, @ModelAttribute("sectoken") String sectoken, Model model) {
        if (!token.equals(sectoken)) {
            return new ResponseEntity<String>(BundleUtil.getString(FenixeduUlisboaSpecificationsInitializer.BUNDLE,
                    "label.gradingTables.unauthorizedOperation"), HttpStatus.UNAUTHORIZED);
        }
        Thread worker = new Thread(() -> generateCourseGradingTables(executionYear));
        workers.put(worker.getId(), worker);
        worker.start();
        return new ResponseEntity<String>(worker.getId() + "/" + token, HttpStatus.OK);
    }

    @Atomic
    private void generateCourseGradingTables(ExecutionYear executionYear) {
        CourseGradingTable.generate(executionYear);
    }

    private static final String _POLL_WORKERS_URI = "/pollworkers/";
    public static final String POLL_WORKERS_URL = CONTROLLER_URL + _POLL_WORKERS_URI;

    @RequestMapping(value = _POLL_WORKERS_URI + "{workerId}" + "/" + "{token}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> pollWorkers(@PathVariable(value = "workerId") long workerId, @PathVariable(
            value = "token") String token, @ModelAttribute("sectoken") String sectoken, Model model) {
        if (!token.equals(sectoken)) {
            return new ResponseEntity<String>(BundleUtil.getString(FenixeduUlisboaSpecificationsInitializer.BUNDLE,
                    "label.gradingTables.unauthorizedOperation"), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<String>(hasFinished(workerId) ? "done" : "ongoing", HttpStatus.OK);
    }

    private static final String _DELETE_TABLES_URI = "/deletetable/";
    public static final String DELETE_TABLES_URL = CONTROLLER_URL + _DELETE_TABLES_URI;

    @RequestMapping(value = _DELETE_TABLES_URI + "{oid}" + "/" + "{oids}" + "/" + "{token}", method = RequestMethod.GET)
    public String deleteTableGet(@PathVariable(value = "oid") ExecutionYear executionYear,
            @PathVariable(value = "oids") String oids, @PathVariable(value = "token") String token,
            @ModelAttribute("sectoken") String sectoken, Model model) {
        if (!token.equals(sectoken)) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsInitializer.BUNDLE,
                    "label.gradingTables.unauthorizedOperation"), model);
        } else {
            deleteTables(oids.split("\\+"));
        }
        loadModel(model, executionYear);
        return VIEW_URL + "search";
    }

    @RequestMapping(value = _DELETE_TABLES_URI + "{oid}" + "/" + "{token}", method = RequestMethod.POST)
    public String deleteTablePost(@PathVariable(value = "oid") ExecutionYear executionYear,
            @PathVariable(value = "token") String token, @RequestParam(value = "oids", required = true) String oids,
            @ModelAttribute("sectoken") String sectoken, Model model) {
        if (!token.equals(sectoken)) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsInitializer.BUNDLE,
                    "label.gradingTables.unauthorizedOperation"), model);
        } else {
            deleteTables(oids.split("\\+"));
        }
        loadModel(model, executionYear);
        return VIEW_URL + "search";
    }

    @Atomic
    private void deleteTables(String[] oids) {
        for (String oid : oids) {
            GradingTable table = FenixFramework.getDomainObject(oid);
            table.delete();
        }
    }

    private List<String> calculateHeaders(Collection<? extends GradingTable> tables) {
        List<String> headers = new ArrayList<String>();
        if (!tables.isEmpty()) {
            for (GradingTable gt : tables) {
                for (GradeConversion gc : gt.getData().getTable()) {
                    headers.add(gc.getMark());
                }
                if (!headers.isEmpty()) {
                    break;
                }
            }
        }
        return headers;
    }
}
