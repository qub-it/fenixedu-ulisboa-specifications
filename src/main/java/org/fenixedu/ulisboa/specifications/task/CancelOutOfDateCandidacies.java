package org.fenixedu.ulisboa.specifications.task;

import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;

@Task(englishTitle = "Cancel Out Of Date Candidacies", readOnly = true)
public class CancelOutOfDateCandidacies extends CronTask {
    @Override
    public void runTask() throws Exception {
        //do nothing . When possible delete it
    }

}
