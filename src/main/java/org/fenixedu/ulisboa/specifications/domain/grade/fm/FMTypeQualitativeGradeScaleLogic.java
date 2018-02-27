package org.fenixedu.ulisboa.specifications.domain.grade.fm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale.GradeScaleLogic;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.grade.common.QualitativeGradeComparator;

public class FMTypeQualitativeGradeScaleLogic implements GradeScaleLogic {

    private static final String INSC = "INSC";
    private static final String RE = "RE";
    private static final String NE = "NE";
    private static final String NAP = "NAP";
    private static final String NA = "NA";
    private static final String NAPT = "NAPT";
    private static final String I = "I";
    private static final String A = "A";
    private static final String SU = "SU";
    private static final String B = "B";
    private static final String BD = "BD";
    private static final String MBD = "MBD";
    private static final String APT = "APT";
    private static final String MB = "MB";
    private static final String D = "D";
    private static final String DL = "DL";
    private static final String AD = "AD";
    private static final String ADL = "ADL";
    private static final String E = "E";
    private static final String C = "C";
    private static final String BPM = "BPM";
    private static final String BDM = "BDM";
    private static final String BDU = "BDU";
    private static final String BU = "BU";
    private static final String MBM = "MBM";
    private static final String MBU = "MBU";
    private static final String AUDL = "AUDL";
    private static final String AMDL = "AMDL";
    private static final String AMD = "AMD";
    private static final String AUD = "AUD";

    private static final List<String> SORTED_GRADES = Arrays.asList(INSC, RE, NE, NAP, NA, NAPT, I, APT, C, SU, A, D, DL, AMD,
            AMDL, AUDL, AUD, B, BD, BPM, BDM, BU, BDU, MB, MBD, MBM, MBU, AD, ADL, E);
    private static final QualitativeGradeComparator COMPARATOR = new QualitativeGradeComparator(SORTED_GRADES);

    private static Map<String, LocalizedString> CONFIGURATION = new HashMap<String, LocalizedString>();

    static {
        CONFIGURATION.put(INSC, new LocalizedString(Locale.getDefault(), "Inscrito").with(Locale.ENGLISH, "Enrolled"));
        CONFIGURATION.put(RE, new LocalizedString(Locale.getDefault(), "Reprovado").with(Locale.ENGLISH, "Not Approved"));
        CONFIGURATION.put(NE, new LocalizedString(Locale.getDefault(), "Não entregou").with(Locale.ENGLISH, "Not delivered"));
        CONFIGURATION.put(NAP, new LocalizedString(Locale.getDefault(), "Não Aprovado").with(Locale.ENGLISH, "Not Approved"));
        CONFIGURATION.put(I, new LocalizedString(Locale.getDefault(), "Insuficiente").with(Locale.ENGLISH, "Not Enough"));
        CONFIGURATION.put(NA, new LocalizedString(Locale.getDefault(), "Não Avaliado").with(Locale.ENGLISH, "Not Evaluated"));
        CONFIGURATION.put(NAPT, new LocalizedString(Locale.getDefault(), "Não Apto").with(Locale.ENGLISH, "Does Not Fit"));
        CONFIGURATION.put(A, new LocalizedString(Locale.getDefault(), "Aprovado").with(Locale.ENGLISH, "Approved"));
        CONFIGURATION.put(SU, new LocalizedString(Locale.getDefault(), "Suficiente").with(Locale.ENGLISH, "Sufficient"));
        CONFIGURATION.put(B, new LocalizedString(Locale.getDefault(), "Bom").with(Locale.ENGLISH, "Good"));
        CONFIGURATION.put(BD,
                new LocalizedString(Locale.getDefault(), "Bom com Distinção").with(Locale.ENGLISH, "Good with Distinction"));
        CONFIGURATION.put(MBD, new LocalizedString(Locale.getDefault(), "Muito Bom com Distinção").with(Locale.ENGLISH,
                "Very good with Distinction"));
        CONFIGURATION.put(APT, new LocalizedString(Locale.getDefault(), "Apto").with(Locale.ENGLISH, "Fit"));
        CONFIGURATION.put(MB, new LocalizedString(Locale.getDefault(), "Muito Bom").with(Locale.ENGLISH, "Very good"));
        CONFIGURATION.put(D, new LocalizedString(Locale.getDefault(), "Distinção").with(Locale.ENGLISH, "Distinction"));
        CONFIGURATION.put(DL,
                new LocalizedString(Locale.getDefault(), "Distinção e Louvor").with(Locale.ENGLISH, "Distinction and Praise"));
        CONFIGURATION.put(AD, new LocalizedString(Locale.getDefault(), "Aprovado com Distinção").with(Locale.ENGLISH,
                "Approved with Distinction"));
        CONFIGURATION.put(ADL, new LocalizedString(Locale.getDefault(), "Aprovado com Distinção e Louvor").with(Locale.ENGLISH,
                "Approved with Distinction and Honors"));
        CONFIGURATION.put(E, new LocalizedString(Locale.getDefault(), "Excelente").with(Locale.ENGLISH, "Excellent"));
        CONFIGURATION.put(C, new LocalizedString(Locale.getDefault(), "Creditação").with(Locale.ENGLISH, "Crediting"));
        CONFIGURATION.put(BPM,
                new LocalizedString(Locale.getDefault(), "Bom por Maioria").with(Locale.ENGLISH, "Good by Majority"));
        CONFIGURATION.put(BDM, new LocalizedString(Locale.getDefault(), "Bom c/ Distinção por Maioria").with(Locale.ENGLISH,
                "Good with Distinction by Majority"));
        CONFIGURATION.put(BDU, new LocalizedString(Locale.getDefault(), "Bom c/ Distinção por Unanimidade").with(Locale.ENGLISH,
                "Good with Distinction by Unanimity"));
        CONFIGURATION.put(BU,
                new LocalizedString(Locale.getDefault(), "Bom por Unanimidade").with(Locale.ENGLISH, "Good by Unanimity"));
        CONFIGURATION.put(MBM,
                new LocalizedString(Locale.getDefault(), "Muito Bom por Maioria").with(Locale.ENGLISH, "Very good by Majority"));
        CONFIGURATION.put(MBU, new LocalizedString(Locale.getDefault(), "Muito Bom por Unanimidade").with(Locale.ENGLISH,
                "Very good by Unanimity"));
        CONFIGURATION.put(AUDL, new LocalizedString(Locale.getDefault(), "Aprovado por Unanimidade com Distinção e Louvor").with(
                Locale.ENGLISH, "Approved by Unanimity with Distinction and Praise"));
        CONFIGURATION.put(AMDL, new LocalizedString(Locale.getDefault(), "Aprovado por Maioria com Distinção e Louvor").with(
                Locale.ENGLISH, "Approved by Majority with Distinction and Praise"));
        CONFIGURATION.put(AMD, new LocalizedString(Locale.getDefault(), "Aprovado por Maioria com Distinção").with(
                Locale.ENGLISH, "Approved by Majority with Distinction"));
        CONFIGURATION.put(AUD, new LocalizedString(Locale.getDefault(), "Aprovado por Unanimidade com distinção").with(
                Locale.ENGLISH, "Approved by Unanimity with Distinction"));

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
        return I.equals(grade.getValue()) || RE.equals(grade.getValue()) || NAP.equals(grade.getValue())
                || NAPT.equals(grade.getValue());

    }

    @Override
    public boolean isNotEvaluated(Grade grade) {
        return INSC.equals(grade.getValue()) || NE.equals(grade.getValue()) || NA.equals(grade.getValue());
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

}
