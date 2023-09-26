package Lexico.AccionesSemanticas;

import Lexico.TablaTipos;
import Tokenizer.TablaSimbolos;
import Tools.Logger;
import Tools.ProgramReader;

import java.util.HashMap;

public class AsRangoDouble implements AccionSemantica {

    @Override
    public int run(char simbolo, ProgramReader reader) {
        boolean error = false;
        String auxBuffer = this.buffer.toString();
        double numero = 0.0;

        if(auxBuffer.contains("d")) {
            auxBuffer = auxBuffer.replace('d','e');
        } else{
            auxBuffer = auxBuffer.replace('D','e');
        };
        try {
            numero = Double.parseDouble(auxBuffer);
        } catch(Exception ex) {
            error = true;
        }

        if( numero > 1.7976931348623157D+308 || numero < 2.2250738585072014D-308 || error){
            Logger.logError(reader.getCurrentLine(),"Float fuera de rango");

        } else {
            if(!TablaSimbolos.tablaSimbolos.containsKey(String.valueOf(numero))){
                HashMap<String, String> auxMap = new HashMap<String,String>();
                auxMap.put("tipo", TablaTipos.DOUBLE_TYPE);

                TablaSimbolos.tablaSimbolos.put(String.valueOf(numero),auxMap);
            }

            //yylval = numero??
        }

        this.buffer.setLength(0);
        return 0;
    }
}
