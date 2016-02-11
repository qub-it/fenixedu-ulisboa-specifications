package org.fenixedu.ulisboa.specifications.domain.ects;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.CompetenceCourse;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.Enrolment;
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
        setData(tableData);
        List<BigDecimal> sample = harvestSample();
        if (sample != null) {
            GradingTableGenerator.generateTableData(this, sample);
        } else {
            InstitutionGradingTable.copyData(this);
            setCopied(true);
        }
    }

    private List<BigDecimal> harvestSample() {
        List<BigDecimal> sample = new ArrayList<BigDecimal>();
        int coveredYears = 0;
        boolean sampleOK = false;
        for (ExecutionYear year = getExecutionYear().getPreviousExecutionYear(); year != null; year =
                year.getPreviousExecutionYear()) {
            for (final CurricularCourse curricularCourse : getCompetenceCourse().getAssociatedCurricularCoursesSet()) {
                List<Enrolment> enrolmentsByExecutionYear = curricularCourse.getEnrolmentsByExecutionYear(year);
                for (Enrolment enrolment : enrolmentsByExecutionYear) {
                    if (!enrolment.isApproved()) {
                        continue;
                    }
//                    //dsimoes@03_02_2016: Porque tenho que filtrar só inscrições em planos concluidos??
//                    if (!isGraduated(enrolment)) {
//                        continue;
//                    }
//                    // dsimoes@03_02_2016: Estas grades existem mesmo e são mesmo para ignorar?
//                    // Test if grade is integer because some grades are decimal
//                    try {
//                        Integer.valueOf(enrolment.getGradeValue());
//                    } catch (final NumberFormatException e) {
//                        continue;
//                    }
                    if (enrolment.getFinalGrade() == null) {
                        continue;
                    }
                    sample.add(new BigDecimal(enrolment.getFinalGrade()));
                }
            }

            if (++coveredYears >= GradingTableSettings.getMinimumPastYears()
                    && sample.size() >= GradingTableSettings.getMinimumSampleSize()) {
                sampleOK = true;
                break;
            }
        }
        return sampleOK ? sample : null;
    }

}
