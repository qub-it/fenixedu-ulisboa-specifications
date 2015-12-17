package org.fenixedu.ulisboa.specifications.authentication;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.core.domain.User;

public interface IULisboaRedirectionHandler {

    public boolean isToRedirect(final User user, final HttpServletRequest request);
    
    public String redirectionPath(final User user, final HttpServletRequest request);
    
}
