%{
import java.util.Collections;
import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

import Lexico.AnalizadorLexico;

import GCodigo.Tercetos;
import GCodigo.Scope;

import GWebAssembly.GeneradorAssembler;

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
        | '{' '}'
        |
        | '(' ')' {Logger.logError(aLexico.getProgramPosition(), "Un programa debe estar delimitado por llaves '{}'.");} 
        | error {Logger.logError(aLexico.getProgramPosition(), "No se reconocio el programa.");} 
;

/*

>>>     DECLARATIONS

*/
type_declarations : type_declaration 
                  | type_declarations type_declaration
;

type_declaration : class_declaration {scope.reset(); if (!$1.sval.isEmpty()) scope.changeScope($1.sval);}
                 | interface_declaration {scope.reset(); if (!$1.sval.isEmpty()) scope.changeScope($1.sval);}
                 | implement_for_declaration {scope.reset();}
                 | block_statement {scope.reset();}
;

class_declaration : CLASS class_name class_body {
                        $$ = $2;
                        if (!$2.sval.isEmpty()){
                          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una clase.");

                          String error = TablaClases.chequeoAtributoSobreescrito($2.sval);
                          if (error != null) 
                            Logger.logError(aLexico.getProgramPosition(), error);
                        }
                    }
                  | CLASS class_name interfaces class_body {
                        $$ = $2;
                        if (!$2.sval.isEmpty()){
                          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS.");

                          String error = TablaClases.chequeoAtributoSobreescrito($2.sval);
                          if (error != null) 
                            Logger.logError(aLexico.getProgramPosition(), error);
                          if (!$3.sval.isEmpty()) {
                            String msj = TablaClases.implementaMetodosInterfaz($2.sval,$3.sval);
                            if (msj.isEmpty())
                              Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS que implementa una interface e implementa todos sus metodos.");
                            else
                              Logger.logError(aLexico.getProgramPosition(), msj);
                          }

                        }            
                    }
;

class_name : ID {
              if(!scope.isDeclaredInMyScope($1.sval)){
                TablaSimbolos.addClase($1.sval); 
                TablaClases.addClase($1.sval);
                $$ = new ParserVal($1.sval);
              } else {
                Logger.logError(aLexico.getProgramPosition(), "La clase " + $1.sval + " ya esta declarada en el ambito" + scope.getCurrentScope() + ".");
                $$ = new ParserVal("");
              }
                scope.stack($1.sval); 
            }
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

field_declaration : type variable_declarators ',' {
                      System.out.println("Las variables son las siguientes, estoy en gramatica " + $2.sval);
                      if (!($1.sval.isEmpty() || $2.sval.isEmpty())) {
                        ArrayList<String> ambitos = scope.getAmbitos($2.sval);
                        String _attributes = ambitos.get(0);
                        String _class = "";
                        if (ambitos.size() >= 2){
                            _class = ambitos.get(2);
                        }

                        Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de atributo/s.");
                        TablaSimbolos.addTipoVariable($1.sval, $2.sval);
                        
                        if (TablaSimbolos.isClass($1.sval + Scope.getScopeMain())) {
                            TablaClases.addAtributos($1.sval, _attributes, _class);
                            TablaClases.addInstancia($1.sval, $2.sval);
                            TablaSimbolos.addUsoInstancia($2.sval);
                        }
                        else {
                            TablaSimbolos.addUsedVariables($2.sval);
                            TablaClases.addAtributos(_attributes, _class);
                        }
                      }
                   }
                  | type variable_declarators {Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
;

variable_declarators : variable_declarator 
                     | variable_declarators ';' variable_declarator {
                        if (!($1.sval.isEmpty() || $3.sval.isEmpty()))
                          $$ = new ParserVal($1.sval.substring(0, $1.sval.indexOf(Scope.SEPARATOR)) + ";" + $3.sval);
                        else {
                          if ($1.sval.isEmpty())
                            $$ = new ParserVal($3.sval);
                          else 
                            $$ = new ParserVal($1.sval);
                        }
                     }
;

// ;

variable_declarator : variable_declarator_id
                    | variable_declarator_id '=' variable_initializer {Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
                    | variable_declarator_id error '=' variable_initializer {Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
                    | variable_declarator_id EQUAL_OPERATOR variable_initializer {Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
                    | variable_declarator_id NOT_EQUAL_OPERATOR variable_initializer {Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
                    | variable_declarator_id LESS_THAN_OR_EQUAL_OPERATOR variable_initializer {Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
                    | variable_declarator_id GREATER_THAN_OR_EQUAL_OPERATOR variable_initializer {Logger.logError(aLexico.getProgramPosition(), "No esta permitida la inicialización en la declaracion de variables.");}
;

variable_declarator_id : ID {
                          if (!scope.isDeclaredInMyScope($1.sval)) {
                              $$ = new ParserVal(scope.changeScope($1.sval));
                          }
                          else {
                              Logger.logError(aLexico.getProgramPosition() - 1, "La variable " + $1.sval + " ya esta declarado en el ambito " + scope.getCurrentScope() + ".");
                              $$ = new ParserVal("");
                          }
                        }
;

variable_initializer : arithmetic_operation
;

method_declaration : method_header method_body ',' {
                      if (!$1.sval.isEmpty()) {
                        ArrayList<String> ambitos = scope.getAmbitos($1.sval);
                        if (ambitos.size() > 2) {
                          String _method = ambitos.get(0); 
                          String _class = ambitos.get(2);

                            if ($2.sval.isEmpty()) {
                              TablaClases.addMetodoIMPL(_method, _class);
                              TablaSimbolos.setFuncPrototype($1.sval);
                            } else {
                              TablaClases.addMetodo(_method, _class);
                              TablaSimbolos.setImplemented($1.sval);
                            }
                        } else { 
                            Logger.logError(aLexico.getProgramPosition(), "Hay un error en la declaracion del metodo.");
                        }
                      scope.deleteLastScope();
                      }
                   }
                   | method_header method_body  {
                      if (!$1.sval.isEmpty()) {
                        ArrayList<String> ambitos = scope.getAmbitos($1.sval);
                        
                        if (ambitos.size() > 2) {
                          String _method = ambitos.get(0); 
                          String _class = ambitos.get(2);

                            if ($2.sval.isEmpty()) {
                              TablaClases.addMetodoIMPL(_method, _class);
                              TablaSimbolos.setFuncPrototype($1.sval);
                            } else {
                              TablaClases.addMetodo(_method, _class);
                              TablaSimbolos.setImplemented($1.sval);
                            }
                        } else { 
                            Logger.logError(aLexico.getProgramPosition(), "Hay un error en la declaracion del metodo.");
                        }
                      scope.deleteLastScope();
                      }
                   }
;

method_header : result_type method_declarator {$$ = $2;}
              | type method_declarator {Logger.logError(aLexico.getProgramPosition(), "No se permite retornar un tipo, el retorno debe ser VOID."); $$ = new ParserVal("");}
;

result_type : VOID
;

method_declarator : method_name '(' formal_parameter ')' {
                      String ref = $1.sval;
                      String par = $3.sval;
                      if (!ref.isEmpty() && !par.isEmpty()) {
                        $$ = new ParserVal(ref);
                        TablaSimbolos.addParameter(ref, par);
                      } else {
                        $$ = new ParserVal("");
                      }
                      
                  }
                  | method_name '(' ')' {
                      String ref = $1.sval;
                      if (!ref.isEmpty()) {
                        Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo sin p/j de parametro.");
                        $$ = new ParserVal(ref);
                        TablaSimbolos.addParameter(ref);
                      } else {
                        $$ = $1;
                      } 
                      
                  }
                  | method_name '(' formal_parameter error ')' { Logger.logError(aLexico.getProgramPosition(), "Solo se permite la declaracion de un unico parametro formal.");}
                  | method_name '{' error '}'{ Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar delimitado por parentesis \"(...)\"."); }
;

method_name : ID {
                if(!scope.isDeclaredInMyScope($1.sval)) {
                  $$ = new ParserVal($1.sval);
                  if (scope.hasPassedNesting()) {
                    Logger.logError(aLexico.getProgramPosition(), "Solo se permite 1 nivel de anidamiento, el metodo/funcion " + $1.sval + " no cumple con esto.");
                    $$ = new ParserVal("");
                  } else {
                    $$ = new ParserVal(scope.changeScope($1.sval));
                    TablaSimbolos.addFunction($$.sval);
                    scope.stack($1.sval);
                  }
                } else {
                  Logger.logError(aLexico.getProgramPosition(), "El metodo ya esta declarado en el ambito" + scope.getCurrentScope() + ".");
                  $$ = new ParserVal("");
                  }                 
              }

// Permito la creacion de multiples block en un metodo, se debe chequear que luego permita
// un nivel de anidamiento
method_body : block
            | ',' {$$ = new ParserVal("");}// Propotipo de metodo -> ID '(' ')' ',' sin block
;



formal_parameter : type variable_declarator_id {
                      if (TablaSimbolos.isClass($1.sval + Scope.getScopeMain())) 
                        Logger.logError(aLexico.getProgramPosition(), "No se permite que un parametro formal sea del tipo de una clase.");
                      
                      if (!$2.sval.isEmpty()) {
                          $$ = new ParserVal($2.sval);
                          TablaSimbolos.addTipoVariable($1.sval, $2.sval);
                      } else $$ = new ParserVal(""); 
                  }
;

real_parameter : arithmetic_operation
;

inheritance_declaration : class_type ',' {
                            Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una herencia compuesta.");

                            if (!$1.sval.isEmpty()) { //si existe la clase a la cual quiere heredar
                                ArrayList<String> ambitos = scope.getAmbitos();
                                String _parentClass = $1.sval; 
                                String _class = ambitos.get(1);
                                if (!TablaClases.tieneHerencia(_class)){
                                    TablaClases.addHerencia(_class, _parentClass);
                                } else {
                                    Logger.logError(aLexico.getProgramPosition(), "No se permite herencia multiple");
                                }
                                Logger.logRule(aLexico.getProgramPosition(), "La clase " + _class + " hereda de " + _parentClass + ".");
                            } else {
                                Logger.logError(aLexico.getProgramPosition(), "La clase a la cual se quiere heredar no existe");
                            }
                        }
                        | class_type ';' error ',' {Logger.logError(aLexico.getProgramPosition(), "No se permite herencia multiple.");}
                        | class_type ',' error ';' {Logger.logError(aLexico.getProgramPosition(), "No se permite herencia multiple.");}
;

interfaces : IMPLEMENT interface_type_list {$$ = new ParserVal($2.sval); if ($2.sval.contains(";")) Logger.logError(aLexico.getProgramPosition(), "No se permite implementar multiples interfaces.");}
;

interface_type_list : reference_interface
                    | interface_type_list ';' reference_interface {$$ = new ParserVal($1.sval + ";" + $3.sval);}
                    | interface_type_list ',' reference_interface {Logger.logError(aLexico.getProgramPosition(), "Las interfaces deben estar separadas por ';'.");}
;

interface_declaration : INTERFACE interface_name interface_body {$$ = $2;}
;

interface_name : ID {
                    if (!scope.isDeclaredInMyScope($1.sval)) {
                      TablaClases.addInterface($1.sval);
                      TablaSimbolos.addInterface($1.sval);
                      $$ = new ParserVal($1.sval);
                    } else {
                      Logger.logError(aLexico.getProgramPosition(), "La interface " + $1.sval + " ya esta declarada en el ambito" + scope.getCurrentScope() + ".");
                      $$ = new ParserVal("");
                    }
                      scope.stack($1.sval); 
                }
;

interface_body : '{' interface_member_declaration '}'
               | '(' interface_member_declaration ')' {Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
               | '{' '}'
               | '(' ')' {Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
;

interface_member_declaration : interface_method_declaration 
                             | interface_member_declaration interface_method_declaration
;

interface_method_declaration : constant_declaration {Logger.logError(aLexico.getProgramPosition(), "No se permite la declaracion de constantes en las interfaces.");}
                             | abstract_method_declaration {
                                if (!$1.sval.isEmpty()) {
                                  ArrayList<String> ambitos = scope.getAmbitos($1.sval);
                                  String _method = ambitos.get(0);
                                  String _class = ambitos.get(2);

                                  TablaClases.addMetodoIMPL(_method, _class);

                                  scope.deleteLastScope();
                                }
                             }
                             | inheritance_declaration {Logger.logError(aLexico.getProgramPosition(), "No esta permitida la herencia en una interface.");}
;

constant_declaration : type variable_declarators
                     | type variable_declarators ','
;

abstract_method_declaration : abstract_method_header
                            | abstract_method_header_with_block {$$ = new ParserVal(""); Logger.logError(aLexico.getProgramPosition(), "No se puede declarar un bloque dentro de un metodo en una interface.");}
;

abstract_method_header_with_block : result_type method_declarator block
;

abstract_method_header : result_type method_declarator ',' {$$ = $2;}
                       | result_type method_declarator {Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
                       | result_type method_declarator ';' {Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' no \';\'en el final de la sentencia.");}
;

implement_for_declaration : IMPL FOR reference_class ':' implement_for_body 
                          | IMPL FOR reference_class ':' empty_statement {Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el cuerpo del metodo.");}
                          | IMPL FOR error ':' implement_for_body ',' {Logger.logError(aLexico.getProgramPosition(), "Se debe referenciar a una clase.");}
                          | IMPL FOR reference_class implement_for_body {Logger.logError(aLexico.getProgramPosition(), "Seguido de la referencia a la clase debe ir el caracter ':'.");}
;

implement_for_body : '{' implement_for_body_declarations '}' 
                   | '(' implement_for_body_declarations ')' {Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
                   | '{' '}'
                   | '(' ')' {Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar delimitado por llaves \"{...}\".");}
;

implement_for_body_declarations : implement_for_method_declaration 
                                | implement_for_body_declarations implement_for_method_declaration
;

//Aca esta el problema del impl for
implement_for_method_declaration : impl_for_method_header block  {

                                    if (!$1.sval.isEmpty()){

                                      ArrayList<String> ambitos = scope.getAmbitos($1.sval);
                                      if (ambitos.size() > 2) {
                                        String _class = ambitos.get(1); 
                                        String _method = ambitos.get(2);                                      

                                        if (!TablaClases.esUnMetodoAImplementar(_method, _class)){
                                          if (TablaClases.esUnMetodoConcreto(_method, _class)) {
                                            Logger.logError(aLexico.getProgramPosition(), "Se intentó implementar un metodo ya implementado (IMPL FOR)");
                                          } else {
                                            Logger.logError(aLexico.getProgramPosition(), "Se intentó implementar un metodo que no existe (IMPL FOR)");
                                          }  
                                        } else {
                                          TablaSimbolos.setImplemented($1.sval.replaceAll(".*@([^@]*)@([^@]*)@([^@:]*):([^@]*)", "$3@$1@$2"));
                                          TablaClases.setMetodoIMPL(_method, _class);
                                        }
                                      }
                                      scope.deleteLastScope();
                                     }
                                  }
                                  | impl_for_method_header',' {Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el metodo de la clase.");}
                                  | impl_for_method_header ';' {Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el metodo de la clase.");}
;


impl_for_method_header : result_type impl_for_method_declarator {$$ = $2;}
                       | type impl_for_method_declarator {Logger.logError(aLexico.getProgramPosition(), "No se permite retornar un tipo, el retorno debe ser VOID."); $$ = new ParserVal("");}
;



impl_for_method_declarator : impl_method_name '(' impl_formal_parameter ')' {
                                String ref = $1.sval;
                                String par = $3.sval;

                                if (!ref.isEmpty()) 
                                  $$ = new ParserVal(ref + TablaClases.TYPE_SEPARATOR + par);
                                else 
                                  Logger.logError(aLexico.getProgramPosition(), "No se reconocio el metodo a sobreescribir con p/j de parametro.");
                                
                            }
                            | impl_method_name '(' ')' {
                                $$ = new ParserVal($1.sval + TablaClases.TYPE_SEPARATOR + TablaSimbolos.SIN_PARAMETRO);
                            }
                            | impl_method_name '(' formal_parameter error ')' { Logger.logError(aLexico.getProgramPosition(), "Solo se permite la declaracion de un unico parametro formal.");}
                            | impl_method_name '{' error '}'{ Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar delimitado por parentesis \"(...)\"."); }
;

impl_formal_parameter : type ID {$$ = $1;}
        
;

impl_method_name : ID {
                    scope.stack($1.sval);
                    $$ = new ParserVal(scope.getCurrentScope());
                  }
;




/*

>>>     EXPRESSIONS

*/
assignment : left_hand_side '=' arithmetic_operation  {
                Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion.");
                tercetos.add("=", $1.sval, $3.sval, tercetos.typeTerceto($1.sval, $3.sval));
                tercetos.declaredFactorsUsed($3.sval);
           }
           | left_hand_side MINUS_ASSIGN arithmetic_operation {
                Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion de resta.");
                tercetos.add("=", $1.sval, tercetos.add("-", $1.sval, $3.sval));
                tercetos.declaredFactorsUsed($3.sval);
           }
           | left_hand_side error arithmetic_operation {Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
           | left_hand_side EQUAL_OPERATOR arithmetic_operation {Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
           | left_hand_side NOT_EQUAL_OPERATOR arithmetic_operation {Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
           | left_hand_side LESS_THAN_OR_EQUAL_OPERATOR arithmetic_operation {Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
           | left_hand_side GREATER_THAN_OR_EQUAL_OPERATOR arithmetic_operation {Logger.logError(aLexico.getProgramPosition(), "Las asignaciones se deben hacer con el caracter '=' o '-='.");}
;

left_hand_side : reference_type
               | invocation {Logger.logError(aLexico.getProgramPosition(), "No se puede invocar un metodo/funcion en el lado izquierdo de una asignación.");}
               | factor {Logger.logError(aLexico.getProgramPosition(), "No se puede utilizar constantes en el lado izquierdo de una asignación.");}
;


field_acces : primary '.' ID {$$ = new ParserVal($1.sval + "." + $3.sval);}
;

primary : ID 
        | field_acces
;

equality_expression : relational_expression {$$ = new ParserVal($1.sval);}
                    | equality_expression EQUAL_OPERATOR relational_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); $$ = new ParserVal(tercetos.add("==", $1.sval, $3.sval, tercetos.typeTerceto($1.sval, $3.sval)));}
                    | equality_expression NOT_EQUAL_OPERATOR relational_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); $$ = new ParserVal(tercetos.add("!!", $1.sval, $3.sval, tercetos.typeTerceto($1.sval, $3.sval)));}
;

relational_expression : additive_expression {$$ = new ParserVal($1.sval);}
                      | relational_expression '<' additive_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); $$ = new ParserVal(tercetos.add("<", $1.sval, $3.sval, tercetos.typeTerceto($1.sval, $3.sval)));}
                      | relational_expression '>' additive_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); $$ = new ParserVal(tercetos.add(">", $1.sval, $3.sval, tercetos.typeTerceto($1.sval, $3.sval)));}
                      | relational_expression GREATER_THAN_OR_EQUAL_OPERATOR additive_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); $$ = new ParserVal(tercetos.add(">=", $1.sval, $3.sval, tercetos.typeTerceto($1.sval, $3.sval)));}
                      | relational_expression LESS_THAN_OR_EQUAL_OPERATOR additive_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica."); $$ = new ParserVal(tercetos.add("<=", $1.sval, $3.sval, tercetos.typeTerceto($1.sval, $3.sval)));}
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

unary_expression : factor 
                 | reference_type
                 | invocation {Logger.logError(aLexico.getProgramPosition(), "No se puede invocar un metodo/funcion en una expresion.");}
                 | conversion_expression
                 | '(' arithmetic_operation ')' {
                    if (tercetos.hasNestingExpressions($2.sval)) 
                      Logger.logError(aLexico.getProgramPosition(), "No se permite el anidamiento de expresiones.");
                    $$ = new ParserVal($2.sval);  
                 }  //Aca se debe chequear que sea un nivel de ()?
                 | '(' ')' {Logger.logError(aLexico.getProgramPosition(), "Termino vacio.");}
;

conversion_expression : TOD '(' arithmetic_operation ')' {
                          $$ = new ParserVal(tercetos.add("TOD", $3.sval, "-"));
                          tercetos.TODtracking($$.sval);
                          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una conversion explicita.");
                       }
                      | TOD '(' error ')' {Logger.logError(aLexico.getProgramPosition(), "No se puede convertir la expresion declarada.");}
                      | TOD '{' error '}' {Logger.logError(aLexico.getProgramPosition(), "El metodo TOD debe estar delimitado por parentesis \"(...)\".");}
                      | TOD '{' '}' {Logger.logError(aLexico.getProgramPosition(), "El metodo TOD debe estar delimitado por parentesis \"(...)\".");}
                      | TOD '(' ')' {Logger.logError(aLexico.getProgramPosition(), "Es necesario pasar una expresion aritmetica.");}
;

factor : CTE_DOUBLE {$$ = new ParserVal($1.sval); } 
       | CTE_UINT {$$ = new ParserVal($1.sval);} 
       | CTE_LONG {$$ = new ParserVal(TablaTipos.chequearRangoLong($1.sval, aLexico.getProgramPosition()));} 
       | '-'CTE_DOUBLE { $$ = new ParserVal(TablaTipos.negarDouble($2.sval));}
       | '-'CTE_LONG {
              if(!TablaTipos.chequearRangoLongNegativo($2.sval)){
                  Logger.logWarning(aLexico.getProgramPosition(),"LONG NEGATIVO FUERA DE RANGO SE TRUNCA AL MINIMO PERMITIDO");
                  $$ = new ParserVal(TablaTipos.negarLong("2147483648"));
              } else{
                  $$ = new ParserVal(TablaTipos.negarLong($2.sval));
              }
        }
       | '-'CTE_UINT {Logger.logError(aLexico.getProgramPosition() ,"Los tipos UINT deben ser sin signo."); $$ = new ParserVal($2.sval);}
;

invocation : reference_function '(' real_parameter ')' {
                String ref = $1.sval;

                if ( !ref.isEmpty() ){
                  $$ = new ParserVal(ref);
                  
                  if (!tercetos.linkFunction(ref, $3.sval))
                    Logger.logError(aLexico.getProgramPosition(), "La funcion a invocar no posee parametro formal.");
                  else
                    Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a una funcion, con pj de parametro.");
                } 
            }
            | reference_function '(' ')' {
                String ref = $1.sval;

                if (!ref.isEmpty()){
                  $$ = new ParserVal(ref);
                  
                  if (!tercetos.linkFunction(ref))
                    Logger.logError(aLexico.getProgramPosition(), "La funcion a invocar no posee parametro formal.");
                  else
                    Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a una funcion, con pj de parametro.");
                } 
            }
            | reference_method '(' real_parameter  ')' {
                String ref = $1.sval;

                if ( !ref.isEmpty() ){
                  $$ = new ParserVal(ref);
                  
                  if (!tercetos.linkMethod(ref, $3.sval, scope.getCurrentScope()))
                    Logger.logError(aLexico.getProgramPosition(), "La funcion a invocar no posee parametro formal.");
                  else
                    Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a una funcion, con pj de parametro.");
                } 
            }
            | reference_method '(' ')' {
                String ref = $1.sval;

                if (!ref.isEmpty()){
                  $$ = new ParserVal(ref);
                  
                  if (!tercetos.linkMethod(ref, scope.getCurrentScope()))
                    Logger.logError(aLexico.getProgramPosition(), "La funcion a invocar no posee parametro formal.");
                  else
                    Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a una funcion, con pj de parametro.");
                } 
            }
            | reference_method '{' error '}'
            | reference_function '{' error '}'
            | reference_function '(' real_parameter error ')' {Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
            | reference_method '(' real_parameter error ')' {Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
;
/*

>>>     TYPES

*/
type : primitive_type
     | class_type
;


primitive_type : numeric_type
;

numeric_type : integral_type
             | floating_type
;

integral_type : UINT {$$ = new ParserVal("UINT");}
              | LONG {$$ = new ParserVal("LONG");}
;

floating_type : DOUBLE {$$ = new ParserVal("DOUBLE");}
;


reference_interface : ID {
                        String reference = scope.searchInterface($1.sval);
                        if (reference == null) {
                          $$ = new ParserVal("");
                          Logger.logError(aLexico.getProgramPosition(), "La interface " + $1.sval + " no esta al alcance.");
                        } else {
                          $$ = new ParserVal($1.sval);
                        }
                    }
;

class_type : ID {
              String reference = scope.searchClass($1.sval);

              if (reference == null) {
                Logger.logError(aLexico.getProgramPosition(), "La clase " + $1.sval + " no esta al alcance.");
                $$ = new ParserVal("");
              } else {
                $$ = new ParserVal($1.sval);
              }
            }
;

reference_class : ID {
                    String reference = scope.searchClass($1.sval);
                    //Revisar esto
                    if (reference == null) {
                      Logger.logError(aLexico.getProgramPosition(), "La clase " + $1.sval + " no esta al alcance.");
                      $$ = new ParserVal("");
                    } else {
                      scope.stack($1.sval);
                      $$ = new ParserVal(reference);
                    }
                  }
;


reference_function : ID {
                      String reference = scope.searchFunc($1.sval);

                      if(reference == null) {
                        Logger.logError(aLexico.getProgramPosition(), "La funcion " + $1.sval + " no esta al alcance.");
                        $$ = new ParserVal("");
                      }
                      else
                        $$ = new ParserVal(reference);
               }
;

reference_method : field_acces {
                    String instance = TablaClases.getInstance($1.sval);
                    String instance_s = scope.searchInstance(instance);

                    if (instance_s != null) {
                      String reference = TablaClases.searchMethod($1.sval, scope.getAmbito(instance_s));

                      if(reference == null) {
                        Logger.logError(aLexico.getProgramPosition(), "El metodo " + $1.sval + " no esta al alcance.");
                        $$ = new ParserVal("");
                      } else
                        $$ = new ParserVal(reference);
                    } else {
                        Logger.logError(aLexico.getProgramPosition(), "La instancia " + instance + " no esta al alcance.");
                        $$ = new ParserVal("");
                    }
               }
;


reference_type : primary {
                    String reference = scope.searchVar($1.sval);
                    if(reference == null) {
                      Logger.logError(aLexico.getProgramPosition(), "La variable " + $1.sval + " no esta al alcance.");
                      $$ = new ParserVal("");
                    }
                    else
                      $$ = new ParserVal(reference);
               }
;

/*

>>>     BLOCKS AND COMMANDS

*/
block : '{' block_statements RETURN',' '}' {tercetos.addReturn();}
      | '{' block_statements RETURN',' block_statements '}' {tercetos.addReturn(); Logger.logWarning(aLexico.getProgramPosition(), "Se esta declarando un bloque sin utilizar luego de un RETURN.");}
      | '{' RETURN',' '}' {tercetos.addReturn();}
      | '{' RETURN '}' {Logger.logError(aLexico.getProgramPosition(), "Se esperaba una ',' luego del RETURN");}
      | '{' RETURN',' block_statements '}' {tercetos.addReturn(); Logger.logWarning(aLexico.getProgramPosition(), "Se esta declarando un bloque sin utilizar luego de un RETURN");}
      | '{' block_statements '}' {Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
      | '(' block_statements RETURN',' ')' {Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...} y es necesario declarar el retorno del bloque.");}
      | '(' RETURN',' ')' {Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
      | '{' '}' {Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
      | '(' ')' {Logger.logError(aLexico.getProgramPosition(), "Un bloque debe estar delimitado por llaves \"{...}\".");}
;

executable_block : '{' executable_block_statements '}' 
                 | '{' '}'
                 | '{' executable_block_statements RETURN ',' '}' {tercetos.addReturn(); Logger.logWarning(aLexico.getProgramPosition(), "Se esta declarando un bloque sin utilizar luego de un RETURN");}
                 | '{' executable_block_statements RETURN ',' executable_block_statements '}' {tercetos.addReturn(); Logger.logWarning(aLexico.getProgramPosition(), "Se esta declarando un bloque sin utilizar luego de un RETURN");}
                 | '{' RETURN ',' '}' {tercetos.addReturn();}
                 | '{' RETURN ',' executable_block_statements '}' {tercetos.addReturn();}

                 {Logger.logWarning(aLexico.getProgramPosition(), "Se esta declarando un bloque sin utilizar luego de un RETURN");}
;

block_statements : block_statement 
                 | block_statements block_statement
;

executable_block_statements : executable_statement
                            | executable_block_statements executable_statement
;


block_statement : local_variable_declaration
                | statement
;

executable_statement : if_then_declaration
                     | if_then_else_declaration  
                     | for_in_range_statement
                     | print_statement
                     | expression_statement
                     | empty_statement
;




local_variable_declaration : type variable_declarators ',' {
                              if (!($1.sval.isEmpty() || $2.sval.isEmpty())) {
                                TablaSimbolos.addTipoVariable($1.sval, $2.sval);                      

                                if (TablaSimbolos.isClass($1.sval + Scope.getScopeMain())) {
                                  TablaClases.addInstancia($1.sval, $2.sval);
                                  TablaSimbolos.addUsoInstancia($2.sval);
                                } else
                                  TablaSimbolos.addUsedVariables($2.sval);
                                Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de variable local.");
                              } 
                            }
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
                     | invocation
;

empty_statement : ','
;


if_then_declaration : IF if_then_cond if_then_body END_IF ','  {
                         tercetos.backPatching(0);
                         tercetos.addLabel();
                    }
                    | IF if_then_cond if_then_body END_IF ';' {Logger.logError(aLexico.getProgramPosition(), "La sentencia de control IF debe terminar con ','.");}
                    | IF if_then_cond if_then_body ',' {Logger.logError(aLexico.getProgramPosition(), "La sentencia de control IF debe terminar con la palabra reservada END_IF.");}
                    | IF if_then_cond if_then_body ';' {Logger.logError(aLexico.getProgramPosition(), "La sentencia de control IF debe terminar con la palabra reservada END_IF y con finalizar con ','.");}   
                    | IF if_then_cond if_then_body END_IF error '}' {Logger.logError(aLexico.getProgramPosition(), "La sentencia de control IF debe terminar con ','.");}
                    | IF if_then_cond if_then_body error '}' {Logger.logError(aLexico.getProgramPosition(), "La sentencia de control IF debe terminar con ','.");}
; 

if_then_cond : '(' equality_expression ')' {tercetos.addCondBranch($2.sval);}
             | '(' error ')' {Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
             | '{' equality_expression '}' {Logger.logError(aLexico.getProgramPosition(), "La condicion debe estar delimitado por parentesis \"(...)\".");}
             | '(' ')'
;

if_then_body : executable_statement 
             | executable_block
             | local_variable_declaration {Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF.");}
;

if_then_else_declaration : IF if_then_cond if_then_else_body END_IF ',' {
                            Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");
                            tercetos.backPatching(0);
                            tercetos.addLabel();
                          }
                          | IF if_then_cond if_then_else_body ',' {Logger.logError(aLexico.getProgramPosition(), "La sentencia de control IF ELSE debe terminar con la palabra reservada END_IF.");}
                          | IF if_then_cond if_then_else_body END_IF ';' {Logger.logError(aLexico.getProgramPosition(), "La sentencia de control IF ELSE debe terminar con ','.");}
                          | IF if_then_cond if_then_else_body ';' {Logger.logError(aLexico.getProgramPosition(), "La sentencia de control IF ELSE debe terminar con la palabra reservada END_IF y con finalizar con ','.");}
                          | IF if_then_cond if_then_else_body END_IF error '}' {Logger.logError(aLexico.getProgramPosition(), "La sentencia de control IF ELSE debe terminar con ','.");}
                          | IF if_then_cond if_then_else_body error '}' {Logger.logError(aLexico.getProgramPosition(), "La sentencia de control IF ELSE debe terminar con ','.");}
; 

if_else_then_body : executable_statement {tercetos.backPatching(1); tercetos.addUncondBranch(); tercetos.addLabel();}
                  | executable_block {tercetos.backPatching(1); tercetos.addUncondBranch(); tercetos.addLabel();}
                  | local_variable_declaration {Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF.");}
;

if_else_body : executable_statement
             | executable_block
             | local_variable_declaration {Logger.logError(aLexico.getProgramPosition(), "No se permiten sentencias declarativas en una sentencia IF ELSE.");}
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
                               if (!$1.sval.isEmpty()) {
                                $$ = new ParserVal($1.sval);
                                tercetos.add("=", $1.sval, "-");
                                tercetos.stack($1.sval);
                                tercetos.stack();
                               }
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

for_init : factor
;

for_update : factor
;

for_end : factor
;

function_declaration : method_header method_body_without_prototype {
                        if (!$1.sval.isEmpty())
                          scope.deleteLastScope();
                      }
;

method_body_without_prototype : block
                              | ',' {$$ = new ParserVal(""); Logger.logError(aLexico.getProgramPosition(), "Es necesario definir el cuerpo de la funcion.");}
;

print_statement : PRINT CADENA ',' {tercetos.add("PRINT", $2.sval, "[-]");}
                | PRINT CADENA error {Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
                | PRINT CADENA ';' {Logger.logError(aLexico.getProgramPosition(), "Se esperaba una \',\' en el final de la sentencia.");}
                | PRINT factor ',' {Logger.logError(aLexico.getProgramPosition(), "Solo se permite imprimir variables del tipo CADENA.");}
                | PRINT primary ',' {Logger.logError(aLexico.getProgramPosition(), "Solo se permite imprimir variables del tipo CADENA.");}
                | PRINT invocation ',' {Logger.logError(aLexico.getProgramPosition(), "Solo se permite imprimir variables del tipo CADENA.");}
                | PRINT ',' {tercetos.add("PRINT", "", "[-]");}
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
      GeneradorAssembler.generarCodigoAssembler(tercetos);
      System.out.println("ASSEMBLER \n" + GeneradorAssembler.codigoAssembler);
    } else 
      System.out.println("Se produjeron errores.\n");
    

    System.out.println(aLexico.getProgram());
}

