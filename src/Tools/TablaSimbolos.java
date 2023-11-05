package Tools;

import Lexico.TablaTipos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class TablaSimbolos {

    public static final HashMap<String, HashMap<String, String>> tablaSimbolos = new HashMap<>();
    public static final String valorLexema = "lexema";
    // private static int identifierNumber = 0;

    private static void addTipo(String tipo, String key){
        try {
            tablaSimbolos.get(key).put("tipo", tipo);
        } catch (Exception e) {
            System.out.println("NO SE ENCONTRO LA KEY EN EL MAPA");
        }
    };

    public static void addTipoVariable(String tipo, String variable,String scope){
        System.out.println(variable + "  ES DE TIPO: " + tipo + " SCOPE: " + scope);
        String[] identificadores = variable.split(";");

        for(String identificador : identificadores){
            addTipo(tipo,identificador + scope);
        };
    };

    public static void addCadena(String cadena) {
        tablaSimbolos.put(cadena, createAttribute("tipo", TablaTipos.STRING));
    }

    public static void addClase(String key){
        System.out.println("ADDCLASE: " + key);
        tablaSimbolos.put(key, createAttribute("tipo","CLASS"));
    };
    public static void addClasePerteneciente(String key,String value_atributo ){
        String aux = "";
        try {
            if(tablaSimbolos.get(value_atributo) != null && tablaSimbolos.get(value_atributo).get("tipo") != null) {
                aux = tablaSimbolos.get(value_atributo).get("tipo"); //si nombre_clase tiene el tipo CLASS
            }
            if (aux.equals("CLASS")){
                tablaSimbolos.get(key).put("clase", value_atributo);
                System.out.println("Mi ultimo scope es una CLASEE");
            }
            else {
                String[] aux2 = key.split("@");
                String abuelo = aux2[aux2.length-2];
                System.out.println("MI ABUELO ES: " + abuelo);
                aux = tablaSimbolos.get(abuelo).get("tipo");
                if(aux.equals("CLASS")){
                    tablaSimbolos.get(key).put("clase", abuelo);
                }
            }

        } catch (Exception e) {
            System.out.println("NO ES UN METODO DE UNA CLASE, NO SE ENCONTRO EN LA TABLA DE SIMBOLOS");
        }


    }

    public static void addAtributo(String key ,String atributo, String value_atributo){
        tablaSimbolos.get(key).put(atributo,value_atributo);
    }

    public static void addFunction(String cadena){
        tablaSimbolos.put(cadena,createAttribute("tipo",TablaTipos.FUNCTION));
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

    public static boolean increaseCounter(Object key, String name) {
        key = String.valueOf(key);
        if (tablaSimbolos.containsKey(key)) {
            if (tablaSimbolos.get(key).containsKey(name)) {
                int counter = Integer.valueOf(tablaSimbolos.get(key).get(name));
                counter += 1;
                tablaSimbolos.get(key).replace(name, String.valueOf(counter));
            } else {
                tablaSimbolos.get(key).put(name, "1");
            }

            return true;
        }

        return false;

    }

    public static void decreaseCounter(Object key, String name) {
        key = String.valueOf(key);
        if (tablaSimbolos.containsKey(key))
            if (tablaSimbolos.get(key).containsKey(name)) {
                int counter = Integer.valueOf(tablaSimbolos.get(key).get(name));
                if (counter == 1) {
                    // System.out.println("Eliminado referencia de la tabla de simbolos...");
                    tablaSimbolos.remove(key);
                } else {
                    counter -= 1;
                    tablaSimbolos.get(key).replace(name, String.valueOf(counter));
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
            tablaSimbolos.put(n_lexema,createAttribute("uso","identificador"));
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

}
