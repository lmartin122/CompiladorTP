package Lexico.AccionesSemanticas;

import Tools.ProgramReader;
import Tools.Tupla;

public interface AccionSemantica {
    static final StringBuilder buffer = new StringBuilder();
    final short SUCCESSFUL = 0;
    final short FLOAT_ERROR = -2;
    final short INTEGER_ERROR = -1;
    final short SIMBOL_ERROR = -3;

    public abstract Tupla<String, Short> run(char simbolo, ProgramReader reader);
}
