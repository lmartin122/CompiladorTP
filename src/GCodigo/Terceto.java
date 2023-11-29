package GCodigo;

import java.util.ArrayList;

public class Terceto {
    private String first, second, third, type;
    private boolean flagBrackets;
    public int i;

    public static final String UNDEFINED = "-";
    public static final String LABEL = "label";
    public static final String ERROR = "ERROR";
    public static final String TYPE_TOD = "DOUBLE";

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

    public String getFirst() {
        return first;
    }

    public void setFirst(String s) {
        first = s;
    }

    public void setSecond(String s) {
        second = s;
    }

    public String getSecond() {
        return second;
    }

    public String getThird() {
        return third;
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
        return Character.isDigit(ref.charAt(0));
    }

    public int getNumber() {
        return i;
    }
}