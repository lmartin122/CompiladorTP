package Lexico.AccionesSemanticas;

import Tools.Logger;
import Tools.ProgramReader;

public class ASIntegerError implements AccionSemantica {

    @Override
    public int run(char simbolo, ProgramReader reader) {
        Logger.logError(reader.getCurrentLine(), "integer mal escrito.");
        return INTEGER_ERROR;
    }
}
