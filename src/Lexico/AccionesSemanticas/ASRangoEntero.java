package Lexico.AccionesSemanticas;

import Lexico.TablaTipos;
import Tokenizer.TablaSimbolos;
import Tools.Logger;
import Tools.ProgramReader;

import java.util.HashMap;

public class ASRangoEntero implements AccionSemantica {
    @Override
    public int run(char simbolo, ProgramReader reader) {
        String aux = this.buffer.toString();
        boolean error = false;
        int numero = 0;
        try {
            numero = Integer.parseInt(aux);
        } catch (NumberFormatException exception) {
            error = true;
        }

        if ( !error && numero <= (int) Math.pow(2,16)-1){

            if(!TablaSimbolos.tablaSimbolos.containsKey(String.valueOf(numero))){ //si la constante no está
                HashMap<String, String> auxMap = new HashMap<String,String>();
                auxMap.put("tipo", TablaTipos.UINT_TYPE);

                TablaSimbolos.tablaSimbolos.put(String.valueOf(numero),auxMap);
            }
        } else {
            Logger.logError(reader.getCurrentLine(),"Unsigned entero fuera de rango");
            //Hacer algo con yylval ??
        }

        //yylval = numero ??
        this.buffer.setLength(0);
        return 0;
    };
}
