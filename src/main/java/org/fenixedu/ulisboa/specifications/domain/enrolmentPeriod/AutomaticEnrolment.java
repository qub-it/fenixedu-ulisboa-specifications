package org.fenixedu.ulisboa.specifications.domain.enrolmentPeriod;

import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;

public enum AutomaticEnrolment {

    NO, YES_EDITABLE, YES_UNEDITABLE;

    public boolean isAutomatic() {
        return this != NO;
    }

    public boolean isEditable() {
        return this != YES_UNEDITABLE;
    }

    public LocalizedString getDescriptionI18N() {
        return ULisboaSpecificationsUtil.bundleI18N(getClass().getSimpleName() + "." + name());
    }
}
