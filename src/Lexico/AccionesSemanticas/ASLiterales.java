package Lexico.AccionesSemanticas;

import Tools.ProgramReader;
import Tools.Tupla;

public class ASLiterales implements AccionSemantica {

    @Override
    public Tupla<String, Short> run(char simbolo, ProgramReader reader) {

        return switch (simbolo) {
            case '+' -> new Tupla<>(null, (short) '+');
            case '/' -> new Tupla<>(null, (short) '/');
            case '(' -> new Tupla<>(null, (short) '(');
            case ')' -> new Tupla<>(null, (short) ')');
            case ':' -> new Tupla<>(null, (short) ':');
            case ';' -> new Tupla<>(null, (short) ';');
            case ',' -> new Tupla<>(null, (short) ',');
            case '{' -> new Tupla<>(null, (short) '{');
            case '}' -> new Tupla<>(null, (short) '}');
            default -> null;
        };
    }
}