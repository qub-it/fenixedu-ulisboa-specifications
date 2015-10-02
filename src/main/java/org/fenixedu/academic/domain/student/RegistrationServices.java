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
package org.fenixedu.academic.domain.student;

import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.bennu.core.domain.Bennu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;

abstract public class RegistrationServices {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationServices.class);

    @Atomic
    static public void delete(final Registration registration) throws Exception {

        if (registration != null) {

            final RegistrationProtocol normalProtocol = findRegistrationProtocolNormal();

            try {
                // HACK BEGIN, temporary in order to bypass domain logic
                normalProtocol.setAlien(true);

                // must use this remove, setIngreesionType is protected by domain logic
                registration.getIngressionType().removeRegistration(registration);
                registration.getStudentCandidacy().setIngressionType(null);

                // TODO legidio, necessary?
                while (!registration.getRegistrationStatesSet().isEmpty()) {
                    final RegistrationState state = registration.getRegistrationStatesSet().iterator().next();
                    state.delete();
                    logger.info("Deleting " + state);
                }

                while (!registration.getRegistrationStateLogSet().isEmpty()) {
                    final RegistrationStateLog log = registration.getRegistrationStateLogSet().iterator().next();
                    log.setRegistration(null);
                    log.delete();
                    logger.info("Deleting " + log);
                }

                final Student student = registration.getStudent();
                registration.delete();
                logger.info("Deleting " + registration);

                for (final PersonalIngressionData data : student.getPersonalIngressionsDataSet()) {
                    if (data.getPrecedentDegreesInformationsSet().isEmpty()) {
                        data.delete();
                        logger.info("Deleting " + data);
                    }
                }

            } finally {
                // HACK END, temporary in order to bypass domain logic
                normalProtocol.setAlien(false);
            }

        }
    }

    static private RegistrationProtocol findRegistrationProtocolNormal() {
        return Bennu.getInstance().getRegistrationProtocolsSet().stream().filter(r -> r.getCode().equals("NORMAL")).findFirst()
                .get();
    }

}
