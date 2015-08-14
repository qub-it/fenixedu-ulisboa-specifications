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
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        String documentIdNumber = studentCandidacy.getPerson().getDocumentIdNumber();
        String name = studentCandidacy.getPerson().getName();
        String registrationStatus = "";
        if (studentCandidacy.getRegistration() != null && !studentCandidacy.getRegistration().isCanceled()) {
            registrationStatus = "Sim";
        } else {
            registrationStatus = "Não";
        }
        return new RegistrationDGESStateBean(documentIdNumber, name, registrationStatus);
    }

    public static class RegistrationDGESStateBean {
        String idNumber;

        String name;
        String registrationState;

        public RegistrationDGESStateBean(String idNumber, String name, String registrationState) {
            super();
            this.idNumber = idNumber;
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
    }

}
