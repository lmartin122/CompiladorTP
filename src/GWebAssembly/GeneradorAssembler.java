package GWebAssembly;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import Tools.TablaSimbolos;
import Tools.TablaTipos;

public class GeneradorAssembler {

    public static StringBuilder codigoAssembler = new StringBuilder();
    public static int contadorVariablesAux = 0;
    public static HashMap<String, Integer> tercetosAsociados = new HashMap<>();

    private static final String OVERFLOW_SUMA_PFLOTANTE = "Error: se excedió el límite permitido (overflow)";
    private static final String OVERFLOW_PRODUCTO_ENTERO = "Error: se excedió el límite permitido (overflow)";
    private static final String INVOCACION_RECURSIVA = "Error: no se permiten declaraciones recursivas.";


    public void generarCodigoAssembler(){ //Importamos las librerías que se necesitan en el Assembler.
        generarCodigoLibrerias();
        generarCodigoVariables();

    }

    private void generarCodigoLibrerias(){
        StringBuilder header = new StringBuilder();
        header.append(".386\n")
        .append(".model flat, stdcall\n")
        .append("option casemap :none\n")
        .append("include \\masm32\\include\\windows.inc\n")
        .append("include \\masm32\\include\\kernel32.inc\n")
        .append("include \\masm32\\include\\masm32.inc\n")
        .append("includelib \\masm32\\lib\\kernel32.lib\n")
        .append("includelib \\masm32\\lib\\masm32.lib\n");
    }

    private StringBuilder generarCodigoVariables(){ //Generamos el código para las variables declaradas. 
            StringBuilder seccionVariables = new StringBuilder();
            seccionVariables.append(".data\n"); //Empieza la declaración de variables.
            for(String identificadorActual: TablaSimbolos.getSimbolos()){
                //String uso = TablaSimbolos.getUseLexema(identificadorActual);
                String tipo = TablaSimbolos.getTypeLexema(identificadorActual);
                String lexema = TablaSimbolos.getLexema(identificadorActual);
                //Hay que chequear el tipo FUNCTION?
                switch (tipo) {
                    case TablaTipos.UINT_TYPE:
                        //if(lexema.startsWith("aux")){} //Si es variable auxiliar, deberíamos hacer algo, o es una variable más? 
                        seccionVariables.append(lexema).append(" dw ? \n");
                        break;
                    case TablaTipos.DOUBLE_TYPE:
                        seccionVariables.append(lexema).append(" dq ? \n");
                        break;
                    case TablaTipos.LONG_TYPE:
                        seccionVariables.append(lexema).append(" dd ? \n");
                        break;
                }
            }
            return seccionVariables;
    }

    

