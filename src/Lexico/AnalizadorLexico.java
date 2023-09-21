package Lexico;

import java.util.ArrayList;
import Tokenizer.Token;

public class AnalizadorLexico {
    private ArrayList<ArrayList<Character>> program;
    private int ln, col;

    public AnalizadorLexico(ArrayList<ArrayList<Character>> p) {
        this.program = p;
        this.ln = 0;
        this.col = 0;
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