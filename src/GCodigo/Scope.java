package GCodigo;

import Tools.TablaSimbolos;

public class Scope {
    private StringBuilder ambito;
    private String separator; // Creo q Marcela dijo que no usemos un simbolo que reconoce nuestro automata

    public Scope() {
        ambito = new StringBuilder();
        separator = "@";
    }

    public void changeScope(String lexema) {
        TablaSimbolos.changeKey(lexema, lexema + getCurrentScope());
    }

    public void stack(String ambito) {
        this.ambito.append(separator + ambito);
    }

    public void reset(String ambito) {
        this.ambito.setLength(0);
        this.ambito.append(ambito);
    }

    public void reset() {
        this.ambito.setLength(0);
    }

    public String getCurrentScope() {
        return ambito.toString();
    }

}
