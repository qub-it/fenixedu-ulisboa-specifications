package org.fenixedu.ulisboa.specifications.domain.grade.isa;

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

public class ISATypeQualitativeGradeScaleLogic implements GradeScaleLogic {

    private static final String RE = "RE";
    private static final String AP = "AP";
    private static final String REC = "REC";
    private static final String APDL = "APDL";
    private static final String APD = "APD";
    private static final String APMBD = "APMBD";
    private static final String APMB = "APMB";
    private static final String APB = "APB";
    private static final String F = "F";
    private static final String NADM = "NADM";

    private static final List<String> SORTED_GRADES = Arrays.asList(RE, REC, F, NADM, AP, APDL, APD, APMBD, APB);
    private static final QualitativeGradeComparator COMPARATOR = new QualitativeGradeComparator(SORTED_GRADES);

    private static Map<String, LocalizedString> CONFIGURATION = new HashMap<String, LocalizedString>();

    static {

        CONFIGURATION.put(RE, new LocalizedString(Locale.getDefault(), "Reprovado").with(Locale.ENGLISH, "Not Approved"));
        CONFIGURATION.put(REC, new LocalizedString(Locale.getDefault(), "Recusado").with(Locale.ENGLISH, "Refused"));
        CONFIGURATION.put(F, new LocalizedString(Locale.getDefault(), "Faltou").with(Locale.ENGLISH, "Missed"));
        CONFIGURATION.put(NADM, new LocalizedString(Locale.getDefault(), "Não Admitido").with(Locale.ENGLISH, "Not Admitted"));
        CONFIGURATION.put(AP, new LocalizedString(Locale.getDefault(), "Aprovado").with(Locale.ENGLISH, "Approved"));
        CONFIGURATION.put(APDL, new LocalizedString(Locale.getDefault(), "Aprovado com Distinção e Louvor").with(Locale.ENGLISH,
                "Approved with Distinction and Honors"));
        CONFIGURATION.put(APD, new LocalizedString(Locale.getDefault(), "Aprovado com Distinção").with(Locale.ENGLISH,
                "Approved with Distinction"));
        CONFIGURATION.put(APMBD, new LocalizedString(Locale.getDefault(), "Aprovado com Muito Bom com Distinção")
                .with(Locale.ENGLISH, "Approved Very Good with Distinction"));
        CONFIGURATION.put(APMB, new LocalizedString(Locale.getDefault(), "Aprovado com Muito Bom").with(Locale.ENGLISH,
                "Approved with Very Good"));
        CONFIGURATION.put(APB,
                new LocalizedString(Locale.getDefault(), "Aprovado com Bom").with(Locale.ENGLISH, "Approved with Good"));

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
        return F.equals(grade.getValue()) || RE.equals(grade.getValue()) || REC.equals(grade.getValue());
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
