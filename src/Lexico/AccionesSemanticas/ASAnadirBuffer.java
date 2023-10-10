package Lexico.AccionesSemanticas;

import Tools.ProgramReader;
import Tools.Tupla;

public class ASAnadirBuffer implements AccionSemantica {
    /*
     * ACCION SEMANTICA 1
     */
    @Override
    public Tupla<String, Short> run(char simbolo, ProgramReader reader) {
        if (simbolo == '\n' || simbolo == '\r') {
            System.out.print(" SALTO DE LINEA");
            buffer.append(" ");
        } else {
            buffer.append(simbolo);

        }
        return null;
    }
}
