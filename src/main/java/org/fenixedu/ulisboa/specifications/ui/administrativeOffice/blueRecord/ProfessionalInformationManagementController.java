package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.blueRecord;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

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
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo.ProfessionalInformationForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.householdinfo.ProfessionalInformationFormController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = BlueRecordManagementEntryPoint.class)
@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.professionalInformationManagement",
        accessGroup = "logged")
@RequestMapping(ProfessionalInformationManagementController.CONTROLLER_URL)
public class ProfessionalInformationManagementController extends ProfessionalInformationFormController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/professionalinformationulisboamanagement";

    protected static final String STUDENT_SESSION_ATTR = "studentOID";

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    private static final String _READ_URI = "/read";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI + "/{studentId}/{executionYearId}")
    public String read(@PathVariable("studentId") final Student student,
            @PathVariable("executionYearId") final ExecutionYear executionYear, final Model model) {
        model.addAttribute("student", student);
        model.addAttribute("personalIngressionData", getPersonalIngressionData(student, executionYear, false));

        return "/fenixedu-ulisboa-specifications/householdinformationmanagement/professionalInformation/read";
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

        ProfessionalInformationForm form = createProfissionalInformationForm(student, executionYear, false);

        setForm(form, model);

        return fillGetScreen(executionYear, model, redirectAttributes);
    }

    @RequestMapping(value = _UPDATE_URI + "/{studentId}/{executionYearId}", method = RequestMethod.POST)
    public String update(@PathVariable("studentId") final Student student,
            @PathVariable("executionYearId") final ExecutionYear executionYear,
            @RequestParam(value = "bean", required = true) final ProfessionalInformationForm form, final Model model,
            final RedirectAttributes redirectAttributes, final HttpSession session) {
        session.setAttribute(STUDENT_SESSION_ATTR, student);
        model.addAttribute("student", student);

        if (!validate(executionYear, form, model)) {
            setForm(form, model);
            addControllerURLToModel(executionYear, model);
            return update(student, executionYear, model, redirectAttributes, session);
        }
        addControllerURLToModel(executionYear, model);

        try {
            writeData(executionYear, form, model);

            model.addAttribute("form", form);

            return redirect(READ_URL + "/" + student.getExternalId() + "/" + executionYear.getExternalId(), model,
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
        return update(student, executionYear, model, redirectAttributes, session);
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
    public String back(@RequestParam(value = "executionYear", required = false) final ExecutionYear executionYear,
            final Model model, final RedirectAttributes redirectAttributes, final HttpSession session) {
        Student student = (Student) session.getAttribute(STUDENT_SESSION_ATTR);
        if (student != null) {
            return redirect(HouseholdInformationManagementController.SEARCH_URL + "/" + student.getExternalId(), model,
                    redirectAttributes);
        }

        throw new RuntimeException("Student object is not in http session as an attribute.");
    }

}
