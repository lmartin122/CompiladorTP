package GCodigo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;

import Tools.TablaSimbolos;

public class Scope {
    private StringBuilder ambito;
    private PropertyChangeSupport support; // El observer va a hacer el terceto, para saber cuando cambia de ambito
    public static final String SEPARATOR = "@";
    private static final String MAIN = "main";
    private static final String S_MAIN = SEPARATOR + MAIN;
    private final int LIMITED_NESTING = 2;

    private interface Lambda {
        boolean invoke(String s);

    }

    public Scope() {
        ambito = new StringBuilder(S_MAIN);
        support = new PropertyChangeSupport(this);
    }

    public static String getScopeMain() {
        return S_MAIN;
    }

    public static boolean outMain(String s) {
        return !s.contains(S_MAIN);
    }

    public boolean isDeclaredInMyScope(String ref) {
        // System.out.println(
        // ref + " en el scope " + getCurrentScope() + " ya esta declarada? "
        // + TablaSimbolos.containsKey(ref + getCurrentScope()));
        ref = ref + getCurrentScope();

        return TablaSimbolos.containsKey(ref);
    }

    private String search(String r, Lambda f) {
        StringBuilder amb = new StringBuilder(ambito);
        String toSearch = r + getCurrentScope();

        while (!outMain(toSearch) && f.invoke(toSearch)) {
            deleteLastScope(amb);
            toSearch = r + amb.toString();
        }

        if (!outMain(toSearch) && !f.invoke(toSearch))
            return toSearch;

        return null;

    }

    public String searchVar(String r) {

        return search(r,
                (e) -> !(TablaSimbolos.isID(e)));
    }

    public String searchFunc(String r) {
        return search(r,
                (e) -> !(TablaSimbolos.isFunction(e)));
    }

    public String searchClass(String r) {
        return search(r,
                (e) -> !(TablaSimbolos.isClass(e)));
    }

    public String searchInterface(String r) {
        return search(r,
                (e) -> !(TablaSimbolos.isInterface(e)));
    }

    public String searchInstance(String r) {
        return search(r,
                (e) -> !(TablaSimbolos.isInstance(e)));
    }

    public String getAmbito(String ref) {
        // System.out.println("el ambito que me viene es" + ref);
        return ref.substring(ref.indexOf(Scope.SEPARATOR));
    }

    public ArrayList<String> getAmbitos() {
        return getAmbitos(getCurrentScope());
    }

    public ArrayList<String> getAmbitos(String a) {
        String[] parts = a.split(SEPARATOR);
        ArrayList<String> result = new ArrayList<>(Arrays.asList(parts));
        result.removeIf(String::isEmpty);

        return result;
    }

    public String changeScope(String lexema) {
        TablaSimbolos.changeKey(lexema, lexema + getCurrentScope());
        return lexema + getCurrentScope();
    }

    public void stack(String ambito) {
        this.ambito.append(SEPARATOR).append(ambito);
        firePropertyChange();
    }

    public void reset(String ambito) {
        this.ambito.setLength(0);
        this.ambito.append(S_MAIN + SEPARATOR).append(ambito);
        firePropertyChange();
    }

    public void deleteLastScope(StringBuilder a) {
        int lastIndex = a.lastIndexOf(SEPARATOR);
        if (lastIndex != -1) {
            a.delete(lastIndex, a.length());
        }
    }

    public void deleteLastScope() {
        deleteLastScope(ambito);
        firePropertyChange();
    };

    public void reset() {
        this.ambito.setLength(0);
        this.ambito.append(S_MAIN);
        firePropertyChange();
    }

    public String getCurrentScope() {
        return ambito.toString();
    }

    public String getLastScope() {
        String[] aux = this.getCurrentScope().split(SEPARATOR);
        return aux[aux.length - 1];
    }

    public boolean hasPassedNesting() {
        ArrayList<String> ambitos = getAmbitos();
        ambitos.remove("main");

        if (ambitos.size() > 1) {
            // System.out.println("Estoy en hasPassed " + ambitos + " es mayor " +
            // (ambitos.size() >= LIMITED_NESTING) + " no es una c/i " +
            // !(TablaSimbolos.isClass(ambitos.get(0) + Scope.S_MAIN)
            // || TablaSimbolos.isInterface(ambitos.get(0) + Scope.S_MAIN)));

            // System.out.println(ambitos.get(0) + Scope.S_MAIN +
            // TablaSimbolos.isClass(ambitos.get(0)));

        }

        if (ambitos.size() > LIMITED_NESTING)
            return true;

        if (ambitos.size() == LIMITED_NESTING)
            if (!(TablaSimbolos.isClass(ambitos.get(0))
                    || TablaSimbolos.isInterface(ambitos.get(0))))
                return true;

        return false;
    }

    public void addObserver(PropertyChangeListener o) {
        support.addPropertyChangeListener(o);
        firePropertyChange();
    }

    public void removeObserver(PropertyChangeListener o) {
        support.removePropertyChangeListener(o);
    }

    public void firePropertyChange() {
        support.firePropertyChange("Cambio el scope", null, getCurrentScope());
    }

}
