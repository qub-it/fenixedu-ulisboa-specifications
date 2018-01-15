package org.fenixedu.academic.domain.person;

import java.util.Collection;

import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;

/**
 * 
 * @author shezad
 *
 */
public class JobType extends JobType_Base {

    public JobType() {
        super();
        setRoot(Bennu.getInstance());
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);
        if (!getJobsSet().isEmpty()) {
            blockers.add(BundleUtil.getString(Bundle.APPLICATION, "error.JobType.delete.jobsNotEmpty"));
        }
    }

    public void delete() {
        super.setRoot(null);
        super.deleteDomainObject();
    }

}
