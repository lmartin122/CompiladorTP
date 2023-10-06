package Lexico.AccionesSemanticas;

import Tools.ProgramReader;

public class ASLimpiarBuffer implements AccionSemantica{
    @Override
    public int run(char simbolo, ProgramReader reader) {
        this.buffer.setLength(0);
        return 0;
    }
}
