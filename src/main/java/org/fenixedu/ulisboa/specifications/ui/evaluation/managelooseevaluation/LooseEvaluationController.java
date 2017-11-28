/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: anil.mamede@qub-it.com
 * Contributors: luis.egidio@qub-it.com
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
package org.fenixedu.ulisboa.specifications.ui.evaluation.managelooseevaluation;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.fenixedu.academic.domain.DomainObjectUtil;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.EnrolmentEvaluation;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScale;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.academic.util.EnrolmentEvaluationState;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.evaluation.EvaluationServices;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices;
import org.fenixedu.ulisboa.specifications.domain.services.CurriculumLineServices;
import org.fenixedu.ulisboa.specifications.domain.services.enrollment.EnrolmentServices;
import org.fenixedu.ulisboa.specifications.domain.services.evaluation.EnrolmentEvaluationServices;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorServices;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;

import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

@Component("org.fenixedu.ulisboa.specifications.ui.evaluation.managelooseevaluation")
@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.LooseEvaluationBean",
        accessGroup = "academic(MANAGE_MARKSHEETS)")
@RequestMapping(LooseEvaluationController.CONTROLLER_URL)
public class LooseEvaluationController extends FenixeduUlisboaSpecificationsBaseController {

    public static final String CONTROLLER_URL = "/fenixedu-ulisboa-specifications/evaluation/managelooseevaluation";

    private static final String JSP_PATH = CONTROLLER_URL.substring(1);

    private String jspPage(final String page) {
        return JSP_PATH + "/" + page;
    }

    @Autowired
    private HttpSession session;

    @Autowired
    private HttpServletRequest request;

    private static final String _CREATE_URI = "/create/";
    public static final String CREATE_URL = CONTROLLER_URL + _CREATE_URI;

    @RequestMapping(value = _CREATE_URI + "{scpId}/{executionSemesterId}", method = RequestMethod.GET)
    public String create(@PathVariable("scpId") final StudentCurricularPlan studentCurricularPlan,
            @PathVariable("executionSemesterId") final ExecutionSemester semester, final Model model) {

        model.addAttribute("studentCurricularPlan", studentCurricularPlan);
        model.addAttribute("LooseEvaluationBean_enrolment_options",
                studentCurricularPlan.getEnrolmentsSet().stream().filter(e -> e.getExecutionPeriod() == semester)
                        .sorted(CurriculumLineServices.COMPARATOR).collect(Collectors.toList()));

        final boolean possibleOldData = semester.getExecutionYear().getEndCivilYear() < 2016;
        final Stream<EvaluationSeason> evaluationSeasons =
                possibleOldData ? EvaluationSeasonServices.findAll() : EvaluationSeasonServices.findByActive(true);
        model.addAttribute("typeValues",
                evaluationSeasons.sorted(EvaluationSeasonServices.SEASON_ORDER_COMPARATOR).collect(Collectors.toList()));

        model.addAttribute("gradeScaleValues",
                Arrays.<GradeScale> asList(GradeScale.values()).stream()
                        .map(l -> new TupleDataSourceBean(((GradeScale) l).name(), ((GradeScale) l).getDescription()))
                        .collect(Collectors.<TupleDataSourceBean> toList()));

        model.addAttribute("improvementSemesterValues", ExecutionSemester.readNotClosedPublicExecutionPeriods().stream()
                .sorted(ExecutionSemester.COMPARATOR_BY_BEGIN_DATE.reversed()).collect(Collectors.toList()));

        model.addAttribute("executionSemester", semester);

        final String url = String.format(
                "/academicAdministration/studentEnrolmentsExtended.do?scpID=%s&executionSemesterID=%s&method=prepare",
                studentCurricularPlan.getExternalId(), semester.getExternalId());

        final String backUrl = GenericChecksumRewriter.injectChecksumInUrl(request.getContextPath(), url, session);
        model.addAttribute("backUrl", backUrl);

        // comparators
        final Comparator<EnrolmentEvaluation> c1 =
                (x, y) -> CurriculumLineServices.COMPARATOR.compare(x.getEnrolment(), y.getEnrolment());
        final Comparator<EnrolmentEvaluation> c2 = (x, y) -> EvaluationSeasonServices.SEASON_ORDER_COMPARATOR
                .compare(x.getEvaluationSeason(), y.getEvaluationSeason());

        final List<EnrolmentEvaluation> evaluations =
                studentCurricularPlan.getEnrolmentsSet().stream().flatMap(enr -> enr.getEvaluationsSet().stream())
                        .filter(ev -> ev.getExecutionPeriod() == semester || ev.getEnrolment().getExecutionPeriod() == semester)
                        .filter(ev -> ev.getCompetenceCourseMarkSheet() == null && ev.getMarkSheet() == null)
                        .filter(ev -> EvaluationServices
                                .findEnrolmentCourseEvaluations(ev.getEnrolment(), ev.getEvaluationSeason(), semester).isEmpty()
                                || AcademicAccessRule.isProgramAccessibleToFunction(AcademicOperationType.ENROLMENT_WITHOUT_RULES,
                                        studentCurricularPlan.getDegree(), Authenticate.getUser()))
                        .sorted(c1.thenComparing(c2).thenComparing(DomainObjectUtil.COMPARATOR_BY_ID))
                        .collect(Collectors.toList());

        model.addAttribute("evaluationsSet", evaluations);

        return jspPage("create");
    }

