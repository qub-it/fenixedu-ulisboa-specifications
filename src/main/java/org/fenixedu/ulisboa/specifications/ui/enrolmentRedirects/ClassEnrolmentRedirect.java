package org.fenixedu.ulisboa.specifications.ui.enrolmentRedirects;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.web.bind.annotation.RequestMapping;

import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;

@RequestMapping("/classEnrolmentRedirect")
@SpringFunctionality(app = EnrolmentRedirects.class, title = "label.title.classEnrolmentRedirect", accessGroup = "activeStudents")
public class ClassEnrolmentRedirect {

    @RequestMapping
    public String redirect(HttpServletRequest request) {
        String url = "/student/schoolClassStudentEnrollment.do?method=prepare";
        String injectChecksumInUrl =
                GenericChecksumRewriter.injectChecksumInUrl(request.getContextPath(), url, request.getSession());
        return "redirect:" + injectChecksumInUrl;
    }
}
