package org.fenixedu.ulisboa.specifications.domain.grade.fmh;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale.GradeScaleLogic;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.grade.common.QualitativeGradeComparator;

public class FMHTypeQualitativeGradeScaleLogic implements GradeScaleLogic {

    private static final String A = "A";
    private static final String B = "B";
    private static final String E = "E";
    private static final String RE = "RE";
    private static final String SU = "SU";
    private static final String MB = "MB";
    private static final String BD = "BD";
    private static final String AB = "AB";
    private static final String AM = "AM";
    private static final String AMB = "AMB";
    private static final String AMBD = "AMBD";
    private static final String M = "M";
    private static final String MBD = "MBD";
    private static final String MBDL = "MBDL";
    private static final String U = "U";

    private static final List<String> SORTED_GRADES = Arrays.asList(RE, SU, A, M, U, AM, AB, B, BD, AMB, AMBD, MB, MBD, MBDL, E);
    private static final QualitativeGradeComparator COMPARATOR = new QualitativeGradeComparator(SORTED_GRADES);

    private static Map<String, LocalizedString> CONFIGURATION = new HashMap<String, LocalizedString>();

    static {
        CONFIGURATION.put(RE, new LocalizedString(Locale.getDefault(), "Reprovado").with(Locale.ENGLISH, "Not Approved"));
        CONFIGURATION.put(SU, new LocalizedString(Locale.getDefault(), "Suficiente").with(Locale.ENGLISH, "Enough"));
        CONFIGURATION.put(A, new LocalizedString(Locale.getDefault(), "Aprovado").with(Locale.ENGLISH, "Approved"));
        CONFIGURATION.put(M, new LocalizedString(Locale.getDefault(), "Maioria").with(Locale.ENGLISH, "Majority"));
        CONFIGURATION.put(U, new LocalizedString(Locale.getDefault(), "Unanimidade").with(Locale.ENGLISH, "Unanimity"));
        CONFIGURATION.put(AM,
                new LocalizedString(Locale.getDefault(), "Aprovado por maioria").with(Locale.ENGLISH, "Approved with majority"));
        CONFIGURATION
                .put(AB, new LocalizedString(Locale.getDefault(), "Aprovado Bom").with(Locale.ENGLISH, "Approved with Good"));
        CONFIGURATION.put(B, new LocalizedString(Locale.getDefault(), "Bom").with(Locale.ENGLISH, "Good"));
        CONFIGURATION.put(BD,
                new LocalizedString(Locale.getDefault(), "Bom com distinção").with(Locale.ENGLISH, "Good with distinction"));
        CONFIGURATION.put(MB, new LocalizedString(Locale.getDefault(), "Muito Bom").with(Locale.ENGLISH, "Very good"));
        CONFIGURATION.put(AMB, new LocalizedString(Locale.getDefault(), "Aprovado com Muito Bom").with(Locale.ENGLISH,
                "Approved with Very good"));
        CONFIGURATION.put(MBD,
                new LocalizedString(Locale.getDefault(), "M.Bom Distinção").with(Locale.ENGLISH, "Very Good with distinction"));
        CONFIGURATION.put(AMBD, new LocalizedString(Locale.getDefault(), "Aprovado com Muito Bom com Distinção").with(
                Locale.ENGLISH, "Approved with Very good with distinction"));
        CONFIGURATION.put(MBDL, new LocalizedString(Locale.getDefault(), "M.Bom Distinção Louvor").with(Locale.ENGLISH,
                "Very good with distinction and Honors"));
        CONFIGURATION.put(E, new LocalizedString(Locale.getDefault(), "Excelente").with(Locale.ENGLISH, "Excellent"));

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
        return false;
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

}
