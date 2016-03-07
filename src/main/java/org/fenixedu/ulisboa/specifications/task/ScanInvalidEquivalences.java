package org.fenixedu.ulisboa.specifications.task;

import org.fenixedu.academic.domain.studentCurriculum.Credits;
import org.fenixedu.academic.domain.studentCurriculum.CreditsDismissal;
import org.fenixedu.academic.domain.studentCurriculum.Dismissal;
import org.fenixedu.academic.domain.studentCurriculum.Equivalence;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;

public class ScanInvalidEquivalences extends CustomTask {

    @Override
    public void runTask() throws Exception {
        for (Credits credits : Bennu.getInstance().getCreditsSet()) {
            if (credits.isEquivalence()) {
                for (Dismissal dismissal : credits.getDismissalsSet()) {
                    if (dismissal instanceof CreditsDismissal) {
                        taskLog("Equivalence with no destination found: "
                                + dismissal.getRegistration().getStudent().getPerson().getUsername() + " "
                                + dismissal.getRegistration().getDegreeName() + " - "
                                + dismissal.getCurriculumGroup().getFullPath() + ":" + dismissal.getName().getContent());
                    }
                }
            }
        }
    }
}
