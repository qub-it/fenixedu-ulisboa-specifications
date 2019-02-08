package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.registrationObservations;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.accessControl.AcademicAuthorizationGroup;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academic.ui.spring.StrutsFunctionalityController;
import org.fenixedu.academic.ui.struts.action.administrativeOffice.student.SearchForStudentsDA;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.treasury.domain.accesscontrol.TreasuryAccessControl;
import org.fenixedu.treasury.services.accesscontrol.TreasuryAccessControlAPI;
import org.fenixedu.ulisboa.specifications.domain.RegistrationObservations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;
import pt.ist.fenixframework.Atomic;

@Controller
@RequestMapping("registrations")
public class RegistrationObservationsController extends StrutsFunctionalityController {

    @RequestMapping(value = "/{registration}/observations", method = RequestMethod.GET)
    public String home(@PathVariable("registration") Registration registration, Model model, HttpServletRequest request) {
//
//        RegistrationObservations registrationObservations = registration.getRegistrationObservations();
//
//        if (registrationObservations != null && !StringUtils.isEmpty(registrationObservations.getValue())) {
//            model.addAttribute("observations", registrationObservations.getValue().replaceAll("\r\n", "\\\\n"));
//        }
        model.addAttribute("observations", RegistrationObservations.getReverseSortedObservations(registration));
        model.addAttribute("registration", registration);

        String returnLink =
                "/academicAdministration/registration.do?method=visualizeRegistration&registrationID="
                        + registration.getExternalId();
        String contextPath = request.getContextPath();
        returnLink = GenericChecksumRewriter.injectChecksumInUrl(contextPath, returnLink, request.getSession());
        returnLink = contextPath.equals("/") ? returnLink : contextPath + returnLink;

        model.addAttribute("returnURL", returnLink);
        boolean writeAccessControll = getWriteAccessControl(registration);
        model.addAttribute("writeAccess", writeAccessControll);
        return "fenixedu-ulisboa-specifications/registrationObservations/editRegistrationObservations";
    }

    private boolean getWriteAccessControl(Registration registration) {
        User user = AccessControl.getPerson().getUser();
        boolean academicAuthorization =
                AcademicAuthorizationGroup.get(AcademicOperationType.STUDENT_ENROLMENTS, registration.getDegree()).isMember(user);
        boolean treasuryAuthorization = TreasuryAccessControlAPI.isBackOfficeMember(user.getUsername());

        boolean writeAccessControll = academicAuthorization || treasuryAuthorization;
        return writeAccessControll;
    }

    @RequestMapping(value = "/{registration}/observations/{registrationObservations}", method = RequestMethod.GET)
    public String prepareEdit(@PathVariable("registrationObservations") RegistrationObservations registrationObservations,
            Model model, HttpServletRequest request) {
        model.addAttribute("editObservationText", registrationObservations.getValue().replaceAll("\r\n", "\\\\n"));
        model.addAttribute("editObservation", registrationObservations);
        return home(registrationObservations.getRegistration(), model, request);
    }

    @RequestMapping(value = "/{registration}/observations/create", method = RequestMethod.GET)
    public String prepareCreate(@PathVariable("registration") Registration registration, Model model, HttpServletRequest request) {
        model.addAttribute("createMode", true);
        return home(registration, model, request);
    }

    @Atomic
    @RequestMapping(value = "/{registration}/observations/{registrationObservations}", method = RequestMethod.POST)
    public String edit(@PathVariable("registrationObservations") RegistrationObservations registrationObservations,
            @RequestParam(value = "delete", required = false) String delete,
            @RequestParam(value = "create", required = false) String create, @RequestParam("observations") String observations,
            Model model, HttpServletRequest request) {
        if (!getWriteAccessControl(registrationObservations.getRegistration())) {
            throw new RuntimeException("Unauthorized");
        }
        Registration registration = registrationObservations.getRegistration();
        if (!StringUtils.isEmpty(delete)) {
            model.addAttribute("deleted", true);
            registrationObservations.delete();
            return home(registration, model, request);
        } else {
            registrationObservations.setValue(observations);
            model.addAttribute("saved", true);
            return home(registration, model, request);
        }
    }

    @Atomic
    @RequestMapping(value = "/{registration}/observations", method = RequestMethod.POST)
    public String create(@PathVariable("registration") Registration registration,
            @RequestParam("observations") String observations, Model model, HttpServletRequest request) {
        if (!getWriteAccessControl(registration)) {
            throw new RuntimeException("Unauthorized");
        }
        RegistrationObservations registrationObservations = new RegistrationObservations(registration);
        registrationObservations.setValue(observations);
        model.addAttribute("saved", true);
        return home(registration, model, request);
    }

    @Override
    protected Class<?> getFunctionalityType() {
        return SearchForStudentsDA.class;
    }

}
