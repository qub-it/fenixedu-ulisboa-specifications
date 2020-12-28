package org.fenixedu.ulisboa.specifications.domain.serviceRequests;

import java.util.Comparator;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.accessControl.AcademicAuthorizationGroup;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.academic.domain.groups.PermissionService;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academic.service.AcademicPermissionService;
import org.fenixedu.bennu.core.domain.User;

import pt.ist.fenixframework.Atomic;

public class ULisboaServiceRequestGeneratedDocument extends ULisboaServiceRequestGeneratedDocument_Base {

    public static final Comparator<ULisboaServiceRequestGeneratedDocument> COMPARATOR_BY_UPLOAD_TIME =
            new Comparator<ULisboaServiceRequestGeneratedDocument>() {
                @Override
                public int compare(ULisboaServiceRequestGeneratedDocument o1, ULisboaServiceRequestGeneratedDocument o2) {
                    return o1.getCreationDate().compareTo(o2.getCreationDate());
                }
            };

    protected ULisboaServiceRequestGeneratedDocument(ULisboaServiceRequest serviceRequest, Person requester, Person operator,
            String contentType, String filename, byte[] content) {
        super();
        setServiceRequest(serviceRequest);
        setRequester(requester);
        setOperator(operator);
        init(filename, filename, content);
        setContentType(contentType);
    }

    @Override
    public boolean isAccessible(User user) {
        if (user == null || user.getPerson() == null) {
            return false;
        }
        return user.getPerson().equals(getOperator()) || user.getPerson().equals(getOperator())
                || AcademicAuthorizationGroup.get(AcademicOperationType.SERVICE_REQUESTS).isMember(user)
                || AcademicPermissionService.hasAccess("ACADEMIC_REQUISITIONS", user);
    }

    @Override
    public void delete() {
        setServiceRequest(null);
        setRequester(null);
        setOperator(null);
        super.delete();
    }

    @Atomic
    public static ULisboaServiceRequestGeneratedDocument store(ULisboaServiceRequest serviceRequest, String contentType,
            String filename, byte[] content) {
        return new ULisboaServiceRequestGeneratedDocument(serviceRequest, serviceRequest.getPerson(), AccessControl.getPerson(),
                contentType, filename, content);
    }

}
