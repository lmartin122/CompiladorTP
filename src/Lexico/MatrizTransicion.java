package Lexico;

import Lexico.AccionesSemanticas.AccionSemantica;
import Tools.Tupla;

public class MatrizTransicion {

    private final Tupla<Integer, AccionSemantica>[][] matriz;

    public MatrizTransicion(int estados, int simbolos) {
        this.matriz = new Tupla[estados][simbolos];
    }

    @Override
    public String toString() {
        String out = "";
        int i = 0;
        for (Tupla<Integer, AccionSemantica>[] tuplas : matriz) {
            out += "[" + i + "] : ";
            for (Tupla<Integer, AccionSemantica> tupla : tuplas) {
                if (tupla.getAs() != null) {
                    out += "[" + tupla.getEstado() + "," + tupla.getAs().getClass().getName().split("\\.")[2] + "]";
                } else {
                    out += "[" + tupla.getEstado() + "," + "null" + "]";
                }
            }
            i++;
            out += '\n';
        }

        return out;
    }

    private Integer convertir(char simbolo) {
        return switch (simbolo) {
            case ' ' -> 0;
            case '\t' -> 1;
            case '\n', '\r' -> 2; // salto de linea
            case 'a', 'b', 'c', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x',
                    'y', 'z', 'A', 'B', 'C', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
                    'T', 'U', 'V', 'W', 'X', 'Y', 'Z' ->
                3;
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> 4;
            case '-' -> 5;
            case '+' -> 6;
            case '/' -> 7;
            case '*' -> 8;
            case '_' -> 9;
            case '%' -> 10;
            case '=' -> 11;
            case '.' -> 12;
            case ';' -> 13;
            case ',' -> 14;
            case '(' -> 15;
            case ')' -> 16;
            case '!' -> 17;
            case '{' -> 18;
            case '}' -> 19;
            case '>' -> 20;
            case '<' -> 21;
            case 'l' -> 22;
            case 'u' -> 23;
            case 'D' -> 24;
            case 'd' -> 25;
            case 'i' -> 26;
            case ':' -> 27;
            default -> 28;
        };
    }

    public void addTransicion(int estado, int simbolo, int newEstado, AccionSemantica a) {
        matriz[estado][simbolo] = new Tupla<>(newEstado, a);
    };

    public Integer nextEstado(Integer estado, char simbolo) {
        int col = convertir(simbolo);
        if (col == 2) {
            System.out.print("simbolo: " + col + "/" + "\\n" + "  estado: " + estado);
        } else {
            System.out.print("simbolo: " + col + "/" + simbolo + "  estado: " + estado);
        }
        return matriz[estado][col].getEstado();
    };

    public AccionSemantica accionSemantica(Integer estado, char simbolo) {
        int col = convertir(simbolo);
        return matriz[estado][col].getAs();
    };

}
