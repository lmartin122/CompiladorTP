package Lexico.AccionesSemanticas;

public interface AccionSemantica {
    static final StringBuilder buffer = new StringBuilder();
    final int TOKEN = 1;
    final int ERROR = 2;

    public abstract int run(char simbolo);
}
