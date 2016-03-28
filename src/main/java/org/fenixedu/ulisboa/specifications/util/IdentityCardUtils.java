package org.fenixedu.ulisboa.specifications.util;

import org.fenixedu.academic.domain.IdentificationDocumentExtraDigit;
import org.fenixedu.academic.domain.IdentificationDocumentSeriesNumber;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.PersonIdentificationDocumentExtraInfo;
import org.fenixedu.academic.domain.person.IDDocumentType;
import org.fenixedu.ulisboa.specifications.domain.exceptions.ULisboaSpecificationsDomainException;

import com.google.common.base.Strings;

/**
 * @see {@link com.qubit.qubEdu.module.base.util.IdentityCardUtil}
 */
public class IdentityCardUtils {

    public static String getDigitControlFromPerson(final Person person) {
        if (!Strings.isNullOrEmpty(person.getIdentificationDocumentSeriesNumberValue())) {
            return person.getIdentificationDocumentSeriesNumberValue();
        }

        return person.getIdentificationDocumentExtraDigitValue();
    }

    public static void editDigitControlOnPerson(final Person person, final String digitControl) {
        PersonIdentificationDocumentExtraInfo documentExtraInfo =
                person.getPersonIdentificationDocumentExtraInfo(IdentificationDocumentSeriesNumber.class);

        if (documentExtraInfo == null) {
            documentExtraInfo = person.getPersonIdentificationDocumentExtraInfo(IdentificationDocumentExtraDigit.class);
        }

        if (documentExtraInfo == null) {
            documentExtraInfo = new IdentificationDocumentSeriesNumber();
            documentExtraInfo.setPerson(person);
        }

        if (person.getIdDocumentType() == IDDocumentType.IDENTITY_CARD) {
            if (Strings.isNullOrEmpty(digitControl)) {
                throw new ULisboaSpecificationsDomainException("error.IdentityCardUtils.digit.control.required");
            }

            if (!validate(person.getDocumentIdNumber(), digitControl)) {
                throw new ULisboaSpecificationsDomainException("error.IdentityCardUtils.invalid.digit.control");
            }
        }

        documentExtraInfo.setValue(digitControl);
    }

    public static boolean validate(final String idDocumentNumber, final String digitControl) {

        if (isCartaoCidadaoDigitControlFormatValid(digitControl)) {
            return validateCartaoCidadaoDigitControl(idDocumentNumber, digitControl);
        }

        if (isBilheteIdentidadeDigitControlFormatValid(digitControl)) {
            return validateBilheteIdentidadeDigitControl(idDocumentNumber, digitControl);
        }

        return false;
    }

    private static boolean validateBilheteIdentidadeDigitControl(final String idDocumentNumber, final String digitControl) {
        if (!isBilheteIdentidadeDigitControlFormatValid(digitControl)) {
            return false;
        }

        return generateBilheteIdentidadeDigitControl(idDocumentNumber) == Integer.parseInt(digitControl);
    }

    private static boolean validateCartaoCidadaoDigitControl(final String idDocumentNumber, final String digitControl) {
        if (!isCartaoCidadaoDigitControlFormatValid(digitControl)) {
            return false;
        }

        if ((idDocumentNumber.length() + digitControl.length()) != 12) {
            return false;
        }

        final String s = idDocumentNumber + digitControl;

        int sum = 0;
        boolean secondDigit = false;
        for (int i = s.length() - 1; i >= 0; --i) {
            int valor = getNumberFromChar(s.charAt(i));
            if (secondDigit) {
                valor *= 2;

                if (valor > 9) {
                    valor -= 9;
                }
            }

            sum += valor;
            secondDigit = !secondDigit;
        }

        return (sum % 10) == 0;
    }

    public static boolean isIdentityCardDigitControlFormatValid(final String extraValue) {
        return !Strings.isNullOrEmpty(extraValue)
                && (isBilheteIdentidadeDigitControlFormatValid(extraValue) || isCartaoCidadaoDigitControlFormatValid(extraValue));
    }

    public static boolean isBilheteIdentidadeDigitControlFormatValid(final String extraValue) {
        return extraValue.matches("\\d");
    }

    public static boolean isCartaoCidadaoDigitControlFormatValid(final String extraValue) {
        return extraValue.matches("\\d[A-Z][A-Z]\\d");
    }

    public static int generateBilheteIdentidadeDigitControl(final String idDocumentNumber) throws NumberFormatException {

        try {
            Integer.valueOf(idDocumentNumber);
        } catch (NumberFormatException ex) {
            throw new ULisboaSpecificationsDomainException("error.IdentityCardUtils.invalid.document.number");
        }

        int mult = 2;
        int controlSum = 0;
        for (int i = 0; i < idDocumentNumber.length(); i++) {
            controlSum += Integer.valueOf(idDocumentNumber.charAt(idDocumentNumber.length() - i - 1)) * mult;

            mult++;
        }

        int result = controlSum % 11;

        int checkDigit;

        if (result < 2) {
            checkDigit = 0;
        } else {
            checkDigit = 11 - result;
        }

        return checkDigit;
    }

    private static int getNumberFromChar(char letter) {
        switch (letter) {
        case '0':
            return 0;
        case '1':
            return 1;
        case '2':
            return 2;
        case '3':
            return 3;
        case '4':
            return 4;
        case '5':
            return 5;
        case '6':
            return 6;
        case '7':
            return 7;
        case '8':
            return 8;
        case '9':
            return 9;
        case 'A':
            return 10;
        case 'B':
            return 11;
        case 'C':
            return 12;
        case 'D':
            return 13;
        case 'E':
            return 14;
        case 'F':
            return 15;
        case 'G':
            return 16;
        case 'H':
            return 17;
        case 'I':
            return 18;
        case 'J':
            return 19;
        case 'K':
            return 20;
        case 'L':
            return 21;
        case 'M':
            return 22;
        case 'N':
            return 23;
        case 'O':
            return 24;
        case 'P':
            return 25;
        case 'Q':
            return 26;
        case 'R':
            return 27;
        case 'S':
            return 28;
        case 'T':
            return 29;
        case 'U':
            return 30;
        case 'V':
            return 31;
        case 'W':
            return 32;
        case 'X':
            return 33;
        case 'Y':
            return 34;
        case 'Z':
            return 35;
        }

        throw new ULisboaSpecificationsDomainException("error.IdentityCardUtils.invalid.document.number");
    }

}
