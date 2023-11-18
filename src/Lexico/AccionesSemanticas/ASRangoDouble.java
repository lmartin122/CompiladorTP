package Lexico.AccionesSemanticas;

import Sintactico.Parser;
import Tools.Logger;
import Tools.ProgramReader;
import Tools.TablaSimbolos;
import Tools.TablaTipos;
import Tools.Tupla;

import java.util.HashMap;

public class ASRangoDouble implements AccionSemantica {
    /*
     * ACCION SEMANTICA 6
     */

    // Rango double positivo
    public static final double RDP_MIN = 2.2250738585072014D * Math.pow(10, -308);
    public static final double RDP_MAX = 1.7976931348623157D * Math.pow(10, 308);

    @Override
    public Tupla<String, Short> run(char simbolo, ProgramReader reader) {
        boolean error = false;
        String auxBuffer = buffer.toString();
        double numero = 0.0;

        if (auxBuffer.contains("d")) {
            auxBuffer = auxBuffer.replace('d', 'e');
        } else {
            auxBuffer = auxBuffer.replace('D', 'e');
        }
        ;
        try {
            numero = Double.parseDouble(auxBuffer);
        } catch (Exception ex) {
            error = true;
        }

        if (numero > RDP_MAX || numero < RDP_MIN || error) {
            // System.out.print(" double fuera de rango");
            Logger.logWarning(reader.getCurrentLine(),
                    "El DOUBLE se excedio de rango, el mismo fue truncado al valor " + (RDP_MAX - 1) + ".");
            numero = RDP_MAX - 1;
            auxBuffer = String.valueOf(RDP_MAX - 1);
        }

        if (!TablaSimbolos.containsKey(auxBuffer)) {
            // System.out.print(" DOUBLE AÑADIDO");
            TablaSimbolos.addDouble(auxBuffer);
        }
        TablaSimbolos.increaseCounter(auxBuffer);

        reader.returnCharacter();
        buffer.setLength(0);
        return new Tupla<>(String.valueOf(auxBuffer), Parser.CTE_DOUBLE);
    }
}
