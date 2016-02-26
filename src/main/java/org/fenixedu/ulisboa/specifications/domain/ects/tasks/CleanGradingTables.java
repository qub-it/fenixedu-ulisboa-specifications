package org.fenixedu.ulisboa.specifications.domain.ects.tasks;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;

public class CleanGradingTables extends CustomTask {

    @Override
    public void runTask() throws Exception {
        Bennu.getInstance().getGradingTablesSet().stream().forEach(gt -> gt.delete());
    }

}
