package Tools;

import Lexico.TablaTipos;

import java.util.HashMap;

public class TablaSimbolos {

    public static final HashMap<String, HashMap<String, String>> tablaSimbolos = new HashMap<>();
    public static final String valorLexema = "lexema";
    // private static int identifierNumber = 0;

    public static void addCadena(String cadena) {
        tablaSimbolos.put(cadena, createAttribute("tipo", TablaTipos.STRING));
    }

    public static void addIdentificador(String new_symbol) {
        tablaSimbolos.put(new_symbol, createAttribute("uso", "identificador"));
    }

    public static void addUInteger(Object uint) {
        tablaSimbolos.put(String.valueOf(uint), createAttribute("tipo", TablaTipos.UINT_TYPE));
    }

    public static void addLong(Object _long) {
        tablaSimbolos.put(String.valueOf(_long), createAttribute("tipo", TablaTipos.LONG_TYPE));
    }

    public static void addDouble(Object _double) {
        tablaSimbolos.put(String.valueOf(_double), createAttribute("tipo", TablaTipos.DOUBLE_TYPE));
    }

    public static boolean containsKey(Object key) {
        return tablaSimbolos.containsKey(String.valueOf(key));
    }

    public static void addContador(Object key) {
        key = String.valueOf(key);
        if (tablaSimbolos.containsKey(key))
            if (!tablaSimbolos.get(key).containsKey("contador"))
                tablaSimbolos.get(key).put("contador", "1");
    }

    public static void increaseCounter(Object key) {
        key = String.valueOf(key);
        if (tablaSimbolos.containsKey(key))
            if (tablaSimbolos.get(key).containsKey("contador")) {
                int counter = Integer.valueOf(tablaSimbolos.get(key).get("contador"));
                counter += 1;
                tablaSimbolos.get(key).replace("contador", String.valueOf(counter));
            }
    }

    public static void decreaseCounter(Object key) {
        key = String.valueOf(key);
        if (tablaSimbolos.containsKey(key))
            if (tablaSimbolos.get(key).containsKey("contador")) {
                int counter = Integer.valueOf(tablaSimbolos.get(key).get("contador"));
                if (counter == 1) {
                    // System.out.println("Eliminado referencia de la tabla de simbolos...");
                    tablaSimbolos.remove(key);
                } else {
                    counter -= 1;
                    tablaSimbolos.get(key).replace("contador", String.valueOf(counter));
                }
            }
    }

    public static HashMap<String, String> createAttribute(String key, String value) {
        HashMap<String, String> out = new HashMap<>();
        out.put(key, value);
        return out;
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

    public static String printTable() {
        // System.out.println("Tabla de símbolos: ");
        String out = "";

        for (HashMap.Entry<String, HashMap<String, String>> m : tablaSimbolos.entrySet()) {
            HashMap<String, String> values = m.getValue();
            out += "Clave " + m.getKey() + ": ";
            for (HashMap.Entry<String, String> v : values.entrySet()) {
                out += "(" + v.getKey() + ": " + v.getValue() + ") ";
            }
            out += "\n";
        }

        return out;
    }

}
