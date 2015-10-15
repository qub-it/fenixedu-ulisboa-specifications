package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.registrationObservations;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.ui.spring.StrutsFunctionalityController;
import org.fenixedu.academic.ui.struts.action.administrativeOffice.student.SearchForStudentsDA;
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

        RegistrationObservations registrationObservations = registration.getRegistrationObservations();

        if (registrationObservations != null && !StringUtils.isEmpty(registrationObservations.getValue())) {
            model.addAttribute("observations", registrationObservations.getValue().replaceAll("\r\n", "\\\\n"));
        }

        String returnLink =
                "/academicAdministration/registration.do?method=visualizeRegistration&registrationID="
                        + registration.getExternalId();
        String contextPath = request.getContextPath();
        returnLink = GenericChecksumRewriter.injectChecksumInUrl(contextPath, returnLink, request.getSession());
        returnLink = contextPath.equals("/") ? returnLink : contextPath + returnLink;

        model.addAttribute("returnURL", returnLink);

        return "fenixedu-ulisboa-specifications/registrationObservations/editRegistrationObservations";
    }

    @Atomic
    @RequestMapping(value = "/{registration}/observations", method = RequestMethod.POST)
    public String updateObservations(@PathVariable("registration") Registration registration,
            @RequestParam("observations") String observations, Model model, HttpServletRequest request) {

        RegistrationObservations registrationObservations = registration.getRegistrationObservations();
        if (registrationObservations == null) {
            registrationObservations = new RegistrationObservations(registration);
        }
        registrationObservations.setValue(observations);
        model.addAttribute("saved", true);
        return home(registration, model, request);
    }

    @Override
    protected Class<?> getFunctionalityType() {
        return SearchForStudentsDA.class;
    }

}
