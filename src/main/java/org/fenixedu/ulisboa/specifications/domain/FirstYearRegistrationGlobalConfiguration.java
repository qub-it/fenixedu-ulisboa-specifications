package org.fenixedu.ulisboa.specifications.domain;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;

public class FirstYearRegistrationGlobalConfiguration extends FirstYearRegistrationGlobalConfiguration_Base {

    private FirstYearRegistrationGlobalConfiguration() {
        super();
        setBennu(Bennu.getInstance());
    }

    public void uploadMod43Template(final String filename, final byte[] content) {
        CgdMod43Template template = new CgdMod43Template(filename, content);
        cleanTemplate();
        setMod43Template(template);
    }

    public void cleanTemplate() {
        if (hasMod43Template()) {
            CgdMod43Template oldTemplate = getMod43Template();
            setMod43Template(null);
            oldTemplate.delete();
        }
    }

    public boolean hasMod43Template() {
        return getMod43Template() != null;
    }

    @Atomic
    public static FirstYearRegistrationGlobalConfiguration getInstance() {
        if (Bennu.getInstance().getFirstYearRegistrationGlobalConfigurationsSet().isEmpty()) {
            new FirstYearRegistrationGlobalConfiguration();
        }
        return Bennu.getInstance().getFirstYearRegistrationGlobalConfigurationsSet().iterator().next();
    }

    @Atomic
    public static void init() {
        FirstYearRegistrationGlobalConfiguration theGlobalConfiguration = getInstance();
        for (Degree degree : Bennu.getInstance().getDegreesSet()) {
            degree.getFirstYearRegistrationConfigurationsSet().forEach(c -> c.setGlobalConfiguration(theGlobalConfiguration));
        }
    }
}
