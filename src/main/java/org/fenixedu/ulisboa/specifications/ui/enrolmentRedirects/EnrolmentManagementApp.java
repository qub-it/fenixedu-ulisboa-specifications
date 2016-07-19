package org.fenixedu.ulisboa.specifications.ui.enrolmentRedirects;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.springframework.web.bind.annotation.RequestMapping;

import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;

@RequestMapping("/fenixedu-ulisboa-specifications/enrolmentRedirects")
@SpringApplication(group = "activeStudents", path = "fenixedu-ulisboa-specifications", title = "label.title.enrolmentManagement",
        hint = "Student")
public class EnrolmentManagementApp {

    static protected String redirect(final String entryPointUrl) {
        return "redirect:" + entryPointUrl;
    }

    static public String getStrutsEntryPointURL(final HttpServletRequest request, final String action) {
        return getStrutsURL(request, action, "prepare");
    }

    static public String getStrutsURL(final HttpServletRequest request, final String action, final String method) {
        final String url = action + "?method=" + method;

        if (request != null) {
            // we have the responsability to inject checksum
            return GenericChecksumRewriter.injectChecksumInUrl(request.getContextPath(), url, request.getSession());

        } else {
            // URL will be available somewhere in a soon to be parsed and injected JSP page
            return url;
        }
    }

}
