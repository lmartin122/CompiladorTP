package Lexico.AccionesSemanticas;

import Tools.Logger;
import Tools.ProgramReader;
import Tools.Tupla;

public class ASIntegerError implements AccionSemantica {

    @Override
    public Tupla<String, Short> run(char simbolo, ProgramReader reader) {
        Logger.logError(reader.getCurrentLine(), "integer mal escrito.");
        return new Tupla<>(null, INTEGER_ERROR);
    }
}
