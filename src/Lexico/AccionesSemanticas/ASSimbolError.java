package Lexico.AccionesSemanticas;

import Tools.Logger;
import Tools.ProgramReader;

public class ASSimbolError implements AccionSemantica {
    @Override
    public int run(char simbolo, ProgramReader reader) {
        Logger.logError(reader.getCurrentLine(), simbolo + " es un simbolo desconocido.");
        return SIMBOL_ERROR;
    }
}
