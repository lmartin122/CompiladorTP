package Lexico.AccionesSemanticas;

import Lexico.AnalizadorLexico;
import Lexico.PalabrasReservadasTabla;
import Sintactico.Parser;
import Tools.ProgramReader;
import Tools.TablaSimbolos;
import Tools.Logger;

import java.util.HashMap;
import java.util.Map;

public class ASDevolverIdentificador implements AccionSemantica {
    /*
    ACCION SEMANTICA 3
    */



    @Override
    public int run(char simbolo, ProgramReader reader) {
        String aux = this.buffer.toString();

        if (!PalabrasReservadasTabla.p.containsKey(aux))  {
            System.out.println(" IDENTIFICADOR ENCONTRADO: " + aux);
            if (aux.length() > 20) {
                Logger.logWarning(reader.getCurrentLine(), "Identificador truncado.");
                aux = aux.substring(0, 20);
            }
            if (!TablaSimbolos.tablaSimbolos.containsKey(aux)){
                TablaSimbolos.addIdentificador(aux);
            };

            reader.returnCharacter(); //devuelvo el caracter leido de mas

            //yylval = buffer?
            this.buffer.setLength(0); // limpio el buffer
            return Parser.ID;

        } else {
            System.out.println(" PALABRA RESERVADA: " + aux);
            reader.returnCharacter(); //devuelvo el caracter leido de mas
            this.buffer.setLength(0);
            //yylval = null?
            return PalabrasReservadasTabla.p.get(aux);
        }


    }
}