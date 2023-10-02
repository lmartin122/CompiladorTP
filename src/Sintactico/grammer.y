%{
package Sintactico;
import Lexico.AnalizadorLexico;
import java.util.Scanner;
import Tools.Logger;
%}

%token       
CLASS INTERFACE IMPLEMENT NEW SUPER THIS
FUNC RETURN
IF ELSE END_IF FOR IN RANGE IMPL PRINT TOD
EQUAL_OPERATOR NOT_EQUAL_OPERATOR GREATER_THAN_OR_EQUAL_OPERATOR LESS_THAN_OR_EQUAL_OPERATOR MINUS_ASSIGN
VOID LONG UINT DOUBLE BOOLEAN CADENA ID CTE

// Precedencia 
%left '+' '-'
%left '*' '/'

%start program

%%
/*

>>>     PROGRAM

*/
program : type_declarations 
        | type_declaration
;

/*

>>>     DECLARATIONS

*/
type_declarations : type_declaration 
                  | type_declarations type_declaration
;

type_declaration : class_declaration 
                 | function_declaration 
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
                       | constructor_declaration
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

variable_initializer : expression
;

method_declaration : method_header method_body 
;

method_header : result_type method_declarator
;

// Chequear si el tipo de returno solo es de void 
result_type : type 
            | VOID
;

method_declarator : ID '(' formal_parameter ')' 
                  | ID '(' ')' 
;

// Permito la creacion de multiples block en un metodo, se debe chequear que luego permita
// un nivel de anidamiento
method_body : block 
            | ',' // Propotipo de metodo -> ID '(' ')' ',' sin block
;

/*
Se permite hasta un parámetro, y puede no haber parámetros.
Este chequeo debe efectuarse durante el Análisis Sintáctico

formal_parameter_list : formal_parameter
;
*/
formal_parameter : type variable_declarator_id
;

// Deberia ser una asignacion? le permitiria hacer ID ( a  = 3 ) por ejemplo
real_parameter : expression
;

// No se si hace falta el constructor pero lo dejo por las dudas, me falta el cuerpo?
constructor_declaration : simple_type_name formal_parameter
;

simple_type_name : ID 
;

interfaces : IMPLEMENT interface_type_list
;

interface_type_list : interface_type 
                    | interface_type_list ';' interface_type
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

function_declaration : FUNC function_header function_body 
                     | FUNC function_header {Logger.logError(0, "Es necesario implementar el cuerpo de la funcion.");}
;

function_header : result_type function_declarator
;

function_declarator : ID '(' formal_parameter ')' 
                    | ID '(' ')' 
;

function_body : function_block 
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
               | field_access
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

unary_expression : term | expression_name
;

term : factor
     | '(' expression ')'
;

factor : CTE
;

expression_name : ID
;

field_access : primary'.' ID 
             | SUPER'.' ID 
;

assignment_operator : '=' 
                    | MINUS_ASSIGN 
;

primary : THIS 
        | class_instance_creation_expression 
        | field_access 
;

literal: CTE
;

// Chequear si es necesario poner la instanciacion de una clase
class_instance_creation_expression : NEW class_type '(' real_parameter ')' 
                                   | NEW class_type '(' ')' 
                                   | NEW class_type {Logger.logError(0, "Se esperaba un \'(\'.");}
;


// Chequear si hay que poner super o puede ser la misma regla gramatical para las funciones y metodos
method_invocation : ID '(' real_parameter ')' invocation_end 
                  | ID '(' ')' invocation_end
                  | primary'.'ID '(' real_parameter ')' invocation_end 
                  | primary'.'ID '(' ')' invocation_end
                  | SUPER'.'ID '(' real_parameter ')' invocation_end 
                  | SUPER'.'ID '(' ')' invocation_end 
;

invocation_end : ','
               | {Logger.logError(0, "Se esperaba una \",\".");}
;

function_invocation : ID '(' real_parameter ')' invocation_end
                    | ID '(' ')' invocation_end
;

/*

>>>     TYPES

*/
type : primitive_type 
     | reference_type
;

// No se si hace falta el boolean
primitive_type : numeric_type 
               | BOOLEAN
;

// Se puede utilizar impl for para una interfaz? supongo que no
reference_type : class_type 
               | interface_type
;

class_type : type_name
;

interface_type : type_name
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
block : '{' block_statements '}' 
      | '{' '}'
;

//Chequear si solamente las funciones tienen que tener retorno y de forma obligatoria
function_block : '{' block_statements return_statement '}' 
               | return_statement
               | '{' block_statements '}' {Logger.logError(0, "Es necesario declarar el returno de la funcion.");} 


block_statements : block_statement 
                 | block_statements block_statement
;

block_statement : local_variable_declaration_statement 
                | statement
;

local_variable_declaration_statement : local_variable_declaration invocation_end 
;

local_variable_declaration : type variable_declarators
;

// Creo que va aca method_declaration y print_statement
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
                                        | return_statement
;


expression_statement : statement_expression
;

statement_expression : assignment 
                     | method_invocation
                     | function_invocation
;

empty_statement : ','
;

if_then_statement : IF '(' assignment ')' statement END_IF invocation_end 
; 

if_then_else_statement : IF '(' assignment ')' statement ELSE statement END_IF invocation_end 
;


for_in_range_statement : FOR for_variable IN RANGE '(' for_init ; for_end ; for_update ')' statement invocation_end 


//Chequear si solo son CTE o pueden ser expresiones tambien, aunque tengo que tener
//cuidado ya que la expresion me permite un ID
for_variable : ID
;

for_init : CTE
;

for_update : CTE
;

for_end : CTE
;

statement_expression_list : statement_expression 
                          | statement_expression_list ';' statement_expression
;

print_statement : PRINT CADENA invocation_end 
                | PRINT {Logger.logError(0, "Se esperaba una cadena.");}
;

// Creo que solo podemos tener funciones y metodos del tipo void
// return_statement : RETURN expression ',' | RETURN ','
return_statement : RETURN','
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


