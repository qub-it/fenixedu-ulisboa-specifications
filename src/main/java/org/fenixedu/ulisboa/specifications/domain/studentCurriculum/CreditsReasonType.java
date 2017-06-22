package org.fenixedu.ulisboa.specifications.domain.studentCurriculum;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.fenixedu.academic.domain.studentCurriculum.Credits;
import org.fenixedu.academic.domain.studentCurriculum.ExternalEnrolment;
import org.fenixedu.academic.util.MultiLanguageString;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.commons.i18n.LocalizedString.Builder;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;

import pt.ist.fenixframework.Atomic;

public class CreditsReasonType extends CreditsReasonType_Base {

    static final public String SEPARATOR = " ; ";

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
            throw new ULisboaSpecificationsDomainException(
                    "error.CreditsReasonType.cannot.delete.because.already.has.credits.associated");
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

    public LocalizedString getInfo(final Credits credits) {
        final Builder result = new LocalizedString.Builder();

        if (isActive()) {

            // null forces hidden; empty forces fallback
            if (getInfoHidden()) {
                return null;
            }

            result.append(getReason().toLocalizedString());

            if (getInfoExplained()) {

                final LocalizedString explanation = getExplanation(credits);
                if (!explanation.isEmpty()) {
                    final LocalizedString prefix = ULisboaSpecificationsUtil.bundleI18N(credits
                            .isSubstitution() ? "info.CreditsReasonType.explained.Substitution" : "info.CreditsReasonType.explained.Equivalence");
                    result.append(prefix, ", ");
                    result.append(explanation, ": ");
                }
            }

            /* TODO legidio
            boolean infoExplainedWithCountry;
            boolean infoExplainedWithInstitution;
            boolean infoExplainedWithEcts;
            */
        }

        return result.build();
    }

    private LocalizedString getExplanation(final Credits credits) {
        final Builder explanationJustification = new LocalizedString.Builder();
        if (credits.isSubstitution()) {
            credits.getDismissalsSet().stream()
                    .forEach(i -> explanationJustification.append(i.getName().toLocalizedString(), ", "));
        } else {
            credits.getIEnrolments().stream().forEach(i -> {

                if (i instanceof ExternalEnrolment) {
                    final ExternalEnrolment externalEnrolment = (ExternalEnrolment) i;
                    if (externalEnrolment.getAcademicUnit() != null) {

                        if (getInfoExplainedWithCountry()) {
                            explanationJustification.append(i.getName().toLocalizedString(), " ");
                        }
                    }
                }
                explanationJustification.append(i.getName().toLocalizedString(), ", ");

            });
        }
        if (!explanationJustification.build().isEmpty()) {
            result.append(explanationJustification.build(), ": ");
        }

        // TODO Auto-generated method stub
        return null;
    }

}
