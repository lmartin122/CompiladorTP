%{
package Sintactico;
import Lexico.AnalizadorLexico;
import java.util.Scanner;
import Tools.Logger;
%}

%token IF THEN ELSE END_IF CLASS VOID ID CTE_INT FOR PRINT CADENA INTEGER FLOAT ULONGINT ASING COMP_IGUAL COMP_MENOR_IGUAL COMP_MAYOR_IGUAL COMP_DISTINTO STRUCT CTE_ULON RETURN FUNC
%start program

%%

program : asing
;

asing   : ID ASING expr ';'
;

expr    : expr '+' expr
        | expr '-' expr
        | term
;

term    : term '*' fact 
        | term '/' fact
        | fact
;

fact    : ID
        | CTE_ULON
;

%%

private static AnalizadorLexico aLexico;

// This method is the one where BYACC/J expects to obtain its input tokens. 
// Wrap any file/string scanning code you have in this function. This method should return <0 if there is an error, and 0 when it encounters the end of input. See the examples to clarify what we mean.
int yylex() {
    
    int token = -1;
    while (!aLexico.hasFinishedTokenizer()) {
        token = aLexico.generateToken();
        if(token >= 0 ){ // deberia devolver cuando llega a un estado final
            // yyval = new ParserVal(token);
            return token;
        }

    }
    
    return token;
}


// This method is expected by BYACC/J, and is used to provide error messages to be directed to the channels the user desires.
void yyerror(String msg) {
    System.out.println(msg);
}

public static void main (String [] args){
    System.out.println("Iniciando compilacion... ");
    System.out.print("Ingrese el nombre del archivo binario a compilar: ");

    Scanner scanner = new Scanner(System.in);
    String input = scanner.nextLine();
    scanner.close();

    aLexico = new AnalizadorLexico(input);

    if ( !aLexico.hasReadWell() ) {
        return;
    }

    Parser aSintactico = new Parser();
    aSintactico.run();


    Logger.logError(1, "Este es un error.");
    Logger.logWarning(2, "Esta es una advertencia.");

    Logger.dumpLog();
    System.out.println(aLexico.getProgram());
}


