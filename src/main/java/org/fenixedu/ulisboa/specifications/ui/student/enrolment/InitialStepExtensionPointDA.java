/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2016 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2016 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: shezad.anavarali@qub-it.com
 *
 * 
 * This file is part of FenixEdu fenixedu-ulisboa-specifications.
 *
 * FenixEdu fenixedu-ulisboa-specifications is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu fenixedu-ulisboa-specifications is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu fenixedu-ulisboa-specifications.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.ulisboa.specifications.ui.student.enrolment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.ui.struts.action.base.FenixDispatchAction;
import org.fenixedu.academic.ui.struts.action.student.StudentApplication.StudentEnrollApp;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentProcess;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentStep;
import org.fenixedu.ulisboa.specifications.ui.student.enrolment.process.EnrolmentStepTemplate;

/**
 * 
 * @author nadir
 *
 */
@StrutsFunctionality(app = StudentEnrollApp.class, path = "initialStepExtensionPoint-student-enrollment",
        titleKey = "link.initialStepExtensionPoint.student.enrolment", bundle = "FenixeduUlisboaSpecificationsResources")
@Mapping(module = "student", path = "/initialStepExtensionPointStudentEnrollment")
public class InitialStepExtensionPointDA extends FenixDispatchAction {

    @FunctionalInterface
    static public interface ExtensionPredicate {
        public boolean appliesTo(ExecutionSemester executionSemester, StudentCurricularPlan studentCurricularPlan);
    }

    static final private String MAPPING_MODULE = "/student";
    static final private String MAPPING = MAPPING_MODULE + "/initialStepExtensionPointStudentEnrollment";
    static final private String ACTION = MAPPING + ".do";

    static private String EXTENSION_URL = null;

    static private ExtensionPredicate EXTENSION_PREDICATE = null;

    static public String getEntryPointURL() {
        return ACTION;
    }

    @EntryPoint
    public ActionForward prepare(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {

        return EXTENSION_PREDICATE.appliesTo(getExecutionSemester(request),
                getStudentCurricularPlan(request)) ? proceedToExtensionURL(request) : proceedToEnrolmentProcess(request);

    }

    protected ActionForward proceedToExtensionURL(HttpServletRequest request) {
        final ActionForward redirect =
                redirect(EXTENSION_URL + "?executionSemesterOID=" + getExecutionSemester(request).getExternalId()
                        + "&studentCurricularPlanOID=" + getStudentCurricularPlan(request).getExternalId(), request);
        redirect.setModule("/");

        return redirect;

    }

    protected ActionForward proceedToEnrolmentProcess(HttpServletRequest request) {
        final EnrolmentProcess process = EnrolmentProcess.find(getExecutionSemester(request), getStudentCurricularPlan(request));
        final ActionForward redirect =
                redirect(process.getContinueURL(request).replaceFirst(request.getContextPath(), ""), request);
        redirect.setModule("/");

        return redirect;
    }

    protected StudentCurricularPlan getStudentCurricularPlan(HttpServletRequest request) {
        return getDomainObject(request, "studentCurricularPlanOID");
    }

    protected ExecutionSemester getExecutionSemester(HttpServletRequest request) {
        return getDomainObject(request, "executionSemesterOID");
    }

    static public void registerExtension(LocalizedString label, String extensionUrl, ExtensionPredicate extensionPredicate) {
        EnrolmentProcess.addBeginEnrolmentStep(0, createEnrolmentStep(label));
        EXTENSION_URL = extensionUrl;
        EXTENSION_PREDICATE = extensionPredicate;
    }

    static private EnrolmentStepTemplate createEnrolmentStep(LocalizedString label) {
        return new EnrolmentStepTemplate(

                label,

                getEntryPointURL(),

                (enrolmentProcess) -> {
                    return EnrolmentStep.buildArgsStruts(enrolmentProcess.getExecutionSemester(),
                            enrolmentProcess.getStudentCurricularPlan());
                },

                (enrolmentProcess) -> {
                    return enrolmentProcess.getEnrolmentPeriods().stream().anyMatch(i -> i.isForCurricularCourses());
                });
    }

}
