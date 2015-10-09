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

import java.util.stream.Collectors;

import org.fenixedu.academic.domain.serviceRequests.ServiceRequestCategory;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.ui.spring.controller.AcademicAdministrationSpringApplication;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.dto.ServiceRequestTypeBean;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
            serviceRequestType.delete();

            addInfoMessage(BundleUtil.getString(BUNDLE, "message.ServiceRequestType.removed.with.success"), model);
            return redirect(SEARCH_URL, model, redirectAttributes);
        } catch (DomainException ex) {
            addErrorMessage(BundleUtil.getString(BUNDLE, ex.getKey()), model);
        }

        return read(serviceRequestType, model);
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
                        bean.isNotifyUponConclusion(), bean.getServiceRequestCategory());
        for (ServiceRequestSlot serviceRequestSlot : bean.getServiceRequestSlots()) {
            serviceRequestType.addServiceRequestSlots(serviceRequestSlot);
        }
        return serviceRequestType;
    }

    private static final String READ_URI = "/read/";
    public static final String READ_URL = CONTROLLER_URL + READ_URI;

    @RequestMapping(value = READ_URI + "{serviceRequestTypeId}")
    public String read(@PathVariable("serviceRequestTypeId") final ServiceRequestType serviceRequestType, final Model model) {
        model.addAttribute("serviceRequestType", serviceRequestType);

        return "fenixedu-ulisboa-specifications/manageservicerequesttypes/servicerequesttype/read";
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
                bean.getServiceRequestCategory(), bean.getNumberOfUnitsLabel());
        for (ServiceRequestSlot serviceRequestSlot : serviceRequestType.getServiceRequestSlotsSet()) {
            if (!bean.getServiceRequestSlots().contains(serviceRequestSlot)) {
                serviceRequestType.removeServiceRequestSlots(serviceRequestSlot);
            }
        }
        for (ServiceRequestSlot newServiceRequestSlot : bean.getServiceRequestSlots()) {
            if (!serviceRequestType.getServiceRequestSlotsSet().contains(newServiceRequestSlot)) {
                serviceRequestType.addServiceRequestSlots(newServiceRequestSlot);
            }
        }
    }
}
