package Tools;

import java.util.HashMap;

import GCodigo.Scope;

public class TablaSimbolos {

    public static final HashMap<String, HashMap<String, String>> tablaSimbolos = new HashMap<>();
    public static final String valorLexema = "lexema";

    private static final HashMap<String, String> toErase = createAttribute("uso", "identificador");
    private static final String TIPO = "tipo";
    private static final String CONTADOR = "contador";
    // private static int identifierNumber = 0;

    private static void addTipo(String tipo, String key) {
        try {
            tablaSimbolos.get(key).put(TIPO, tipo);
        } catch (Exception e) {
            System.out.println("NO SE ENCONTRO LA KEY EN EL MAPA");
        }
    };

    public static void addTipoVariable(String tipo, String variable, String scope) {
        // System.out.println(variable + " ES DE TIPO: " + tipo + " SCOPE: " + scope);
        String[] identificadores = variable.split(";");

        for (String identificador : identificadores) {
            addTipo(tipo, identificador + scope);
        }
        ;
    };

    public static void addCadena(String cadena) {
        tablaSimbolos.put(cadena, createAttribute(TIPO, TablaTipos.STRING));
    }

    public static void addClase(String key) {
        System.out.println("ADDCLASE: " + key);
        tablaSimbolos.put(key, createAttribute(TIPO, "CLASS"));
    };

    public static void addClasePerteneciente(String key, String value_atributo) {
        String aux = "";
        try {
            if (tablaSimbolos.get(value_atributo) != null && tablaSimbolos.get(value_atributo).get(TIPO) != null) {
                aux = tablaSimbolos.get(value_atributo).get(TIPO); // si nombre_clase tiene el tipo CLASS
            }
            if (aux.equals("CLASS")) {
                tablaSimbolos.get(key).put("clase", value_atributo);
            } else {
                String[] aux2 = key.split("@");
                String abuelo = aux2[aux2.length - 2];
                aux = tablaSimbolos.get(abuelo).get(TIPO);
                if (aux.equals("CLASS")) {
                    tablaSimbolos.get(key).put("clase", abuelo);
                }
            }

        } catch (Exception e) {
            //System.out.println("NO ES UN METODO DE UNA CLASE, NO SE ENCONTRO EN LA TABLA DE SIMBOLOS");
        }

    }

    public static void addAtributo(String key, String atributo, String value_atributo) {
        tablaSimbolos.get(key).put(atributo, value_atributo);
    }

    public static void addFunction(String cadena) {
        tablaSimbolos.put(cadena, createAttribute(TIPO, TablaTipos.FUNCTION));
    }

    public static void addIdentificador(String new_symbol) {
        tablaSimbolos.put(new_symbol, createAttribute("uso", "identificador"));
    }

    public static void addUInteger(Object uint) {
        tablaSimbolos.put(String.valueOf(uint), createAttribute(TIPO, TablaTipos.UINT_TYPE));
    }

    public static void addLong(Object _long) {
        tablaSimbolos.put(String.valueOf(_long), createAttribute(TIPO, TablaTipos.LONG_TYPE));
    }

    public static void addDouble(Object _double) {
        tablaSimbolos.put(String.valueOf(_double), createAttribute(TIPO, TablaTipos.DOUBLE_TYPE));
    }

    public static boolean containsKey(Object key) {
        return tablaSimbolos.containsKey(String.valueOf(key));
    }

    public static void deleteKey(Object key) {
        if (containsKey(key))
            tablaSimbolos.remove(key);
    }

    public static boolean increaseCounter(Object key) {
        key = String.valueOf(key);
        if (tablaSimbolos.containsKey(key)) {
            if (tablaSimbolos.get(key).containsKey(CONTADOR)) {
                int counter = Integer.valueOf(tablaSimbolos.get(key).get(CONTADOR));
                counter += 1;
                tablaSimbolos.get(key).replace(CONTADOR, String.valueOf(counter));
            } else {
                tablaSimbolos.get(key).put(CONTADOR, "1");
            }

            return true;
        }

        return false;

    }

    public static void decreaseCounter(Object key) {
        key = String.valueOf(key);
        if (tablaSimbolos.containsKey(key))
            if (tablaSimbolos.get(key).containsKey(CONTADOR)) {
                int counter = Integer.valueOf(tablaSimbolos.get(key).get(CONTADOR));
                if (counter == 1) {
                    // System.out.println("Eliminado referencia de la tabla de simbolos...");
                    tablaSimbolos.remove(key);
                } else {
                    counter -= 1;
                    tablaSimbolos.get(key).replace(CONTADOR, String.valueOf(counter));
                }
            }
    }

    public static HashMap<String, String> createAttribute(String key, String value) {
        HashMap<String, String> out = new HashMap<>();
        out.put(key, value);
        return out;
    }

    public static void changeKey(String lexema, String n_lexema) {

        if (tablaSimbolos.containsKey(lexema)) {
            HashMap<String, String> attributes = tablaSimbolos.get(lexema);
            tablaSimbolos.remove(lexema);
            tablaSimbolos.put(n_lexema, attributes);
        } else {
            tablaSimbolos.put(n_lexema, createAttribute("uso", "identificador"));
        }
    }

    public static String printTable() {
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

    public static boolean isClass(String k) {
        if (!tablaSimbolos.containsKey(k))
            return false;

        if (tablaSimbolos.get(k).get(TIPO).equals("class"))
            return true;

        return false;
    }

    public static void purge() {
        tablaSimbolos.entrySet().removeIf(entry -> entry.getValue().equals(toErase) && Scope.outMain(entry.getKey()));
    }

    public static String getTypeLexema(String l) {
        if (containsKey(l)) {
            return tablaSimbolos.get(l).get(TIPO);
        }
        return null;
    }

}
