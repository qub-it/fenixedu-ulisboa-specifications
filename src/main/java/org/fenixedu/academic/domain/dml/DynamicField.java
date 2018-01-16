package org.fenixedu.academic.domain.dml;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;

import com.google.common.base.Strings;

import pt.ist.fenixframework.DomainObject;

public class DynamicField extends DynamicField_Base {

    protected DynamicField() {
        super();
        setRoot(Bennu.getInstance());
    }

    static public DynamicField create(final DomainObject domainObject, final DynamicFieldDescriptor descriptor) {
        final DynamicField result = new DynamicField();
        result.setDescriptor(descriptor);
        descriptor.setField(domainObject, result);
        return result;
    }

    private DynamicField edit(final String value) {
        setValue(value);

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
