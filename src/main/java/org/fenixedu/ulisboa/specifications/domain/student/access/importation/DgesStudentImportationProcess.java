/**
 *  Copyright © 2015 Universidade de Lisboa
 *  
 *  This file is part of FenixEdu fenixedu-ulisboa-specifications.
 *  
 *  FenixEdu fenixedu-ulisboa-specifications is free software: you can redistribute 
 *  it and/or modify it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  FenixEdu fenixedu-ulisboa-specifications is distributed in the hope that it will
 *  be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with FenixEdu fenixedu-ulisboa-specifications.
 *  If not, see <http://www.gnu.org/licenses/>.
 **/
package org.fenixedu.ulisboa.specifications.domain.student.access.importation;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.fenixedu.academic.domain.EntryPhase;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.QueueJob;
import org.fenixedu.academic.domain.QueueJobResult;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.spaces.domain.Space;

import pt.ist.fenixframework.core.WriteOnReadError;

public class DgesStudentImportationProcess extends DgesStudentImportationProcess_Base {

    protected DgesStudentImportationProcess() {
        super();
    }

    public DgesStudentImportationProcess(ExecutionYear executionYear, Space space, EntryPhase entryPhase,
            DgesStudentImportationFile dgesStudentImportationFile) {
        this();

        init(executionYear, space, entryPhase, dgesStudentImportationFile);
    }

    private void init(final ExecutionYear executionYear, final EntryPhase entryPhase) {
        String[] args = new String[0];
        if (executionYear == null) {
            throw new DomainException("error.DgesBaseProcess.execution.year.is.null", args);
        }
        String[] args1 = new String[0];
        if (entryPhase == null) {
            throw new DomainException("error.DgesBaseProcess.entry.phase.is.null", args1);
        }

        setExecutionYear(executionYear);
        setEntryPhase(entryPhase);
    }

    private void init(ExecutionYear executionYear, Space space, EntryPhase entryPhase,
            DgesStudentImportationFile dgesStudentImportationFile) {
        init(executionYear, entryPhase);

        String[] args = new String[0];

        if (space == null) {
            throw new DomainException("error.DgesStudentImportationProcess.campus.is.null", args);
        }
        String[] args1 = {};
        if (dgesStudentImportationFile == null) {
            throw new DomainException("error.DgesStudentImportationProcess.importation.file.is.null", args1);
        }

        setSpace(space);
        setDgesStudentImportationFile(dgesStudentImportationFile);
    }

    private transient PrintWriter LOG_WRITER = null;

    @Override
    public QueueJobResult execute() throws Exception {
        ByteArrayOutputStream stream = null;
        try {
            stream = new ByteArrayOutputStream();
            LOG_WRITER = new PrintWriter(new BufferedOutputStream(stream));

            importCandidates();
        } catch (WriteOnReadError e) {
            throw e;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t);
        }

        finally {
            LOG_WRITER.close();
            stream.close();
        }

        final QueueJobResult queueJobResult = new QueueJobResult();
        queueJobResult.setContentType("text/plain");

        queueJobResult.setContent(stream.toByteArray());

        stream.close();
        return queueJobResult;
    }

    public void importCandidates() {

        final DgesStudentImportService service = new DgesStudentImportService(getExecutionYear(), getSpace(), getEntryPhase());
        service.importStudents(getDgesStudentImportationFile().getContent());
    }

    public static List<DgesStudentImportationProcess> readDoneJobs(ExecutionYear executionYear) {
        List<DgesStudentImportationProcess> jobList = new ArrayList<DgesStudentImportationProcess>();
        CollectionUtils.select(executionYear.getDgesStudentImportationProcessSet(), new Predicate() {
            @Override
            public boolean evaluate(Object process) {
                return process instanceof DgesStudentImportationProcess && ((QueueJob) process).getDone();
            }
        }, jobList);
        return jobList;
    }

    public static List<DgesStudentImportationProcess> readUndoneJobs(ExecutionYear executionYear) {
        return new ArrayList(CollectionUtils.subtract(readAllJobs(executionYear), readDoneJobs(executionYear)));
    }

    public static List<DgesStudentImportationProcess> readAllJobs(ExecutionYear executionYear) {
        List<DgesStudentImportationProcess> jobList = new ArrayList<DgesStudentImportationProcess>();
        CollectionUtils.select(executionYear.getDgesStudentImportationProcessSet(), new Predicate() {
            @Override
            public boolean evaluate(Object arg0) {
                return arg0 instanceof DgesStudentImportationProcess;
            }
        }, jobList);
        return jobList;
    }

    public static boolean canRequestJob() {
        return QueueJob.getUndoneJobsForClass(DgesStudentImportationProcess.class).isEmpty();
    }

    @Override
    public String getFilename() {
        return "log.txt";
    }
}
