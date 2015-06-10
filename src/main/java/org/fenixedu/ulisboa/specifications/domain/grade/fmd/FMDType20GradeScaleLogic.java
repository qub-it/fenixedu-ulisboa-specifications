package org.fenixedu.ulisboa.specifications.domain.grade.fmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale.GradeScaleLogic;
import org.fenixedu.academic.domain.exceptions.DomainException;

public class FMDType20GradeScaleLogic implements GradeScaleLogic {

    private static final List<String> APPROVED_TEXTUAL_GRADES = Arrays.asList("AP");
    private static final List<String> NOT_APPROVED_TEXTUAL_GRADES = Arrays.asList("F", "D", "NADM", "RE", "AN");
    private static final List<String> NOT_EVALUATED_TEXTUAL_GRADES = Arrays.asList("NA");
    private static final List<String> TEXTUAL_GRADES = new ArrayList<String>();

    static {
        TEXTUAL_GRADES.addAll(APPROVED_TEXTUAL_GRADES);
        TEXTUAL_GRADES.addAll(NOT_APPROVED_TEXTUAL_GRADES);
        TEXTUAL_GRADES.addAll(NOT_EVALUATED_TEXTUAL_GRADES);
    }

    @Override
    public boolean checkNotFinal(final Grade grade) {
        return belongsTo(grade.getValue());
    }

    @Override
    public boolean checkFinal(final Grade grade) {
        return belongsTo(grade.getValue());
    }

    @Override
    public boolean belongsTo(final String value) {
        if (TEXTUAL_GRADES.contains(value)) {
            return true;
        }

        try {
            final double doubleValue = Double.parseDouble(value);
            return doubleValue >= 0 && doubleValue <= 20;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String qualify(final Grade grade) {
        throw new DomainException("GradeScale.unable.to.qualify.given.grade.use.qualitative.scale");
    }

    @Override
    public boolean isNotEvaluated(final Grade grade) {
        final String value = grade.getValue();
        return grade.isEmpty() || NOT_EVALUATED_TEXTUAL_GRADES.contains(value);
    }

    @Override
    public boolean isNotApproved(final Grade grade) {
        final String value = grade.getValue();

        if (NOT_APPROVED_TEXTUAL_GRADES.contains(value) || isNotEvaluated(grade)) {
            return true;
        }

        try {
            return Double.parseDouble(value) < 10;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isApproved(final Grade grade) {
        final String value = grade.getValue();

        if (APPROVED_TEXTUAL_GRADES.contains(value)) {
            return true;
        }

        try {
            final double doubleValue = Double.parseDouble(value);
            return 10 <= doubleValue && doubleValue <= 20;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
