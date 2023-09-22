package Lexico;

public class inicBuffer {
    public class InicBuffer extends AccionSemantica {

        @Override
        public void run(char simbolo) {
            buffer.setLength(0);
            buffer.append(simbolo);
        }
    }
}
