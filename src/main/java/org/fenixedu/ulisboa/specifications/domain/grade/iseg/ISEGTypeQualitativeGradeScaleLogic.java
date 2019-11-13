package org.fenixedu.ulisboa.specifications.domain.grade.iseg;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScaleEnum;
import org.fenixedu.academic.domain.GradeScaleEnum.GradeScaleLogic;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.academic.domain.curriculum.grade.QualitativeGradeComparator;
import org.fenixedu.commons.i18n.LocalizedString;

public class ISEGTypeQualitativeGradeScaleLogic implements GradeScaleLogic {

    private static final String REU = "REU";
    private static final String REM = "REM";
    private static final String APM = "APM";
    private static final String APU = "APU";
    private static final String APDM = "APDM";
    private static final String APDU = "APDU";
    private static final String APDLU = "APDLU";
    private static final String APBM = "APBM";
    private static final String APBU = "APBU";
    private static final String APMBM = "APMBM";
    private static final String APMBU = "APMBU";
    private static final String APMBDU = "APMBDU";

    private static final List<String> SORTED_GRADES =
            Arrays.asList(APM, APU, APDM, APDU, APDLU, APBM, APBU, APMBM, APMBU, APMBDU, REM, REU);
    private static final QualitativeGradeComparator COMPARATOR = new QualitativeGradeComparator(SORTED_GRADES);

    private static Map<String, LocalizedString> CONFIGURATION = new HashMap<String, LocalizedString>();

    static {
        CONFIGURATION.put(APM,
                new LocalizedString(Locale.getDefault(), "Aprovado por Maioria").with(Locale.ENGLISH, "Aprovado por Maioria"));
        CONFIGURATION.put(APU, new LocalizedString(Locale.getDefault(), "Aprovado por Unanimidade").with(Locale.ENGLISH,
                "Aprovado por Unanimidade"));
        CONFIGURATION.put(APDM, new LocalizedString(Locale.getDefault(), "Aprovado com Distinção por Maioria")
                .with(Locale.ENGLISH, "Aprovado com Distinção por Maioria"));
        CONFIGURATION.put(APDU, new LocalizedString(Locale.getDefault(), "Aprovado com Distinção por Unanimidade")
                .with(Locale.ENGLISH, "Aprovado com Distinção por Unanimidade"));
        CONFIGURATION.put(APDLU, new LocalizedString(Locale.getDefault(), "Aprovado com Distinção e Louvor por Unanimidade")
                .with(Locale.ENGLISH, "Aprovado com Distinção e Louvor por Unanimidade"));
        CONFIGURATION.put(REM,
                new LocalizedString(Locale.getDefault(), "Recusado por maioria").with(Locale.ENGLISH, "Recusado por maioria"));
        CONFIGURATION.put(REU, new LocalizedString(Locale.getDefault(), "Recusado por unanimidade").with(Locale.ENGLISH, "Recusado por unanimidade"));
        CONFIGURATION.put(APBM, new LocalizedString(Locale.getDefault(), "Aprovado com Bom por maioria").with(Locale.ENGLISH,
                "Aprovado com Bom por maioria"));
        CONFIGURATION.put(APBU, new LocalizedString(Locale.getDefault(), "Aprovado com Bom por unanimidade").with(Locale.ENGLISH,
                "Aprovado com Bom por unanimidade"));
        CONFIGURATION.put(APMBM, new LocalizedString(Locale.getDefault(), "Aprovado com Muito Bom por maioria").with(Locale.ENGLISH,
                "Aprovado com Muito Bom por maioria"));
        CONFIGURATION.put(APMBU, new LocalizedString(Locale.getDefault(), "Aprovado com Muito Bom por unanimidade").with(Locale.ENGLISH,
                "Aprovado com Muito Bom por unanimidade"));
        CONFIGURATION.put(APMBDU, new LocalizedString(Locale.getDefault(), "Aprovado com Muito Bom com distinção por unanimidade").with(Locale.ENGLISH,
                "Aprovado com Muito Bom com distinção por unanimidade"));

    }

    @Override
    public boolean belongsTo(String value) {
        return CONFIGURATION.containsKey(value);

    }

    @Override
    public boolean checkFinal(Grade grade) {
        return belongsTo(grade.getValue());
    }

    @Override
    public boolean checkNotFinal(Grade grade) {
        return belongsTo(grade.getValue());
    }

    @Override
    public boolean isApproved(Grade grade) {
        return !(isNotEvaluated(grade) || isNotApproved(grade));
    }

    @Override
    public boolean isNotApproved(Grade grade) {
        return REM.equals(grade.getValue()) || REU.equals(grade.getValue());
    }

    @Override
    public boolean isNotEvaluated(Grade grade) {
        return false;
    }

    @Override
    public String qualify(Grade grade) {
        return CONFIGURATION.get(grade.getValue()).getContent();
    }

    @Override
    public LocalizedString getExtendedValue(Grade grade) {
        return CONFIGURATION.get(grade.getValue());
    }

    @Override
    public int compareGrades(Grade leftGrade, Grade rightGrade) {
        return COMPARATOR.compare(leftGrade, rightGrade);
    }

    @Override
    public boolean hasRestrictedGrades() {
        return true;
    }

    @Override
    public Collection<Grade> getPossibleGrades() {
        return CONFIGURATION.keySet().stream().map(v -> Grade.createGrade(v, GradeScaleEnum.TYPEQUALITATIVE))
                .collect(Collectors.toSet());
    }
}
