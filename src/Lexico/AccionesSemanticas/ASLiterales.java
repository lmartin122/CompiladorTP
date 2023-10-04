package Lexico.AccionesSemanticas;

import Tools.ProgramReader;

public class ASLiterales implements AccionSemantica{

    @Override
    public int run(char simbolo, ProgramReader reader){

        switch (simbolo){
            case '+':
                return 7;
            case '/':
                return 8;
            case '(':
                return 16;
            case ')':
                return 17;
            case ';':
                return 14;
            case ',':
                return 15;
            case '{':
                return 50;
            case '}':
                return 51;
        }
        return 0;
    }
}
