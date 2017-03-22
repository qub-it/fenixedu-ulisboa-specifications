package org.fenixedu.commons.spreadsheet;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

import org.fenixedu.commons.spreadsheet.converters.CellConverter;

public class SpreadsheetBuilderForXLSX extends SpreadsheetBuilder {

    private Map<String, SheetData<?>> getSheets() {
        return (Map<String, SheetData<?>>) getField("sheets");
    }

    private Map<Class<?>, CellConverter> getConverters() {
        return (Map<Class<?>, CellConverter>) getField("converters");
    }

    private Object getField(final String name) {
        Object result = null;

        try {
            final Field field = getClass().getSuperclass().getDeclaredField(name);
            field.setAccessible(true);
            result = field.get(this);
        } catch (final Throwable t) {
        }

        return result;
    }

    public void build(OutputStream output) throws IOException {

        DocxBuilder builder = new DocxBuilder();
        for (Entry<Class<?>, CellConverter> entry : getConverters().entrySet()) {
            builder.addConverter(entry.getKey(), entry.getValue());
        }
        builder.build(getSheets(), output);
    }

}
