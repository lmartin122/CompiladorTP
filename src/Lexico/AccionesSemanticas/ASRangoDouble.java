package Lexico.AccionesSemanticas;

import Lexico.TablaTipos;
import Sintactico.Parser;
import Tools.Logger;
import Tools.ProgramReader;
import Tools.TablaSimbolos;
import Tools.Tupla;

import java.util.HashMap;

public class ASRangoDouble implements AccionSemantica {
    /*
     * ACCION SEMANTICA 6
     */

    // Rango double positivo
    public static final double RDP_MIN = 2.2250738585072014D * -Math.pow(10, 308);
    public static final double RDP_MAX = 1.7976931348623157D * Math.pow(10, 308);

    // Rango double negativo
    public static final double RDN_MIN = -2.2250738585072014D * -Math.pow(10, 308);
    public static final double RDN_MAX = -1.7976931348623157D * Math.pow(10, 308);

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
            System.out.print(" double fuera de rango");
            Logger.logError(reader.getCurrentLine(), "Float fuera de rango");

        } else {
            if (!TablaSimbolos.tablaSimbolos.containsKey(String.valueOf(numero))) {
                System.out.print(" DOUBLE AÑADIDO");
                HashMap<String, String> auxMap = new HashMap<String, String>();
                auxMap.put("tipo", TablaTipos.DOUBLE_TYPE);

                TablaSimbolos.tablaSimbolos.put(String.valueOf(numero), auxMap);
            }
        }

        buffer.setLength(0);
        return new Tupla<>(auxBuffer, Parser.CTE_DOUBLE);
    }
}
