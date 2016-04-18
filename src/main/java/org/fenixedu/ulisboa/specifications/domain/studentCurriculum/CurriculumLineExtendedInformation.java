package org.fenixedu.ulisboa.specifications.domain.studentCurriculum;

import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;

import pt.ist.fenixframework.FenixFramework;

public class CurriculumLineExtendedInformation extends CurriculumLineExtendedInformation_Base {

    protected CurriculumLineExtendedInformation() {
        super();
        super.setBennu(Bennu.getInstance());
    }

    protected void init(final CurriculumLine curriculumLine) {
        super.setCurriculumLine(curriculumLine);
        checkRules();

    }

    private void checkRules() {
        if (getCurriculumLine() == null) {
            throw new ULisboaSpecificationsDomainException(
                    "error.CurriculumLineExtendedInformation.curriculumLine.cannot.be.null");
        }
    }

    static public void setupDeleteListener() {
        FenixFramework.getDomainModel().registerDeletionListener(CurriculumLine.class, line ->
        {
            if (line.getExtendedInformation() != null) {
                line.getExtendedInformation().delete();
            }
        });
    }

    private void delete() {

        super.setCurriculumLine(null);
        super.setBennu(null);

        super.deleteDomainObject();

    }

    static public CurriculumLineExtendedInformation findOrCreate(CurriculumLine curriculumLine) {
        return curriculumLine.getExtendedInformation() != null ? curriculumLine.getExtendedInformation() : create(curriculumLine);
    }

    static public CurriculumLineExtendedInformation create(CurriculumLine curriculumLine) {
        final CurriculumLineExtendedInformation result = new CurriculumLineExtendedInformation();
        result.init(curriculumLine);

        return result;
    }

}
