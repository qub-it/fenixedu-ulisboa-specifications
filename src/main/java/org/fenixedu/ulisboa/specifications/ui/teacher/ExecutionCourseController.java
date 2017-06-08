package org.fenixedu.ulisboa.specifications.ui.teacher;

import java.util.Optional;

import javax.ws.rs.core.Response.Status;

import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.Professorship;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academic.ui.spring.StrutsFunctionalityController;
import org.fenixedu.academic.ui.spring.controller.teacher.ProjectGroupBean;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

public abstract class ExecutionCourseController extends StrutsFunctionalityController {

    ExecutionCourse executionCourse;

    public ExecutionCourseController() {
        super();
    }

    private Professorship findProfessorship(final ExecutionCourse executionCourse) {
        final Person person = AccessControl.getPerson();
        if (person != null) {
            Optional<Professorship> professorshipOpt =
                    person.getProfessorshipsSet().stream()
                            .filter(professorship -> professorship.getExecutionCourse().equals(executionCourse)).findFirst();
            if (professorshipOpt.isPresent()) {
                Professorship prof = professorshipOpt.get();
                if (!this.getPermission(prof)) {
                    throw new DomainException(Status.FORBIDDEN, "message.error.notAuthorized");
                } else {
                    return prof;
                }
            }
        }
        throw new DomainException(Status.FORBIDDEN, "message.error.notAuthorized");
    }

    @ModelAttribute("projectGroup")
    public ProjectGroupBean setProjectGroup() {
        return new ProjectGroupBean();
    }

    @ModelAttribute("professorship")
    public Professorship setProfessorship(@PathVariable ExecutionCourse executionCourse) {
        return findProfessorship(executionCourse);
    }

    abstract Boolean getPermission(Professorship prof);

    @ModelAttribute("executionCourse")
    public ExecutionCourse getExecutionCourse(@PathVariable ExecutionCourse executionCourse) {
        this.executionCourse = executionCourse;
        return executionCourse;
    }
}