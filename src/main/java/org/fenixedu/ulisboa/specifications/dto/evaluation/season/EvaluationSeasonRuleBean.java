/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: luis.egidio@qub-it.com
 *
 * 
 * This file is part of FenixEdu Specifications.
 *
 * FenixEdu Specifications is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Specifications is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Specifications.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fenixedu.ulisboa.specifications.dto.evaluation.season;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.Grade;
import org.fenixedu.academic.domain.GradeScaleEnum;
import org.fenixedu.academic.domain.ShiftType;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.student.StatuteType;
import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.academic.domain.evaluation.season.EvaluationSeasonServices;
import org.fenixedu.academic.domain.evaluation.season.rule.EvaluationSeasonRule;
import org.fenixedu.academic.domain.evaluation.season.rule.EvaluationSeasonShiftType;
import org.fenixedu.academic.domain.evaluation.season.rule.EvaluationSeasonStatuteType;
import org.fenixedu.academic.domain.evaluation.season.rule.GradeScaleValidator;
import org.fenixedu.academic.domain.evaluation.season.rule.PreviousSeasonBlockingGrade;
import org.fenixedu.academic.domain.evaluation.season.rule.PreviousSeasonMinimumGrade;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class EvaluationSeasonRuleBean implements IBean {

    private EvaluationSeason season;

    String evaluationSeasonRuleSubclass;

    private String gradeValue;

    private GradeScaleEnum gradeScale;

    private List<TupleDataSourceBean> gradeScaleDataSource;

    private Set<DegreeType> degreeTypes;

    private List<TupleDataSourceBean> degreeTypesDataSource;

    private List<ShiftType> shiftTypes;

    private List<TupleDataSourceBean> shiftTypesDataSource;

    private List<StatuteType> statuteTypes;

    private List<TupleDataSourceBean> statuteTypesDataSource;

    private String gradeValues;

    private LocalizedString ruleDescription;

    private boolean appliesToCurriculumAggregatorEntry;

    public EvaluationSeason getSeason() {
        return season;
    }

    public void setSeason(EvaluationSeason evaluationSeason) {
        season = evaluationSeason;
    }

    public String getEvaluationSeasonRuleSubclass() {
        return this.evaluationSeasonRuleSubclass;
    }

    public void setEvaluationSeasonRuleSubclass(final String input) {
        this.evaluationSeasonRuleSubclass = input;
    }

    public String getGradeValue() {
        return gradeValue;
    }

    public void setGradeValue(String gradeValue) {
        this.gradeValue = gradeValue;
    }

    public GradeScaleEnum getGradeScale() {
        return gradeScale;
    }

    public void setGradeScale(GradeScaleEnum gradeScale) {
        this.gradeScale = gradeScale;
    }

    public List<TupleDataSourceBean> getGradeScaleDataSource() {
        return gradeScaleDataSource;
    }

    public void setGradeScaleDataSource(List<TupleDataSourceBean> gradeScaleDataSource) {
        this.gradeScaleDataSource = gradeScaleDataSource;
    }

    public Set<DegreeType> getDegreeTypes() {
        return degreeTypes;
    }

    public void setDegreeTypes(Set<DegreeType> degreeTypes) {
        this.degreeTypes = degreeTypes;
    }

    public List<TupleDataSourceBean> getDegreeTypesDataSource() {
        return degreeTypesDataSource;
    }

    public void setDegreeTypesDataSource(List<TupleDataSourceBean> value) {
        this.degreeTypesDataSource = value;
    }

    public List<ShiftType> getShiftTypes() {
        return this.shiftTypes;
    }

    public void setShiftTypes(final List<ShiftType> input) {
        this.shiftTypes = input;
    }

    public List<TupleDataSourceBean> getShiftTypesDataSource() {
        return shiftTypesDataSource;
    }

    public void setShiftTypesDataSource(List<TupleDataSourceBean> value) {
        this.shiftTypesDataSource = value;
    }

    public List<StatuteType> getStatuteTypes() {
        return this.statuteTypes;
    }

    public void setStatuteTypes(final List<StatuteType> input) {
        this.statuteTypes = input;
    }

    public List<TupleDataSourceBean> getStatuteTypesDataSource() {
        return statuteTypesDataSource;
    }

    public void setStatuteTypesDataSource(List<TupleDataSourceBean> value) {
        this.statuteTypesDataSource = value;
    }

    public String getGradeValues() {
        return gradeValues;
    }

    public void setGradeValues(String gradeScaleValues) {
        this.gradeValues = gradeScaleValues;
    }

    public LocalizedString getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(LocalizedString ruleDescription) {
        this.ruleDescription = ruleDescription;
    }

    public boolean getAppliesToCurriculumAggregatorEntry() {
        return appliesToCurriculumAggregatorEntry;
    }

    public void setAppliesToCurriculumAggregatorEntry(boolean appliesToCurriculumAggregatorEntry) {
        this.appliesToCurriculumAggregatorEntry = appliesToCurriculumAggregatorEntry;
    }

    public EvaluationSeasonRuleBean() {
        init();
    }

    static public EvaluationSeasonRuleBean creation(final EvaluationSeason season,
            final Class<? extends EvaluationSeasonRule> clazz) {

        final EvaluationSeasonRuleBean result = new EvaluationSeasonRuleBean();
        result.setSeason(season);
        result.setEvaluationSeasonRuleSubclass(clazz == null ? null : clazz.getSimpleName());
        return result;
    }

    static private EvaluationSeasonRuleBean update(final EvaluationSeasonRule input) {
        return creation(input.getSeason(), input.getClass());
    }

    static public EvaluationSeasonRuleBean update(final PreviousSeasonBlockingGrade input) {
        final EvaluationSeasonRuleBean result = update((EvaluationSeasonRule) input);
        result.init(input.getBlocking());
        return result;
    }

    static public EvaluationSeasonRuleBean update(final PreviousSeasonMinimumGrade input) {
        final EvaluationSeasonRuleBean result = update((EvaluationSeasonRule) input);
        result.init(input.getMinimum());
        return result;
    }

    static public EvaluationSeasonRuleBean update(final GradeScaleValidator input) {
        final EvaluationSeasonRuleBean result = update((EvaluationSeasonRule) input);

        result.setGradeScale(input.getGradeScale());
        result.setGradeValues(input.getGradeValues());
        result.setDegreeTypes(input.getDegreeTypeSet());
        result.setRuleDescription(input.getRuleDescription());
        result.setAppliesToCurriculumAggregatorEntry(input.getAppliesToCurriculumAggregatorEntry());

        return result;
    }

    static public EvaluationSeasonRuleBean update(final EvaluationSeasonShiftType input) {
        final EvaluationSeasonRuleBean result = update((EvaluationSeasonRule) input);
        result.setShiftTypes(Lists.newArrayList(input.getShiftTypes().getTypes()));
        return result;
    }

    static public EvaluationSeasonRuleBean update(final EvaluationSeasonStatuteType input) {
        final EvaluationSeasonRuleBean result = update((EvaluationSeasonRule) input);
        result.setStatuteTypes(Lists.newArrayList(input.getStatuteTypesSet()));
        return result;
    }

    private void init(final Grade grade) {
        if (grade != null) {
            setGradeValue(grade.getValue());
            setGradeScale(grade.getGradeScale());
        }
    }

    private void init() {
        this.gradeScaleDataSource = Arrays.<GradeScaleEnum> asList(GradeScaleEnum.values()).stream()
                .map(l -> new TupleDataSourceBean(((GradeScaleEnum) l).name(), ((GradeScaleEnum) l).getDescription()))
                .collect(Collectors.<TupleDataSourceBean> toList());

        this.degreeTypesDataSource = DegreeType.all()
                .sorted((x, y) -> x.getName().getContent().compareTo(y.getName().getContent()))
                .map(x -> new TupleDataSourceBean(x.getExternalId(), x.getName().getContent())).collect(Collectors.toList());

        this.shiftTypesDataSource =
                Sets.newHashSet(ShiftType.values()).stream()
                        .sorted((x, y) -> x.getFullNameTipoAula().compareTo(y.getFullNameTipoAula()))
                        .map(i -> new TupleDataSourceBean(i.getName(),
                                String.format("%s [%s]", i.getFullNameTipoAula(), i.getSiglaTipoAula())))
                        .collect(Collectors.toList());

        this.statuteTypesDataSource = StatuteType.readAll(type -> true).sorted((x, y) -> x.getName().compareTo(y.getName())).map(
                i -> new TupleDataSourceBean(i.getExternalId(), String.format("%s [%s]", i.getName().getContent(), i.getCode())))
                .collect(Collectors.toList());
    }

    public LocalizedString getDescriptionI18N() {
        return ULisboaSpecificationsUtil.bundleI18N(getEvaluationSeasonRuleSubclass());
    }

    public LocalizedString getSeasonDescriptionI18N() {
        return EvaluationSeasonServices.getDescriptionI18N(getSeason());
    }

    public Grade getGrade() {
        try {
            return Grade.createGrade(getGradeValue(), getGradeScale());
        } catch (final Throwable t) {
            return Grade.createEmptyGrade();
        }
    }

}
