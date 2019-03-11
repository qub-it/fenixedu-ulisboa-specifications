package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.personalinfo;

import static org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE;
import static org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController.FIRST_TIME_START_URL;

import java.util.Set;

import org.fenixedu.academic.domain.Country;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.candidacy.StudentCandidacy;
import org.fenixedu.academic.domain.contacts.PartyContactType;
import org.fenixedu.academic.domain.contacts.PhysicalAddress;
import org.fenixedu.academic.domain.contacts.PhysicalAddressData;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.FenixeduUlisboaSpecificationsSpringConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.ulisboa.specifications.domain.Parish;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.FirstTimeCandidacyController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.CandidancyForm;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.FormAbstractController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.contacts.ContactsFormController;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.forms.filiation.FiliationFormController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Sets;

import org.apache.commons.lang.StringUtils;

@RequestMapping(FiscalInformationFormController.CONTROLLER_URL)
public class FiscalInformationFormController extends FormAbstractController {

    public static final String CONTROLLER_URL = FIRST_TIME_START_URL + "/{executionYearId}/fiscalinformationform";
    
    
    @Override
    protected String getControllerURL() {
        return CONTROLLER_URL;
    }

    @Override
    protected String getFormVariableName() {
        return "fiscalInformationForm";
    }
    
    @Override
    protected String fillGetScreen(ExecutionYear executionYear, Model model, RedirectAttributes redirectAttributes) {
        
        FiscalInformationForm form = fillFormIfRequired(executionYear, model);
        
        if(getForm(model) == null) {
            setForm(form, model);
        }
        
        addInfoMessage(BundleUtil.getString(BUNDLE, "label.firstTimeCandidacy.fillFiscalInformation.info"), model);

        return "fenixedu-ulisboa-specifications/firsttimecandidacy/angular/personalinformationform/fillfiscalinformation";
    }

    private FiscalInformationForm fillFormIfRequired(final ExecutionYear executionYear, final Model model) {
        FiscalInformationForm form = (FiscalInformationForm) getForm(model);
        
        if(form == null) {
            form = createFiscalInformationForm(getStudent(model), executionYear, false);
            
            setForm(form, model);
        }
        
        return form;
    }

    private FiscalInformationForm createFiscalInformationForm(final Student student, final ExecutionYear executionYear, final boolean create) {
        StudentCandidacy candidacy =  FirstTimeCandidacyController.getCandidacy();
        Person person = candidacy.getPerson();
        
        FiscalInformationForm form = new FiscalInformationForm();

        form.setSocialSecurityNumber(person.getSocialSecurityNumber());
        form.setFiscalAddress(person.getFiscalAddress());
     
        form.setAssociateExistingPhysicalAddresses(!person.getValidAddressesForFiscalData().isEmpty());
        
        return form;
        
    }

    @Override
    protected void fillPostScreen(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model,
            final RedirectAttributes redirectAttributes) {
        //nothing
    }
    
    @Override
    protected boolean validate(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model) {
        
        if (!(candidancyForm instanceof FiscalInformationForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.FiscalInformationForm.wrong.form.type"), model);
        }
        
        
        final FiscalInformationForm fiscalInfoForm = (FiscalInformationForm) candidancyForm;
        
        final Set<String> result = Sets.newLinkedHashSet();
        
        if(StringUtils.isEmpty(fiscalInfoForm.getSocialSecurityNumber())) {
            result.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                    "error.candidacy.workflow.FiscalInformationForm.socialSecurityNumber.required"));
        }
        
