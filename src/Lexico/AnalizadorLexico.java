package Lexico;

import java.util.ArrayList;
import java.util.Map;

import Tokenizer.Token;

public class AnalizadorLexico {
    private final int ESTADOS = 19; // osea 20 estados 0 a 19
    private final int SIMBOLOS = 27 ; // el 27 seria el simbolo "otros"
    MatrizTransicion matrizTransicion;
    private ArrayList<ArrayList<Character>> program;
    private Map<String, Integer> palabrasReservadas;
    private int ln, col;


    public AnalizadorLexico(ArrayList<ArrayList<Character>> p) {
        this.program = p;
        this.ln = 0;
        this.col = 0;
    }

    private void cargarMatriz() {
        this.matrizTransicion = new MatrizTransicion(ESTADOS,SIMBOLOS);
        /*cargar cada posicion con el siguiente estado y la accion semantica a ejecutar
           ej matriz.addEstado(0,1,1,inicBuffer) => si estoy en el
                estado 0 me viene el simbolo 1(blanco) voy al estado 1 ejecutando inicBuffer
        * */

    }


    private Token generateNextToken() {
        return new Token(0);
    }

    public Token generateToken() {
        Token t = null;

        // .. codigo por aca
        t = generateNextToken(); // delegar
        // .. mas por aca

        return t;
    }

    public boolean hasFinishedTokenizer() {
        return this.ln > getNumberOfLines();
    }

    public int getNumberOfLines() {
        return this.program.size();
    }

}