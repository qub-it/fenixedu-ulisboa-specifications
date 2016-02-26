package org.fenixedu.ulisboa.specifications.ui.ectsgradingtable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.ects.CourseGradingTable;
import org.fenixedu.ulisboa.specifications.domain.ects.DegreeGradingTable;
import org.fenixedu.ulisboa.specifications.domain.ects.GradingTable;
import org.fenixedu.ulisboa.specifications.domain.ects.InstitutionGradingTable;
import org.fenixedu.ulisboa.specifications.domain.ects.GradingTableData.GradeConversion;
import org.fenixedu.ulisboa.specifications.servlet.FenixeduUlisboaSpecificationsInitializer;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.manageECTSGradingTables",
        accessGroup = "academic(MANAGE_DEGREE_CURRICULAR_PLANS)| #managers")
@RequestMapping(EctsGradingTableBackofficeController.CONTROLLER_URL)
@SessionAttributes("sectoken")
public class EctsGradingTableBackofficeController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/ulisboaspecifications/ectsgradingtable";

    public static final String VIEW_URL = "fenixedu-ulisboa-specifications/managegradingtable/ectsgradingtable/";

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
        model.addAttribute("selectedYear", executionYear);
        model.addAttribute("institutionGradeTable", InstitutionGradingTable.find(executionYear));
        model.addAttribute("degreeGradeTableHeaders", calculateHeaders(DegreeGradingTable.find(executionYear)));
        model.addAttribute("degreeGradeTable", DegreeGradingTable.find(executionYear));
        model.addAttribute("courseGradeTableHeaders", calculateHeaders(CourseGradingTable.find(executionYear)));
        model.addAttribute("courseGradeTable", CourseGradingTable.find(executionYear));
        model.addAttribute("sectoken", SecToken.generate());
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

    private static final String _CREATE_INSTITUTIONAL_URI = "/createinstitutional/";
    public static final String CREATE_INSTITUTIONAL_URL = CONTROLLER_URL + _CREATE_INSTITUTIONAL_URI;

    @RequestMapping(value = _CREATE_INSTITUTIONAL_URI + "{oid}" + "/" + "{token}", method = RequestMethod.GET)
    public String createInstitutional(@PathVariable(value = "oid") ExecutionYear executionYear,
            @PathVariable(value = "token") String token, @ModelAttribute("sectoken") String sectoken, Model model) {
        InstitutionGradingTable createdTable = null;
        if (!token.equals(sectoken)) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsInitializer.BUNDLE,
                    "label.gradingTables.unauthorizedOperation"), model);
        } else {
            createdTable = generateInstitutionGradingTable(executionYear);
        }
        loadModel(model, executionYear);
        model.addAttribute("institutionGradeTable", createdTable);
        return VIEW_URL + "search";
    }

    @Atomic
    private InstitutionGradingTable generateInstitutionGradingTable(final ExecutionYear executionYear) {
        return InstitutionGradingTable.find(executionYear) != null ? InstitutionGradingTable.find(executionYear) : InstitutionGradingTable
                .generate(executionYear);
    }

    private static final String _CREATE_DEGREES_URI = "/createdegrees/";
    public static final String CREATE_DEGREES_URL = CONTROLLER_URL + _CREATE_DEGREES_URI;

    @RequestMapping(value = _CREATE_DEGREES_URI + "{oid}" + "/" + "{token}", method = RequestMethod.GET)
    public String createDegrees(@PathVariable(value = "oid") ExecutionYear executionYear,
            @PathVariable(value = "token") String token, @ModelAttribute("sectoken") String sectoken, Model model,
            RedirectAttributes redirectAttributes) {
        if (!token.equals(sectoken)) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsInitializer.BUNDLE,
                    "label.gradingTables.unauthorizedOperation"), model);
            loadModel(model, executionYear);
            return VIEW_URL + "search";
        }
        DegreeGradingTable.generate(executionYear);
        return redirect(SEARCH_URL, model, redirectAttributes);
    }

    private static final String _CREATE_COURSESS_URI = "/createcourses/";
    public static final String CREATE_COURSESS_URL = CONTROLLER_URL + _CREATE_COURSESS_URI;

    @RequestMapping(value = _CREATE_COURSESS_URI + "{oid}" + "/" + "{token}", method = RequestMethod.GET)
    public String createCourses(@PathVariable(value = "oid") ExecutionYear executionYear,
            @PathVariable(value = "token") String token, @ModelAttribute("sectoken") String sectoken, Model model,
            RedirectAttributes redirectAttributes) {
        if (!token.equals(sectoken)) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsInitializer.BUNDLE,
                    "label.gradingTables.unauthorizedOperation"), model);
            loadModel(model, executionYear);
            return VIEW_URL + "search";
        }
        CourseGradingTable.generate(executionYear);
        return redirect(SEARCH_URL, model, redirectAttributes);
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