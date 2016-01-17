package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

public class ChangeRaidesInstanceName extends CustomTask {

    @Override
    public void runTask() throws Exception {
        RaidesInstance.getInstance().edit(new LocalizedString(ULisboaConstants.DEFAULT_LOCALE, "RAIDES"),
                RaidesInstance.getInstance().getGroup(), true, true);
    }

}
