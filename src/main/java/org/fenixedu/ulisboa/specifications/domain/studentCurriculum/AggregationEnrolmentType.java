/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 *
 * 
 * This file is part of FenixEdu fenixedu-ulisboa-specifications.
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
package org.fenixedu.ulisboa.specifications.domain.studentCurriculum;

import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.ulisboa.specifications.util.ULisboaSpecificationsUtil;

import pt.ist.fenixWebFramework.rendererExtensions.util.IPresentableEnum;

public enum AggregationEnrolmentType implements IPresentableEnum {

    ALL_MEMBERS,

    ONLY_AGGREGATOR,

    ONLY_AGGREGATOR_ENTRIES;

    @Override
    public String getLocalizedName() {
        return ULisboaSpecificationsUtil.bundleI18N(AggregationEnrolmentType.class.getSimpleName() + "." + name())
                .getContent(I18N.getLocale());
    }

}
