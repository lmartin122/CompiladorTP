package Lexico.AccionesSemanticas;

import Sintactico.Parser;
import Tools.Logger;
import Tools.ProgramReader;

public class ASChequeoOperador  implements AccionSemantica{
    /*
    ACCION SEMANTICA 7 y 8
    */
    @Override
    public int run(char simbolo, ProgramReader reader) {
        String auxBuffer = this.buffer.toString();

        switch (auxBuffer){
            case "-":
                if(simbolo == '='){
                    this.buffer.append(simbolo);
                    return Parser.MINUS_ASSIGN;
                } else {
                    reader.returnCharacter();
                    return 45;
                }


            case "<":
                if(simbolo == '='){
                    this.buffer.append(simbolo);
                    return Parser.LESS_THAN_OR_EQUAL_OPERATOR;
                } else {
                    reader.returnCharacter();
                    return 60;
                }


            case ">":
                if(simbolo == '='){
                    this.buffer.append(simbolo);
                    return Parser.GREATER_THAN_OR_EQUAL_OPERATOR;
                } else {
                    reader.returnCharacter();
                    return 62;
                }


            case "=":
                if(simbolo == '='){
                    this.buffer.append(simbolo);
                    return Parser.EQUAL_OPERATOR;
                } else {
                    reader.returnCharacter();
                    return 61;
                }

            case "!":
                if(simbolo == '!'){
                    this.buffer.append(simbolo);
                    return Parser.NOT_EQUAL_OPERATOR;
                } else {
                    //ERROR, no existe el "!" solo
                    Logger.logError(reader.getCurrentLine(), "Se leyo el caracter '!' ");
                    return SIMBOL_ERROR;

                }

        }
        return 0;
    }
}
