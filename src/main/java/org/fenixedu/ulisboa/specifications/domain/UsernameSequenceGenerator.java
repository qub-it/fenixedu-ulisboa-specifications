package org.fenixedu.ulisboa.specifications.domain;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.User.UsernameGenerator;
import org.fenixedu.bennu.core.domain.UserProfile;

import pt.ist.fenixframework.Atomic;

public class UsernameSequenceGenerator extends UsernameSequenceGenerator_Base implements UsernameGenerator {

    private static final String USERNAME_DEFAULT_PREFIX = "bennu";

    public UsernameSequenceGenerator() {
        super();
        UsernameSequenceGenerator usernameSequenceGenerator =
                ULisboaSpecificationsRoot.getInstance().getUsernameSequenceGenerator();
        if (usernameSequenceGenerator != null && usernameSequenceGenerator != this) {
            throw new IllegalStateException("Can only exist one username sequence generator");
        }
        setULisboaSpecificationsRoot(ULisboaSpecificationsRoot.getInstance());
        setPrefix(USERNAME_DEFAULT_PREFIX);
        setCurrentValue(getLatestValue() + 1);
    }

    private Integer getLatestValue() {
        Integer maxValue = 0;
        for (User user : Bennu.getInstance().getUserSet()) {
            String username = user.getUsername();
            if (username.startsWith(USERNAME_DEFAULT_PREFIX) && !username.endsWith(USERNAME_DEFAULT_PREFIX)) {
                Integer valueOf = Integer.valueOf(username.substring(5));
                maxValue = Math.max(maxValue, valueOf);
            }
        }
        return maxValue;
    }

    public Integer getCurrentValueForDisplay() {
        return super.getCurrentValue();
    }

    @Override
    public void setCurrentValue(Integer currentValue) {
        super.setCurrentValue(currentValue);
    }

    @Atomic
    public Integer getNextSequenceNumber() {
        Integer currentValue = super.getCurrentValue();
        setCurrentValue(currentValue + 1);
        return currentValue;
    }

    @Override
    public String doGenerate(UserProfile parameter) {
        return getPrefix() + getNextSequenceNumber();
    }
}
