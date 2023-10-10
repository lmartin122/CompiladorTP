package Tools;

import Lexico.TablaTipos;

import java.util.Map;
import java.util.HashMap;

public class TablaSimbolos {

    public static final Map<String, Map<String, String>> tablaSimbolos = new HashMap<>();
    public static final String valorLexema = "lexema";
    // private static int identifierNumber = 0;

    public static void addCadena(String cadena) {
        Map<String, String> values = new HashMap<>();
        values.put("tipo", TablaTipos.STRING);
        tablaSimbolos.put(cadena, values);
    }

    public static void addIdentificador(String new_symbol) {
        Map<String, String> values = new HashMap<>();
        values.put("uso", "identificador");
        tablaSimbolos.put(new_symbol, values);
    }

    /*
     * public static int getSymbol(String lex) {
     * for (Map.Entry<String, Map<String, String>> m : tablaSimbolos.entrySet()) {
     * String i = m.getValue().get(lex);
     * if (i == lex) {
     * int ref = m.getKey();
     * return ref;
     * }
     * }
     * return -1;
     * 
     * }
     */
    public static void printTable() {
        System.out.println("Tabla de símbolos: ");

        for (Map.Entry<String, Map<String, String>> m : tablaSimbolos.entrySet()) {
            Map<String, String> values = m.getValue();
            System.out.print(m.getKey() + ": ");
            for (Map.Entry<String, String> v : values.entrySet()) {
                System.out.print("(" + v.getKey() + ": " + v.getValue() + ") ");
            }
            System.out.println();
        }
    }

}
