%{
package Sintactico;
import Lexico.AnalizadorLexico;
import java.util.Scanner;
import Tools.Logger;
import java.util.ArrayList;
import java.io.File;
%}

%token       
CLASS INTERFACE IMPLEMENT
RETURN
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
program : type_declarations
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
;

class_declaration : CLASS ID class_body 
                  | CLASS ID interfaces class_body 
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

field_declaration : type variable_declarators ','
                  | error ',' //Descartar tokens hasta la coma, modo panico
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

method_declarator : ID '(' formal_parameter ')' 
                  | ID '(' ')' 
;

// Permito la creacion de multiples block en un metodo, se debe chequear que luego permita
// un nivel de anidamiento
method_body : block 
            | ',' // Propotipo de metodo -> ID '(' ')' ',' sin block
;

formal_parameter : type variable_declarator_id
;

real_parameter : expression
;


interfaces : IMPLEMENT interface_type_list
;

interface_type_list : type_name 
                    | interface_type_list ';' type_name
;

interface_declaration : INTERFACE ID interface_body
;

interface_body : '{' interface_member_declaration '}' 
               | '{' '}' ','
;

interface_member_declaration :  interface_method_declaration 
                             | interface_member_declaration interface_method_declaration
;

interface_method_declaration : result_type method_declarator
;

implement_for_declaration : IMPL FOR reference_type ':' implement_for_body 
                          | IMPL FOR reference_type ':' {Logger.logError(0, "Es necesario implementar el cuerpo del metodo.");}
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
assignment : left_hand_side assignment_operator expression
;

left_hand_side : expression_name 
;

expression : equality_expression 
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

arithmetic_operation : additive_expression
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
                 | expression_name
;

term : factor
     | '(' expression ')'
;

factor : CTE_DOUBLE
       | CTE_UINT 
       | CTE_LONG
;

expression_name : ID
;


assignment_operator : '=' 
                    | MINUS_ASSIGN 
;



// Chequear si hay que poner super o puede ser la misma regla gramatical para las funciones y metodos
method_invocation : ID '(' real_parameter ')' ','
                  | ID '(' ')' ','
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
reference_type : type_name 
;


numeric_type : integral_type 
             | floating_type
;

// Chequear si tenemos que implementar mas tipos    
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
      | '{' block_statements '}' {Logger.logError(0, "Es necesario declarar el retorno del bloque.");}
      | '{' RETURN',' '}' 
      | '{' '}' {Logger.logError(0, "Es necesario declarar el retorno del bloque.");}
;

executables_block : '{' executables_block_statements '}' 
                  | '{' '}'
;

block_statements : block_statement 
                 | block_statements block_statement
;

executables_block_statements : executables_block_statament
                             | executables_block_statements executables_block_statament
;


block_statement : local_variable_declaration_statement 
                | statement
;

executables_block_statament : if_then_statement
                            | if_then_else_statement  
                            | for_in_range_statement
                            | print_statement
                            | expression_statement
                            | empty_statement
;

local_variable_declaration_statement : local_variable_declaration ','
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

if_then_statement : IF '(' expression ')' executables_block END_IF ','
; 

if_then_else_statement : IF '(' expression ')' executables_block ELSE executables_block END_IF ',' 
;


for_in_range_statement : FOR for_variable IN RANGE '(' for_init ; for_end ; for_update ')' executables_block ','


for_variable : ID
;

for_init : factor
;

for_update : factor
;

for_end : factor
;



print_statement : PRINT CADENA ','
                | PRINT {Logger.logError(0, "Se esperaba una cadena.");}
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
            // yyval = new ParserVal(token); //genera la referencia a la tabla de simbolos?
            return token;
        }

    }
    
    return token;
}


// This method is expected by BYACC/J, and is used to provide error messages to be directed to the channels the user desires.
void yyerror(String msg) {
    System.out.println(msg);
}


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


public static void main (String [] args){
    System.out.println("Iniciando compilacion... ");

    String input = generatePath();

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


