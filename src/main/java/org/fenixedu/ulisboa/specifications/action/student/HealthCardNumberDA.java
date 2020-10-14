/**
 *  Copyright © 2015 Universidade de Lisboa
 *
 *  This file is part of FenixEdu fenixedu-ulisboa-specifications.
 *
 *  FenixEdu fenixedu-ulisboa-specifications is free software: you can redistribute
 *  it and/or modify it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  FenixEdu fenixedu-ulisboa-specifications is distributed in the hope that it will
 *  be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with FenixEdu fenixedu-ulisboa-specifications.
 *  If not, see <http://www.gnu.org/licenses/>.
 **/
package org.fenixedu.ulisboa.specifications.action.student;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.ui.struts.action.administrativeOffice.student.SearchForStudentsDA;
import org.fenixedu.academic.ui.struts.action.administrativeOffice.student.StudentDA;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

@Mapping(path = "/healthCardNumber", module = "academicAdministration", functionality = SearchForStudentsDA.class)
@Forwards({ @Forward(name = "editHealthCardNumber.jsp", path = "/student/editHealthCardNumber.jsp") })
public class HealthCardNumberDA extends StudentDA {
    public static class HealthCardNumberBean implements Serializable {
        private String healthCardNumber;

        public HealthCardNumberBean(Person person) {
            if (person != null) {
                setHealthCardNumber(person.getHealthCardNumber());
            }
        }

        public String getHealthCardNumber() {
            return healthCardNumber;
        }

        public void setHealthCardNumber(String healthCardNumber) {
            this.healthCardNumber = healthCardNumber;
        }
    }

    private Student getAndSetStudent(HttpServletRequest request) {
        final String studentID = getFromRequest(request, "studentID").toString();
        final Student student = FenixFramework.getDomainObject(studentID);
        request.setAttribute("student", student);
        return student;
    }

    public ActionForward prepareEditHealthCardNumber(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        Student student = getAndSetStudent(request);
        request.setAttribute("healthCardNumberBean", new HealthCardNumberBean(student.getPerson()));
        return mapping.findForward("editHealthCardNumber.jsp");
    }

    public ActionForward editHealthCardNumber(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        HealthCardNumberBean bean = getRenderedObject("healthCardNumberBean");
        Student student = getAndSetStudent(request);

        editHealthCardNumber(student.getPerson(), bean.getHealthCardNumber());

        return prepareEditPersonalData(mapping, form, request, response);
    }

    @Atomic
    private void editHealthCardNumber(Person person, String healthCardNumber) {
        person.setHealthCardNumber(healthCardNumber);
    }
}
