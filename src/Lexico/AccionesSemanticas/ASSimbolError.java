package Lexico.AccionesSemanticas;

import Tools.Logger;
import Tools.ProgramReader;
import Tools.Tupla;

public class ASSimbolError implements AccionSemantica {
    @Override
    public Tupla<String, Short> run(char simbolo, ProgramReader reader) {
        System.out.println("SIMBOLO DESCONOCIDO");
        Logger.logError(reader.getCurrentLine(), simbolo + " es un simbolo desconocido.");
        return null;
    }
}