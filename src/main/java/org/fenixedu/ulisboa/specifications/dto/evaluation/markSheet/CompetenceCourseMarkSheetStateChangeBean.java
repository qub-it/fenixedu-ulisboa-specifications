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

package org.fenixedu.ulisboa.specifications.dto.evaluation.markSheet;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.bennu.IBean;
import org.fenixedu.bennu.TupleDataSourceBean;
import org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheet;
import org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheetStateChange;
import org.fenixedu.ulisboa.specifications.domain.evaluation.markSheet.CompetenceCourseMarkSheetStateEnum;
import org.joda.time.DateTime;

public class CompetenceCourseMarkSheetStateChangeBean implements IBean {

    private CompetenceCourseMarkSheet competenceCourseMarkSheet;
    private List<TupleDataSourceBean> competenceCourseMarkSheetDataSource;
    private CompetenceCourseMarkSheetStateEnum state;
    private List<TupleDataSourceBean> stateDataSource;
    private DateTime date;
    private String reason;
    private boolean byTeacher;

    public CompetenceCourseMarkSheet getCompetenceCourseMarkSheet() {
        return competenceCourseMarkSheet;
    }

    public void setCompetenceCourseMarkSheet(CompetenceCourseMarkSheet value) {
        competenceCourseMarkSheet = value;
    }

    public List<TupleDataSourceBean> getCompetenceCourseMarkSheetDataSource() {
        return competenceCourseMarkSheetDataSource;
    }

    public void setCompetenceCourseMarkSheetDataSource(List<CompetenceCourseMarkSheet> value) {
        this.competenceCourseMarkSheetDataSource = value.stream().map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.getExternalId()); //CHANGE_ME
            tuple.setText(x.toString()); //CHANGE_ME
            return tuple;
        }).collect(Collectors.toList());
    }

    public CompetenceCourseMarkSheetStateEnum getState() {
        return state;
    }

    public void setState(CompetenceCourseMarkSheetStateEnum value) {
        state = value;
    }

    public List<TupleDataSourceBean> getStateDataSource() {
        return stateDataSource;
    }

    public void setStateDataSource(List<CompetenceCourseMarkSheetStateEnum> value) {
        this.stateDataSource = value.stream().map(x -> {
            TupleDataSourceBean tuple = new TupleDataSourceBean();
            tuple.setId(x.name());
            tuple.setText(x.getDescriptionI18N().getContent());
            return tuple;
        }).collect(Collectors.toList());
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime value) {
        date = value;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String value) {
        reason = value;
    }

    public boolean getByTeacher() {
        return byTeacher;
    }

    public void setByTeacher(boolean value) {
        byTeacher = value;
    }

    public CompetenceCourseMarkSheetStateChangeBean() {

    }

    public CompetenceCourseMarkSheetStateChangeBean(CompetenceCourseMarkSheetStateChange competenceCourseMarkSheetStateChange) {
        this.setState(competenceCourseMarkSheetStateChange.getState());
        this.setDate(competenceCourseMarkSheetStateChange.getDate());
        this.setReason(competenceCourseMarkSheetStateChange.getReason());
        this.setByTeacher(competenceCourseMarkSheetStateChange.getByTeacher());
        this.setState(competenceCourseMarkSheetStateChange.getState());
        this.setDate(competenceCourseMarkSheetStateChange.getDate());
        this.setReason(competenceCourseMarkSheetStateChange.getReason());
        this.setByTeacher(competenceCourseMarkSheetStateChange.getByTeacher());
    }

}
