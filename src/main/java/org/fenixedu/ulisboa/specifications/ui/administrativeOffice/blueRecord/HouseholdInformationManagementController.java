package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.blueRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.GrantOwnerType;
import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.person.MaritalStatus;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationDataByExecutionYear;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.ProfessionTimeType;
import org.fenixedu.ulisboa.specifications.domain.SalarySpan;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.HouseholdInformationFormController;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.emory.mathcs.backport.java.util.Collections;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.householdinformationmanagement",
        accessGroup = "logged")
@RequestMapping(HouseholdInformationManagementController.CONTROLLER_URL)
public class HouseholdInformationManagementController extends HouseholdInformationFormController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/householdinformationmanagement";
    private static final String JSP_PATH = "/fenixedu-ulisboa-specifications/householdinformationmanagement";

    private static final String _SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;
    
    @RequestMapping(_SEARCH_URI + "/{studentId}")
    public String search(@PathVariable("studentId") final Student student, final Model model) {
        model.addAttribute("student", student);

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
    public String create(@PathVariable("studentId") final Student student, final Model model) {
        return _create(student, createEmptyHouseholdInformationForm(student, model), model);
    }

    public String _create(final Student student, final HouseholdInformationForm form, final Model model) {
        model.addAttribute("student", student);
        model.addAttribute("householdInformationForm", form);

        model.addAttribute("schoolLevelValues", SchoolLevelType.values());
        model.addAttribute("professionTypeValues", ProfessionType.values());
        model.addAttribute("professionalConditionValues", ProfessionalSituationConditionType.values());
        model.addAttribute("salarySpanValues", SalarySpan.readAll().collect(Collectors.toList()));
        model.addAttribute("professionTimeTypeValues", ProfessionTimeType.readAll().collect(Collectors.toList()));
        model.addAttribute("grantOwnerTypeValues", GrantOwnerType.values());
        model.addAttribute("executionYearValues", loadActiveExecutionYearValues(student));

        List<MaritalStatus> maritalStatusValues = new ArrayList<>();
        maritalStatusValues.addAll(Arrays.asList(MaritalStatus.values()));
        maritalStatusValues.remove(MaritalStatus.UNKNOWN);
        model.addAttribute("maritalStatusValues", maritalStatusValues);
        
        return jspPage(_CREATE_URI);
    }

    @RequestMapping(value = _CREATE_URI + "/{studentId}", method = RequestMethod.POST)
    public String create(@PathVariable("studentId") final Student student, final HouseholdInformationForm form,
            final Model model) {
        try {

            if (form.getExecutionYear() == null) {
                addErrorMessage(ULisboaSpecificationsUtil.bundle("label.HouseholdInformationForm.executionYear.required"), model);
                return _create(student, form, model);
            }

            if (getPersonalIngressionData(student, form.getExecutionYear(), false) != null) {
                addErrorMessage(ULisboaSpecificationsUtil.bundle("label.HouseholdInformationForm.exists.for.execution.year"),
                        model);
            }

            if (!validate(form, model)) {
                return _create(student, form, model);
            }

            writeData(student, form.getExecutionYear(), form, model);

            return "redirect:" + SEARCH_URL + "/" + student.getExternalId();
        } catch (final DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return _create(student, form, model);
    }

    private static final String _UPDATE_URI = "/update";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI + "/{studentId}/{executionYearId}", method = RequestMethod.GET)
    public String update(@PathVariable("studentId") final Student student, @PathVariable("executionYearId") final ExecutionYear executionYear, 
            final Model model) {
        return _update(student, executionYear, createHouseholdInformationForm(student, executionYear, false, model), model);
    }

    private String _update(final Student student, final ExecutionYear executionYear, final HouseholdInformationForm form,
            final Model model) {
        model.addAttribute("student", student);
        model.addAttribute("executionYear", executionYear);
        model.addAttribute("householdInformationForm", form);

        model.addAttribute("schoolLevelValues", SchoolLevelType.values());
        model.addAttribute("professionTypeValues", ProfessionType.values());
        model.addAttribute("professionalConditionValues", ProfessionalSituationConditionType.values());
        model.addAttribute("salarySpanValues", SalarySpan.readAll().collect(Collectors.toList()));
        model.addAttribute("professionTimeTypeValues", ProfessionTimeType.readAll().collect(Collectors.toList()));
        model.addAttribute("grantOwnerTypeValues", GrantOwnerType.values());
        model.addAttribute("executionYearValues", loadActiveExecutionYearValues(student));

        List<MaritalStatus> maritalStatusValues = new ArrayList<>();
        maritalStatusValues.addAll(Arrays.asList(MaritalStatus.values()));
        maritalStatusValues.remove(MaritalStatus.UNKNOWN);
        model.addAttribute("maritalStatusValues", maritalStatusValues);
        
        return jspPage(_UPDATE_URI);
    }

    @RequestMapping(value = _UPDATE_URI + "/{studentId}/{executionYearId}", method = RequestMethod.POST)
    public String update(@PathVariable("studentId") final Student student, @PathVariable("executionYearId") final ExecutionYear executionYear, final HouseholdInformationForm form,
            final Model model) {
        try {

            if (getPersonalIngressionData(student, form.getExecutionYear(), false) == null) {
                addErrorMessage(ULisboaSpecificationsUtil.bundle("label.HouseholdInformationForm.executionYear.required"), model);
            }

            if (!validate(form, model)) {
                return _update(student, executionYear, form, model);
            }

            writeData(student, executionYear, form, model);

            return "redirect:" + SEARCH_URL + "/" + student.getExternalId();
        } catch (final DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return _update(student, executionYear, form, model);
    }

    private List<ExecutionYear> loadActiveExecutionYearValues(final Student student) {
        Set<ExecutionYear> executionYearsSet = Sets.newHashSet();

        for (final Registration registration : student.getRegistrationsSet()) {
            for (final RegistrationDataByExecutionYear registrationDataByExecutionYear : registration.getRegistrationDataByExecutionYearSet()) {
                executionYearsSet.add(registrationDataByExecutionYear.getExecutionYear());
            }
        }
        
        final List<ExecutionYear> result = Lists.newArrayList(executionYearsSet);
        Collections.sort(result, Collections.reverseOrder(ExecutionYear.COMPARATOR_BY_YEAR));
        
        return result;
    }
    
    @Override
    protected boolean isProfessionRequired() {
        return false;
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page.substring(1, page.length());
    }

    /* ********************
     * MAPPINGS NOT APPLIED 
     * ********************
     */

    @Override
    protected String nextScreen(Model model, RedirectAttributes redirectAttributes) {
        throw new RuntimeException("not applied in this controller");
    }
    
    @Override
    protected Student getStudent(final Model model) {
        throw new RuntimeException("not applied in this controller");
    }

    @Override
    public String back(final Model model, final RedirectAttributes redirectAttributes) {
        throw new RuntimeException("not applied in this controller");
    }

}
