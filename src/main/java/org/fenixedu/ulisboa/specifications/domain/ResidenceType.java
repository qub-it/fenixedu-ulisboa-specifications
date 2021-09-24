/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: joao.roxo@qub-it.com
 *
 *
 * This file is part of FenixEdu fenixedu-ulisboa-specifications.
 *
 * FenixEdu fenixedu-ulisboa-specifications is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu fenixedu-ulisboa-specifications is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu fenixedu-ulisboa-specifications.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.ulisboa.specifications.domain;

import java.util.Comparator;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;

public class ResidenceType extends ResidenceType_Base implements Comparable<ResidenceType> {

    public static final Comparator<ResidenceType> COMPARE_BY_DESCRIPTION_OTHER_IN_END = new Comparator<ResidenceType>() {

        @Override
        public int compare(ResidenceType o1, ResidenceType o2) {
            if (o1.isOther()) {
                return 1;
            }
            if (o2.isOther()) {
                return -1;
            }

            return o1.getDescription().getContent().compareTo(o2.getDescription().getContent());
        }
    };

    private ResidenceType() {
        super();
        setRoot(Bennu.getInstance());
    }

    public ResidenceType(String code, LocalizedString description) {
        this();
        setCode(code);
        setDescription(description);
        setOther(false);
    }

    @Override
    public void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            super.setCode(null);
        } else {
            if (readAll().filter(type -> code.equals(type.getCode()) && type != ResidenceType.this).findAny().isPresent()) {
                throw new DomainException("error.code.alreadyUsed");
            }
            super.setCode(code);
        }
    }

    @SafeVarargs
    public static Stream<ResidenceType> readAll(Predicate<ResidenceType>... predicates) {
        Stream<ResidenceType> disabilityTypes = Bennu.getInstance().getResidenceTypesSet().stream();
        for (Predicate<ResidenceType> predicate : predicates) {
            disabilityTypes = disabilityTypes.filter(predicate);
        }
        return disabilityTypes;
    }

    public static ResidenceType findByCode(String code) {
        Predicate<ResidenceType> matchesCode = disabilityType -> code.equals(disabilityType.getCode());
        return readAll(matchesCode).findFirst().orElse(null);
    }

    public String getLocalizedName() {
        return getLocalizedName(I18N.getLocale());
    }

    public String getLocalizedName(Locale locale) {
        return getCode() + " - " + getDescription().getContent(locale);
    }

    public void delete() {
        setRoot(null);
        deleteDomainObject();
    }

    public boolean isOther() {
        return getOther();
    }

    @Override
    public int compareTo(ResidenceType anotherType) {
        //OTHER is the last option (ascending order)
        if (isOther() && anotherType.isOther()) {
            return 0;
        }
        if (isOther()) {
            return 1;
        }
        if (anotherType.isOther()) {
            return -1;
        }

        return getDescription().getContent().compareTo(anotherType.getDescription().getContent());
    }
}
