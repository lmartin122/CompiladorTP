package Tools;

public class TablaTipos {

    public static final String LONG_TYPE = "LONG"; // _l
    public static final String DOUBLE_TYPE = "DOUBLE";
    public static final String UINT_TYPE = "UINT"; // _ui
    public static final String STRING = "STRING"; // cadena multilinea

    public static String checkTypeCondition(String... cond) {
        String out = "";
        String type = null;

        for (String s : cond) {
            if (s.contains("D"))
                return "Los parametros de actualizacion no pueden ser del tipo DOUBLE.";

            if (type == null) {
                if (s.contains("L"))
                    type = "L";
            } else if (!s.contains(type))
                out = "Los parametros de actualizacion deben ser del mismo tipo UINT o LONG.";
        }

        return out;
    }

    public static String negarLong(String lexema) {
        long number = 0;

        try {
            number = -Long.parseLong(lexema.replaceAll("L", ""));
        } catch (Exception ex) {
        }

        String n_lexema = String.valueOf(number) + "L";

        addTablaSimbolos(lexema, n_lexema, "L");
        // TablaSimbolos.decreaseCounter(lexema);
        return n_lexema;
    }

    public static boolean chequearRangoLongNegativo(String lexema) {
        long n_lexema = 0;

        try {
            n_lexema = -Long.parseLong(lexema.replace("L", ""));
        } catch (Exception ex) {
            System.out.println("ERROR AL CONVERTIR DOUBLE");
        }
        return n_lexema >= -2147483648L;
    };

    public static String negarDouble(String lexema) {

        String n_lexema = lexema;

        try {
            n_lexema = String.valueOf(-Double.parseDouble(lexema));
        } catch (Exception ex) {
        }

        addTablaSimbolos(lexema, n_lexema, "D");

        return n_lexema;
    }

    private static void addTablaSimbolos(String lexema, String n_lexema, String tipo) {

        if (!TablaSimbolos.containsKey(n_lexema)) {
            if (tipo == "D") { // Perdon Luis por hacer un if por tipos
                TablaSimbolos.addDouble(n_lexema);
            } else {
                TablaSimbolos.addLong(n_lexema);
            }
        } else {
            TablaSimbolos.increaseCounter(n_lexema);
        }

        TablaSimbolos.decreaseCounter(lexema);
    }

    public static String chequearRangoLong(String lexema, int p) {

        long RDN_MAX = (long) Math.pow(2, 31) - 1;
        long number = 0;

        try {
            number = Long.parseLong(lexema.replace("L", ""));
        } catch (Exception ex) {
        }
        // System.out.println("LONG: " + number);
        if (number > RDN_MAX) {
            Logger.logWarning(p,
                    "El LONG se excedio de rango, el mismo fue truncado al valor " + RDN_MAX + ".");
            String n_lexema = String.valueOf(RDN_MAX) + "L";
            addTablaSimbolos(lexema, n_lexema, "L");

            return n_lexema;
        }

        return lexema;
    }
}