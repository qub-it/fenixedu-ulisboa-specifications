package org.fenixedu.ulisboa.specifications.domain.ects;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GradingTableData {

    /**
     * GradingTableData - JSON structure holding a grade conversion table
     * - mark : internal grade representation
     * - ectsGrade : ects grade representation
     */

    private Gson gson = new Gson();
    private List<GradeConversion> table = new ArrayList<GradeConversion>();

    public String toJson() {
        Type type = new TypeToken<List<GradeConversion>>() {
        }.getType();
        return gson.toJson(table, type);
    }

    public static GradingTableData fromJson(String json) {
        GradingTableData data = new GradingTableData();
        Type type = new TypeToken<List<GradeConversion>>() {
        }.getType();
        data.table = data.gson.fromJson(json, type);
        return data;
    }

    public List<GradeConversion> getTable() {
        return table;
    }

    public void setTable(List<GradeConversion> table) {
        this.table = table;
    }

    static public class GradeConversion {
        private String mark;
        private String ectsGrade;

        public GradeConversion(String mark, String ectsGrade) {
            this.mark = mark;
            this.ectsGrade = ectsGrade;
        }

        public String getMark() {
            return mark;
        }

        public void setMark(final String mark) {
            this.mark = mark;
        }

        public String getEctsGrade() {
            return ectsGrade;
        }

        public void setEctsGrade(final String ectsGrade) {
            this.ectsGrade = ectsGrade;
        }
    }
}
