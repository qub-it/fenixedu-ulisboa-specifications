package org.fenixedu.ulisboa.specifications.task;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;

public class CleanAllExtendedDegreeInfos extends CustomTask {

    @Override
    public void runTask() throws Exception {
        Bennu.getInstance().getExtendedDegreeInfoSet().stream().forEach(edi -> {
            taskLog("----------------------------------");
            taskLog(edi.getDegreeInfo() != null ? edi.getDegreeInfo().getExecutionYear().getYear() : "..../....");
            taskLog(edi.getDegreeInfo() != null ? edi.getDegreeInfo().getDegree().getNameI18N().getContent() : ".........");
            taskLog("\n");
            //edi.delete();
            });
    }
}
