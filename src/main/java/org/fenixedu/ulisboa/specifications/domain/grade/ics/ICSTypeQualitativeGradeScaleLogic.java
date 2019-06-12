package org.fenixedu.ulisboa.specifications.domain.grade.ics;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.GradeScale.GradeScaleLogic;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.academic.domain.curriculum.grade.QualitativeGradeComparator;

public class ICSTypeQualitativeGradeScaleLogic implements GradeScaleLogic {

    private static final String SU = "SU";
    private static final String B = "B";
    private static final String MB = "MB";
    private static final String E = "E";
    private static final String BD = "BD";
    private static final String DL = "DL";
    private static final String A = "A";
    private static final String RE = "RE";
    private static final String AD = "AD";
    private static final String ADL = "ADL";
    private static final String C = "C";

    private static final List<String> SORTED_GRADES = Arrays.asList(RE, C, SU, A, B, BD, MB, AD, DL, ADL, E);
    private static final QualitativeGradeComparator COMPARATOR = new QualitativeGradeComparator(SORTED_GRADES);

    private static Map<String, LocalizedString> CONFIGURATION = new HashMap<String, LocalizedString>();

    static {
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
        CONFIGURATION.put(DL,
                new LocalizedString(Locale.getDefault(), "Distinção e Louvor").with(Locale.ENGLISH, "Distinction and Praise"));
        CONFIGURATION.put(RE, new LocalizedString(Locale.getDefault(), "Reprovado").with(Locale.ENGLISH, "Not Approved"));
        CONFIGURATION.put(C, new LocalizedString(Locale.getDefault(), "Creditação").with(Locale.ENGLISH, "Crediting"));
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
        return RE.equals(grade.getValue());
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
        return CONFIGURATION.keySet().stream().map(v -> Grade.createGrade(v, GradeScale.TYPEQUALITATIVE))
                .collect(Collectors.toSet());
    }

}
