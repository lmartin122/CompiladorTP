package Tools;

public class TablaTipos {

    public static final String LONG_TYPE = "LONG"; // _l
    public static final String DOUBLE_TYPE = "DOUBLE";
    public static final String UINT_TYPE = "UINT"; // _ui
    public static final String STRING = "STRING"; // cadena multilinea
    public static final String FUNCTION = "FUNCTION";
    public static final String ID = "identificador";

    // public static boolean hasSameType() {

    // }

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
}