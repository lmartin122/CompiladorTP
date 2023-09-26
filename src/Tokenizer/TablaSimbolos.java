package Tokenizer;

import java.util.Map;
import java.util.HashMap;

public class TablaSimbolos {

    public static final Map<String, Map<String, String>> tablaSimbolos = new HashMap<>();
    public static final String valorLexema = "lexema";
    //private static int identifierNumber = 0;

    
    public static void addSymbol(String new_symbol) {
        Map<String, String> values = new HashMap<>();
        values.put(valorLexema, new_symbol);
        tablaSimbolos.put(valorLexema, values);
        //identifierNumber += 1;
    }
    
    /*
    public static int getSymbol(String lex) {
        for (Map.Entry<String, Map<String, String>> m : tablaSimbolos.entrySet()) {
            String i = m.getValue().get(lex);
            if (i == lex) {
                int ref = m.getKey();
                return ref;
            }
        }
        return -1;

    }
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
