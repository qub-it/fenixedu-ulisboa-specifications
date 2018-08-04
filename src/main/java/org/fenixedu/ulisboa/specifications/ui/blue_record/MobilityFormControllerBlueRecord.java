package org.fenixedu.ulisboa.specifications.ui.blue_record;

import java.util.Optional;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecificationsByExecutionYear;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.mobility.MobilityFormControler;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@BennuSpringController(value = BlueRecordEntryPoint.class)
@RequestMapping(MobilityFormControllerBlueRecord.CONTROLLER_URL)
public class MobilityFormControllerBlueRecord extends MobilityFormControler {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/blueRecord/{executionYearId}/mobilityform";

    @Override
    protected String nextScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(CgdDataAuthorizationControllerBlueRecord.CONTROLLER_URL, executionYear), model,
                redirectAttributes);

    }

    @Override
    public Optional<String> accessControlRedirect(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return Optional.empty();
    }

    @Override
    public String backScreen(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        return redirect(urlWithExecutionYear(MotivationsExpectationsFormControllerBlueRecord.INVOKE_BACK_URL, executionYear),
                model, redirectAttributes);
    }

    private static final String _INVOKE_BACK_URI = "/invokeback";
    public static final String INVOKE_BACK_URL = CONTROLLER_URL + _INVOKE_BACK_URI;

    @RequestMapping(value = _INVOKE_BACK_URI, method = RequestMethod.GET)
    public String invokeBack(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        if (isFormIsFilled(executionYear, model)) {
            return backScreen(executionYear, model, redirectAttributes);
        }

        return redirect(getControllerURLWithExecutionYear(executionYear), model, redirectAttributes);
    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    public boolean isFormIsFilled(final ExecutionYear executionYear, final Student student) {
        PersonUlisboaSpecificationsByExecutionYear personUl =
                PersonUlisboaSpecificationsByExecutionYear.findOrCreate(student.getPerson(), executionYear);
        boolean result = validateForm(createMobilityForm(executionYear, student)).isEmpty();
        return result && personUl.isFormAnswered(MobilityFormControllerBlueRecord.class.getSimpleName());
    }

}
