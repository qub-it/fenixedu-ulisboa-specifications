package org.fenixedu.ulisboa.specifications.domain.ects;

import java.util.stream.Stream;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.core.domain.Bennu;

public class InstitutionGradingTable extends InstitutionGradingTable_Base {

    private InstitutionGradingTable() {
        super();
    }

    public static Stream<InstitutionGradingTable> findAll() {
        return Bennu.getInstance().getGradingTablesSet().stream().filter(InstitutionGradingTable.class::isInstance)
                .map(InstitutionGradingTable.class::cast);
    }

    public static InstitutionGradingTable find(final ExecutionYear ey) {
        return findAll().filter(igt -> igt.getExecutionYear() == ey).findAny().orElse(null);
    }

    public static InstitutionGradingTable generate(final ExecutionYear executionYear) {
        if (find(executionYear) != null) {
            return null;
        }
        InstitutionGradingTable table = new InstitutionGradingTable();
        table.setExecutionYear(executionYear);
        table.setData(new GradingTableData());
        compileData(table);
        return table;
    }

}
