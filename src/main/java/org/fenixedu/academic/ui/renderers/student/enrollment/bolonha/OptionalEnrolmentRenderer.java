/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 *
 * 
 * This file is part of FenixEdu fenixedu-ulisboa-specifications.
 *
 * FenixEdu Specifications is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Specifications is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Specifications.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.academic.ui.renderers.student.enrollment.bolonha;

import pt.ist.fenixWebFramework.renderers.layouts.Layout;

public class OptionalEnrolmentRenderer extends BolonhaStudentOptionalEnrollmentInputRenderer {

    private String curricularCoursesToEnrol = "smalltxt, smalltxt aright, smalltxt aright, aright";

    @Override
    public Integer getInitialWidth() {
        // qubExtension
        return 80;
    }

    private String[] getCurricularCourseClasses() {
        return curricularCoursesToEnrol.split(",");
    }

    protected String getCurricularCourseNameClasses() {
        return getCurricularCourseClasses()[0];
    }

    protected String getCurricularCourseYearClasses() {
        return getCurricularCourseClasses()[1];
    }

    protected String getCurricularCourseEctsClasses() {
        return getCurricularCourseClasses()[2];
    }

    protected String getCurricularCourseLinkClasses() {
        return getCurricularCourseClasses()[3];
    }

    @Override
    protected Layout getLayout(final Object object, final Class type) {
        final OptionalEnrolmentLayout result = new OptionalEnrolmentLayout();
        result.setRenderer(this);
        return result;
    }

}
