package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.blueRecord;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo.HouseholdInformationForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo.HouseholdInformationFormController;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = BlueRecordManagementEntryPoint.class)
@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.householdinformationmanagement",
        accessGroup = "logged")
@RequestMapping(HouseholdInformationManagementController.CONTROLLER_URL)
public class HouseholdInformationManagementController extends HouseholdInformationFormController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/householdinformationmanagement";
    private static final String JSP_PATH = "/fenixedu-ulisboa-specifications/householdinformationmanagement";

    private static final String _SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    protected static final String STUDENT_SESSION_ATTR = "studentOID";
    protected static final String EXECUTION_YEAR_SESSION_ATTR = "executionYearOID";

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @RequestMapping(_SEARCH_URI + "/{studentId}")
    public String search(@PathVariable("studentId") final Student student, final Model model) {
        model.addAttribute("student", student);

        List<ExecutionYear> executionYears = student.getRegistrationsSet().stream()
                .flatMap(r -> r.getRegistrationDataByExecutionYearSet().stream()).map(data -> data.getExecutionYear()).distinct()
                .filter(e -> getPersonalIngressionData(student, e, false) == null)
                .sorted(ExecutionYear.COMPARATOR_BY_BEGIN_DATE.reversed()).collect(Collectors.toList());

        model.addAttribute("allowedExecutionYears", executionYears);

        return jspPage(_SEARCH_URI);
    }

    private static final String _READ_URI = "/read";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI + "/{studentId}/{executionYearId}")
    public String read(@PathVariable("studentId") final Student student,
            @PathVariable("executionYearId") final ExecutionYear executionYear, final Model model) {
        model.addAttribute("student", student);
        model.addAttribute("personalIngressionData", getPersonalIngressionData(student, executionYear, false));

        return jspPage(_READ_URI);
    }

    private static final String _CREATE_URI = "/create";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI + "/{studentId}", method = RequestMethod.GET)
    public String create(@PathVariable("studentId") final Student student,
            @RequestParam(value = "executionYear", required = true) final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes, final HttpSession session) {
        session.setAttribute(STUDENT_SESSION_ATTR, student);

        model.addAttribute("student", student);
        model.addAttribute("postAction", "create/" + student.getExternalId());
        addControllerURLToModel(executionYear, model);

        HouseholdInformationForm form = createEmptyHouseholdInformationForm(student, model);
        form.setExecutionYear(executionYear);
        setForm(form, model);

        return fillGetScreen(executionYear, model, redirectAttributes);
    }

    @RequestMapping(value = _CREATE_URI + "/{studentId}", method = RequestMethod.POST)
    public String create(@PathVariable("studentId") final Student student,
            @RequestParam(value = "bean", required = true) final HouseholdInformationForm form, final Model model,
            final RedirectAttributes redirectAttributes, final HttpSession session) {
        session.setAttribute(STUDENT_SESSION_ATTR, student);
        model.addAttribute("student", student);

        if (!validate(form.getExecutionYear(), form, model)) {
            setForm(form, model);
            addControllerURLToModel(form.getExecutionYear(), model);
            return create(student, form.getExecutionYear(), model, redirectAttributes, session);
        }
        addControllerURLToModel(form.getExecutionYear(), model);

        try {
            writeData(form, model);

            model.addAttribute("form", form);

            return redirect(READ_URL + "/" + student.getExternalId() + "/" + form.getExecutionYear().getExternalId(), model,
                    redirectAttributes);
        } catch (DomainException domainEx) {
            logger.error("Exception for user " + AccessControl.getPerson().getUsername());
            domainEx.printStackTrace();
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, domainEx.getKey()),
                    model);
        } catch (Exception de) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "label.error.create") + de.getLocalizedMessage(), model);
            logger.error("Exception for user " + AccessControl.getPerson().getUsername());
            de.printStackTrace();
        }

        setForm(form, model);
        return create(student, form.getExecutionYear(), model, redirectAttributes, session);
    }

    @Atomic
    private void writeData(final HouseholdInformationForm form, final Model model) {
        getPersonalIngressionData(getStudent(model), form.getExecutionYear(), true);

        writeData(form.getExecutionYear(), form, model);
    }

    @Override
    protected boolean validate(final HouseholdInformationForm form, final Model model) {
        boolean valid = super.validate(form, model);

        if (form.getExecutionYear() == null) {
            addErrorMessage(ULisboaSpecificationsUtil.bundle("label.HouseholdInformationForm.executionYear.required"), model);
            form.setExecutionYear(ExecutionYear.findCurrent(null));
            valid = false;
        }

        return valid;
    }

    private static final String _UPDATE_URI = "/update";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI + "/{studentId}/{executionYearId}", method = RequestMethod.GET)
    public String update(@PathVariable("studentId") final Student student,
            @PathVariable("executionYearId") final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes, final HttpSession session) {
        session.setAttribute(STUDENT_SESSION_ATTR, student);

        model.addAttribute("student", student);
        model.addAttribute("postAction", "update/" + student.getExternalId() + "/" + executionYear.getExternalId());
        addControllerURLToModel(executionYear, model);

        HouseholdInformationForm form = createHouseholdInformationForm(student, executionYear, false);
        form.setExecutionYear(executionYear);

        setForm(form, model);

        return fillGetScreen(executionYear, model, redirectAttributes);
    }

    @RequestMapping(value = _UPDATE_URI + "/{studentId}/{executionYearId}", method = RequestMethod.POST)
    public String update(@PathVariable("studentId") final Student student,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @RequestParam(value = "bean", required = true) final HouseholdInformationForm form, final Model model,
            final RedirectAttributes redirectAttributes, final HttpSession session) {
        session.setAttribute(STUDENT_SESSION_ATTR, student);
        model.addAttribute("student", student);

        if (!validate(form.getExecutionYear(), form, model)) {
            setForm(form, model);
            addControllerURLToModel(form.getExecutionYear(), model);
            return update(student, form.getExecutionYear(), model, redirectAttributes, session);
        }
        addControllerURLToModel(form.getExecutionYear(), model);

        try {
            writeData(form.getExecutionYear(), form, model);

            model.addAttribute("form", form);

            return redirect(READ_URL + "/" + student.getExternalId() + "/" + form.getExecutionYear().getExternalId(), model,
                    redirectAttributes);
        } catch (DomainException domainEx) {
            logger.error("Exception for user " + AccessControl.getPerson().getUsername());
            domainEx.printStackTrace();
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, domainEx.getKey()),
                    model);
        } catch (Exception de) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "label.error.create") + de.getLocalizedMessage(), model);
            logger.error("Exception for user " + AccessControl.getPerson().getUsername());
            de.printStackTrace();
        }

        setForm(form, model);
        return update(student, form.getExecutionYear(), model, redirectAttributes, session);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page.substring(1, page.length());
    }

    @Override
    public boolean isFormIsFilled(final ExecutionYear executionYear, final Student student) {
        return false;
    }

    @Override
    protected Student getStudent(final Model model) {
        return (Student) model.asMap().get("student");
    }

    @Override
    public void addDefaultAttributes(Model model) {
        model.addAttribute(SHOW_DISLOCATED_QUESTION, true);
        model.addAttribute(SHOW_FLUNKED_UNIVERSITY, true);
        model.addAttribute(SHOW_GRANT_OWNER_TYPE, true);
    }

    @Override
    public String back(@RequestParam(value = "executionYear", required = false) final ExecutionYear executionYear,
            final Model model, final RedirectAttributes redirectAttributes, final HttpSession session) {
        Student student = (Student) session.getAttribute(STUDENT_SESSION_ATTR);
        ExecutionYear executionYearSession = (ExecutionYear) session.getAttribute(EXECUTION_YEAR_SESSION_ATTR);
        if (student != null) {
            if (executionYearSession != null) {
                return redirect(READ_URL + "/" + student.getExternalId() + "/" + executionYearSession.getExternalId(), model,
                        redirectAttributes);
            }

            return redirect(SEARCH_URL + "/" + student.getExternalId(), model, redirectAttributes);
        }

        throw new RuntimeException("Student object is not in http session as an attribute.");
    }

    /* ********************
     * MAPPINGS NOT APPLIED
     * ********************
     */

    @Override
    protected String nextScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        throw new RuntimeException("not applied in this controller");
    }

}
