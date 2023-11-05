package GCodigo;

import java.util.ArrayList;
import java.util.Stack;

public class Tercetos {
    private ArrayList<Terceto> rules;
    private Stack<String> stack; // Apilar los saltos

    private class Terceto {
        private String first, second, third;
        public static final String TOD = "TOD";

        public Terceto(String... values) {
            first = values[0];
            second = values[1];
            third = values[2];
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
            return "(" + first + ", " + second + ", " + third + ")";
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
            return s.equals("-");
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
        rules = new ArrayList<>();
        stack = new Stack<>();
    }

    public String add(String st, String nd, String rd) {
        Terceto t = new Terceto(st, nd, rd);
        rules.add(t);
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
        rules.add(t);
        stack();
    }

    public void addUncondBranch() {
        Terceto t = new Terceto("UB", "[-]", "[-]");
        rules.add(t);
        stack();
    }

    public void addUncondBranch(boolean stack) {
        if (stack) {
            addUncondBranch();
        } else {
            Terceto t = new Terceto("UB", "[-]", "[-]");
            rules.add(t);
        }
    }

    public String addLabel() {
        int i = rules.size();
        Terceto t = new Terceto("Label" + i, "[-]", "[-]");
        rules.add(t);
        return "[" + i + "]";
    }

    public void backPatching(int d) {

        if (stack.isEmpty())
            return;

        Integer i = Integer.valueOf(stack.pop());
        rules.get(i).setThird("[" + (rules.size() + d) + "]");
    }

    public void backPatching() {
        if (stack.isEmpty())
            return;

        String ref = stack.pop();

        if (ref.matches("[0-9]+"))
            ref = "[" + ref + "]";

        ref = ref.replace("+", "");

        rules.get((rules.size() - 1)).setThird(ref);

    }

    public void backPatching(String r) {

        if (stack.isEmpty())
            return;

        Integer i = Integer.valueOf(stack.pop());
        rules.get(i).setThird(r);
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
        Terceto t = rules.get(pos);
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

            t = rules.get(references.pop());
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

            for (Terceto rule : rules) {
                if (rule.length() > maxLengthSC) {
                    maxLengthSC = rule.length();
                }
            }

            String formatFC = "| %-" + maxLengthFC + "s.";
            String formatSC = " %-" + maxLengthSC + "s |";

            String border = "+" + "-".repeat(maxLengthFC + maxLengthSC + 4) + "+";

            System.out.println(border);
            for (int i = 0; i < rules.size(); i++) {
                String formattedRule = String.format(formatFC, i);
                formattedRule += String.format(formatSC, rules.get(i));
                System.out.println(formattedRule);
                if (i == rules.size() - 1) {
                    System.out.println(border);
                }
            }
        } else {
            System.out.println("No se generaron reglas.");
        }

    }

}
