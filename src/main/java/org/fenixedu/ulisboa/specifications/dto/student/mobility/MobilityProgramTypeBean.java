package org.fenixedu.ulisboa.specifications.dto.student.mobility;

import org.fenixedu.academic.domain.student.mobility.MobilityProgramType;
import org.fenixedu.bennu.IBean;
import org.fenixedu.commons.i18n.LocalizedString;

public class MobilityProgramTypeBean implements IBean {

    private String code;
    private LocalizedString name;
    private boolean active;

    public MobilityProgramTypeBean() {
        this.name = new LocalizedString();
        this.active = true;
    }

    public MobilityProgramTypeBean(final MobilityProgramType type) {
        this.code = type.getCode();
        this.name = type.getName();
        this.active = type.isActive();
    }

    /* *****************
     * GETTERS & SETTERS
     * *****************
     */

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalizedString getName() {
        return name;
    }

    public void setName(LocalizedString name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
