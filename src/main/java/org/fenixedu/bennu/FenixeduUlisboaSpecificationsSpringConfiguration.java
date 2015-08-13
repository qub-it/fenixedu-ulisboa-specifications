package org.fenixedu.bennu;

import org.fenixedu.bennu.spring.BennuSpringModule;

@BennuSpringModule(basePackages = "org.fenixedu.ulisboa.specifications", bundles = "FenixeduUlisboaSpecificationsResources")
public class FenixeduUlisboaSpecificationsSpringConfiguration {
    public final static String BUNDLE = "resources/FenixeduUlisboaSpecificationsResources";
}
