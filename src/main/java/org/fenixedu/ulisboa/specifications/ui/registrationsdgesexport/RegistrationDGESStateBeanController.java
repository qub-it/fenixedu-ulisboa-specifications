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
 * This file is part of FenixEdu ULisboaSpecifications.
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
package org.fenixedu.ulisboa.specifications.ui.registrationsdgesexport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.EntryPhase;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.candidacy.CandidacySituationType;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.candidacy.FirstTimeCandidacy;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.FenixFramework;

@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.registrationsDgesExport",
        accessGroup = "logged")
@RequestMapping(RegistrationDGESStateBeanController.CONTROLLER_URL)
public class RegistrationDGESStateBeanController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/registrationsdgesexport";

    private static final String _SEARCH_URI = "/";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping
    public String search(Model model, @RequestParam(required = false) ExecutionYear executionYear,
            @RequestParam(required = false) Integer phase) {
        executionYear = executionYear != null ? executionYear : ExecutionYear.readCurrentExecutionYear();
        phase = phase != null ? phase : 1;
        List<RegistrationDGESStateBean> searchregistrationdgesstatebeanResultsDataSet =
                filterSearchRegistrationDGESStateBean(executionYear, phase);

        model.addAttribute("searchregistrationdgesstatebeanResultsDataSet", searchregistrationdgesstatebeanResultsDataSet);
        model.addAttribute("executionYears",
                Bennu.getInstance().getExecutionYearsSet().stream().sorted().collect(Collectors.toList()));
        model.addAttribute("phases", getPhases());
        model.addAttribute("selectedExecutionYear", executionYear);
        model.addAttribute("selectedPhase", phase);
        return "registrationsdgesexport/search";
    }

    @RequestMapping(value = "/reactivate/{candidacyId}")
    public String reActivateCandidacy(Model model, @PathVariable("candidacyId") String candidacyId,
            RedirectAttributes redirectAttributes) {
        FirstTimeCandidacy candidacy = FenixFramework.getDomainObject(candidacyId);
        if (!FenixFramework.isDomainObjectValid(candidacy)) {
            throw new RuntimeException("Invalid externalId: " + candidacyId + " is not a FirstTimeCandidacy");
        }

        candidacy.revertCancel();
        return redirect(CONTROLLER_URL, model, redirectAttributes);
    }

    private Collection<Integer> getPhases() {
        EntryPhase[] values = EntryPhase.values();
        List<Integer> phases = new ArrayList<>();
        for (EntryPhase phase : values) {
            phases.add(phase.getPhaseNumber());
        }
        return phases;
    }

    private List<RegistrationDGESStateBean> filterSearchRegistrationDGESStateBean(ExecutionYear executionYear, int phase) {
        Predicate<? super StudentCandidacy> hasDgesImportationForCurrentPhase =
                sc -> sc.getDgesStudentImportationProcess() != null
                        && sc.getDgesStudentImportationProcess().getEntryPhase().getPhaseNumber() == phase;
        return executionYear.getStudentCandidacies().stream().filter(hasDgesImportationForCurrentPhase)
                .map(sc -> populateBean(sc)).collect(Collectors.toList());
    }

    private RegistrationDGESStateBean populateBean(StudentCandidacy studentCandidacy) {
        String degreeCode = studentCandidacy.getDegreeCurricularPlan().getDegree().getMinistryCode();
        String documentIdNumber = studentCandidacy.getPerson().getDocumentIdNumber();
        String candidacyState =
                BundleUtil.getString("resources/EnumerationResources", studentCandidacy.getActiveCandidacySituationType()
                        .getQualifiedName());
        String name = studentCandidacy.getPerson().getName();
        String registrationStatus = "";
        if (studentCandidacy.getActiveCandidacySituationType().equals(CandidacySituationType.REGISTERED)) {
            registrationStatus = "Sim";
        } else {
            registrationStatus = "Não";
        }
        return new RegistrationDGESStateBean(studentCandidacy.getExternalId(), degreeCode, documentIdNumber, candidacyState,
                name, registrationStatus);
    }

    public static class RegistrationDGESStateBean {
        private String candidacyId;
        private String degreeCode;
        private String idNumber;
        private String candidacyState;
        private String name;
        private String registrationState;

        public RegistrationDGESStateBean(String candidacyId, String degreeCode, String idNumber, String candidacyState,
                String name, String registrationState) {
            super();
            this.candidacyId = candidacyId;
            this.degreeCode = degreeCode;
            this.idNumber = idNumber;
            this.setCandidacyState(candidacyState);
            this.name = name;
            this.registrationState = registrationState;
        }

        public String getIdNumber() {
            return idNumber;
        }

        public void setIdNumber(String idNumber) {
            this.idNumber = idNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRegistrationState() {
            return registrationState;
        }

        public void setRegistrationState(String registrationState) {
            this.registrationState = registrationState;
        }

        public String getDegreeCode() {
            return degreeCode;
        }

        public void setDegreeCode(String degreeCode) {
            this.degreeCode = degreeCode;
        }

        public String getCandidacyState() {
            return candidacyState;
        }

        public void setCandidacyState(String candidacyState) {
            this.candidacyState = candidacyState;
        }

        public String getCandidacyId() {
            return candidacyId;
        }

        public void setCandidacyId(String candidacyId) {
            this.candidacyId = candidacyId;
        }
    }
}
