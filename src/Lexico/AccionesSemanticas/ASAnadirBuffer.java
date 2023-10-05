package Lexico.AccionesSemanticas;

import Tools.ProgramReader;

public class ASAnadirBuffer implements AccionSemantica {
/*
ACCION SEMANTICA 1
 */
    @Override
    public int run(char simbolo, ProgramReader reader) {
        if(simbolo == '\n' || simbolo == '\r'){
            System.out.print(" SALTO DE LINEA");
            buffer.append(" ");
        } else {
            buffer.append(simbolo);

        }
        return 0;
    }
}
