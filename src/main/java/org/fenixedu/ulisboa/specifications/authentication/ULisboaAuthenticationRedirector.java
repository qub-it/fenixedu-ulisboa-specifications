package org.fenixedu.ulisboa.specifications.authentication;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;

import com.google.common.collect.Lists;

public class ULisboaAuthenticationRedirector {

    private static List<IULisboaRedirectionHandler> handlers = Lists.newArrayList();
    
    public static void registerRedirectionHandler(final IULisboaRedirectionHandler handler) {
        synchronized(handlers) {
            handlers.add(handler);
        }
    }
    
    public static void unregisterRedirectionHandler(final IULisboaRedirectionHandler handler) {
        synchronized(handlers) {
            handlers.remove(handler);
        }
    }
    
    public static String getRedirectionPath(final User user, final HttpServletRequest request) {
        synchronized(handlers) {
            for (final IULisboaRedirectionHandler iuLisboaRedirectionHandler : handlers) {
                if(iuLisboaRedirectionHandler.isToRedirect(user, request)) {
                    return iuLisboaRedirectionHandler.redirectionPath(user, request);
                }
            }
        }
        
        return PortalConfiguration.getInstance().getMenu().getUserMenuStream().findFirst().get().getPath();
    }
    
}
