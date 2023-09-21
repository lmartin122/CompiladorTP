package Sintactico;

import Lexico.AnalizadorLexico;
import Tokenizer.Token;

public class AnalizadorSintactico {

    public void getNextToken(AnalizadorLexico al) {
        Token t = al.generateToken();
    }
}
