package Lexico.AccionesSemanticas;

import Sintactico.Parser;
import Tools.Logger;
import Tools.ProgramReader;
import Tools.TablaSimbolos;
import Tools.Tupla;

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

            if (!TablaSimbolos.containsKey(numero)) { // si la constante no está;

                TablaSimbolos.addUInteger(numero);
            }
        } else {
            TablaSimbolos.addUInteger(65535);

            Logger.logWarning(reader.getCurrentLine(), "Unsigned integer fuera de rango");

        }
        buffer.setLength(0);
        return new Tupla<>(aux, Parser.CTE_UINT);
    };
}
