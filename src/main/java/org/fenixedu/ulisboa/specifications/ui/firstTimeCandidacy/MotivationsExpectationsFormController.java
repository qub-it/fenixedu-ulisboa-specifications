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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.PersonUlisboaSpecifications;
import org.fenixedu.ulisboa.specifications.domain.UniversityChoiceMotivationAnswer;
import org.fenixedu.ulisboa.specifications.domain.UniversityDiscoveryMeansAnswer;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;
import edu.emory.mathcs.backport.java.util.Collections;

@Component("org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.MotivationsExpectationsFormController")
@BennuSpringController(value = FirstTimeCandidacyController.class)
@RequestMapping(MotivationsExpectationsFormController.CONTROLLER_URL)
public abstract class MotivationsExpectationsFormController extends FirstTimeCandidacyAbstractController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/OLD/firsttimecandidacy/{executionYearId}/motivationsexpectationsform";

    public static final String _FILLMOTIVATIONSEXPECTATIONS_URI = "/fillmotivationsexpectations";
    public static final String FILLMOTIVATIONSEXPECTATIONS_URL = CONTROLLER_URL + _FILLMOTIVATIONSEXPECTATIONS_URI;

    @RequestMapping(value = "/back", method = RequestMethod.GET)
    public String back(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model, RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        return redirect(urlWithExecutionYear(DisabilitiesFormController.FILLDISABILITIES_URL, executionYear), model, redirectAttributes);
    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @RequestMapping(value = _FILLMOTIVATIONSEXPECTATIONS_URI, method = RequestMethod.GET)
    public String fillmotivationsexpectations(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model, final RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        if(isFormIsFilled(executionYear, model)) {
            return nextScreen(executionYear, model, redirectAttributes);
        }
        
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }
        List<UniversityDiscoveryMeansAnswer> allDiscoveryMeans =
                UniversityDiscoveryMeansAnswer.readAll().collect(Collectors.toList());
        Collections.sort(allDiscoveryMeans);
        model.addAttribute("universityDiscoveryMeansAnswers", allDiscoveryMeans);

        List<UniversityChoiceMotivationAnswer> allChoiceMotivations =
                UniversityChoiceMotivationAnswer.readAll().collect(Collectors.toList());
        Collections.sort(allChoiceMotivations);
        model.addAttribute("universityChoiceMotivationAnswers", allChoiceMotivations);

        fillFormIfRequired(executionYear, model);
        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillMotivationsExpectations.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/motivationsexpectationsform/fillmotivationsexpectations";
    }

    private void fillFormIfRequired(final ExecutionYear executionYear, final Model model) {
        MotivationsExpectationsForm form;
        if (!model.containsAttribute("motivationsexpectationsform")) {
            form = createMotivationsExpectationsForm(executionYear, getStudent(model));

            model.addAttribute("motivationsexpectationsform", form);
        } else {
            form = (MotivationsExpectationsForm) model.asMap().get("motivationsexpectationsform");
        }
        
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
        
        form.populateRequestCheckboxes(request);
    }

    protected MotivationsExpectationsForm createMotivationsExpectationsForm(final ExecutionYear executionYear, final Student student) {
        MotivationsExpectationsForm form = new MotivationsExpectationsForm();
        PersonUlisboaSpecifications personUlisboa = student.getPerson().getPersonUlisboaSpecifications();
        if (personUlisboa != null) {
            form.getUniversityDiscoveryMeansAnswers().addAll(personUlisboa.getUniversityDiscoveryMeansAnswersSet());
            form.getUniversityChoiceMotivationAnswers().addAll(personUlisboa.getUniversityChoiceMotivationAnswersSet());

            form.setOtherUniversityChoiceMotivation(personUlisboa.getOtherUniversityChoiceMotivation());
            form.setOtherUniversityDiscoveryMeans(personUlisboa.getOtherUniversityDiscoveryMeans());
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
        
        form.setAnswered(personUlisboa != null ? personUlisboa.getMotivationsExpectationsFormAnswered() : false);
        
        return form;
    }

    @RequestMapping(value = _FILLMOTIVATIONSEXPECTATIONS_URI, method = RequestMethod.POST)
    public String fillmotivationsexpectations(@PathVariable("executionYearId") final ExecutionYear executionYear, final MotivationsExpectationsForm form, Model model, RedirectAttributes redirectAttributes) {
        addControllerURLToModel(executionYear, model);
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }
        form.populateFormValues(request);
        if (!validate(form, model)) {
            model.addAttribute("motivationsexpectationsform", form);
            return fillmotivationsexpectations(executionYear, model, redirectAttributes);
        }

        try {
            writeData(form, model);
            model.addAttribute("motivationsexpectationsform", form);
            return nextScreen(executionYear, model, redirectAttributes);
        } catch (Exception de) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "label.error.create")
                    + de.getLocalizedMessage(), model);
            LoggerFactory.getLogger(this.getClass()).error("Exception for user " + getStudent(model).getPerson().getUsername());
            de.printStackTrace();
            return fillmotivationsexpectations(executionYear, model, redirectAttributes);
        }
    }

    protected String nextScreen(final ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(SchoolSpecificDataController.CREATE_URL, executionYear), model, redirectAttributes);
    }

    private boolean validate(MotivationsExpectationsForm form, Model model) {
        if (form.getUniversityChoiceMotivationAnswers().size() > 3) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                    "error.candidacy.workflow.MotivationsExpectationsForm.max.three.choices"), model);
            return false;
        }
        if (form.getUniversityDiscoveryMeansAnswers().size() > 3) {
            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                    "error.candidacy.workflow.MotivationsExpectationsForm.max.three.choices"), model);
            return false;
        }

        for (UniversityChoiceMotivationAnswer answer : form.getUniversityChoiceMotivationAnswers()) {
            if (answer.isOther() && StringUtils.isEmpty(form.getOtherUniversityChoiceMotivation())) {
                addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                        "error.candidacy.workflow.MotivationsExpectationsForm.other.must.be.filled"), model);
                return false;
            }
        }

        for (UniversityDiscoveryMeansAnswer answer : form.getUniversityDiscoveryMeansAnswers()) {
            if (answer.isOther() && StringUtils.isEmpty(form.getOtherUniversityDiscoveryMeans())) {
                addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                        "error.candidacy.workflow.MotivationsExpectationsForm.other.must.be.filled"), model);
                return false;
            }
        }
        return true;
    }

    @Atomic
    protected void writeData(MotivationsExpectationsForm form, final Model model) {
        PersonUlisboaSpecifications personUlisboa = PersonUlisboaSpecifications.findOrCreate(getStudent(model).getPerson());
        personUlisboa.getUniversityChoiceMotivationAnswersSet().clear();
        personUlisboa.getUniversityDiscoveryMeansAnswersSet().clear();

        for (UniversityChoiceMotivationAnswer answer : form.getUniversityChoiceMotivationAnswers()) {
            personUlisboa.addUniversityChoiceMotivationAnswers(answer);
        }
        for (UniversityDiscoveryMeansAnswer answer : form.getUniversityDiscoveryMeansAnswers()) {
            personUlisboa.addUniversityDiscoveryMeansAnswers(answer);
        }

        personUlisboa.setOtherUniversityChoiceMotivation(form.getOtherUniversityChoiceMotivation());
        personUlisboa.setOtherUniversityDiscoveryMeans(form.getOtherUniversityDiscoveryMeans());
        personUlisboa.setMotivationsExpectationsFormAnswered(true);
    }
    
    public static class MotivationsExpectationsForm {
        private List<UniversityDiscoveryMeansAnswer> universityDiscoveryMeansAnswers = new ArrayList<>();

        private String otherUniversityDiscoveryMeans;

        private List<UniversityChoiceMotivationAnswer> universityChoiceMotivationAnswers = new ArrayList<>();

        private String otherUniversityChoiceMotivation;
        
        private boolean firstYearRegistration;
        
        private boolean answered;

        public List<UniversityDiscoveryMeansAnswer> getUniversityDiscoveryMeansAnswers() {
            return universityDiscoveryMeansAnswers;
        }

        public void setUniversityDiscoveryMeansAnswers(List<UniversityDiscoveryMeansAnswer> universityDiscoveryMeansAnswers) {
            this.universityDiscoveryMeansAnswers = universityDiscoveryMeansAnswers;
        }

        public List<UniversityChoiceMotivationAnswer> getUniversityChoiceMotivationAnswers() {
            return universityChoiceMotivationAnswers;
        }

        public void setUniversityChoiceMotivationAnswers(List<UniversityChoiceMotivationAnswer> universityChoiceMotivationAnswers) {
            this.universityChoiceMotivationAnswers = universityChoiceMotivationAnswers;
        }

        public String getOtherUniversityDiscoveryMeans() {
            return otherUniversityDiscoveryMeans;
        }

        public void setOtherUniversityDiscoveryMeans(String otherUniversityDiscoveryMeans) {
            this.otherUniversityDiscoveryMeans = otherUniversityDiscoveryMeans;
        }

        public String getOtherUniversityChoiceMotivation() {
            return otherUniversityChoiceMotivation;
        }

        public void setOtherUniversityChoiceMotivation(String otherUniversityChoiceMotivation) {
            this.otherUniversityChoiceMotivation = otherUniversityChoiceMotivation;
        }

        private void populateFormValues(HttpServletRequest request) {
            for (UniversityDiscoveryMeansAnswer answer : UniversityDiscoveryMeansAnswer.readAll().collect(Collectors.toList())) {
                if (request.getParameter("universityDiscoveryMeans_" + answer.getExternalId()) != null) {
                    getUniversityDiscoveryMeansAnswers().add(answer);
                }
            }

            for (UniversityChoiceMotivationAnswer answer : UniversityChoiceMotivationAnswer.readAll()
                    .collect(Collectors.toList())) {
                if (request.getParameter("universityChoiceMotivation_" + answer.getExternalId()) != null) {
                    getUniversityChoiceMotivationAnswers().add(answer);
                }
            }
        }

        private void populateRequestCheckboxes(HttpServletRequest request) {
            for (UniversityDiscoveryMeansAnswer answer : UniversityDiscoveryMeansAnswer.readAll().collect(Collectors.toList())) {
                boolean checked = getUniversityDiscoveryMeansAnswers().contains(answer);
                request.setAttribute("universityDiscoveryMeans_" + answer.getExternalId(), checked);
            }

            for (UniversityChoiceMotivationAnswer answer : UniversityChoiceMotivationAnswer.readAll()
                    .collect(Collectors.toList())) {
                boolean checked = getUniversityChoiceMotivationAnswers().contains(answer);
                request.setAttribute("universityChoiceMotivation_" + answer.getExternalId(), checked);
            }
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
}
