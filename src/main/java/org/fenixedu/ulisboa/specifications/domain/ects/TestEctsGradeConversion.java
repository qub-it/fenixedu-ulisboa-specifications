package org.fenixedu.ulisboa.specifications.domain.ects;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.ects.GradingTableData.GradeConversion;

public class TestEctsGradeConversion extends CustomTask {

    @Override
    public void runTask() throws Exception {
        String[] control = new String[8];
        control[0] = "ABBCCCCDDDE"; // Uniform distro
        control[1] = "AAABBBCCDDE"; // Chi-squared distro
        control[2] = "AAABBCCDDEE"; // Normal distro
        control[3] = "AABBBCDDDEE"; // Normal distro #2
        control[4] = "ABBCDDDEEEE"; // Tripolar distro
        control[5] = "ABCDDDDEEEE"; // Unbalanced tripolar distro
        control[6] = "ABCDEEEEEEE"; // Bipolar distro
        control[7] = "ABCDEEEEEEE"; // Monopolar distro

        verify("Uniform distro", generateSampleUniform(), control[0]);
        verify("Chi-squared distro", generateSampleChiSq(), control[1]);
        verify("Normal distro", generateSampleNormal(), control[2]);
        verify("Normal distro #2", generateSampleNormal2(), control[3]);
        verify("Tripolar distro", generateSampleTripolar(), control[4]);
        verify("Unbalanced tripolar distro", generateSampleTripolarUnbalanced(), control[5]);
        verify("Bipolar distro", generateSampleBipolar(), control[6]);
        verify("Monopolar distro", generateSampleMonopolar(), control[7]);
    }

    public void verify(String testTitle, List<BigDecimal> input, String expectedOutput) {
        GeneralPurposeGradingTable testingTable = new GeneralPurposeGradingTable(input);
        String result = testingTable.printScale();
        if (result.equals(expectedOutput)) {
            taskLog("✓ " + testTitle + ": PASSED");
        } else {
            taskLog("  " + testTitle + ": FAILED");
        }
        testingTable.delete();
    }

