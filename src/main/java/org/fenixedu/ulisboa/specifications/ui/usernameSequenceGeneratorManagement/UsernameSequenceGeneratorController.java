package org.fenixedu.ulisboa.specifications.ui.usernameSequenceGeneratorManagement;

import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot;
import org.fenixedu.ulisboa.specifications.domain.UsernameSequenceGenerator;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class,
        title = "label.title.usernameSequenceGeneratorManagement", accessGroup = "#managers")
@RequestMapping("/fenixedu-ulisboa-specifications/usernamesequencegeneratormanagement/usernamesequencegenerator")
public class UsernameSequenceGeneratorController extends FenixeduUlisboaSpecificationsBaseController {

    @RequestMapping
    public String home(Model model) {
        return "forward:/fenixedu-ulisboa-specifications/usernamesequencegeneratormanagement/usernamesequencegenerator/read";
    }

    private UsernameSequenceGenerator getUsernameSequenceGenerator(Model m) {
        return (UsernameSequenceGenerator) m.asMap().get("usernameSequenceGenerator");
    }

    private void setUsernameSequenceGenerator(UsernameSequenceGenerator usernameSequenceGenerator, Model m) {
        m.addAttribute("usernameSequenceGenerator", usernameSequenceGenerator);
    }

    @RequestMapping(value = "/read")
    public String read(Model model) {
        setUsernameSequenceGenerator(ULisboaSpecificationsRoot.getInstance().getUsernameSequenceGenerator(), model);
        return "fenixedu-ulisboa-specifications/usernamesequencegeneratormanagement/usernamesequencegenerator/read";
    }

    @RequestMapping(value = "/update/{oid}", method = RequestMethod.GET)
    public String update(@PathVariable("oid") UsernameSequenceGenerator usernameSequenceGenerator, Model model) {
        setUsernameSequenceGenerator(usernameSequenceGenerator, model);
        return "fenixedu-ulisboa-specifications/usernamesequencegeneratormanagement/usernamesequencegenerator/update";
    }

    @RequestMapping(value = "/update/{oid}", method = RequestMethod.POST)
    public String update(@PathVariable("oid") UsernameSequenceGenerator usernameSequenceGenerator, @RequestParam(
            value = "prefix", required = false) java.lang.String prefix,
            @RequestParam(value = "currentvalue", required = false) java.lang.Integer currentValue, Model model) {

        setUsernameSequenceGenerator(usernameSequenceGenerator, model);
        updateUsernameSequenceGenerator(prefix, currentValue, model);

        return "redirect:/fenixedu-ulisboa-specifications/usernamesequencegeneratormanagement/usernamesequencegenerator/read/"
                + getUsernameSequenceGenerator(model).getExternalId();

    }

    @Atomic
    public void updateUsernameSequenceGenerator(java.lang.String prefix, java.lang.Integer currentValue, Model m) {
        UsernameSequenceGenerator usernameSequenceGenerator = getUsernameSequenceGenerator(m);
        usernameSequenceGenerator.setPrefix(prefix);
        usernameSequenceGenerator.setCurrentValue(currentValue);
    }

}
