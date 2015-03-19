package org.fenixedu.ulisboa.specifications.ui;

import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/fenixedu-ulisboa-specifications")
@SpringApplication(group = "logged", path = "fenixedu-ulisboa-specifications", title = "title.FenixeduUlisboaSpecifications")
@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "title.FenixeduUlisboaSpecifications")
public class FenixeduUlisboaSpecificationsController {

    @RequestMapping
    public String home(Model model) {
        model.addAttribute("world", "World");
        return "fenixedu-ulisboa-specifications/home";
    }

}
