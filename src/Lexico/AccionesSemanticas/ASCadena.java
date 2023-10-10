package Lexico.AccionesSemanticas;

import Sintactico.Parser;
import Tools.ProgramReader;
import Tools.TablaSimbolos;
import Tools.Tupla;

public class ASCadena implements AccionSemantica {
    /*
     * Accion semantica 10
     */
    @Override
    public Tupla<String, Short> run(char simbolo, ProgramReader reader) {
        String aux_buffer = buffer.toString();
        System.out.print(" Cadena leida: '" + aux_buffer + "'");
        TablaSimbolos.addCadena(aux_buffer);
        buffer.setLength(0);
        return new Tupla<>(aux_buffer, Parser.CADENA); // devuelve el TOKEN correspondiente a
        // la cadena
        // multilinea
    }

}
