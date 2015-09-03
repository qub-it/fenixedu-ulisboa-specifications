package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.util;

public class UnitBean {
    String unitExternalId;
    String unitName;

    public UnitBean(String unitExternalId, String unitName) {
        super();
        this.unitExternalId = unitExternalId;
        this.unitName = unitName;
    }

    public String getUnitExternalId() {
        return unitExternalId;
    }

    public String getUnitName() {
        return unitName;
    }
}