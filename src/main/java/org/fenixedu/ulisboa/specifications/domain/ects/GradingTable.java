package org.fenixedu.ulisboa.specifications.domain.ects;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.domain.ects.GradingTableData.GradeConversion;

abstract public class GradingTable extends GradingTable_Base {

    public GradingTable() {
        super();
        setBennu(Bennu.getInstance());
    }

    public void delete() {
        setExecutionYear(null);
        setBennu(null);
        deleteDomainObject();
    }

    //Private methods
    private GradeConversion find(final String mark) {
        return getData().getTable().stream().filter((gc -> gc.getMark().equals(mark))).findFirst().orElse(null);
    }

    // Public API - getEctsGrade
    public String getEctsGrade(final String mark) {
        GradeConversion gc = find(mark);
        return gc != null ? gc.getEctsGrade() : null;
    }

    public String getEctsGrade(final int mark) {
        return getEctsGrade(String.valueOf(mark));
    }

    public String getEctsGrade(final BigDecimal mark) {
        return getEctsGrade(mark.toString());
    }

    // Public API - setGrade
    public void setEctsGrade(final String mark, final String ectsGrade) {
        GradeConversion gc = find(mark);
        if (gc != null) {
            gc.setEctsGrade(ectsGrade);
        }
    }

    public void setGrade(final int mark, final String ectsGrade) {
        setEctsGrade(String.valueOf(mark), ectsGrade);
    }

    public void setGrade(final BigDecimal mark, final String ectsGrade) {
        setEctsGrade(mark.toString(), ectsGrade);
    }

    // Public API - hasMark
    public boolean hasMark(final String mark) {
        return find(mark) != null;
    }

    public boolean hasMark(final int mark) {
        return hasMark(String.valueOf(mark));
    }

    public boolean hasMark(final BigDecimal mark) {
        return hasMark(mark.toString());
    }

    // Public API - addMark
    public void addMark(final String mark, final String ectsGrade, int order) {
        GradeConversion gc = new GradeConversion(mark, ectsGrade);
        getData().getTable().add(order, gc);
    }

    public void addMark(final String mark, final String grade) {
        addMark(mark, grade, getData().getTable().size());
    }

    public void addMark(final int mark, final String ectsGrade, int order) {
        addMark(String.valueOf(mark), ectsGrade, order);
    }

    public void addMark(final int mark, final String ectsGrade) {
        addMark(mark, ectsGrade, getData().getTable().size());
    }

    public void addMark(final BigDecimal mark, final String ectsGrade, int order) {
        addMark(mark.toString(), ectsGrade, order);
    }

    public void addMark(final BigDecimal mark, final String ectsGrade) {
        addMark(mark, ectsGrade, getData().getTable().size());
    }

    // Public API - getEctsGrades
    public List<GradeConversion> getEctsGrades() {
        return new ArrayList<GradeConversion>(getData().getTable());
    }

    // Dummy harvest algortithm
    protected static void compileData(GradingTable table) {
        table.addMark("10", "E");
        table.addMark("11", "E");
        table.addMark("12", "D");
        table.addMark("13", "D");
        table.addMark("14", "C");
        table.addMark("15", "C");
        table.addMark("16", "B");
        table.addMark("17", "B");
        table.addMark("18", "A");
        table.addMark("19", "A");
        table.addMark("20", "A");
    }

}
