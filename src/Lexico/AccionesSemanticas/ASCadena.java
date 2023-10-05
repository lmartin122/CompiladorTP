package Lexico.AccionesSemanticas;

import Tools.ProgramReader;
import Tools.TablaSimbolos;

public class ASCadena implements AccionSemantica{
    /*
        Accion semantica 10
     */
    @Override
    public int run(char simbolo, ProgramReader reader) {
        String aux_buffer = this.buffer.toString();
        System.out.print(" Cadena leida: '" + aux_buffer + "'");
        TablaSimbolos.addCadena(aux_buffer);
        return 0;
    }
}
