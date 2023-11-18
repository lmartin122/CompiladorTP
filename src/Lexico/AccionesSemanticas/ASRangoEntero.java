package Lexico.AccionesSemanticas;

import Sintactico.Parser;
import Tools.Logger;
import Tools.ProgramReader;
import Tools.TablaSimbolos;
import Tools.TablaTipos;
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

            // System.out.println("IF");
            if (!TablaSimbolos.containsKey(numero)) { // si la constante no está
                // System.out.print(" ENTERO UI AÑADIDO ");

                TablaSimbolos.addUInteger(numero);
            }
        } else {
            aux = String.valueOf(Math.pow(2, 16) - 1);
            TablaSimbolos.addUInteger(aux);
            // System.out.print(" UI entero fuera de rango, se convirtio al maximo
            // permitido");
            Logger.logWarning(reader.getCurrentLine(), "Unsigned integer fuera de rango");
            // Hacer algo con yylval ??
        }
        // yylval = numero ?? jijijija
        buffer.setLength(0);
        return new Tupla<>(aux, Parser.CTE_UINT);
    };
}
