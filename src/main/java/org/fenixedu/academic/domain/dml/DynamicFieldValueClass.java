package org.fenixedu.academic.domain.dml;

import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.commons.i18n.LocalizedString;

import com.google.common.collect.Lists;

@SuppressWarnings("rawtypes")
public enum DynamicFieldValueClass {

    LOCALIZED_STRING(LocalizedString.class),

    // TODO add more
    ;

    private Class clazz;

    private DynamicFieldValueClass(final Class clazz) {
        this.clazz = clazz;
    }

    static public Set<Class> getSupported() {
        return Lists.newArrayList(values()).stream().map(i -> i.clazz).collect(Collectors.toSet());
    }

    static public boolean isSupported(final Class input) {
        return getSupported().contains(input);
    }

}
