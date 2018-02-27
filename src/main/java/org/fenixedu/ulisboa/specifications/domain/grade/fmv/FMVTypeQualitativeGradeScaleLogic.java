package org.fenixedu.ulisboa.specifications.domain.grade.fmv;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale.GradeScaleLogic;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.grade.common.QualitativeGradeComparator;

public class FMVTypeQualitativeGradeScaleLogic implements GradeScaleLogic {

    private static final String B = "B";
    private static final String MB = "MB";
    private static final String SU = "SU";
    private static final String BD = "BD";
    private static final String MBD = "MBD";
    private static final String AMBD = "AMBD";
    private static final String AMB = "AMB";
    private static final String A = "A";
    private static final String ADL = "ADL";

    private static final List<String> SORTED_GRADES = Arrays.asList(SU, A, ADL, B, BD, AMB, MB, MBD, AMBD);
    private static final QualitativeGradeComparator COMPARATOR = new QualitativeGradeComparator(SORTED_GRADES);

    private static Map<String, LocalizedString> CONFIGURATION = new HashMap<String, LocalizedString>();

    static {
        CONFIGURATION.put(B, new LocalizedString(Locale.getDefault(), "BOM").with(Locale.ENGLISH, "GOOD"));
        CONFIGURATION.put(MB, new LocalizedString(Locale.getDefault(), "MUITO BOM").with(Locale.ENGLISH, "Very good"));
        CONFIGURATION.put(SU, new LocalizedString(Locale.getDefault(), "Suficiente").with(Locale.ENGLISH, "Sufficient"));
        CONFIGURATION.put(BD,
                new LocalizedString(Locale.getDefault(), "Bom com Distinção").with(Locale.ENGLISH, "Good with Distinction"));
        CONFIGURATION.put(MBD, new LocalizedString(Locale.getDefault(), "Muito Bom com Distinção").with(Locale.ENGLISH,
                "Very good with Distinction"));
        CONFIGURATION.put(AMBD, new LocalizedString(Locale.getDefault(), "Aprovado com Muito Bom com Distinção").with(
                Locale.ENGLISH, "Approved with Very Good with Distinction"));
        CONFIGURATION.put(AMB, new LocalizedString(Locale.getDefault(), "Aprovado com Muito Bom").with(Locale.ENGLISH,
                "Approved with Very Good"));
        CONFIGURATION.put(A, new LocalizedString(Locale.getDefault(), "Aprovado").with(Locale.ENGLISH, "Approved"));
        CONFIGURATION.put(ADL, new LocalizedString(Locale.getDefault(), "Aprovado c/distinção e louvor").with(Locale.ENGLISH,
                "Approved with distinction and honors"));

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
