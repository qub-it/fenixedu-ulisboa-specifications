package org.fenixedu.ulisboa.specifications.domain.grade.rul;

import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale.GradeScaleLogic;

public class RULType20GradeScaleLogic implements GradeScaleLogic {

    @Override
    public boolean checkFinal(Grade grade) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean checkNotFinal(Grade grade) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String qualify(Grade grade) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isNotEvaluated(Grade grade) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isNotApproved(Grade grade) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isApproved(Grade grade) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean belongsTo(String value) {
        // TODO Auto-generated method stub
        return false;
    }

}
