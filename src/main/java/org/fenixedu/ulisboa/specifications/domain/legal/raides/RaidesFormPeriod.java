package org.fenixedu.ulisboa.specifications.domain.legal.raides;

import java.util.stream.Stream;

import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;

import pt.ist.fenixframework.Atomic;

public class RaidesFormPeriod extends RaidesFormPeriod_Base {
    
    protected RaidesFormPeriod() {
        super();
        setBennu(Bennu.getInstance());
    }
    
    protected RaidesFormPeriod(ExecutionYear executionYear) {
        this();
        
        setActive(true);
        setExecutionYear(executionYear);
        
        checkRules();
    }

    private void checkRules() {
        if(getBennu() == null) {
            throw new ULisboaSpecificationsDomainException("error.RaidesFormPeriod.required");
        }
        
        if(getExecutionYear() == null) {
            throw new ULisboaSpecificationsDomainException("error.RaidesFormPeriod.executionYear.required");
        }
        
        if(findActive().count() > 1) {
            throw new ULisboaSpecificationsDomainException("errorRaidesFormPeriod.only.one.active.required");
        }
    }

    @Atomic
    public void activate() {
        setActive(true);
        
        checkRules();
    }
    
    @Atomic
    public void deactivate() {
        setActive(false);
        
        checkRules();
    }

    public boolean isActive() {
        return getActive();
    }
    
    @Atomic
    public void delete() {
        setExecutionYear(null);
        setBennu(null);
        
        super.deleteDomainObject();
    }
    
    // @formatter:off
    /* ********
     * SERVICES
     * ********
     */
    // @formatter:on
    
    public static Stream<RaidesFormPeriod> findAll() {
        return Bennu.getInstance().getRaidesFormPeriodsSet().stream();
    }

    public static Stream<RaidesFormPeriod> findActive() {
        return findAll().filter(r -> r.isActive());
    }
    
    @Atomic
    public static RaidesFormPeriod create(final ExecutionYear executionYear) {
        return new RaidesFormPeriod(executionYear);
    }
    
}
