package GCodigo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Stack;

import Tools.TablaSimbolos;

import java.util.HashMap;
import java.util.Map;

public class Tercetos implements PropertyChangeListener {
    private HashMap<String, ArrayList<Terceto>> rules;
    private Stack<String> stack; // Apilar los saltos
    private String scope;

    // Constantes
    private static final String ERROR = "error";
    private static final String TYPE_TOD = "DOUBLE";

    private class Terceto {
        private String first, second, third, type;
        public static final String TOD = "TOD";
        public static final String UNDEFINED = "-";
        private boolean flagBrackets;

        public Terceto(String... values) {
            first = values[0];
            second = values[1];
            third = values[2];
            type = values[3];
            flagBrackets = false;
        }

        public int length() {
            return toString().length();
        }

        public void setFirst(String s) {
            first = s;
        }

        public void setSecond(String s) {
            second = s;
        }

        public void setThird(String s) {
            third = s;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public String toString() {
            if (type == null)
                return "(" + first + ", " + second + ", " + third + ")";
            return "(" + first + ", " + second + ", " + third + ") " + type;
        }

        public static int getRefPos(String ref) {
            ref = ref.replace("[", "").replace("]", "");

            try {
                return Integer.parseInt(ref);
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        public static boolean hasReferefence(String s) {
            return s.contains("[") && s.contains("]");
        }

        public boolean isUndefined(String s) {
            return s.equals(UNDEFINED);
        }

        public boolean hasConversion() {
            return first.equals(TOD);
        }

        public ArrayList<String> getFactors() {
            ArrayList<String> out = new ArrayList<>();

            if (!hasReferefence(second) && !isUndefined(second))
                out.add(second);

            if (!hasReferefence(third) && !isUndefined(third))
                out.add(third);

            return out;
        }

        public boolean isFlagBrackets() {
            return flagBrackets;
        }

        public void turnOnFlagBrackets() {
            flagBrackets = true;
        }

        public void turnOffFlagBrackets() {
            flagBrackets = true;
        }

        public ArrayList<Integer> getReferences() {
            ArrayList<Integer> out = new ArrayList<>();

            if (hasReferefence(second) && !isUndefined(second))
                out.add(getRefPos(second));

            if (hasReferefence(third) && !isUndefined(third))
                out.add(getRefPos(third));

            return out;
        }

    }

    public Tercetos() {
        rules = new HashMap<>();
        stack = new Stack<>();
    }

    public Tercetos(String s) {
        this();
        scope = s;
    }

    // ###############################################################
    // >>> Metodos para el manejo de los tercetos
    // ###############################################################
    private void add(Terceto t) {
        if (rules.containsKey(scope)) {
            rules.get(scope).add(t);
        } else {
            ArrayList<Terceto> aux = new ArrayList<>();
            aux.add(t);
            rules.put(scope, aux);
        }
    }

    private Terceto get(int i) {
        ArrayList<Terceto> out = rules.get(scope);

        if (out == null || out.size() <= i)
            return null;

        return out.get(i);
    }

    private ArrayList<Terceto> get(String i) {
        if (!rules.containsKey(i))
            return null;

        return rules.get(i);
    }

    private int size() {
        return rules.get(scope).size();
    }

    public String add(String st, String nd, String rd) {
        return add(st, nd, rd, null);
    }

    public String add(String st, String nd, String rd, String type) {
        Terceto t = new Terceto(st, nd, rd, type);
        add(t);
        return "[" + (size() - 1) + "]";
    }

    // ###############################################################
    // >>> Metodos para resolver las sentencias de control
    // ###############################################################

    public void stack() {
        stack.push(String.valueOf(size() - 1));
    }

    public void stack(String r) {
        stack.push(r);
    }

    public void addCondBranch(String ref) {
        Terceto t = new Terceto("CB", ref, "[-]");
        add(t);
        stack();
    }

    public void addUncondBranch() {
        Terceto t = new Terceto("UB", "[-]", "[-]");
        add(t);
        stack();
    }

    public void addUncondBranch(boolean stack) {
        if (stack) {
            addUncondBranch();
        } else {
            Terceto t = new Terceto("UB", "[-]", "[-]");
            add(t);
        }
    }

    public String addLabel() {
        int i = size();
        Terceto t = new Terceto("Label" + i, "[-]", "[-]");
        add(t);
        return "[" + i + "]";
    }

    public void backPatching(int d) {

        if (stack.isEmpty())
            return;

        Integer i = Integer.valueOf(stack.pop());
        get(i).setThird("[" + (size() + d) + "]");
    }

    public void backPatching() {
        if (stack.isEmpty())
            return;

        String ref = stack.pop();

        if (ref.matches("[0-9]+"))
            ref = "[" + ref + "]";

        ref = ref.replace("+", "");

        get(size() - 1).setThird(ref);

    }

    public void backPatching(String r) {

        if (stack.isEmpty())
            return;

        Integer i = Integer.valueOf(stack.pop());
        get(i).setThird(r);
    }

    public String getComparator(String f) {
        return (f.contains("-")) ? "<=" : ">=";
    }

    // ###############################################################
    // >>> Metodos para indicar el tipo de un terceto
    // ###############################################################

    private void TODbacktracking(Terceto t) {

        Stack<Integer> references = new Stack<>();
        t.setType(TYPE_TOD);

        while (!isLeaf(t) || !references.empty()) {
            for (Integer r : t.getReferences()) {
                references.push(r);
            }
            t = get(references.pop());
            t.setType(TYPE_TOD);
        }
    }

    public void TODtracking(String r) {
        Terceto tod = get(Terceto.getRefPos(r)); // Obtengo el terceto que tiene el TOD i.e [tod, [1], -]

        ArrayList<Integer> references = tod.getReferences(); // Obtengo la referencia [1]

        tod.setType(TYPE_TOD);

        if (references.isEmpty()) // El terceto TOD no tiene referencias
            return;

        int ref = references.get(0);

        Terceto t_r = get(ref);
        String typeR = type(t_r); // Le pido el tipo al terceto [1]

        if (typeR.equals(ERROR)) {
            tod.setType(ERROR);
        } else { // Si no tengo error, tengo que propagar que sean dobles
            TODbacktracking(t_r);
        }

    }

    private String type(Terceto t) {
        if (t != null)
            return t.getType();
        return "";
    }

    private String type(String lexema) {
        if (Terceto.hasReferefence(lexema)) {
            return type(get(Terceto.getRefPos(lexema)));
        }

        return TablaSimbolos.getTypeLexema(lexema);
    }

    public String typeTerceto(String l, String r) {

        String typeL = type(l);
        String typeR = type(r);

        if (typeL == null) {
            return ERROR;
        }

        if (typeL.equals(typeR))
            return typeL;

        return ERROR;
    }

    private boolean isLeaf(Terceto t) {
        return !t.hasReferefence(t.toString());
    }

    public boolean hasNestingExpressions(String r) {

        Terceto t = get(Terceto.getRefPos(r));

        if (t == null)
            return false;

        t.turnOnFlagBrackets();
        Stack<Integer> references = new Stack<>();

        while (!isLeaf(t) || !references.empty()) {
            for (Integer ref : t.getReferences()) {
                references.push(ref);
            }
            t = get(references.pop());

            if (t.isFlagBrackets()) {
                return true;
            }

        }

        return false;
    }

    @Override
    public void propertyChange(PropertyChangeEvent arg0) {
        setScope((String) arg0.getNewValue());
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }

    public void printRules() {
        int maxLengthFC = String.valueOf(rules.size()).length();
        int maxLengthSC = 0;

        if (!rules.isEmpty()) {
            System.out.println(">>>    LISTA DE TERCETOS");

            for (ArrayList<Terceto> t : rules.values()) {
                for (Terceto rule : t) {
                    if (rule.length() > maxLengthSC)
                        maxLengthSC = rule.length();
                }
            }

            for (Map.Entry<String, ArrayList<Terceto>> func : rules.entrySet()) {

                String title = "Función " + func.getKey();
                ArrayList<Terceto> rulesFunc = func.getValue();

                String formatFC = "| %-" + maxLengthFC + "s.";
                String formatSC = " %-" + maxLengthSC + "s |";

                int titleLength = title.length();
                int totalLength = maxLengthFC + maxLengthSC + 4;
                int leftPadding = (totalLength - titleLength) / 2;
                int rightPadding = totalLength - titleLength - leftPadding;

                String border = "+" + "-".repeat(maxLengthFC + maxLengthSC + 4) + "+";

                System.out.println(border);
                System.out.println("|" + " ".repeat(leftPadding) + title + " ".repeat(rightPadding) + "|");
                System.out.println(border);
                for (int i = 0; i < rulesFunc.size(); i++) {
                    String formattedRule = String.format(formatFC, i);
                    formattedRule += String.format(formatSC, rulesFunc.get(i));
                    System.out.println(formattedRule);
                    if (i == rulesFunc.size() - 1) {
                        System.out.println(border);
                    }
                }
            }
        } else {
            System.out.println("No se generaron reglas.");
        }

    }

}
