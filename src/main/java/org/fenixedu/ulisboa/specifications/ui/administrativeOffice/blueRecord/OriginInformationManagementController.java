package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.blueRecord;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

import javax.servlet.http.HttpSession;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.qualification.OriginInformationForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.qualification.OriginInformationFormController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = BlueRecordManagementEntryPoint.class)
@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.origininformationmanagement",
        accessGroup = "logged")
@RequestMapping(OriginInformationManagementController.CONTROLLER_URL)
public class OriginInformationManagementController extends OriginInformationFormController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/origininformationmanagement";
    private static final String JSP_PATH = "/fenixedu-ulisboa-specifications/origininformationmanagement";

    private static final String _READ_URI = "/read";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    protected static final String REGISTRATION_SESSION_ATTR = "registrationOID";

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(_READ_URI + "/{registrationId}")
    public String read(@PathVariable("registrationId") final Registration registration, final Model model) {
        OriginInformationForm form = createOriginInformationForm(registration);

        model.addAttribute("registration", registration);
        model.addAttribute("originInformationForm", form);

        return jspPage(_READ_URI);
    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    private static final String _UPDATE_URI = "/update";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI + "/{registrationId}", method = RequestMethod.GET)
    public String update(@PathVariable("registrationId") final Registration registration, final Model model,
            final RedirectAttributes redirectAttributes, final HttpSession session) {
        session.setAttribute(REGISTRATION_SESSION_ATTR, registration);

        ExecutionYear executionYear = ExecutionYear.findCurrent(registration.getDegree().getCalendar());

        model.addAttribute("registration", registration);
        model.addAttribute("postAction", "update/" + registration.getExternalId());
        addControllerURLToModel(executionYear, model);

        return fillGetScreen(executionYear, model, redirectAttributes);
    }

    @RequestMapping(value = _UPDATE_URI + "/{registrationId}", method = RequestMethod.POST)
    public String update(@PathVariable("registrationId") final Registration registration,
            @RequestParam(value = "bean", required = true) final OriginInformationForm form, final Model model,
            final RedirectAttributes redirectAttributes, final HttpSession session) {
        session.setAttribute(REGISTRATION_SESSION_ATTR, registration);
        model.addAttribute("registration", registration);
        ExecutionYear executionYear = ExecutionYear.findCurrent(registration.getDegree().getCalendar());
        addControllerURLToModel(executionYear, model);
        if (!validate(executionYear, form, model)) {
            setForm(form, model);
            return update(registration, model, redirectAttributes, session);
        }

        try {
            writeData(executionYear, form, model);

            model.addAttribute("form", form);

            return redirect(READ_URL + "/" + registration.getExternalId(), model, redirectAttributes);
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
        return update(registration, model, redirectAttributes, session);
    }

    @Override
    protected Registration getRegistration(final ExecutionYear executionYear, final Model model) {
        return (Registration) model.asMap().get("registration");
    }

    @Override
    public boolean isDistrictAndSubdivisionRequired() {
        return false;
    }

    @Override
    protected boolean isInstitutionAndDegreeRequiredWhenNotDefaultCountryOrNotHigherLevel() {
        return false;
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
        Registration registration = (Registration) model.asMap().get("registration");
        return registration.getStudent();
    }

    @Override
    public String back(@RequestParam(value = "executionYear", required = false) final ExecutionYear executionYear,
            final Model model, final RedirectAttributes redirectAttributes, final HttpSession session) {
        Registration registration = (Registration) session.getAttribute(REGISTRATION_SESSION_ATTR);
        if (registration != null) {
            return redirect(READ_URL + "/" + registration.getExternalId(), model, redirectAttributes);
        }

        throw new RuntimeException("Registration object is not in http session as an attribute.");
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
