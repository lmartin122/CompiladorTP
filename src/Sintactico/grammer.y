%{
package Sintactico;
import Lexico.AnalizadorLexico;
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


// This method is the one where BYACC/J expects to obtain its input tokens. 
// Wrap any file/string scanning code you have in this function. This method should return <0 if there is an error, and 0 when it encounters the end of input. See the examples to clarify what we mean.
int yylex() {
    
    // int token =  al.generateToken(); //hay que ver como le va a pedir los tokens, pense en que lo pase por parametro pero no se deberia cambiar la signatura
    return 0;
}


// This method is expected by BYACC/J, and is used to provide error messages to be directed to the channels the user desires.
void yyerror(String msg) {
    
}




