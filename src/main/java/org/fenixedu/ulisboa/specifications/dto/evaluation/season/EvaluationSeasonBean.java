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

import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.bennu.IBean;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonInformation;
import org.fenixedu.ulisboa.specifications.domain.evaluation.season.EvaluationSeasonServices;

public class EvaluationSeasonBean implements IBean {

    private EvaluationSeasonInformation evaluationSeasonInformation;
    private String code;
    private LocalizedString acronym;
    private LocalizedString name;
    private boolean normal;
    private boolean improvement;
    private boolean special;
    private boolean specialAuthorization;
    private Integer seasonOrder;
    private boolean active;
    private boolean requiresEnrolmentEvaluation;

    public EvaluationSeasonInformation getInformation() {
        return evaluationSeasonInformation;
    }

    public void setInformation(EvaluationSeasonInformation value) {
        evaluationSeasonInformation = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String value) {
        code = value;
    }

    public LocalizedString getAcronym() {
        return acronym;
    }

    public void setAcronym(LocalizedString value) {
        acronym = value;
    }

    public LocalizedString getName() {
        return name;
    }

    public void setName(LocalizedString value) {
        name = value;
    }

    public boolean getNormal() {
        return normal;
    }

    public void setNormal(boolean value) {
        normal = value;
    }

    public boolean getImprovement() {
        return improvement;
    }

    public void setImprovement(boolean value) {
        improvement = value;
    }

    public boolean getSpecial() {
        return special;
    }

    public void setSpecial(boolean value) {
        special = value;
    }

    public boolean getSpecialAuthorization() {
        return specialAuthorization;
    }

    public void setSpecialAuthorization(boolean value) {
        specialAuthorization = value;
    }

    public Integer getSeasonOrder() {
        return seasonOrder;
    }

    public void setSeasonOrder(Integer value) {
        seasonOrder = value;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(boolean value) {
        active = value;
    }

    public boolean getRequiresEnrolmentEvaluation() {
        return requiresEnrolmentEvaluation;
    }

    public void setRequiresEnrolmentEvaluation(boolean value) {
        requiresEnrolmentEvaluation = value;
    }

    public EvaluationSeasonBean() {

    }

    public EvaluationSeasonBean(EvaluationSeason evaluationSeason) {
        this.setInformation(evaluationSeason.getInformation());
        this.setCode(evaluationSeason.getCode());
        this.setAcronym(evaluationSeason.getAcronym());
        this.setName(evaluationSeason.getName());
        this.setNormal(evaluationSeason.getNormal());
        this.setImprovement(evaluationSeason.getImprovement());
        this.setSpecial(evaluationSeason.getSpecial());
        this.setSpecialAuthorization(evaluationSeason.getSpecialAuthorization());
        this.setSeasonOrder(getInformation().getSeasonOrder());
        this.setActive(getInformation().getActive());
        this.setRequiresEnrolmentEvaluation(EvaluationSeasonServices.isRequiresEnrolmentEvaluation(evaluationSeason));
    }

}
