package org.fenixedu.academicextensions.domain.exceptions;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;

public class AcademicExtensionsDomainException extends DomainException {

    private static final long serialVersionUID = 1L;

    public AcademicExtensionsDomainException(String key, String... args) {
        super(ULisboaSpecificationsUtil.BUNDLE, key, args);
    }

    public AcademicExtensionsDomainException(Status status, String key, String... args) {
        super(status, ULisboaSpecificationsUtil.BUNDLE, key, args);
    }

    public AcademicExtensionsDomainException(Throwable cause, String key, String... args) {
        super(cause, ULisboaSpecificationsUtil.BUNDLE, key, args);
    }

    public AcademicExtensionsDomainException(Throwable cause, Status status, String key, String... args) {
        super(cause, status, ULisboaSpecificationsUtil.BUNDLE, key, args);
    }

    public static void throwWhenDeleteBlocked(Collection<String> blockers) {
        if (!blockers.isEmpty()) {
            throw new AcademicExtensionsDomainException("key.return.argument", blockers.stream().collect(
                    Collectors.joining(", ")));
        }
    }

}
