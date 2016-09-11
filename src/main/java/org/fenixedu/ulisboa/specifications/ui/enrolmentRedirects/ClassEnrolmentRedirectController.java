package org.fenixedu.ulisboa.specifications.ui.enrolmentRedirects;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.SchoolClassStudentEnrollmentDA;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentStep;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/classEnrolmentRedirect")
@SpringFunctionality(app = EnrolmentManagementApp.class, title = "label.title.classEnrolmentRedirect",
        accessGroup = "activeStudents")
public class ClassEnrolmentRedirectController {

    @RequestMapping
    public String redirect(final HttpServletRequest request) {
        return EnrolmentManagementApp.redirect(

                EnrolmentStep.prepareURL(request, SchoolClassStudentEnrollmentDA.getEntryPointURL())

        );
    }

}
