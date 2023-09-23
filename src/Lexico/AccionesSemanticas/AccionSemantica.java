package Lexico.AccionesSemanticas;

import Tools.ProgramReader;

public interface AccionSemantica {
    static final StringBuilder buffer = new StringBuilder();
    final int TOKEN = 1;
    final int SUCCESSFUL = 0;
    final int FLOAT_ERROR = -2;
    final int INTEGER_ERROR = -1;
    final int SIMBOL_ERROR = -3;

    public abstract int run(char simbolo, ProgramReader reader);
}
