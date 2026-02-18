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
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.academic.domain.student.mobility.MobilityRegistrationInformation;
import org.fenixedu.ulisboa.specifications.dto.student.mobility.MobilityRegistrationInformationBean;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.FenixFramework;

@BennuSpringController(value = RegistrationController.class)
@RequestMapping(MobilityRegistrationInformationController.CONTROLLER_URL)
public class MobilityRegistrationInformationController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL =
            "/fenixedu-ulisboa-specifications/managemobilityregistrationinformation/mobilityregistrationinformation";
    public static final String JSP_PATH =
            "fenixedu-ulisboa-specifications/managemobilityregistrationinformation/mobilityregistrationinformation";

    private static final String _SEARCH_URI = "/search";
    public static final String SEARCH_URL = CONTROLLER_URL + _SEARCH_URI;

    @RequestMapping(value = _SEARCH_URI + "/{registrationId}")
    public String search(@PathVariable("registrationId") final Registration registration, final Model model) {

        List<MobilityRegistrationInformation> searchmobilityregistrationinformationResultsDataSet = registration
                .getMobilityRegistrationInformationsSet().stream().filter(l -> l.isIncoming()).collect(Collectors.toList());

        List<MobilityRegistrationInformation> searchmobilityregistrationinformationResultsDataSetForOutgoing = registration
                .getMobilityRegistrationInformationsSet().stream().filter(l -> !l.isIncoming()).collect(Collectors.toList());

        model.addAttribute("registration", registration);

        model.addAttribute("searchmobilityregistrationinformationResultsDataSetForOutgoing",
                searchmobilityregistrationinformationResultsDataSetForOutgoing);

        model.addAttribute("searchmobilityregistrationinformationResultsDataSet",
                searchmobilityregistrationinformationResultsDataSet);

        return jspPage(_SEARCH_URI);
    }

    private static final String _SEARCH_TO_DELETE_ACTION_URI = "/search/delete/";
    public static final String SEARCH_TO_DELETE_ACTION_URL = CONTROLLER_URL + _SEARCH_TO_DELETE_ACTION_URI;

    @RequestMapping(value = _SEARCH_TO_DELETE_ACTION_URI + "{registrationId}", method = RequestMethod.POST)
    public String processSearchToDeleteAction(@PathVariable("registrationId") final Registration registration,
            @RequestParam("mobilityRegistrationInformationId") final MobilityRegistrationInformation mobilityRegistrationInformation,
            final Model model, RedirectAttributes redirectAttributes) {
        try {

            mobilityRegistrationInformation.delete();

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
        setRegistration(registration, model);
        setMobilityRegistrationInformationBean(new MobilityRegistrationInformationBean(registration), model);
        return jspPage(_CREATE_URI);
    }

    @RequestMapping(value = _CREATE_URI + "/{registrationId}", method = RequestMethod.POST)
    public String create(@PathVariable("registrationId") final Registration registration,
            @RequestParam("bean") final MobilityRegistrationInformationBean bean, Model model,
            RedirectAttributes redirectAttributes) {
        try {

            if (bean.isIncoming()) {
                FenixFramework.atomic(() -> {
                    MobilityRegistrationInformation.createIncoming(bean.getRegistration(), bean.isNational(),
                            bean.getProgramDuration(), bean.getBegin(), bean.getEnd(), bean.getBeginDate(), bean.getEndDate(),
                            bean.getMobilityActivityType(), bean.getMobilityProgramType(), bean.getCountryUnit(),
                            bean.getForeignInstitutionUnit(), bean.getRemarks(), bean.getExternalRemarks(),
                            bean.getOriginMobilityProgrammeLevel(),
                            bean.getOtherOriginMobilityProgrammeLevel(), bean.isDegreeBased(), bean.getDegreeCurricularPlan(),
                            bean.getBranchCourseGroup(), bean.getMobilityScientificArea(),
                            bean.getIncomingMobilityProgrammeLevel(), bean.getOtherIncomingMobilityProgrammeLevel());
                });
            } else {
                FenixFramework.atomic(() -> {
                    MobilityRegistrationInformation.createOutgoing(bean.getRegistration(), bean.isNational(),
                            bean.getProgramDuration(), bean.getBegin(), bean.getEnd(), bean.getBeginDate(), bean.getEndDate(),
                            bean.getMobilityActivityType(), bean.getMobilityProgramType(), bean.getCountryUnit(),
                            bean.getForeignInstitutionUnit(), bean.getRemarks(), bean.getExternalRemarks());
                });
            }

            return redirect(String.format("%s/%s", SEARCH_URL, registration.getExternalId()), model, redirectAttributes);
        } catch (final DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
            setRegistration(registration, model);
            setMobilityRegistrationInformationBean(bean, model);
            return jspPage(_CREATE_URI);
        }
    }

    private static final String _CREATEPOSTBACK_URI = "/createpostback/";
    public static final String CREATEPOSTBACK_URL = CONTROLLER_URL + _CREATEPOSTBACK_URI;

    @RequestMapping(value = _CREATEPOSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> createpostback(
            @RequestParam(value = "bean", required = false) final MobilityRegistrationInformationBean bean, final Model model) {

        bean.update();
        setMobilityRegistrationInformationBean(bean, model);

        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    private static final String _UPDATE_URI = "/update";
    public static final String UPDATE_URL = CONTROLLER_URL + _UPDATE_URI;

    @RequestMapping(value = _UPDATE_URI + "/{mobilityRegistrationInformationId}", method = RequestMethod.GET)
    public String update(
            @PathVariable("mobilityRegistrationInformationId") final MobilityRegistrationInformation mobilityRegistrationInformation,
            final Model model) {
        setRegistration(mobilityRegistrationInformation.getRegistration(), model);
        setMobilityRegistrationInformationBean(new MobilityRegistrationInformationBean(mobilityRegistrationInformation), model);
        return jspPage(_UPDATE_URI);
    }

    @RequestMapping(value = _UPDATE_URI + "/{mobilityRegistrationInformationId}", method = RequestMethod.POST)
    public String update(
            @PathVariable("mobilityRegistrationInformationId") final MobilityRegistrationInformation mobilityRegistrationInformation,
            @RequestParam("bean") final MobilityRegistrationInformationBean bean, Model model,
            RedirectAttributes redirectAttributes) {
        try {

            if (bean.isIncoming()) {
                FenixFramework.atomic(() -> bean.getMobilityRegistrationInformation().editIncoming(bean.isNational(),
                        bean.getProgramDuration(), bean.getBegin(), bean.getEnd(), bean.getBeginDate(), bean.getEndDate(),
                        bean.getMobilityActivityType(), bean.getMobilityProgramType(), bean.getCountryUnit(),
                        bean.getForeignInstitutionUnit(), bean.getRemarks(), bean.getExternalRemarks(),
                        bean.getOriginMobilityProgrammeLevel(),
                        bean.getOtherOriginMobilityProgrammeLevel(), bean.isDegreeBased(), bean.getDegreeCurricularPlan(),
                        bean.getBranchCourseGroup(), bean.getMobilityScientificArea(), bean.getIncomingMobilityProgrammeLevel(),
                        bean.getOtherIncomingMobilityProgrammeLevel()));
            } else {
                FenixFramework.atomic(() -> bean.getMobilityRegistrationInformation().editOutgoing(bean.isNational(),
                        bean.getProgramDuration(), bean.getBegin(), bean.getEnd(), bean.getBeginDate(), bean.getEndDate(),
                        bean.getMobilityActivityType(), bean.getMobilityProgramType(), bean.getCountryUnit(),
                        bean.getForeignInstitutionUnit(), bean.getRemarks(), bean.getExternalRemarks()));
            }

            return redirect(String.format("%s/%s", SEARCH_URL, mobilityRegistrationInformation.getRegistration().getExternalId()),
                    model, redirectAttributes);
        } catch (final DomainException de) {
            addErrorMessage(de.getLocalizedMessage(), model);
            setRegistration(mobilityRegistrationInformation.getRegistration(), model);
            setMobilityRegistrationInformationBean(bean, model);
            return jspPage(_UPDATE_URI);
        }
    }

    private static final String _UPDATEPOSTBACK_URI = "/updatepostback/";
    public static final String UPDATEPOSTBACK_URL = CONTROLLER_URL + _UPDATEPOSTBACK_URI;

    @RequestMapping(value = _UPDATEPOSTBACK_URI, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseEntity<String> updatepostback(
            @RequestParam(value = "bean", required = false) final MobilityRegistrationInformationBean bean, final Model model) {

        bean.update();
        setMobilityRegistrationInformationBean(bean, model);

        return new ResponseEntity<String>(getBeanJson(bean), HttpStatus.OK);
    }

    private static final String _MARK_AS_MAIN_URI = "/markAsMain";
    public static final String MARK_AS_MAIN_URL = CONTROLLER_URL + _MARK_AS_MAIN_URI;

    @RequestMapping(value = _MARK_AS_MAIN_URI + "/{mobilityRegistrationInformationId}")
    public String markAsMain(
            @PathVariable("mobilityRegistrationInformationId") final MobilityRegistrationInformation mobilityRegistrationInformation,
            final Model model, RedirectAttributes redirectAttributes) {

        try {

            mobilityRegistrationInformation.markAsMainInformation();

            return redirect(SEARCH_URL + "/" + mobilityRegistrationInformation.getRegistration().getExternalId(), model,
                    redirectAttributes);
        } catch (final DomainException ex) {
            addErrorMessage(ex.getLocalizedMessage(), model);
        }

        return search(mobilityRegistrationInformation.getRegistration(), model);

    }

    private void setRegistration(final Registration registration, final Model model) {
        model.addAttribute("registration", registration);
    }

    private void setMobilityRegistrationInformationBean(final MobilityRegistrationInformationBean bean, final Model model) {
        model.addAttribute("mobilityRegistrationInformationBeanJson", getBeanJson(bean));
        model.addAttribute("mobilityRegistrationInformationBean", bean);
    }

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page.substring(1, page.length());
    }

}
