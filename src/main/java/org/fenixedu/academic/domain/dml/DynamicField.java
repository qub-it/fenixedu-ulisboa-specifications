package org.fenixedu.academic.domain.dml;

import java.util.LinkedHashSet;
import java.util.Set;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;

import com.google.common.base.Strings;

import jvstm.Atomic;
import pt.ist.fenixframework.DomainObject;

public class DynamicField extends DynamicField_Base {

    protected DynamicField() {
        super();
        setRoot(Bennu.getInstance());
    }

    @Atomic
    static public Set<DynamicField> findOrCreateFields(final DomainObject domainObject) {
        final Set<DynamicField> result = new LinkedHashSet<>();

        DynamicFieldDescriptor.find(domainObject).stream().sorted((x, y) -> Integer.compare(x.getOrder(), y.getOrder()))
                .forEach(descriptor -> {

                    DynamicField field = descriptor.findField(domainObject);
                    if (field == null) {
                        field = descriptor.createField(domainObject);
                    }

                    result.add(field);
                });

        return result;
    }

    @Atomic
    private DynamicField edit(final String value) {
        setValue(value);

        // checkRules
        if (isRequired() && Strings.isNullOrEmpty(getValue())) {
            throw new ULisboaSpecificationsDomainException("error.DynamicField.value.required");
        }

        return this;
    }

    private boolean isRequired() {
        return getDescriptor().getRequired();
    }

    public DynamicField edit(final LocalizedString value) {
        // TODO nadir
        return edit(value == null ? null : value.json().toString());
    }

}
