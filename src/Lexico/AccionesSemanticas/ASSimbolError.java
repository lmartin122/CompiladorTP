package Lexico.AccionesSemanticas;

import Tools.Logger;
import Tools.ProgramReader;
import Tools.Tupla;

public class ASSimbolError implements AccionSemantica {
    @Override
    public Tupla<String, Short> run(char simbolo, ProgramReader reader) {
        Logger.logError(reader.getCurrentLine(), simbolo + " es un simbolo desconocido.");
        return new Tupla<>(null, SIMBOL_ERROR);
    }
}
