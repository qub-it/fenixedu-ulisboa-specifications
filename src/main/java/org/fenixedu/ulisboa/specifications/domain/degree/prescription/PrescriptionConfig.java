package org.fenixedu.ulisboa.specifications.domain.degree.prescription;

import java.math.BigDecimal;
import java.util.Collection;

import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.student.StatuteType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;

import pt.ist.fenixframework.Atomic;

public class PrescriptionConfig extends PrescriptionConfig_Base {

    protected PrescriptionConfig() {
        super();
        super.setBennu(Bennu.getInstance());
    }

    protected void init(String name, BigDecimal partialRegimeBonus) {
        super.setName(name);
        super.setPartialRegimeBonus(partialRegimeBonus);

        checkRules();

    }

    @Atomic
    public void edit(String name, BigDecimal bonus) {
        super.setName(name);
        super.setPartialRegimeBonus(bonus);

        checkRules();
    }

    @Override
    public void removeDegreeCurricularPlans(DegreeCurricularPlan degreeCurricularPlans) {
        super.removeDegreeCurricularPlans(degreeCurricularPlans);

        checkRules();
    }

    @Override
    public void addDegreeCurricularPlans(DegreeCurricularPlan degreeCurricularPlans) {
        super.addDegreeCurricularPlans(degreeCurricularPlans);

        checkRules();
    }

    protected void checkRules() {

        if (getName() == null) {
            throw new ULisboaSpecificationsDomainException("error.PrescriptionConfig.name.cannot.be.null");
        }

        if (getPartialRegimeBonus() == null) {
            throw new ULisboaSpecificationsDomainException("error.PrescriptionConfig.partialRegimeBonus.cannot.be.null");
        }

        if (PrescriptionConfig.findAll().stream().anyMatch(pc -> pc != this && pc.getName().equals(getName()))) {
            throw new ULisboaSpecificationsDomainException(
                    "error.PrescriptionConfig.already.exists.other.configuration.with.same.name");
        }

        if (PrescriptionConfig.findAll().stream()
                .anyMatch(pc -> pc != this && getDegreeCurricularPlansSet().stream().anyMatch(dcp -> pc.appliesTo(dcp)))) {
            throw new ULisboaSpecificationsDomainException(
                    "error.PrescriptionConfig.degree.curricular.plan.can.only.belong.to.single.configuration");
        }

    }

    public BigDecimal getBonification(Collection<StatuteType> statuteTypes, boolean partialRegime) {
        return getBonificationStatutesSet().stream().filter(b -> statuteTypes.contains(b.getStatuteType())).map(b -> b.getBonus())
                .max((x, y) -> x.compareTo(y)).orElse(BigDecimal.ZERO)
                .max(partialRegime ? getPartialRegimeBonus() : BigDecimal.ZERO);
    }

    public boolean appliesTo(DegreeCurricularPlan degreeCurricularPlan) {
        return getDegreeCurricularPlansSet().contains(degreeCurricularPlan);
    }

    @Atomic
    public void delete() {

        while (!getBonificationStatutesSet().isEmpty()) {
            getBonificationStatutesSet().iterator().next().delete();
        }

        getDegreeCurricularPlansSet().clear();
        super.setBennu(null);
        super.deleteDomainObject();
    }

    @Atomic
    static public PrescriptionConfig create(String name, BigDecimal partialRegimeBonus) {
        final PrescriptionConfig result = new PrescriptionConfig();
        result.init(name, partialRegimeBonus);

        return result;
    }

    public static Collection<PrescriptionConfig> findAll() {
        return Bennu.getInstance().getPrescriptionConfigsSet();
    }

    public static PrescriptionConfig findBy(final DegreeCurricularPlan degreeCurricularPlan) {
        return findAll().stream().filter(pc -> pc.appliesTo(degreeCurricularPlan)).findFirst().orElse(null);
    }

}
