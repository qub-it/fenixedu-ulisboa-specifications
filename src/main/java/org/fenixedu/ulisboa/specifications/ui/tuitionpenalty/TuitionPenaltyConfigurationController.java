package org.fenixedu.ulisboa.specifications.ui.tuitionpenalty;

import java.util.stream.Collectors;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.tuitionpenalty.TuitionPenaltyConfiguration;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.TuitionPenaltyConfiguration.title",
        accessGroup = "treasuryManagers")
@RequestMapping(TuitionPenaltyConfigurationController.CONTROLLER_URL)
public class TuitionPenaltyConfigurationController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/tuitionpenaltyconfiguration";
    public static final String JSP_PATH = "fenixedu-ulisboa-specifications/tuitionpenaltyconfiguration";

    @RequestMapping
    public String home() {
        return "forward:" + READ_URL;
    }

    private static final String _READ_URI = "/read";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI, method = RequestMethod.GET)
    public String read(final Model model) {
        model.addAttribute("tuitionPenaltyConfiguration", TuitionPenaltyConfiguration.getInstance());

        return jspPage(_READ_URI);
    }

    private static final String _EDIT_URI = "/edit";
    public static final String EDIT_URL = CONTROLLER_URL + _EDIT_URI;

    @RequestMapping(value = _EDIT_URI, method = RequestMethod.GET)
    public String edit(final Model model) {
        return _edit(new TuitionPenaltyConfigurationBean(TuitionPenaltyConfiguration.getInstance()), model);
    }

    private String _edit(final TuitionPenaltyConfigurationBean bean, final Model model) {
        model.addAttribute("bean", bean);
        model.addAttribute("serviceRequestTypes", ServiceRequestType.findActive().sorted(ServiceRequestType.COMPARE_BY_CATEGORY_THEN_BY_NAME).collect(Collectors.toList()));
        model.addAttribute("serviceRequestSlots", ServiceRequestSlot.findAll().sorted(ServiceRequestSlot.COMPARE_BY_LABEL).collect(Collectors.toList()));
        
        return jspPage(_EDIT_URI);
    }

    @RequestMapping(value = _EDIT_URI, method = RequestMethod.POST)
    public String editpost(TuitionPenaltyConfigurationBean bean, final Model model) {
        try {
            TuitionPenaltyConfiguration.getInstance().edit(bean.getTuitionPenaltyServiceRequestType(),
                    bean.getTuitionInstallmentOrderSlot(), bean.getExecutionYearSlot());
            
            return "redirect:" + READ_URL;
        } catch (final DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return _edit(bean, model);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page.substring(1, page.length());
    }
}
