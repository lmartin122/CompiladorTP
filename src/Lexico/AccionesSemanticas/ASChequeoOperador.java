package Lexico.AccionesSemanticas;

import Sintactico.Parser;
import Tools.Logger;
import Tools.ProgramReader;
import Tools.Tupla;

public class ASChequeoOperador implements AccionSemantica {
    /*
     * ACCION SEMANTICA 7 y 8
     */
    @Override
    public Tupla<String, Short> run(char simbolo, ProgramReader reader) {
        String auxBuffer = buffer.toString();

        switch (auxBuffer) {
            case "-":
                if (simbolo == '=') {

                    buffer.setLength(0);
                    return new Tupla<>("-=", Parser.MINUS_ASSIGN);
                } else {
                    buffer.setLength(0);
                    reader.returnCharacter();
                    return new Tupla<>("-", (short) '-');
                }

            case "<":
                if (simbolo == '=') {

                    buffer.setLength(0);
                    return new Tupla<>("<=", Parser.LESS_THAN_OR_EQUAL_OPERATOR);
                } else {
                    buffer.setLength(0);
                    reader.returnCharacter();
                    return new Tupla<>("<", (short) '<');
                }

            case ">":
                if (simbolo == '=') {

                    buffer.setLength(0);
                    return new Tupla<>(">=", Parser.GREATER_THAN_OR_EQUAL_OPERATOR);
                } else {
                    buffer.setLength(0);
                    reader.returnCharacter();
                    return new Tupla<>(">", (short) '>');
                }

            case "=":
                if (simbolo == '=') {

                    buffer.setLength(0);
                    return new Tupla<>("==", Parser.EQUAL_OPERATOR);
                } else {
                    buffer.setLength(0);
                    reader.returnCharacter();
                    return new Tupla<>("=", (short) '=');
                }

            case "!":
                if (simbolo == '!') {

                    buffer.setLength(0);
                    return new Tupla<>("!!", Parser.NOT_EQUAL_OPERATOR);
                } else {
                    buffer.setLength(0);
                    // ERROR, no existe el "!" solo
                    Logger.logError(reader.getCurrentLine(), "Se leyo el caracter '!' ");
                    return new Tupla<>("!", (short) '!');
                }

        }
        return null;
    }
}
