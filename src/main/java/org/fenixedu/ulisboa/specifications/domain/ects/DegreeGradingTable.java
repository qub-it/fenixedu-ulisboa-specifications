package org.fenixedu.ulisboa.specifications.domain.ects;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.bennu.core.domain.Bennu;

public class DegreeGradingTable extends DegreeGradingTable_Base {

    public DegreeGradingTable() {
        super();
    }

    @Override
    public void delete() {
        setDegree(null);
        setProgramConclusion(null);
        setRegistration(null);
        super.delete();
    }

    public static Stream<DegreeGradingTable> findAll() {
        return Bennu.getInstance().getGradingTablesSet().stream().filter(DegreeGradingTable.class::isInstance)
                .map(DegreeGradingTable.class::cast);
    }

    public static Set<DegreeGradingTable> find(final ExecutionYear ey) {
        return findAll().filter(dgt -> dgt.getExecutionYear() == ey).collect(Collectors.toSet());
    }

    public static DegreeGradingTable find(final ExecutionYear ey, final ProgramConclusion pc, final Degree d) {
        return findAll().filter(dgt -> dgt.getExecutionYear() == ey).filter(dgt -> dgt.getProgramConclusion() == pc)
                .filter(dgt -> dgt.getDegree() == d).findAny().orElse(null);
    }

    public static Set<DegreeGradingTable> generate(final ExecutionYear executionYear) {
        Set<DegreeGradingTable> allTables = new HashSet<DegreeGradingTable>();
        for (DegreeCurricularPlan dcp : executionYear.getDegreeCurricularPlans()) {
            Degree degree = dcp.getDegree();
            for (ProgramConclusion programConclusion : ProgramConclusion.conclusionsFor(dcp).collect(Collectors.toSet())) {
                DegreeGradingTable table = find(executionYear, programConclusion, degree);
                if (table == null) {
                    table = new DegreeGradingTable();
                    table.setExecutionYear(executionYear);
                    table.setProgramConclusion(programConclusion);
                    table.setDegree(degree);
                    table.compileData();
                }
                allTables.add(table);
            }
        }
        return allTables;
    }

    @Override
    public void compileData() {
        GradingTableData tableData = new GradingTableData();
        GradingTableSettings.defaultData(this);
    }
}
