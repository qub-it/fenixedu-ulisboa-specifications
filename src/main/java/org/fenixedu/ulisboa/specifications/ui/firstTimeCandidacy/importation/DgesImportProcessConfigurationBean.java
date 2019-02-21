package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.importation;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.student.RegistrationProtocol;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.CgdMod43Template;
import org.fenixedu.ulisboa.specifications.domain.ContingentToIngression;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationConfiguration;
import org.fenixedu.ulisboa.specifications.domain.FirstYearRegistrationGlobalConfiguration;
import org.fenixedu.ulisboa.specifications.domain.ULisboaSpecificationsRoot;

public class DgesImportProcessConfigurationBean implements IBean {

    private RegistrationProtocol defaultRegistrationProtocol;
    private String cgdTemplateId;
    private String cgdTemplateName;
    private LocalizedString introductionText;
    private List<TupleDataSourceBean> registrationProtocolDataSource;
    private List<ContingentMappingBean> contingentMappings;
    private List<TupleDataSourceBean> ingressionTypeDataSource;
    private List<DegreeMappingBean> activeDegrees;
    private List<TupleDataSourceBean> activeDegreeDataSource;

    public DgesImportProcessConfigurationBean() {
        CgdMod43Template mod43Template = FirstYearRegistrationGlobalConfiguration.getInstance().getMod43Template();
        if (mod43Template != null) {
            setCgdTemplateName(mod43Template.getDisplayName());
            setCgdTemplateId(mod43Template.getExternalId());
        }

        setRegistrationProtocolDataSource(Bennu.getInstance().getRegistrationProtocolsSet());
        setIngressionTypeDataSource(Bennu.getInstance().getIngressionTypesSet());

        updateLists();
    }

    public RegistrationProtocol getDefaultRegistrationProtocol() {
        return defaultRegistrationProtocol;
    }

    public void setDefaultRegistrationProtocol(final RegistrationProtocol defaultRegistrationProtocol) {
        this.defaultRegistrationProtocol = defaultRegistrationProtocol;
    }

    public String getCgdTemplateId() {
        return cgdTemplateId;
    }

    public void setCgdTemplateId(final String cgdTemplateId) {
        this.cgdTemplateId = cgdTemplateId;
    }

    public String getCgdTemplateName() {
        return cgdTemplateName;
    }

    public void setCgdTemplateName(final String cgdTemplateName) {
        this.cgdTemplateName = cgdTemplateName;
    }

    public LocalizedString getIntroductionText() {
        return introductionText;
    }

    public void setIntroductionText(final LocalizedString introductionText) {
        this.introductionText = introductionText;
    }

    public List<TupleDataSourceBean> getRegistrationProtocolDataSource() {
        return registrationProtocolDataSource;
    }

    public void setRegistrationProtocolDataSource(final Collection<RegistrationProtocol> registrationProtocols) {
        this.registrationProtocolDataSource =
                registrationProtocols.stream().sorted(RegistrationProtocol.AGREEMENT_COMPARATOR).map(p -> {
                    TupleDataSourceBean tuple = new TupleDataSourceBean();
                    tuple.setId(p.getExternalId());
                    tuple.setText(p.getCode() + " - " + p.getDescription().getContent());
                    return tuple;
                }).collect(Collectors.toList());
    }

    public List<ContingentMappingBean> getContingentMappings() {
        return contingentMappings;
    }

    public void setContingentMappings(final Collection<ContingentToIngression> contingentMappings) {
        this.contingentMappings = contingentMappings.stream().map(m -> new ContingentMappingBean(m)).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getIngressionTypeDataSource() {
        return ingressionTypeDataSource;
    }

    public void setIngressionTypeDataSource(final Collection<IngressionType> ingressionTypes) {
        this.ingressionTypeDataSource = ingressionTypes.stream().map(i -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(i.getExternalId());
            tuple.setText(i.getCode() + " - " + i.getDescription().getContent());
            return tuple;
        }).collect(Collectors.toList());
    }

    public List<DegreeMappingBean> getActiveDegrees() {
        return activeDegrees;
    }

    public void setActiveDegrees(final List<FirstYearRegistrationConfiguration> activeDegrees) {
        this.activeDegrees = activeDegrees.stream().map(c -> new DegreeMappingBean(c)).collect(Collectors.toList());
    }

    public List<TupleDataSourceBean> getActiveDegreeDataSource() {
        return activeDegreeDataSource;
    }

    public void setActiveDegreeDataSource(final List<Degree> activeDegrees) {
        this.activeDegreeDataSource = activeDegrees.stream().map(d -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(d.getExternalId());
            tuple.setText("[" + d.getCode() + "] " + d.getPresentationName());
            return tuple;
        }).collect(Collectors.toList());
    }

    public void updateLists() {

        final Predicate<Degree> isDegreeActive =
                d -> d.getExecutionDegrees().stream().anyMatch(ed -> ed.getExecutionYear().isCurrent());

        setDefaultRegistrationProtocol(ULisboaSpecificationsRoot.getInstance().getDefaultRegistrationProtocol());
        setIntroductionText(FirstYearRegistrationGlobalConfiguration.getInstance().getIntroductionText());
        setContingentMappings(ULisboaSpecificationsRoot.getInstance().getContingentToIngressionsSet());
        setActiveDegrees(FirstYearRegistrationGlobalConfiguration.getInstance().getFirstYearRegistrationConfigurationsSet()
                .stream().collect(Collectors.toList()));
        final List<Degree> degreesWithConfig = getActiveDegrees().stream().map(b -> b.getDegree()).collect(Collectors.toList());
        setActiveDegreeDataSource(Bennu.getInstance().getDegreesSet().stream()
                .filter(d -> isDegreeActive.test(d) && !degreesWithConfig.contains(d)).collect(Collectors.toList()));
    }

}
