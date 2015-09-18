package org.fenixedu.ulisboa.specifications.domain;

import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.ulisboa.specifications.accessControl.SpecialIngressionsAndOtherCyclesGroup;

@Deprecated
public class SpecialIngressionsAndOtherCyclesPersistentGroup extends SpecialIngressionsAndOtherCyclesPersistentGroup_Base {

    public SpecialIngressionsAndOtherCyclesPersistentGroup() {
        super();
    }

    @Override
    public Group toGroup() {
        return Group.parse(SpecialIngressionsAndOtherCyclesGroup.GROUP_OPERATOR);
    }

    public static SpecialIngressionsAndOtherCyclesPersistentGroup getInstance() {
        return singleton(() -> find(SpecialIngressionsAndOtherCyclesPersistentGroup.class),
                () -> new SpecialIngressionsAndOtherCyclesPersistentGroup());
    }
}
