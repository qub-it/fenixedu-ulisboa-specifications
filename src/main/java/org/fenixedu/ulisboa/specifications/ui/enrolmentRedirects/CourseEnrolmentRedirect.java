package org.fenixedu.ulisboa.specifications.ui.enrolmentRedirects;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.springframework.web.bind.annotation.RequestMapping;

import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;

@RequestMapping("/returnToStudentPortal")
@SpringFunctionality(app = EnrolmentRedirects.class, title = "label.title.returnToStudentPortal",
        accessGroup = "activeStudents & !candidate")
public class CourseEnrolmentRedirect {

    @RequestMapping
    public String redirect(HttpServletRequest request) {
        String url = "/student/viewStudentCurriculum.do?method=prepare";
        String injectChecksumInUrl =
                GenericChecksumRewriter.injectChecksumInUrl(request.getContextPath(), url, request.getSession());
        return "redirect:" + injectChecksumInUrl;
    }
}
