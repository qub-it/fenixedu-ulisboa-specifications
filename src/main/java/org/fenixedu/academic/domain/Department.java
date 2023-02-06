package org.fenixedu.academic.domain;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

public class Department extends Department_Base {

    public Department() {
        super();
    }

    public String getAcronym() {
        return StringUtils.isNotBlank(getCode()) ? getCode() : WordUtils.initials(getName()).replaceAll("[a-z]", "");
    }

}
