package org.fenixedu.ulisboa.specifications.domain.serviceRequests;

import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;

public enum UIComponentType {
    DROP_DOWN_ONE_VALUE, DROP_DOWN_MULTIPLE, DROP_DOWN_BOOLEAN, TEXT_LOCALIZED_STRING, TEXT, NUMBER, DATE;

    public boolean isSingleDropDown() {
        return this == DROP_DOWN_ONE_VALUE;
    }

    public boolean isMultipleDropDown() {
        return this == DROP_DOWN_MULTIPLE;
    }

    public boolean isBooleanDropDown() {
        return this == DROP_DOWN_BOOLEAN;
    }

    public boolean isLocalizedTextBox() {
        return this == TEXT_LOCALIZED_STRING;
    }

    public boolean isTextBox() {
        return this == TEXT;
    }

    public boolean isNumberBox() {
        return this == NUMBER;
    }

    public boolean isDateBox() {
        return this == DATE;
    }

    public boolean needDataSource() {
        return this == DROP_DOWN_MULTIPLE || this == DROP_DOWN_ONE_VALUE;
    }

    public LocalizedString getDescriptionI18N() {
        return BundleUtil.getLocalizedString(ULisboaConstants.BUNDLE, getClass().getSimpleName() + "." + name());
    }

}
