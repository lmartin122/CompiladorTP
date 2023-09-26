package Lexico.AccionesSemanticas;

import Lexico.AnalizadorLexico;
import Lexico.PalabrasReservadasTabla;
import Tokenizer.TablaSimbolos;
import Tools.Logger;


public class ASPalabrasReservadas implements AccionSemantica {

    PalabrasReservadasTabla prs; // se lee desde un archivo

    @Override
    public int run(char simbolo, ProgramReader reader){
        String aux = this.buffer.toString();
        
        if (!prs.getPalabrasReservadas().containsKey(aux)){
            if(aux.length() > 20){
            Logger.logWarning(reader.getLine(), "Identificador truncado.");
            aux = aux.substring(0, 20);
            TablaSimbolos.addSymbol(aux); //Lo agrego truncado a la tabla de simbolos.
            }
        }
        else{
            //deberia devolver el token de la palabra reservada.
        }
        buffer.setLength(0);
    }
}