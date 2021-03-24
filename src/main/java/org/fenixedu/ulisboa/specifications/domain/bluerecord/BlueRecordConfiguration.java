package org.fenixedu.ulisboa.specifications.domain.bluerecord;

import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class BlueRecordConfiguration extends BlueRecordConfiguration_Base {

    public BlueRecordConfiguration() {
        super();
        setBennu(Bennu.getInstance());
    }

    public void delete() {
        getExclusiveDegreesSet().clear();
        setBennu(null);

        deleteDomainObject();
    }

    @Atomic(mode = TxMode.SPECULATIVE_READ)
    public static BlueRecordConfiguration getInstance() {
        if (Bennu.getInstance().getBlueRecordConfiguration() == null) {
            return initialize();
        }
        return Bennu.getInstance().getBlueRecordConfiguration();
    }

    private static BlueRecordConfiguration initialize() {
        if (Bennu.getInstance().getBlueRecordConfiguration() == null) {
            return new BlueRecordConfiguration();
        }
        return Bennu.getInstance().getBlueRecordConfiguration();
    }

    @Override
    public Boolean getIsCgdFormToFill() {
        return isCgdFormToFill();
    }

    public Boolean isCgdFormToFill() {
        Boolean result = super.getIsCgdFormToFill();
        if (result == null) {
            return Boolean.FALSE;
        }
        return result;
    }

    @Override
    public Boolean getIsInactive() {
        return Boolean.TRUE.equals(super.getIsInactive());
    }

}
