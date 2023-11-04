%{
package Sintactico;

import java.util.Collections;
import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

import Lexico.AnalizadorLexico;

import GCodigo.Tercetos;
import GCodigo.Scope;

import Tools.Logger;
import Tools.Tupla;
import Tools.TablaSimbolos;

%}


%token       
CLASS INTERFACE IMPLEMENT RETURN
IF ELSE END_IF FOR IN RANGE IMPL PRINT TOD
EQUAL_OPERATOR NOT_EQUAL_OPERATOR GREATER_THAN_OR_EQUAL_OPERATOR LESS_THAN_OR_EQUAL_OPERATOR MINUS_ASSIGN
VOID LONG UINT DOUBLE CADENA ID CTE_DOUBLE CTE_UINT CTE_LONG

// Precedencia 
%left '+' '-'
%left '*' '/'

%start program

%%
/*

>>>     PROGRAM

*/
program : '{' type_declarations '}' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio el programa.");}
        | error {Logger.logError(aLexico.getProgramPosition(), "No se reconocio el programa.");} 
;

/*

>>>     DECLARATIONS

*/
type_declarations : type_declaration {scope.reset(); scope.changeScope($1.sval);}
                  | type_declarations type_declaration
;

type_declaration : class_declaration {$$ = new ParserVal($1.sval);}
                 | interface_declaration {$$ = new ParserVal($1.sval);}
                 | implement_for_declaration {$$ = new ParserVal($1.sval);}
                 | block_statement
;

