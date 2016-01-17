package org.fenixedu.ulisboa.specifications.domain.legal.raides;

import java.util.Set;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;
import org.fenixedu.ulisboa.specifications.domain.legal.mapping.ILegalMappingType;
import org.fenixedu.ulisboa.specifications.domain.legal.raides.mapping.LegalMappingType;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReport;
import org.fenixedu.ulisboa.specifications.domain.legal.report.LegalReportRequest;

import com.google.common.collect.Sets;

import pt.ist.fenixframework.Atomic;

public class RaidesInstance extends RaidesInstance_Base {
    
    private RaidesInstance() {
        super();
    }

    @Override
    public Set<ILegalMappingType> getMappingTypes() {
        return Sets.<ILegalMappingType> newHashSet(LegalMappingType.values());
    }

    @Override
    public Set<?> getPossibleKeys(final String type) {
        return LegalMappingType.valueOf(type).getValues();
    }

    @Override
    public LocalizedString getMappingTypeNameI18N(final String type) {
        if (BranchMappingType.isTypeForMapping(type)) {
            return BranchMappingType.getInstance().getName();
        }

        return LegalMappingType.valueOf(type).getName();
    }

    @Override
    public LocalizedString getLocalizedNameMappingKey(final String type, final String key) {
        if (BranchMappingType.isTypeForMapping(type)) {
            return BranchMappingType.getInstance().getLocalizedNameKey(key);
        }

        return LegalMappingType.valueOf(type).getLocalizedNameKey(key);
    }

    @Override
    public LocalizedString getNameI18N() {
        return QubBundleUtil.getMultiLanguageStringFromResourceBundle(QubEduLegalPlugin.BUNDLE, PropertiesManager.getLocales(),
                "title." + RaidesInstance.class.getName());
    }

    public synchronized static RaidesInstance getInstance() {
        RaidesInstance instance = find(RaidesInstance.class);

        if (instance != null) {
            return instance;
        }

        return new RaidesInstance();
    }

    protected static <T extends LegalReport> T find(Class<T> reportClass) {
        for (final LegalReport report : Bennu.getInstance().getLegalReportsSet()) {
            if (reportClass == report.getClass()) {
                return (T) report;
            }
        }

        return null;
    }

    @Override
    public void executeProcessing(final LegalReportRequest reportRequest) {
        (new Raides()).process(this, reportRequest);
    }

    @Override
    @Atomic
    public void edit(LocalizedString name, PersistentGroup group, Boolean synchronous, Boolean hasMappings) {
        setName(name);
        setGroup(group);
        setSynchronous(synchronous);
        setHasMappings(hasMappings);
    }
    
    @Override
    @Atomic
    public void delete() {
        if(this.getLegalMappingsSet().size() > 0) {
            throw new ULisboaSpecificationsDomainException("error.report.delete.not.empty.mappings");
        } 
        if(this.getLegalRequestsSet().size() > 0) {
            throw new ULisboaSpecificationsDomainException("error.report.delete.not.empty.requests");
        } 
        super.setGroup(null);
        super.setBennu(null);
        super.deleteDomainObject();
    }
    
}
