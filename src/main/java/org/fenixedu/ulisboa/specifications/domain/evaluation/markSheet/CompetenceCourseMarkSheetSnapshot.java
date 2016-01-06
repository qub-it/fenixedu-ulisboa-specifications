package org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.DomainObjectUtil;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.util.FenixDigestUtils;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.joda.time.LocalDate;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public class CompetenceCourseMarkSheetSnapshot extends CompetenceCourseMarkSheetSnapshot_Base {

    protected CompetenceCourseMarkSheetSnapshot() {
        super();
    }

    protected void init(final CompetenceCourseMarkSheetStateChange stateChange, final String competenceCourseCode,
            final LocalizedString competenceCourseName, final String executionSemester, final LocalizedString evaluationSeason,
            final String certifier, final LocalDate evaluationDate) {

        setStateChange(stateChange);
        setCompetenceCourseCode(competenceCourseCode);
        setCompetenceCourseName(competenceCourseName);
        setExecutionSemester(executionSemester);
        setEvaluationSeason(evaluationSeason);
        setCertifier(certifier);
        setEvaluationDate(evaluationDate);
        checkRules();
    }

    private void checkRules() {

        if (getStateChange().getState() != CompetenceCourseMarkSheetStateEnum.findSubmited()) {
            throw new ULisboaSpecificationsDomainException(
                    "error.CompetenceCourseMarkSheetSnapshot.stateChange.must.be.of.type.submited");
        }

        if (getStateChange() == null) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheetSnapshot.stateChange.required");
        }

        if (getCompetenceCourseName() == null || getCompetenceCourseName().isEmpty()) {
            throw new ULisboaSpecificationsDomainException(
                    "error.CompetenceCourseMarkSheetSnapshot.competenceCourseName.required");
        }

        if (StringUtils.isEmpty(getExecutionSemester())) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheetSnapshot.executionSemester.required");
        }

        if (getEvaluationSeason() == null || getEvaluationSeason().isEmpty()) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheetSnapshot.evaluationSeason.required");
        }

        if (StringUtils.isEmpty(getCertifier())) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheetSnapshot.certifier.required");
        }

        if (getEvaluationDate() == null) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheetSnapshot.evaluationDate.required");
        }

    }

    @Atomic
    public void delete() {
        ULisboaSpecificationsDomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        final Iterator<CompetenceCourseMarkSheetSnapshotEntry> iterator = getEntrySet().iterator();
        while (iterator.hasNext()) {
            final CompetenceCourseMarkSheetSnapshotEntry entry = iterator.next();
            iterator.remove();
            entry.delete();
        }

        super.setStateChange(null);
        deleteDomainObject();
    }

    public void addEntry(final Integer studentNumber, final String studentName, final Grade grade, final String degreeCode,
            final LocalizedString degreeName, final String shifts) {
        getEntrySet().add(CompetenceCourseMarkSheetSnapshotEntry.create(this, studentNumber, studentName, grade, degreeCode,
                degreeName, shifts));
    }

    public void finalize() {
        checkRules();

        if (getEntrySet().isEmpty()) {
            throw new ULisboaSpecificationsDomainException("error.CompetenceCourseMarkSheetSnapshot.entry.required");
        }

        updateCheckSum();
    }

    private void updateCheckSum() {

        final StringBuilder content = new StringBuilder();
        content.append(getCompetenceCourseName().toString());
        content.append(getExecutionSemester());
        content.append(getEvaluationSeason().toString());
        content.append(getCertifier());
        content.append(getEvaluationDate().toString("yyyy/MM/dd"));
        content.append(getStateChange().getDate().toString("yyyy/MM/dd"));

        for (final CompetenceCourseMarkSheetSnapshotEntry entry : getSortedEntries()) {
            content.append(entry.getStudentNumber().toString());
            content.append(entry.getStudentName());
            content.append(entry.getGrade().getValue());
        }

        setCheckSum(FenixDigestUtils.createDigest(content.toString()));

    }

    public SortedSet<CompetenceCourseMarkSheetSnapshotEntry> getSortedEntries() {

        final Comparator<CompetenceCourseMarkSheetSnapshotEntry> byStudentName =
                (x, y) -> x.getStudentName().compareTo(y.getStudentName());

        final SortedSet<CompetenceCourseMarkSheetSnapshotEntry> result =
                Sets.newTreeSet(byStudentName.thenComparing(DomainObjectUtil.COMPARATOR_BY_ID));

        result.addAll(getEntrySet());

        return result;

    }

    public static CompetenceCourseMarkSheetSnapshot create(final CompetenceCourseMarkSheetStateChange stateChange,
            final String competenceCourseCode, final LocalizedString competenceCourseName, final String executionSemester,
            final LocalizedString evaluationSeason, final String certifier, final LocalDate evaluationDate) {

        final CompetenceCourseMarkSheetSnapshot result = new CompetenceCourseMarkSheetSnapshot();
        result.init(stateChange, competenceCourseCode, competenceCourseName, executionSemester, evaluationSeason, certifier,
                evaluationDate);

        return result;
    }

    public String getFormattedCheckSum() {
        
        if (StringUtils.isEmpty(getCheckSum())) {
            return null;
        }

        final StringBuilder result = new StringBuilder();
        int counter = 0;
        for (final Character c : getCheckSum().toCharArray()) {
            result.append(c);
            if (++counter % 2 == 0) {
                result.append(" ");
            }
        }

        if (result.toString().endsWith(" ")) {
            result.delete(result.length() - 1, result.length());
        }

        return result.toString().toUpperCase();
    }
    
    public boolean isLastSnapshot() {
        return getStateChange().getCompetenceCourseMarkSheet().getLastSnapshot().get() == this;
        
    }

}
