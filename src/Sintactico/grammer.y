%{
package Sintactico;
import Lexico.AnalizadorLexico;
import java.util.Scanner;
import Tools.Logger;
import java.util.ArrayList;
import java.io.File;
import Tools.Tupla;
import Tools.TablaSimbolos;
import java.io.IOException;
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
program : type_declarations {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio el programa");}
;

/*

>>>     DECLARATIONS

*/
type_declarations : type_declaration 
                  | type_declarations type_declaration
;

type_declaration : class_declaration 
                 | interface_declaration
                 | implement_for_declaration
                 | block_statement
;




class_declaration : CLASS ID class_body {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS.");}
                  | CLASS ID interfaces class_body {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS que implementa una interface.");}
                  | error ID class_body {Logger.logRule(aLexico.getProgramPosition(), "Declaracion de CLASS no valida.");}
;

class_body : '{' class_body_declarations '}' 
;

class_body_declarations : class_body_declaration 
                        | class_body_declarations class_body_declaration
;

class_body_declaration : class_member_declaration 
;

class_member_declaration : field_declaration 
                         | method_declaration
;

field_declaration : type variable_declarators ',' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de atributo/s.");}
;

variable_declarators : variable_declarator 
                     | variable_declarators ';' variable_declarator
;

variable_declarator : variable_declarator_id 
                    | variable_declarator_id '=' variable_initializer
;

variable_declarator_id : ID
;

variable_initializer : arithmetic_operation
;

method_declaration : method_header method_body 
;

method_header : result_type method_declarator
;

result_type : VOID 
;

method_declarator : ID '(' formal_parameter ')'{Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");}
                  | ID '{' formal_parameter '}'{Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar limitado por parentesis \"(...)\".");}
                  | ID '(' formal_parameter ',' error ')' {Logger.logError(aLexico.getProgramPosition(), "Solo se permite la declaracion de un unico parametro formal.");}
                  | ID '(' ')' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");}
                  | ID '{' '}' {Logger.logError(aLexico.getProgramPosition(), "La declaracion de un metodo debe estar limitado por parentesis \"(...)\".");}
;

// Permito la creacion de multiples block en un metodo, se debe chequear que luego permita
// un nivel de anidamiento
method_body : block 
            | ',' // Propotipo de metodo -> ID '(' ')' ',' sin block
;

formal_parameter : type variable_declarator_id
;

real_parameter : arithmetic_operation
;

interfaces : IMPLEMENT interface_type_list
;

interface_type_list : type_name 
                    | interface_type_list ';' type_name
;

interface_declaration : INTERFACE ID interface_body {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una INTERFACE.");}
                      | INTERFACE ID error ',' {Logger.logError(aLexico.getProgramPosition(), "Es necesario definir el cuerpo de la interface.");}
;

interface_body : '{' interface_member_declaration '}'
               | '(' interface_member_declaration ')'{Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar limitado por llaves \"{...}\".");}
               | '{' '}'
               | '(' ')' {Logger.logError(aLexico.getProgramPosition(), "El cuerpo de la interface debe estar limitado por llaves \"{...}\".");}
;

interface_member_declaration : interface_method_declaration 
                             | interface_member_declaration interface_method_declaration
;

interface_method_declaration : result_type method_declarator
                             | result_type error ',' {Logger.logError(aLexico.getProgramPosition(), "Es necesario generar la declaracion del metodo");}
;

implement_for_declaration : IMPL FOR reference_type ':' implement_for_body {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un IMPL FOR.");}
                          | IMPL FOR reference_type ':' error ',' {Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el cuerpo del metodo.");}
                          | IMPL FOR error ':' implement_for_body ',' {Logger.logError(aLexico.getProgramPosition(), "Se debe referenciar a una clase.");}
                          | error reference_type ':' implement_for_body {Logger.logRule(aLexico.getProgramPosition(), "Declaracion de IMPL FOR no valida.");}
;

implement_for_body : '{' implement_for_body_declarations '}' 
;

implement_for_body_declarations : implement_for_body_declaration 
                                | implement_for_body_declarations implement_for_body_declaration
;

implement_for_body_declaration : implement_for_method_declaration
;

implement_for_method_declaration : method_header implement_for_method_body 
;

implement_for_method_body : method_body
;

/*

>>>     EXPRESSIONS

*/
assignment : left_hand_side assignment_operator arithmetic_operation {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion.");}
;

left_hand_side : reference_type
;

expression : equality_expression  {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");}
;

equality_expression : relational_expression 
                    | equality_expression EQUAL_OPERATOR relational_expression 
                    | equality_expression NOT_EQUAL_OPERATOR relational_expression
;

relational_expression : additive_expression 
                      | relational_expression '<' additive_expression
                      | relational_expression '>' additive_expression
                      | relational_expression GREATER_THAN_OR_EQUAL_OPERATOR additive_expression
                      | relational_expression LESS_THAN_OR_EQUAL_OPERATOR additive_expression
;

arithmetic_operation : additive_expression {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion aritmetica.");}
;

additive_expression : multiplicative_expression 
                    | additive_expression '+' multiplicative_expression 
                    | additive_expression '-' multiplicative_expression
;

multiplicative_expression : unary_expression 
                          | TOD '(' unary_expression ')' // Conversion explicita
                          | multiplicative_expression '*' unary_expression
                          | multiplicative_expression '/' unary_expression
                          | multiplicative_expression '%' unary_expression
;

unary_expression : term 
                 | ID
;

term : factor
     | '(' expression ')'
     | '(' ')' {Logger.logError(aLexico.getProgramPosition(), "Termino vacio.");}
;

factor : CTE_DOUBLE
       | CTE_UINT 
       | CTE_LONG
       | '-'CTE_DOUBLE {System.out.println("Posicion 1: " + $1.sval + ", Posicion 2: " + $2.sval); $$ = new ParserVal(negarDouble($2.sval));}
       | '-'CTE_LONG {System.out.println($2.sval); $$ = new ParserVal(negarLong($2.sval));}
       | '-'CTE_UINT {Logger.logWarning(aLexico.getProgramPosition() ,"Los tipos enteros deben ser sin signo."); $$ = new ParserVal($2.sval);}
;

assignment_operator : '=' 
                    | MINUS_ASSIGN 
;

method_invocation : ID '(' real_parameter ')' ',' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo.");}
                  | ID '(' ')' ',' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo.");}
                  | ID '(' real_parameter ',' error ')' ',' {Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");}
;

/*

>>>     TYPES

*/
type : primitive_type 
     | reference_type
;


primitive_type : numeric_type
;

// Se puede utilizar impl for para una interfaz? supongo que no
reference_type : ID 
;


numeric_type : integral_type 
             | floating_type
;

integral_type : UINT 
              | LONG 
;

floating_type : DOUBLE
;


type_name : ID
;

/*

>>>     BLOCKS AND COMMANDS

*/
block : '{' block_statements RETURN',' '}'
      | '{' block_statements '}' {Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
      | '{' RETURN',' '}'
      | '{' '}' {Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");}
;

executable_block : '{' executable_block_statements '}' 
                 | '{' '}'
;

block_statements : block_statement 
                 | block_statements block_statement
;

executable_block_statements : executable_statament
                            | executable_block_statements executable_statament
;


block_statement : local_variable_declaration_statement 
                | statement
;

executable_statament : if_then_statement
                     | if_then_else_statement  
                     | for_in_range_statement
                     | print_statement
                     | expression_statement
                     | empty_statement
;

local_variable_declaration_statement : local_variable_declaration ',' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de variable local.");}
;

local_variable_declaration : type variable_declarators
;


statement : statement_without_trailing_substatement
          | if_then_statement 
          | if_then_else_statement 
          | for_in_range_statement
          | method_declaration //Es con los metodos? Esta bien declarar un metodo prototipo?
          | print_statement
;

statement_without_trailing_substatement : block 
                                        | empty_statement
                                        | expression_statement
;

expression_statement : statement_expression
;

statement_expression : assignment 
                     | method_invocation
;

empty_statement : ','
;


if_then_statement : IF '(' expression ')' executable_block END_IF ',' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");}
                  | IF '(' expression ')' executable_statament END_IF ',' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");}
                  | IF '(' expression ')' executable_statament ',' {Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el final de la sentencia de control IF.");}
                  | IF '(' expression ')' executable_block ',' {Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el final de la sentencia de control IF.");}
                  | IF '(' expression ')' error END_IF ',' {Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el cuerpo de la sentencia de control IF.");}
                  | IF '(' error ')' executable_block END_IF ',' {Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
                  | IF '(' error ')' executable_statament END_IF ',' {Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}

;   

if_then_else_statement : IF '(' expression ')' executable_block ELSE executable_block END_IF ',' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
                       | IF '(' expression ')' executable_block ELSE executable_statament END_IF ',' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
                       | IF '(' expression ')' executable_statament ELSE executable_statament END_IF ',' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
                       | IF '(' expression ')' executable_statament ELSE executable_block END_IF ',' {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");}
                       | IF '(' expression ')' executable_statament ELSE executable_block error ',' {Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el END_IF de la sentencia de control IF.");} 
                       | IF '(' error ')' executable_block ELSE executable_block END_IF ',' {Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
                       | IF '(' error ')' executable_block ELSE executable_statament END_IF ',' {Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
                       | IF '(' error ')' executable_statament ELSE executable_statament END_IF ',' {Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
                       | IF '(' error ')' executable_statament ELSE executable_block END_IF ',' {Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");}
; 


for_in_range_statement : FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' executable_block {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");}
                       | FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' executable_statament {Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");}
                       | FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' error ',' {Logger.logError(aLexico.getProgramPosition(), "Cuerpo del FOR IN RANGE no valido.");} 
                       | FOR for_variable IN RANGE '('error')'  executable_block {Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");} 
                       | FOR for_variable IN RANGE '('error')' executable_statament {Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");} 
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
                | PRINT error ','{Logger.logError(aLexico.getProgramPosition(), "Se esperaba una cadena.");}
                | error CADENA ',' {Logger.logRule(aLexico.getProgramPosition(), "Declaracion de PRINT no valida.");}
;
%%

private static AnalizadorLexico aLexico;
private static int yylval_recognition = 0;
public static boolean error = false;

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
    System.out.println("Error en el parser: " + msg);
}

// ###############################################################
// metodos auxiliares a la gramatica
// ###############################################################

private String negarDouble(String lexema) {
      
    double RDN_MIN = -2.2250738585072014D * -Math.pow(10, 308);
    double RDN_MAX = -1.7976931348623157D * Math.pow(10, 308);

    String n_lexema = '-'+lexema;
    double numero = 0.0;

    try {
        numero = Double.parseDouble(n_lexema);
    } catch (Exception ex) {}

    if (numero > RDN_MAX || numero < RDN_MIN ){
      Logger.logWarning(aLexico.getProgramPosition(), "El DOUBLE se excedio de rango, el mismo fue truncado al valor " + RDN_MAX + ".");
      n_lexema = "" + RDN_MAX;
    } 

    addTablaSimbolos(lexema, n_lexema, "D");

    return n_lexema;
}


private void addTablaSimbolos(String lexema, String n_lexema, String tipo) {

  if (!TablaSimbolos.containsKey(n_lexema)) {

    if (tipo == "D") { // Perdon Luis por hacer un if por tipos T_T
      TablaSimbolos.addDouble(n_lexema);

    } else {
      TablaSimbolos.addLong(n_lexema);
    }

    TablaSimbolos.addContador(n_lexema);
    TablaSimbolos.decreaseCounter(lexema);

  } else {
    TablaSimbolos.increaseCounter(n_lexema);
  }
}


private String negarLong(String lexema) {

    String n_lexema = '-'+lexema;
    long numero = 0;

    try {
        numero = Long.parseLong(n_lexema);
    } catch (Exception ex) {}

    addTablaSimbolos(lexema, n_lexema, "L");

    return n_lexema;
}

// ###############################################################
// metodos de lectura de los programadas
// ###############################################################

private static ArrayList<String> listFilesInDirectory(String path) {
  // Obtén el directorio actual
  File element = new File(System.getProperty("user.dir") + "/" + path);
  ArrayList<String> out = new ArrayList<>();

  // Verifica si es un directorio o archivo válido
  if (element.isDirectory() || element.isFile()) {
    // Lista de archivos y directorios en el directorio actual
    File[] filesAndDirs = element.listFiles();

    // Itera a través de los archivos y directorios
    int i = 0;
    for (File fileOrDir : filesAndDirs) {
      String name = fileOrDir.getName();
      System.out.println("[" + i + "]" + ": " + name);
      out.add(name);
      i++;
    }
  } else {
    System.err.println("No es un directorio válido.");
  }

  return out;
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
      indice = Integer.parseInt(input);
      if (indice < directories.size() || indice >= 0) {
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
        indice = Integer.parseInt(input);

        if (indice < directories.size() || indice >= 0) {
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
    System.out.println("Iniciando compilacion... ");

    String input = generatePath();

    aLexico = new AnalizadorLexico(input);

    if ( !aLexico.hasReadWell() ) {
        return;
    }

    System.out.println(aLexico.getProgram());

    Parser aSintactico = new Parser();
    aSintactico.run();
    aSintactico.dump_stacks(yylval_recognition);

    System.out.println(Logger.dumpLog());
}


