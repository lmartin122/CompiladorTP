package Lexico.AccionesSemanticas;

import Lexico.TablaTipos;
import Sintactico.Parser;
import Tools.Logger;
import Tools.ProgramReader;
import Tools.TablaSimbolos;

import java.util.HashMap;

public class ASRangoEnteroLargo implements AccionSemantica {

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

        if (!error && numero <= (int) Math.pow(2, 31)) {

            if (!TablaSimbolos.tablaSimbolos.containsKey(String.valueOf(numero))) { // si la constante no está
                HashMap<String, String> auxMap = new HashMap<String, String>();
                auxMap.put("tipo", TablaTipos.LONG_TYPE);

                TablaSimbolos.tablaSimbolos.put(String.valueOf(numero), auxMap);
            }
        } else {
            Logger.logError(reader.getCurrentLine(), "Entero long fuera de rango");
            // Hacer algo con yylval ??
        }

        // yylval = numero ??
        this.buffer.setLength(0);
        return Parser.CTE;
    }
}
