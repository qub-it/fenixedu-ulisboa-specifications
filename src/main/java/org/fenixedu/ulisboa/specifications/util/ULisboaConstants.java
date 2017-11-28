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

package org.fenixedu.ulisboa.specifications.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.degreeStructure.ProgramConclusion;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituationType;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.dto.student.RegistrationConclusionBean;
import org.fenixedu.academictreasury.util.Constants;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.services.CurriculumLineServices;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorServices;
import org.joda.time.DateTime;

public class ULisboaConstants {

    public static final String BUNDLE = FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE.replace('/', '.');
    /*Empty justification used in AcademicServiceRequestSituation*/
    public static final LocalizedString EMPTY_JUSTIFICATION = BundleUtil.getLocalizedString(BUNDLE, "label.empty.justification");
    /*Codes used to identify static slots (ServiceRequestSlot)*/
    public static final String LANGUAGE = "language";
    public static final String DOCUMENT_PURPOSE_TYPE = "documentPurposeType";
    public static final String OTHER_DOCUMENT_PURPOSE = "otherDocumentPurpose";
    public static final String IS_DETAILED = "isDetailed";
    public static final String IS_URGENT = "isUrgent";
    public static final String CYCLE_TYPE = "cycleType";
    public static final String PROGRAM_CONCLUSION = "programConclusion";
    public static final String NUMBER_OF_UNITS = "numberOfUnits";
    public static final String NUMBER_OF_DAYS = "numberOfDays";
    public static final String NUMBER_OF_PAGES = "numberOfPages";
    public static final String EXECUTION_YEAR = "executionYear";
    public static final String EXECUTION_SEMESTER = "executionSemester";
    public static final String EVALUATION_SEASON = "evaluationSeason";
    public static final String CURRICULAR_PLAN = "curricularPlan";
    public static final String APPROVED_EXTRA_CURRICULUM = "approvedExtraCurriculum";
    public static final String APPROVED_STANDALONE_CURRICULUM = "approvedStandaloneCurriculum";
    public static final String APPROVED_ENROLMENTS = "approvedEnrolments";
    public static final String CURRICULUM = "curriculum";
    public static final String ENROLMENTS_BY_YEAR = "enrolmentsByYear";
    public static final String ENROLMENTS_BY_SEMESTER = "enrolmentsBySemester";
    public static final String STANDALONE_ENROLMENTS_BY_YEAR = "standaloneEnrolmentsByYear";
    public static final String EXTRACURRICULAR_ENROLMENTS_BY_YEAR = "extracurricularEnrolmentsByYear";
    public static final String ENROLMENTS_BEFORE_SEMESTER = "enrolmentsBeforeSemester";
    /*Slots used as default */
    public static final List<String> DEFAULT_PROPERTIES = Arrays.asList(LANGUAGE, EXECUTION_YEAR);
    /*Subset of AcademicServiceRequestSituationType. This are the valid states for the ULisboa Service Request */
    public static final List<AcademicServiceRequestSituationType> USED_SITUATION_TYPES =
            Arrays.asList(AcademicServiceRequestSituationType.NEW, AcademicServiceRequestSituationType.PROCESSING,
                    AcademicServiceRequestSituationType.CONCLUDED, AcademicServiceRequestSituationType.DELIVERED,
                    AcademicServiceRequestSituationType.CANCELLED, AcademicServiceRequestSituationType.REJECTED);
    /*Label for each ULisboa Service Request Processor */
    public static final String STATE_LOGGER_PROCESSOR = "label.StateLoggerProcessor.name";
    public static final String FILL_ENROLMENTS_BY_YEAR_PROPERTY_PROCESSOR =
            "label.FillRequestPropertyProcessor.EnrolmentsByYear.name";
    public static final String FILL_STANDALONE_ENROLMENTS_BY_YEAR_PROPERTY_PROCESSOR =
            "label.FillRequestPropertyProcessor.StandaloneEnrolmentsByYear.name";
    public static final String FILL_EXTRACURRICULAR_ENROLMENTS_BY_YEAR_PROPERTY_PROCESSOR =
            "label.FillRequestPropertyProcessor.ExtracurricularEnrolmentsByYear.name";
    public static final String FILL_EXTRA_CURRICULUM_PROPERTY_PROCESSOR =
            "label.FillRequestPropertyProcessor.ExtraCurriculum.name";
    public static final String FILL_STANDALONE_CURRICULUM_PROPERTY_PROCESSOR =
            "label.FillRequestPropertyProcessor.StandAloneCurriculum.name";
    public static final String FILL_APPROVED_ENROLMENTS_PROPERTY_PROCESSOR =
            "label.FillRequestPropertyProcessor.ApprovedEnrolments.name";
    public static final String FILL_ALL_PLANS_APPROVEMENTS_PROPERTY_PROCESSOR =
            "label.FillRequestPropertyProcessor.AllPlansApprovements.name";
    public static final String FILL_CURRICULUM_PROPERTY_PROCESSOR = "label.FillRequestPropertyProcessor.Curriculum.name";
    public static final String AUTOMATIC_ONLINE_REQUEST_PROCESSOR = "label.AutomaticOnlineRequestProcessor.name";
    public static final String VALIDATE_ENROLMENTS_EXISTENCE_BY_YEAR_PROCESSOR =
            "label.ValidateEnrolmentsExistenceByYearProcessor.name";
    public static final String VALIDATE_PROGRAM_CONCLUSION_PROCESSOR = "label.ValidateProgramConclusionProcessor.name";
    public static final String VALIDATE_IMPROVEMENT_ENROLMENT_PROCESSOR = "label.ValidateImprovementEnrolmentProcessor.name";
    public static final String VALIDATE_SPECIAL_SEASON_ENROLMENT_PROCESSOR = "label.ValidateSpecialSeasonEnrolmentProcessor.name";

