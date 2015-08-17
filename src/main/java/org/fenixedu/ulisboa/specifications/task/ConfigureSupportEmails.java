package org.fenixedu.ulisboa.specifications.task;

import org.fenixedu.academic.domain.Installation;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.helpdesk.HelpdeskConfigurations;

public class ConfigureSupportEmails extends CustomTask {

    @Override
    public void runTask() throws Exception {
        HelpdeskConfigurations.getInstance().addBCC("support@qub-it.com");
        
        /*FMV*/Installation.getInstance().setAcademicEmailAddress("secretaria@fmv.ulisboa.pt");
//*FF */ Installation.getInstance().setAcademicEmailAddress("academicos@ff.ulisboa.pt");
//*FMD*/ Installation.getInstance().setAcademicEmailAddress("secretaria@fmd.ulisboa.pt");
//*FL */ Installation.getInstance().setAcademicEmailAddress("sa.graduados@letras.ulisboa.pt");
//*RUL*/ Installation.getInstance().setAcademicEmailAddress("academicos@ulisboa.pt");
    }

}
