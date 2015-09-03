package org.fenixedu.ulisboa.specifications.ui.enrolmentRedirects;

import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/shiftEnrolmentRedirect")
@SpringFunctionality(app = EnrolmentRedirects.class, title = "label.title.shiftEnrolmentRedirect", accessGroup = "activeStudents")
public class ShiftEnrolmentRedirect {
    @RequestMapping
    public String redirect() {
        return "redirect:/student/shiftEnrolment";
    }
}