    /* Predicates and filtering criteria */
    public static final Predicate<Enrolment> isStandalone = e -> !e.isAnnulled() && e.isStandalone();
    public static final Predicate<Enrolment> isExtraCurricular = e -> !e.isAnnulled() && e.isExtraCurricular();

    public static final Predicate<Enrolment> isNormalEnrolment = e -> !e.isAnnulled()

            && (e.getCurriculumGroup().isInternalCreditsSourceGroup() || !e.getCurriculumGroup().isNoCourseGroupCurriculumGroup())

            && (e.getParentCycleCurriculumGroup() == null || !e.getParentCycleCurriculumGroup().isExternal())

            && !ULisboaConstants.isAggregationChild.test(e);

    private static final Predicate<Enrolment> isAggregationChild =
            x -> CurriculumAggregatorServices.getAggregatorEntry(x) != null;

    public static final List<ICurriculumEntry> getLastPlanApprovements(final Registration registration) {
        return registration.getLastStudentCurricularPlan().getCurriculum(new DateTime(), null).getCurriculumEntries().stream()
                .collect(Collectors.toList());
    }

    public static final List<ICurriculumEntry> getAllPlansApprovements(final Registration registration) {
        Curriculum mergedCurricula = registration.getStudentCurricularPlansSet().stream()
                .map(scp -> scp.getCurriculum(new DateTime(), null)).reduce((c1, c2) -> {
                    c1.add(c2);
                    return c1;
                }).orElse(null);
        return mergedCurricula == null ? new ArrayList<>() : mergedCurricula.getCurriculumEntries().stream()
                .collect(Collectors.toList());
    }

    public static final List<ICurriculumEntry> getLastPlanStandaloneApprovements(final Registration registration) {
        return registration.getLastStudentCurricularPlan().getStandaloneCurriculumGroup().getEnrolmentsSet().stream()
                .filter(e -> e.isApproved()).map(ICurriculumEntry.class::cast).collect(Collectors.toList());
    }

    public static final List<ICurriculumEntry> getLastPlanExtracurricularApprovements(final Registration registration) {

        final StudentCurricularPlan plan = registration.getLastStudentCurricularPlan();
        return plan.getExtraCurriculumGroup().getCurriculumLines().stream().filter(e -> e.isApproved()).flatMap(i -> {

            if (i.isEnrolment() && i.isExtraCurricular()) {
                // HACK bypass Enrolment.getCurriculum's isExtraCurricular test
                return Collections.singleton((ICurriculumEntry) i).stream();

            } else {
                final Curriculum curriculum = i.getCurriculum(new DateTime(), (ExecutionYear) null);
                return curriculum.getCurriculumEntries().stream();
            }

        }).filter(i -> !CurriculumLineServices.isSourceOfAnyCredits(i, plan)).collect(Collectors.toList());
    }

    public static final List<ICurriculumEntry> getConclusionCurriculum(final Registration registration,
            final ProgramConclusion programConclusion) {
        if (programConclusion == null) {
            return Collections.emptyList();
        }
        final RegistrationConclusionBean conclusionBean = new RegistrationConclusionBean(registration, programConclusion);
        return conclusionBean.getCurriculumForConclusion().getCurriculumEntries().stream().collect(Collectors.toList());
        // programConclusion -> programConclusionInformation -> curriculumGroup/cycleGroup -> studentCurricularPlan
    }

    public static final Locale DEFAULT_LOCALE = new Locale("PT");

    public static final TupleDataSourceBean SELECT_OPTION =
            new TupleDataSourceBean("", BundleUtil.getString(Constants.BUNDLE, "label.TupleDataSourceBean.select.description"));

}
