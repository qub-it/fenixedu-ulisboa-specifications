package org.fenixedu.ulisboa.specifications.ui.student.enrolment.process;

import java.util.function.Predicate;

import org.fenixedu.commons.i18n.LocalizedString;

import com.google.common.base.Function;

public class EnrolmentStepTemplate extends EnrolmentStep {

    private Predicate<EnrolmentProcess> validator;
    private Function<EnrolmentProcess, String> argsSupplier;

    public EnrolmentStepTemplate(final LocalizedString description, final String entryPointURL,
            final Function<EnrolmentProcess, String> argsSupplier, final Predicate<EnrolmentProcess> validator) {

        super(description, entryPointURL);
        this.argsSupplier = argsSupplier;
        this.validator = validator;
    }

    @Override
    public String getEntryPointURL() {
        final String url = super.getEntryPointURL();
        return url + (getProcess() == null ? "" : (url.contains("?") ? "&" : "?") + argsSupplier.apply(getProcess()));
    }

    protected boolean appliesTo(final EnrolmentProcess input) {
        return validator.test(input);
    }

}
