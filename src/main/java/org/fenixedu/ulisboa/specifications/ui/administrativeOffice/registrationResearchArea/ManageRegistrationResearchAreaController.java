package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.registrationResearchArea;

import java.util.List;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.student.ResearchArea;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;

import edu.emory.mathcs.backport.java.util.Collections;
import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.manageRegistrationResearchArea",
        accessGroup = "logged")
@RequestMapping(ManageRegistrationResearchAreaController.CONTROLLER_URL)
public class ManageRegistrationResearchAreaController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/manageregistrationresearcharea";
    public static final String JSP_PATH = "fenixedu-ulisboa-specifications/manageregistrationresearcharea";

    private static final String EDIT_URI = "/edit";
    public static final String EDIT_URL = CONTROLLER_URL + EDIT_URI;

    @RequestMapping(value = EDIT_URI + "/{registrationId}", method = RequestMethod.GET)
    public String edit(@PathVariable("registrationId") final Registration registration, final Model model) {
        model.addAttribute("registration", registration);
        model.addAttribute("researchArea", registration.getResearchArea());
        List<ResearchArea> researchAreaSet = Lists.newArrayList(Bennu.getInstance().getResearchAreasSet());
        
        Collections.sort(researchAreaSet, ResearchArea.COMPARATOR_BY_CODE);
        model.addAttribute("researchAreaSet", researchAreaSet);
        
        return jspPage(EDIT_URI);
    }

    @RequestMapping(value=EDIT_URI + "/{registrationId}", method=RequestMethod.POST)
    public String editpost(@PathVariable("registrationId") final Registration registration, 
            @RequestParam("researchArea") final ResearchArea researchArea, final Model model) {
        
        try {
            
            editResearchArea(registration, researchArea);
            
            addInfoMessage(ULisboaSpecificationsUtil.bundle("label.ManageRegistrationResearchArea.edition.success"), model);
            
            return String.format("redirect:%s/%s", EDIT_URL, registration.getExternalId());  
        } catch(final ULisboaSpecificationsDomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }
        
        return edit(registration, model);
    }

    @Atomic
    private void editResearchArea(final Registration registration, final ResearchArea researchArea) {
        registration.setResearchArea(researchArea);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page.substring(1, page.length());
    }
}
