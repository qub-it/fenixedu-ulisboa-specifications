package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.enrolments;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyAbstractController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public abstract class EnrolmentAbstractController extends FirstTimeCandidacyAbstractController {

    @RequestMapping
    public String home(@PathVariable("executionYearId") final ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        return redirect(getControllerURLWithExecutionYear(executionYear) + _ENROL_URI, model, redirectAttributes);
    }

    protected static final String _BACK_URI = "/back";

    @RequestMapping(value = _BACK_URI, method = RequestMethod.GET)
    public String back(@PathVariable("executionYearId") ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        return backScreen(executionYear, model, redirectAttributes);
    }

    protected static final String _ENROL_URI = "/enrol";

    @RequestMapping(value = _ENROL_URI, method = RequestMethod.GET)
    public String enrol(@PathVariable("executionYearId") ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes, HttpServletRequest request) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        return enrolScreen(executionYear, model, redirectAttributes, request);
    }

    protected static final String _SHOW_URI = "/show";

    @RequestMapping(value = _SHOW_URI, method = RequestMethod.GET)
    public String show(@PathVariable("executionYearId") ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes, HttpServletRequest request) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        return showScreen(executionYear, model, redirectAttributes, request);
    }

    protected static final String _CONTINUE_URI = "/continue";

    @RequestMapping(value = _CONTINUE_URI, method = RequestMethod.GET)
    public String next(@PathVariable("executionYearId") ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        return nextScreen(executionYear, model, redirectAttributes);
    }

    @Override
    protected abstract String getControllerURL();

    protected abstract String enrolScreen(final ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes,
            HttpServletRequest request);

    protected abstract String showScreen(final ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes,
            HttpServletRequest request);

    protected abstract String backScreen(final ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes);

    protected abstract String nextScreen(final ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes);

    @Override
    protected abstract Student getStudent(Model model);

    @Override
    public abstract boolean isFormIsFilled(ExecutionYear executionYear, Student student);
}
