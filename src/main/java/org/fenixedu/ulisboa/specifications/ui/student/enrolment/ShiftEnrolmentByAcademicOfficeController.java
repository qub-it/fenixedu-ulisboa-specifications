/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.Lesson;
import org.fenixedu.academic.domain.SchoolClass;
import org.fenixedu.academic.domain.Shift;
import org.fenixedu.academic.domain.ShiftType;
import org.fenixedu.academic.domain.accessControl.AcademicAuthorizationGroup;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.groups.PermissionService;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationServices;
import org.fenixedu.academic.dto.ShiftToEnrol;
import org.fenixedu.academic.service.services.exceptions.FenixServiceException;
import org.fenixedu.academic.service.services.exceptions.NotAuthorizedException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.ulisboa.specifications.domain.services.enrollment.shift.ReadShiftsToEnroll;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsBaseController;
import org.fenixedu.ulisboa.specifications.ui.FenixeduUlisboaSpecificationsController;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import pt.ist.fenixframework.Atomic;

/**
 * 
 * @author shezad - Sep 10, 2015
 *
 */
@SpringFunctionality(app = FenixeduUlisboaSpecificationsController.class, title = "label.title.shiftEnrolmentByAcademicOffice", accessGroup = "logged")
@RequestMapping("/academicAdministration/shiftEnrolment")
public class ShiftEnrolmentByAcademicOfficeController extends FenixeduUlisboaSpecificationsBaseController {

	@RequestMapping(value = "{registrationOid}/{semesterOid}")
	public String home(@PathVariable("registrationOid") Registration registration,
			@PathVariable("semesterOid") ExecutionSemester executionSemester, Model model) {

		checkUser();

		final List<EnrolmentPeriodDTO> enrolmentBeans = new ArrayList<EnrolmentPeriodDTO>();
		for (final ExecutionSemester otherExecutionSemester : executionSemester.getExecutionYear()
				.getExecutionPeriodsSet()) {
			enrolmentBeans.add(new EnrolmentPeriodDTO(registration, otherExecutionSemester,
					otherExecutionSemester == executionSemester));
		}

		if (!enrolmentBeans.isEmpty()) {
			enrolmentBeans.sort((eb1, eb2) -> eb1.compareTo(eb2));

			try {
				final List<ShiftToEnrol> shiftsToEnrol = ReadShiftsToEnroll.read(registration, executionSemester);
				shiftsToEnrol.sort(
						(s1, s2) -> s1.getExecutionCourse().getName().compareTo(s2.getExecutionCourse().getName()));
				model.addAttribute("shiftsToEnrol", shiftsToEnrol);
			} catch (NotAuthorizedException e) {
				addErrorMessage(e.getLocalizedMessage(), model);
			} catch (FenixServiceException e) {
				addErrorMessage(
						BundleUtil.getString("resources.FenixeduUlisboaSpecificationsResources", e.getMessage()),
						model);
//            addActionMessage(request, "error.enrollment.period.closed", exception.getArgs());
			} catch (DomainException e) {
				addErrorMessage(e.getLocalizedMessage(), model);
			}

		}

		model.addAttribute("enrolmentBeans", enrolmentBeans);

		return "academicOffice/shiftEnrolment/shiftEnrolment";
	}

	@RequestMapping(value = "possibleShiftsToEnrol.json/{registrationOid}/{executionCourseOid}/{shiftType}")
	public @ResponseBody String getPossibleShiftsToEnrol(@PathVariable("registrationOid") Registration registration,
			@PathVariable("executionCourseOid") ExecutionCourse executionCourse,
			@PathVariable("shiftType") ShiftType shiftType) {

		checkUser();

		final List<Shift> shifts = executionCourse.getAssociatedShifts().stream().filter(s -> s.containsType(shiftType))
				.sorted(Shift.SHIFT_COMPARATOR_BY_NAME).collect(Collectors.toList());

		final JsonArray result = new JsonArray();
		for (final Shift shift : shifts) {
			if (shift.getLotacao().intValue() > shift.getStudentsSet().size()) {
				JsonObject jsonShift = new JsonObject();
				jsonShift.addProperty("name", shift.getNome());
				jsonShift.addProperty("type", shift.getShiftTypesPrettyPrint());
				jsonShift.addProperty("lessons", shift.getLessonPresentationString());
				jsonShift.addProperty("externalId", shift.getExternalId());
				result.add(jsonShift);
			}
		}

		return new GsonBuilder().create().toJson(result);
	}

	@RequestMapping(value = "addShift/{registrationOid}/{semesterOid}/{shiftOid}")
	public String addShift(@PathVariable("registrationOid") Registration registration,
			@PathVariable("semesterOid") ExecutionSemester executionSemester, @PathVariable("shiftOid") Shift shift,
			Model model) {

		checkUser();

		try {
			addShiftService(registration, shift);
			addInfoMessage(BundleUtil.getString("resources.FenixeduUlisboaSpecificationsResources",
					"message.shiftEnrolment.addShift.success"), model);
		} catch (DomainException e) {
			addErrorMessage(BundleUtil.getString("resources.FenixeduUlisboaSpecificationsResources", e.getMessage()),
					model);
		}

		return home(registration, executionSemester, model);
	}

	@Atomic
	protected void addShiftService(Registration registration, Shift shift) {
		if (!shift.reserveForStudent(registration)) {
			throw new DomainException("error.shiftEnrolment.shiftFull", shift.getNome(),
					shift.getShiftTypesPrettyPrint(), shift.getExecutionCourse().getName());
		}
	}

