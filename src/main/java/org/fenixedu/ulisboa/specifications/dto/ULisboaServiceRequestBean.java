/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: diogo.simoes@qub-it.com
 *               jnpa@reitoria.ulisboa.pt
 *
 * 
 * This file is part of FenixEdu QubDocs.
 *
 * FenixEdu QubDocs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu QubDocs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu QubDocs.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fenixedu.ulisboa.specifications.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentPurposeTypeInstance;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlot;
import org.fenixedu.ulisboa.specifications.util.Constants;

import com.google.common.collect.Sets;

public class ULisboaServiceRequestBean implements IBean {

    //TODOJN maneira mais elegante
    public static final Map<String, DataSourceProvider> DATA_SOURCE_PROVIDERS = new HashMap<String, DataSourceProvider>();
    static {
        initProviderMap();
    }

    private Registration registration;
    private ServiceRequestType serviceRequestType;
    private List<TupleDataSourceBean> serviceRequestTypesDataSource;
    private List<ServiceRequestPropertyBean> serviceRequestPropertyBeans;

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public ServiceRequestType getServiceRequestType() {
        return serviceRequestType;
    }

    public void setServiceRequestType(ServiceRequestType serviceRequestType) {
        this.serviceRequestType = serviceRequestType;
    }

    public List<TupleDataSourceBean> getServiceRequestTypesDataSource() {
        return serviceRequestTypesDataSource;
    }

    public void setServiceRequestTypesDataSource(List<ServiceRequestType> documentTypesValues) {
        this.serviceRequestTypesDataSource = documentTypesValues.stream().map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.getExternalId());
            tuple.setText(x.getName().getContent());
            return tuple;
        }).collect(Collectors.toList());
    }

    public List<ServiceRequestPropertyBean> getServiceRequestPropertyBeans() {
        return serviceRequestPropertyBeans;
    }

    public void setServiceRequestPropertyBeans(List<ServiceRequestPropertyBean> serviceRequestPropertyBeans) {
        this.serviceRequestPropertyBeans = serviceRequestPropertyBeans;
    }

    public ULisboaServiceRequestBean() {
        setServiceRequestPropertyBeans(new ArrayList<ServiceRequestPropertyBean>());
        setServiceRequestTypesDataSource(ServiceRequestType.findActive()
                .sorted(ServiceRequestType.COMPARE_BY_CATEGORY_THEN_BY_NAME).collect(Collectors.toList()));
    }

    public ULisboaServiceRequestBean(Registration registration) {
        this();
        setRegistration(registration);
    }

    private boolean isSameServiceRequestType() {
        Set<String> oldSlotNames =
                serviceRequestPropertyBeans.stream().map(ServiceRequestPropertyBean::getCode).collect(Collectors.toSet());
        Set<String> newSlotNames =
                serviceRequestType.getServiceRequestSlotsSet().stream().map(ServiceRequestSlot::getCode)
                        .collect(Collectors.toSet());
        return oldSlotNames.size() == newSlotNames.size() && Sets.difference(oldSlotNames, newSlotNames).isEmpty();
    }

    public void updateModelLists() {
        //update service request type
        if (serviceRequestType == null) {
            serviceRequestPropertyBeans = new ArrayList<ServiceRequestPropertyBean>();
        } else if (!isSameServiceRequestType()) {
            serviceRequestPropertyBeans = new ArrayList<ServiceRequestPropertyBean>();
            for (ServiceRequestSlot serviceRequestSlot : serviceRequestType.getServiceRequestSlotsSet()) {
                serviceRequestPropertyBeans.add(new ServiceRequestPropertyBean(serviceRequestSlot));
            }
        }
        //update properties
        for (ServiceRequestPropertyBean serviceRequestPropertyBean : serviceRequestPropertyBeans) {
            if (serviceRequestPropertyBean.getUiComponent().needDataSource()) {
                DataSourceProvider dataSourceProvider =
                        ULisboaServiceRequestBean.DATA_SOURCE_PROVIDERS.get(serviceRequestPropertyBean.getCode());
                if (dataSourceProvider == null) {
                    throw new RuntimeException("error.provider.not.defined");
                }
                serviceRequestPropertyBean.setDataSource(dataSourceProvider.provideDataSourceList(this));
            }
        }
    }

    public static void initProviderMap() {
        DATA_SOURCE_PROVIDERS.put(Constants.LANGUAGE, new DataSourceProvider() {

            @Override
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                return CoreConfiguration.supportedLocales().stream().map(x -> {
                    TupleDataSourceBean tuple = new TupleDataSourceBean();
                    tuple.setId(x.toString().replace("_", "-"));
                    tuple.setText(x.getDisplayLanguage());
                    return tuple;
                }).collect(Collectors.toList());
            }
        });
        DATA_SOURCE_PROVIDERS.put(Constants.DOCUMENT_PURPOSE_TYPE, new DataSourceProvider() {

            @Override
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                return DocumentPurposeTypeInstance.findActives().map(x -> {
                    TupleDataSourceBean tuple = new TupleDataSourceBean();
                    tuple.setId(x.getExternalId());
                    tuple.setText(x.getName().getContent());
                    return tuple;
                }).collect(Collectors.toList());
            }
        });
        DATA_SOURCE_PROVIDERS.put(Constants.CYCLE_TYPE, new DataSourceProvider() {

            @Override
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                return bean.getRegistration().getDegreeType().getCycleTypes().stream()
                        .sorted(CycleType.COMPARATOR_BY_LESS_WEIGHT).map(x -> {
                            TupleDataSourceBean tuple = new TupleDataSourceBean();
                            tuple.setId(x.toString());
                            tuple.setText(x.getDescription());
                            return tuple;
                        }).collect(Collectors.toList());
            }
        });
        //TODOJN
//      public static final String CURRICULAR_PLAN = "curricularPlan";
//      public static final String APPROVED_COURSES = "approvedCourses";
//      public static final String ENROLLED_COURSES = "enrolledCourses";
//      public static final String CREDITS = "credits";
//        DATA_SOURCE_PROVIDERS.put(Constants.DOCUMENT_PURPOSE_TYPE, );
        DATA_SOURCE_PROVIDERS.put(Constants.EXECUTION_YEAR, new DataSourceProvider() {

            @Override
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                return ExecutionYear.readOpenExecutionYears().stream().sorted(ExecutionYear.COMPARATOR_BY_YEAR).map(x -> {
                    TupleDataSourceBean tuple = new TupleDataSourceBean();
                    tuple.setId(x.getExternalId());
                    tuple.setText(x.getName());
                    return tuple;
                }).collect(Collectors.toList());
            }
        });
    }
}
