package org.fenixedu.ulisboa.specifications.domain.ects;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class GradingTableGenerator {

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
            grade = grade.setScale(1);
            gradeDistro.put(grade, (gradeDistro.get(grade) + 1));
        }

        // 2. Heaped grades distribution
        BigDecimal heap = BigDecimal.ZERO;
        for (Entry<BigDecimal, Integer> step : gradeDistro.entrySet()) {
            BigDecimal grade = step.getKey();
            BigDecimal count = new BigDecimal(step.getValue());
            BigDecimal share = count.divide(sampleSize, 5, BigDecimal.ROUND_HALF_EVEN);
            heap = heap.add(share);
            heapedGradeDistro.put(grade, heap.setScale(3, BigDecimal.ROUND_HALF_EVEN));
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
            String grantedValue = tableMap.get(grade);
            gradesCnt++;

            if (isStagnant(grade, heapedGradeDistro)) {
                if (shouldIncrement(grade, grantedValue, heapedGradeDistro, tableMap)) {
                    switch (grantedValue) {
                    case "A":
                        tableMap.put(grade, "B");
                        break;
                    case "B":
                        tableMap.put(grade, "C");
                        break;
                    case "C":
                        tableMap.put(grade, "D");
                        break;
                    case "D":
                        tableMap.put(grade, "E");
                        break;
                    default:
                        break;
                    }
                    ectsGradesSize--;
                } else if (shouldNormalize(grade, grantedValue, heapedGradeDistro, tableMap)) {
                    tableMap.put(grade, getPrevGrade(grade, tableMap));
                }

            } else {
                // Inflate
                if (ectsGrade.compareTo(grantedValue) < 0) {
                    if (((gradesSize - gradesCnt) >= ectsGradesSize)) {
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
                }
            }
        }

        // 5. Populate the table with final values
        List<BigDecimal> reverseOrderedKeys = new ArrayList<BigDecimal>(tableMap.keySet());
        Collections.reverse(reverseOrderedKeys);
        for (BigDecimal mark : reverseOrderedKeys) {
            table.addMark(mark, tableMap.get(mark));
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

    private static boolean shouldIncrement(BigDecimal grade, String ectsGrade, Map<BigDecimal, BigDecimal> heapedGradeDistro,
            Map<BigDecimal, String> tableMap) {
        boolean isStagnant = isStagnant(grade, heapedGradeDistro);
        BigDecimal nextHeap = nextIncrement(grade, heapedGradeDistro);
        String nextEctsGrade = nextHeap != null ? getEctsGrade(nextHeap) : ectsGrade;

        return isStagnant && !hasIncrementedEcts(grade, tableMap) && (compareEctsGrade(ectsGrade, nextEctsGrade) < 0);
    }

    private static boolean shouldNormalize(BigDecimal grade, String ectsGrade, Map<BigDecimal, BigDecimal> heapedGradeDistro,
            Map<BigDecimal, String> tableMap) {
        boolean isStagnant = isStagnant(grade, heapedGradeDistro);
        BigDecimal nextHeap = nextIncrement(grade, heapedGradeDistro);
        String nextEctsGrade = nextHeap != null ? getEctsGrade(nextHeap) : ectsGrade;

        return isStagnant && (compareEctsGrade(ectsGrade, nextEctsGrade) > 0);
    }

    private static boolean isStagnant(BigDecimal grade, Map<BigDecimal, BigDecimal> heapedGradeDistro) {
        BigDecimal prevHeap = null;
        for (Entry<BigDecimal, BigDecimal> step : heapedGradeDistro.entrySet()) {
            if (step.getKey().equals(grade)) {
                return prevHeap != null && prevHeap.equals(step.getValue());
            }
            prevHeap = step.getValue();
        }
        return false;
    }

    private static BigDecimal nextIncrement(BigDecimal grade, Map<BigDecimal, BigDecimal> heapedGradeDistro) {
        BigDecimal prevHeap = null;
        for (Entry<BigDecimal, BigDecimal> step : heapedGradeDistro.entrySet()) {
            if (step.getKey().equals(grade)) {
                prevHeap = step.getValue();
            }
            if (prevHeap != null && prevHeap.compareTo(step.getValue()) < 0) {
                return step.getValue();
            }
        }
        return null;
    }

    private static boolean hasIncrementedEcts(BigDecimal grade, Map<BigDecimal, String> tableMap) {
        String prevEctsGrade = null;
        for (Entry<BigDecimal, String> step : tableMap.entrySet()) {
            if (step.getKey().equals(grade)) {
                return prevEctsGrade != null && !prevEctsGrade.equals(step.getValue());
            }
            prevEctsGrade = step.getValue();
        }
        return false;
    }

    private static String getPrevGrade(BigDecimal grade, Map<BigDecimal, String> tableMap) {
        String prevEctsGrade = null;
        for (Entry<BigDecimal, String> step : tableMap.entrySet()) {
            if (step.getKey().equals(grade)) {
                return prevEctsGrade;
            }
            prevEctsGrade = step.getValue();
        }
        return prevEctsGrade;
    }

    private static int compareEctsGrade(String ects0, String ects1) {
        return ects0.charAt(0) - ects1.charAt(0);
    }

}
