package Tools;

import java.util.ArrayList;
import java.util.HashMap;

import GCodigo.Scope;

public class TablaSimbolos {

    public static final HashMap<String, HashMap<String, String>> tablaSimbolos = new HashMap<>();
    public static final String valorLexema = "lexema";
    public static final String SIN_PARAMETRO = "s/p";

    // public static final String METHOD = "METHOD";
    public static final String FUNCTION = "FUNCTION";
    public static final String ID = "ID";
    public static final String CLASS = "CLASS";
    public static final String INTERFACE = "INTERFACE";
    public static final String INSTANCE = "INSTANCE";

    public static final String TIPO = "tipo";
    public static final String REF = "atributo";
    public static final String USO = "uso";
    public static final String CONTADOR = "contador";
    public static final String USADO = "usada_r";
    public static final String PARAMETRO = "parametro";
    public static final String IMPLEMENTADO = "implementado";
    // private static int identifierNumber = 0;
    private static final String toErase = USO;

    // private static void addTipo(String tipo, String key) {
    // try {
    // tablaSimbolos.get(key).put(TIPO, tipo);
    // } catch (Exception e) {
    // System.out.println("NO SE ENCONTRO LA KEY EN EL MAPA");
    // }
    // };

    public static void addTipo(String tipo, String variable) {
        addAtributo(variable, TIPO, tipo);
    };

    public static void addRef(String ref, String variable) {
        addAtributo(variable, REF, ref);
    };

    public static void addInstancia(String uso, String variable) {
        setAtributo(variable, USO, uso);
    };

    public static void addUsoInstancia(String variable) {
        String[] identificadores = variable.split(";");

        for (String identificador : identificadores) {
            addInstancia(INSTANCE, identificador);
        }
    };

    public static void addTipoVariable(String tipo, String variable) {
        // System.out.println(variable + " ES DE TIPO: " + tipo + " SCOPE: " + scope);
        String[] identificadores = variable.split(";");

        for (String identificador : identificadores) {
            addTipo(tipo, identificador);
        }
    };

    public static void addCadena(String cadena) {
        tablaSimbolos.put(cadena, createAttribute(TIPO, TablaTipos.STRING));
    }

    public static void addClase(String key) {
        tablaSimbolos.put(key, createAttribute(USO, CLASS));
    };

    public static void addInterface(String key) {
        tablaSimbolos.put(key, createAttribute(USO, INTERFACE));
    };

    // Esto se usa?
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
            // System.out.println("NO ES UN METODO DE UNA CLASE, NO SE ENCONTRO EN LA TABLA
            // DE SIMBOLOS");
        }

    }

    public static void addAtributo(String key, String atributo, String value_atributo) {
        tablaSimbolos.get(key).put(atributo, value_atributo);
    }

    private static void setAtributo(String key, String atributo, String value_atributo) {
        // System.out.println(key + " tiene " + tablaSimbolos.get(key).toString());
        tablaSimbolos.get(key).put(atributo, value_atributo);
    }

    public static void addFunction(String cadena) {
        tablaSimbolos.put(cadena, createAttribute(USO, FUNCTION));
    }

    public static void addIdentificador(String new_symbol) {
        tablaSimbolos.put(new_symbol, createAttribute(USO, ID));
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

    public static void addParameter(String key, Object _id) {
        if (!containsKey(key))
            return;

        tablaSimbolos.get(key).put(PARAMETRO, String.valueOf(_id));
    }

    public static void addParameter(String key) {
        if (!containsKey(key))
            return;

        tablaSimbolos.get(key).put(PARAMETRO, SIN_PARAMETRO);
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
                if (counter == 0) {
                    // System.out.println("Eliminado referencia de la tabla de simbolos...");
                    tablaSimbolos.remove(key);
                } else {
                    counter -= 1;
                    tablaSimbolos.get(key).replace(CONTADOR, String.valueOf(counter));
                    int counter_final = Integer.valueOf(tablaSimbolos.get(key).get(CONTADOR));
                    if (counter_final <= 0) {
                        tablaSimbolos.remove(key);
                    }
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
            tablaSimbolos.put(n_lexema, createAttribute(USO, ID));
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

    public static void purge() {
        tablaSimbolos.entrySet()
                .removeIf(entry -> entry.getValue().containsKey(toErase) && Scope.outMain(entry.getKey()));
    }

    private static boolean hasAttribute(String k, String a) {
        if (containsKey(k)) {
            String uso = tablaSimbolos.get(k).get(USO);
            return (uso == null) ? false : uso.equals(a);
        }

        return false;
    }

    public static boolean isID(String k) {
        return hasAttribute(k, ID);
    }

    public static boolean isFunction(String k) {
        return hasAttribute(k, FUNCTION);
    }

    public static boolean isClass(String k) {
        return hasAttribute(k, CLASS);
    }

    public static boolean isInterface(String k) {
        return hasAttribute(k, INTERFACE);
    }

    public static boolean isInstance(String k) {
        return hasAttribute(k, INSTANCE);
    }

    public static String getTypeLexema(String l) {
        if (containsKey(l))
            return tablaSimbolos.get(l).get(TIPO);

        return "";
    }

    public static String getUse(String l) {
        if (containsKey(l))
            return tablaSimbolos.get(l).get(USO);

        return "";
    }

    public static String getRefAttribute(String l) {
        if (containsKey(l))
            return tablaSimbolos.get(l).get(REF);

        return "";
    }

    public static String getParameter(String k) {
        if (containsKey(k))
            return tablaSimbolos.get(k).get(PARAMETRO);

        return null;
    }

    public static void setFuncPrototype(String r) {
        if (!containsKey(r))
            return;

        tablaSimbolos.get(r).put(IMPLEMENTADO, "False");
    }

    public static void setImplemented(String r) {
        System.out.println("Set implemented a " + r);

        if (!containsKey(r))
            return;

        tablaSimbolos.get(r).put(IMPLEMENTADO, "True");
    }

    public static void setUsed(String r) {
        if (!containsKey(r))
            return;

        tablaSimbolos.get(r).put(USADO, "True");
    }

    // Pense que me iba a funcionar pero no je
    // public static ArrayList<String> getListAttributes(String lexema, String
    // scope) {
    // if (!containsKey(lexema))
    // return null;

    // ArrayList<String> out = new ArrayList<>();

    // for (String k : getTablaSimbolos()) {
    // int i = k.indexOf(Scope.SEPARATOR);
    // String splitL = k.substring(0, i);
    // String splitR = k.substring(i + 1);

    // if (splitL.contains(lexema + TablaClases.ATTRIBUTE_SEPARATOR) &&
    // splitR.equals(scope))
    // out.add(k);
    // }

    // return out;
    // }

    public static ArrayList<String> getTablaSimbolos() {
        return new ArrayList<>(tablaSimbolos.keySet());
    }

}
