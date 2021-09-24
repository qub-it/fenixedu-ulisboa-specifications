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

import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;

public class ProfessionTimeType extends ProfessionTimeType_Base implements Comparable<ProfessionTimeType> {

    private ProfessionTimeType() {
        super();
        setRoot(Bennu.getInstance());
    }

    public ProfessionTimeType(String code, LocalizedString description) {
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
            if (readAll().filter(type -> code.equals(type.getCode()) && type != ProfessionTimeType.this).findAny().isPresent()) {
                throw new DomainException("error.code.alreadyUsed");
            }
            super.setCode(code);
        }
    }

    @SafeVarargs
    public static Stream<ProfessionTimeType> readAll(Predicate<ProfessionTimeType>... predicates) {
        Stream<ProfessionTimeType> disabilityTypes = Bennu.getInstance().getProfessionTimeTypesSet().stream();
        for (Predicate<ProfessionTimeType> predicate : predicates) {
            disabilityTypes = disabilityTypes.filter(predicate);
        }
        return disabilityTypes;
    }

    public static ProfessionTimeType findByCode(String code) {
        Predicate<ProfessionTimeType> matchesCode = disabilityType -> code.equals(disabilityType.getCode());
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
    public int compareTo(ProfessionTimeType anotherType) {
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
