package Lexico.AccionesSemanticas;

import Lexico.AnalizadorLexico;
import Tools.Logger;
import Tools.ProgramReader;
import Tools.Tupla;

public class ASIdError implements AccionSemantica{
    @Override
    public Tupla<String, Short> run(char simbolo, ProgramReader reader) {
        Logger.logError(reader.getCurrentLine(),"No se admite mayusculas en los identificadores");
        this.buffer.setLength(0);
        AnalizadorLexico.estado_error = -1;
        return null;
    }
}
