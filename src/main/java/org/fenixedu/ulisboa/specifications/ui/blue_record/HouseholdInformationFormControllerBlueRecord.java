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
package org.fenixedu.ulisboa.specifications.ui.blue_record;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.GrantOwnerType;
import org.fenixedu.academic.domain.ProfessionType;
import org.fenixedu.academic.domain.ProfessionalSituationConditionType;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.person.MaritalStatus;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.ulisboa.specifications.domain.ProfessionTimeType;
import org.fenixedu.ulisboa.specifications.domain.SalarySpan;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.HouseholdInformationFormController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.HouseholdInformationFormController.HouseholdInformationForm;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@BennuSpringController(value = BlueRecordEntryPoint.class)
@RequestMapping(HouseholdInformationFormControllerBlueRecord.CONTROLLER_URL)
public class HouseholdInformationFormControllerBlueRecord extends HouseholdInformationFormController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/blueRecord/{executionYearId}/householdinformationform";

    @Override
    protected String nextScreen(final ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        final String url = OriginInformationFormControllerBlueRecord.CONTROLLER_URL + OriginInformationFormControllerBlueRecord._FILLORIGININFORMATION_URI;
        return redirect(urlWithExecutionYear(url, executionYear), model, redirectAttributes);
    }

    @Override
    public Optional<String> accessControlRedirect(final ExecutionYear executionYear, final Model model, final RedirectAttributes redirectAttributes) {
        return Optional.empty();
    }

    @Override
    public String back(final ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(PersonalInformationFormControllerBlueRecord.INVOKE_BACK_URL, executionYear), model, redirectAttributes);
    }

    private static final String _INVOKE_BACK_URI = "/invokeback";
    public static final String INVOKE_BACK_URL = CONTROLLER_URL + _INVOKE_BACK_URI;

    @RequestMapping(value = _INVOKE_BACK_URI, method = RequestMethod.GET)
    public String invokeBack(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model, final RedirectAttributes redirectAttributes) {
        if (isFormIsFilled(executionYear, model)) {
            return back(executionYear, model, redirectAttributes);
        }

        String url = HouseholdInformationFormControllerBlueRecord.CONTROLLER_URL + HouseholdInformationFormControllerBlueRecord._FILLHOUSEHOLDINFORMATION_URI;
        return redirect(urlWithExecutionYear(url , executionYear), model, redirectAttributes);
    }

    @RequestMapping(value = _FILLHOUSEHOLDINFORMATION_URI, method = RequestMethod.GET)
    public String fillhouseholdinformation(@PathVariable("executionYearId") final ExecutionYear executionYear, final Model model, final RedirectAttributes redirectAttributes) {
        if (isFormIsFilled(executionYear, model)) {
            return nextScreen(executionYear, model, redirectAttributes);
        }

        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }
        model.addAttribute("schoolLevelValues", SchoolLevelType.values());
        model.addAttribute("professionTypeValues", ProfessionType.values());
        model.addAttribute("professionalConditionValues", ProfessionalSituationConditionType.values());
        model.addAttribute("salarySpanValues", SalarySpan.readAll().collect(Collectors.toList()));
        model.addAttribute("professionTimeTypeValues", ProfessionTimeType.readAll().collect(Collectors.toList()));
        model.addAttribute("grantOwnerTypeValues", GrantOwnerType.values());

        List<MaritalStatus> maritalStatusValues = new ArrayList<>();
        maritalStatusValues.addAll(Arrays.asList(MaritalStatus.values()));
        maritalStatusValues.remove(MaritalStatus.UNKNOWN);
        model.addAttribute("maritalStatusValues", maritalStatusValues);

        model.addAttribute("countries", Bennu.getInstance().getCountrysSet());
        model.addAttribute("districts_options", Bennu.getInstance().getDistrictsSet());

        model.addAttribute("residenceType_values", Bennu.getInstance().getResidenceTypesSet());
        
        fillFormIfRequired(executionYear, model);
        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillHouseHoldInformation.info"), model);
        return "fenixedu-ulisboa-specifications/firsttimecandidacy/householdinformationform/fillhouseholdinformation";
    }

    protected void fillFormIfRequired(final ExecutionYear executionYear, Model model) {
        if (!model.containsAttribute("householdInformationForm")) {
            HouseholdInformationForm form = createHouseholdInformationForm(executionYear, getStudent(model));

            model.addAttribute("householdInformationForm", form);
        }
    }
    
    @Atomic
    protected HouseholdInformationForm createHouseholdInformationForm(final ExecutionYear executionYear, final Student student) {
        return createHouseholdInformationForm(student, executionYear, true);
    }
    
    @RequestMapping(value = _FILLHOUSEHOLDINFORMATION_URI, method = RequestMethod.POST)
    public String fillhouseholdinformation(@PathVariable("executionYearId") final ExecutionYear executionYear, final HouseholdInformationForm form, Model model, RedirectAttributes redirectAttributes) {
        Optional<String> accessControlRedirect = accessControlRedirect(executionYear, model, redirectAttributes);
        if (accessControlRedirect.isPresent()) {
            return accessControlRedirect.get();
        }
        if (!validate(form, model)) {
            model.addAttribute("householdInformationForm", form);
            return fillhouseholdinformation(executionYear, model, redirectAttributes);
        }

        try {
            writeData(getStudent(model), executionYear, form, model);
            model.addAttribute("householdInformationForm", form);
            return nextScreen(executionYear, model, redirectAttributes);
        } catch (Exception de) {

            addErrorMessage(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "label.error.create")
                    + de.getLocalizedMessage(), model);
            LoggerFactory.getLogger(this.getClass()).error("Exception for user " + getStudent(model).getPerson().getUsername());
            de.printStackTrace();
            return fillhouseholdinformation(executionYear, model, redirectAttributes);
        }
    }

    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    public boolean isFormIsFilled(final ExecutionYear executionYear, final Student student) {
        return validateHouseholdInformationForm(createHouseholdInformationForm(executionYear, student)).isEmpty();
    }

    @Override
    protected Student getStudent(final Model model) {
        return AccessControl.getPerson().getStudent();
    }
}
