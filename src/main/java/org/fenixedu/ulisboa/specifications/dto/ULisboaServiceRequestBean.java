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
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
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

    public static final Map<String, DataSourceProvider> DATA_SOURCE_PROVIDERS = new HashMap<>();

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

    public void setRegistration(final Registration registration) {
        this.registration = registration;
    }

    public ServiceRequestType getServiceRequestType() {
        return serviceRequestType;
    }

    public void setServiceRequestType(final ServiceRequestType serviceRequestType) {
        this.serviceRequestType = serviceRequestType;
    }

    public List<TupleDataSourceBean> getServiceRequestTypesDataSource() {
        return serviceRequestTypesDataSource;
    }

    public void setServiceRequestTypesDataSource(final List<ServiceRequestType> documentTypesValues) {
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

    public void setServiceRequestPropertyBeans(final List<ServiceRequestPropertyBean> serviceRequestPropertyBeans) {
        this.serviceRequestPropertyBeans = serviceRequestPropertyBeans;
    }

    public <T> T getServiceRequestPropertyValue(final String code) {
        Optional<ServiceRequestPropertyBean> serviceRequestPropertyBean =
                getServiceRequestPropertyBeans().stream().filter(s -> s.getCode().equals(code)).findFirst();

        return serviceRequestPropertyBean.isPresent() ? serviceRequestPropertyBean.get().getValue() : null;
    }

    public boolean isRequestedOnline() {
        return requestedOnline;
    }

    public void setRequestedOnline(final boolean requestedOnline) {
        this.requestedOnline = requestedOnline;
    }

    public DateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(final DateTime requestDate) {
        this.requestDate = requestDate;
    }

    public ULisboaServiceRequestBean() {
        setServiceRequestPropertyBeans(new ArrayList<ServiceRequestPropertyBean>());
        setRequestDate(new DateTime());
    }

    public ULisboaServiceRequestBean(final Registration registration, final boolean requestedOnline) {
        this();
        setRegistration(registration);
        setRequestedOnline(requestedOnline);
        setServiceRequestTypesDataSource(
                ServiceRequestType.findActive().filter(ServiceRequestRestriction.restrictionFilter(registration))
                        .filter(srt -> !requestedOnline || srt.isRequestedOnline())
                        .sorted(ServiceRequestType.COMPARE_BY_CATEGORY_THEN_BY_NAME).collect(Collectors.toList()));
    }

    public ULisboaServiceRequestBean(final ULisboaServiceRequest request) {
        setRegistration(request.getRegistration());
        setRequestedOnline(request.getRequestedOnline());
        setRequestDate(request.getRequestDate());
        setServiceRequestType(request.getServiceRequestType());
        setServiceRequestPropertyBeans(new ArrayList<ServiceRequestPropertyBean>());
        //Use its properties and set the value
        for (ServiceRequestProperty property : request.getSortedServiceRequestProperties()) {
            ServiceRequestPropertyBean propertyBean = new ServiceRequestPropertyBean(property);
            if (propertyBean.getUiComponent().needDataSource()) {
                DataSourceProvider dataSourceProvider =
                        ULisboaServiceRequestBean.DATA_SOURCE_PROVIDERS.get(propertyBean.getCode());
                propertyBean.setDataSource(dataSourceProvider.provideDataSourceList(this));
            }
            serviceRequestPropertyBeans.add(propertyBean);
        }

        List<String> propertiesCodes = request.getSortedServiceRequestProperties().stream()
                .map(p -> p.getServiceRequestSlot().getCode()).collect(Collectors.toList());

        //Add new slots that might exist in configuration of this request
        serviceRequestType.getServiceRequestSlotEntriesSet().stream().filter(ServiceRequestSlotEntry.PRINT_PROPERTY.negate())
                .filter(entry -> !propertiesCodes.contains(entry.getServiceRequestSlot().getCode()))
                .sorted(ServiceRequestSlotEntry.COMPARE_BY_ORDER_NUMBER).forEachOrdered(entry -> {
                    ServiceRequestPropertyBean propertyBean = new ServiceRequestPropertyBean(entry);
                    if (propertyBean.getUiComponent().needDataSource()) {
                        DataSourceProvider dataSourceProvider =
                                ULisboaServiceRequestBean.DATA_SOURCE_PROVIDERS.get(propertyBean.getCode());
                        propertyBean.setDataSource(dataSourceProvider.provideDataSourceList(this));
                    }
                    serviceRequestPropertyBeans.add(propertyBean);
                });

    }

    private boolean isSameServiceRequestType() {
        Set<String> oldSlotNames =
                serviceRequestPropertyBeans.stream().map(ServiceRequestPropertyBean::getCode).collect(Collectors.toSet());
        Set<String> newSlotNames = serviceRequestType.getServiceRequestSlotEntriesSet().stream()
                .filter(ServiceRequestSlotEntry.PRINT_PROPERTY.negate()).map(entry -> entry.getServiceRequestSlot().getCode())
                .collect(Collectors.toSet());
        return oldSlotNames.size() == newSlotNames.size() && Sets.difference(oldSlotNames, newSlotNames).isEmpty();
    }

    public void updateModelLists(final boolean forceUpdate) {
        // update service request type
        if (serviceRequestType == null) {
            serviceRequestPropertyBeans = new ArrayList<>();
        } else if (!forceUpdate && !isSameServiceRequestType()) {
            serviceRequestPropertyBeans = new ArrayList<>();
            serviceRequestType.getServiceRequestSlotEntriesSet().stream().filter(ServiceRequestSlotEntry.PRINT_PROPERTY.negate())
                    .sorted(ServiceRequestSlotEntry.COMPARE_BY_ORDER_NUMBER)
                    .forEachOrdered(entry -> serviceRequestPropertyBeans.add(new ServiceRequestPropertyBean(entry)));
        }
        // update properties
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

    private static List<TupleDataSourceBean> provideForCurriculumEntry(final Stream<ICurriculumEntry> collection) {
        return collection.sorted((x, y) -> x.getName().compareTo(y.getName())).map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.getExternalId());
            tuple.setText(x.getCode() + " - " + x.getName().getContent() + " - " + x.getExecutionPeriod().getQualifiedName());
            return tuple;
        }).collect(Collectors.toList());
    }

    public static void initProviderMap() {
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.LANGUAGE, bean -> CoreConfiguration.supportedLocales().stream().map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.toString());
            tuple.setText(x.getDisplayLanguage());
            return tuple;
        }).collect(Collectors.toList()));
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.DOCUMENT_PURPOSE_TYPE, bean -> DocumentPurposeTypeInstance
                .findActivesFor(bean.getServiceRequestType()).sorted(DocumentPurposeTypeInstance.COMPARE_BY_LEGACY).map(x -> {
                    TupleDataSourceBean tuple = new TupleDataSourceBean();
                    tuple.setId(x.getExternalId());
                    tuple.setText(x.getName().getContent());
                    return tuple;
                }).collect(Collectors.toList()));
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.CYCLE_TYPE, bean -> {
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
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.PROGRAM_CONCLUSION, bean -> {
            if (ProgramConclusion.conclusionsFor(bean.getRegistration()).count() == 0) {
                return Collections.emptyList();
            }
            return ProgramConclusion.conclusionsFor(bean.getRegistration()).map(x -> {
                TupleDataSourceBean tuple = new TupleDataSourceBean();
                tuple.setId(x.getExternalId());
                tuple.setText(x.getName().getContent());
                return tuple;
            }).collect(Collectors.toList());
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.CURRICULAR_PLAN, bean -> {

            final ExecutionYear executionYear = bean.getServiceRequestPropertyValue(ULisboaConstants.EXECUTION_YEAR);
            if (executionYear == null) {
                return bean.getRegistration().getStudentCurricularPlansSet().stream()
                        .sorted((x1, y) -> x1.getName().compareTo(y.getName())).map(x2 -> {
                            TupleDataSourceBean tuple = new TupleDataSourceBean();
                            tuple.setId(x2.getExternalId());
                            tuple.setText(x2.getName());
                            return tuple;
                        }).collect(Collectors.toList());
            }

            final StudentCurricularPlan studentCurricularPlan = bean.getRegistration().getStudentCurricularPlan(executionYear);
            final TupleDataSourceBean result = new TupleDataSourceBean();
            if (studentCurricularPlan != null) {
                result.setId(studentCurricularPlan.getExternalId());
                result.setText(studentCurricularPlan.getName());
            }

            return Collections.singletonList(result);
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.APPROVED_EXTRA_CURRICULUM, bean -> {
            if (bean.getRegistration().getLastStudentCurricularPlan() == null
                    || bean.getRegistration().getLastStudentCurricularPlan().getExtraCurriculumGroup() == null) {
                return Collections.emptyList();
            }
            Stream<ICurriculumEntry> collection =
                    ULisboaConstants.getLastPlanExtracurricularApprovements(bean.getRegistration()).stream();
            return provideForCurriculumEntry(collection);
        });

        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.APPROVED_STANDALONE_CURRICULUM, bean -> {
            if (bean.getRegistration().getLastStudentCurricularPlan() == null
                    || bean.getRegistration().getLastStudentCurricularPlan().getStandaloneCurriculumGroup() == null) {
                return Collections.emptyList();
            }
            Stream<ICurriculumEntry> collection =
                    ULisboaConstants.getLastPlanStandaloneApprovements(bean.getRegistration()).stream();
            return provideForCurriculumEntry(collection);
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.APPROVED_ENROLMENTS, bean -> {
            if (bean.getRegistration().getLastStudentCurricularPlan() == null) {
                return Collections.emptyList();
            }
            Stream<ICurriculumEntry> collection = ULisboaConstants.getLastPlanApprovements(bean.getRegistration()).stream();
            return provideForCurriculumEntry(collection);
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.ACTIVE_ENROLMENTS, bean -> {
            if (bean.getRegistration() == null || bean.getRegistration().getLastStudentCurricularPlan() == null) {
                return Collections.emptyList();
            }
            List<CurriculumLine> enrolments = ULisboaConstants.getEnrolmentsInEnrolledState(bean.getRegistration());
            return provideForCurriculumEntry(enrolments.stream().map(ICurriculumEntry.class::cast));
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.FLUNKED_ENROLMENTS, bean -> {
            if (bean.getRegistration() == null || bean.getRegistration().getLastStudentCurricularPlan() == null) {
                return Collections.emptyList();
            }
            List<CurriculumLine> enrolments = ULisboaConstants.getNotApprovedEnrolments(bean.getRegistration());
            return provideForCurriculumEntry(enrolments.stream().map(ICurriculumEntry.class::cast));
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.CURRICULUM, bean -> {
            if (bean.getRegistration().getLastStudentCurricularPlan() == null) {
                return Collections.emptyList();
            }
            final Curriculum curriculum =
                    bean.getRegistration().getLastStudentCurricularPlan().getCurriculum(new DateTime(), null);
            Stream<ICurriculumEntry> collection = curriculum.getCurriculumEntries().stream();
            return provideForCurriculumEntry(collection);
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.ENROLMENTS_BY_YEAR, bean -> {
            final ExecutionYear executionYear = bean.getServiceRequestPropertyValue(ULisboaConstants.EXECUTION_YEAR);
            if (executionYear == null || bean.getRegistration().getStudentCurricularPlan(executionYear) == null) {
                return Collections.emptyList();
            }
            Stream<ICurriculumEntry> collection = bean.getRegistration().getStudentCurricularPlan(executionYear)
                    .getEnrolmentsByExecutionYear(executionYear).stream().filter(ULisboaConstants.isNormalEnrolment)
                    .sorted(Enrolment.COMPARATOR_BY_NAME_AND_ID).map(ICurriculumEntry.class::cast);
            return provideForCurriculumEntry(collection);
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.ENROLMENTS_BY_SEMESTER, bean -> {
            final ExecutionSemester executionSemester = bean.getServiceRequestPropertyValue(ULisboaConstants.EXECUTION_SEMESTER);
            StudentCurricularPlan studentCurricularPlan = bean.getServiceRequestPropertyValue(ULisboaConstants.CURRICULAR_PLAN);
            if (studentCurricularPlan == null) {
                studentCurricularPlan = bean.getRegistration().getStudentCurricularPlan(executionSemester);
            }
            if (executionSemester == null || studentCurricularPlan == null) {
                return Collections.emptyList();
            }
            Stream<ICurriculumEntry> collection = studentCurricularPlan.getEnrolmentsByExecutionPeriod(executionSemester).stream()
                    .sorted(Enrolment.COMPARATOR_BY_NAME_AND_ID).map(ICurriculumEntry.class::cast);
            return provideForCurriculumEntry(collection);
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.STANDALONE_ENROLMENTS_BY_YEAR, bean -> {
            final ExecutionYear executionYear = bean.getServiceRequestPropertyValue(ULisboaConstants.EXECUTION_YEAR);
            if (executionYear == null || bean.getRegistration().getStudentCurricularPlan(executionYear) == null) {
                return Collections.emptyList();
            }
            Stream<ICurriculumEntry> collection = bean.getRegistration().getStudentCurricularPlan(executionYear)
                    .getEnrolmentsByExecutionYear(executionYear).stream().filter(ULisboaConstants.isStandalone)
                    .sorted(Enrolment.COMPARATOR_BY_NAME_AND_ID).map(ICurriculumEntry.class::cast);
            return provideForCurriculumEntry(collection);
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.EXTRACURRICULAR_ENROLMENTS_BY_YEAR, bean -> {
            final ExecutionYear executionYear = bean.getServiceRequestPropertyValue(ULisboaConstants.EXECUTION_YEAR);
            if (executionYear == null || bean.getRegistration().getStudentCurricularPlan(executionYear) == null) {
                return Collections.emptyList();
            }
            Stream<ICurriculumEntry> collection = bean.getRegistration().getStudentCurricularPlan(executionYear)
                    .getEnrolmentsByExecutionYear(executionYear).stream().filter(ULisboaConstants.isExtraCurricular)
                    .sorted(Enrolment.COMPARATOR_BY_NAME_AND_ID).map(ICurriculumEntry.class::cast);
            return provideForCurriculumEntry(collection);
        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.ENROLMENTS_BEFORE_SEMESTER, new DataSourceProvider() {

            @Override
            public List<TupleDataSourceBean> provideDataSourceList(final ULisboaServiceRequestBean bean) {
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

            private List<Enrolment> getEnrolmentsToEnrol(final ExecutionSemester executionSemester,
                    final StudentCurricularPlan studentCurricularPlan, final Set<ULisboaServiceRequestProcessor> processors) {
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
                    // No filter
                    return studentCurricularPlan.getRoot().getCurriculumModulesSet().stream()
                            .filter(module -> module.isEnrolment()).map(Enrolment.class::cast).collect(Collectors.toList());
                }

                return EnrolmentServices.getEnrolmentsToEnrol(studentCurricularPlan, executionSemester, evaluationSeason,
                        predicate);
            }

        });
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.EXECUTION_YEAR,
                bean -> ExecutionYear.readNotClosedExecutionYears().stream()
                        .filter(e -> e.isAfterOrEquals(bean.getRegistration().getRegistrationYear()))
                        .sorted(ExecutionYear.REVERSE_COMPARATOR_BY_YEAR).map(x -> {
                            TupleDataSourceBean tuple = new TupleDataSourceBean();
                            tuple.setId(x.getExternalId());
                            tuple.setText(x.getName());
                            return tuple;
                        }).collect(Collectors.toList()));
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.EXECUTION_SEMESTER, bean -> ExecutionSemester.readNotClosedExecutionPeriods()
                .stream().sorted(ExecutionSemester.COMPARATOR_BY_BEGIN_DATE.reversed()).map(x -> {
                    TupleDataSourceBean tuple = new TupleDataSourceBean();
                    tuple.setId(x.getExternalId());
                    tuple.setText(x.getQualifiedName());
                    return tuple;
                }).collect(Collectors.toList()));
        DATA_SOURCE_PROVIDERS.put(ULisboaConstants.EVALUATION_SEASON, new DataSourceProvider() {

            @Override
            // TODOJN - review this provider, I put dummy values
            public List<TupleDataSourceBean> provideDataSourceList(final ULisboaServiceRequestBean bean) {
                Stream<EvaluationSeason> seasons =
                        Stream.concat(EvaluationSeason.readNormalSeasons(), EvaluationSeason.readImprovementSeasons());
                return Stream.concat(seasons, EvaluationSeason.readSpecialSeasons()).map(x -> {
                    TupleDataSourceBean tuple = new TupleDataSourceBean();
                    tuple.setId(x.getExternalId());
                    tuple.setText(getExtendedName(x));
                    return tuple;
                }).collect(Collectors.toList());
            }

            // TODOJN - Delete after provider reviewed
            private String getExtendedName(final EvaluationSeason x) {
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
