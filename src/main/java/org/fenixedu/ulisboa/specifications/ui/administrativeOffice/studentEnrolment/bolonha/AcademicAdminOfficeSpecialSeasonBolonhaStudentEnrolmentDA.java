/**
 * Copyright © 2002 Instituto Superior Técnico
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
package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.studentEnrolment.bolonha;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.ui.struts.action.administrativeOffice.student.SearchForStudentsDA;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices;

@Mapping(path = "/specialSeasonBolonhaStudentEnrolment", module = "academicAdministration",
        formBean = "bolonhaStudentEnrollmentForm", functionality = SearchForStudentsDA.class)
@Forwards({
        @Forward(name = "chooseEvaluationSeason",
                path = "/academicAdminOffice/student/enrollment/bolonha/chooseEvaluationSeason.jsp"),
        @Forward(name = "showDegreeModulesToEnrol",
                path = "/academicAdminOffice/student/enrollment/bolonha/showDegreeModulesToEnrol.jsp") })
public class AcademicAdminOfficeSpecialSeasonBolonhaStudentEnrolmentDA extends
        org.fenixedu.academic.ui.struts.action.administrativeOffice.studentEnrolment.bolonha.AcademicAdminOfficeSpecialSeasonBolonhaStudentEnrolmentDA {

    @Override
    public ActionForward prepareChooseEvaluationSeason(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) {

        super.prepareChooseEvaluationSeason(mapping, form, request, response);

        request.setAttribute("chooseEvaluationSeasonBean", new SpecialSeasonChooseEvaluationSeasonBean());

        return mapping.findForward("chooseEvaluationSeason");
    }

    @Override
    protected String getAction() {
        return "/specialSeasonBolonhaStudentEnrolment.do";
    }

    @SuppressWarnings("serial")
    static private class SpecialSeasonChooseEvaluationSeasonBean
            extends org.fenixedu.academic.dto.student.enrollment.bolonha.SpecialSeasonChooseEvaluationSeasonBean {

        @Override
        public Collection<EvaluationSeason> getActiveEvaluationSeasons() {
            return EvaluationSeasonServices.findByActive(true).filter(i -> i.isSpecial())
                    .sorted(EvaluationSeasonServices.SEASON_ORDER_COMPARATOR).collect(Collectors.toList());
        }
    }

}
