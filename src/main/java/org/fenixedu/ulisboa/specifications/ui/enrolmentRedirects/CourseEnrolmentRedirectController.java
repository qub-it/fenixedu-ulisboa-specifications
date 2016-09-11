package org.fenixedu.ulisboa.specifications.ui.enrolmentRedirects;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.CourseEnrolmentDA;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentStep;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/courseEnrolmentRedirect")
@SpringFunctionality(app = EnrolmentManagementApp.class, title = "label.title.courseEnrolmentRedirect",
        accessGroup = "activeStudents")
public class CourseEnrolmentRedirectController {

    @RequestMapping
    public String redirect(final HttpServletRequest request) {
        return EnrolmentManagementApp.redirect(

                EnrolmentStep.prepareURL(request, CourseEnrolmentDA.getEntryPointURL())

        );
    }

}