class_declaration : CLASS class_name class_body {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS.");  $$ = new ParserVal($2.sval); System.out.println("CLASS: " + $2.sval);}
                  | CLASS class_name interfaces class_body {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS que implementa una interface.");}
;

class_name : ID {scope.stack($1.sval); TablaSimbolos.addClase($1.sval);}
;

class_body : '{' class_body_declarations '}' 
           | '(' class_body_declarations ')' {Logger.logError(aLexico.getProgramPosition(), "La declaracion de una clase debe estar delimitado por llaves \"{...}\".");}
;

class_body_declarations : class_body_declaration 
                        | class_body_declarations class_body_declaration
;

class_body_declaration : class_member_declaration 
;

class_member_declaration : field_declaration 
                         | method_declaration
                         | inheritance_declaration
;

field_declaration : type variable_declarators ',' {$$ = new ParserVal($1.sval + ";" + $3.sval ); Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de atributo/s."); TablaSimbolos.addTipoVariable($1.sval, $2.sval,scope.getCurrentScope());}
                  | type variable_declarators {Logger.logError(aLexico.getProgramPosition(), "La sentencia debe terminar con ','.");}
;


variable_declarators : variable_declarator
                     | variable_declarators ';' variable_declarator { $$ = new ParserVal($1.sval + ";" + $3.sval );}
;

variable_declarator : variable_declarator_id
                    | variable_declarator_id '=' variable_initializer
                    | variable_declarator_id error '=' variable_initializer {Logger.logError(aLexico.getProgramPosition(), "Las declaraciones de variables se deben hacer con el caracter '='.");}
                    | variable_declarator_id EQUAL_OPERATOR variable_initializer {Logger.logError(aLexico.getProgramPosition(), "Declaracion de variable no valida. El caracter == no se permite en una declaracion.");}
                    | variable_declarator_id NOT_EQUAL_OPERATOR variable_initializer {Logger.logError(aLexico.getProgramPosition(), "Declaracion de variable no valida. El caracter !! no se permite en una declaracion.");}
                    | variable_declarator_id LESS_THAN_OR_EQUAL_OPERATOR variable_initializer {Logger.logError(aLexico.getProgramPosition(), "Declaracion de variable no valida. El caracter <= no se permite en una declaracion.");}
                    | variable_declarator_id GREATER_THAN_OR_EQUAL_OPERATOR variable_initializer {Logger.logError(aLexico.getProgramPosition(), "Declaracion de variable no valida. El caracter >= no se permite en una declaracion.");}
;

variable_declarator_id : ID {scope.changeScope($1.sval);}
;

variable_initializer : arithmetic_operation
;

method_declaration : method_header method_body {scope.deleteLastScope();}
;

method_header : result_type method_declarator
;

result_type : VOID 
;

method_declarator : method_name '(' formal_parameter ')'{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");}
                  | method_name '{' formal_parameter '}'{Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar delimitado por parentesis \"(...)\".");}
                  | method_name '(' formal_parameter error ')' {Logger.logError(aLexico.getProgramPosition(), "Solo se permite la declaracion de un unico parametro formal.");}
                  | method_name '(' ')' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");}
                  | method_name '{' '}' {Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar delimitado por parentesis \"(...)\".");}
;

method_name : ID {$$ = new ParserVal(scope.changeScope($1.sval)); TablaSimbolos.addFunction($$.sval); TablaSimbolos.addClasePerteneciente($$.sval,scope.getLastScope()); scope.stack($1.sval);}
;

// Permito la creacion de multiples block en un metodo, se debe chequear que luego permita
// un nivel de anidamiento
method_body : block {setMetodoDeclarado(scope.getCurrentScope(),"true");}
            | ',' {setMetodoDeclarado(scope.getCurrentScope(),"false");} // Propotipo de metodo -> ID '(' ')' ',' sin block
;

formal_parameter : type variable_declarator_id
;

real_parameter : arithmetic_operation
;

inheritance_declaration : reference_type ',' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una herencia compuesta.");}
                        | reference_type ';' error ',' {Logger.logError(aLexico.getProgramPosition(), "No se permite herencia multiple.");}
;

interfaces : IMPLEMENT interface_type_list
;

interface_type_list : type_name 
                    | interface_type_list ';' type_name
                    | interface_type_list ',' type_name {Logger.logError(aLexico.getProgramPosition(), "Las interfaces deben estar separadas por ';'.");}
;

interface_declaration : INTERFACE ID interface_body {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una INTERFACE.");}
;

interface_body : '{' interface_member_declaration '}'
               | '(' interface_member_declaration ')' {Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
               | '{' '}'
               | '(' ')' {Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
;

interface_member_declaration : interface_method_declaration 
                             | interface_member_declaration interface_method_declaration
;

interface_method_declaration : constant_declaration
                             | abstract_method_declaration
;

constant_declaration : type variable_declarators
;

abstract_method_declaration : result_type method_declarator ','
                            | result_type method_declarator {Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
                            | result_type method_declarator ';' {Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' no \';\'en el final de la sentencia.");}
;

implement_for_declaration : IMPL FOR reference_type ':' implement_for_body {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un IMPL FOR.");}
                          | IMPL FOR reference_type ':' error ',' {Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el cuerpo del metodo.");}
                          | IMPL FOR error ':' implement_for_body ',' {Logger.logError(aLexico.getProgramPosition(), "Se debe referenciar a una clase.");}
                          | IMPL FOR reference_type error ':' implement_for_body {Logger.logError(aLexico.getProgramPosition(), "Declaracion de IMPL FOR no valida, no es correcta la signatura.");}
                          | error ID ':' implement_for_body {Logger.logError(aLexico.getProgramPosition(), "Declaracion de IMPL FOR no valida, no es correcta la signatura.");}
;

implement_for_body : '{' implement_for_body_declarations '}'
                   | '(' implement_for_body_declarations ')' {Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
;

implement_for_body_declarations : implement_for_body_declaration 
                                | implement_for_body_declarations implement_for_body_declaration
;

implement_for_body_declaration : implement_for_method_declaration
;

implement_for_method_declaration : method_header implement_for_method_body 
;

implement_for_method_body : block 
                          | ',' {Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el metodo de la clase.");}
;

/*

>>>     EXPRESSIONS

*/
assignment : left_hand_side assignment_operator arithmetic_operation {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion."); tercetos.add($2.sval, $1.sval, $3.sval);}
;

left_hand_side : reference_type 
               | field_acces
;

field_acces : primary '.' ID 
;

primary : reference_type
        | field_acces
;

equality_expression : relational_expression {$$ = new ParserVal($1.sval);}
                    | equality_expression EQUAL_OPERATOR relational_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); $$ = new ParserVal(tercetos.add("==", $1.sval, $3.sval));}
                    | equality_expression NOT_EQUAL_OPERATOR relational_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); $$ = new ParserVal(tercetos.add("!!", $1.sval, $3.sval));}
;

relational_expression : additive_expression {$$ = new ParserVal($1.sval);}
                      | relational_expression '<' additive_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); $$ = new ParserVal(tercetos.add("<", $1.sval, $3.sval));}
                      | relational_expression '>' additive_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); $$ = new ParserVal(tercetos.add(">", $1.sval, $3.sval));}
                      | relational_expression GREATER_THAN_OR_EQUAL_OPERATOR additive_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
                      | relational_expression LESS_THAN_OR_EQUAL_OPERATOR additive_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
;

arithmetic_operation : additive_expression {$$ = new ParserVal($1.sval);}
                     | TOD '(' additive_expression ')' {$$ = new ParserVal(tercetos.add("TOD", $3.sval, "-"));}
                       {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una conversion explicita.");}
                     | TOD '(' error ')' {Logger.logError(aLexico.getProgramPosition(), "No se puede convertir la expresion declarada.");}
                     | TOD '{' error '}' {Logger.logError(aLexico.getProgramPosition(), "El metodo TOD debe estar delimitado por parentesis \"(...)\".");}
                     | TOD '{' '}' {Logger.logError(aLexico.getProgramPosition(), "El metodo TOD debe estar delimitado por parentesis \"(...)\".");}
                     | TOD '(' ')' {Logger.logError(aLexico.getProgramPosition(), "Es necesario pasar una expresion aritmetica.");}
                     | error {Logger.logError(aLexico.getProgramPosition(), "No es una expresion aritmetica valida.");}
;


additive_expression : multiplicative_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion aritmetica.");}
                    | additive_expression '+' multiplicative_expression {$$ = new ParserVal(tercetos.add("+", $1.sval, $3.sval));}
                    | additive_expression '-' multiplicative_expression {$$ = new ParserVal(tercetos.add("-", $1.sval, $3.sval));}
; 

multiplicative_expression : unary_expression {$$ = new ParserVal($1.sval);} 
                          | multiplicative_expression '*' unary_expression {$$ = new ParserVal(tercetos.add("*", $1.sval, $3.sval));}
                          | multiplicative_expression '/' unary_expression {$$ = new ParserVal(tercetos.add("/", $1.sval, $3.sval));}
                          | multiplicative_expression '%' unary_expression {Logger.logError(aLexico.getProgramPosition(), "El operator % no es valido.");}
;

unary_expression : factor 
                 | '(' arithmetic_operation ')' //Aca se debe chequear que sea un nivel de ()?
                 | '(' ')' {Logger.logError(aLexico.getProgramPosition(), "Termino vacio.");}
                 | ID
;

factor : CTE_DOUBLE
       | CTE_UINT
       | CTE_LONG {$$ = new ParserVal(chequearRangoLong($1.sval));} 
       | '-'CTE_DOUBLE {$$ = new ParserVal(negarDouble($2.sval));}
       | '-'CTE_LONG {System.out.println($2.sval); $$ = new ParserVal(negarLong($2.sval));}
       | '-'CTE_UINT {Logger.logError(aLexico.getProgramPosition() ,"Los tipos UINT deben ser sin signo."); $$ = new ParserVal($2.sval);}
;



assignment_operator : '=' {$$ = new ParserVal("=");}
                    | MINUS_ASSIGN {$$ = new ParserVal("-=");}
                    | error '=' {Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
                    | EQUAL_OPERATOR {Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
                    | NOT_EQUAL_OPERATOR {Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
                    | LESS_THAN_OR_EQUAL_OPERATOR {Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
                    | GREATER_THAN_OR_EQUAL_OPERATOR {Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
;

method_invocation : ID '(' real_parameter ')' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, con pj de parametro.");}
                  | ID '(' ')' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, sin pj de parametro.");}
                  | ID '(' real_parameter error ')' {Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
                  | field_acces '(' real_parameter ')' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo desde una clase, con pj de parametro.");}
                  | field_acces '(' ')' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo desde una clase, sin pj de parametro.");}
                  | field_acces '(' real_parameter error ')' {Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
;

/*

>>>     TYPES

*/
type : primitive_type 
     | reference_type
;

primitive_type : numeric_type
;

reference_type : ID {/*scope.changeScope($1.sval);*/}
;

numeric_type : integral_type 
             | floating_type
;

integral_type : UINT {$$ = new ParserVal("UINT");}
              | LONG {$$ = new ParserVal("LONG");}
;

floating_type : DOUBLE {$$ = new ParserVal("DOUBLE");}
;


type_name : ID
;

/*

>>>     BLOCKS AND COMMANDS

*/
block : '{' block_statements RETURN',' '}'
      | '{' block_statements '}' {Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
      | '(' block_statements RETURN',' ')' {Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...} y es necesario declarar el retorno del bloque.");}
      | '{' RETURN',' '}'
      | '(' RETURN',' ')' {Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
      | '{' '}' {Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
      | '(' ')' {Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
;

executable_block : '{' executable_block_statements '}' 
                 | '{' '}'
;

block_statements : block_statement 
                 | block_statements block_statement
;

executable_block_statements : executable_statement
                            | executable_block_statements executable_statement
;


block_statement : local_variable_declaration_statement 
                | statement
;

executable_statement : if_then_declaration
                     | if_then_else_declaration  
                     | for_in_range_statement
                     | print_statement
                     | expression_statement
                     | empty_statement
;

local_variable_declaration_statement : local_variable_declaration ',' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de variable local.");}
;

local_variable_declaration : type variable_declarators {TablaSimbolos.addTipoVariable($1.sval, $2.sval,scope.getCurrentScope());}
;


statement : statement_without_trailing_substatement
          | if_then_declaration
          | if_then_else_declaration 
          | for_in_range_statement
          | method_declaration //Es con los metodos? Esta bien declarar un metodo prototipo?
          | print_statement
;

statement_without_trailing_substatement : block 
                                        | empty_statement
                                        | expression_statement
;

expression_statement : statement_expression ','
                     | statement_expression ';' {Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
;

statement_expression : assignment 
                     | method_invocation
;

empty_statement : ','
;


if_then_declaration : IF if_then_cond if_then_body END_IF ',' 
                    {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF."); tercetos.backPatching(0); tercetos.addLabel();}
                    | IF if_then_cond if_then_body ',' {Logger.logRule(aLexico.getProgramPosition(), "Es necesario declarar el final END_IF de la sentencia IF.");}
;

if_then_cond : '(' equality_expression ')' {tercetos.addCondBranch($2.sval);}
             | '(' error ')' {Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
             | '{' equality_expression '}' {Logger.logError(aLexico.getProgramPosition(), "La condicion debe estar delimitado por parentesis \"(...)\".");}
             | '(' ')'
;

if_then_body : executable_statement {tercetos.backPatching(0);}
             | executable_block {tercetos.backPatching(0);}
             | local_variable_declaration_statement {Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF.");}
             | error END_IF {Logger.logError(aLexico.getProgramPosition(), "Cuerpo de la sentencia IF invalido.");}
             | error ',' {Logger.logError(aLexico.getProgramPosition(), "Cuerpo de la sentencia IF invalido.");}
;

if_else_then_body : executable_statement {tercetos.backPatching(1); tercetos.addUncondBranch(); tercetos.addLabel();}
                  | executable_block {tercetos.backPatching(1); tercetos.addUncondBranch(); tercetos.addLabel();}
                  | local_variable_declaration_statement {Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF.");}
                  | error END_IF {Logger.logError(aLexico.getProgramPosition(), "Cuerpo de la sentencia IF invalido.");}
                  | error ',' {Logger.logError(aLexico.getProgramPosition(), "Cuerpo de la sentencia IF invalido.");}
;

if_else_body : executable_statement
             | executable_block
             | local_variable_declaration_statement {Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF ELSE.");}
             | error END_IF {Logger.logError(aLexico.getProgramPosition(), "Cuerpo de la sentencia IF ELSE invalido.");}
             | error ',' {Logger.logError(aLexico.getProgramPosition(), "Cuerpo de la sentencia IF ELSE invalido.");}
;

if_then_else_body : if_else_then_body ELSE if_else_body
;

if_then_else_declaration : IF if_then_cond if_then_else_body END_IF ',' 
                         {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE."); tercetos.backPatching(0); tercetos.addLabel();}
                         | IF if_then_cond if_then_else_body ',' {Logger.logRule(aLexico.getProgramPosition(), "Es necesario declarar el final END_IF de la sentencia IF.");}
; 


for_in_range_statement : FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' executable_block {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");}
                       | FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' executable_statement {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");}
                       | FOR for_variable IN RANGE '(' for_init ',' for_end ',' for_update ')' executable_block {Logger.logError(aLexico.getProgramPosition(), "Las constantes de actualizacion deben estar separadas por ';'.");}
                       | FOR for_variable IN RANGE '(' for_init ',' for_end ',' for_update ')' executable_statement {Logger.logError(aLexico.getProgramPosition(), "Las constantes de actualizacion deben estar separadas por ';'.");}
                       | FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' error ',' {Logger.logError(aLexico.getProgramPosition(), "Cuerpo del FOR IN RANGE no valido.");} 
                       | FOR for_variable IN RANGE '(' error ')'  executable_block {Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");} 
                       | FOR for_variable IN RANGE '(' error ')' executable_statement {Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");}
                       | error for_variable error '(' for_init ';' for_end ';' for_update ')' executable_block {Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
                       | error for_variable error '(' for_init ';' for_end ';' for_update ')' executable_statement {Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
                       | error for_variable error '(' for_init ',' for_end ',' for_update ')' executable_block {Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
                       | error for_variable error '(' for_init ',' for_end ',' for_update ')' executable_statement {Logger.logError(aLexico.getProgramPosition(), "La signatura del FOR IN RANGE no es valida.");}
;

for_variable : reference_type
;

for_init : factor
;

for_update : factor 
;

for_end : factor 
;


print_statement : PRINT CADENA ',' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia PRINT.");}
                | PRINT CADENA error {Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
                | PRINT error ','{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una cadena.");}
                | error CADENA ',' {Logger.logError(aLexico.getProgramPosition(), "Declaracion de PRINT no valida.");}
                | PRINT '\0' {Logger.logError(aLexico.getProgramPosition(), "Se esperaba un % que cierre la cadena.");}
;
%%

private static AnalizadorLexico aLexico;
private static Tercetos tercetos;
private static Scope scope;
private static int yylval_recognition = 0;

// This method is the one where BYACC/J expects to obtain its input tokens. 
// Wrap any file/string scanning code you have in this function. This method should return <0 if there is an error, and 0 when it encounters the end of input. See the examples to clarify what we mean.
int yylex() {
  Tupla<String, Short> t = aLexico.generateToken();
  String lexema = t.getFirst();
  Short token = t.getSecond();

  if (lexema != null){
    yylval = new ParserVal(lexema);
    yylval_recognition += 1;
  }
  
  return token;
}

// This method is expected by BYACC/J, and is used to provide error messages to be directed to the channels the user desires.
void yyerror(String msg) {
    System.out.println("Error en el parser: " + msg + " in " + val_peek(0).sval);
}

// ###############################################################
// metodos auxiliares a la gramatica
// ###############################################################

private void setMetodoDeclarado(String lexema, String declarado){
    //Acá yo voy a recibir @ambitos@funcion y la debo buscar en la tabla de simbolos como
    //funcion@ambitos
    String[] ambitos = lexema.split("@");
    String resultado = ambitos[ambitos.length-1];
    for(int i = 1; i <= ambitos.length-2; i++){
        resultado += "@" + ambitos[i];
    };
    TablaSimbolos.addAtributo(resultado,"declarado",declarado);
};



private String negarDouble(String lexema) {

    String n_lexema = lexema;

    try {
      n_lexema = String.valueOf(-Double.parseDouble(lexema));
    } catch (Exception ex) {}

    addTablaSimbolos(lexema, n_lexema, "D");

    return n_lexema;
  }


private void addTablaSimbolos(String lexema, String n_lexema, String tipo) {

    if (!TablaSimbolos.containsKey(n_lexema)) {
      if (tipo == "D") { // Perdon Luis por hacer un if por tipos
        TablaSimbolos.addDouble(n_lexema);
      } else {
        TablaSimbolos.addLong(n_lexema);
      }
      TablaSimbolos.addContador(n_lexema);
    } else {
      TablaSimbolos.increaseCounter(n_lexema);
    }

    TablaSimbolos.decreaseCounter(lexema);
}

private String chequearRangoLong(String lexema) {

    long RDN_MAX = (long) Math.pow(2, 31) - 1;
    long number = 0;

    try {
      number = Long.parseLong(lexema);
    } catch (Exception ex) {}

    if (number > RDN_MAX) {
      Logger.logWarning(aLexico.getProgramPosition(),
          "El LONG se excedio de rango, el mismo fue truncado al valor " + RDN_MAX + ".");
      String n_lexema = String.valueOf(RDN_MAX);
      addTablaSimbolos(lexema, n_lexema, "L");

      return n_lexema;
    }

    return lexema;
}


private String negarLong(String lexema) {
  
    long number = 0;

    try {
        number = -Long.parseLong(lexema);
    } catch (Exception ex) {}

    String n_lexema = String.valueOf(number);

    addTablaSimbolos(lexema, n_lexema, "L");

    return n_lexema;
}

// ###############################################################
// metodos de lectura de los programadas
// ###############################################################

private static ArrayList<String> listFilesInDirectory(String path) {
  // Obtén el directorio actual
  File element = new File(System.getProperty("user.dir") + "/" + path);

  // Verifica si es un directorio o archivo válido
  if (element.isDirectory() || element.isFile()) {
    // Lista de archivos y directorios en el directorio actual
    File[] filesAndDirs = element.listFiles();
    ArrayList<String> out = new ArrayList<>();

    for (File fileOrDir : filesAndDirs) {
      out.add(fileOrDir.getName());
    }

    Collections.sort(out);

    // Itera a través de los archivos y directorios
    int i = 0;
    for (String name : out) {
      System.out.println("[" + i + "]" + ": " + name);
      i++;
    }

    return out;
  } else {
    System.err.println("No es un directorio válido.");
  }

  return null;
}

private static String generatePath() {
  ArrayList<String> directories = listFilesInDirectory("sample_programs");
  String path = "";

  if (!directories.isEmpty()) {
    Scanner scanner = new Scanner(System.in);
    int indice = -1;

    while (indice < 0) {
      System.out.print("Ingrese el numero de carpeta a acceder: ");
      String input = scanner.nextLine();
   
      try {
        indice = Integer.parseInt(input);
      } catch (Exception ex) {
        indice = -1;
      } 

      if (indice < directories.size() && indice >= 0) {
        path = directories.get(indice);
        directories = listFilesInDirectory("sample_programs" + "/" + path);
      } else {
        System.out.println("El indice no es correcto, ingrese nuevamente...");
        indice = -1;
      }

    }

    if (!directories.isEmpty()) {
      indice = -1;

      while (indice < 0) {

        System.out.print("Ingrese el numero de archivo binario a compilar: ");
        String input = scanner.nextLine();

        try {
          indice = Integer.parseInt(input);
        } catch (Exception ex) {
          indice = -1;
        } 

        if (indice < directories.size() && indice >= 0) {
          path += "/" + directories.get(Integer.parseInt(input));
        } else {
          System.out.println("El indice no es correcto, ingrese nuevamente...");
          indice = -1;
        }

      }
    }
    scanner.close();
  }
  return path;
}

public static void main (String [] args) throws IOException {
    System.out.println("Iniciando compilacion...");

    String input = generatePath();

    aLexico = new AnalizadorLexico(input);

    if ( !aLexico.hasReadWell() ) {
        return;
    }

    Parser aSintactico = new Parser();
    tercetos = new Tercetos();
    scope = new Scope();
    
    aSintactico.run();

    //aSintactico.dump_stacks(yylval_recognition);
    System.out.println(Logger.dumpLog());

    if(!Logger.errorsOcurred()){
      System.out.println("No se produjeron errores."); //Para la parte 4, generacion de codigo maquina
    }

    tercetos.printRules();
    System.out.println(aLexico.getProgram());

}


