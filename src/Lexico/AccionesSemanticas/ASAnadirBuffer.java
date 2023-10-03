package Lexico.AccionesSemanticas;

import Tools.ProgramReader;

public class ASAnadirBuffer implements AccionSemantica {
/*
ACCION SEMANTICA 1
 */
    @Override
    public int run(char simbolo, ProgramReader reader) {
        buffer.append(simbolo);
        return 0;
    }
}
