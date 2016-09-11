package org.fenixedu.ulisboa.specifications.ui.enrolmentRedirects;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentStep;
import org.springframework.web.bind.annotation.RequestMapping;

import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;

@RequestMapping("/returnToStudentPortal")
@SpringFunctionality(app = EnrolmentManagementApp.class, title = "label.title.returnToStudentPortal",
        accessGroup = "activeStudents & !candidate")
public class StudentPortalRedirectController {

    static final private String MAPPING = "/student/viewStudentCurriculum";
    static final private String ACTION = MAPPING + ".do";

    @RequestMapping
    public String redirect(final HttpServletRequest request) {
        return EnrolmentManagementApp.redirect(

                GenericChecksumRewriter.injectChecksumInUrl(request.getContextPath(), getEntryPointURL() + "?method=prepare",
                        request.getSession())

        );
    }

    static public String getEntryPointURL() {
        return ACTION;
    }

}
