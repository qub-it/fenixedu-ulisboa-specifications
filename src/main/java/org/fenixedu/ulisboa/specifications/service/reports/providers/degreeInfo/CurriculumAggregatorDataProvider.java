package org.fenixedu.ulisboa.specifications.service.reports.providers.degreeInfo;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.RegistrationServices;
import org.fenixedu.academic.domain.student.curriculum.ICurriculum;
import org.fenixedu.academic.domain.student.curriculum.ICurriculumEntry;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.qubdocs.academic.documentRequests.providers.CurriculumEntry;
import org.fenixedu.qubdocs.academic.documentRequests.providers.CurriculumEntryRemarksDataProvider;
import org.fenixedu.qubdocs.util.CurriculumEntryServices;
import org.fenixedu.ulisboa.specifications.domain.studentCurriculum.CurriculumAggregatorServices;

import com.google.common.collect.Sets;
import com.qubit.terra.docs.util.IDocumentFieldsData;
import com.qubit.terra.docs.util.IReportDataProvider;

public class CurriculumAggregatorDataProvider implements IReportDataProvider {

    protected static final String KEY = "approvedDescendentEntries";
    protected static final String KEY_FOR_REMARKS = "approvementDescendentRemarks";
    protected static final String KEY_FOR_TOTAL_UNITS = "totalDescendentApprovements";
    protected static final String KEY_FOR_TOTAL_ECTS = "totalDescendentApprovedECTS";

    private final Registration registration;
    private final CurriculumEntryRemarksDataProvider remarksDataProvider;
    private final Locale locale;
    private Collection<ICurriculumEntry> descendentApprovements;
    private Set<CurriculumEntry> curriculumEntries;
    private final CurriculumEntryServices service;

    public CurriculumAggregatorDataProvider(final Registration registration, final Locale locale,
            final CurriculumEntryServices service) {
        this.registration = registration;
        this.locale = locale;
        this.remarksDataProvider = new CurriculumEntryRemarksDataProvider(registration);
        this.service = service;
        init();
    }

    @Override
    public void registerFieldsAndImages(final IDocumentFieldsData documentFieldsData) {
        documentFieldsData.registerCollectionAsField(KEY);
        documentFieldsData.registerCollectionAsField(KEY_FOR_REMARKS);
    }

    @Override
    public boolean handleKey(final String key) {
        if (descendentApprovements == null || descendentApprovements.isEmpty()) {
            return false;
        }
        return KEY.equals(key) || KEY_FOR_REMARKS.equals(key) || KEY_FOR_TOTAL_UNITS.equals(key)
                || KEY_FOR_TOTAL_ECTS.equals(key);
    }

    @Override
    public Object valueForKey(final String key) {
        if (key.equals(KEY)) {
            return getCurriculumEntries();
        } else if (key.equals(KEY_FOR_REMARKS)) {
            return getRemarks();
        } else if (key.equals(KEY_FOR_TOTAL_UNITS)) {
            return getTotalApprovements();
        } else if (key.equals(KEY_FOR_TOTAL_ECTS)) {
            return getApprovedEcts();
        } else {
            return null;
        }
    }

    private Set<CurriculumEntry> getCurriculumEntries() {
        return curriculumEntries;
    }

    private int getTotalApprovements() {
        if (curriculumEntries == null) {
            return 0;
        }
        return curriculumEntries.size();
    }

    private BigDecimal getApprovedEcts() {
        if (curriculumEntries == null) {
            return BigDecimal.ZERO;
        }
        return curriculumEntries.stream().map(CurriculumEntry::getEctsCredits).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Object getRemarks() {
        return remarksDataProvider.valueForKey("curriculumEntryRemarks");
    }

    protected void init() {
        final ICurriculum curriculum = RegistrationServices.getCurriculum(registration, null);

        descendentApprovements = curriculum.getCurricularYearEntries().stream().map(CurriculumLine.class::cast)
                .flatMap(line -> CurriculumAggregatorServices.getDescendentApprovedCurriculumLines(line).stream())
                .map(ICurriculumEntry.class::cast).collect(Collectors.toSet());

        curriculumEntries = Sets.newTreeSet(new Comparator<CurriculumEntry>() {

            @Override
            public int compare(final CurriculumEntry left, final CurriculumEntry right) {
                if (left.getExecutionYear() == right.getExecutionYear()) {
                    return compareByName(left, right);
                }
                return left.getExecutionYear().compareTo(right.getExecutionYear());
            }

            public int compareByName(final CurriculumEntry left, final CurriculumEntry right) {
                String leftContent = left.getName().getContent(locale) != null ? left.getName().getContent(locale) : left
                        .getName().getContent();
                String rightContent = right.getName().getContent(locale) != null ? right.getName().getContent(locale) : right
                        .getName().getContent();
                leftContent = leftContent.toLowerCase();
                rightContent = rightContent.toLowerCase();

                return leftContent.compareTo(rightContent);
            }
        });
        curriculumEntries.addAll(CurriculumEntry.transform(registration, descendentApprovements, remarksDataProvider, service));
    }

}
