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
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

@Mapping(path = "/vaccinationValidity", module = "academicAdministration", functionality = SearchForStudentsDA.class)
@Forwards({ @Forward(name = "editVaccination.jsp", path = "/student/editVaccination.jsp") })
public class VaccinationValidityDA extends StudentDA {
    public static class VaccinationValidityBean implements Serializable {
        private LocalDate vaccinationValidity = null;

        public VaccinationValidityBean(Person person) {
            PersonUlisboaSpecifications personUlisboaSpecifications = person.getPersonUlisboaSpecifications();
            if (personUlisboaSpecifications != null) {
                setVaccinationValidity(personUlisboaSpecifications.getVaccinationValidity());
            }
        }

        public LocalDate getVaccinationValidity() {
            return vaccinationValidity;
        }

        public void setVaccinationValidity(LocalDate vaccinationValidity) {
            this.vaccinationValidity = vaccinationValidity;
        }
    }

    private Student getAndSetStudent(HttpServletRequest request) {
        final String studentID = getFromRequest(request, "studentID").toString();
        final Student student = FenixFramework.getDomainObject(studentID);
        request.setAttribute("student", student);
        return student;
    }

    public ActionForward prepareEditVaccinationValidity(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        Student student = getAndSetStudent(request);
        request.setAttribute("vaccinationBean", new VaccinationValidityBean(student.getPerson()));
        return mapping.findForward("editVaccination.jsp");
    }

    public ActionForward editVaccinationValidity(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        VaccinationValidityBean bean = getRenderedObject("vaccinationBean");
        Student student = getAndSetStudent(request);

        editVaccinationValidity(student.getPerson(), bean.getVaccinationValidity());

        return prepareEditPersonalData(mapping, form, request, response);
    }

    @Atomic
    private void editVaccinationValidity(Person person, LocalDate vaccinationDate) {
        PersonUlisboaSpecifications personUlisboaSpecifications = PersonUlisboaSpecifications.findOrCreate(person);
        personUlisboaSpecifications.setVaccinationValidity(vaccinationDate);
    }
}
