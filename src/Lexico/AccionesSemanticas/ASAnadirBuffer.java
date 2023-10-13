package Lexico.AccionesSemanticas;

import Tools.Logger;
import Tools.ProgramReader;
import Tools.Tupla;

public class ASAnadirBuffer implements AccionSemantica {
    /*
     * ACCION SEMANTICA 1
     */
    @Override
    public Tupla<String, Short> run(char simbolo, ProgramReader reader) {
        if (reader.isNextEndProgram()) {
            Logger.logError(reader.getCurrentLine(), "El string nunca se cierra y finaliza el programa");
            return new Tupla<>("Fin del programa.", (short) 0);
        }
        if (simbolo == '\n' || simbolo == '\r') {
            // System.out.print(" SALTO DE LINEA");
            buffer.append(" ");
        } else {
            buffer.append(simbolo);

        }
        return null;
    }
}
