package GCodigo;

import java.util.ArrayList;
import java.util.List;

public class PolacaInversa {
    private ArrayList<String> rules;

    public PolacaInversa() {
        rules = new ArrayList<>();
    }

    public void add(String... values) {
        for (String value : values) {
            rules.add(value);
        }
    }

    public void printRules() {
        int maxLengthFC = 0;
        int maxLengthSC = String.valueOf(rules.size()).length();

        if (rules.isEmpty()) {

            for (String rule : rules) {
                if (rule.length() > maxLengthFC) {
                    maxLengthFC = rule.length();
                }
            }

            String formatFC = "| %-" + maxLengthFC + "s ";
            String formatSC = "| %-" + maxLengthSC + "s |";

            String border = "+" + "-".repeat(maxLengthFC + maxLengthSC + 5) + "+";

            System.out.println(border);
            for (int i = 0; i < rules.size(); i++) {
                String formattedRule = String.format(formatFC, rules.get(i));
                formattedRule += String.format(formatSC, i);
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
