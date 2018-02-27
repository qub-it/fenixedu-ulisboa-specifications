package org.fenixedu.ulisboa.specifications.domain.grade.ie;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale.GradeScaleLogic;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.grade.common.QualitativeGradeComparator;

public class IETypeQualitativeGradeScaleLogic implements GradeScaleLogic {

    private static final String R = "R";
    private static final String A = "A";
    private static final String SU = "SU";
    private static final String B = "B";
    private static final String BD = "BD";
    private static final String MB = "MB";
    private static final String E = "E";
    private static final String AD = "AD";
    private static final String ADL = "ADL";
    private static final String CRUL = "CRUL";
    private static final String MBDL = "MBDL";

    private static final List<String> SORTED_GRADES = Arrays.asList(R, CRUL, SU, A, B, BD, MB, AD, ADL, MBDL, E);
    private static final QualitativeGradeComparator COMPARATOR = new QualitativeGradeComparator(SORTED_GRADES);

    private static Map<String, LocalizedString> CONFIGURATION = new HashMap<String, LocalizedString>();

    static {
        CONFIGURATION.put(R, new LocalizedString(Locale.getDefault(), "Recusado").with(Locale.ENGLISH, "Refused"));

        CONFIGURATION.put(SU, new LocalizedString(Locale.getDefault(), "Suficiente").with(Locale.ENGLISH, "Sufficient"));
        CONFIGURATION.put(A, new LocalizedString(Locale.getDefault(), "Aprovado").with(Locale.ENGLISH, "Approved"));
        CONFIGURATION.put(B, new LocalizedString(Locale.getDefault(), "Bom").with(Locale.ENGLISH, "Good"));
        CONFIGURATION.put(BD,
                new LocalizedString(Locale.getDefault(), "Bom com Distinção").with(Locale.ENGLISH, "Good with Distinction"));
        CONFIGURATION.put(MB, new LocalizedString(Locale.getDefault(), "Muito Bom").with(Locale.ENGLISH, "Very good"));
        CONFIGURATION.put(E, new LocalizedString(Locale.getDefault(), "Excelente").with(Locale.ENGLISH, "Excellent"));
        CONFIGURATION.put(AD, new LocalizedString(Locale.getDefault(), "Aprovado com Distinção").with(Locale.ENGLISH,
                "Approved with Distinction"));
        CONFIGURATION.put(ADL, new LocalizedString(Locale.getDefault(), "Aprovado com Distinção e Louvor").with(Locale.ENGLISH,
                "Approved with Distinction and Honors"));
        CONFIGURATION.put(CRUL,
                new LocalizedString(Locale.getDefault(), "Creditação (RUL)").with(Locale.ENGLISH, "Crediting (RUL)"));
        CONFIGURATION.put(MBDL, new LocalizedString(Locale.getDefault(), "Muito Bom c/ Distinção e Louvor").with(Locale.ENGLISH,
                "Very Good with Distinction and Praise"));
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
        return R.equals(grade.getValue());
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
