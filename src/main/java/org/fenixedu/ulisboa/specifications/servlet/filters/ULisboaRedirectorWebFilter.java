package org.fenixedu.ulisboa.specifications.servlet.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.servlet.AuthenticationRedirector;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;

public class ULisboaRedirectorWebFilter implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        final String path = httpServletRequest.getRequestURI();

        if (path.endsWith(".js") || path.endsWith(".css") || path.endsWith(".eot") || path.endsWith(".otf")
                || path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".gif")
                || path.endsWith("favicon") || path.endsWith("logo") || path.endsWith(".svg") || path.endsWith(".less")
                || path.endsWith(".ttf") || path.endsWith(".woff") || path.endsWith("/logout")
                || path.contains("/api/bennu-core")) {
            chain.doFilter(request, response);
            return;
        }

        if (path.contains("/fenixedu-ulisboa-specifications/firsttimecandidacy/autocompletes")) {
            chain.doFilter(request, response);
            return;
        }

        final User user = Authenticate.getUser();

        if (user != null) {
            final String redirectionPath = AuthenticationRedirector.getRedirectionPath(user, httpServletRequest);
            if (StringUtils.isNotBlank(redirectionPath) && !path.contains(redirectionPath)) {
                String contextPath = httpServletRequest.getContextPath();
                if (contextPath.endsWith("/")) {
                    httpServletResponse.sendRedirect(redirectionPath.substring(1));
                } else {
                    httpServletResponse.sendRedirect(contextPath + redirectionPath);
                }
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

}
