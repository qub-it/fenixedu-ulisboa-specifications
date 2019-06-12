package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.student;

import java.io.Serializable;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.IEnrolment;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.curriculum.CreditsReasonType;
import org.fenixedu.academic.domain.studentCurriculum.Credits;
import org.fenixedu.academic.ui.struts.action.administrativeOffice.student.SearchForStudentsDA;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.joda.time.LocalDate;
import org.joda.time.YearMonthDay;

import pt.ist.fenixframework.Atomic;

@Mapping(path = "/studentDismissalsExtended", module = "academicAdministration", formBean = "studentDismissalForm",
        functionality = SearchForStudentsDA.class)
@Forwards({ @Forward(name = "manage", path = "/academicAdminOffice/dismissal/managementDismissals.jsp"),
        @Forward(name = "editCredits", path = "/academicAdminOffice/dismissal/editCredits.jsp") })
public class StudentDismissalsDA extends org.fenixedu.academic.ui.struts.action.administrativeOffice.student.StudentDismissalsDA {

    protected static final String DATE_FORMAT = "yyyy-MM-dd";

    @SuppressWarnings("serial")
    public static class CreditsBean extends org.fenixedu.academic.dto.administrativeOffice.dismissal.CreditsBean
            implements Serializable {

        private CreditsReasonType reason;

        public CreditsBean(final Credits credits) {
            super(credits);
            this.reason = credits.getReason();
        }

        public CreditsReasonType getReason() {
            return reason;
        }

        public void setReason(CreditsReasonType reason) {
            this.reason = reason;
        }

        public Collection<CreditsReasonType> getReasonOptions() {
            return CreditsReasonType.findActive();
        }
    }

    @Override
    public ActionForward prepareEditCredits(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        request.setAttribute("creditsBean", new CreditsBean(getCredits(request)));
        return mapping.findForward("editCredits");
    }

    public ActionForward editCredits(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {

        final CreditsBean bean = getRenderedObject("creditsBean");

        try {
            editService(bean);
        } catch (DomainException e) {
            addActionMessage("error", request, e.getKey(), e.getArgs());
            request.setAttribute("creditsBean", bean);

            return mapping.findForward("editCredits");
        }

        request.setAttribute("scpID", bean.getCredits().getStudentCurricularPlan().getExternalId());

        return manage(mapping, form, request, response);
    }

    @Atomic
    private void editService(CreditsBean bean) {
        final Credits credits = bean.getCredits();

        credits.setOfficialDate(bean.getOfficialDate());
        final LocalDate officialDate = credits.getOfficialDate();

        for (final IEnrolment sourceEnrolment : credits.getIEnrolments()) {
            final YearMonthDay approvementDate = sourceEnrolment.getApprovementDate();

            if (officialDate != null) {
                if (approvementDate.isAfter(officialDate)) {
                    throw new DomainException("error.Credits.officialDate.must.be.after.source.enrolments.approval.date",
                            approvementDate.toString(DATE_FORMAT));
                }
            }
        }

        credits.setReason(bean.getReason());
    }

    private Credits getCredits(HttpServletRequest request) {
        return getDomainObject(request, "creditsId");
    }

}
