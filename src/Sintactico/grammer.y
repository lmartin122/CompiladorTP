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
import Tools.TablaTipos;
import Tools.TablaClases;
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
program : '{' type_declarations '}'
        | error {Logger.logError(aLexico.getProgramPosition(), "No se reconocio el programa.");} 
;

/*

>>>     DECLARATIONS

*/
type_declarations : type_declaration 
                  | type_declarations type_declaration
;

type_declaration : class_declaration {scope.reset(); scope.changeScope($1.sval);}
                 | interface_declaration {scope.reset(); scope.changeScope($1.sval);}
                 | implement_for_declaration {scope.reset(); scope.changeScope($1.sval);}
                 | block_statement {/*scope.reset();*/}
;

class_declaration : CLASS class_name class_body {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS.");  $$ = new ParserVal($2.sval);}
                  | CLASS class_name interfaces class_body {

                        $$ = new ParserVal($2.sval);
                        if(TablaClases.implementaMetodosInterfaz($2.sval,$3.sval)){
                            Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS que implementa una interface e implementa todos sus metodos.");
                        } else {
                            Logger.logError(aLexico.getProgramPosition(), "Se reconocio una CLASS que implementa una interface y NO implementa todos sus metodos.");
                        }
                    }
;

class_name : ID {scope.stack($1.sval); TablaSimbolos.addClase($1.sval); TablaClases.addClase($1.sval);}
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

field_declaration : type variable_declarators ',' {$$ = new ParserVal($1.sval + ";" + $3.sval );
                                                   Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de atributo/s.");
                                                   TablaSimbolos.addTipoVariable($1.sval, $2.sval,scope.getCurrentScope());
                                                   TablaClases.addAtributo($1.sval,$2.sval,scope.getLastScope());

                                                   }
                  | type variable_declarators {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de atributo/s.");}
;

variable_declarators : variable_declarator
                     | variable_declarators ';' variable_declarator { $$ = new ParserVal($1.sval + ";" + $3.sval );}
;

variable_declarator : variable_declarator_id
                    | variable_declarator_id '=' variable_initializer {Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
                    | variable_declarator_id error '=' variable_initializer {Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
                    | variable_declarator_id EQUAL_OPERATOR variable_initializer {Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
                    | variable_declarator_id NOT_EQUAL_OPERATOR variable_initializer {Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
                    | variable_declarator_id LESS_THAN_OR_EQUAL_OPERATOR variable_initializer {Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
                    | variable_declarator_id GREATER_THAN_OR_EQUAL_OPERATOR variable_initializer {Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
;

variable_declarator_id : ID {scope.changeScope($1.sval);}
;

variable_initializer : arithmetic_operation
;

method_declaration : method_header method_body ',' {scope.deleteLastScope();}
                   | method_header method_body 
;

method_header : result_type method_declarator {$$ = new ParserVal($2.sval);}
;

result_type : VOID 
;

method_declarator : method_name '(' formal_parameter ')'{
                    if(!scope.isDeclaredInMyScope($1.sval)){
                      Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo con p/j de parametro.");
                      $$ = new ParserVal($1.sval);
                      TablaSimbolos.addParameter($1.sval, $3.sval + scope.getCurrentScope());
                    } else 
                       Logger.logError(aLexico.getProgramPosition(), "El metodo ya esta declarado en el ambito" + scope.getCurrentScope() + " .");
                    
                  }
                  | method_name '(' ')' {
                    if(!scope.isDeclaredInMyScope($1.sval)){
                      Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo sin p/j de parametro.");
                      $$ = new ParserVal($1.sval);
                      TablaSimbolos.addParameter($1.sval);
                    } else 
                       Logger.logError(aLexico.getProgramPosition(), "El metodo ya esta declarado en el ambito" + scope.getCurrentScope() + " .");
                    
                  }
                  | method_name '(' formal_parameter error ')' { Logger.logError(aLexico.getProgramPosition(), "Solo se permite la declaracion de un unico parametro formal.");}
                  | method_name '{' error '}'{ Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar delimitado por parentesis \"(...)\"."); }
;

method_name : ID {
                if (scope.hasPassedNesting())
                  Logger.logError(aLexico.getProgramPosition(), "Solo se permite 2 niveles de anidamiento en las funciones/metodos.");
                else {
                  $$ = new ParserVal(scope.changeScope($1.sval));
                  TablaSimbolos.addFunction($$.sval);
                  TablaClases.addMetodo($1.sval,scope.getLastScope());
                  TablaSimbolos.addClasePerteneciente($$.sval,scope.getLastScope());
                  scope.stack($1.sval);
                }

              }

// Permito la creacion de multiples block en un metodo, se debe chequear que luego permita
// un nivel de anidamiento
method_body : block {setMetodoDeclarado(scope.getCurrentScope(),"true");}
            | ',' {setMetodoDeclarado(scope.getCurrentScope(),"false");} // Propotipo de metodo -> ID '(' ')' ',' sin block
;

formal_parameter : type variable_declarator_id {
                      $$ = new ParserVal($2.sval);
                   }
;

real_parameter : arithmetic_operation
;

inheritance_declaration : reference_type ',' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una herencia compuesta.");}
                        | reference_type ';' error ',' {Logger.logError(aLexico.getProgramPosition(), "No se permite herencia multiple.");}
                        | reference_type ',' error ';' {Logger.logError(aLexico.getProgramPosition(), "No se permite herencia multiple.");}
;

interfaces : IMPLEMENT interface_type_list {$$ = new ParserVal($2.sval);}
;

interface_type_list : type_name {$$ = new ParserVal($1.sval);}
                    | interface_type_list ';' type_name
                    | interface_type_list ',' type_name {Logger.logError(aLexico.getProgramPosition(), "Las interfaces deben estar separadas por ';'.");}
;

interface_declaration : INTERFACE ID interface_body {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una INTERFACE.");TablaClases.addInterface($2.sval);}
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

abstract_method_declaration : result_type method_declarator ',' {TablaClases.addInterfaz($2.sval.split("@")[0],"interfaz1");}
                            | result_type method_declarator {Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
                            | result_type method_declarator ';' {Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' no \';\'en el final de la sentencia.");}
;

implement_for_declaration : IMPL FOR reference_class ':' implement_for_body {
                                System.out.println("IMPL FOR de la clase: " + $3.sval);
                                if(!TablaClases.t.contains(TablaClases.actualImplFor)){ //si el IMPL FOR no es de una clase existente
                                    Logger.logError(aLexico.getProgramPosition(), "IMPL FOR de clase inexistente");
                                } else {
                                    Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un IMPL FOR.");
                                }
                            }
                          | IMPL FOR reference_class ':' error ',' {Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el cuerpo del metodo.");}
                          | IMPL FOR error ':' implement_for_body ',' {Logger.logError(aLexico.getProgramPosition(), "Se debe referenciar a una clase.");}
                          | IMPL FOR reference_class error ':' implement_for_body {Logger.logError(aLexico.getProgramPosition(), "Declaracion de IMPL FOR no valida, no es correcta la signatura.");}
;

implement_for_body : '{' implement_for_body_declarations '}'
                   | '(' implement_for_body_declarations ')' {Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
;

implement_for_body_declarations : implement_for_body_declaration 
                                | implement_for_body_declarations implement_for_body_declaration
;

implement_for_body_declaration : implement_for_method_declaration
;

implement_for_method_declaration : method_header implement_for_method_body {
                                    System.out.println("metodo impl for: " + $1.sval + " de la clase: " + TablaClases.actualImplFor );
                                    if(TablaClases.t.contains(TablaClases.actualImplFor)){ //si el IMPL FOR es de una clase existente
                                        System.out.println("METODO DE CLASE EXISTENTE: " + TablaClases.actualImplFor);
                                        TablaClases.cambiarMetodoADeclaradoImplFor($1.sval.split("@")[0],TablaClases.actualImplFor);
                                    }

                                  }
;

implement_for_method_body : block 
                          | ',' {Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el metodo de la clase.");}
;

/*

>>>     EXPRESSIONS

*/
assignment : left_hand_side '=' arithmetic_operation {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion."); tercetos.add("=", $1.sval, $3.sval, tercetos.typeTerceto($1.sval, $3.sval));}
           | left_hand_side MINUS_ASSIGN arithmetic_operation {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion de resta."); tercetos.add("=", $1.sval, tercetos.add("-", $1.sval, $3.sval));}
           | left_hand_side error arithmetic_operation {Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
           | left_hand_side EQUAL_OPERATOR arithmetic_operation {Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
           | left_hand_side NOT_EQUAL_OPERATOR arithmetic_operation {Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
           | left_hand_side LESS_THAN_OR_EQUAL_OPERATOR arithmetic_operation {Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
           | left_hand_side GREATER_THAN_OR_EQUAL_OPERATOR arithmetic_operation {Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
;

left_hand_side : primary
;

field_acces : primary '.' ID {
                                $$ = new ParserVal($1.sval + "." + $3.sval);
                                /*
                                esto es viejo
                                if(chequeoMetodoClase($1.sval + scope.getCurrentScope() + "-" + $3.sval) == false){ //Si no es un metodo

                                }
                                */
                             }
;
primary : reference_type
        | field_acces {$$ = new ParserVal($1.sval);}
;

equality_expression : relational_expression {$$ = new ParserVal($1.sval);}
                    | equality_expression EQUAL_OPERATOR relational_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); $$ = new ParserVal(tercetos.add("==", $1.sval, $3.sval, tercetos.typeTerceto($1.sval, $3.sval)));}
                    | equality_expression NOT_EQUAL_OPERATOR relational_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); $$ = new ParserVal(tercetos.add("!!", $1.sval, $3.sval, tercetos.typeTerceto($1.sval, $3.sval)));}
;

relational_expression : additive_expression {$$ = new ParserVal($1.sval);}
                      | relational_expression '<' additive_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); $$ = new ParserVal(tercetos.add("<", $1.sval, $3.sval));}
                      | relational_expression '>' additive_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); $$ = new ParserVal(tercetos.add(">", $1.sval, $3.sval));}
                      | relational_expression GREATER_THAN_OR_EQUAL_OPERATOR additive_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); $$ = new ParserVal(tercetos.add(">=", $1.sval, $3.sval));}
                      | relational_expression LESS_THAN_OR_EQUAL_OPERATOR additive_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); $$ = new ParserVal(tercetos.add("<=", $1.sval, $3.sval));}
;

arithmetic_operation : additive_expression {$$ = new ParserVal($1.sval);}
;

additive_expression : multiplicative_expression {$$ = new ParserVal($1.sval); Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion aritmetica.");}
                    | additive_expression '+' multiplicative_expression {$$ = new ParserVal(tercetos.add("+", $1.sval, $3.sval, tercetos.typeTerceto($1.sval, $3.sval)));}
                    | additive_expression '-' multiplicative_expression {$$ = new ParserVal(tercetos.add("-", $1.sval, $3.sval, tercetos.typeTerceto($1.sval, $3.sval)));}
; 

multiplicative_expression : unary_expression {$$ = new ParserVal($1.sval);} 
                          | multiplicative_expression '*' unary_expression {$$ = new ParserVal(tercetos.add("*", $1.sval, $3.sval, tercetos.typeTerceto($1.sval, $3.sval)));}
                          | multiplicative_expression '/' unary_expression {$$ = new ParserVal(tercetos.add("/", $1.sval, $3.sval, tercetos.typeTerceto($1.sval, $3.sval)));}
                          | multiplicative_expression '%' unary_expression {Logger.logError(aLexico.getProgramPosition(), "El operator % no es valido.");}
;

unary_expression : factor {$$ = new ParserVal($1.sval);} 
                 | reference_type {$$ = new ParserVal($1.sval);}
                 | conversion_expression {$$ = new ParserVal($1.sval);} 
                 | '(' arithmetic_operation ')' {
                    if (tercetos.hasNestingExpressions($2.sval)) Logger.logError(aLexico.getProgramPosition(), "No se permite el anidamiento de expresiones.");
                  $$ = new ParserVal($2.sval);  }  //Aca se debe chequear que sea un nivel de ()?
                 | '(' ')' {Logger.logError(aLexico.getProgramPosition(), "Termino vacio.");}
;

conversion_expression : TOD '(' arithmetic_operation ')' {
                          $$ = new ParserVal(tercetos.add("TOD", $3.sval, "-")); tercetos.TODtracking($$.sval);
                          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una conversion explicita.");}
                      | TOD '(' error ')' {Logger.logError(aLexico.getProgramPosition(), "No se puede convertir la expresion declarada.");}
                      | TOD '{' error '}' {Logger.logError(aLexico.getProgramPosition(), "El metodo TOD debe estar delimitado por parentesis \"(...)\".");}
                      | TOD '{' '}' {Logger.logError(aLexico.getProgramPosition(), "El metodo TOD debe estar delimitado por parentesis \"(...)\".");}
                      | TOD '(' ')' {Logger.logError(aLexico.getProgramPosition(), "Es necesario pasar una expresion aritmetica.");}
;

factor : CTE_DOUBLE {$$ = new ParserVal($1.sval); } 
       | CTE_UINT {$$ = new ParserVal($1.sval);} 
       | CTE_LONG {$$ = new ParserVal(chequearRangoLong($1.sval));} 
       | '-'CTE_DOUBLE {$$ = new ParserVal(negarDouble($2.sval));}
       | '-'CTE_LONG {$$ = new ParserVal(negarLong($2.sval));}
       | '-'CTE_UINT {Logger.logError(aLexico.getProgramPosition() ,"Los tipos UINT deben ser sin signo."); $$ = new ParserVal($2.sval);}
;

method_invocation : ID '(' real_parameter ')' {
                    String ref = scope.searchFunc($1.sval);
                    if (ref != null ){
                      Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, con pj de parametro.");
                      $$ = new ParserVal(ref);
                      
                      if (!tercetos.linkInvocation(ref, $3.sval))
                        Logger.logError(aLexico.getProgramPosition(), "El metodo a invocar no posee parametro formal.");
                    } else {
                      Logger.logError(aLexico.getProgramPosition(), "El metodo " + $1.sval + " no esta al alcance.");
                    }
                  }
                  | ID '(' ')' {
                    
                    String ref = scope.searchFunc($1.sval);
                    if (ref != null ){
                      Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, sin pj de parametro.");
                      $$ = new ParserVal(ref);

                      if (!tercetos.linkInvocation(ref))
                        Logger.logError(aLexico.getProgramPosition(), "El metodo a invocar posee parametro formal.");
                    } else 
                      Logger.logError(aLexico.getProgramPosition(), "El metodo " + $1.sval + " no esta al alcance.");
                    
                  }
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

reference_class : ID {$$ = new ParserVal($1.sval); TablaClases.actualImplFor = $1.sval;}

reference_type : ID {
                    String reference = scope.searchVar($1.sval);
                    if(reference == null)
                      Logger.logError(aLexico.getProgramPosition(), "La variable " + $1.sval + " no esta al alcance.");
                    else {
                      $$ = new ParserVal(reference);
                      TablaSimbolos.increaseCounter($$.sval);
                    }
               }
;

numeric_type : integral_type 
             | floating_type
;

integral_type : UINT {$$ = new ParserVal("UINT");}
              | LONG {$$ = new ParserVal("LONG");}
;

floating_type : DOUBLE {$$ = new ParserVal("DOUBLE");}
;


type_name : ID {
                  String reference = scope.searchVar($1.sval);
                  if(reference == null)
                    Logger.logError(aLexico.getProgramPosition(), "La interface " + $1.sval + " no esta al alcance.");
               }
;

/*

>>>     BLOCKS AND COMMANDS

*/
block : '{' block_statements RETURN',' '}' {
        tercetos.addReturn();
      }
      | '{' RETURN',' '}' {
        tercetos.addReturn();
      }
      | '{' block_statements '}' {Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
      | '(' block_statements RETURN',' ')' {Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...} y es necesario declarar el retorno del bloque.");}
      | '(' RETURN',' ')' {Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
      | '{' '}' {Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
      | '(' ')' {Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
;

executable_block : '{' executable_block_statements '}' 
                 | '{' '}'
                 | '{' executable_block_statements RETURN ',' '}' 
                 | '{' RETURN ',' '}'
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

local_variable_declaration : type variable_declarators {TablaSimbolos.addTipoVariable($1.sval, $2.sval,scope.getCurrentScope()); addAtributosInstanciaClase($1.sval, $2.sval + scope.getCurrentScope());}
;


statement : statement_without_trailing_substatement
          | if_then_declaration
          | if_then_else_declaration 
          | for_in_range_statement
          | function_declaration
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


if_then_declaration : IF if_then_cond if_then_body END_IF ','  {tercetos.backPatching(0); tercetos.addLabel();}
                    {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");}
                    | IF if_then_cond if_then_body error ',' {Logger.logError(aLexico.getProgramPosition(), "La sentencia de control IF debe terminar con la palabra reservada END_IF.");}
;

if_then_cond : '(' equality_expression ')' {tercetos.addCondBranch($2.sval);}
             | '(' error ')' {Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
             | '{' equality_expression '}' {Logger.logError(aLexico.getProgramPosition(), "La condicion debe estar delimitado por parentesis \"(...)\".");}
             | '(' ')'
;

if_then_body : executable_statement 
             | executable_block
             | local_variable_declaration_statement {Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF.");}
;

if_then_else_declaration : IF if_then_cond if_then_else_body END_IF ',' 
                         {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE."); tercetos.backPatching(0); tercetos.addLabel();}
; 

if_else_then_body : executable_statement {tercetos.backPatching(1); tercetos.addUncondBranch(); tercetos.addLabel();}
                  | executable_block {tercetos.backPatching(1); tercetos.addUncondBranch(); tercetos.addLabel();}
                  | local_variable_declaration_statement {Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF.");}
;

if_else_body : executable_statement
             | executable_block
             | local_variable_declaration_statement {Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF ELSE.");}
;

if_then_else_body : if_else_then_body ELSE if_else_body
;

for_in_range_statement : FOR for_in_range_initializer IN RANGE for_in_range_cond for_in_range_body {
                         Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");
                         tercetos.add("+", $2.sval, "-");
                         tercetos.backPatching();
                         tercetos.stack();
                         tercetos.add("=", $2.sval, $$.sval);
                         tercetos.backPatching();
                         tercetos.addUncondBranch(false);
                         tercetos.backPatching(0); //Agrego el salto del CB
                         tercetos.backPatching(); //Agrego el salgo del UB
                         tercetos.addLabel();
                         }
;

for_in_range_initializer : reference_type {
                               $$ = new ParserVal($1.sval);
                               tercetos.add("=", $1.sval, "-");
                               tercetos.stack($1.sval);
                               tercetos.stack();
                         }
                         | error IN {Logger.logError(aLexico.getProgramPosition(), "Error en la signatura del FOR IN RANGE.");}
;

for_in_range_cond : '(' for_init ';' for_end ';' for_update ')' {
                      String msj = TablaTipos.checkTypeCondition($2.sval, $4.sval, $6.sval);

                      if (!msj.isEmpty()) {
                        Logger.logError(aLexico.getProgramPosition(), msj);
                      } else {
                        tercetos.backPatching($2.sval);
                        String ref = tercetos.addLabel();
                        $$ = new ParserVal(tercetos.add(tercetos.getComparator($6.sval), $4.sval, "-"));
                        tercetos.backPatching();
                        tercetos.stack(ref);
                        tercetos.addCondBranch($$.sval);
                        tercetos.stack("+" + $6.sval);
                      }
                  }
                  | '(' for_init ',' for_end ',' for_update ')' {Logger.logError(aLexico.getProgramPosition(), "Las constantes de actualizacion deben estar separadas por ';'.");}
;

for_in_range_body : executable_block
                  | executable_statement
;

for_init : factor {$$ = new ParserVal($1.sval);}
;

for_update : factor {$$ = new ParserVal($1.sval);}
;

for_end : factor {$$ = new ParserVal($1.sval);}
;

function_declaration : method_header method_body_without_prototype {scope.deleteLastScope();}
;

method_body_without_prototype : block
;

print_statement : PRINT CADENA ',' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia PRINT."); tercetos.add("PRINT", $2.sval, "-");}
                | PRINT error ',' {Logger.logError(aLexico.getProgramPosition(), "Se esperaba una cadena en la sentencia PRINT.");}
                | PRINT CADENA ';' {Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
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


  private String acomodarString(String atributo, String instancia){
    String[] ambitos = instancia.split("@");

    String instancia_aux = ambitos[0] + "." + atributo;
    for(int i = 1; i < ambitos.length; i++){
        String s = ambitos[i];
        instancia_aux = instancia_aux + "@" + s;
    };
    return instancia_aux;
  };

  private void addAtributosInstanciaClase(String tipo, String instancia){
    //todavia no está implementado para que haya multiples declaraciones al estilo clase3 m;v;c;x,
    String clase = tipo.split("@")[0];
    if(TablaClases.t.contains(clase)){ //si el tipo es una clase, entonces creo todos sus atributos
      String[] instancias = instancia.split(";");
      for (String i: instancias){
        for(String s: TablaClases.a){
          if(s.contains(clase)){
            String atributo = s.split("@")[0];
            String instancia_aux = acomodarString(atributo,instancia);
            TablaSimbolos.addIdentificador(instancia_aux);
            TablaSimbolos.addAtributo(instancia_aux,"tipo", TablaClases.tipoDeAtributo(atributo,clase));
          };
        }
      };
    }


  }

  private void setMetodoDeclarado(String lexema, String declarado){
    //Acá yo voy a recibir @ambitos@funcion y la debo buscar en la tabla de simbolos como
    //funcion@ambitos
    String[] ambitos = lexema.split("@");
    String metodo = ambitos[ambitos.length-1];
    String clase = ambitos[ambitos.length-2];
    TablaClases.setMetodoDeclarado(metodo + "@" + clase, declarado);
  };

    private boolean chequeoMetodoClase(String value){

        String instancia = value.split("-")[0];
        String metodo = value.split("-")[1];
        String claseInstancia = TablaSimbolos.tablaSimbolos.get(instancia).get("tipo");
        metodo = metodo + "@main@" + claseInstancia;

        if(TablaSimbolos.tablaSimbolos.get(metodo) != null){
            System.out.println("EL METODO Y LA INSTANCIA PERTENECEN A LA MISMA CLASE");
            return true;
        } else {
            System.out.println("METODO E INSTANCIA NO PERTENECEN A LA MISMA CLASE O ES UN ATRIBUTO");
            return false;
        }
    }





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
      String n_lexema = String.valueOf(RDN_MAX) + "L";
      addTablaSimbolos(lexema, n_lexema, "L");

      return n_lexema;
    }

    return lexema;
}


private String negarLong(String lexema) {
  
    long number = 0;

    try {
        number = -Long.parseLong(lexema.replaceAll("L", ""));
    } catch (Exception ex) {}

    String n_lexema = String.valueOf(number) + "L";

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
    System.err.println("f es un directorio válido.");
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

    if ( !aLexico.hasReadWell() )
        return;

    Parser aSintactico = new Parser();
    scope = new Scope();
    tercetos = new Tercetos();
    scope.addObserver(tercetos); //Añado al terceto para avisarle cuando cambio de scope

    aSintactico.run();

    //Borrado de los identificadores que quedan sin ambito
    TablaSimbolos.purge();

    //aSintactico.dump_stacks(yylval_recognition);
    System.out.println(Logger.dumpLog());

    if (!Logger.errorsOcurred()){
      System.out.println("No se produjeron errores.\n"); //Para la parte 4, generacion de codigo maquina
      tercetos.printRules();
    } else 
      System.out.println("Se produjeron errores.\n");
    

    System.out.println(aLexico.getProgram());
}

