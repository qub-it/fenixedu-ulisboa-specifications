package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import java.util.Locale;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityActivityType;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityProgramType;

public class CreateMobilityProgramAndActivity extends CustomTask {
    
    private static final Locale PT = new Locale("PT");
    private static final Locale EN = new Locale("EN");
    
    @Override
    public void runTask() throws Exception {
        
        MobilityActivityType.create("1", new LocalizedString(PT, "Mobilidade de estudo"), true);
        MobilityActivityType.create("2", new LocalizedString(PT, "Mobilidade de estágio"), true);
        
        MobilityProgramType.create("ERASMUS", new LocalizedString(PT, "Erasmus"), true);
        
    }

}
