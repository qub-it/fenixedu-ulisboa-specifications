package org.fenixedu.legalpt.ui.example;

import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.legalpt.ui.FenixeduLegalPTBaseController;
import org.fenixedu.legalpt.ui.FenixeduLegalPTController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@SpringFunctionality(app = FenixeduLegalPTController.class, title = "label.title.example", accessGroup = "#managers")
@RequestMapping(FenixeduLegalPTExampleController.CONTROLLER_URL)
public class FenixeduLegalPTExampleController extends FenixeduLegalPTBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-legal-pt/example";
    private static final String JSP_PATH = CONTROLLER_URL.substring(1);

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

    @RequestMapping
    public String home(Model model) {
        return "forward:" + CONTROLLER_URL + "/";
    }

    @RequestMapping(value = "/")
    public String showExample(Model model) {
        model.addAttribute("example", "Example message");
        return jspPage("showExample");
    }

    @RequestMapping(value = "/showExampleWithParams/{oid}/", method = RequestMethod.GET)
    public String showExampleWithParams(@PathVariable("oid") String oid, Model model) {
        return jspPage("showExample");
    }

    @RequestMapping(value = "/showExampleWithParams/{oid}/", method = RequestMethod.POST)
    public String showExampleWithParams(@PathVariable("oid") String oid, Model model,
            @RequestParam(value = "exampleParam", required = false) String exampleParam,
            final RedirectAttributes redirectAttributes) {
        return redirect(CONTROLLER_URL + "", model, redirectAttributes);
    }

}
