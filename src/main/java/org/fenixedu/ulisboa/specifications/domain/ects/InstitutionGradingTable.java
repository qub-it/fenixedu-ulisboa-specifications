package org.fenixedu.ulisboa.specifications.domain.ects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.domain.ects.GradingTableData.GradeConversion;

public class InstitutionGradingTable extends InstitutionGradingTable_Base {

    private InstitutionGradingTable() {
        super();
    }

    public static Stream<InstitutionGradingTable> findAll() {
        return Bennu.getInstance().getGradingTablesSet().stream().filter(InstitutionGradingTable.class::isInstance)
                .map(InstitutionGradingTable.class::cast);
    }

    public static InstitutionGradingTable find(final ExecutionYear ey) {
        return findAll().filter(igt -> igt.getExecutionYear() == ey).findAny().orElse(null);
    }

    public static InstitutionGradingTable generate(final ExecutionYear executionYear) {
        if (find(executionYear) != null) {
            return null;
        }
        InstitutionGradingTable table = new InstitutionGradingTable();
        table.setExecutionYear(executionYear);
        table.compileData();
        return table;
    }

    public static void copyData(GradingTable table) {
        InstitutionGradingTable institutionTable = find(table.getExecutionYear());
        if (institutionTable != null) {
            for (GradeConversion gc : institutionTable.getData().getTable()) {
                table.addMark(gc.getMark(), gc.getEctsGrade());
            }
        }
    }

    @Override
    public void compileData() {
        GradingTableData tableData = new GradingTableData();
        setData(tableData);
        List<BigDecimal> sample = harvestSample();
        if (sample != null) {
            GradingTableGenerator.generateTableData(this, sample);
        } else {
            GradingTableGenerator.defaultData(this);
            setCopied(true);
        }
    }

    private List<BigDecimal> harvestSample() {
        List<BigDecimal> sample = new ArrayList<BigDecimal>();
        int coveredYears = 0;
        boolean sampleOK = false;
        final Map<ExecutionYear, Set<RegistrationConclusionBean>> conclusionsMap = collectConclusions();
        for (ExecutionYear year = getExecutionYear().getPreviousExecutionYear(); year != null; year =
                year.getPreviousExecutionYear()) {

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
        }
        return sampleOK ? sample : null;
    }

    private Map<ExecutionYear, Set<RegistrationConclusionBean>> collectConclusions() {
        final Map<ExecutionYear, Set<RegistrationConclusionBean>> conclusionsMap =
                new LinkedHashMap<ExecutionYear, Set<RegistrationConclusionBean>>();

        for (final Registration registration : Bennu.getInstance().getRegistrationsSet()) {
            if (registration.getStudentCurricularPlansSet().isEmpty()) {
                continue;
            }

            ProgramConclusion.conclusionsFor(registration).forEach(pc -> {
                final RegistrationConclusionBean bean = new RegistrationConclusionBean(registration, pc);

                if (!bean.isConcluded()) {
                    return;
                }
                final ExecutionYear conclusionYear = bean.getConclusionYear();
                if (!conclusionsMap.containsKey(conclusionYear)) {
                    conclusionsMap.put(conclusionYear, new HashSet<RegistrationConclusionBean>());
                }

                conclusionsMap.get(conclusionYear).add(bean);
            });
        }
        return conclusionsMap;
    }

}