    @RequestMapping(value = _CREATE_URI + "{scpId}/{executionSemesterId}", method = RequestMethod.POST)
    public String create(@PathVariable("scpId") final StudentCurricularPlan studentCurricularPlan,
            @PathVariable("executionSemesterId") final ExecutionSemester executionSemester,
            @RequestParam(value = "enrolment", required = false) Enrolment enrolment,
            @RequestParam(value = "availabledate",
                    required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate availableDate,
            @RequestParam(value = "examdate", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate examDate,
            @RequestParam(value = "gradescale", required = false) GradeScale gradeScale,
            @RequestParam(value = "grade", required = false) String grade,
            @RequestParam(value = "type", required = false) EvaluationSeason type,
            @RequestParam(value = "improvementsemester", required = false) ExecutionSemester improvementSemester, Model model,
            final RedirectAttributes redirectAttributes) {

        try {

            final List<String> others = checkIfAllGradesAreSameScale(enrolment, gradeScale);
            if (!others.isEmpty()) {
                addErrorMessage(ULisboaSpecificationsUtil.bundle("error.LooseEvaluationBean.grade.not.same.scale",
                        others.stream().collect(Collectors.joining(", "))), model);
                return create(studentCurricularPlan, executionSemester, model);
            }

            createLooseEvaluation(enrolment, examDate, Grade.createGrade(grade, gradeScale), availableDate, type,
                    improvementSemester);
            return redirect(CREATE_URL + studentCurricularPlan.getExternalId() + "/" + executionSemester.getExternalId(), model,
                    redirectAttributes);
        } catch (final DomainException e) {
            addErrorMessage(e, model);
            return create(studentCurricularPlan, executionSemester, model);
        }
    }

    private void addErrorMessage(final DomainException e, final Model model) {
        if (!e.getLocalizedMessage().startsWith("!")) {
            addErrorMessage(e.getLocalizedMessage(), model);
        } else {
            addErrorMessage(BundleUtil.getString(Bundle.ACADEMIC, e.getKey()), model);
        }
    }

    static private List<String> checkIfAllGradesAreSameScale(final Enrolment enrolment, final GradeScale gradeScale) {
        final List<String> result = Lists.newArrayList();

        for (final EnrolmentEvaluation iter : enrolment.getEvaluationsSet()) {
            final GradeScale other = iter.getGrade().getGradeScale();

            if (!iter.getGrade().isEmpty() && other != gradeScale) {
                result.add(other.getDescription());
            }
        }

        return result;
    }

    @Atomic
    public void createLooseEvaluation(Enrolment enrolment, LocalDate examDate, Grade grade, LocalDate availableDate,
            EvaluationSeason season, ExecutionSemester improvementSemester) {

        EnrolmentEvaluation evaluation = enrolment.getEnrolmentEvaluation(season, improvementSemester, false).orElse(null);
        if (evaluation == null || evaluation.getCompetenceCourseMarkSheet() != null) {
            evaluation = new EnrolmentEvaluation(enrolment, season);
            if (season.isImprovement()) {
                evaluation.setExecutionPeriod(improvementSemester);
            }
        }

        final Person person = Authenticate.getUser().getPerson();
        evaluation.edit(person, grade, availableDate.toDate(), examDate.toDateTimeAtStartOfDay().toDate());
        evaluation.confirmSubmission(person, "");
        EnrolmentEvaluationServices.onStateChange(evaluation);
        EnrolmentServices.updateState(enrolment);
        CurriculumAggregatorServices.updateAggregatorEvaluation(evaluation);
    }

    private static final String _DELETE_URI = "/delete/";
    public static final String DELETE_URL = CONTROLLER_URL + _DELETE_URI;

    @RequestMapping(value = _DELETE_URI + "{scpId}/{evaluationId}/{executionSemesterId}", method = RequestMethod.POST)
    public String delete(@PathVariable("scpId") final StudentCurricularPlan studentCurricularPlan,
            @PathVariable("evaluationId") EnrolmentEvaluation enrolmentEvaluation,
            @PathVariable("executionSemesterId") final ExecutionSemester executionSemester, Model model,
            final RedirectAttributes redirectAttributes) {

        try {
            deleteLooseEvaluation(enrolmentEvaluation);
        } catch (final DomainException e) {
            addErrorMessage(e, model);
        }

        return redirect(CREATE_URL + studentCurricularPlan.getExternalId() + "/" + executionSemester.getExternalId(), model,
                redirectAttributes);
    }

    @Atomic
    private void deleteLooseEvaluation(final EnrolmentEvaluation evaluation) {
        final Enrolment enrolment = evaluation.getEnrolment();
        final EvaluationSeason season = evaluation.getEvaluationSeason();

        evaluation.setEnrolmentEvaluationState(EnrolmentEvaluationState.TEMPORARY_OBJ);
        EnrolmentEvaluationServices.onStateChange(evaluation);

        if (FenixFramework.isDomainObjectValid(evaluation)) {
            //TODO: hack since listeners can cause object to be deleted
            //logic should be two-step, first change to Temporary and if it still exists delete
            evaluation.delete();
        }

        EnrolmentServices.updateState(enrolment);
        CurriculumAggregatorServices.updateAggregatorEvaluation(enrolment, (EnrolmentEvaluation) null);
    }

    private static final String _ANNUL_URI = "/annul/";
    public static final String ANNUL_URL = CONTROLLER_URL + _ANNUL_URI;

    @RequestMapping(value = _ANNUL_URI + "{scpId}/{evaluationId}/{executionSemesterId}", method = RequestMethod.POST)
    public String annul(@PathVariable("scpId") final StudentCurricularPlan studentCurricularPlan,
            @PathVariable("evaluationId") EnrolmentEvaluation enrolmentEvaluation,
            @PathVariable("executionSemesterId") final ExecutionSemester executionSemester, Model model,
            final RedirectAttributes redirectAttributes) {

        try {
            EnrolmentEvaluationServices.annul(enrolmentEvaluation);
        } catch (final DomainException e) {
            addErrorMessage(e, model);
        }

        return redirect(CREATE_URL + studentCurricularPlan.getExternalId() + "/" + executionSemester.getExternalId(), model,
                redirectAttributes);
    }

    private static final String _ACTIVATE_URI = "/activate/";
    public static final String ACTIVATE_URL = CONTROLLER_URL + _ACTIVATE_URI;

    @RequestMapping(value = _ACTIVATE_URI + "{scpId}/{evaluationId}/{executionSemesterId}", method = RequestMethod.POST)
    public String activate(@PathVariable("scpId") final StudentCurricularPlan studentCurricularPlan,
            @PathVariable("evaluationId") EnrolmentEvaluation enrolmentEvaluation,
            @PathVariable("executionSemesterId") final ExecutionSemester executionSemester, Model model,
            final RedirectAttributes redirectAttributes) {

        try {
            EnrolmentEvaluationServices.activate(enrolmentEvaluation);
        } catch (final DomainException e) {
            addErrorMessage(e, model);
        }

        return redirect(CREATE_URL + studentCurricularPlan.getExternalId() + "/" + executionSemester.getExternalId(), model,
                redirectAttributes);
    }

}
