package Lexico.AccionesSemanticas;

import Tools.ProgramReader;
import Tools.Tupla;

public class ASLimpiarBuffer implements AccionSemantica {
    @Override
    public Tupla<String, Short> run(char simbolo, ProgramReader reader) {
        buffer.setLength(0);
        return null;
    }
}
