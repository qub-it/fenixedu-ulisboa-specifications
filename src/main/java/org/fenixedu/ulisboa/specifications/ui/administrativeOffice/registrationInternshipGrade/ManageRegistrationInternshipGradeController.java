package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.registrationInternshipGrade;

import java.math.BigDecimal;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationServices;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.manageRegistrationInternshipGrade",
        accessGroup = "logged")
@RequestMapping(ManageRegistrationInternshipGradeController.CONTROLLER_URL)
public class ManageRegistrationInternshipGradeController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/manageregistrationinternshipgrade";
    public static final String JSP_PATH = "fenixedu-ulisboa-specifications/manageregistrationinternshipgrade";

    private static final String EDIT_URI = "/edit";
    public static final String EDIT_URL = CONTROLLER_URL + EDIT_URI;

    @RequestMapping(value = EDIT_URI + "/{registrationId}", method = RequestMethod.GET)
    public String edit(@PathVariable("registrationId") final Registration registration, final Model model) {

        model.addAttribute("registration", registration);
        model.addAttribute("internshipGrade", RegistrationServices.getInternshipGrade(registration));
        model.addAttribute("internshipConclusionDate", RegistrationServices.getInternshipConclusionDate(registration));

        return jspPage(EDIT_URI);
    }

    @RequestMapping(value = EDIT_URI + "/{registrationId}", method = RequestMethod.POST)
    public String edit(@PathVariable("registrationId") final Registration registration,
            @RequestParam("internshipGrade") final BigDecimal internshipGrade,
            @RequestParam("internshipConclusionDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate internshipConclusionDate,
            final Model model) {
        try {

            editInformation(registration, internshipGrade, internshipConclusionDate);

            addInfoMessage(ULisboaSpecificationsUtil.bundle("label.success.update"), model);

            return String.format("redirect:%s/%s", EDIT_URL, registration.getExternalId());
        } catch (final ULisboaSpecificationsDomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return edit(registration, model);
    }

    @Atomic
    private void editInformation(final Registration registration, final BigDecimal internshipGrade,
            final LocalDate internshipConclusionDate) {
        RegistrationServices.setInternshipGrade(registration, internshipGrade);
        RegistrationServices.setInternshipConclusionDate(registration, internshipConclusionDate);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page.substring(1, page.length());
    }
}
