package org.fenixedu.ulisboa.specifications.domain.ects.tasks;

import java.util.List;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.ects.GradingTableData;
import org.fenixedu.ulisboa.specifications.domain.ects.GradingTableData.GradeConversion;

public class TestGradingTableData extends CustomTask {

    @Override
    public void runTask() throws Exception {
        GradingTableData gradingTable = new GradingTableData();
        List<GradeConversion> data = gradingTable.getTable();
        data.add(new GradeConversion("10", "E"));
        data.add(new GradeConversion("11", "E"));
        data.add(new GradeConversion("14", "C"));
        data.add(new GradeConversion("18", "A"));
        print(data);

        gradingTable.setTable(data);
        String json = gradingTable.toJson();
        print(json);

        GradingTableData deserialTable = GradingTableData.fromJson(json);
        print(deserialTable.getTable());

        String json2 = deserialTable.toJson();
        print(json2);

        taskLog("--Finished--");
    }

    private void print(final List<GradeConversion> data) {
        taskLog("[");
        for (int index = 0; index < data.size(); index++) {
            taskLog("\t" + index + ": {");
            taskLog("\t\t\"" + data.get(index).getMark() + "\" : \"" + data.get(index).getEctsGrade() + "\"");
            taskLog("\t}");
        }
        taskLog("]");
    }

    private void print(String json) {
        taskLog(json);
    }

}
