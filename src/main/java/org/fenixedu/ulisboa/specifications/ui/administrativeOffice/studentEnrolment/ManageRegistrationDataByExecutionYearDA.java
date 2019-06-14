package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.studentEnrolment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationDataByExecutionYear;
import org.fenixedu.academic.domain.student.RegistrationDataServices;
import org.fenixedu.academic.ui.struts.action.administrativeOffice.student.SearchForStudentsDA;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.ulisboa.specifications.dto.student.RegistrationDataBean;

import pt.ist.fenixframework.Atomic;

@Mapping(path = "/manageRegistrationData", module = "academicAdministration", functionality = SearchForStudentsDA.class)
@Forwards({

        @Forward(name = "edit",
                path = "/academicAdminOffice/student/registration/manageRegistrationDataByExecutionYear/editRegistrationDataByExecutionYear.jsp"),

        @Forward(name = "viewRegistrationDetails", path = "/academicAdminOffice/student/registration/viewRegistrationDetails.jsp")

})
public class ManageRegistrationDataByExecutionYearDA extends
        org.fenixedu.academic.ui.struts.action.administrativeOffice.studentEnrolment.ManageRegistrationDataByExecutionYearDA {

    @SuppressWarnings("serial")
    static public class RegistrationDataEditBean extends RegistrationDataBean {

        private Integer overridenCurricularYear;

        public RegistrationDataEditBean(final RegistrationDataByExecutionYear data) {
            super(data);
            this.setOverridenCurricularYear(getCurricularYearResult().getResult());
        }

        public RegistrationDataByExecutionYear getDataByExecutionYear() {
            return getData();
        }

        public Integer getOverridenCurricularYear() {
            return overridenCurricularYear;
        }

        public void setOverridenCurricularYear(final Integer input) {
            this.overridenCurricularYear = input;
        }

    }

    @Override
    public ActionForward prepareEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {

        final RegistrationDataByExecutionYear dataByExecutionYear = getDomainObject(request, "registrationDataByExecutionYearId");
        request.setAttribute("dataByExecutionYearBean", new RegistrationDataEditBean(dataByExecutionYear));

        return mapping.findForward("edit");
    }

    @Override
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        final RegistrationDataEditBean bean = getRenderedObject("dataByExecutionYearBean");

        try {
            editService(bean);
            addActionMessage("success", request, "success.RegistrationDataByExecutionYear.edit",
                    bean.getExecutionYear().getQualifiedName());
        } catch (DomainException e) {
            addActionMessage("error", request, e.getKey(), e.getArgs());
            request.setAttribute("dataByExecutionYearBean", bean);

            return mapping.findForward("edit");
        }

        final Registration registration = bean.getDataByExecutionYear().getRegistration();
        request.setAttribute("registration", registration);
        return mapping.findForward("viewRegistrationDetails");
    }

    @Atomic
    private void editService(final RegistrationDataEditBean bean) {
        final RegistrationDataByExecutionYear data = bean.getData();
        data.edit(bean.getEnrolmentDate());
        RegistrationDataServices.setCurricularYear(data, bean.getOverridenCurricularYear());
    }

    public ActionForward delete(final ActionMapping mapping, final ActionForm actionForm, final HttpServletRequest request,
            final HttpServletResponse response) {

        final RegistrationDataByExecutionYear data = getDomainObject(request, "registrationDataByExecutionYearId");
        final Registration registration = data.getRegistration();

        try {
            delete(data);
        } catch (final DomainException e) {
            addActionMessage("error", request, e.getKey(), e.getArgs());
        }

        request.setAttribute("registration", registration);
        return mapping.findForward("viewRegistrationDetails");
    }

    @Atomic
    static private void delete(final RegistrationDataByExecutionYear data) {
        if (data != null) {
            RegistrationDataServices.delete(data);
        }
    }

}