	@RequestMapping(value = "removeShift/{registrationOid}/{semesterOid}/{shiftOid}")
	public String removeShift(@PathVariable("registrationOid") Registration registration,
			@PathVariable("semesterOid") ExecutionSemester executionSemester, @PathVariable("shiftOid") Shift shift,
			Model model) {

		checkUser();

		try {
			removeShiftService(registration, shift);
			addInfoMessage(BundleUtil.getString("resources.FenixeduUlisboaSpecificationsResources",
					"message.shiftEnrolment.removeShift.success"), model);
		} catch (DomainException e) {
			addErrorMessage(e.getLocalizedMessage(), model);
		}

		return home(registration, executionSemester, model);
	}

	@Atomic
	private void removeShiftService(Registration registration, Shift shift) {
		registration.removeShifts(shift);
	}

	@RequestMapping(value = "currentSchedule.json/{registrationOid}/{executionSemesterOid}", produces = "application/json; charset=utf-8")
	public @ResponseBody String schedule(@PathVariable("registrationOid") Registration registration,
			@PathVariable("executionSemesterOid") ExecutionSemester executionSemester) {

		checkUser();

		final JsonArray result = new JsonArray();

		for (final Shift shift : registration.getShiftsFor(executionSemester)) {
			for (Lesson lesson : shift.getAssociatedLessonsSet()) {
				final DateTime now = new DateTime();
				final DateTime weekDay = now.withDayOfWeek(lesson.getDiaSemana().getDiaSemanaInDayOfWeekJodaFormat());
				final DateTime startTime = weekDay.withTime(lesson.getBeginHourMinuteSecond().getHour(),
						lesson.getBeginHourMinuteSecond().getMinuteOfHour(), 0, 0);
				final DateTime endTime = weekDay.withTime(lesson.getEndHourMinuteSecond().getHour(),
						lesson.getEndHourMinuteSecond().getMinuteOfHour(), 0, 0);

				final JsonObject event = new JsonObject();
				event.addProperty("id", lesson.getExternalId());
				event.addProperty("start", startTime.toString());
				event.addProperty("end", endTime.toString());
				event.addProperty("title", shift.getExecutionCourse().getName() + " ("
						+ shift.getShiftTypesCodePrettyPrint() + " - " + shift.getNome() + ")");
				event.addProperty("shiftId", shift.getExternalId());
				result.add(event);
			}
		}

		return result.toString();
	}

	static private void checkUser() {
		if (!(AcademicAuthorizationGroup.get(AcademicOperationType.STUDENT_ENROLMENTS).isMember(Authenticate.getUser())
				|| PermissionService.hasAccess("STUDENT_ENROLMENTS", Authenticate.getUser()))) {
			throw new SecurityException("error.authorization.notGranted");
		}
	}

	public static class EnrolmentPeriodDTO implements Serializable, Comparable<EnrolmentPeriodDTO> {

		private final Registration registration;
		private final ExecutionSemester executionSemester;
		private Boolean selected;

		public EnrolmentPeriodDTO(Registration registration, ExecutionSemester executionSemester, Boolean selected) {
			super();
			this.registration = registration;
			this.executionSemester = executionSemester;
			this.selected = selected;
		}

		public Registration getRegistration() {
			return registration;
		}

		public ExecutionSemester getExecutionSemester() {
			return executionSemester;
		}

		public Boolean getSelected() {
			return selected;
		}

		public void setSelected(Boolean selected) {
			this.selected = selected;
		}

		public SchoolClass getSchoolClass() {
			return RegistrationServices.getSchoolClassBy(getRegistration(), executionSemester).orElse(null);
		}

		@Override
		public int compareTo(EnrolmentPeriodDTO o) {
			int result = Degree.COMPARATOR_BY_NAME_AND_ID.compare(getRegistration().getDegree(),
					o.getRegistration().getDegree());
			return result == 0 ? getExecutionSemester().compareTo(o.getExecutionSemester()) : result;
		}

		public Map<Lesson, Collection<Lesson>> getLessonsOverlaps() {
			final Map<Lesson, Collection<Lesson>> overlapsMap = new HashMap<Lesson, Collection<Lesson>>();

			final List<Lesson> allLessons = registration.getShiftsFor(getExecutionSemester()).stream()
					.flatMap(s -> s.getAssociatedLessonsSet().stream()).collect(Collectors.toList());
			while (!allLessons.isEmpty()) {
				final Lesson lesson = allLessons.remove(0);
				final Set<Lesson> overlappingLessons = allLessons.stream()
						.filter(l -> getLessonIntervalHack(l).overlaps(getLessonIntervalHack(lesson)))
						.collect(Collectors.toSet());
				if (!overlappingLessons.isEmpty()) {
					overlapsMap.put(lesson, overlappingLessons);
				}
			}

			return overlapsMap;
		}

		/**
		 * HACK: this interval is not accurate, because it doesn't takes into account
		 * lesson instance dates
		 */
		private static Interval getLessonIntervalHack(final Lesson lesson) {
			final int weekDay = lesson.getDiaSemana().getDiaSemanaInDayOfWeekJodaFormat();
			return new Interval(
					new LocalDate().toDateTime(lesson.getBeginHourMinuteSecond().toLocalTime()).withDayOfWeek(weekDay),
					new LocalDate().toDateTime(lesson.getEndHourMinuteSecond().toLocalTime()).withDayOfWeek(weekDay));
		}

	}
}
