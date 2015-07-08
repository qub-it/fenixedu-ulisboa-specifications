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

import java.util.function.Predicate;

import org.fenixedu.academic.domain.candidacy.IngressionType;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public class ULisboaSpecificationsRoot extends ULisboaSpecificationsRoot_Base {
    public ULisboaSpecificationsRoot() {
        super();
    }

    public static void init() {
        if (getInstance() != null) {
            return;
        }
        makeInstance();
    }

    private static ULisboaSpecificationsRoot instance;

    public static ULisboaSpecificationsRoot getInstance() {
        if (instance == null) {
            instance = FenixFramework.getDomainRoot().getBennu().getULisboaSpecificationsRoot();
        }
        return instance;
    }

    @Atomic
    private static void makeInstance() {
        FenixFramework.getDomainRoot().getBennu().setULisboaSpecificationsRoot(new ULisboaSpecificationsRoot());
    }

    public IngressionType getIngressionType(String contingent) {
        ContingentToIngression ingression = getContingentToIngression(contingent);
        return (ingression != null) ? ingression.getIngressionType() : null;
    }

    @Atomic
    public void setIngressionType(String contingent, IngressionType ingressionType) {
        ContingentToIngression ingression = getContingentToIngression(contingent);
        if (ingression != null) {
            ingression.setIngressionType(ingressionType);
        } else {
            new ContingentToIngression(contingent, ingressionType);
        }
    }

    @Atomic
    public void deleteIngressionType(String contingent) {
        getContingentToIngression(contingent).delete();
    }

    private ContingentToIngression getContingentToIngression(String contingent) {
        Predicate<ContingentToIngression> matchContingent = cToI -> cToI.getContingent().equals(contingent);
        return getContingentToIngressionsSet().stream().filter(matchContingent).findAny().orElse(null);
    }
}
