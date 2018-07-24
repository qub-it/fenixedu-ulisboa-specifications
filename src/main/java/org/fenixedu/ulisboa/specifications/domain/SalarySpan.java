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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;

public class SalarySpan extends SalarySpan_Base implements Comparable<SalarySpan> {

    private static final Pattern OVER_PATTERN = Pattern.compile("OVER_(\\d+)");
    private static final Pattern RANGE_PATTERN = Pattern.compile("(\\d+)_(\\d+)");

    private SalarySpan() {
        super();
        setRoot(Bennu.getInstance());
    }

    public SalarySpan(String code, LocalizedString description) {
        this();
        setCode(code);
        setDescription(description);
        setOther(false);

        checkRules();
    }

    private void checkRules() {
        if (readAll().filter(salary -> getCode().equals(salary.getCode()) && salary != SalarySpan.this).findAny().isPresent()) {
            throw new DomainException("error.code.alreadyUsed");
        }

        if (!getCode().equals("DONT_KNOW") && !OVER_PATTERN.matcher(getCode()).matches()
                && !RANGE_PATTERN.matcher(getCode()).matches()) {
            throw new DomainException("error.code.not.in.right.format");
        }
    }

    @Override
    public void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            super.setCode(null);
        } else {
            super.setCode(code);
        }

        checkRules();
    }

    @SafeVarargs
    public static Stream<SalarySpan> readAll(Predicate<SalarySpan>... predicates) {
        Stream<SalarySpan> disabilityTypes = Bennu.getInstance().getSalarySpansSet().stream();
        for (Predicate<SalarySpan> predicate : predicates) {
            disabilityTypes = disabilityTypes.filter(predicate);
        }
        return disabilityTypes;
    }

    public static SalarySpan findByCode(String code) {
        Predicate<SalarySpan> matchesCode = salarySpan -> code.equals(salarySpan.getCode());
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

    public boolean isOther() {
        return getOther();
    }

    private int compareValue(SalarySpan another, boolean isOver) {
        Matcher myMatcher;
        Matcher theirMatcher;

        if (isOver) {
            myMatcher = OVER_PATTERN.matcher(getCode());
            theirMatcher = OVER_PATTERN.matcher(another.getCode());
        } else {
            myMatcher = RANGE_PATTERN.matcher(getCode());
            theirMatcher = RANGE_PATTERN.matcher(another.getCode());
        }

        myMatcher.find();
        theirMatcher.find();

        Integer myMinValue = new Integer(Integer.parseInt(myMatcher.group(1)));
        Integer theirMinValue = new Integer(Integer.parseInt(theirMatcher.group(1)));

        return myMinValue.compareTo(theirMinValue);
    }

    @Override
    public int compareTo(SalarySpan anotherSalary) {
        //OTHER is the last option (ascending order)
        if (isOther() && anotherSalary.isOther()) {
            return 0;
        }
        if (isOther()) {
            return 1;
        }
        if (anotherSalary.isOther()) {
            return -1;
        }

        if (getCode().contains("OVER")) {
            if (anotherSalary.getCode().contains("OVER")) {
                return compareValue(anotherSalary, true);
            }

            return 1;
        }

        if (anotherSalary.getCode().contains("OVER")) {
            return -1;
        }

        return compareValue(anotherSalary, false);
    }
}
