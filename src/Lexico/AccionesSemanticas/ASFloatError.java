package Lexico.AccionesSemanticas;

import Tools.Logger;
import Tools.ProgramReader;
import Tools.Tupla;

public class ASFloatError implements AccionSemantica {

    @Override
    public Tupla<String, Short> run(char simbolo, ProgramReader reader) {
        Logger.logError(reader.getCurrentLine(), "flotante mal escrito.");
        return new Tupla<>(null, FLOAT_ERROR);
    }
}
