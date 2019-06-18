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
 * This file is part of FenixEdu Academic.
 *
 * FenixEdu Academic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.ulisboa.specifications.ui.servicerequestslot;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.exceptions.AcademicExtensionsDomainException;
import org.fenixedu.academic.ui.spring.controller.AcademicAdministrationSpringApplication;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.UIComponentType;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = AcademicAdministrationSpringApplication.class, title = "label.title.manageServiceRequestSlots",
        accessGroup = "#managers")
@RequestMapping(ServiceRequestSlotController.CONTROLLER_URL)
public class ServiceRequestSlotController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/ulisboaspecifications/manageservicerequestslots/servicerequestslot";
    private static final String BUNDLE = "resources.ApplicationResources";

    @RequestMapping(method = GET)
    public String home(Model model, RedirectAttributes redirectAttributes) {
        return redirect(SEARCH_URL, model, redirectAttributes);
    }

    private static final String SEARCH_URI = "/search/";
    public static final String SEARCH_URL = CONTROLLER_URL + SEARCH_URI;

    @RequestMapping(value = SEARCH_URI)
    public String search(@RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "uicomponenttype", required = false) UIComponentType uiComponentType,
            @RequestParam(value = "label", required = false) LocalizedString label, Model model) {
        List<ServiceRequestSlot> searchserviceRequestSlotResultsDataSet =
                filterSearchServiceRequestSlot(code, uiComponentType, label);
        model.addAttribute("searchserviceRequestSlotResultsDataSet", searchserviceRequestSlotResultsDataSet);
        model.addAttribute("uiComponentTypeList", UIComponentType.values());
        return "fenixedu-ulisboa-specifications/manageservicerequestslots/servicerequestslot/search";
    }

    private List<ServiceRequestSlot> filterSearchServiceRequestSlot(String code, UIComponentType uiComponentType,
            LocalizedString label) {
        return ServiceRequestSlot.findAll()
                .filter(serviceRequestSlot -> code == null || code.length() == 0
                        || serviceRequestSlot.getCode().toLowerCase().contains(code.toLowerCase()))
                .filter(serviceRequestSlot -> uiComponentType == null
                        || serviceRequestSlot.getUiComponentType() == uiComponentType)
                .filter(serviceRequestSlot -> label == null || label.isEmpty() || label.getLocales().stream()
                        .allMatch(locale -> serviceRequestSlot.getLabel().getContent(locale) != null && serviceRequestSlot
                                .getLabel().getContent(locale).toLowerCase().contains(label.getContent(locale).toLowerCase())))
                .collect(Collectors.toList());
    }

    private static final String SEARCH_VIEW_URI = "/search/view/";
    public static final String SEARCH_VIEW_URL = CONTROLLER_URL + SEARCH_VIEW_URI;

    @RequestMapping(value = SEARCH_VIEW_URI + "{oid}")
    public String processSearchToViewAction(@PathVariable("oid") final ServiceRequestSlot serviceRequestSlot, final Model model,
            final RedirectAttributes redirectAttributes) {

        return redirect(READ_URL + serviceRequestSlot.getExternalId(), model, redirectAttributes);
    }

    private static final String DELETE_URI = "/delete/";
    public static final String DELETE_URL = CONTROLLER_URL + DELETE_URI;

    @RequestMapping(value = DELETE_URI + "{oid}", method = RequestMethod.POST)
    public String processSearchToDeleteAction(@PathVariable("oid") final ServiceRequestSlot serviceRequestSlot, final Model model,
            final RedirectAttributes redirectAttributes) {
        try {
            serviceRequestSlot.delete();

            addInfoMessage(BundleUtil.getString(BUNDLE, "message.ServiceRequestSlot.removed.with.success"), model);
            return redirect(SEARCH_URL, model, redirectAttributes);
        } catch (ULisboaSpecificationsDomainException | AcademicExtensionsDomainException ex) {
            addErrorMessage(BundleUtil.getString(BUNDLE, ex.getKey()), model);
        }
        return read(serviceRequestSlot, model);
    }

    private static final String CREATE_URI = "/create/";
    public static final String CREATE_URL = CONTROLLER_URL + CREATE_URI;

    @RequestMapping(value = CREATE_URI, method = RequestMethod.GET)
    public String create(Model model) {
        model.addAttribute("uiComponentTypeList", getPrimitivesUIComponentTypes());
        return "fenixedu-ulisboa-specifications/manageservicerequestslots/servicerequestslot/create";
    }

    private List<UIComponentType> getPrimitivesUIComponentTypes() {
        List<UIComponentType> uiComponentTypes = new ArrayList<UIComponentType>();
        for (UIComponentType uiComponentType : UIComponentType.values()) {
            if (!uiComponentType.needDataSource()) {
                uiComponentTypes.add(uiComponentType);
            }
        }
        return uiComponentTypes;
    }

    @RequestMapping(value = CREATE_URI, method = RequestMethod.POST)
    public String create(@RequestParam(value = "code", required = true) String code,
            @RequestParam(value = "uicomponenttype", required = true) UIComponentType uiComponentType,
            @RequestParam(value = "label", required = false) LocalizedString label, Model model,
            RedirectAttributes redirectAttributes) {
        try {
            final ServiceRequestSlot serviceRequestSlot = createServiceRequestSlot(code, uiComponentType, label);

            return redirect(READ_URL + serviceRequestSlot.getExternalId(), model, redirectAttributes);
        } catch (ULisboaSpecificationsDomainException | AcademicExtensionsDomainException de) {
            addErrorMessage(BundleUtil.getString(BUNDLE, de.getKey()), model);
        }

        return create(model);
    }

    @Atomic
    public ServiceRequestSlot createServiceRequestSlot(final String code, final UIComponentType uiComponentType,
            final LocalizedString label) {
        return ServiceRequestSlot.createDynamicSlot(code, uiComponentType, label);
    }

    private static final String READ_URI = "/read/";
    public static final String READ_URL = CONTROLLER_URL + READ_URI;

    @RequestMapping(value = READ_URI + "{oid}")
    public String read(@PathVariable("oid") final ServiceRequestSlot serviceRequestSlot, final Model model) {
        model.addAttribute("serviceRequestSlot", serviceRequestSlot);
        return "fenixedu-ulisboa-specifications/manageservicerequestslots/servicerequestslot/read";
    }

    private static final String UPDATE_URI = "/update/";
    public static final String UPDATE_URL = CONTROLLER_URL + UPDATE_URI;

    @RequestMapping(value = UPDATE_URI + "{oid}", method = RequestMethod.GET)
    public String update(@PathVariable("oid") final ServiceRequestSlot serviceRequestSlot, final Model model) {
        model.addAttribute("uiComponentTypeList", getPrimitivesUIComponentTypes());
        model.addAttribute("serviceRequestSlot", serviceRequestSlot);
        return "fenixedu-ulisboa-specifications/manageservicerequestslots/servicerequestslot/update";
    }

    @RequestMapping(value = UPDATE_URI + "{oid}", method = RequestMethod.POST)
    public String update(@PathVariable("oid") final ServiceRequestSlot serviceRequestSlot,
            @RequestParam(value = "code", required = true) String code,
            @RequestParam(value = "uicomponenttype", required = true) UIComponentType uiComponentType,
            @RequestParam(value = "label", required = false) LocalizedString label, Model model,
            RedirectAttributes redirectAttributes) {
        model.addAttribute("serviceRequestSlot", serviceRequestSlot);
        try {
            updateServiceRequestSlot(serviceRequestSlot, code, uiComponentType, label);
            return redirect(READ_URL + serviceRequestSlot.getExternalId(), model, redirectAttributes);
        } catch (ULisboaSpecificationsDomainException | AcademicExtensionsDomainException de) {
            addErrorMessage(BundleUtil.getString(BUNDLE, de.getKey()), model);
        }
        return update(serviceRequestSlot, model);
    }

    @Atomic
    private void updateServiceRequestSlot(ServiceRequestSlot serviceRequestSlot, final String code,
            final UIComponentType uiComponentType, final LocalizedString label) {
        serviceRequestSlot.edit(code, uiComponentType, label);
    }
}
