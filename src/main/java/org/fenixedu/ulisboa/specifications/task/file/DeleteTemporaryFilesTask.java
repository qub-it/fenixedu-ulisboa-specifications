package org.fenixedu.ulisboa.specifications.task.file;

import org.fenixedu.bennu.scheduler.CronTask;
import org.fenixedu.bennu.scheduler.annotation.Task;
import org.fenixedu.ulisboa.specifications.domain.file.ULisboaSpecificationsTemporaryFile;
import org.joda.time.DateTime;
import org.joda.time.Period;

@Task(englishTitle = "Delete persistent temporary files", readOnly = false)
public class DeleteTemporaryFilesTask extends CronTask {

    private static final int MAX_HOURS_TO_KEEP = 1;

    @Override
    public void runTask() throws Exception {
        ULisboaSpecificationsTemporaryFile.findAll()
                .filter(f -> new Period(f.getCreationDate(), new DateTime()).getHours() > MAX_HOURS_TO_KEEP)
                .forEach(f -> f.delete());

    }

}
