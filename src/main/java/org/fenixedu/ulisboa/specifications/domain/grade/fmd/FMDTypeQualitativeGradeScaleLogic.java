package org.fenixedu.ulisboa.specifications.domain.grade.fmd;

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
import org.fenixedu.ulisboa.specifications.domain.grade.common.QualitativeGradeComparator;

public class FMDTypeQualitativeGradeScaleLogic implements GradeScaleLogic {

    private static final String NAPT = "NAPT";
    private static final String APT = "APT";
    private static final String I = "I";
    private static final String SU = "SU";
    private static final String B = "B";
    private static final String MB = "MB";
    private static final String BD = "BD";
    private static final String CA = "CA";
    private static final String DL = "DL";
    private static final String A = "A";
    private static final String AD = "AD";

    private static final List<String> SORTED_GRADES = Arrays.asList(NAPT, I, SU, APT, A, CA, B, BD, MB, AD, DL);
    private static final QualitativeGradeComparator COMPARATOR = new QualitativeGradeComparator(SORTED_GRADES);

    private static Map<String, LocalizedString> CONFIGURATION = new HashMap<String, LocalizedString>();

    static {
        CONFIGURATION.put(NAPT, new LocalizedString(Locale.getDefault(), "Não Apto").with(Locale.ENGLISH, "Does Not Fit"));
        CONFIGURATION.put(APT, new LocalizedString(Locale.getDefault(), "Apto").with(Locale.ENGLISH, "Fit"));
        CONFIGURATION.put(I, new LocalizedString(Locale.getDefault(), "Insuficiente").with(Locale.ENGLISH, "Not Enough"));
        CONFIGURATION.put(SU, new LocalizedString(Locale.getDefault(), "Suficiente").with(Locale.ENGLISH, "Sufficient"));
        CONFIGURATION.put(B, new LocalizedString(Locale.getDefault(), "Bom").with(Locale.ENGLISH, "Good"));
        CONFIGURATION.put(MB, new LocalizedString(Locale.getDefault(), "Muito Bom").with(Locale.ENGLISH, "Very good"));
        CONFIGURATION.put(BD,
                new LocalizedString(Locale.getDefault(), "Bom com Distinção").with(Locale.ENGLISH, "Good with Distinction"));
        CONFIGURATION.put(CA,
                new LocalizedString(Locale.getDefault(), "Com Aproveitamento").with(Locale.ENGLISH, "Successfully"));
        CONFIGURATION.put(DL,
                new LocalizedString(Locale.getDefault(), "Distinção e Louvor").with(Locale.ENGLISH, "Distinction and Praise"));
        CONFIGURATION.put(A, new LocalizedString(Locale.getDefault(), "Aprovado").with(Locale.ENGLISH, "Approved"));
        CONFIGURATION.put(AD, new LocalizedString(Locale.getDefault(), "Aprovado com Distinção").with(Locale.ENGLISH,
                "Approved with Distinction"));

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
        return I.equals(grade.getValue()) || NAPT.equals(grade.getValue());
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
