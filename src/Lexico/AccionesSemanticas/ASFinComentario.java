package Lexico.AccionesSemanticas;

import Tools.Logger;
import Tools.ProgramReader;
import Tools.Tupla;

public class ASFinComentario implements AccionSemantica {

    @Override
    public Tupla<String, Short> run(char simbolo, ProgramReader reader) {
        System.out.println("ASFinComentario: " + simbolo);
        if (reader.isNextEndProgram()) {
            Logger.logError(reader.getCurrentLine(), "El comentario nunca se cierra y finaliza el programa");
            return new Tupla<>("Fin del programa.", (short) 0);
        }
    return null;
    }
}
