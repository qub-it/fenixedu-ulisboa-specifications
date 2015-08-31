package org.fenixedu.ulisboa.specifications.ui.enrolmentRedirects;

import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/fenixedu-ulisboa-specifications/enrolmentRedirects")
@SpringApplication(group = "activeStudents", path = "fenixedu-ulisboa-specifications",
        title = "title.FenixeduUlisboaSpecifications.enrolmentRedirects")
public class EnrolmentRedirects {

}
