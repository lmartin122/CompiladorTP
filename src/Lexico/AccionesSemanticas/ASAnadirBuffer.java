package Lexico.AccionesSemanticas;

import Tools.ProgramReader;

public class ASAnadirBuffer implements AccionSemantica {

    @Override
    public int run(char simbolo, ProgramReader reader) {
        buffer.append(simbolo);
        return 0;
    }
}