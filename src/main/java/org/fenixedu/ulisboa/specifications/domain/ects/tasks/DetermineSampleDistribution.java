package org.fenixedu.ulisboa.specifications.domain.ects.tasks;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.ulisboa.specifications.domain.ects.GradingTableSettings;
import org.fenixedu.ulisboa.specifications.domain.student.curriculum.conclusion.RegistrationConclusionInformation;
import org.fenixedu.ulisboa.specifications.domain.student.curriculum.conclusion.RegistrationConclusionServices;

import pt.ist.fenixframework.FenixFramework;

public class DetermineSampleDistribution extends CustomTask {

    ExecutionYear executionYear;
    Degree degree;

    @Override
    public void runTask() throws Exception {
        executionYear = FenixFramework.getDomainObject("566269963141137");
        degree = FenixFramework.getDomainObject("564182609035536");

        List<BigDecimal> sample = harvestSample();
        printSample(sample);

    }

    private List<BigDecimal> harvestSample() {
        List<BigDecimal> sample = new ArrayList<BigDecimal>();
        int coveredYears = 0;
        boolean sampleOK = false;
        final Map<ExecutionYear, Set<RegistrationConclusionBean>> conclusionsMap = collectConclusions();
        for (ExecutionYear year = executionYear.getPreviousExecutionYear(); year != null; year = year.getPreviousExecutionYear()) {

            if (conclusionsMap.get(year) != null) {
                for (RegistrationConclusionBean bean : conclusionsMap.get(year)) {
                    Integer finalAverage =
                            bean.getFinalGrade().getNumericValue() != null ? bean.getFinalGrade().getNumericValue()
                                    .setScale(0, RoundingMode.HALF_UP).intValue() : 0;
                    if (finalAverage == 0) {
                        continue;
                    }
                    sample.add(new BigDecimal(finalAverage));
                }
            }

            if (++coveredYears >= GradingTableSettings.getMinimumPastYears()
                    && sample.size() >= GradingTableSettings.getMinimumSampleSize()) {
                sampleOK = true;
                break;
            }

            if (coveredYears == GradingTableSettings.getMaximumPastYears()) {
                break;
            }
        }
        return sampleOK ? sample : null;
    }

    private Map<ExecutionYear, Set<RegistrationConclusionBean>> collectConclusions() {
        final Map<ExecutionYear, Set<RegistrationConclusionBean>> conclusionsMap =
                new LinkedHashMap<ExecutionYear, Set<RegistrationConclusionBean>>();

        for (final Registration registration : degree.getRegistrationsSet()) {
            if (registration.getStudentCurricularPlansSet().isEmpty()) {
                continue;
            }
            for (RegistrationConclusionInformation info : RegistrationConclusionServices.inferConclusion(registration)) {
                if (info.getCurriculumGroup() == null || !info.isConcluded()) {
                    continue;
                }
                final ExecutionYear conclusionYear = info.getRegistrationConclusionBean().getConclusionYear();
                if (!conclusionsMap.containsKey(conclusionYear)) {
                    conclusionsMap.put(conclusionYear, new HashSet<RegistrationConclusionBean>());
                }

                conclusionsMap.get(conclusionYear).add(info.getRegistrationConclusionBean());
            }
        }
        return conclusionsMap;
    }

    private void printSample(List<BigDecimal> sample) {
        final Map<BigDecimal, Integer> distribution = new TreeMap<BigDecimal, Integer>(new Comparator<BigDecimal>() {

            @Override
            public int compare(BigDecimal bd1, BigDecimal bd2) {
                return bd2.compareTo(bd1);
            }

        });
        for (BigDecimal mark : sample) {
            if (distribution.get(mark) == null) {
                distribution.put(mark, 0);
            }
            Integer count = distribution.get(mark);
            distribution.put(mark, ++count);
        }

        taskLog("+------+------+------+------+------+");
        taskLog("| MARK |  SUM |   %  | HEAP | %HEAP|");
        taskLog("+------+------+------+------+------+");
        int heap = 0;
        for (BigDecimal mark : distribution.keySet()) {
            int sum = distribution.get(mark);
            heap += sum;
            double percent = (sum * 1.0 / sample.size()) * 100.0;
            double heapPerc = (heap * 1.0 / sample.size()) * 100.0;
            taskLog("|  " + mark.toString() + "  |" + String.format("%5d", sum) + " |" + String.format("%5.1f", percent) + "%|"
                    + String.format("%5d", heap) + " |" + String.format("%5.1f", heapPerc) + "%|");
            taskLog("+------+------+------+------+------+");
        }

    }
}
