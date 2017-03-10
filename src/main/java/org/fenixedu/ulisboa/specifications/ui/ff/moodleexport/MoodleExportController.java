/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: nuno.pinheiro@qub-it.com
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
package org.fenixedu.ulisboa.specifications.ui.ff.moodleexport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(MoodleExportController.CONTROLLER_URL)
@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.moodleExport",
        accessGroup = "logged")
public class MoodleExportController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/ff/moodleexport";

    @RequestMapping
    public String home(Model model) {
        return "ff/moodleExport/homescreen";
    }

    private MoodleExportBean getMoodleExportBean(Model model) {
        return (MoodleExportBean) model.asMap().get("MoodleExportBean");
    }

    private void setMoodleExportBean(MoodleExportBean MoodleExportBean, Model model) {
        model.addAttribute("MoodleExportBean", MoodleExportBean);
    }

    private static final String _LISTSTUDENTS_URI = "/liststudents";
    public static final String LISTSTUDENTS_URL = CONTROLLER_URL + _LISTSTUDENTS_URI;

    @RequestMapping(value = _LISTSTUDENTS_URI)
    public String listStudents(Model model) {
        List<MoodleExportBean> liststudentsResultsDataSet =
                getSearchUniverseListStudentsDataSet().map(student -> populateStudents(student))
                        .filter(meb -> meb.getCourses().size() > 0).collect(Collectors.toList());

        //add the results dataSet to the model
        model.addAttribute("resultsDataSet", liststudentsResultsDataSet);

        int maxCourses = liststudentsResultsDataSet.stream().mapToInt(mb -> mb.getCourses().size()).max().orElse(0);
        model.addAttribute("numberOfCourseColumns", maxCourses);

        model.addAttribute("listType", "Alunos");
        return "ff/moodleExport/list";
    }

    @RequestMapping(value = _LISTTEACHERS_URI)
    public String listTeachers(Model model) {
        List<MoodleExportBean> listteachersResultsDataSet = getSearchUniverseListTeachersDataSet().map(p -> populateProfessors(p))
                .filter(meb -> meb.getCourses().size() > 0).collect(Collectors.toList());

        //add the results dataSet to the model
        model.addAttribute("resultsDataSet", listteachersResultsDataSet);
        //add the max number of courses
        int maxCourses = listteachersResultsDataSet.stream().mapToInt(mb -> mb.getCourses().size()).max().orElse(0);
        model.addAttribute("numberOfCourseColumns", maxCourses);

        model.addAttribute("listType", "Docentes");
        return "ff/moodleExport/list";
    }

    private MoodleExportBean populateStudents(Student student) {
        MoodleExportBean moodleExportBean = new MoodleExportBean();
        populatePerson(student.getPerson(), moodleExportBean);
        moodleExportBean.setRole1("student");
        ExecutionYear currentExecutionYear = ExecutionYear.readCurrentExecutionYear();
        String yearName = calculateYearName(currentExecutionYear);

        List<String> coursesNames = new ArrayList<>();
        List<String> roles = new ArrayList<>();
//        List<String> coursesNames = student.getActiveRegistrations().stream()
//                .flatMap(r -> r.getEnrolments(currentExecutionYear).stream()).flatMap(en -> en.getExecutionCourses().stream())
//                .map(ec -> ec.getName() + " " + yearName).collect(Collectors.toList());
        student.getActiveRegistrations()
                .stream().flatMap(r -> r.getEnrolments(currentExecutionYear).stream()).flatMap(en -> en.getExecutionCourses()
                        .stream().filter(ec -> ec.getExecutionPeriod().getExecutionYear() == currentExecutionYear))
                .forEach(ec -> {
                    coursesNames.add(ec.getName() + " " + yearName);
                    roles.add("student");
                });
        moodleExportBean.setCourses(coursesNames);
        moodleExportBean.setRoles(roles);

        return moodleExportBean;
    }

    private void populatePerson(Person person, MoodleExportBean moodleExportBean) {
        String firstAndLastName = person.getFirstAndLastName();
        String firstName = firstAndLastName.split(" ")[0];
        String lastName = firstAndLastName.split(" ")[1];
        moodleExportBean.setFirstname(firstName);
        moodleExportBean.setLastname(lastName);

        moodleExportBean.setEmail(person.getInstitutionalEmailAddressValue());
        if (StringUtils.isEmpty(person.getInstitutionalEmailAddressValue())) {
            final String defaultEmailAddress = person.getDefaultEmailAddressValue();
            if (StringUtils.isNotEmpty(defaultEmailAddress) && defaultEmailAddress.endsWith("@campus.ul.pt")) {
                moodleExportBean.setEmail(defaultEmailAddress);
            }
        }

        moodleExportBean.setUsername(person.getUsername());
        moodleExportBean.setAuth("shibboleth");
    }

    private MoodleExportBean populateProfessors(Person person) {
        MoodleExportBean moodleExportBean = new MoodleExportBean();
        populatePerson(person, moodleExportBean);
        moodleExportBean.setRole1("teacher");
        ExecutionYear currentExecutionYear = ExecutionYear.readCurrentExecutionYear();
        String yearName = calculateYearName(currentExecutionYear);
        List<String> coursesNames = new ArrayList<>();
        List<String> roles = new ArrayList<>();
//        List<String> coursesNames = person.getProfessorships(currentExecutionYear).stream()
//                .map(p -> p.getExecutionCourse().getName() + " " + yearName).collect(Collectors.toList());
        for (Professorship professorship : person.getProfessorships(currentExecutionYear)) {
            coursesNames.add(professorship.getExecutionCourse().getName() + " " + yearName);
            roles.add(professorship.isResponsibleFor() ? "editingteacher" : "teacher");
        }
        moodleExportBean.setCourses(coursesNames);
        moodleExportBean.setRoles(roles);
        return moodleExportBean;
    }

    private String calculateYearName(ExecutionYear currentExecutionYear) {
        //Transforms fenix execution year format 20XX/20YY to the required format [XX-YY]
        String yearName = "[";
        yearName += currentExecutionYear.getName().substring(2, 4);
        yearName += "-";
        yearName += currentExecutionYear.getName().substring(7, 9);
        yearName += "]";
        return yearName;
    }

    private Stream<Student> getSearchUniverseListStudentsDataSet() {
        return ExecutionYear.readCurrentExecutionYear().getExecutionPeriodsSet().stream()
                .flatMap(es -> es.getEnrolmentsSet().stream()).map(en -> en.getStudent()).distinct();
    }

//				
    private static final String _LISTTEACHERS_URI = "/listteachers";
    public static final String LISTTEACHERS_URL = CONTROLLER_URL + _LISTTEACHERS_URI;

    private Stream<Person> getSearchUniverseListTeachersDataSet() {
        return ExecutionYear.readCurrentExecutionYear().getExecutionPeriodsSet().stream()
                .flatMap(es -> es.getAssociatedExecutionCoursesSet().stream()).flatMap(ec -> ec.getProfessorshipsSet().stream())
                .map(p -> p.getPerson()).distinct();
    }

    public static class MoodleExportBean {
        String username, firstname, lastname, email, role1, auth;
        Collection<String> courses;
        Collection<String> roles;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRole1() {
            return role1;
        }

        public void setRole1(String role1) {
            this.role1 = role1;
        }

        public String getAuth() {
            return auth;
        }

        public void setAuth(String auth) {
            this.auth = auth;
        }

        public Collection<String> getCourses() {
            return courses;
        }

        public void setCourses(Collection<String> courses) {
            this.courses = courses;
        }

        public Collection<String> getRoles() {
            return roles;
        }

        public void setRoles(Collection<String> roles) {
            this.roles = roles;
        }

    }
}
