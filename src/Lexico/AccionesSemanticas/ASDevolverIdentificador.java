package Lexico.AccionesSemanticas;

import Lexico.PalabrasReservadasTabla;
import Sintactico.Parser;
import Tools.ProgramReader;
import Tools.TablaSimbolos;
import Tools.Tupla;
import Tools.Logger;

public class ASDevolverIdentificador implements AccionSemantica {
    /*
     * ACCION SEMANTICA 3
     */

    @Override
    public Tupla<String, Short> run(char simbolo, ProgramReader reader) {
        String aux = buffer.toString();

        if (!PalabrasReservadasTabla.contienePalabra(aux)) {
            System.out.println(" IDENTIFICADOR ENCONTRADO: " + aux);
            if (aux.length() > 20) {
                Logger.logWarning(reader.getCurrentLine(), "Identificador truncado.");
                aux = aux.substring(0, 20);
            }
            if (!TablaSimbolos.tablaSimbolos.containsKey(aux)) {
                TablaSimbolos.addIdentificador(aux);
            }

            reader.returnCharacter(); // devuelvo el caracter leido de mas

            buffer.setLength(0); // limpio el buffer
            return new Tupla<>(aux, Parser.ID);

        } else {
            System.out.println(" PALABRA RESERVADA: " + aux);
            reader.returnCharacter(); // devuelvo el caracter leido de mas
            buffer.setLength(0);
            return new Tupla<>(aux, PalabrasReservadasTabla.getClave(aux));
        }

    }
}