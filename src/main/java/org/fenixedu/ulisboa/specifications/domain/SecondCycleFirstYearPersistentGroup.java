package org.fenixedu.ulisboa.specifications.domain;

import org.fenixedu.bennu.core.domain.BennuGroupIndex;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.ulisboa.specifications.accessControl.SecondCycleFirstYearGroup;

@Deprecated
public class SecondCycleFirstYearPersistentGroup extends SecondCycleFirstYearPersistentGroup_Base {

    public SecondCycleFirstYearPersistentGroup() {
        super();
    }

    @Override
    public Group toGroup() {
        return Group.parse(SecondCycleFirstYearGroup.GROUP_OPERATOR);
    }

    public static SecondCycleFirstYearPersistentGroup getInstance() {
        return singleton(() -> BennuGroupIndex.groupConstant(SecondCycleFirstYearPersistentGroup.class).findAny(),
                () -> new SecondCycleFirstYearPersistentGroup());
    }
}
