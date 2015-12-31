/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and ServiÃ§os Partilhados da
 * Universidade de Lisboa:
 *  - Copyright Â© 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright Â© 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: xpto@qub-it.com
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
package org.fenixedu.ulisboa.specifications.ui.managemobilityregistrationinformation;

import java.util.List;

import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.SchoolPeriodDuration;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityActivityType;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityProgramType;
import org.fenixedu.ulisboa.specifications.domain.student.mobility.MobilityRegistrationInformation;
import org.fenixedu.ulisboa.specifications.dto.student.mobility.MobilityRegistrationInformationBean;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;

@BennuSpringController(value=RegistrationController.class)
@RequestMapping(MobilityRegistrationInformationController.CONTROLLER_URL)
public class MobilityRegistrationInformationController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL =
            "/fenixedu-ulisboa-specifications/managemobilityregistrationinformation/mobilityregistrationinformation";
    public static final String JSP_PATH =
            "fenixedu-ulisboa-specifications/managemobilityregistrationinformation/mobilityregistrationinformation";

    private static final String _SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI + "/{registrationId}")
    public String search(@PathVariable("registrationId") Registration registration, final Model model) {
        List<MobilityRegistrationInformation> searchmobilityregistrationinformationResultsDataSet =
                Lists.newArrayList(registration.getMobilityRegistrationInformationsSet());

        model.addAttribute("searchmobilityregistrationinformationResultsDataSet",
                searchmobilityregistrationinformationResultsDataSet);

        return jspPage(_SEARCH_URI);
    }

    private static final String _SEARCH_TO_DELETE_ACTION_URI = "/search/delete/";
    public static final String SEARCH_TO_DELETE_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_DELETE_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_DELETE_ACTION_URI + "{registrationId}/{oid}", method = RequestMethod.POST)
    public String processSearchToDeleteAction(@PathVariable("registrationId") final Registration registration,
            @PathVariable("oid") final MobilityRegistrationInformation mobilityRegistrationInformation, final Model model,
            RedirectAttributes redirectAttributes) {
        try {

            mobilityRegistrationInformation.delete();

            addInfoMessage("label.MobilityRegistrationInformation.success", model);
            return redirect(SEARCH_URL + "/" + registration.getExternalId(), model, redirectAttributes);
        } catch (final DomainException ex) {
            addErrorMessage(ex.getLocalizedMessage(), model);
        }

        return search(registration, model);
    }

//				
    private static final String _CREATE_URI = "/create";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI + "/{registrationId}", method = RequestMethod.GET)
    public String create(@PathVariable("registrationId") final Registration registration, final Model model) {
        model.addAttribute("programDurationValues", SchoolPeriodDuration.values());
        model.addAttribute("MobilityRegistrationInformation_begin_options", ExecutionSemester.readNotClosedExecutionPeriods());
        model.addAttribute("MobilityRegistrationInformation_end_options", ExecutionSemester.readNotClosedExecutionPeriods());
        model.addAttribute("MobilityRegistrationInformation_mobilityProgramType_options", model.addAttribute(
                "MobilityRegistrationInformation_mobilityProgramType_options", MobilityProgramType.findAllActive()));
        model.addAttribute("MobilityRegistrationInformation_mobilityActivityType_options", MobilityActivityType.findAllActive());
        model.addAttribute("MobilityRegistrationInformation_foreignInstitutionUnit_options", Unit.readAllUnits());

        model.addAttribute("mobilityRegistrationInformationBeanJson", new MobilityRegistrationInformationBean());

        return jspPage(_CREATE_URI);
    }

    @RequestMapping(value = _CREATE_URI + "/{registrationId}", method = RequestMethod.POST)
    public String create(@PathVariable("registrationId") final Registration registration,
            @RequestParam("bean") final MobilityRegistrationInformationBean bean, Model model,
            RedirectAttributes redirectAttributes) {
        try {

            MobilityRegistrationInformation mobilityRegistrationInformation = null;
            if (bean.isIncoming()) {
                MobilityRegistrationInformation.createMobilityRegistrationForIncoming(bean);
            } else {
                MobilityRegistrationInformation.createMobilityRegistrationForOutgoing(bean);
            }

            return redirect(String.format("%s/%s/%s", READ_URL, registration.getExternalId(),
                    mobilityRegistrationInformation.getExternalId()), model, redirectAttributes);
        } catch (final DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
            return create(registration, model);
        }
    }

    private static final String _READ_URI = "/read";
    public static final String READ_URL = CONTROLLER_URL + _READ_URI;

    @RequestMapping(value = _READ_URI + "/{registrationId}/{oid}")
    public String read(@PathVariable("registrationId") final Registration registration,
            @PathVariable("oid") final MobilityRegistrationInformation mobilityRegistrationInformation, final Model model) {
        model.addAttribute("mobilityRegistrationInformation", mobilityRegistrationInformation);
        return jspPage(_READ_URI);
    }

    private static final String _UPDATE_URI = "/update";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI + "/{registrationId}/{oid}", method = RequestMethod.GET)
    public String update(@PathVariable("registrationId") final Registration registratrion,
            @PathVariable("oid") MobilityRegistrationInformation mobilityRegistrationInformation, Model model) {
        model.addAttribute("programDurationValues", SchoolPeriodDuration.values());
        model.addAttribute("MobilityRegistrationInformation_begin_options", ExecutionSemester.readNotClosedExecutionPeriods());
        model.addAttribute("MobilityRegistrationInformation_end_options", ExecutionSemester.readNotClosedExecutionPeriods());
        model.addAttribute("MobilityRegistrationInformation_mobilityProgramType_options", model.addAttribute(
                "MobilityRegistrationInformation_mobilityProgramType_options", MobilityProgramType.findAllActive()));
        model.addAttribute("MobilityRegistrationInformation_mobilityActivityType_options", MobilityActivityType.findAllActive());
        model.addAttribute("MobilityRegistrationInformation_foreignInstitutionUnit_options", Unit.readAllUnits());

        model.addAttribute("mobilityRegistrationInformationBeanJson",
                new MobilityRegistrationInformationBean(mobilityRegistrationInformation));

        return jspPage(_UPDATE_URI);
    }

    @RequestMapping(value = _UPDATE_URI + "{registrationId}/{oid}", method = RequestMethod.POST)
    public String update(@PathVariable("registrationId") final Registration registration,
            @PathVariable("oid") final MobilityRegistrationInformation mobilityRegistrationInformation,
            @RequestParam("bean") final MobilityRegistrationInformationBean bean, final Model model,
            final RedirectAttributes redirectAttributes) {

        try {

            mobilityRegistrationInformation.edit(bean);

            return redirect(String.format("%s/%s/%s", READ_URL, registration.getExternalId(),
                    mobilityRegistrationInformation.getExternalId()), model, redirectAttributes);
        } catch (final DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
            return update(registration, mobilityRegistrationInformation, bean, model, redirectAttributes);
        }
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page.substring(1, page.length());
    }

}
