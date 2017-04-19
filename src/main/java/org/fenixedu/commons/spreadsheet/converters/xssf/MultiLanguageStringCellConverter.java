package org.fenixedu.commons.spreadsheet.converters.xssf;

import java.util.Locale;

import org.fenixedu.academic.util.MultiLanguageString;
import org.fenixedu.commons.spreadsheet.converters.CellConverter;

/**
 * Needed since MLS was deprecated and this converter was removed from fenixedu-commons
 */
public class MultiLanguageStringCellConverter implements CellConverter {

    private Locale locale = null;

    public MultiLanguageStringCellConverter() {
    }

    public MultiLanguageStringCellConverter(final Locale locale) {
        this.locale = locale;
    }

    @Override
    public Object convert(Object source) {

        if (source != null) {
            final MultiLanguageString value = (MultiLanguageString) source;
            return (locale != null) ? value.getContent(locale) : value.getContent();
        }

        return null;
    }

}
