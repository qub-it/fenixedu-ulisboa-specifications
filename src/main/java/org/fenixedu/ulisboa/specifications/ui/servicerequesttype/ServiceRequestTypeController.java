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
package org.fenixedu.ulisboa.specifications.ui.servicerequesttype;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestCategory;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.ui.spring.controller.AcademicAdministrationSpringApplication;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlotEntry;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.validators.ULisboaServiceRequestValidator;
import org.fenixedu.ulisboa.specifications.dto.ServiceRequestSlotsBean;
import org.fenixedu.ulisboa.specifications.dto.ServiceRequestTypeBean;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pt.ist.fenixframework.Atomic;

@SpringFunctionality(app = AcademicAdministrationSpringApplication.class, title = "label.title.manageServiceRequestTypes",
        accessGroup = "#managers")
@RequestMapping(ServiceRequestTypeController.CONTROLLER_URL)
public class ServiceRequestTypeController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/ulisboaspecifications/manageservicerequesttypes/servicerequesttype";
    private static final String BUNDLE = "resources.ApplicationResources";

    @RequestMapping(method = GET)
    public String home(Model model, RedirectAttributes redirectAttributes) {
        return redirect(SEARCH_URL, model, redirectAttributes);
    }

    private ServiceRequestTypeBean getServiceRequestTypeBean(Model model) {
        return (ServiceRequestTypeBean) model.asMap().get("serviceRequestTypeBean");
    }

    private void setServiceRequestTypeBean(ServiceRequestTypeBean bean, Model model) {
        model.addAttribute("serviceRequestTypeBeanJson", getBeanJson(bean));
        model.addAttribute("serviceRequestTypeBean", bean);
    }

    private void setServiceRequestSlotsBean(ServiceRequestSlotsBean bean, Model model) {
        bean.updateModelLists();
        model.addAttribute("serviceRequestSlotsBeanJson", getBeanJson(bean));
        model.addAttribute("serviceRequestSlotsBean", bean);
    }

    private static final String SEARCH_URI = "/search/";
    public static final String SEARCH_URL = CONTROLLER_URL + SEARCH_URI;

    @RequestMapping(value = SEARCH_URI, method = GET)
    public String search(Model model) {
        model.addAttribute("searchservicerequesttypeResultsDataSet", ServiceRequestType.findAll().collect(Collectors.toList()));
        model.addAttribute("serviceRequestCategoryValues", ServiceRequestCategory.values());

        return "fenixedu-ulisboa-specifications/manageservicerequesttypes/servicerequesttype/search";
    }

    private static final String SEARCH_VIEW_URI = "/search/view/";
    public static final String SEARCH_VIEW_URL = CONTROLLER_URL + SEARCH_VIEW_URI;

    @RequestMapping(value = SEARCH_VIEW_URI + "{serviceRequestTypeId}")
    public String processSearchToViewAction(@PathVariable("serviceRequestTypeId") final ServiceRequestType serviceRequestType,
            final Model model, final RedirectAttributes redirectAttributes) {

        return redirect(READ_URL + serviceRequestType.getExternalId(), model, redirectAttributes);
    }

    private static final String DELETE_URI = "/delete/";
    public static final String DELETE_URL = CONTROLLER_URL + DELETE_URI;

    @RequestMapping(value = DELETE_URI + "{serviceRequestTypeId}", method = RequestMethod.POST)
    public String processSearchToDeleteAction(@PathVariable("serviceRequestTypeId") final ServiceRequestType serviceRequestType,
            final Model model, final RedirectAttributes redirectAttributes) {
        try {
            deleteServiceRequestType(serviceRequestType);

            addInfoMessage(BundleUtil.getString(BUNDLE, "message.ServiceRequestType.removed.with.success"), model);
            return redirect(SEARCH_URL, model, redirectAttributes);
        } catch (DomainException ex) {
            addErrorMessage(BundleUtil.getString(BUNDLE, ex.getKey()), model);
        }

        return read(serviceRequestType, model);
    }

    //TODOJN o delete não protege nada
    @Atomic
    public void deleteServiceRequestType(ServiceRequestType serviceRequestType) {
        serviceRequestType.delete();
    }

    private static final String CREATE_URI = "/create/";
    public static final String CREATE_URL = CONTROLLER_URL + CREATE_URI;

    @RequestMapping(value = CREATE_URI, method = RequestMethod.GET)
    public String create(Model model) {
        if (getServiceRequestTypeBean(model) == null) {
            setServiceRequestTypeBean(new ServiceRequestTypeBean(), model);
        }
        return "fenixedu-ulisboa-specifications/manageservicerequesttypes/servicerequesttype/create";
    }

    @RequestMapping(value = CREATE_URI, method = RequestMethod.POST)
    public String create(@RequestParam(value = "bean", required = true) ServiceRequestTypeBean bean, Model model,
            RedirectAttributes redirectAttributes) {
        setServiceRequestTypeBean(bean, model);
        try {
            final ServiceRequestType serviceRequestType = createServiceRequestType(bean);

            return redirect(READ_URL + serviceRequestType.getExternalId(), model, redirectAttributes);
        } catch (DomainException de) {
            addErrorMessage(BundleUtil.getString(BUNDLE, de.getKey()), model);
        }

        return create(model);
    }

    @Atomic
    public ServiceRequestType createServiceRequestType(final ServiceRequestTypeBean bean) {
        final ServiceRequestType serviceRequestType =
                ServiceRequestType.create(bean.getCode(), bean.getName(), bean.isActive(), bean.isPayable(),
                        bean.isNotifyUponConclusion(), bean.isPrintable(), bean.isRequestedOnline(),
                        bean.getServiceRequestCategory());
        for (ULisboaServiceRequestValidator validator : bean.getValidators()) {
            serviceRequestType.addULisboaServiceRequestValidators(validator);
        }
        return serviceRequestType;
    }

    private static final String READ_URI = "/read/";
    public static final String READ_URL = CONTROLLER_URL + READ_URI;

    @RequestMapping(value = READ_URI + "{serviceRequestTypeId}", method = RequestMethod.GET)
    public String read(@PathVariable("serviceRequestTypeId") final ServiceRequestType serviceRequestType, final Model model) {
        setServiceRequestSlotsBean(new ServiceRequestSlotsBean(serviceRequestType), model);
        model.addAttribute("serviceRequestType", serviceRequestType);
        return "fenixedu-ulisboa-specifications/manageservicerequesttypes/servicerequesttype/read";
    }

    private static final String ADD_PROPERTY_URI = "/add/property/";
    public static final String ADD_PROPERTY_URL = CONTROLLER_URL + ADD_PROPERTY_URI;

    @RequestMapping(value = ADD_PROPERTY_URI + "{oid}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody String addProperty(@PathVariable("oid") ServiceRequestType serviceRequestType, @RequestParam(
            value = "bean", required = true) ServiceRequestSlotsBean bean,
            @RequestParam(value = "required", required = true) boolean required, @RequestParam(value = "orderNumber",
                    required = true) int orderNumber,
            @RequestParam(value = "serviceRequestSlot", required = true) ServiceRequestSlot serviceRequestSlot, Model model) {
        addProperty(serviceRequestType, required, orderNumber, serviceRequestSlot);
        setServiceRequestSlotsBean(bean, model);
        return getBeanJson(bean);
    }

    @Atomic
    public void addProperty(ServiceRequestType serviceRequestType, boolean required, int orderNumber,
            ServiceRequestSlot serviceRequestSlot) {
        ServiceRequestSlotEntry.create(serviceRequestType, serviceRequestSlot, required, orderNumber);
    }

    private static final String DELETE_PROPERTY_URI = "/delete/property/";
    public static final String DELETE_PROPERTY_URL = CONTROLLER_URL + DELETE_PROPERTY_URI;

    @RequestMapping(value = DELETE_PROPERTY_URI + "{serviceRequestTypeId}/{oid}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody String deleteProperty(@PathVariable("serviceRequestTypeId") ServiceRequestType serviceRequestType,
            @PathVariable("oid") ServiceRequestSlotEntry entry,
            @RequestParam(value = "bean", required = true) ServiceRequestSlotsBean bean, Model model) {
        deleteProperty(entry);
        model.addAttribute("serviceRequestType", serviceRequestType);
        setServiceRequestSlotsBean(bean, model);
        return getBeanJson(bean);
    }

    @Atomic
    public void deleteProperty(ServiceRequestSlotEntry entry) {
        List<ServiceRequestSlotEntry> orderEntries = sortedServiceRequestSlotEntriesList(entry);
        for (int i = entry.getOrderNumber() + 1; i < orderEntries.size(); i++) {
            ServiceRequestSlotEntry e = orderEntries.get(i);
            e.setOrderNumber(e.getOrderNumber() - 1);
        }
        entry.delete();
    }

    private static final String UPDATE_PROPERTY_URI = "/update/property/";
    public static final String UPDATE_PROPERTY_URL = CONTROLLER_URL + UPDATE_PROPERTY_URI;

    @RequestMapping(value = UPDATE_PROPERTY_URI + "{serviceRequestTypeId}/{oid}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody String updateProperty(@PathVariable("serviceRequestTypeId") ServiceRequestType serviceRequestType,
            @PathVariable("oid") ServiceRequestSlotEntry entry,
            @RequestParam(value = "bean", required = true) ServiceRequestSlotsBean bean, @RequestParam(value = "required",
                    required = true) boolean required, Model model) {
        updateProperty(entry, required);
        model.addAttribute("serviceRequestType", serviceRequestType);
        setServiceRequestSlotsBean(bean, model);
        return getBeanJson(bean);
    }

    @Atomic
    public void updateProperty(ServiceRequestSlotEntry entry, boolean required) {
        entry.setRequired(required);
    }

    private static final String MOVE_UP_PROPERTY_URI = "/moveUp/property/";
    public static final String MOVE_UP_PROPERTY_URL = CONTROLLER_URL + MOVE_UP_PROPERTY_URI;

    @RequestMapping(value = MOVE_UP_PROPERTY_URI + "{serviceRequestTypeId}/{oid}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody String moveUpProperty(@PathVariable("serviceRequestTypeId") ServiceRequestType serviceRequestType,
            @PathVariable("oid") ServiceRequestSlotEntry entry,
            @RequestParam(value = "bean", required = true) ServiceRequestSlotsBean bean, Model model) {
        moveUpProperty(entry);
        model.addAttribute("serviceRequestType", serviceRequestType);
        setServiceRequestSlotsBean(bean, model);
        return getBeanJson(bean);
    }

    @Atomic
    public void moveUpProperty(ServiceRequestSlotEntry entry) {
        List<ServiceRequestSlotEntry> orderEntries = sortedServiceRequestSlotEntriesList(entry);
        orderEntries.get(entry.getOrderNumber() - 1).setOrderNumber(entry.getOrderNumber());
        orderEntries.get(entry.getOrderNumber()).setOrderNumber(entry.getOrderNumber() - 1);
    }

    private static final String MOVE_DOWN_PROPERTY_URI = "/moveDown/property/";
    public static final String MOVE_DOWN_PROPERTY_URL = CONTROLLER_URL + MOVE_DOWN_PROPERTY_URI;

    @RequestMapping(value = MOVE_DOWN_PROPERTY_URI + "{serviceRequestTypeId}/{oid}", method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    public @ResponseBody String moveDownProperty(@PathVariable("serviceRequestTypeId") ServiceRequestType serviceRequestType,
            @PathVariable("oid") ServiceRequestSlotEntry entry,
            @RequestParam(value = "bean", required = true) ServiceRequestSlotsBean bean, Model model) {
        moveDownProperty(entry);
        model.addAttribute("serviceRequestType", serviceRequestType);
        setServiceRequestSlotsBean(bean, model);
        return getBeanJson(bean);
    }

    @Atomic
    public void moveDownProperty(ServiceRequestSlotEntry entry) {
        List<ServiceRequestSlotEntry> orderEntries = sortedServiceRequestSlotEntriesList(entry);
        orderEntries.get(entry.getOrderNumber() + 1).setOrderNumber(entry.getOrderNumber());
        orderEntries.get(entry.getOrderNumber()).setOrderNumber(entry.getOrderNumber() + 1);
    }

    @Atomic
    private List<ServiceRequestSlotEntry> sortedServiceRequestSlotEntriesList(ServiceRequestSlotEntry entry) {
        return entry.getServiceRequestType().getServiceRequestSlotEntriesSet().stream()
                .sorted(ServiceRequestSlotEntry.COMPARE_BY_ORDER_NUMBER).collect(Collectors.toList());
    }

    private static final String UPDATE_URI = "/update/";
    public static final String UPDATE_URL = CONTROLLER_URL + UPDATE_URI;

    @RequestMapping(value = UPDATE_URI + "{serviceRequestTypeId}", method = RequestMethod.GET)
    public String update(@PathVariable("serviceRequestTypeId") final ServiceRequestType serviceRequestType, final Model model) {

        model.addAttribute("serviceRequestType", serviceRequestType);
        if (getServiceRequestTypeBean(model) == null) {
            setServiceRequestTypeBean(new ServiceRequestTypeBean(serviceRequestType), model);
        }
        return "fenixedu-ulisboa-specifications/manageservicerequesttypes/servicerequesttype/update";
    }

    @RequestMapping(value = UPDATE_URI + "{serviceRequestTypeId}", method = RequestMethod.POST)
    public String update(@PathVariable("serviceRequestTypeId") final ServiceRequestType serviceRequestType, @RequestParam(
            value = "bean", required = true) ServiceRequestTypeBean bean, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("serviceRequestType", serviceRequestType);
        setServiceRequestTypeBean(bean, model);
        try {
            updateServiceRequestType(serviceRequestType, bean);
            return redirect(READ_URL + serviceRequestType.getExternalId(), model, redirectAttributes);
        } catch (DomainException de) {
            addErrorMessage(BundleUtil.getString(BUNDLE, de.getKey()), model);
        }

        return update(serviceRequestType, model);
    }

    @Atomic
    public void updateServiceRequestType(ServiceRequestType serviceRequestType, ServiceRequestTypeBean bean) {
        serviceRequestType.edit(bean.getCode(), bean.getName(), bean.isActive(), bean.isPayable(), bean.isNotifyUponConclusion(),
                bean.isPrintable(), bean.isRequestedOnline(), bean.getServiceRequestCategory(), bean.getNumberOfUnitsLabel());
        //Update the ULisboa Service Request Validators
        for (ULisboaServiceRequestValidator validator : serviceRequestType.getULisboaServiceRequestValidatorsSet()) {
            if (!bean.getValidators().contains(validator)) {
                serviceRequestType.removeULisboaServiceRequestValidators(validator);
            }
        }
        for (ULisboaServiceRequestValidator newValidator : bean.getValidators()) {
            if (!serviceRequestType.getULisboaServiceRequestValidatorsSet().contains(newValidator)) {
                serviceRequestType.addULisboaServiceRequestValidators(newValidator);
            }
        }

    }
}
