package Lexico.AccionesSemanticas;

public class ASIniciarBuffer implements AccionSemantica {

    @Override
    public int run(char simbolo) {
        buffer.setLength(0);
        buffer.append(simbolo);

        return 0;
    }
}
