package org.fenixedu.ulisboa.specifications.domain.student.importation;

import org.fenixedu.academic.domain.EntryPhase;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.spaces.domain.Space;

import pt.ist.fenixframework.Atomic;

public class DgesStudentImportationFile extends DgesStudentImportationFile_Base {
    private DgesStudentImportationFile() {
        super();
    }

    protected DgesStudentImportationFile(byte[] contents, String filename) {
        this();
        init(filename, filename, contents);
    }

    @Atomic
    public static DgesStudentImportationFile create(byte[] contents, String filename, ExecutionYear executionYear, Space space,
            EntryPhase entryPhase) {
        if (executionYear == null) {
            throw new DomainException("error.DgesStudentImportationFile.execution.year.is.null");
        }

        if (space == null) {
            throw new DomainException("error.error.DgesStudentImportationFile.campus.is.null");
        }

        if (entryPhase == null) {
            throw new DomainException("error.error.DgesStudentImportationFile.entry.phase.is.null");
        }

        return new DgesStudentImportationFile(contents, filename);
    }
}
