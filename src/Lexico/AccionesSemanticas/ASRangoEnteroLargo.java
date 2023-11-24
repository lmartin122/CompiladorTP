package Lexico.AccionesSemanticas;

import Sintactico.Parser;
import Tools.Logger;
import Tools.ProgramReader;
import Tools.TablaSimbolos;
import Tools.Tupla;

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
            System.out.println("Error al parsear LONG");
        }

        if (!error && numero <= (long) Math.pow(2, 31)) {

            aux = aux + "L";
            if (!TablaSimbolos.tablaSimbolos.containsKey(aux)) { // si la constante no está
                TablaSimbolos.addLong(aux);
                TablaSimbolos.increaseCounter(aux);
            } else {
                TablaSimbolos.increaseCounter(aux);
            }
        } else {
            TablaSimbolos.addLong(Math.pow(2, 31) + "L");
            Logger.logWarning(reader.getCurrentLine(), "LONG fuera de rango");

        }

        buffer.setLength(0);
        return new Tupla<>(aux, Parser.CTE_LONG);
    }
}