        if(fiscalInfoForm.isAssociateExistingPhysicalAddresses() && fiscalInfoForm.getFiscalAddress() == null) {
            result.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                    "error.candidacy.workflow.FiscalInformationForm.fiscalAddress.required"));
        } else if(!fiscalInfoForm.isAssociateExistingPhysicalAddresses()) {
            
            if(fiscalInfoForm.getCountryOfResidence() == null) {
                result.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, 
                        "error.candidacy.workflow.FiscalInformationForm.countryOfResidence.required"));
             }
             
            if(StringUtils.isEmpty(fiscalInfoForm.getAddress())) {
                result.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                        "error.candidacy.workflow.FiscalInformationForm.address.required"));
            }
            
            if(fiscalInfoForm.getCountryOfResidence() != null && fiscalInfoForm.getCountryOfResidence().isDefaultCountry()) {
                
                if (fiscalInfoForm.getCountryOfResidence().isDefaultCountry() && !fiscalInfoForm.isResidenceInformationFilled()) {
                    result.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE,
                            "error.candidacy.workflow.ResidenceInformationForm.address.national.students.should.supply.complete.address.information"));
                }
            }
        } else if(fiscalInfoForm.getCountryOfResidence() != null && !fiscalInfoForm.getCountryOfResidence().isDefaultCountry()) {
            
            if (fiscalInfoForm.getCountryOfResidence().isDefaultCountry() && StringUtils.isEmpty(fiscalInfoForm.getAreaCode())) {
                result.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.incorrect.areaCode"));
            }
            
            if(!fiscalInfoForm.getCountryOfResidence().isDefaultCountry() && StringUtils.isEmpty(fiscalInfoForm.getDistrictSubdivisionOfResidenceName())) {
                result.add(BundleUtil.getString(FenixeduUlisboaSpecificationsSpringConfiguration.BUNDLE, "error.candidacy.workflow.FiscalInformationForm.districtSubdivisionOfResidence.city.required"));
            }
            
        }
        
        return result.isEmpty();
    }
    
    
    @Override
    protected void writeData(final ExecutionYear executionYear, final CandidancyForm candidancyForm, final Model model) {
        final Student student = getStudent(model);
        final Person person = student.getPerson();
        
        
        if (!(candidancyForm instanceof FiscalInformationForm)) {
            addErrorMessage(BundleUtil.getString(BUNDLE, "error.FiscalInformationForm.wrong.form.type"), model);
            return;
        }
        
        
        final FiscalInformationForm fiscalInfoForm = (FiscalInformationForm) candidancyForm;
        
        final boolean updateSocialSecurityNumber = fiscalInfoForm.getSocialSecurityNumber().equals(person.getSocialSecurityNumber());
        final boolean updateFiscalAddress = isUpdateFiscalAddress(person, fiscalInfoForm);
        
        if(updateSocialSecurityNumber || updateFiscalAddress) {

            PhysicalAddress fiscalAddress;
            if(fiscalInfoForm.isAssociateExistingPhysicalAddresses()) {
                fiscalAddress = fiscalInfoForm.getFiscalAddress();
            } else {
                // Create fiscal address
                
                String district = fiscalInfoForm.getDistrictSubdivisionOfResidence() != null ? fiscalInfoForm.getDistrictSubdivisionOfResidence()
                        .getDistrict().getName() : null;
                String subdivision =
                        fiscalInfoForm.getDistrictSubdivisionOfResidence() != null ? fiscalInfoForm.getDistrictSubdivisionOfResidence().getName() : null;
                
                if(!fiscalInfoForm.getCountryOfResidence().isDefaultCountry()) {
                    subdivision = fiscalInfoForm.getDistrictSubdivisionOfResidenceName();
                }

                String areaCode = "";
                String areaOfAreaCode = "";
                if (fiscalInfoForm.getAreaCode() != null) {
                    areaCode = fiscalInfoForm.getAreaCode().substring(0, 8);
                    areaOfAreaCode = fiscalInfoForm.getAreaCode().substring(9);
                }
                Parish parishOfResidence = fiscalInfoForm.getParishOfResidence();
                
                PhysicalAddressData physicalAddressData = new PhysicalAddressData(fiscalInfoForm.getAddress(), areaCode, areaOfAreaCode, fiscalInfoForm.getArea(),
                        parishOfResidence != null ? parishOfResidence.getName() : "", subdivision, district,
                                fiscalInfoForm.getCountryOfResidence());
                
                fiscalAddress = PhysicalAddress.createPhysicalAddress(person, physicalAddressData, PartyContactType.PERSONAL, true);
                
            }
            
            person.editSocialSecurityNumber(fiscalInfoForm.getSocialSecurityNumber(), fiscalAddress);
        }
        
    }

    
    private boolean isUpdateFiscalAddress(final Person person, final FiscalInformationForm form) {
        if(person.getFiscalAddress() == null) {
            return true;
        }
        
        if(form.isAssociateExistingPhysicalAddresses()) {
            return person.getFiscalAddress() != form.getFiscalAddress();
        }
        
        final PhysicalAddress fiscalAddress = person.getFiscalAddress();
        
        // Fiscal address is composed in various fields
        
        String district = form.getDistrictSubdivisionOfResidence() != null ? form.getDistrictSubdivisionOfResidence()
                .getDistrict().getName() : null;
        String subdivision =
                form.getDistrictSubdivisionOfResidence() != null ? form.getDistrictSubdivisionOfResidence().getName() : null;
        
        if(!form.getCountryOfResidence().isDefaultCountry()) {
            subdivision = form.getDistrictSubdivisionOfResidenceName();
        }
                
        boolean addressIsEqual = StringUtils.equals(form.getAddress(), fiscalAddress.getAddress());

        addressIsEqual &= form.getCountryOfResidence() != fiscalAddress.getCountryOfResidence();
        addressIsEqual &= StringUtils.equals(form.getAreaCode(), fiscalAddress.getAreaCode());
        addressIsEqual &= StringUtils.equals(form.getArea(), fiscalAddress.getArea());
        addressIsEqual &= StringUtils.equals(form.getParishOfResidence().getName(), fiscalAddress.getParishOfResidence());
        addressIsEqual &= StringUtils.equals(subdivision, fiscalAddress.getDistrictSubdivisionOfResidence());
        
        if(form.getCountryOfResidence().isDefaultCountry()) {
            addressIsEqual &= StringUtils.equals(district, fiscalAddress.getDistrictOfResidence());
        }
        
        return addressIsEqual;
    }

    @Override
    protected String backScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(FiliationFormController.CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    @Override
    protected String nextScreen(final ExecutionYear executionYear, final Model model,
            final RedirectAttributes redirectAttributes) {
        return redirect(urlWithExecutionYear(ContactsFormController.CONTROLLER_URL, executionYear), model, redirectAttributes);
    }

    @Override
    public boolean isFormIsFilled(final ExecutionYear executionYear, final Student student) {
        return false;
    }

    @Override
    protected Student getStudent(final Model model) {
        return AccessControl.getPerson().getStudent();
    }

    
}
