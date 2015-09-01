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

import org.fenixedu.academic.domain.EntryPhase;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;
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

    @Override
    public boolean isAccessible(User user) {
        return Group.parse("academic(scope=ADMINISTRATION) | academic(scope=OFFICE) | #managers").isMember(user);
    }
}
