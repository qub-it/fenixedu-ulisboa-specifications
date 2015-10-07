package org.fenixedu.ulisboa.specifications.ui.administrativeOffice.lists;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.CurricularCourse;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.contacts.PartyContact;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.dto.academicAdministration.SearchStudentsByCurricularCourseParametersBean;
import org.fenixedu.academic.service.services.exceptions.FenixServiceException;
import org.fenixedu.academic.ui.struts.action.academicAdministration.AcademicAdministrationApplication.AcademicAdminListingsApp;
import org.fenixedu.academic.ui.struts.action.exceptions.FenixActionException;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;
import org.fenixedu.commons.spreadsheet.StyledExcelSpreadsheet;
import org.fenixedu.ulisboa.specifications.domain.services.RegistrationServices;

@StrutsFunctionality(app = AcademicAdminListingsApp.class, path = "students-by-curricular-course",
        titleKey = "link.studentsListByCurricularCourse", accessGroup = "academic(STUDENT_LISTINGS)")
@Mapping(path = "/studentsListByCurricularCourse", module = "academicAdministration")
@Forwards({ @Forward(name = "chooseCurricularCourse", path = "/academicAdminOffice/lists/chooseCurricularCourses.jsp"),
        @Forward(name = "studentByCurricularCourse", path = "/academicAdminOffice/lists/studentsByCurricularCourses.jsp") })
