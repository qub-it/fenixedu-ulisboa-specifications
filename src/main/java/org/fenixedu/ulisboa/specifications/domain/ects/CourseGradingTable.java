package org.fenixedu.ulisboa.specifications.domain.ects;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.bennu.core.domain.Bennu;

public class CourseGradingTable extends CourseGradingTable_Base {

    public CourseGradingTable() {
        super();
    }

    @Override
    public void delete() {
        setCompetenceCourse(null);
        setCurriculumLines(null);
        super.delete();
    }

    public static Stream<CourseGradingTable> findAll() {
        return Bennu.getInstance().getGradingTablesSet().stream().filter(CourseGradingTable.class::isInstance)
                .map(CourseGradingTable.class::cast);
    }

    public static Set<CourseGradingTable> find(final ExecutionYear ey) {
        return findAll().filter(cgt -> cgt.getExecutionYear() == ey).collect(Collectors.toSet());
    }

    public static CourseGradingTable find(final ExecutionYear ey, final CompetenceCourse cc) {
        return findAll().filter(cgt -> cgt.getExecutionYear() == ey).filter(cgt -> cgt.getCompetenceCourse() == cc).findAny()
                .orElse(null);
    }

    public static Set<CourseGradingTable> generate(final ExecutionYear executionYear) {
        Set<CourseGradingTable> allTables = new HashSet<CourseGradingTable>();
        for (CompetenceCourse cc : Bennu.getInstance().getCompetenceCoursesSet()) {
            if (cc.hasActiveScopesInExecutionYear(executionYear)) {
                CourseGradingTable table = find(executionYear, cc);
                if (table == null) {
                    table = new CourseGradingTable();
                    table.setExecutionYear(executionYear);
                    table.setCompetenceCourse(cc);
                    table.setData(new GradingTableData());
                    compileData(table);
                }
                allTables.add(table);
            }
        }
        return allTables;
    }

}
