package org.fenixedu.ulisboa.specifications.task;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.scheduler.custom.CustomTask;

public class CreateULisboaCentralServicesDynamicGroup extends CustomTask {

    @Override
    public void runTask() throws Exception {
        final User jlima = User.findByUsername("jose.lima");
        final User dmendes = User.findByUsername("dmendes");
        final DynamicGroup dynamicGroup = DynamicGroup.get("ulisboaCentralServices");
        dynamicGroup.mutator().changeGroup(dynamicGroup.underlyingGroup().grant(jlima));
        dynamicGroup.mutator().changeGroup(dynamicGroup.underlyingGroup().grant(dmendes));
    }

}
