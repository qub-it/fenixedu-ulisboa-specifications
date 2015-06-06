package org.fenixedu.ulisboa.specifications.domain.grade.ff;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale.GradeScaleLogic;
import org.fenixedu.commons.i18n.LocalizedString;

public class FFTypeQualitativeGradeScaleLogic implements GradeScaleLogic {

    private static final String I = "I";
    private static final String SU = "SU";
    private static final String B = "B";
    private static final String MB = "MB";
    private static final String MBDL = "MBDL";
    private static final String BD = "BD";
    private static final String E = "E";
    private static final String R = "R";
    private static final String AD = "AD";
    private static final String ADL = "ADL";
    private static final String A = "A";
    private static final String AMB = "AMB";

    private static Map<String, LocalizedString> CONFIGURATION = new HashMap<String, LocalizedString>();

    static {
        CONFIGURATION.put(I, new LocalizedString(Locale.getDefault(), "Insuficiente").with(Locale.ENGLISH, "Not Enough"));
        CONFIGURATION.put(SU, new LocalizedString(Locale.getDefault(), "Suficiente").with(Locale.ENGLISH, "Enough"));
        CONFIGURATION.put(B, new LocalizedString(Locale.getDefault(), "Bom").with(Locale.ENGLISH, "Good"));
        CONFIGURATION.put(MB, new LocalizedString(Locale.getDefault(), "Muito Bom").with(Locale.ENGLISH, "Very good"));
        CONFIGURATION.put(MBDL, new LocalizedString(Locale.getDefault(), "Muito Bom c/ Distinção e Louvor").with(Locale.ENGLISH,
                "Very Good with Distinction and Praise"));
        CONFIGURATION.put(BD,
                new LocalizedString(Locale.getDefault(), "Bom com Distinção").with(Locale.ENGLISH, "Good with Distinction"));
        CONFIGURATION.put(E, new LocalizedString(Locale.getDefault(), "Excelente").with(Locale.ENGLISH, "Excellent"));
        CONFIGURATION.put(R, new LocalizedString(Locale.getDefault(), "Recusado").with(Locale.ENGLISH, "Refused"));
        CONFIGURATION.put(AD, new LocalizedString(Locale.getDefault(), "Aprovado c/distinção").with(Locale.ENGLISH,
                "Approved with distinction"));
        CONFIGURATION.put(ADL, new LocalizedString(Locale.getDefault(), "Aprovado c/distinção e louvor").with(Locale.ENGLISH,
                "Approved with distinction and honors"));
        CONFIGURATION.put(A, new LocalizedString(Locale.getDefault(), "Aprovado").with(Locale.ENGLISH, "Approved"));
        CONFIGURATION.put(AMB, new LocalizedString(Locale.getDefault(), "Aprovado com Muito Bom").with(Locale.ENGLISH,
                "Approved with Very Good"));

    }

    @Override
    public boolean belongsTo(String value) {
        return value != null && CONFIGURATION.containsKey(value);

    }

    @Override
    public boolean checkFinal(Grade grade) {
        return grade != null && belongsTo(grade.getValue());
    }

    @Override
    public boolean checkNotFinal(Grade grade) {
        return grade != null && belongsTo(grade.getValue());
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
}
