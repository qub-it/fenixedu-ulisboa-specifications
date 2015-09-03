package org.fenixedu.ulisboa.specifications.ui.enrolmentRedirects;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.web.bind.annotation.RequestMapping;

import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;

@RequestMapping("/courseEnrolmentRedirect")
@SpringFunctionality(app = EnrolmentRedirects.class, title = "label.title.courseEnrolmentRedirect",
        accessGroup = "activeStudents")
public class ReturnToStudentPortal {

    @RequestMapping
    public String redirect(HttpServletRequest request) {
        String url = "/student/studentEnrollmentManagement.do?method=prepare";
        String injectChecksumInUrl =
                GenericChecksumRewriter.injectChecksumInUrl(request.getContextPath(), url, request.getSession());
        return "redirect:" + injectChecksumInUrl;
    }
}
