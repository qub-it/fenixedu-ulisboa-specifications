package org.fenixedu.ulisboa.specifications.domain;

import java.util.Collection;

import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public class CourseGroupDegreeInfo extends CourseGroupDegreeInfo_Base {

    public static void setupDeleteListener() {
        FenixFramework.getDomainModel().registerDeletionListener(CourseGroup.class, courseGroup -> {
            for (CourseGroupDegreeInfo courseGroupDegreeInfo : courseGroup.getCourseGroupDegreeInfosSet()) {
                courseGroupDegreeInfo.delete();
            }
        });
    }

    protected CourseGroupDegreeInfo() {
        super();
        setBennu(Bennu.getInstance());
    }

    protected CourseGroupDegreeInfo(final LocalizedString name, final ExtendedDegreeInfo extendedDegreeInfo,
            final CourseGroup courseGroup) {
        this();
        setDegreeName(name);
        setExtendedDegreeInfo(extendedDegreeInfo);
        setCourseGroup(courseGroup);

        checkRules();
    }

    private void checkRules() {
        if (getDegreeName() == null || getDegreeName().isEmpty()) {
            throw new ULisboaSpecificationsDomainException("error.CourseGroupDegreeInfo.degreeName.null");
        }

        if (getCourseGroup() == null) {
            throw new ULisboaSpecificationsDomainException("error.CourseGroupDegreeInfo.courseGroup.null");
        }
        int found = 0;
        for (CourseGroupDegreeInfo info : getExtendedDegreeInfo().getCourseGroupDegreeInfosSet()) {
            if (info.getCourseGroup() == getCourseGroup()) {
                found++;
            }
        }

        if (found > 1) {
            throw new ULisboaSpecificationsDomainException("error.CourseGroupDegreeInfo.already.exists.for.value",
                    getExtendedDegreeInfo().getDegreeInfo().getName().getContent(), getCourseGroup().getName());
        }
    }

    @Atomic
    public void delete() {
        setBennu(null);
        setCourseGroup(null);
        setExtendedDegreeInfo(null);

        deleteDomainObject();
    }

    @Atomic
    public void edit(final LocalizedString name, final CourseGroup courseGroup) {
        setDegreeName(name);
        setCourseGroup(courseGroup);

        checkRules();
    }

    public static Collection<CourseGroupDegreeInfo> findAll() {
        return Bennu.getInstance().getCourseGroupDegreeInfosSet();
    }

    public static CourseGroupDegreeInfo create(final LocalizedString name, final ExtendedDegreeInfo extendedDegreeInfo,
            final CourseGroup courseGroup) {
        return new CourseGroupDegreeInfo(name, extendedDegreeInfo, courseGroup);
    }

}
