package org.fenixedu.ulisboa.specifications.domain;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;

public class CgdMod43Template extends CgdMod43Template_Base {

    public CgdMod43Template() {
        super();
    }

    @Override
    public boolean isAccessible(User user) {
        return Group.parse("#managers").isMember(user);
    }

}
