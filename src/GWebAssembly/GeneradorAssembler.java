package GWebAssembly;
import java.util.ArrayList;
import java.util.HashMap;

import GCodigo.Terceto;
import Tools.TablaSimbolos;
import Tools.TablaTipos;

public class GeneradorAssembler {

    public static StringBuilder codigoAssembler = new StringBuilder();
    public static TablaSimbolos tablaSimbolos = new TablaSimbolos();
    public static int contadorVariablesAux = 0;
    public static HashMap<String, Integer> tercetosAsociados = new HashMap<>();

    private static final String OVERFLOW_SUMA_PFLOTANTE = "Error: se excedió el límite permitido (overflow)";
    private static final String OVERFLOW_PRODUCTO_ENTERO = "Error: se excedió el límite permitido (overflow)";
    private static final String INVOCACION_RECURSIVA = "Error: no se permiten declaraciones recursivas.";

    public void generarCodigoLibrerias(){ //Importamos las librerías que se necesitan en el Assembler.
        StringBuilder header = new StringBuilder();
        header.append(".386\n")
        .append(".model flat, stdcall\n")
        .append("option casemap :none\n")
        .append("include \\masm32\\include\\windows.inc\n")
        .append("include \\masm32\\include\\kernel32.inc\n")
        .append("include \\masm32\\include\\masm32.inc\n")
        .append("includelib \\masm32\\lib\\kernel32.lib\n")
        .append("includelib \\masm32\\lib\\masm32.lib\n")
        .append(".data\n"); //Empieza la declaración de variables. 
        generarCodigoVariables();


    }


    public StringBuilder generarCodigoVariables(){ //Generamos el código para las variables declaradas. 
            StringBuilder seccionVariables = new StringBuilder();
            for(String identificadorActual: TablaSimbolos.getSimbolos()){
                //String uso = TablaSimbolos.getUseLexema(identificadorActual);
                String tipo = TablaSimbolos.getTypeLexema(identificadorActual);
                String lexema = TablaSimbolos.getLexema(identificadorActual);
                //Hay que chequear el tipo FUNCTION?
                switch (tipo) {
                    case TablaTipos.UINT_TYPE:
                        if(lexema.startsWith("aux")){} //Si es variable auxiliar, deberíamos hacer algo, o es una variable más? 
                        seccionVariables.append(lexema).append(" dd ? \n");
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

    
    public static void generarCodigoOperacionesEnteros(String operador, String operando1, String operando2){
        String variableAuxiliar;
        switch (operador) {
            case "+":
                if(TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE) || TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE)){
                    codigoAssembler.append("MOV EAX, ").append(operando1).append("\n"); //Movemos el valor del OP1 al registro EAX (32 bits).
                    codigoAssembler.append("ADD EAX, ").append(operando2).append("\n"); //Sumamos los operandos.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE);
                } else {
                    codigoAssembler.append("MOV AX, ").append(operando1).append("\n"); //Movemos el valor del OP1 al registro AX (16 bits).
                    codigoAssembler.append("ADD AX, ").append(operando2).append("\n"); //Sumamos los operandos.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE);
                }
                codigoAssembler.append("MOV ").append(variableAuxiliar).append(", ").append((TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)) ? "EAX" : "AX").append("\n");
                break;
            case "-": //Creo que deberiamos tener en cuenta ambos operandos, o lo dejamos en el mas abarcativo directamente? 
                if(TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE) || TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE)){
                    codigoAssembler.append("MOV EAX, ").append(operando1).append("\n"); //Movemos el valor del OP1 al registro EAX (32 bits).
                    codigoAssembler.append("SUB EAX, ").append(operando2).append("\n"); //Restamos los operandos.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE);
                } else {
                    codigoAssembler.append("MOV AX, ").append(operando1).append("\n"); //Movemos el valor del OP1 al registro AX (16 bits).
                    codigoAssembler.append("SUB AX, ").append(operando2).append("\n"); //Restamos los operandos.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE);
                }
                codigoAssembler.append("MOV ").append(variableAuxiliar).append(", ").append((TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)) ? "EAX" : "AX").append("\n");
                break;
            case "*":
                if(TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE) || TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE)){
                    codigoAssembler.append("MOV EAX, ").append(operando1).append("\n"); //Movemos el valor del OP1 al registro EAX (32 bits).
                    codigoAssembler.append("MUL, ").append(operando2).append("\n"); //Multiplicamos los operandos.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE);
                } else {
                    codigoAssembler.append("MOV AX, ").append(operando1).append("\n"); //Movemos el valor del OP1 al registro AX (16 bits).
                    codigoAssembler.append("MUL, ").append(operando2).append("\n"); //Multiplicamos los operandos.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE);
                }
                //Ver overflow.
                codigoAssembler.append("MOV ").append(variableAuxiliar).append(", ").append((TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)) ? "EAX" : "AX").append("\n");
                break;
            case "=":
                if (TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE) || TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)) {
                    // Asignación de enteros largos (32 bits)
                    codigoAssembler.append("MOV EAX, ").append(operando2).append("\n"); // Movemos el valor de op2 a EAX
                    codigoAssembler.append("MOV ").append(operando1).append(", EAX\n"); // Asignamos el valor de EAX a op1
                } else {
                    // Asignación de enteros sin signo (16 bits)
                    codigoAssembler.append("MOV AX, ").append(operando2).append("\n"); // Movemos el valor de op2 a AX
                    codigoAssembler.append("MOV ").append(operando1).append(", AX\n"); // Asignamos el valor de AX a op1
            }
            break;
            case "/":
            if (TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE) || TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)) {
                variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE);


            } else {
                variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE);
                codigoAssembler.append("MOV AX ").append(operando1).append("\n"); //Guardo el dividendo en el registro AX (16 bits).
                codigoAssembler.append("XOR DX, DX"); //Borro el registro DX para despues almacenar el resto de la division.
                codigoAssembler.append("DIV ").append(operando2); //Divido y ya se guardan el cociente y el resto en AX y DX respectivamente.
                codigoAssembler.append("MOV ").append(variableAuxiliar).append(", AX"); //Guardo el resultado de la división en AX (16 bits).
            }

                break;
        
        }
    }

    public static String generarVariableAuxiliar(String tipo/*, int numeroTerceto*/){ //Generamos la variable auxiliar que voy a necesitar para las conversiones y las operaciones aritméticas.
        String variableAuxiliar = "aux" + contadorVariablesAux;
        ++contadorVariablesAux;
        TablaSimbolos.addIdentificador(variableAuxiliar);
        TablaSimbolos.addAtributo(TablaSimbolos.getTypeLexema(variableAuxiliar), "tipo", tipo);
        //tercetosAsociados.put(variableAuxiliar, numeroTerceto);
        return variableAuxiliar; 
    }

    /*public int getTercetoAsociado(String variableAuxiliar){ //Según alguna variable auxiliar que nos manden, nos fijamos el terceto. 
        return tercetosAsociados.getOrDefault(variableAuxiliar, -1);
    }*/















  

}  
