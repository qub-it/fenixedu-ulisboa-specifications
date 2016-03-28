package org.fenixedu.ulisboa.specifications.domain.ects.tasks;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.ects.GradingTableData;
import org.fenixedu.ulisboa.specifications.domain.ects.GradingTableData.GradeConversion;

public class TestGradingTableData extends CustomTask {

    @Override
    public void runTask() throws Exception {
        GradingTableData gradingTable = new GradingTableData();
        Set<GradeConversion> data = gradingTable.getTable();
        data.add(new GradeConversion("10", "E"));
        data.add(new GradeConversion("11", "E"));
        data.add(new GradeConversion("14", "C"));
        data.add(new GradeConversion("18", "A"));
        print(data);

        gradingTable.setTable(data);
        reorder(gradingTable);
        String json = gradingTable.toJson();
        print(json);

        GradingTableData deserialTable = GradingTableData.fromJson(json);
        print(deserialTable.getTable());
        deserialTable.getTable().add(new GradeConversion("9", "F"));
        deserialTable.getTable().add(new GradeConversion("20", "A"));
        print(deserialTable.getTable());

        String json2 = deserialTable.toJson();
        print(json2);

        taskLog("--Finished--");
    }

    private void print(final Set<GradeConversion> data) {
        taskLog("[");
        Iterator<GradeConversion> iter = data.iterator();
        for (int index = 0; index < data.size(); index++) {
            GradeConversion gc = iter.next();
            taskLog("\t" + index + ": {");
            taskLog("\t\t\"" + gc.getMark() + "\" : \"" + gc.getEctsGrade() + "\"");
            taskLog("\t}");
        }
        taskLog("]");
    }

    private void print(String json) {
        taskLog(json);
    }

    private void reorder(GradingTableData tableData) {
        Set<GradeConversion> table = new TreeSet<GradeConversion>(new Comparator<GradeConversion>() {
            @Override
            public int compare(GradeConversion gc1, GradeConversion gc2) {
                return gc2.getMark().compareTo(gc1.getMark());
            }
        });
        table.addAll(tableData.getTable());
        tableData.setTable(table);
    }

}
