package org.fenixedu.ulisboa.specifications.domain.student.mobility;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;

public class MobilityProgrammeLevel extends MobilityProgrammeLevel_Base {
    
    private MobilityProgrammeLevel() {
        super();
        setBennu(Bennu.getInstance());
    }
    
    protected MobilityProgrammeLevel(final String code, final LocalizedString name) {
        this();
        
        setCode(code);
        setName(name);
        
        checkRules();
    }

    private void checkRules() {
        
    }
    
    public static MobilityProgrammeLevel create(final String code, final LocalizedString name) {
        return new MobilityProgrammeLevel(code, name);
    }
    
}
