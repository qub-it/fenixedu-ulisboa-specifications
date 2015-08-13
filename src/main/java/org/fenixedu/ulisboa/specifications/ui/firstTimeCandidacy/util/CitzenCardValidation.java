package org.fenixedu.ulisboa.specifications.ui.firstTimeCandidacy.util;

public class CitzenCardValidation {

    //Algorithm based on https://www.cartaodecidadao.pt/images/stories/Algoritmo_Num_Documento_CC.pdf
    public static boolean validateNumeroDocumentoCC(String numeroDocumento) {
        int sum = 0;
        boolean secondDigit = false;
        if (numeroDocumento.length() != 12)
//            throw new RuntimeException("Tamanho inválido para número dedocumento.");
            return false;
        for (int i = numeroDocumento.length() - 1; i >= 0; --i) {
            int value = getNumberFromChar(numeroDocumento.charAt(i));
            if (value == -1) {
                return false;
            }
            if (secondDigit) {
                value *= 2;
                if (value > 9)
                    value -= 9;
            }
            sum += value;
            secondDigit = !secondDigit;
        }
        return (sum % 10) == 0;
    }

    public static int getNumberFromChar(char letter) {
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
//        throw new RuntimeException("Valor inválido no número de documento.");
        return -1;
    }
}
