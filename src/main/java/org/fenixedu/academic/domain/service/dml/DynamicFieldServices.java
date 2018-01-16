package org.fenixedu.academic.domain.service.dml;

import java.util.LinkedHashSet;
import java.util.Set;

import org.fenixedu.academic.domain.dml.DynamicField;
import org.fenixedu.academic.domain.dml.DynamicFieldDescriptor;

import pt.ist.fenixframework.DomainObject;

abstract public class DynamicFieldServices {

    static public Set<DynamicField> findOrCreateFields(final DomainObject domainObject) {
        final Set<DynamicField> result = new LinkedHashSet<>();

        DynamicFieldDescriptor.find(domainObject).stream().sorted((x, y) -> Integer.compare(x.getOrder(), y.getOrder()))
                .forEach(descriptor -> result.add(DynamicField.create(domainObject, descriptor)));

        return result;
    }

}
