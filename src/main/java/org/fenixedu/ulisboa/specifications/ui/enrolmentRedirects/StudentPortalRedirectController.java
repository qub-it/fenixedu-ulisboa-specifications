package org.fenixedu.ulisboa.specifications.ui.enrolmentRedirects;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentStep;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/returnToStudentPortal")
@SpringFunctionality(app = EnrolmentManagementApp.class, title = "label.title.returnToStudentPortal",
        accessGroup = "activeStudents & !candidate")
public class StudentPortalRedirectController {

    static final private String MAPPING = "/student/viewStudentCurriculum";
    static final private String ACTION = MAPPING + ".do";

    @RequestMapping
    public String redirect(final HttpServletRequest request) {
        return EnrolmentManagementApp.redirect(

                EnrolmentStep.prepareURL(request, getEntryPointURL())

        );
    }

    static public String getEntryPointURL() {
        return ACTION;
    }

}
