package org.fenixedu.ulisboa.specifications.scripts.legal.raides;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.RaidesInstance;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.mapping.BranchMappingType;

public class CreateRamosMapping extends CustomTask {

    @Override
    public void runTask() throws Exception {
        BranchMappingType.getInstance().createMapping(RaidesInstance.getInstance());
    }

}
