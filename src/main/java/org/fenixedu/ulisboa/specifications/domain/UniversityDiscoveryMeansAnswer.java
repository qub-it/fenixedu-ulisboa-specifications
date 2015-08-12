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
import org.fenixedu.commons.i18n.LocalizedString;

public class UniversityDiscoveryMeansAnswer extends UniversityDiscoveryMeansAnswer_Base {
    private UniversityDiscoveryMeansAnswer() {
        super();
        setRoot(Bennu.getInstance());
    }

    public UniversityDiscoveryMeansAnswer(String code, LocalizedString description) {
        this();
        setCode(code);
        setDescription(description);
    }

    @Override
    public void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            super.setCode(null);
        } else {
            if (readAll()
                    .filter(discovery -> code.equals(discovery.getCode()) && discovery != UniversityDiscoveryMeansAnswer.this)
                    .findAny().isPresent()) {
                throw new DomainException("error.code.alreadyUsed");
            }
            super.setCode(code);
        }
    }

    @SafeVarargs
    public static Stream<UniversityDiscoveryMeansAnswer> readAll(Predicate<UniversityDiscoveryMeansAnswer>... predicates) {
        Stream<UniversityDiscoveryMeansAnswer> discovery = Bennu.getInstance().getUniversityDiscoveryMeansAnswersSet().stream();
        for (Predicate<UniversityDiscoveryMeansAnswer> predicate : predicates) {
            discovery = discovery.filter(predicate);
        }
        return discovery;
    }

    public static UniversityDiscoveryMeansAnswer findByCode(String code) {
        Predicate<UniversityDiscoveryMeansAnswer> matchesCode = discovery -> code.equals(discovery.getCode());
        return readAll(matchesCode).findFirst().orElse(null);
    }

    public String getLocalizedName() {
        return getLocalizedName(Locale.getDefault());
    }

    public String getLocalizedName(Locale locale) {
        return getCode() + " - " + getDescription().getContent(locale);
    }

    public void delete() {
        setRoot(null);
        deleteDomainObject();
    }
}
