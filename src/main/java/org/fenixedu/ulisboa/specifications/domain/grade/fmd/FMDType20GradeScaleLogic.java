package org.fenixedu.ulisboa.specifications.domain.grade.fmd;

import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale.GradeScaleLogic;

public class FMDType20GradeScaleLogic implements GradeScaleLogic {

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

    //F - 7
    //
    
//    @Override
//    public boolean checkFinal(final Grade grade) {
//        final String value = grade.getValue();
//        if (value.equals(NA) || value.equals(RE)) {
//            return true;
//        }
//
//        try {
//            final int intValue = Integer.parseInt(value);
//            return intValue >= 10 && intValue <= 20;
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }
//
//    @Override
//    public boolean checkNotFinal(final Grade grade) {
//        final String value = grade.getValue();
//        if (value.equals(NA) || value.equals(RE)) {
//            return true;
//        }
//
//        try {
//            final double doubleValue = Double.parseDouble(value);
//            return doubleValue >= 0 && doubleValue <= 20;
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }
//
//    @Override
//    public String qualify(final Grade grade) {
//        if (grade.getGradeScale() != GradeScale.TYPE20) {
//            return StringUtils.EMPTY;
//        }
//
//        try {
//            final int intValue = Integer.parseInt(grade.getValue());
//
//            if (18 <= intValue && intValue <= 20) {
//                return BundleUtil.getString(Bundle.APPLICATION, "label.grade.a");
//            } else if (16 <= intValue && intValue <= 17) {
//                return BundleUtil.getString(Bundle.APPLICATION, "label.grade.b");
//            } else if (14 <= intValue && intValue <= 15) {
//                return BundleUtil.getString(Bundle.APPLICATION, "label.grade.c");
//            } else if (10 <= intValue && intValue <= 13) {
//                return BundleUtil.getString(Bundle.APPLICATION, "label.grade.d");
//            } else {
//                throw new DomainException("GradeScale.unable.to.qualify.given.grade");
//            }
//        } catch (NumberFormatException e) {
//            throw new DomainException("GradeScale.unable.to.qualify.given.grade");
//        }
//    }
//
//    @Override
//    public boolean isNotEvaluated(final Grade grade) {
//        final String value = grade.getValue();
//        return grade.isEmpty() || value.equals(GradeScale.NA);
//    }
//
//    @Override
//    public boolean isNotApproved(final Grade grade) {
//        final String value = grade.getValue();
//        if (value.equals(GradeScale.RE) || isNotEvaluated(grade)) {
//            return true;
//        }
//
//        try {
//            return Integer.parseInt(value) < 10;
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }
//
//    @Override
//    public boolean isApproved(final Grade grade) {
//        final String value = grade.getValue();
//        if (value.equals(GradeScale.AP)) {
//            return true;
//        }
//
//        try {
//            final int intValue = Integer.parseInt(value);
//            return 10 <= intValue && intValue <= 20;
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }
//
//    @Override
//    public boolean belongsTo(final String value) {
//        if (value.equals(NA) || value.equals(RE)) {
//            return true;
//        }
//
//        try {
//            final double doubleValue = Double.parseDouble(value);
//            return doubleValue >= 0 && doubleValue <= 20;
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }

}
