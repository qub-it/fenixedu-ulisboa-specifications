package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.registrationEntryGrade;

import java.math.BigDecimal;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationServices;
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
        model.addAttribute("ingressionGradeA", RegistrationServices.getIngressionGradeA(registration));
        model.addAttribute("ingressionGradeB", RegistrationServices.getIngressionGradeB(registration));
        model.addAttribute("ingressionGradeC", RegistrationServices.getIngressionGradeC(registration));
        model.addAttribute("ingressionGradeD", RegistrationServices.getIngressionGradeD(registration));
        model.addAttribute("placingOption", registration.getStudentCandidacy().getPlacingOption());
        model.addAttribute("numberOfCandidaciesToHigherSchool",
                registration.getStudentCandidacy().getNumberOfCandidaciesToHigherSchool());

        return jspPage(EDIT_URI);
    }

    @RequestMapping(value = EDIT_URI + "/{registrationId}", method = RequestMethod.POST)
    public String edit(@PathVariable("registrationId") final Registration registration,
            @RequestParam("entryGrade") final BigDecimal entryGrade,
            @RequestParam("ingressionGradeA") BigDecimal ingressionGradeA,
            @RequestParam("ingressionGradeB") BigDecimal ingressionGradeB,
            @RequestParam("ingressionGradeC") BigDecimal ingressionGradeC,
            @RequestParam("ingressionGradeD") BigDecimal ingressionGradeD,
            @RequestParam("placingOption") final Integer placingOption,
            @RequestParam("numberOfCandidaciesToHigherSchool") Integer numberOfCandidaciesToHigherSchool, final Model model) {
        try {

            editInformation(registration, entryGrade != null ? entryGrade.doubleValue() : null, ingressionGradeA,
                    ingressionGradeB, ingressionGradeC, ingressionGradeD, placingOption, numberOfCandidaciesToHigherSchool);

            addInfoMessage(ULisboaSpecificationsUtil.bundle("label.success.update"), model);

            return String.format("redirect:%s/%s", EDIT_URL, registration.getExternalId());
        } catch (final ULisboaSpecificationsDomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return _edit(registration, model);
    }

    @Atomic
    private void editInformation(final Registration registration, final Double entryGrade, BigDecimal ingressionGradeA,
            BigDecimal ingressionGradeB, BigDecimal ingressionGradeC, BigDecimal ingressionGradeD, final Integer placingOption,
            Integer numberOfCandidaciesToHigherSchool) {

        registration.getStudentCandidacy().setEntryGrade(entryGrade);
        registration.getStudentCandidacy().setPlacingOption(placingOption);
        registration.getStudentCandidacy().setNumberOfCandidaciesToHigherSchool(numberOfCandidaciesToHigherSchool);

        RegistrationServices.setIngressionGradeA(registration, ingressionGradeA);
        RegistrationServices.setIngressionGradeB(registration, ingressionGradeB);
        RegistrationServices.setIngressionGradeC(registration, ingressionGradeC);
        RegistrationServices.setIngressionGradeD(registration, ingressionGradeD);

    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page.substring(1, page.length());
    }
}
