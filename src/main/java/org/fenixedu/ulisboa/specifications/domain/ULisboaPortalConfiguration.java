package org.fenixedu.ulisboa.specifications.domain;

import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.ulisboa.specifications.ULisboaConfiguration;

/**
 * Class to bypass org.fenixedu.bennu.portal.servlet.PortalLayoutInjector limitations.
 * 
 * PortalLayoutInjector uses Pebble to inject certain attributes into an html template
 * All these attributes are hardcoded in the doFilter() method and there is no way to provide new fields to the html,
 * limiting the portals of all applications to whatever vanilla bennu believes is required to all applications and nothing else
 * 
 * To fix this problem we need to enter in creative mode:
 * Besides accessing the value of attributes, we can also navigate through theirs getters (as in jsps)
 * meaning that if we are able to "extend" one of the injected objects, we can make it return one object controlled by us,
 * and then, our desired properties
 * 
 * The best way to do this is by adding a domain relation to any domain object being injected in the html
 * and the best candidate is config, the only instance of org.fenixedu.bennu.portal.domain.PortalConfiguration
 * 
 * We added a one-to-one relation between PortalConfiguration and ULisboaPortalConfiguration allowing the html template to acess
 * this configuration through config.ulisboaPortal
 *
 * By adding methods to this class we can simulate the injection of new properties
 * 
 * TL;DR:
 * Put getter here, portal html pebble template will be able to reach it through config.ulisboaPortal.<yourGetterWithoutGetPart>
 * 
 * @author Nuno Pinheiro
 *
 */

public class ULisboaPortalConfiguration extends ULisboaPortalConfiguration_Base {

    public ULisboaPortalConfiguration() {
        super();
    }

    public boolean isQualityMode() {
        Boolean qualityMode = ULisboaConfiguration.getConfiguration().isQualityMode();
        return qualityMode != null ? qualityMode : false;
    }

    public boolean getShowAllExecutionCourseFuncs() {
        Boolean showFuncs = ULisboaConfiguration.getConfiguration().getShowAllExecutionCourseFuncs();
        return showFuncs != null ? showFuncs : false;
    }

    public boolean isSupportActive() {
        Boolean supportActive = ULisboaConfiguration.getConfiguration().getSupportActive();
        return supportActive != null ? supportActive : false;
    }

    public boolean isDocumentationActive() {
        Boolean documentationActive = ULisboaConfiguration.getConfiguration().getDocumentationActive();
        return documentationActive != null ? documentationActive : false;
    }

    public boolean isSupportAccessibleToUser() {
        String supportAccessControlExpression = ULisboaConfiguration.getConfiguration().getSupportAccessControlExpression();
        return Group.parse(supportAccessControlExpression).isMember(AccessControl.getPerson().getUser());
    }
    
    @Override
    public Boolean getTeacherEvaluationSectionAvailable() {
        return super.getTeacherEvaluationSectionAvailable() == null || super.getTeacherEvaluationSectionAvailable();
    }
}