public class StudentsListByCurricularCourseDA extends
        org.fenixedu.academic.ui.struts.action.administrativeOffice.lists.StudentsListByCurricularCourseDA {

    @Override
    public ActionForward showActiveCurricularCourseScope(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws FenixActionException, FenixServiceException {
        final ActionForward forward = super.showActiveCurricularCourseScope(mapping, form, request, response);

        final SearchStudentsByCurricularCourseParametersBean bean =
                (SearchStudentsByCurricularCourseParametersBean) request.getAttribute("searchBean");

        if (bean != null && bean.getDegreeCurricularPlan() != null) {
            final ExecutionDegree executionDegree =
                    bean.getDegreeCurricularPlan().getExecutionDegreeByYear(bean.getExecutionYear());
            request.setAttribute("executionDegree", executionDegree);
        }

        return forward;
    }

    public ActionForward doExportInfoToExcel(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
            HttpServletResponse response, Boolean detailed) throws FenixServiceException {

        final CurricularCourse curricularCourse = getDomainObject(request, "curricularCourseCode");
        final Integer semester = getIntegerFromRequest(request, "semester");
        final ExecutionYear executionYear =
                ExecutionYear.readExecutionYearByName((String) getFromRequest(request, "curricularYear"));
        final String year = (String) getFromRequest(request, "year");

        try {
            String filename =
                    getResourceMessage("label.students") + "_" + curricularCourse.getName() + "_("
                            + curricularCourse.getDegreeCurricularPlan().getName() + ")_" + executionYear.getYear();

            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment; filename=" + filename.replace(" ", "_") + ".xls");
            ServletOutputStream writer = response.getOutputStream();

            exportToXls(searchStudentByCriteria(executionYear, curricularCourse, semester), writer, executionYear,
                    curricularCourse, year, semester.toString(), detailed);
            writer.flush();
            response.flushBuffer();

        } catch (IOException e) {
            throw new FenixServiceException();
        }

        return null;

    }

    private List<Enrolment> searchStudentByCriteria(final ExecutionYear executionYear, final CurricularCourse curricularCourse,
            final Integer semester) {
        final List<Enrolment> result = new ArrayList<Enrolment>();

        final ExecutionSemester executionSemester = executionYear.getExecutionSemesterFor(semester);
        for (final Enrolment enrolment : curricularCourse.getEnrolmentsByExecutionPeriod(executionSemester)) {
            result.add(enrolment);
        }
        Collections.sort(result, new BeanComparator("studentCurricularPlan.registration.number"));

        return result;
    }

    private void exportToXls(List<Enrolment> registrations, OutputStream outputStream, ExecutionYear executionYear,
            CurricularCourse curricularCourse, String year, String semester, Boolean detailed) throws IOException {

        final StyledExcelSpreadsheet spreadsheet =
                new StyledExcelSpreadsheet(getResourceMessage("lists.studentByCourse.unspaced"));
        fillSpreadSheetFilters(executionYear, curricularCourse, year, semester, spreadsheet);
        fillSpreadSheetResults(registrations, spreadsheet, executionYear, detailed);
        spreadsheet.getWorkbook().write(outputStream);
    }

    private void fillSpreadSheetFilters(ExecutionYear executionYear, CurricularCourse curricularCourse, String year,
            String semester, final StyledExcelSpreadsheet spreadsheet) {
        spreadsheet.newHeaderRow();
        spreadsheet.addHeader(curricularCourse.getDegree().getNameFor(executionYear) + " - " + curricularCourse.getName() + " - "
                + executionYear.getYear() + " - " + year + " " + getResourceMessage("label.year") + " " + semester + " "
                + getResourceMessage("label.semester"));
    }

    private void fillSpreadSheetResults(List<Enrolment> registrations, final StyledExcelSpreadsheet spreadsheet,
            ExecutionYear executionYear, Boolean detailed) {
        spreadsheet.newRow();
        spreadsheet.newRow();
        spreadsheet.addCell(registrations.size() + " " + getResourceMessage("label.students"));

        setHeaders(spreadsheet, detailed);
        for (Enrolment enrolment : registrations) {
            Registration registration = enrolment.getRegistration();
            spreadsheet.newRow();
            spreadsheet.addCell(registration.getNumber().toString());
            spreadsheet.addCell(registration.getPerson().getName());
            spreadsheet.addCell(registration.getRegistrationProtocol().getCode());
            Degree degree = registration.getDegree();
            spreadsheet.addCell(!(StringUtils.isEmpty(degree.getSigla())) ? degree.getSigla() : degree.getNameFor(executionYear)
                    .toString());
            spreadsheet.addCell(enrolment.getEnrollmentState().getDescription());
            spreadsheet.addCell(enrolment.getEvaluationSeason().getName().getContent());

            final ExecutionSemester executionSemester = enrolment.getExecutionPeriod();
            Optional<SchoolClass> schoolClassOptional = RegistrationServices.getSchoolClassBy(registration, executionSemester);
            spreadsheet.addCell(schoolClassOptional.isPresent() ? schoolClassOptional.get().getEditablePartOfName() : "");
            List<Shift> shifts = registration.getShiftsFor(enrolment.getExecutionCourseFor(executionSemester));
            spreadsheet.addCell(shifts.stream().map(s -> s.getNome() + " (" + s.getShiftTypesPrettyPrint() + ")")
                    .collect(Collectors.joining(", ")));

            if (detailed) {
                spreadsheet.addCell(registration.getPerson().hasDefaultEmailAddress() ? registration.getPerson()
                        .getDefaultEmailAddressValue() : "-");
                spreadsheet.addCell(registration.getPerson().hasInstitutionalEmailAddress() ? registration.getPerson()
                        .getInstitutionalEmailAddressValue() : "-");
                PartyContact mobileContact = getMobileContact(registration.getPerson());
                spreadsheet.addCell(mobileContact != null ? mobileContact.getPresentationValue() : "-");

            }

        }
    }

    private PartyContact getMobileContact(final Person person) {
        for (PartyContact contact : person.getPartyContactsSet()) {
            if (contact.isMobile()) {
                return contact;
            }
        }
        return null;
    }

    private void setHeaders(final StyledExcelSpreadsheet spreadsheet, Boolean detailed) {
        spreadsheet.newHeaderRow();
        spreadsheet.addHeader(getResourceMessage("label.student.number"));
        spreadsheet.addHeader(getResourceMessage("label.name"));
        spreadsheet.addHeader(getResourceMessage("label.registrationAgreement"));
        spreadsheet.addHeader(getResourceMessage("label.degree"));
        spreadsheet.addHeader(getResourceMessage("label.state"));
        spreadsheet.addHeader(getResourceMessage("label.epoch"));
        spreadsheet.addHeader(BundleUtil.getString(Bundle.APPLICATION, "label.class"));
        spreadsheet.addHeader(BundleUtil.getString(Bundle.APPLICATION, "label.shifts"));

        if (detailed) {
            spreadsheet.addHeader(getResourceMessage("label.email"));
            spreadsheet.addHeader(getResourceMessage("label.institutional.email"));
            spreadsheet.addHeader(getResourceMessage("label.mobile"));
        }
    }

    static private String getResourceMessage(String key) {
        return BundleUtil.getString(Bundle.ACADEMIC, key);
    }
}
