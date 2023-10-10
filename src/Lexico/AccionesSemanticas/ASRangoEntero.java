package Lexico.AccionesSemanticas;

import Lexico.TablaTipos;
import Sintactico.Parser;
import Tools.Logger;
import Tools.ProgramReader;
import Tools.TablaSimbolos;
import Tools.Tupla;

import java.util.HashMap;

public class ASRangoEntero implements AccionSemantica {
    /*
     * ACCION SEMANTICA 4
     */
    @Override
    public Tupla<String, Short> run(char simbolo, ProgramReader reader) {
        String aux = buffer.toString();
        boolean error = false;
        int numero = 0;
        try {
            numero = Integer.parseInt(aux);
        } catch (NumberFormatException exception) {
            error = true;
        }

        if (!error && numero <= (int) Math.pow(2, 16) - 1) {

            System.out.println("IF");
            if (!TablaSimbolos.tablaSimbolos.containsKey(String.valueOf(numero))) { // si la constante no está
                System.out.print(" ENTERO UI AÑADIDO");
                HashMap<String, String> auxMap = new HashMap<String, String>();
                auxMap.put("tipo", TablaTipos.UINT_TYPE);

                TablaSimbolos.tablaSimbolos.put(String.valueOf(numero), auxMap);
            }
        } else {
            System.out.print(" UI entero fuera de rango");
            Logger.logError(reader.getCurrentLine(), "Unsigned entero fuera de rango");
            // Hacer algo con yylval ??
        }
        System.out.println("ACCION SEMANTICA 4");
        // yylval = numero ?? jijijija
        buffer.setLength(0);
        return new Tupla<>(aux, Parser.CTE_UINT);
    };
}
