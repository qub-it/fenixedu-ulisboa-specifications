package org.fenixedu.ulisboa.specifications.domain.evaluation.config;

import org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot;

import pt.ist.fenixframework.Atomic;

public class MarkSheetSettings extends MarkSheetSettings_Base {

    protected MarkSheetSettings() {
        super();
    }

    public static void init() {
        if (getInstance() == null) {
            makeInstance();
        }
    }

    public static MarkSheetSettings getInstance() {
        return ULisboaSpecificationsRoot.getInstance().getMarkSheetSettings();
    }

    @Atomic
    private static void makeInstance() {
        ULisboaSpecificationsRoot.getInstance().setMarkSheetSettings(new MarkSheetSettings());
    }

    @Atomic
    public void edit(final boolean allowTeacherToChooseCertifier, final boolean requiresExactlyOneShift,
            final boolean limitCertifierToResponsibleTeacher, final boolean limitCreationToResponsibleTeacher) {
        super.setAllowTeacherToChooseCertifier(allowTeacherToChooseCertifier);
        super.setRequiresExactlyOneShift(requiresExactlyOneShift);
        super.setLimitCertifierToResponsibleTeacher(limitCertifierToResponsibleTeacher);
        super.setLimitCreationToResponsibleTeacher(limitCreationToResponsibleTeacher);
    }

    @Atomic
    public void editTemplateFile(final String filename, final byte[] content) {

        if (getTemplateFile() != null) {
            getTemplateFile().delete();
        }

        CompetenceCourseMarkSheetTemplateFile.create(filename, content, this);

    }

}
