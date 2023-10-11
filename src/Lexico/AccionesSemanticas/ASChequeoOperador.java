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
                    return new Tupla<>(null, Parser.MINUS_ASSIGN);
                } else {
                    buffer.setLength(0);
                    reader.returnCharacter();
                    return new Tupla<>(null, (short) '-');
                }

            case "<":
                if (simbolo == '=') {

                    buffer.setLength(0);
                    return new Tupla<>(null, Parser.LESS_THAN_OR_EQUAL_OPERATOR);
                } else {
                    buffer.setLength(0);
                    reader.returnCharacter();
                    return new Tupla<>(null, (short) '<');
                }

            case ">":
                if (simbolo == '=') {

                    buffer.setLength(0);
                    return new Tupla<>(null, Parser.GREATER_THAN_OR_EQUAL_OPERATOR);
                } else {
                    buffer.setLength(0);
                    reader.returnCharacter();
                    return new Tupla<>(null, (short) '>');
                }

            case "=":
                if (simbolo == '=') {

                    buffer.setLength(0);
                    return new Tupla<>(null, Parser.EQUAL_OPERATOR);
                } else {
                    buffer.setLength(0);
                    reader.returnCharacter();
                    return new Tupla<>(null, (short) '=');
                }

            case "!":
                if (simbolo == '!') {

                    buffer.setLength(0);
                    return new Tupla<>(null, Parser.NOT_EQUAL_OPERATOR);
                } else {
                    buffer.setLength(0);
                    // ERROR, no existe el "!" solo
                    Logger.logError(reader.getCurrentLine(), "Se leyo el caracter '!' ");
                    return new Tupla<>(null, (short) '!');
                }
            case ".":
                buffer.setLength(0);
                reader.returnCharacter();
                return new Tupla<>(null, (short) '.');

        }
        return null;
    }
}
