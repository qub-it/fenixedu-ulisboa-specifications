package org.fenixedu.ulisboa.specifications.ui.renderers.student.curriculum;

import pt.ist.fenixWebFramework.renderers.layouts.Layout;

public class StudentCurricularPlanRenderer
        extends org.fenixedu.academic.ui.renderers.student.curriculum.StudentCurricularPlanRenderer {

    public static enum EnrolmentStateFilterType {
        ALL, APPROVED, APPROVED_OR_ENROLED,

        // qubExtension
        ENROLED;

        public String getName() {
            return name();
        }

        public String getFullyQualifiedName() {
            return getClass().getName() + "." + name();
        }

        public static EnrolmentStateFilterType[] getValues() {
            return values();
        }

    }

    public static enum OrganizationType {
        GROUPS, EXECUTION_YEARS,

        // qubExtension
        CURRICULAR_YEARS;

        public String getName() {
            return name();
        }

        public String getFullyQualifiedName() {
            return getClass() + "." + name();
        }

        public static OrganizationType[] getValues() {
            return values();
        }

    }

    private OrganizationType organizedBy = OrganizationType.GROUPS;

    private EnrolmentStateFilterType enrolmentStateFilter = EnrolmentStateFilterType.ALL;

    public StudentCurricularPlanRenderer() {
        super();
    }

    @Override
    public void setOrganizedBy(String organizedBy) {
        this.organizedBy = OrganizationType.valueOf(organizedBy);
    }

    public void setOrganizedByEnum(final OrganizationType organizationType) {
        this.organizedBy = organizationType;
    }

    @Override
    public boolean isOrganizedByGroups() {
        return this.organizedBy == OrganizationType.GROUPS;
    }

    @Override
    public boolean isOrganizedByExecutionYears() {
        return this.organizedBy == OrganizationType.EXECUTION_YEARS;
    }

    @Override
    public void setEnrolmentStateFilter(final String type) {
        this.enrolmentStateFilter = EnrolmentStateFilterType.valueOf(type);
    }

    public void setEnrolmentStateFilterEnum(final EnrolmentStateFilterType enrolmentStateFilter) {
        this.enrolmentStateFilter = enrolmentStateFilter;
    }

    @Override
    public boolean isToShowAllEnrolmentStates() {
        return this.enrolmentStateFilter == EnrolmentStateFilterType.ALL;
    }

    @Override
    public boolean isToShowApprovedOnly() {
        return this.enrolmentStateFilter == EnrolmentStateFilterType.APPROVED;
    }

    @Override
    public boolean isToShowApprovedOrEnroledStatesOnly() {
        return this.enrolmentStateFilter == EnrolmentStateFilterType.APPROVED_OR_ENROLED;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new org.fenixedu.ulisboa.specifications.ui.renderers.student.curriculum.StudentCurricularPlanLayout(this);
    }

}
