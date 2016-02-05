package org.fenixedu.ulisboa.specifications.domain.ects;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;

public class GradingTableSettings extends GradingTableSettings_Base {

    private static Integer MIN_SAMPLE_SIZE = 30;
    private static Integer MIN_PAST_YEARS = 3;

    private GradingTableSettings() {
        super();
        setBennu(Bennu.getInstance());
    }

    private GradingTableSettings(Integer minSampleSize, int minPastYears) {
        this();
        setMinSampleSize(minSampleSize);
        setMinPastYears(minPastYears);
    }

    @Atomic
    private static GradingTableSettings getInstance() {
        GradingTableSettings settings = Bennu.getInstance().getGradingTableSettings();
        if (settings == null) {
            settings = new GradingTableSettings(MIN_SAMPLE_SIZE, MIN_PAST_YEARS);
        }
        return settings;
    }

    public static int getMinimumSampleSize() {
        return getInstance().getMinSampleSize() != null ? getInstance().getMinSampleSize() : MIN_SAMPLE_SIZE;
    }

    public static void setMinimumSampleSize(int sampleSize) {
        getInstance().setMinSampleSize(sampleSize);
    }

    public static int getMinimumPastYears() {
        return getInstance().getMinPastYears() != null ? getInstance().getMinPastYears() : MIN_PAST_YEARS;
    }

    public static void setMinimumPastYears(int pastYears) {
        getInstance().setMinPastYears(pastYears);
    }

    public static void defaultData(GradingTable table) {
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

    public static Map<String, BigDecimal> getEctsAccumulativeDistro() {
        Map<String, BigDecimal> distro = new LinkedHashMap<String, BigDecimal>();
        distro.put("A", new BigDecimal("0.10"));
        distro.put("B", new BigDecimal("0.35"));
        distro.put("C", new BigDecimal("0.65"));
        distro.put("D", new BigDecimal("0.90"));
        distro.put("E", new BigDecimal("1.00"));
        return distro;
    }

    public static void generateTableData(GradingTable table, List<BigDecimal> sample) {
        Map<BigDecimal, Integer> gradeDistro = new LinkedHashMap<BigDecimal, Integer>();
        Map<BigDecimal, BigDecimal> heapedGradeDistro = new LinkedHashMap<BigDecimal, BigDecimal>();
        BigDecimal sampleSize = new BigDecimal(sample.size());
        gradeDistro.put(new BigDecimal("20.0"), 0);
        gradeDistro.put(new BigDecimal("19.0"), 0);
        gradeDistro.put(new BigDecimal("18.0"), 0);
        gradeDistro.put(new BigDecimal("17.0"), 0);
        gradeDistro.put(new BigDecimal("16.0"), 0);
        gradeDistro.put(new BigDecimal("15.0"), 0);
        gradeDistro.put(new BigDecimal("14.0"), 0);
        gradeDistro.put(new BigDecimal("13.0"), 0);
        gradeDistro.put(new BigDecimal("12.0"), 0);
        gradeDistro.put(new BigDecimal("11.0"), 0);
        gradeDistro.put(new BigDecimal("10.0"), 0);

        // 1. Grades distributions
        for (BigDecimal grade : sample) {
            gradeDistro.put(grade, (gradeDistro.get(grade) + 1));
        }

        // 2. Heaped grades distribution
        BigDecimal heap = BigDecimal.ZERO;
        for (Entry<BigDecimal, Integer> step : gradeDistro.entrySet()) {
            BigDecimal grade = step.getKey();
            BigDecimal count = new BigDecimal(step.getValue());
            BigDecimal share = count.divide(sampleSize, 5, BigDecimal.ROUND_HALF_EVEN);
            heap = heap.add(share);
            heapedGradeDistro.put(grade, heap);
        }

        // 3. Initial table fill ABCDEEEEEEE
        Map<BigDecimal, String> tableMap = new LinkedHashMap<BigDecimal, String>();
        tableMap.put(new BigDecimal("20.0"), "A");
        tableMap.put(new BigDecimal("19.0"), "B");
        tableMap.put(new BigDecimal("18.0"), "C");
        tableMap.put(new BigDecimal("17.0"), "D");
        tableMap.put(new BigDecimal("16.0"), "E");
        tableMap.put(new BigDecimal("15.0"), "E");
        tableMap.put(new BigDecimal("14.0"), "E");
        tableMap.put(new BigDecimal("13.0"), "E");
        tableMap.put(new BigDecimal("12.0"), "E");
        tableMap.put(new BigDecimal("11.0"), "E");
        tableMap.put(new BigDecimal("10.0"), "E");

        // 4. Shift according to distribution
        int gradesSize = tableMap.keySet().size();
        int ectsGradesSize = tableMap.values().stream().distinct().mapToInt(s -> 1).sum();
        int gradesCnt = 0;
        for (Entry<BigDecimal, BigDecimal> step : heapedGradeDistro.entrySet()) {
            BigDecimal grade = step.getKey();
            BigDecimal heapedShare = step.getValue();
            String ectsGrade = getEctsGrade(heapedShare);
            String defaultValue = tableMap.get(grade);
            gradesCnt++;

            if (ectsGrade.compareTo(defaultValue) < 0) {
                if ((gradesSize - gradesCnt) >= ectsGradesSize) {
                    boolean found = false;
                    String updateValue = "";
                    for (Entry<BigDecimal, String> entry : tableMap.entrySet()) {
                        if (entry.getKey().equals(grade)) {
                            found = true;
                            updateValue = entry.getValue();
                            entry.setValue(ectsGrade);
                        } else if (found) {
                            String passValue = entry.getValue();
                            entry.setValue(updateValue);
                            updateValue = passValue;
                        }
                    }
                }
            } else {
                ectsGradesSize--;
                continue;
            }
        }

        // 5. Populate the table with final values
        for (Entry<BigDecimal, String> entry : tableMap.entrySet()) {
            table.addMark(entry.getKey(), entry.getValue());
        }
    }

    private static String getEctsGrade(BigDecimal heapedDistro) {
        for (Entry<String, BigDecimal> interval : getEctsAccumulativeDistro().entrySet()) {
            if (heapedDistro.compareTo(interval.getValue()) < 1) {
                return interval.getKey();
            }
        }
        return null;
    }

}
