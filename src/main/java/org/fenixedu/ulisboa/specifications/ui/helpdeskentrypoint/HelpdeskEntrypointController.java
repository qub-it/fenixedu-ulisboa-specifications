package org.fenixedu.ulisboa.specifications.ui.helpdeskentrypoint;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.fenixedu.academic.ui.spring.controller.AcademicAdministrationSpringApplication;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.fenixedu.bennu.portal.servlet.BennuPortalDispatcher;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SpringFunctionality(app = AcademicAdministrationSpringApplication.class, title = "label.title.helpdesk",
        accessGroup = "#managers")
@RequestMapping(HelpdeskEntrypointController.CONTROLLER_URL)
public class HelpdeskEntrypointController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/ulisboaspecifications/helpdesk";

    @RequestMapping(method = GET)
    public String home(Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("themePath", request.getContextPath() + "/themes/" + PortalConfiguration.getInstance().getTheme());
        model.addAttribute("functionalityOid", BennuPortalDispatcher.getSelectedFunctionality(request).getExternalId());
        return "fenixedu-ulisboa-specifications/helpdesk/entrypoint";
    }

}
