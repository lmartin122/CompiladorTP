package GCodigo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;

import Tools.TablaSimbolos;

public class Scope {
    private StringBuilder ambito;
    private PropertyChangeSupport support; // El observer va a hacer el terceto, para saber cuando cambia de ambito
    private final char SEPARATOR = '@';
    private final int LIMITED_NESTING = 3;

    public Scope() {
        ambito = new StringBuilder("@main");
        support = new PropertyChangeSupport(this);
    }

    private boolean inMain() {
        return ambito.toString().equals("@main");
    }

    public static boolean outMain(String s) {
        return !s.contains("@main");
    }

    public String searchReference(String r) {
        StringBuilder amb = new StringBuilder(ambito);
        String toSearch = r + getCurrentScope();

        while (!inMain() && !TablaSimbolos.containsKey(toSearch)) {
            deleteLastScope(amb);
            toSearch = r + amb.toString();
        }

        if (TablaSimbolos.containsKey(toSearch))
            return toSearch;

        return null;

    }

    private ArrayList<String> getAmbitos() {
        String[] parts = getCurrentScope().split("@");
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
        this.ambito.append("@main@").append(ambito);
        firePropertyChange();
    }

    public void deleteLastScope(StringBuilder a) {
        int lastIndex = a.lastIndexOf("@");
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
        this.ambito.append("@main");
        firePropertyChange();
    }

    public String getCurrentScope() {
        return ambito.toString();
    }

    public String getLastScope() {
        String[] aux = this.getCurrentScope().split("@");
        return aux[aux.length - 1];
    }

    public boolean hasPassedNesting() {
        ArrayList<String> ambitos = getAmbitos();

        if (ambitos.size() < LIMITED_NESTING || TablaSimbolos.isClass(ambitos.get(1)))
            return false;

        return true;
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
