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
        private boolean flagBrackets;
        private int i;

        public static final String UNDEFINED = "-";

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

        public String getFirst() {
            return first;
        }

        public void setSecond(String s) {
            second = s;
        }

        public String getSecond() {
            return second;
        }

        public void setThird(String s) {
            third = s;
        }

        public String getThird() {
            return third;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public String getPos() {
            return "[" + i + "]";
        }

        public void setPos(int p) {
            i = p;
        }

        public String toString() {
            String out = "(" + first + ", " + second + ", " + third + ")";
            if (type != null)
                out += " " + type;
            return out;
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

        public static boolean isNumeric(String ref) {
            return ref.matches("[0-9]+");
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
        t.setPos(size() - 1);
        return t.getPos();
    }

    // ###############################################################
    // >>> Metodos para resolver las sentencias ejecutables
    // ###############################################################

    public void stack() {
        stack.push(String.valueOf(size() - 1));
    }

    public void stack(String r) {
        stack.push(r);
    }

    public void addCondBranch(String ref) {
        add("CB", ref, "[-]");
        stack();
    }

    public void addUncondBranch() {
        add("UB", "[-]", "[-]");
        stack();
    }

    public void addUncondBranch(boolean stack) {
        if (stack) {
            addUncondBranch();
        } else {
            add("UB", "[-]", "[-]");
        }
    }

    public String addLabel() {
        return add("Label" + size(), "[-]", "[-]");
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

        if (Terceto.isNumeric(ref))
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

    public boolean linkInvocation(String func, String parameter_r) {

        String parameter_f = TablaSimbolos.getParameter(func);

        if (parameter_f.equals(TablaSimbolos.SIN_PARAMETRO))
            return false;

        String type = typeTerceto(parameter_f, parameter_r);
        String ref = add("=", parameter_f, parameter_r, type);
        addInvocation(func, ref);
        add("=", parameter_r, parameter_f, type);

        return true;
    }

    public boolean linkInvocation(String func) {

        String parameter_f = TablaSimbolos.getParameter(func);

        if (!parameter_f.equals(TablaSimbolos.SIN_PARAMETRO))
            return true;

        addInvocation(func);

        return true;
    }

    private String addInvocation(String ref, String p) {
        return add("CALL", ref, p);
    }

    private String addInvocation(String ref) {
        return add("CALL", ref, "[-]");
    }

    public String addReturn() {
        return add("RETURN", "[-]", "[-]");
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
        return !t.hasReferefence(t.getSecond() + t.getThird());
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

    // ###############################################################
    // >>> Metodos declarar los tipos referenciados como usados
    // ###############################################################

    private void declaredUsed(ArrayList<String> elements) {
        for (String factor : elements) {
            if (!Terceto.isNumeric(factor)) {
                TablaSimbolos.setUsed(factor);
            }
        }
    }

    public void declaredFactorsUsed(String ref) {

        int pos = Terceto.getRefPos(ref);
        Terceto t = get(pos);
        Stack<Integer> references = new Stack<>();

        while (!isLeaf(t) || !references.empty()) {
            for (Integer r : t.getReferences()) {
                references.push(r);
            }
            declaredUsed(t.getFactors());
            t = get(references.pop());
        }
        declaredUsed(t.getFactors());
    }

    // ###############################################################
    // >>> Metodos extras
    // ###############################################################

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

                String border = "+" + "-".repeat(totalLength) + "+";

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
