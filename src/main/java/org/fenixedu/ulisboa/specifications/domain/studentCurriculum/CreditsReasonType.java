package org.fenixedu.ulisboa.specifications.domain.studentCurriculum;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.fenixedu.academic.util.MultiLanguageString;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;

import pt.ist.fenixframework.Atomic;

public class CreditsReasonType extends CreditsReasonType_Base {

    public CreditsReasonType() {
        super();
        setRootDomainObject(Bennu.getInstance());
    }

    @Atomic
    static public CreditsReasonType create(final MultiLanguageString reason, final boolean active, final boolean averageEntry,
            final boolean infoHidden, final boolean infoExplained, final boolean infoExplainedWithCountry,
            final boolean infoExplainedWithInstitution, final boolean infoExplainedWithEcts) {

        final CreditsReasonType result = new CreditsReasonType();
        result.init(reason, active, averageEntry, infoHidden, infoExplained, infoExplainedWithCountry,
                infoExplainedWithInstitution, infoExplainedWithEcts);

        return result;
    }

    @Atomic
    public CreditsReasonType edit(final MultiLanguageString reason, final boolean active, final boolean averageEntry,
            final boolean infoHidden, final boolean infoExplained, final boolean infoExplainedWithCountry,
            final boolean infoExplainedWithInstitution, final boolean infoExplainedWithEcts) {

        init(reason, active, averageEntry, infoHidden, infoExplained, infoExplainedWithCountry, infoExplainedWithInstitution,
                infoExplainedWithEcts);

        return this;
    }

    private void init(final MultiLanguageString reason, final boolean active, final boolean averageEntry,
            final boolean infoHidden, final boolean infoExplained, final boolean infoExplainedWithCountry,
            final boolean infoExplainedWithInstitution, final boolean infoExplainedWithEcts) {

        super.setReason(reason);
        super.setActive(active);
        super.setAverageEntry(averageEntry);
        super.setInfoHidden(infoHidden);
        super.setInfoExplained(infoExplained);
        super.setInfoExplainedWithCountry(infoExplainedWithCountry);
        super.setInfoExplainedWithInstitution(infoExplainedWithInstitution);
        super.setInfoExplainedWithEcts(infoExplainedWithEcts);

        checkRules();
    }

    private void checkRules() {
        
        for (final CreditsReasonType creditsReasonType : findAll()) {
            if (creditsReasonType != this && creditsReasonType.getReason().equalInAnyLanguage(getReason())) {
                throw new ULisboaSpecificationsDomainException("error.CreditsReasonType.reason.must.be.unique");
            }
        }
        
        if (getReason() == null || getReason().isEmpty()) {
            throw new ULisboaSpecificationsDomainException("error.CreditsReasonType.required.Reason");
        }
    }

    public boolean isActive() {
        return super.getActive();
    }

    @Atomic
    public void delete() {
        if (!getCreditsSet().isEmpty()) {
            throw new ULisboaSpecificationsDomainException("error.CreditsReasonType.cannot.delete.because.already.has.credits.associated");
        }

        super.setRootDomainObject(null);
        super.deleteDomainObject();
    }

    static public Collection<CreditsReasonType> findActive() {
        final Set<CreditsReasonType> result = new HashSet<CreditsReasonType>();

        for (final CreditsReasonType reasonType : findAll()) {
            if (reasonType.isActive()) {
                result.add(reasonType);
            }
        }

        return result;
    }

    static public Collection<CreditsReasonType> findAll() {
        return Bennu.getInstance().getCreditsReasonTypesSet();
    }

}
