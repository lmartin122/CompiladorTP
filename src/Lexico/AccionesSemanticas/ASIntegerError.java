package Lexico.AccionesSemanticas;

import Lexico.AnalizadorLexico;
import Sintactico.Parser;
import Tools.Logger;
import Tools.ProgramReader;
import Tools.Tupla;

public class ASIntegerError implements AccionSemantica {

    @Override
    public Tupla<String, Short> run(char simbolo, ProgramReader reader) {
        Logger.logError(reader.getCurrentLine(), " integer mal escrito.");

        reader.returnCharacter();
        this.buffer.setLength(0);
        AnalizadorLexico.estado_error = -1;
        return new Tupla<>(null, (short) Parser.CTE_LONG);
    }
}
