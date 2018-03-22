package org.fenixedu.ulisboa.specifications.domain.studentCurriculum;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.fenixedu.academic.domain.organizationalStructure.AccountabilityTypeEnum;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.organizationalStructure.UnitUtils;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.Credits;
import org.fenixedu.academic.domain.studentCurriculum.Dismissal;
import org.fenixedu.academic.domain.studentCurriculum.ExternalEnrolment;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.commons.i18n.LocalizedString.Builder;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.services.CurriculumLineServices;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;

import com.google.common.collect.Lists;

import pt.ist.fenixframework.Atomic;

public class CreditsReasonType extends CreditsReasonType_Base {

    static final public String SEPARATOR = " ; ";

    public CreditsReasonType() {
        super();
        setRootDomainObject(Bennu.getInstance());
    }

    private static boolean equalInAnyLanguage(final LocalizedString object, final String value) {
        for (Locale locale : object.getLocales()) {
            if (object.getContent(locale).equals(value)) {
                return true;
            }
        }
        return false;
    }

    private static boolean equalInAnyLanguage(final LocalizedString object, final LocalizedString value) {
        for (Locale objectLocale : object.getLocales()) {
            for (Locale valueLocale : value.getLocales()) {
                if (object.getContent(objectLocale).equals(value.getContent(valueLocale))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Atomic
    static public CreditsReasonType create(final LocalizedString reason, final boolean active, final boolean averageEntry,
            final boolean infoHidden, final LocalizedString infoText, final boolean infoExplained,
            final boolean infoExplainedWithCountry, final boolean infoExplainedWithInstitution,
            final boolean infoExplainedWithEcts) {

        final CreditsReasonType result = new CreditsReasonType();
        result.init(reason, active, averageEntry, infoHidden, infoText, infoExplained, infoExplainedWithCountry,
                infoExplainedWithInstitution, infoExplainedWithEcts);

        return result;
    }

    @Atomic
    public CreditsReasonType edit(final LocalizedString reason, final boolean active, final boolean averageEntry,
            final boolean infoHidden, final LocalizedString infoText, final boolean infoExplained,
            final boolean infoExplainedWithCountry, final boolean infoExplainedWithInstitution,
            final boolean infoExplainedWithEcts) {

        init(reason, active, averageEntry, infoHidden, infoText, infoExplained, infoExplainedWithCountry,
                infoExplainedWithInstitution, infoExplainedWithEcts);

        return this;
    }

    private void init(final LocalizedString reason, final boolean active, final boolean averageEntry, final boolean infoHidden,
            final LocalizedString infoText, final boolean infoExplained, final boolean infoExplainedWithCountry,
            final boolean infoExplainedWithInstitution, final boolean infoExplainedWithEcts) {

        super.setReason(reason);
        super.setActive(active);
        super.setAverageEntry(averageEntry);
        super.setInfoHidden(infoHidden);
        super.setInfoText(infoText);
        super.setInfoExplained(infoExplained);
        super.setInfoExplainedWithCountry(infoExplainedWithCountry);
        super.setInfoExplainedWithInstitution(infoExplainedWithInstitution);
        super.setInfoExplainedWithEcts(infoExplainedWithEcts);

        checkRules();
    }

    private void checkRules() {

        for (final CreditsReasonType creditsReasonType : findAll()) {
            if (creditsReasonType != this && equalInAnyLanguage(creditsReasonType.getReason(), getReason())) {
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
        final Set<CreditsReasonType> result = new HashSet<>();

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

    public LocalizedString getInfo(final ICurriculumEntry entry, final Dismissal dismissal, final boolean hideDetails) {
        final Builder result = new LocalizedString.Builder();
        final Credits credits = dismissal.getCredits();

        if (isActive()) {

            // null forces hidden; empty forces fallback
            if (getInfoHidden()) {
                return null;
            }

            result.append(getInfoText());

            if (!hideDetails) {

                if (entry instanceof ExternalEnrolment) {
                    //substitution
                    addExternalEnrolmentInformation(result, (ExternalEnrolment) entry, false);
                }

                if (getInfoExplained()) {

                    final LocalizedString explanation = getExplanation(dismissal);
                    if (!explanation.isEmpty()) {
                        // TODO legidio, prefix for CreditsDismissal
                        final LocalizedString prefix = ULisboaSpecificationsUtil.bundleI18N(credits
                                .isSubstitution() ? "info.CreditsReasonType.explained.Substitution" : "info.CreditsReasonType.explained.Equivalence");
                        result.append(prefix, " - ");
                        result.append(explanation, ": ");
                    }
                }
            }

        }

        return result.build();
    }

    private LocalizedString getExplanation(final Dismissal dismissal) {
        final Builder result = new LocalizedString.Builder();
        final Credits credits = dismissal.getCredits();

        if (credits.isSubstitution()) {
            credits.getDismissalsSet().stream().sorted(CurriculumLineServices.COMPARATOR).forEach(i -> {

                result.append(i.getName(), ", ");
                addECTS(result, i);
            });

        } else {
            credits.getIEnrolments().stream().sorted(ICurriculumEntry.COMPARATOR_BY_EXECUTION_PERIOD_AND_NAME_AND_ID)
                    .forEach(i -> {

                        if (!(i instanceof ExternalEnrolment)) {
                            result.append(i.getName(), ", ");
                        } else {
                            addExternalEnrolmentInformation(result, (ExternalEnrolment) i, true);
                        }

                        addECTS(result, i);
                    });
        }

        return result.build();
    }

    private void addExternalEnrolmentInformation(final Builder result, final ExternalEnrolment external,
            final boolean includeName) {

        final List<Unit> unitFullPath = UnitUtils.getUnitFullPath(external.getExternalCurricularCourse().getUnit(),
                Lists.newArrayList(AccountabilityTypeEnum.GEOGRAPHIC, AccountabilityTypeEnum.ORGANIZATIONAL_STRUCTURE,
                        AccountabilityTypeEnum.ACADEMIC_STRUCTURE));

        Unit countryUnit = null;
        if (getInfoExplainedWithCountry()) {
            for (final Unit iter : unitFullPath) {
                if (iter.isCountryUnit()) {
                    countryUnit = iter;
                    break;
                }
            }

            if (countryUnit != null) {
                result.append(countryUnit.getNameI18n(), ", ");
            }
        }

        Unit institutionUnit = null;
        if (getInfoExplainedWithInstitution()) {
            institutionUnit = external.getAcademicUnit();
            if (institutionUnit != null) {
                result.append(institutionUnit.getNameI18n(), countryUnit != null ? " > " : ", ");
            }
        }

        if (includeName) {
            result.append(external.getName(), countryUnit != null || institutionUnit != null ? " > " : ", ");
        }
    }

    private void addECTS(final Builder result, final ICurriculumEntry entry) {
        final BigDecimal ects = entry.getEctsCreditsForCurriculum();
        if (getInfoExplainedWithEcts() && ects != null && ects.compareTo(BigDecimal.ZERO) > 0) {
            result.append(String.format(" [%s ECTS]", ects));
        }
    }

}
