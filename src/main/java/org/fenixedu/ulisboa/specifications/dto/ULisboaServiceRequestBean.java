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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.Enrolment.EnrolmentPredicate;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentPurposeTypeInstance;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestProperty;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestRestriction;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ServiceRequestSlotEntry;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.ULisboaServiceRequest;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors.ULisboaServiceRequestProcessor;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors.ValidateImprovementEnrolmentProcessor;
import org.fenixedu.ulisboa.specifications.domain.serviceRequests.processors.ValidateSpecialSeasonEnrolmentProcessor;
import org.fenixedu.ulisboa.specifications.domain.services.enrollment.EnrolmentServices;
import org.fenixedu.ulisboa.specifications.util.ULisboaConstants;
import org.joda.time.DateTime;

import com.google.common.collect.Sets;

public class ULisboaServiceRequestBean implements IBean {

    public static final Map<String, DataSourceProvider> DATA_SOURCE_PROVIDERS = new HashMap<String, DataSourceProvider>();

    static {
        initProviderMap();
    }

    private Registration registration;
    private ServiceRequestType serviceRequestType;
    private List<TupleDataSourceBean> serviceRequestTypesDataSource;
    private List<ServiceRequestPropertyBean> serviceRequestPropertyBeans;
    private boolean requestedOnline;
    private DateTime requestDate;

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
            if (x.getRequestedOnline() != null && x.getRequestedOnline() && this.isRequestedOnline()) {
                tuple.setText(x.getRichName());
            } else {
                tuple.setText(x.getName().getContent());
            }
            return tuple;
        }).collect(Collectors.toList());
    }

    public List<ServiceRequestPropertyBean> getServiceRequestPropertyBeans() {
        return serviceRequestPropertyBeans;
    }

    public void setServiceRequestPropertyBeans(List<ServiceRequestPropertyBean> serviceRequestPropertyBeans) {
        this.serviceRequestPropertyBeans = serviceRequestPropertyBeans;
    }

    public <T> T getServiceRequestPropertyValue(String code) {
        Optional<ServiceRequestPropertyBean> serviceRequestPropertyBean =
                getServiceRequestPropertyBeans().stream().filter(s -> s.getCode().equals(code)).findFirst();

        return serviceRequestPropertyBean.isPresent() ? serviceRequestPropertyBean.get().getValue() : null;
    }

    public boolean isRequestedOnline() {
        return requestedOnline;
    }

    public void setRequestedOnline(boolean requestedOnline) {
        this.requestedOnline = requestedOnline;
    }

    public DateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(DateTime requestDate) {
        this.requestDate = requestDate;
    }

    public ULisboaServiceRequestBean() {
        setServiceRequestPropertyBeans(new ArrayList<ServiceRequestPropertyBean>());
        setRequestDate(new DateTime());
    }

    public ULisboaServiceRequestBean(Registration registration, boolean requestedOnline) {
        this();
        setRegistration(registration);
        setRequestedOnline(requestedOnline);
        setServiceRequestTypesDataSource(
                ServiceRequestType.findActive().filter(ServiceRequestRestriction.restrictionFilter(registration))
                        .filter(srt -> !requestedOnline || srt.isRequestedOnline())
                        .sorted(ServiceRequestType.COMPARE_BY_CATEGORY_THEN_BY_NAME).collect(Collectors.toList()));
    }

    public ULisboaServiceRequestBean(ULisboaServiceRequest request) {
        setRegistration(request.getRegistration());
        setRequestedOnline(request.getRequestedOnline());
        setRequestDate(request.getRequestDate());
        setServiceRequestType(request.getServiceRequestType());
        setServiceRequestPropertyBeans(new ArrayList<ServiceRequestPropertyBean>());
        for (ServiceRequestProperty property : request.getSortedServiceRequestProperties()) {
            ServiceRequestPropertyBean propertyBean = new ServiceRequestPropertyBean(property);
            if (propertyBean.getUiComponent().needDataSource()) {
                DataSourceProvider dataSourceProvider =
                        ULisboaServiceRequestBean.DATA_SOURCE_PROVIDERS.get(propertyBean.getCode());
                propertyBean.setDataSource(dataSourceProvider.provideDataSourceList(this));
            }
            serviceRequestPropertyBeans.add(propertyBean);
        }
    }

    private boolean isSameServiceRequestType() {
        Set<String> oldSlotNames =
                serviceRequestPropertyBeans.stream().map(ServiceRequestPropertyBean::getCode).collect(Collectors.toSet());
        Set<String> newSlotNames = serviceRequestType.getServiceRequestSlotEntriesSet().stream()
                .map(entry -> entry.getServiceRequestSlot().getCode()).collect(Collectors.toSet());
        return oldSlotNames.size() == newSlotNames.size() && Sets.difference(oldSlotNames, newSlotNames).isEmpty();
    }

    public void updateModelLists() {
        //update service request type
        if (serviceRequestType == null) {
            serviceRequestPropertyBeans = new ArrayList<ServiceRequestPropertyBean>();
        } else if (!isSameServiceRequestType()) {
            serviceRequestPropertyBeans = new ArrayList<ServiceRequestPropertyBean>();
            serviceRequestType.getServiceRequestSlotEntriesSet().stream().sorted(ServiceRequestSlotEntry.COMPARE_BY_ORDER_NUMBER)
                    .forEachOrdered(entry -> serviceRequestPropertyBeans.add(new ServiceRequestPropertyBean(entry)));
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

    private static List<TupleDataSourceBean> provideForCurriculumEntry(Stream<ICurriculumEntry> collection) {
        return collection.sorted((x, y) -> x.getName().compareTo(y.getName())).map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.getExternalId());
            tuple.setText(x.getCode() + " - " + x.getName().getContent() + " - " + x.getExecutionPeriod().getQualifiedName());
            return tuple;
        }).collect(Collectors.toList());
    }

    public static void initProviderMap() {
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.LANGUAGE, new DataSourceProvider() {

            @Override
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                return CoreConfiguration.supportedLocales().stream().map(x -> {
                    TupleDataSourceBean tuple = new TupleDataSourceBean();
                    tuple.setId(x.toString());
                    tuple.setText(x.getDisplayLanguage());
                    return tuple;
                }).collect(Collectors.toList());
            }
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.DOCUMENT_PURPOSE_TYPE, new DataSourceProvider() {

            @Override
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                return DocumentPurposeTypeInstance.findActivesFor(bean.getServiceRequestType())
                        .sorted(DocumentPurposeTypeInstance.COMPARE_BY_LEGACY).map(x -> {
                    TupleDataSourceBean tuple = new TupleDataSourceBean();
                    tuple.setId(x.getExternalId());
                    tuple.setText(x.getName().getContent());
                    return tuple;
                }).collect(Collectors.toList());
            }
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.CYCLE_TYPE, new DataSourceProvider() {

            @Override
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                if (bean.getRegistration().getDegreeType() == null) {
                    return Collections.emptyList();
                }
                return bean.getRegistration().getDegreeType().getCycleTypes().stream().sorted(CycleType.COMPARATOR_BY_LESS_WEIGHT)
                        .map(x -> {
                    TupleDataSourceBean tuple = new TupleDataSourceBean();
                    tuple.setId(x.toString());
                    tuple.setText(x.getDescription());
                    return tuple;
                }).collect(Collectors.toList());
            }
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.PROGRAM_CONCLUSION, new DataSourceProvider() {

            @Override
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                if (ProgramConclusion.conclusionsFor(bean.getRegistration()).count() == 0) {
                    return Collections.emptyList();
                }
                return ProgramConclusion.conclusionsFor(bean.getRegistration()).map(x -> {
                    TupleDataSourceBean tuple = new TupleDataSourceBean();
                    tuple.setId(x.getExternalId());
                    tuple.setText(x.getName().getContent());
                    return tuple;
                }).collect(Collectors.toList());
            }
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.CURRICULAR_PLAN, new DataSourceProvider() {

            @Override
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {

                final ExecutionYear executionYear = bean.getServiceRequestPropertyValue(ULisboaConstants.EXECUTION_YEAR);
                if (executionYear == null) {
                    return bean.getRegistration().getStudentCurricularPlansSet().stream()
                            .sorted((x, y) -> x.getName().compareTo(y.getName())).map(x -> {
                        TupleDataSourceBean tuple = new TupleDataSourceBean();
                        tuple.setId(x.getExternalId());
                        tuple.setText(x.getName());
                        return tuple;
                    }).collect(Collectors.toList());
                }

                final StudentCurricularPlan studentCurricularPlan =
                        bean.getRegistration().getStudentCurricularPlan(executionYear);
                final TupleDataSourceBean result = new TupleDataSourceBean();
                if (studentCurricularPlan != null) {
                    result.setId(studentCurricularPlan.getExternalId());
                    result.setText(studentCurricularPlan.getName());
                }

                return Collections.singletonList(result);
            }
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.APPROVED_EXTRA_CURRICULUM, new DataSourceProvider() {

            @Override
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                if (bean.getRegistration().getLastStudentCurricularPlan() == null
                        || bean.getRegistration().getLastStudentCurricularPlan().getExtraCurriculumGroup() == null) {
                    return Collections.emptyList();
                }
                Stream<ICurriculumEntry> collection = bean.getRegistration().getLastStudentCurricularPlan()
                        .getExtraCurriculumGroup().getEnrolmentsSet().stream().map(ICurriculumEntry.class::cast);
                return provideForCurriculumEntry(collection);
            }

        });

        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.APPROVED_STANDALONE_CURRICULUM, new DataSourceProvider() {

            @Override
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                if (bean.getRegistration().getLastStudentCurricularPlan() == null
                        || bean.getRegistration().getLastStudentCurricularPlan().getStandaloneCurriculumGroup() == null) {
                    return Collections.emptyList();
                }
                Stream<ICurriculumEntry> collection =
                        ULisboaConstants.getLastPlanStandaloneApprovements(bean.getRegistration()).stream();
                return provideForCurriculumEntry(collection);
            }
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.APPROVED_ENROLMENTS, new DataSourceProvider() {

            @Override
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                if (bean.getRegistration().getLastStudentCurricularPlan() == null) {
                    return Collections.emptyList();
                }
                Stream<ICurriculumEntry> collection = ULisboaConstants.getLastPlanApprovements(bean.getRegistration()).stream();
                return provideForCurriculumEntry(collection);
            }
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.CURRICULUM, new DataSourceProvider() {

            @Override
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                if (bean.getRegistration().getLastStudentCurricularPlan() == null) {
                    return Collections.emptyList();
                }
                final Curriculum curriculum =
                        bean.getRegistration().getLastStudentCurricularPlan().getCurriculum(new DateTime(), null);
                Stream<ICurriculumEntry> collection = curriculum.getCurriculumEntries().stream();
                return provideForCurriculumEntry(collection);
            }
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.ENROLMENTS_BY_YEAR, new DataSourceProvider() {

            @Override
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                final ExecutionYear executionYear = bean.getServiceRequestPropertyValue(ULisboaConstants.EXECUTION_YEAR);
                if (executionYear == null || bean.getRegistration().getStudentCurricularPlan(executionYear) == null) {
                    return Collections.emptyList();
                }
                Stream<ICurriculumEntry> collection = bean.getRegistration().getStudentCurricularPlan(executionYear)
                        .getEnrolmentsByExecutionYear(executionYear).stream().filter(ULisboaConstants.isNormalEnrolment)
                        .sorted(Enrolment.COMPARATOR_BY_NAME_AND_ID).map(ICurriculumEntry.class::cast);
                return provideForCurriculumEntry(collection);
            }
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.ENROLMENTS_BY_SEMESTER, new DataSourceProvider() {

            @Override
            //TODOJN - review this provider, I put dummy values
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                final ExecutionSemester executionSemester =
                        bean.getServiceRequestPropertyValue(ULisboaConstants.EXECUTION_SEMESTER);
                StudentCurricularPlan studentCurricularPlan =
                        bean.getServiceRequestPropertyValue(ULisboaConstants.CURRICULAR_PLAN);
                if (studentCurricularPlan == null) {
                    studentCurricularPlan = bean.getRegistration().getStudentCurricularPlan(executionSemester);
                }
                if (executionSemester == null || studentCurricularPlan == null) {
                    return Collections.emptyList();
                }
                Stream<ICurriculumEntry> collection = studentCurricularPlan.getEnrolmentsByExecutionPeriod(executionSemester)
                        .stream().sorted(Enrolment.COMPARATOR_BY_NAME_AND_ID).map(ICurriculumEntry.class::cast);
                return provideForCurriculumEntry(collection);
            }
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.STANDALONE_ENROLMENTS_BY_YEAR, new DataSourceProvider() {

            @Override
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                final ExecutionYear executionYear = bean.getServiceRequestPropertyValue(ULisboaConstants.EXECUTION_YEAR);
                if (executionYear == null || bean.getRegistration().getStudentCurricularPlan(executionYear) == null) {
                    return Collections.emptyList();
                }
                Stream<ICurriculumEntry> collection = bean.getRegistration().getStudentCurricularPlan(executionYear)
                        .getEnrolmentsByExecutionYear(executionYear).stream().filter(ULisboaConstants.isStandalone)
                        .sorted(Enrolment.COMPARATOR_BY_NAME_AND_ID).map(ICurriculumEntry.class::cast);
                return provideForCurriculumEntry(collection);
            }
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.EXTRACURRICULAR_ENROLMENTS_BY_YEAR, new DataSourceProvider() {

            @Override
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                final ExecutionYear executionYear = bean.getServiceRequestPropertyValue(ULisboaConstants.EXECUTION_YEAR);
                if (executionYear == null || bean.getRegistration().getStudentCurricularPlan(executionYear) == null) {
                    return Collections.emptyList();
                }
                Stream<ICurriculumEntry> collection = bean.getRegistration().getStudentCurricularPlan(executionYear)
                        .getEnrolmentsByExecutionYear(executionYear).stream().filter(ULisboaConstants.isExtraCurricular)
                        .sorted(Enrolment.COMPARATOR_BY_NAME_AND_ID).map(ICurriculumEntry.class::cast);
                return provideForCurriculumEntry(collection);
            }
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.ENROLMENTS_BEFORE_SEMESTER, new DataSourceProvider() {

            @Override
            //TODOJN - review this provider, I put dummy values
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                final ExecutionSemester executionSemester =
                        bean.getServiceRequestPropertyValue(ULisboaConstants.EXECUTION_SEMESTER);
                StudentCurricularPlan studentCurricularPlan =
                        bean.getServiceRequestPropertyValue(ULisboaConstants.CURRICULAR_PLAN);
                if (studentCurricularPlan == null) {
                    studentCurricularPlan = bean.getRegistration().getStudentCurricularPlan(executionSemester);
                }
                if (executionSemester == null || studentCurricularPlan == null) {
                    return Collections.emptyList();
                }

                List<Enrolment> enrolments = getEnrolmentsToEnrol(executionSemester, studentCurricularPlan,
                        bean.getServiceRequestType().getULisboaServiceRequestProcessorsSet());
                Stream<ICurriculumEntry> collection =
                        enrolments.stream().filter(e -> e.getExecutionPeriod().isBefore(executionSemester))
                                .sorted(Enrolment.COMPARATOR_BY_NAME_AND_ID).map(ICurriculumEntry.class::cast);
                return provideForCurriculumEntry(collection);
            }

            private List<Enrolment> getEnrolmentsToEnrol(ExecutionSemester executionSemester,
                    StudentCurricularPlan studentCurricularPlan, Set<ULisboaServiceRequestProcessor> processors) {
                EvaluationSeason evaluationSeason = null;
                EnrolmentPredicate predicate = null;
                for (ULisboaServiceRequestProcessor processor : processors) {
                    if (processor instanceof ValidateImprovementEnrolmentProcessor) {
                        ValidateImprovementEnrolmentProcessor improvementProcessor =
                                (ValidateImprovementEnrolmentProcessor) processor;
                        evaluationSeason = improvementProcessor.getEvaluationSeason();
                        predicate = Enrolment.getPredicateImprovement();
                        break;
                    }
                    if (processor instanceof ValidateSpecialSeasonEnrolmentProcessor) {
                        ValidateSpecialSeasonEnrolmentProcessor specialSeasonProcessor =
                                (ValidateSpecialSeasonEnrolmentProcessor) processor;
                        evaluationSeason = specialSeasonProcessor.getEvaluationSeason();
                        predicate = Enrolment.getPredicateSpecialSeason();
                        break;
                    }
                }
                if (evaluationSeason == null) {
                    //No filter
                    return studentCurricularPlan.getRoot().getCurriculumModulesSet().stream()
                            .filter(module -> module.isEnrolment()).map(Enrolment.class::cast).collect(Collectors.toList());
                }

                return EnrolmentServices.getEnrolmentsToEnrol(studentCurricularPlan, executionSemester, evaluationSeason,
                        predicate);
            }

        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.EXECUTION_YEAR, new DataSourceProvider() {

            @Override
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                return ExecutionYear.readNotClosedExecutionYears().stream()
                        .filter(e -> e.isAfterOrEquals(bean.getRegistration().getRegistrationYear()))
                        .sorted(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR).map(x -> {
                    TupleDataSourceBean tuple = new TupleDataSourceBean();
                    tuple.setId(x.getExternalId());
                    tuple.setText(x.getName());
                    return tuple;
                }).collect(Collectors.toList());
            }
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.EXECUTION_SEMESTER, new DataSourceProvider() {

            @Override
            //TODOJN - review this provider, I put dummy values
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                return ExecutionSemester.readNotClosedExecutionPeriods().stream()
                        .sorted((e1, e2) -> ExecutionSemester.COMPARATOR_BY_BEGIN_DATE.compare(e2, e1)).map(x -> {
                    TupleDataSourceBean tuple = new TupleDataSourceBean();
                    tuple.setId(x.getExternalId());
                    tuple.setText(x.getQualifiedName());
                    return tuple;
                }).collect(Collectors.toList());
            }
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.EVALUATION_SEASON, new DataSourceProvider() {

            @Override
            //TODOJN - review this provider, I put dummy values
            public List<TupleDataSourceBean> provideDataSourceList(ULisboaServiceRequestBean bean) {
                Stream<EvaluationSeason> seasons =
                        Stream.concat(EvaluationSeason.readNormalSeasons(), EvaluationSeason.readImprovementSeasons());
                return Stream.concat(seasons, EvaluationSeason.readSpecialSeasons()).map(x -> {
                    TupleDataSourceBean tuple = new TupleDataSourceBean();
                    tuple.setId(x.getExternalId());
                    tuple.setText(getExtendedName(x));
                    return tuple;
                }).collect(Collectors.toList());
            }

            //TODOJN - Delete after provider reviewed
            private String getExtendedName(EvaluationSeason x) {
                if (x.isNormal()) {
                    return "Normal - " + x.getName().getContent();
                }
                if (x.isImprovement()) {
                    return "Improvement - " + x.getName().getContent();
                }
                return "Special - " + x.getName().getContent();
            }
        });
    }
}
