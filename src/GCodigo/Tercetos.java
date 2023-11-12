package GCodigo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Stack;

import java.util.HashMap;
import java.util.Map;

public class Tercetos implements PropertyChangeListener {
    private HashMap<String, ArrayList<Terceto>> rules;
    private Stack<String> stack; // Apilar los saltos
    private String scope;

    private class Terceto {
        private String first, second, third, type;
        public static final String TOD = "TOD";
        public static final String UNDEFINED = "-";

        public Terceto(String... values) {
            first = values[0];
            second = values[1];
            third = values[2];
            if (values.length > 3) {
                type = values[3];
            }
            type = null;
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

        public boolean hasReferefence(String s) {
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

        if (out.size() <= i)
            return null;

        return out.get(i);
    }

    public String add(String st, String nd, String rd) {
        Terceto t = new Terceto(st, nd, rd);
        System.out.println("En el add tengo el scope " + scope);
        add(t);
        return "[" + (rules.size() - 1) + "]";
    }

    public void stack() {
        stack.push(String.valueOf(rules.size() - 1));
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
        int i = rules.size();
        Terceto t = new Terceto("Label" + i, "[-]", "[-]");
        add(t);
        return "[" + i + "]";
    }

    public void backPatching(int d) {

        if (stack.isEmpty())
            return;

        Integer i = Integer.valueOf(stack.pop());
        get(i).setThird("[" + (rules.size() + d) + "]");
    }

    public void backPatching() {
        if (stack.isEmpty())
            return;

        String ref = stack.pop();

        if (ref.matches("[0-9]+"))
            ref = "[" + ref + "]";

        ref = ref.replace("+", "");

        get((rules.size() - 1)).setThird(ref);

    }

    public void backPatching(String r) {

        if (stack.isEmpty())
            return;

        Integer i = Integer.valueOf(stack.pop());
        get(i).setThird(r);
    }

    private boolean isLeaf(Terceto t) {
        return !t.hasReferefence(t.toString());
    }

    public String getComparator(String f) {
        return (f.contains("-")) ? "<=" : ">=";
    }

    public ArrayList<String> getFactors(String ref) {
        if (rules.isEmpty())
            return new ArrayList<>();

        ArrayList<String> out = new ArrayList<>();

        int pos = Terceto.getRefPos(ref);
        boolean flagTOD = false;
        Terceto t = get(pos);
        Stack<Integer> references = new Stack<>();

        while (!isLeaf(t) || !references.empty()) {
            for (Integer r : t.getReferences()) {
                references.push(r);
            }

            if (t.hasConversion()) {
                flagTOD = true;
                out.add(Terceto.TOD);
            }

            out.addAll(t.getFactors());

            if (flagTOD && isLeaf(t)) {
                out.add(")");
                flagTOD = false;
            }

            t = get(references.pop());
        }

        out.addAll(t.getFactors());

        if (flagTOD)
            out.add(")");

        return out;
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

    @Override
    public void propertyChange(PropertyChangeEvent arg0) {
        setScope((String) arg0.getNewValue());
    }

    public void setScope(String scope) {
        System.out.println("Cambio el scope " + scope);
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }
}
