package Lexico.AccionesSemanticas;

import Lexico.AnalizadorLexico;
import Sintactico.Parser;
import Tools.Logger;
import Tools.ProgramReader;
import Tools.Tupla;

public class ASIntegerError implements AccionSemantica {

    @Override
    public Tupla<String, Short> run(char simbolo, ProgramReader reader) {
        Logger.logError(reader.getCurrentLine(), "El integer esta mal escrito.");

        reader.returnCharacter();
        AccionSemantica.buffer.setLength(0);
        AnalizadorLexico.estado_error = -1;
        return new Tupla<>(null, (short) Parser.CTE_LONG);
    }
}
