package org.fenixedu.ulisboa.specifications.domain.file;

import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;

import pt.ist.fenixframework.Atomic;

public class ULisboaSpecificationsTemporaryFile extends ULisboaSpecificationsTemporaryFile_Base {

    protected ULisboaSpecificationsTemporaryFile() {
        super();
        super.setULisboaSpecificationsRoot(ULisboaSpecificationsRoot.getInstance());
    }

    protected void init(final String filename, final byte[] content, final User user) {
        super.init(filename, filename, content);
        super.setULisboaTemporaryFileOwner(user);
        checkRules();
    }

    private void checkRules() {

        if (getFilename() == null) {
            throw new ULisboaSpecificationsDomainException("error.ULisboaSpecificationsTemporaryFile.filename.required");
        }

        if (getULisboaTemporaryFileOwner() == null) {
            throw new ULisboaSpecificationsDomainException(
                    "error.ULisboaSpecificationsTemporaryFile.uLisboaTemporaryFileOwner.required");
        }

        if (getULisboaTemporaryFileOwner().getULisboaTemporaryFilesSet().stream()
                .anyMatch(f -> f != this && f.getFilename().equals(getFilename()))) {
            throw new ULisboaSpecificationsDomainException(
                    "error.ULisboaSpecificationsTemporaryFile.duplicated.filename.for.user");
        }

    }

    @Atomic
    public void delete() {
        ULisboaSpecificationsDomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        super.setULisboaSpecificationsRoot(null);
        super.setULisboaTemporaryFileOwner(null);
        super.delete();
    }

    @Atomic
    public static ULisboaSpecificationsTemporaryFile create(final String filename, final byte[] content, final User user) {
        final ULisboaSpecificationsTemporaryFile result = new ULisboaSpecificationsTemporaryFile();
        result.init(filename, content, user);

        return result;
    }

    @Override
    public boolean isAccessible(User user) {
        return getULisboaTemporaryFileOwner() == user;
    }

    // @formatter: off
    /************
     * SERVICES *
     ************/
    // @formatter: on

    public static Optional<ULisboaSpecificationsTemporaryFile> findByUserAndFilename(final User user, final String filename) {
        return user.getULisboaTemporaryFilesSet().stream().filter(f -> f.getFilename().equals(filename)).findAny();
    }

    public static Stream<ULisboaSpecificationsTemporaryFile> findAll() {
        return ULisboaSpecificationsRoot.getInstance().getTemporaryFilesSet().stream();
    }

}
