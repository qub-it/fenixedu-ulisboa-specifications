package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.util;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.District;
import org.fenixedu.academic.domain.DistrictSubdivision;
import org.fenixedu.academic.domain.SchoolLevelType;
import org.fenixedu.academic.domain.organizationalStructure.Unit;
import org.fenixedu.academic.domain.organizationalStructure.UnitName;
import org.fenixedu.academic.domain.raides.DegreeDesignation;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.ulisboa.specifications.domain.Parish;
import org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.PersonalInformationFormController.DegreeDesignationBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Strings;

import pt.ist.fenixframework.FenixFramework;

@Controller
@RequestMapping("/fenixedu-ulisboa-specifications/firsttimecandidacy/autocompletes/")
public class AutoCompletesController {

    @RequestMapping(value = "/externalUnit", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody List<UnitBean> readExternalUnits(@RequestParam("namePart") String namePart, Model model) {
        assureLoggedInUser();
        Function<Unit, UnitBean> createUnitBean = un -> new UnitBean(un.getExternalId(), un.getName());
        return UnitName.findExternalUnit(namePart, 50).stream().map(i -> i.getUnit()).map(createUnitBean)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/externalUnitFreeOption", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody List<UnitBean> readExternalUnitsWithFreeOption(@RequestParam("namePart") String namePart, Model model) {
        assureLoggedInUser();
        List<UnitBean> readExternalUnits = readExternalUnits(namePart, model);
        readExternalUnits.add(0, new UnitBean(namePart, namePart));
        return readExternalUnits;
    }

    @RequestMapping(value = "/academicUnit", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody List<UnitBean> readAcademicUnits(@RequestParam("namePart") String namePart, Model model) {
        assureLoggedInUser();
        Function<UnitName, UnitBean> createUnitBean = un -> new UnitBean(un.getUnit().getExternalId(), un.getUnit().getName());
        return UnitName.findExternalAcademicUnit(namePart, 50).stream().map(createUnitBean).collect(Collectors.toList());
    }

    @RequestMapping(value = "/degreeDesignation/{unit}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody Collection<DegreeDesignationBean> readExternalUnits(@PathVariable("unit") String unitOid,
            @RequestParam("namePart") String namePart,
            @RequestParam(value = "schoolLevelType", required = false) final SchoolLevelType schoolLevelType, Model model) {
        assureLoggedInUser();
        Unit unit = null;
        try {
            unit = FenixFramework.getDomainObject(unitOid);
        } catch (Exception e) {
            //Not a unit, so it is a custom value, ignore
        }

        Collection<DegreeDesignation> possibleDesignations;
        if (unit == null) {
            possibleDesignations = Bennu.getInstance().getDegreeDesignationsSet();
        } else {
            possibleDesignations = unit.getDegreeDesignationSet();
        }

        Predicate<DegreeDesignation> matchesName = null;

        if (schoolLevelType != null) {
            matchesName =
                    dd -> schoolLevelType.getEquivalentDegreeClassifications().contains(dd.getDegreeClassification().getCode())
                            && StringNormalizer.normalize(getFullDescription(dd)).contains(StringNormalizer.normalize(namePart));
        } else {
            matchesName = dd -> StringNormalizer.normalize(getFullDescription(dd)).contains(StringNormalizer.normalize(namePart));
        }

        Function<DegreeDesignation, DegreeDesignationBean> createDesignationBean = dd -> {
            return new DegreeDesignationBean(getFullDescription(dd), dd.getExternalId());
        };
        return possibleDesignations.stream().filter(matchesName).map(createDesignationBean).limit(50)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/district/{oid}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody List<DistrictSubdivisionBean> readDistrictSubdivisions(@PathVariable("oid") District district,
            Model model) {
        assureLoggedInUser();
        Function<DistrictSubdivision, DistrictSubdivisionBean> createSubdivisionBean =
                ds -> new DistrictSubdivisionBean(ds.getExternalId(), ds.getName());
        List<DistrictSubdivisionBean> subdivisions =
                district.getDistrictSubdivisionsSet().stream().map(createSubdivisionBean).collect(Collectors.toList());
        subdivisions.add(new DistrictSubdivisionBean("", ""));
        return subdivisions;
    }

    @RequestMapping(value = "/districtSubdivision/{oid}", method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    public @ResponseBody List<ParishBean> readParish(@PathVariable("oid") DistrictSubdivision districtSubdivision, Model model) {
        assureLoggedInUser();
        Function<Parish, ParishBean> createParishBean = p -> new ParishBean(p.getExternalId(), p.getName());
        List<ParishBean> parishes =
                districtSubdivision.getParishSet().stream().map(createParishBean).collect(Collectors.toList());
        parishes.add(new ParishBean("", ""));
        return parishes;
    }

    @RequestMapping(value = "/raidesUnit", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public @ResponseBody List<UnitBean> readRaidesUnits(@RequestParam("namePart") String namePart, Model model) {
        assureLoggedInUser();

        final Function<Unit, UnitBean> createUnitBean = new Function<Unit, UnitBean>() {

            @Override
            public UnitBean apply(Unit t) {
                final String code = !Strings.isNullOrEmpty(t.getCode()) ? "[" + t.getCode() + "]" : "";
                return new UnitBean(t.getExternalId(), code + " " + t.getName());
            }
        };

        return UnitName.findExternalAcademicUnit(namePart, 50).stream().map(i -> i.getUnit())
                .filter(i -> !i.getDegreeDesignationSet().isEmpty()).map(createUnitBean).collect(Collectors.toList());
    }

    private static String getFullDescription(DegreeDesignation designation) {
        return "[" + designation.getCode() + "] " + designation.getDegreeClassification().getDescription1() + " - "
                + designation.getDescription();
    }

    private void assureLoggedInUser() {
        if (AccessControl.getPerson() == null) {
            throw new RuntimeException("No person authenticated");
        }
    }

    public static class DistrictSubdivisionBean {
        String id;

        String text;

        public DistrictSubdivisionBean(String id, String text) {
            this.id = id;
            this.text = text;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class ParishBean {
        String id;

        String text;

        public ParishBean(String id, String text) {
            this.id = id;
            this.text = text;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
