package org.fenixedu.ulisboa.specifications.domain.grade.fd;

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

@Deprecated
public class FDTypeQualitativeGradeScaleLogic implements GradeScaleLogic {

    private static final String R = "REC";
    private static final String I = "I";
    private static final String SU = "SU";
    private static final String A = "A";
    private static final String B = "B";
    private static final String BD = "BD";
    private static final String MB = "MB";
    private static final String MBD = "MBD";
    private static final String MBDL = "MBDL";
    private static final String AD = "AD";
    private static final String ADL = "ADL";
    private static final String E = "E";

    private static final List<String> SORTED_GRADES = Arrays.asList(I, R, SU, A, B, BD, MB, MBD, MBDL, AD, ADL, E);
    private static final QualitativeGradeComparator COMPARATOR = new QualitativeGradeComparator(SORTED_GRADES);

    private static Map<String, LocalizedString> CONFIGURATION = new HashMap<String, LocalizedString>();

    static {
        CONFIGURATION.put(R, new LocalizedString(Locale.getDefault(), "Recusado").with(Locale.ENGLISH, "Refused"));
        CONFIGURATION.put(I, new LocalizedString(Locale.getDefault(), "Insuficiente").with(Locale.ENGLISH, "Not Enough"));
        CONFIGURATION.put(SU, new LocalizedString(Locale.getDefault(), "Suficiente").with(Locale.ENGLISH, "Sufficient"));
        CONFIGURATION.put(A, new LocalizedString(Locale.getDefault(), "Aprovado").with(Locale.ENGLISH, "Approved"));
        CONFIGURATION.put(B, new LocalizedString(Locale.getDefault(), "Bom").with(Locale.ENGLISH, "Good"));
        CONFIGURATION.put(BD,
                new LocalizedString(Locale.getDefault(), "Bom com Distinção").with(Locale.ENGLISH, "Good with Distinction"));
        CONFIGURATION.put(MB, new LocalizedString(Locale.getDefault(), "Muito Bom").with(Locale.ENGLISH, "Very good"));
        CONFIGURATION.put(MBD, new LocalizedString(Locale.getDefault(), "Muito Bom com Distinção").with(Locale.ENGLISH,
                "Very good with Distinction"));
        CONFIGURATION.put(MBDL, new LocalizedString(Locale.getDefault(), "Muito Bom c/ Distinção e Louvor").with(Locale.ENGLISH,
                "Very Good with Distinction and Praise"));
        CONFIGURATION.put(E, new LocalizedString(Locale.getDefault(), "Excelente").with(Locale.ENGLISH, "Excellent"));
        CONFIGURATION.put(AD, new LocalizedString(Locale.getDefault(), "Aprovado com Distinção").with(Locale.ENGLISH,
                "Approved with Distinction"));
        CONFIGURATION.put(ADL, new LocalizedString(Locale.getDefault(), "Aprovado com Distinção e Louvor").with(Locale.ENGLISH,
                "Approved with Distinction and Honors"));

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
        return I.equals(grade.getValue()) || R.equals(grade.getValue());
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
    @Deprecated
    public Collection<Grade> getPossibleGrades() {
        throw new RuntimeException("deprecated");
    }

}
