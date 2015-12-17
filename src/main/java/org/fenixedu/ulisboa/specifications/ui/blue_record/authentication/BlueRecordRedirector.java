package org.fenixedu.ulisboa.specifications.ui.blue_record.authentication;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.ulisboa.specifications.authentication.IULisboaRedirectionHandler;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.Raides;
import org.fenixedu.ulisboa.specifications.ui.blue_record.BlueRecordEntryPoint;

public class BlueRecordRedirector implements IULisboaRedirectionHandler {

    @Override
    public boolean isToRedirect(final User user, final HttpServletRequest request) {
        if(user.getPerson().getStudent() == null) {
            return false;
        }
        
        return !Raides.findActiveRegistrationsWithEnrolments(AccessControl.getPerson().getStudent()).isEmpty();
    }

    @Override
    public String redirectionPath(final User user, final HttpServletRequest request) {
        return BlueRecordEntryPoint.CONTROLLER_URL;
    }

}
