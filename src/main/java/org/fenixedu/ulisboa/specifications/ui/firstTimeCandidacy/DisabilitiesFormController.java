/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: joao.roxo@qub-it.com 
 *               nuno.pinheiro@qub-it.com
 *
 * 
 * This file is part of FenixEdu Specifications.
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
package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.DisabilityType;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.ui.blue_record.PreviousDegreeOriginInformationFormControllerBlueRecord;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.emory.mathcs.backport.java.util.Collections;
import pt.ist.fenixframework.Atomic;

@Component("org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.DisabilitiesFormController")
@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(DisabilitiesFormController.CONTROLLER_URL)
public abstract class DisabilitiesFormController extends FirstTimeCandidacyAbstractController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/OLD/firsttimecandidacy/{executionYearId}/disabilitiesform";

    public static final String _FILLDISABILITIES_URI = "/filldisabilities";
    public static final String FILLDISABILITIES_URL = CONTROLLER_URL + _FILLDISABILITIES_URI;

    @RequestMapping(value = "/back", method = RequestMethod.GET)
    public String back(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model, final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        return redirect(urlWithExecutionYear(PreviousDegreeOriginInformationFormControllerBlueRecord.INVOKE_BACK_URL, executionYear), model, redirectAttributes);
    }

    @RequestMapping(value = _FILLDISABILITIES_URI, method = RequestMethod.GET)
    public String filldisabilities(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model, final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        if(isFormIsFilled(executionYear, model)) {
            return nextScreen(executionYear, model, redirectAttributes);
        }
        
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }

        List<DisabilityType> allDisabilities = DisabilityType.readAll().collect(Collectors.toList());
        Collections.sort(allDisabilities);
        model.addAttribute("disabilityTypeValues", allDisabilities);

        fillFormIfRequired(executionYear, model);
        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillDisabilities.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/disabilitiesform/filldisabilities";
    }

    private void fillFormIfRequired(final ExecutionYear executionYear, final Model model) {
        if (!model.containsAttribute("disabilitiesForm")) {
            model.addAttribute("disabilitiesForm", createDisabilitiesForm(executionYear, getStudent(model)));
        }
        
        final DisabilitiesForm form = (DisabilitiesForm) model.asMap().get("disabilitiesForm");
        form.setFirstYearRegistration(false);
        for (final Registration registration : getStudent(model).getRegistrationsSet()) {
            if(!registration.isActive()) {
                continue;
            }
            
            if(registration.getRegistrationYear() != executionYear) {
                continue;
            }
            
            form.setFirstYearRegistration(true);
        }
        
    }

    protected DisabilitiesForm createDisabilitiesForm(final ExecutionYear executionYear, final Student student) {
        DisabilitiesForm form = new DisabilitiesForm();
        PersonUlisboaSpecifications personUlisboa = student.getPerson().getPersonUlisboaSpecifications();
        if (personUlisboa != null) {
            form.setHasDisabilities(personUlisboa.getHasDisabilities());
            if (personUlisboa.getDisabilityType() != null) {
                form.setDisabilityType(personUlisboa.getDisabilityType());
            }
            form.setOtherDisabilityType(personUlisboa.getOtherDisabilityType());
            form.setNeedsDisabilitySupport(personUlisboa.getNeedsDisabilitySupport());
            
        }
        
        form.setFirstYearRegistration(false);
        for (final Registration registration : student.getRegistrationsSet()) {
            if(!registration.isActive()) {
                continue;
            }
            
            if(registration.getRegistrationYear() != executionYear) {
                continue;
            }
            
            form.setFirstYearRegistration(true);
        }
        
        form.setAnswered(personUlisboa != null ? personUlisboa.getDisabilitiesFormAnswered() : false);
        
        return form;
    }

    @RequestMapping(value = _FILLDISABILITIES_URI, method = RequestMethod.POST)
    public String filldisabilities(@PathVariable("executionYearId") final ExecutionYear executionYear, final DisabilitiesForm form, final Model model, final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }
        if (!validate(executionYear, form, model)) {
            return filldisabilities(executionYear, model, redirectAttributes);
        }

        try {
            writeData(form, model);
            model.addAttribute("disabilitiesForm", form);
            return nextScreen(executionYear, model, redirectAttributes);
        } catch (Exception de) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "label.error.create")
                    + de.getLocalizedMessage(), model);
            LoggerFactory.getLogger(this.getClass()).error("Exception for user " + AccessControl.getPerson().getUsername());
            de.printStackTrace();
            return filldisabilities(executionYear, model, redirectAttributes);
        }
    }

    protected String nextScreen(final ExecutionYear executionYear, final Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(MotivationsExpectationsFormController.FILLMOTIVATIONSEXPECTATIONS_URL, executionYear), model, redirectAttributes);
    }

    protected boolean validate(final ExecutionYear executionYear, final DisabilitiesForm form, Model model) {
        if (form.getHasDisabilities()) {
            if ((form.getDisabilityType() == null) || form.getDisabilityType().isOther()
                    && StringUtils.isEmpty(form.getOtherDisabilityType())) {
                addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                        "error.candidacy.workflow.DisabilitiesForm.disabilityType.must.be.filled"), model);
                return false;
            }
            if (form.getNeedsDisabilitySupport() == null) {
                addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                        "error.candidacy.workflow.DisabilitiesForm.needsDisabilitySupport.must.be.filled"), model);
                return false;
            }
        }
        return true;
    }

    @Atomic
    protected void writeData(DisabilitiesForm form, final Model model) {
        PersonUlisboaSpecifications personUlisboa = PersonUlisboaSpecifications.findOrCreate(getStudent(model).getPerson());
        
        personUlisboa.setHasDisabilities(form.getHasDisabilities());
        if (form.getHasDisabilities()) {
            personUlisboa.setDisabilityType(form.getDisabilityType());
            personUlisboa.setOtherDisabilityType(form.getOtherDisabilityType());
            personUlisboa.setNeedsDisabilitySupport(form.getNeedsDisabilitySupport());
        } else {
            personUlisboa.setDisabilityType(null);
        }
        
        personUlisboa.setDisabilitiesFormAnswered(true);
    }
    
    public static class DisabilitiesForm {
        private boolean hasDisabilities = false;

        private DisabilityType disabilityType;

        private String otherDisabilityType;

        private Boolean needsDisabilitySupport = null;
        
        private boolean firstYearRegistration;
        
        private boolean answered;

        public boolean getHasDisabilities() {
            return hasDisabilities;
        }

        public void setHasDisabilities(boolean hasDisabilities) {
            this.hasDisabilities = hasDisabilities;
        }

        public DisabilityType getDisabilityType() {
            return disabilityType;
        }

        public void setDisabilityType(DisabilityType disabilityType) {
            this.disabilityType = disabilityType;
        }

        public String getOtherDisabilityType() {
            return otherDisabilityType;
        }

        public void setOtherDisabilityType(String otherDisabilityType) {
            this.otherDisabilityType = otherDisabilityType;
        }

        public Boolean getNeedsDisabilitySupport() {
            return needsDisabilitySupport;
        }

        public void setNeedsDisabilitySupport(Boolean needsDisabilitySupport) {
            this.needsDisabilitySupport = needsDisabilitySupport;
        }
        
        public boolean isFirstYearRegistration() {
            return firstYearRegistration;
        }
        
        public void setFirstYearRegistration(boolean firstYearRegistration) {
            this.firstYearRegistration = firstYearRegistration;
        }
        
        public boolean isAnswered() {
            return answered;
        }
        
        public void setAnswered(boolean answered) {
            this.answered = answered;
        }
    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }
}
