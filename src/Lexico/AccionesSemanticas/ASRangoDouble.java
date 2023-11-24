package Lexico.AccionesSemanticas;

import Sintactico.Parser;
import Tools.Logger;
import Tools.ProgramReader;
import Tools.TablaSimbolos;
import Tools.Tupla;

import java.math.BigDecimal;

public class ASRangoDouble implements AccionSemantica {
    /*
     * ACCION SEMANTICA 6
     */

    // Rango double positivo
    public static final BigDecimal RDP_MAX = BigDecimal.valueOf(1.7976931348623157D * Math.pow(10, 308));
    public static final BigDecimal RDP_MIN = BigDecimal.valueOf(2.2250738585072014D * Math.pow(10, -308));

    @Override
    public Tupla<String, Short> run(char simbolo, ProgramReader reader) {
        boolean error = false;
        String auxBuffer = buffer.toString();
        BigDecimal numero = BigDecimal.valueOf(0.0);

        if (auxBuffer.contains("d")) {
            auxBuffer = auxBuffer.replace('d', 'e');
        } else {
            auxBuffer = auxBuffer.replace('D', 'e');
        }

        try {

            numero = new BigDecimal(auxBuffer);
        } catch (Exception ex) {
            error = true;
        }
        // System.out.println("DOUBLEEE: " + numero);
        if (numero.compareTo(RDP_MAX) >= 0) {

            Logger.logWarning(reader.getCurrentLine(),
                    "El DOUBLE se excedio de rango MAXIMO , el mismo fue truncado al valor "
                            + Math.nextDown(Double.MAX_VALUE));

            auxBuffer = String.valueOf(Math.nextDown(Double.MAX_VALUE));

        } else if ((numero.compareTo(RDP_MIN) <= 0 && numero.compareTo(BigDecimal.valueOf(0)) != 0) || error) {
            Logger.logWarning(reader.getCurrentLine(),
                    "El DOUBLE se excedio de rango MINIMO , el mismo fue truncado al valor "
                            + (Math.nextUp(Double.MIN_VALUE)));
            auxBuffer = String.valueOf(Math.nextUp(Double.MIN_VALUE));
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
