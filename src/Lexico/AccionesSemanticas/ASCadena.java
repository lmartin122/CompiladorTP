package Lexico.AccionesSemanticas;

import Tools.ProgramReader;

public class ASCadena implements AccionSemantica{
    /*
        Aca deberiamos hacer que toda la cadena que se concateno con el AnadirBuffer y está en el bufffer
        SE agregue a la tabla de simbolos o donde sea que haya que agregarla.
        Ademas, el anadirBuffer concateno los saltos de linea en caso de que tenga, por ende hay que removerlos
        Esta accion semantica se ejecutaria cuando estamos en el estado 18 y nos viene un % (finalizacion de la cadena)
     */
    @Override
    public int run(char simbolo, ProgramReader reader) {
        return 0;
    }
}
