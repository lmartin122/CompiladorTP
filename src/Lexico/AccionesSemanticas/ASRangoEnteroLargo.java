package Lexico.AccionesSemanticas;

import Lexico.TablaTipos;
import Sintactico.Parser;
import Tools.Logger;
import Tools.ProgramReader;
import Tools.TablaSimbolos;
import Tools.Tupla;

import java.util.HashMap;

public class ASRangoEnteroLargo implements AccionSemantica {

    @Override
    public Tupla<String, Short> run(char simbolo, ProgramReader reader) {
        String aux = buffer.toString();
        boolean error = false;

        Long numero = 0L;
        try {
            numero = Long.parseLong(aux);
        } catch (NumberFormatException exception) {
            error = true;
        }

        if (!error && numero <= (int) Math.pow(2, 31)) {

            if (!TablaSimbolos.tablaSimbolos.containsKey(String.valueOf(numero))) { // si la constante no está
                // System.out.print(" ENTERO LARGO AÑADIDO");

                TablaSimbolos.addLong(numero);
                TablaSimbolos.addContador(numero);
            }
        } else {
            TablaSimbolos.addLong(String.valueOf(Math.pow(2, 31)));
            // System.out.print("LONG fuera de rango, se convirtio al maximo permitido");
            Logger.logError(reader.getCurrentLine(), "LONG fuera de rango");

        }

        buffer.setLength(0);

        return new Tupla<>(aux, Parser.CTE_LONG);
    }
}
