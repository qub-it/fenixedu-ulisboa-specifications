package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance;

public class ClearDefaultDegreesToReport extends CustomTask {

    @Override
    public void runTask() throws Exception {
        RaidesInstance.getInstance().getDegreesToReportSet().clear();
    }

}
