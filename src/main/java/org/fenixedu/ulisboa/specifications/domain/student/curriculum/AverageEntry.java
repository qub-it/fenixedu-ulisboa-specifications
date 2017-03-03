/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 *
 * 
 * This file is part of FenixEdu fenixedu-ulisboa-specifications.
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
package org.fenixedu.ulisboa.specifications.domain.student.curriculum;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.student.curriculum.Curriculum;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.Dismissal;
import org.fenixedu.academic.domain.studentCurriculum.Equivalence;
import org.fenixedu.academic.domain.studentCurriculum.Substitution;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.services.CurricularPeriodServices;
import org.fenixedu.ulisboa.specifications.ui.renderers.student.curriculum.CurriculumLayout;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class AverageEntry implements Comparable<AverageEntry> {

    private Class approvalType;
    private ICurriculumEntry entry;
    private Integer curricularYear;
    private Integer curricularSemester;
    private ExecutionYear executionYear;
    private String entryInfo;
    private String entryCurriculumLinesInfo;

    public AverageEntry(final Class approvalType, final ICurriculumEntry entry) {
        this.approvalType = approvalType;
        this.entry = entry;
        this.curricularYear = getCurricularYear(entry);
        this.curricularSemester = getCurricularSemester(entry);
        this.executionYear = entry.getExecutionYear();
        this.entryInfo = getEntryInfo(entry);
        this.entryCurriculumLinesInfo = getEntryCurriculumLinesInfo(entry);
    }

    public Class getApprovalType() {
        return approvalType;
    }

    public ICurriculumEntry getEntry() {
        return entry;
    }

    public Integer getCurricularYear() {
        return curricularYear;
    }

    public Integer getCurricularSemester() {
        return curricularSemester;
    }

    public ExecutionYear getExecutionYear() {
        return executionYear;
    }

    public String getEntryInfo() {
        return entryInfo;
    }

    public String getEntryCurriculumLinesInfo() {
        return entryCurriculumLinesInfo;
    }

    static private Integer getCurricularYear(final ICurriculumEntry entry) {
        Integer result = null;

        if (entry instanceof CurriculumLine) {
            result = CurricularPeriodServices.getCurricularYear((CurriculumLine) entry);
        }

        return result;
    }

    static private Integer getCurricularSemester(final ICurriculumEntry entry) {
        Integer result = null;

        if (entry instanceof CurriculumLine) {
            result = CurricularPeriodServices.getCurricularSemester((CurriculumLine) entry);
        }

        return result;
    }

    static private String getEntryInfo(final ICurriculumEntry entry) {
        String curricularYear = "";
        String curricularSemester = "";
        if (entry instanceof CurriculumLine) {
            curricularYear = String.valueOf(CurricularPeriodServices.getCurricularYear((CurriculumLine) entry)) + " "
                    + BundleUtil.getString(Bundle.APPLICATION, "label.curricular.year") + ", ";
            curricularSemester = String.valueOf(CurricularPeriodServices.getCurricularSemester((CurriculumLine) entry)) + " "
                    + BundleUtil.getString(Bundle.APPLICATION, "label.semester.short");
        }
        final String executionYear = entry.getExecutionYear() == null ? "" : " " + entry.getExecutionYear().getQualifiedName();
        return curricularYear + curricularSemester + executionYear;
    }

    static private String getEntryCurriculumLinesInfo(final ICurriculumEntry entry) {
        final Set<CurriculumLine> lines = entry.getCurriculumLinesForCurriculum();

        if (lines.isEmpty()) {
            return "-";
        }

        if (lines.size() == 1 && lines.contains(entry)) {
            return Dismissal.class.isAssignableFrom(entry.getClass()) ? "Idem" : "-";
        }

        return lines.stream().map(line -> getEntryInfo((ICurriculumEntry) line)).collect(Collectors.joining(" <br/> "));
    }

    @Override
    public int compareTo(final AverageEntry o) {
        int result = 0;

        if (getCurricularYear() != null && o.getCurricularYear() != null) {
            result = getEntryInfo().compareTo(o.getEntryInfo());
        }

        if (result == 0) {
            result = getCurricularYear() != null ? -1 : 1;
        }

        if (result == 0) {
            result = getEntryInfo().compareTo(o.getEntryInfo());
        }

        if (result == 0) {
            result = CurriculumLayout.getPresentationNameFor(getEntry())
                    .compareTo(CurriculumLayout.getPresentationNameFor(o.getEntry()));
        }

        return result;
    }

    public String getApprovalTypeDescription() {
        String label = null;

        if (getApprovalType() == Enrolment.class) {
            label = "label.approvalType.Enrolment";
        }

        if (getApprovalType() == Substitution.class) {
            label = "label.approvalType.Substitution";
        }

        if (getApprovalType() == Equivalence.class) {
            label = "label.approvalType.Equivalence";
        }

        return Strings.isNullOrEmpty(label) ? "-" : ULisboaSpecificationsUtil.bundle(label);
    }

    static public List<AverageEntry> getAverageEntries(final Curriculum curriculum) {
        final List<AverageEntry> result = Lists.newLinkedList();

        curriculum.getEnrolmentRelatedEntries().stream().map(i -> new AverageEntry(Enrolment.class, i))
                .collect(Collectors.toCollection(() -> result));

        curriculum.getDismissalRelatedEntries().stream()
                .map(i -> new AverageEntry(i instanceof Dismissal ? Equivalence.class : Substitution.class, i))
                .collect(Collectors.toCollection(() -> result));

        Collections.sort(result);
        return result;
    }

}
