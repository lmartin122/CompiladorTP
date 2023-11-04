package GCodigo;

import Tools.TablaSimbolos;

public class Scope {
    private StringBuilder ambito;
    private String separator; // Creo q Marcela dijo que no usemos un simbolo que reconoce nuestro automata

    public Scope() {
        ambito = new StringBuilder("@main");
        separator = "@";
    }

    public String changeScope(String lexema) {
        TablaSimbolos.changeKey(lexema, lexema + getCurrentScope());
        return lexema + getCurrentScope();
    }

    public void stack(String ambito) {
        this.ambito.append(separator).append(ambito);
    }

    public void reset(String ambito) {
        this.ambito.setLength(0);
        this.ambito.append("@main@").append(ambito);
    }


    public void deleteLastScope(){
        int lastIndex = this.ambito.lastIndexOf("@");
        if (lastIndex != -1){
            this.ambito.delete(lastIndex, this.ambito.length());
        };
    };

    public void reset() {
        this.ambito.setLength(0);
        this.ambito.append("@main");
    }

    public String getCurrentScope() {
        return ambito.toString();
    }
    public String getLastScope() {
        String[] aux = this.getCurrentScope().split("@");
        return aux[aux.length-1];
    }

}
