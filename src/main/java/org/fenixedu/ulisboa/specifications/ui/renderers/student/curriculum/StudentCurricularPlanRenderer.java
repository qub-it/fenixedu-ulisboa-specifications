package org.fenixedu.ulisboa.specifications.ui.renderers.student.curriculum;

import pt.ist.fenixWebFramework.renderers.layouts.Layout;

public class StudentCurricularPlanRenderer
        extends org.fenixedu.academic.ui.renderers.student.curriculum.StudentCurricularPlanRenderer {

    public static enum EnrolmentStateFilterType {

        // qubExtension
        ENROLED,

        APPROVED,
        
        APPROVED_OR_ENROLED,

        ALL,

        ;

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

    public static enum ViewType {

        DISMISSALS,

        ENROLMENTS,

        ALL

        ;

        public String getName() {
            return name();
        }

        public String getFullyQualifiedName() {
            return getClass() + "." + name();
        }

        public static ViewType[] getValues() {
            return values();
        }

    }

    public static enum OrganizationType {

        EXECUTION_YEARS,

        // qubExtension
        CURRICULAR_YEARS,

        GROUPS

        ;

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

    // qubExtension
    public static enum DetailedType {

        FALSE,

        TRUE,

        CURRENT

        ;

        public String getName() {
            return name();
        }

        public String getFullyQualifiedName() {
            return getClass() + "." + name();
        }

        public static DetailedType[] getValues() {
            return values();
        }

    }

    private OrganizationType organizedBy = OrganizationType.GROUPS;

    private EnrolmentStateFilterType enrolmentStateFilter = EnrolmentStateFilterType.ALL;

    private String detailedType = null;

    public StudentCurricularPlanRenderer() {
        super();
        setDetailedType(DetailedType.CURRENT.toString());
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

    public String getDetailedType() {
        return detailedType;
    }

    public void setDetailedType(final String input) {
        this.detailedType = input;
    }

    @Override
    protected Layout getLayout(Object object, Class type) {
        return new org.fenixedu.ulisboa.specifications.ui.renderers.student.curriculum.StudentCurricularPlanLayout(this);
    }

}
