package org.fenixedu.ulisboa.specifications.ui.enrolmentRedirects;

import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.ShiftEnrolmentController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/shiftEnrolmentRedirect")
@SpringFunctionality(app = EnrolmentManagementApp.class, title = "label.title.shiftEnrolmentRedirect",
        accessGroup = "activeStudents")
public class ShiftEnrolmentRedirectController {

    @RequestMapping
    public String redirect() {
        return EnrolmentManagementApp.redirect(ShiftEnrolmentController.CONTROLLER_URL);
    }

}
