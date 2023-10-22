package GCodigo;

import java.util.ArrayList;
import java.util.Stack;

public class Tercetos {
    private ArrayList<Terceto> rules;
    private Stack<Integer> stack; // Apilar los saltos

    private class Terceto {
        private String first, second, third;

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

    public void addCondBranch(String ref) {
        Terceto t = new Terceto("CB", ref, "[-]");
        rules.add(t);
        this.stack.push(rules.size() - 1);
    }

    public void addUncondBranch() {
        Terceto t = new Terceto("UB", "[-]", "[-]");
        rules.add(t);
        stack.push(rules.size() - 1);
    }

    public void addLabel() {
        int i = rules.size();
        Terceto t = new Terceto("Label" + i, "[-]", "[-]");
        rules.add(t);
    }

    public void backPatching(int d) {

        if (stack.isEmpty())
            return;

        Integer i = stack.pop();
        rules.get(i).setThird("[" + (rules.size() + d) + "]");
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
