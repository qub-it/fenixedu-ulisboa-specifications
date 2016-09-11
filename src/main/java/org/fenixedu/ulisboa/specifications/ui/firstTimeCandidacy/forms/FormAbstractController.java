package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyAbstractController;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public abstract class FormAbstractController extends FirstTimeCandidacyAbstractController {

    protected CandidancyForm getForm(Model model) {
        return (CandidancyForm) model.asMap().get(getFormVariableName());
    }

    protected void setForm(CandidancyForm form, Model model) {
        form.updateLists();
        model.addAttribute(getFormVariableName() + "Json", getBeanJson(form));
        model.addAttribute(getFormVariableName(), form);
    }

    @RequestMapping
    public String home(@PathVariable("executionYearId") final ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        return redirect(getControllerURLWithExecutionYear(executionYear) + _FILL_URI, model, redirectAttributes);
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

    protected static final String _FILL_URI = "/fill";

    @RequestMapping(value = _FILL_URI, method = RequestMethod.GET)
    public String fill(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        if (isFormIsFilled(executionYear, model)) {
            return nextScreen(executionYear, model, redirectAttributes);
        }

        return fillGetScreen(executionYear, model, redirectAttributes);
    }

    protected static final String _FILL_POSTBACK_URI = "/fillPostback";

    @RequestMapping(value = _FILL_POSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String fillPostBack(@RequestParam(value = "bean", required = true) CandidancyForm bean, Model model) {
        setForm(bean, model);
        return getBeanJson(bean);
    }

    @RequestMapping(value = _FILL_URI, method = RequestMethod.POST)
    public String fillPost(@PathVariable("executionYearId") final ExecutionYear executionYear,
            @RequestParam(value = "bean", required = true) CandidancyForm candidancyForm, final Model model,
            final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        if (!validate(executionYear, candidancyForm, model)) {
            setForm(candidancyForm, model);
            return fillGetScreen(executionYear, model, redirectAttributes);
        }

        try {
            writeData(executionYear, candidancyForm, model);
            fillPostScreen(executionYear, candidancyForm, model, redirectAttributes);
            model.addAttribute("form", candidancyForm);
            return nextScreen(executionYear, model, redirectAttributes);
        } catch (DomainException domainEx) {
            LoggerFactory.getLogger(this.getClass()).error("Exception for user " + AccessControl.getPerson().getUsername());
            domainEx.printStackTrace();
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, domainEx.getKey()),
                    model);
            return fillGetScreen(executionYear, model, redirectAttributes);
        } catch (Exception de) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "label.error.create") + de.getLocalizedMessage(), model);
            LoggerFactory.getLogger(this.getClass()).error("Exception for user " + AccessControl.getPerson().getUsername());
            de.printStackTrace();

            return fillGetScreen(executionYear, model, redirectAttributes);
        }
    }

    public static Stream<District> getDistrictsWithSubdivisionsAndParishes() {
        Predicate<District> hasSubdivisionsWithParishes = district -> getSubdivisionsWithParishes(district).count() != 0l;
        return Bennu.getInstance().getDistrictsSet().stream().filter(hasSubdivisionsWithParishes);
    }

    public static Stream<DistrictSubdivision> getSubdivisionsWithParishes(District district) {
        Predicate<DistrictSubdivision> hasParishes = subdivision -> !subdivision.getParishSet().isEmpty();
        return district.getDistrictSubdivisionsSet().stream().filter(hasParishes);
    }

    protected abstract String fillGetScreen(final ExecutionYear executionYear, Model model,
            RedirectAttributes redirectAttributes);

    protected abstract void fillPostScreen(final ExecutionYear executionYear, final CandidancyForm candidancyForm, Model model,
            RedirectAttributes redirectAttributes);

    protected abstract boolean validate(final ExecutionYear executionYear, final CandidancyForm candidancyForm, Model model);

    protected abstract void writeData(final ExecutionYear executionYear, final CandidancyForm candidancyForm, Model model);

    protected abstract String backScreen(final ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes);

    protected abstract String nextScreen(final ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes);

    protected abstract String getFormVariableName();
}
