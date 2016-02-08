package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.registrationEntryGrade;

import java.math.BigDecimal;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.manageRegistrationEntryGrade",
        accessGroup = "logged")
@RequestMapping(ManageRegistrationEntryGradeController.CONTROLLER_URL)
public class ManageRegistrationEntryGradeController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/manageregistrationentrygrade";
    public static final String JSP_PATH = "fenixedu-ulisboa-specifications/manageregistrationentrygrade";

    private static final String EDIT_URI = "/edit";
    public static final String EDIT_URL = CONTROLLER_URL + EDIT_URI;

    @RequestMapping(value = EDIT_URI + "/{registrationId}", method = RequestMethod.GET)
    public String edit(@PathVariable("registrationId") final Registration registration, final Model model) {
        return _edit(registration, model);
    }

    public String _edit(@PathVariable("registrationId") final Registration registration, final Model model) {

        model.addAttribute("registration", registration);
        model.addAttribute("entryGrade", registration.getStudentCandidacy().getEntryGrade());
        model.addAttribute("placingOption", registration.getStudentCandidacy().getPlacingOption());

        return jspPage(EDIT_URI);
    }

    @RequestMapping(value = EDIT_URI + "/{registrationId}", method = RequestMethod.POST)
    public String edit(@PathVariable("registrationId") final Registration registration,
            @RequestParam("entryGrade") final String entryGrade, @RequestParam("placingOption") final String placingOption,
            final Model model) {
        try {

            try {
                new BigDecimal(entryGrade);
            } catch (NumberFormatException e) {
                addErrorMessage(ULisboaSpecificationsUtil.bundle("error.ManageRegistrationEntryGrade.entryGrade.format"), model);
                return _edit(registration, model);
            }
            
            try {
                Integer.parseInt(placingOption);
            } catch(final NumberFormatException e) {
                addErrorMessage(ULisboaSpecificationsUtil.bundle("error.ManageRegistrationEntryGrade.placingOption.format"), model);
                return _edit(registration, model);                
            }

            editInformation(registration, new BigDecimal(entryGrade).doubleValue(), Integer.parseInt(placingOption));

            return String.format("redirect:%s/%s", EDIT_URL, registration.getExternalId());
        } catch (final ULisboaSpecificationsDomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return _edit(registration, model);
    }

    @Atomic
    private void editInformation(final Registration registration, final Double entryGrade, final Integer placingOption) {
        registration.getStudentCandidacy().setEntryGrade(entryGrade);
        registration.getStudentCandidacy().setPlacingOption(placingOption);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page.substring(1, page.length());
    }
}