    public List<BigDecimal> generateSampleUniform() {
        List<BigDecimal> sample = new ArrayList<BigDecimal>();
        for (int i = 0; i < 100; i++) {
            if (i < 9) {
                sample.add(new BigDecimal("20.0"));
            } else if (i < 18) {
                sample.add(new BigDecimal("19.0"));
            } else if (i < 27) {
                sample.add(new BigDecimal("18.0"));
            } else if (i < 36) {
                sample.add(new BigDecimal("17.0"));
            } else if (i < 45) {
                sample.add(new BigDecimal("16.0"));
            } else if (i < 54) {
                sample.add(new BigDecimal("15.0"));
            } else if (i < 63) {
                sample.add(new BigDecimal("14.0"));
            } else if (i < 72) {
                sample.add(new BigDecimal("13.0"));
            } else if (i < 81) {
                sample.add(new BigDecimal("12.0"));
            } else if (i < 90) {
                sample.add(new BigDecimal("11.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("10.0"));
            }
        }
        return sample;
    }

    public List<BigDecimal> generateSampleChiSq() {
        List<BigDecimal> sample = new ArrayList<BigDecimal>();
        for (int i = 0; i < 100; i++) {
            if (i < 2) {
                sample.add(new BigDecimal("20.0"));
            } else if (i < 5) {
                sample.add(new BigDecimal("19.0"));
            } else if (i < 9) {
                sample.add(new BigDecimal("18.0"));
            } else if (i < 14) {
                sample.add(new BigDecimal("17.0"));
            } else if (i < 19) {
                sample.add(new BigDecimal("16.0"));
            } else if (i < 31) {
                sample.add(new BigDecimal("15.0"));
            } else if (i < 54) {
                sample.add(new BigDecimal("14.0"));
            } else if (i < 65) {
                sample.add(new BigDecimal("13.0"));
            } else if (i < 78) {
                sample.add(new BigDecimal("12.0"));
            } else if (i < 90) {
                sample.add(new BigDecimal("11.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("10.0"));
            }
        }
        return sample;
    }

    public List<BigDecimal> generateSampleNormal() {
        List<BigDecimal> sample = new ArrayList<BigDecimal>();
        for (int i = 0; i < 100; i++) {
            if (i < 2) {
                sample.add(new BigDecimal("20.0"));
            } else if (i < 5) {
                sample.add(new BigDecimal("19.0"));
            } else if (i < 9) {
                sample.add(new BigDecimal("18.0"));
            } else if (i < 14) {
                sample.add(new BigDecimal("17.0"));
            } else if (i < 19) {
                sample.add(new BigDecimal("16.0"));
            } else if (i < 36) {
                sample.add(new BigDecimal("15.0"));
            } else if (i < 55) {
                sample.add(new BigDecimal("14.0"));
            } else if (i < 68) {
                sample.add(new BigDecimal("13.0"));
            } else if (i < 74) {
                sample.add(new BigDecimal("12.0"));
            } else if (i < 92) {
                sample.add(new BigDecimal("11.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("10.0"));
            }
        }
        return sample;
    }

    public List<BigDecimal> generateSampleNormal2() {
        List<BigDecimal> sample = new ArrayList<BigDecimal>();
        for (int i = 0; i < 100; i++) {
            if (i < 2) {
                sample.add(new BigDecimal("20.0"));
            } else if (i < 5) {
                sample.add(new BigDecimal("19.0"));
            } else if (i < 12) {
                sample.add(new BigDecimal("18.0"));
            } else if (i < 17) {
                sample.add(new BigDecimal("17.0"));
            } else if (i < 29) {
                sample.add(new BigDecimal("16.0"));
            } else if (i < 44) {
                sample.add(new BigDecimal("15.0"));
            } else if (i < 69) {
                sample.add(new BigDecimal("14.0"));
            } else if (i < 79) {
                sample.add(new BigDecimal("13.0"));
            } else if (i < 86) {
                sample.add(new BigDecimal("12.0"));
            } else if (i < 93) {
                sample.add(new BigDecimal("11.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("10.0"));
            }
        }
        return sample;
    }

    public List<BigDecimal> generateSampleTripolar() {
        List<BigDecimal> sample = new ArrayList<BigDecimal>();
        for (int i = 0; i < 100; i++) {
            if (i < 0) {
                sample.add(new BigDecimal("20.0"));
            } else if (i < 0) {
                sample.add(new BigDecimal("19.0"));
            } else if (i < 33) {
                sample.add(new BigDecimal("18.0"));
            } else if (i < 33) {
                sample.add(new BigDecimal("17.0"));
            } else if (i < 33) {
                sample.add(new BigDecimal("16.0"));
            } else if (i < 33) {
                sample.add(new BigDecimal("15.0"));
            } else if (i < 66) {
                sample.add(new BigDecimal("14.0"));
            } else if (i < 66) {
                sample.add(new BigDecimal("13.0"));
            } else if (i < 66) {
                sample.add(new BigDecimal("12.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("11.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("10.0"));
            }
        }
        return sample;
    }

    public List<BigDecimal> generateSampleTripolarUnbalanced() {
        List<BigDecimal> sample = new ArrayList<BigDecimal>();
        for (int i = 0; i < 100; i++) {
            if (i < 0) {
                sample.add(new BigDecimal("20.0"));
            } else if (i < 0) {
                sample.add(new BigDecimal("19.0"));
            } else if (i < 74) {
                sample.add(new BigDecimal("18.0"));
            } else if (i < 74) {
                sample.add(new BigDecimal("17.0"));
            } else if (i < 74) {
                sample.add(new BigDecimal("16.0"));
            } else if (i < 74) {
                sample.add(new BigDecimal("15.0"));
            } else if (i < 75) {
                sample.add(new BigDecimal("14.0"));
            } else if (i < 75) {
                sample.add(new BigDecimal("13.0"));
            } else if (i < 75) {
                sample.add(new BigDecimal("12.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("11.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("10.0"));
            }
        }
        return sample;
    }

    public List<BigDecimal> generateSampleBipolar() {
        List<BigDecimal> sample = new ArrayList<BigDecimal>();
        for (int i = 0; i < 100; i++) {
            if (i < 0) {
                sample.add(new BigDecimal("20.0"));
            } else if (i < 0) {
                sample.add(new BigDecimal("19.0"));
            } else if (i < 50) {
                sample.add(new BigDecimal("18.0"));
            } else if (i < 50) {
                sample.add(new BigDecimal("17.0"));
            } else if (i < 50) {
                sample.add(new BigDecimal("16.0"));
            } else if (i < 50) {
                sample.add(new BigDecimal("15.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("14.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("13.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("12.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("11.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("10.0"));
            }
        }
        return sample;
    }

    public List<BigDecimal> generateSampleMonopolar() {
        List<BigDecimal> sample = new ArrayList<BigDecimal>();
        for (int i = 0; i < 100; i++) {
            if (i < 0) {
                sample.add(new BigDecimal("20.0"));
            } else if (i < 0) {
                sample.add(new BigDecimal("19.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("18.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("17.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("16.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("15.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("14.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("13.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("12.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("11.0"));
            } else if (i < 100) {
                sample.add(new BigDecimal("10.0"));
            }
        }
        return sample;
    }
}