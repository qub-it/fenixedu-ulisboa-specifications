package org.fenixedu.academic.ui.renderers.providers.enrollment.bolonha;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.dto.student.enrollment.bolonha.BolonhaStudentOptionalEnrollmentBean;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class DegreeCurricularPlansForDegreeForOptionalEnrollment extends DegreeCurricularPlansForDegree {

    @Override
    public Object provide(Object source, Object currentValue) {
        final List<DegreeCurricularPlan> result = Lists.newArrayList();

        if (source instanceof BolonhaStudentOptionalEnrollmentBean) {

            final BolonhaStudentOptionalEnrollmentBean bean = (BolonhaStudentOptionalEnrollmentBean) source;
            result.addAll(getDegreeCurricularPlans(bean));

            final DegreeCurricularPlan current = (DegreeCurricularPlan) currentValue;
            if (!result.contains(current)) {
                bean.setDegreeCurricularPlan(null);
            }
        }

        Collections.sort(result, DegreeCurricularPlan.COMPARATOR_BY_NAME);
        return result;
    }

    static private Set<DegreeCurricularPlan> getDegreeCurricularPlans(final BolonhaStudentOptionalEnrollmentBean bean) {
        final Set<DegreeCurricularPlan> result = Sets.newHashSet();

        final DegreeType degreeType = bean.getDegreeType();
        final Degree degree = bean.getDegree();
        if (degreeType != null && degree != null) {

            if (degreeType != degree.getDegreeType()) {
                bean.setDegree(null);
                bean.setDegreeType(null);

            } else {

                for (final DegreeCurricularPlan iter : degree.getDegreeCurricularPlansSet()) {
                    if (iter.isActive() && iter.hasExecutionDegreeFor(bean.getExecutionYear())) {
                        result.add(iter);
                    }
                }
            }
        }

        return result;
    }

}
