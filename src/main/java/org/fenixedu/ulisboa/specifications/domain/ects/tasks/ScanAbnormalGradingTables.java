package org.fenixedu.ulisboa.specifications.domain.ects.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.ects.CourseGradingTable;
import org.fenixedu.ulisboa.specifications.domain.ects.DegreeGradingTable;
import org.fenixedu.ulisboa.specifications.domain.ects.GradingTable;
import org.fenixedu.ulisboa.specifications.domain.ects.InstitutionGradingTable;

public class ScanAbnormalGradingTables extends CustomTask {

    @Override
    public void runTask() throws Exception {
        Predicate<GradingTable> abnormality =
                (gt) -> {
                    List<String> uniqueEctsGrades =
                            gt.getEctsGrades().stream().map(gc -> gc.getEctsGrade()).distinct().collect(Collectors.toList());
                    return !uniqueEctsGrades.get(0).equals("E") || !uniqueEctsGrades.get(1).equals("D")
                            || !uniqueEctsGrades.get(2).equals("C") || !uniqueEctsGrades.get(3).equals("B")
                            || !uniqueEctsGrades.get(4).equals("A");
                };
        List<String> tables = new ArrayList<String>();
        InstitutionGradingTable.findAll().filter(abnormality)
                .forEach(igt -> tables.add("Institution: " + igt.getExecutionYear().getYear()));
        DegreeGradingTable
                .findAll()
                .filter(abnormality)
                .forEach(
                        dgt -> tables.add("Degrees: " + dgt.getExecutionYear().getYear() + " - "
                                + dgt.getDegree().getPresentationName() + ":" + dgt.getDegree().getExternalId() + " ["
                                + dgt.getProgramConclusion().getName().getContent() + ":"
                                + dgt.getProgramConclusion().getExternalId() + "]"));
        CourseGradingTable
                .findAll()
                .filter(abnormality)
                .forEach(
                        cgt -> tables.add("Courses: " + cgt.getExecutionYear().getYear() + " - "
                                + cgt.getCompetenceCourse().getName() + ":" + cgt.getCompetenceCourse().getExternalId()));
        tables.stream().forEach(s -> taskLog(s));
    }

}
