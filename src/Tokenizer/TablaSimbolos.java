package Tokenizer;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class TablaSimbolos {

    private static final Map<Integer, Map<String, String>> tablaSimbolos = new HashMap<>();
    private static int identifierNumber = 0;

    public static void addSymbol(String new_symbol){
        Map<String, String> values = new HashMap<>();
        values.put(new_symbol, new_symbol);
        tablaSimbolos.put(identifierNumber, values);
        identifierNumber += 1;
    }

    

}
