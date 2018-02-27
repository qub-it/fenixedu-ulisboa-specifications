package org.fenixedu.ulisboa.specifications.domain.grade.fa;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale.GradeScaleLogic;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.grade.common.QualitativeGradeComparator;

public class FATypeQualitativeGradeScaleLogic implements GradeScaleLogic {
    
    private static final String RE = "RE";
    private static final String R = "R";
    private static final String RM = "RM";
    private static final String A = "A";
    private static final String SU = "SU";
    private static final String ABU = "ABU";
    private static final String AD = "AD";
    private static final String ADL = "ADL";
    private static final String B = "B";
    private static final String ABM = "ABM";
    private static final String MB = "MB";
    private static final String AMBM = "AMBM";
    private static final String AMBU = "AMBU";
    private static final String AMBD = "AMBD";
    private static final String AMB = "AMB";
    private static final String E = "E";

    private static final List<String> SORTED_GRADES = Arrays.asList(RE, R, RM, A, SU, ABU, AD, ADL, B, ABM, MB, AMBM, AMBU, AMBD, AMB, E);
    private static final QualitativeGradeComparator COMPARATOR = new QualitativeGradeComparator(SORTED_GRADES);

    private static Map<String, LocalizedString> CONFIGURATION = new HashMap<String, LocalizedString>();

    static {
        CONFIGURATION.put(RE, new LocalizedString(Locale.getDefault(), "Reprovado(a)").with(Locale.ENGLISH, "Not Approved"));
        CONFIGURATION.put(R, new LocalizedString(Locale.getDefault(), "Recusado").with(Locale.ENGLISH, "Refused"));
        CONFIGURATION.put(RM, new LocalizedString(Locale.getDefault(), "Recusado(a) Maioria").with(Locale.ENGLISH, "Refused by Majority"));
        CONFIGURATION.put(A, new LocalizedString(Locale.getDefault(), "Aprovado(a)").with(Locale.ENGLISH, "Approved"));
        CONFIGURATION.put(SU, new LocalizedString(Locale.getDefault(), "Suficiente").with(Locale.ENGLISH, "Sufficient"));
        CONFIGURATION.put(ABU, new LocalizedString(Locale.getDefault(), "Aprovado(a) Bom, por Unanimidade").with(Locale.ENGLISH, "Approved Good by Unanimity"));
        CONFIGURATION.put(AD, new LocalizedString(Locale.getDefault(), "Aprovado(a) com Distinção").with(Locale.ENGLISH, "Approved with Distinction"));
        CONFIGURATION.put(ADL, new LocalizedString(Locale.getDefault(), "Aprovado(a)  com Distinção e Louvor").with(Locale.ENGLISH, "Approved with Distinction and Honors"));
        CONFIGURATION.put(B, new LocalizedString(Locale.getDefault(), "Bom").with(Locale.ENGLISH, "Good"));
        CONFIGURATION.put(ABM, new LocalizedString(Locale.getDefault(), "Aprovado(a) Bom, por Maioria").with(Locale.ENGLISH, "Approved Good by Majority"));
        CONFIGURATION.put(MB, new LocalizedString(Locale.getDefault(), "Muito Bom").with(Locale.ENGLISH, "Very good"));
        CONFIGURATION.put(AMBM, new LocalizedString(Locale.getDefault(), "Aprovado(a) Muito Bom, por Maioria").with(Locale.ENGLISH, "Approved Very Good by Majority"));
        CONFIGURATION.put(AMBU, new LocalizedString(Locale.getDefault(), "Aprovado(a) Muito Bom, por Unanimidade").with(Locale.ENGLISH, "Approved Very Good by Unanimity"));
        CONFIGURATION.put(AMBD, new LocalizedString(Locale.getDefault(), "Aprovado(a) Muito Bom, com Distinção").with(Locale.ENGLISH, "Approved Very Good with Distinction"));
        CONFIGURATION.put(AMB, new LocalizedString(Locale.getDefault(), "Aprovado(a) Muito Bom").with(Locale.ENGLISH, "Approved Very Good"));
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
        return R.equals(grade.getValue()) || RE.equals(grade.getValue()) || RM.equals(grade.getValue());
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
