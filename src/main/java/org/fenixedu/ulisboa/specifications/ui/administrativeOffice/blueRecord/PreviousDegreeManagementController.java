package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.blueRecord;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.PreviousDegreeOriginInformationFormController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.previousdegreeinformationmanagement",
accessGroup = "logged")
@RequestMapping(PreviousDegreeManagementController.CONTROLLER_URL)
public class PreviousDegreeManagementController extends PreviousDegreeOriginInformationFormController {

    
    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/previousdegreeinformationmanagement";
    private static final String JSP_PATH = "/fenixedu-ulisboa-specifications/previousdegreeinformationmanagement";

    private static final String _READ_URI = "/read";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(_READ_URI + "/{registrationId}")
    public String read(@PathVariable("registrationId") final Registration registration, final Model model) {
        final PreviousDegreeInformationForm form = createPreviousDegreeInformationForm(registration);
        
        model.addAttribute("registration", registration);
        model.addAttribute("previousDegreeInformationForm", form);
        
        return jspPage(_READ_URI);
    }
    
    private static final String _UPDATE_URI = "/update";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI + "/{registrationId}", method = RequestMethod.GET)
    public String update(@PathVariable("registrationId") final Registration registration, final Model model) {
        return _update(registration, createPreviousDegreeInformationForm(registration), model);
    }
    
    private String _update(final Registration registration, final PreviousDegreeInformationForm form, final Model model) {
        
        model.addAttribute("schoolLevelValues", schoolLevelTypeValues());
        model.addAttribute("countries", Bennu.getInstance().getCountrysSet());

        fillFormIfRequired(registration, model);
        
        return jspPage(_UPDATE_URI);
    }    
    
    @RequestMapping(value = _UPDATE_URI + "/{registrationId}", method = RequestMethod.POST)
    public String update(@PathVariable("registrationId") final Registration registration, final PreviousDegreeInformationForm form,
            final Model model) {
        try {
            if (!validate(registration, form, model)) {
                return _update(registration, form, model);
            }
            
            writeData(registration, form);

            return "redirect:"  + READ_URL + "/" + registration.getExternalId();
        } catch(final DomainException e) {
            addErrorMessage(e.getLocalizedMessage(), model);
        }

        return _update(registration, form, model);
    }
    
    
    @Override
    protected boolean isDegreeRequiredWhenNotDefaultCountryOrNotHigherLevel() {
        return false;
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page.substring(1, page.length());
    }

    /* ********************
     * MAPPINGS NOT APPLIED 
     * ********************
     */

    @Override
    public boolean isFormIsFilled(Student student) {
        throw new RuntimeException("not applied in this controller");
    }

    @Override
    protected String nextScreen(Model model, RedirectAttributes redirectAttributes) {
        throw new RuntimeException("not applied in this controller");
    }

    @Override
    protected Student getStudent(final Model model) {
        throw new RuntimeException("not applied in this controller");
    }

    @Override
    public String back(final Model model, final RedirectAttributes redirectAttributes) {
        throw new RuntimeException("not applied in this controller");
    }

}