    private void generarCodigoOperacionesEnteros(String operador, String operando1, String operando2){ //Preguntar si tengo que involucrar a la pila al final de cada operacion. PREGUNTAR
        String variableAuxiliar;
        switch (operador) {
            case "+":
                if(TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE) && TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE)){
                    codigoAssembler.append("MOV EAX, ").append("__").append(operando1).append("\n"); //Movemos el valor del OP1 al registro EAX (32 bits).
                    codigoAssembler.append("ADD EAX, ").append("__").append(operando2).append("\n"); //Sumamos los operandos.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE); //Hacemos la variable auxiliar del tipo del resultado de la operación (LONG).
                } else {
                    codigoAssembler.append("MOV AX, ").append("__").append(operando1).append("\n"); //Movemos el valor del OP1 al registro AX (16 bits).
                    codigoAssembler.append("ADD AX, ").append("__").append(operando2).append("\n"); //Sumamos los operandos.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE); //Hacemos la variable auxiliar del tipo del resultado de la operación (UINT).
                }
                codigoAssembler.append("MOV ").append(variableAuxiliar).append(", ").append((TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)) ? "EAX" : "AX").append("\n");
                break;
            case "-":
                if(TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE) && TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE)){
                    codigoAssembler.append("MOV EAX, ").append("__").append(operando1).append("\n"); //Movemos el valor del OP1 al registro EAX (32 bits).
                    codigoAssembler.append("SUB EAX, ").append("__").append(operando2).append("\n"); //Restamos los operandos.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE); //Hacemos la variable auxiliar del tipo del resultado de la operación (LONG).
                } else {
                    codigoAssembler.append("MOV AX, ").append("__").append(operando1).append("\n"); //Movemos el valor del OP1 al registro AX (16 bits).
                    codigoAssembler.append("SUB AX, ").append("__").append(operando2).append("\n"); //Restamos los operandos.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE); //Hacemos la variable auxiliar del tipo del resultado de la operación (UINT).
                }
                codigoAssembler.append("MOV ").append(variableAuxiliar).append(", ").append((TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)) ? "EAX" : "AX").append("\n");
                break;
            case "*":
                if(TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE) && TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE)){
                    codigoAssembler.append("MOV EAX, ").append("__").append(operando1).append("\n"); //Movemos el valor del OP1 al registro EAX (32 bits).
                    codigoAssembler.append("MUL ").append("__").append(operando2).append("\n"); //Multiplicamos los operandos.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE); //Hacemos la variable auxiliar del tipo del resultado de la operación (LONG).
                } else {
                    codigoAssembler.append("MOV AX, ").append("__").append(operando1).append("\n"); //Movemos el valor del OP1 al registro AX (16 bits).
                    codigoAssembler.append("MUL ").append("__").append(operando2).append("\n"); //Multiplicamos los operandos.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE); //Hacemos la variable auxiliar del tipo del resultado de la operación (UINT).
                }
                //Ver overflow.
                codigoAssembler.append("MOV ").append(variableAuxiliar).append(", ").append((TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)) ? "EAX" : "AX").append("\n");
                break;
            case "=":
                if (TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE) && TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)) {
                    // Asignación de enteros largos (32 bits)
                    codigoAssembler.append("MOV EAX, ").append("__").append(operando2).append("\n"); // Movemos el valor de op2 a EAX
                    codigoAssembler.append("MOV ").append("__").append(operando1).append(", EAX\n"); // Asignamos el valor de EAX a op1
                } else {
                    // Asignación de enteros sin signo (16 bits)
                    codigoAssembler.append("MOV AX, ").append("__").append(operando2).append("\n"); // Movemos el valor de op2 a AX
                    codigoAssembler.append("MOV ").append("__").append(operando1).append(", AX\n"); // Asignamos el valor de AX a op1
            }
                break;
            case "/":
            if (TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE) && TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)) {
                variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE);
                codigoAssembler.append("MOV EAX ").append("__").append(operando1).append("\n"); //Guardamos el dividendo en el registro EAX (32 bits).
                codigoAssembler.append("DIV ").append("__").append(operando2).append("\n"); //Realizamos la división.
                codigoAssembler.append("MOV ").append(variableAuxiliar); // Guardamos el valor en la variable auxiliar.
            } else {
                variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE);
                codigoAssembler.append("MOV AX ").append("__").append(operando1).append("\n"); //Guardamos el dividendo en el registro AX (16 bits).
                codigoAssembler.append("DIV ").append("__").append(operando2); //Realizamos la división.
                codigoAssembler.append("MOV ").append(variableAuxiliar).append(", AX"); //Guardo el resultado de la división en AX (16 bits).
            }
                break;
            case "==":
                if (TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE) && TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)) {
                    codigoAssembler.append("MOV EAX ").append("__").append(operando1).append("\n");
                    codigoAssembler.append("CMP EAX ").append("__").append(operando2).append("\n");
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE);
                    codigoAssembler.append("");
                    
                }
                break;
            
            case ">=":

                break;

            case "<=":

                break;
            
            case ">":
                
                break;
            
            case "<":

                break;
            
            case "!!":

                break;
        }
    }


    private void generarCodigoOperacionesDouble(String operador, String operando1, String operando2){
        String variableAuxiliar;
        switch (operador) {
            case "+":
                codigoAssembler.append("FLD ").append("__").append(operando2).append("\n"); // Cargamos en la pila del coprocesador los valores de punto flotante.
                codigoAssembler.append("FLD ").append("__").append(operando1).append("\n");
                codigoAssembler.append("FADD "); // Hacemos la suma de los operandos recién cargados en la pila. 
                variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE);
                codigoAssembler.append("FSTP ").append(contadorVariablesAux).append("\n"); // Almacenamos el resultado y desapilamos.
                //Ver overflow.
                break;
        
            case "-":
                codigoAssembler.append("FLD ").append("__").append(operando2).append("\n"); // Cargamos en la pila del coprocesador los valores de punto flotante.
                codigoAssembler.append("FLD ").append("__").append(operando1).append("\n");
                codigoAssembler.append("FSUB "); // Hacemos la resta de los operandos recién cargados en la pila. 
                variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE);
                codigoAssembler.append("FSTP ").append(contadorVariablesAux).append("\n"); // Almacenamos el resultado y desapilamos.
                break;
            
            case "*":
                codigoAssembler.append("FLD ").append("__").append(operando2).append("\n"); // Cargamos en la pila del coprocesador los valores de punto flotante.
                codigoAssembler.append("FLD ").append("__").append(operando1).append("\n");
                codigoAssembler.append("FMUL "); // Hacemos la multiplicación de los operandos recién cargados en la pila. 
                variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE);
                codigoAssembler.append("FSTP ").append(contadorVariablesAux).append("\n"); // Almacenamos el resultado y desapilamos.
                break;
            
            case "/":
                codigoAssembler.append("FLD ").append("__").append(operando2).append("\n"); // Cargamos en la pila del coprocesador los valores de punto flotante.
                codigoAssembler.append("FLD ").append("__").append(operando1).append("\n");
                codigoAssembler.append("FDIV "); // Hacemos la división de los operandos recién cargados en la pila. 
                variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE);
                codigoAssembler.append("FSTP ").append(contadorVariablesAux).append("\n"); // Almacenamos el resultado y desapilamos.
                break;
            
            case ">=":

                break;

            case "<=":

                break;
            
            case ">":
                
                break;
            
            case "<":

                break;
            
            case "!!":

                break;
        }
    }

    private String generarVariableAuxiliar(String tipo/*, int numeroTerceto*/){ //Generamos la variable auxiliar que voy a necesitar para las conversiones y las operaciones aritméticas.
        String variableAuxiliar = "@aux" + contadorVariablesAux;
        ++contadorVariablesAux;
        TablaSimbolos.addIdentificador(variableAuxiliar);
        TablaSimbolos.addAtributo(TablaSimbolos.getTypeLexema(variableAuxiliar), "tipo", tipo);
        //tercetosAsociados.put(variableAuxiliar, numeroTerceto);
        return variableAuxiliar; 
    }

    /*public int getTercetoAsociado(String variableAuxiliar){ //Según alguna variable auxiliar que nos manden, nos fijamos el terceto. 
        return tercetosAsociados.getOrDefault(variableAuxiliar, -1);
    }*/

    private static void escribirCodigoEnArchivo(String nombreArchivo, String codigoAssembler) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            writer.write(codigoAssembler);
        } catch (IOException e) {
            e.printStackTrace();  
        }
    }















  

}  
