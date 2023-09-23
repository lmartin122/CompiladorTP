package Lexico.AccionesSemanticas;

import Tools.Logger;
import Tools.ProgramReader;

public class ASFloatError implements AccionSemantica {

    @Override
    public int run(char simbolo, ProgramReader reader) {
        Logger.logError(reader.getCurrentLine(), "flotante mal escrito.");
        return FLOAT_ERROR;
    }
}
