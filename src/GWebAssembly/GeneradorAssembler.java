package GWebAssembly;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import GCodigo.Terceto;
import GCodigo.Tercetos;
import Tools.TablaSimbolos;
import Tools.TablaTipos;

public class GeneradorAssembler {

    public static StringBuilder codigoAssembler = new StringBuilder();
    public static HashMap<String, Integer> tercetosAsociados = new HashMap<>();
    private static final Stack<String> pilaFunciones = new Stack<>(); // Para controlar la recursividad en una
                                                                      // función.
    private static String auxiliar2bytes = "@variable2bytes";

    private static final String AUX = "@aux";
    private static String auxiliar = "";
    private static final String OVERFLOW_SUMA_PFLOTANTE = "Error: se excedió el límite permitido (overflow)";
    private static final String OVERFLOW_PRODUCTO_ENTERO_SIN_SIGNO = "Error: se excedió el límite permitido (overflow)";
    private static final String OVERFLOW_PRODUCTO_ENTERO_CON_SIGNO = "Error: se excedió el límite permitido (overflow)";
    private static final String INVOCACION_RECURSIVA = "Error: no se permiten declaraciones recursivas.";
    private static final String ERROR_MSJ_POR_PANTALLA = "Error: se terminará el programa.";

    private static String tag = null; //Ambito
    private static String OP = null;
    private static String OP1 = null;
    private static String OP2 = null;
    private static String type = null;
    private static int number = 0;
    private static String salto = "";
    private static String label = "";

    private static String getOperando(String r) {
        if (r.contains("["))
        {
            r = r.substring(1, r.length()-1);
            if (r.equals(Terceto.UNDEFINED))
                return r;
            

            if (Integer.valueOf(r) > number)
                return tag + "_" + Terceto.LABEL + r + ":";
                        
            return AUX + r + tag;
        }else{
            if(OP.equals("PRINT")){
                return r;
            }
            if (esConstante(r)){
                r = r.replaceAll("\\D", "");
                return r;
            }
            if(r.startsWith("@")){
                return r;
            }
            return "__" + r;
        }
    }

    public static void generarCodigoAssembler(Tercetos tercetosGenerados) {
        for (Map.Entry<String, ArrayList<Terceto>> func : tercetosGenerados.getTercetos().entrySet()) {
            tag = func.getKey();

            codigoAssembler.append(tag + ":").append('\n');
            for (Terceto terceto : func.getValue()) {
                number = terceto.getNumber();
                type = terceto.getType();
                OP = terceto.getFirst();
                OP1 = getOperando(terceto.getSecond());
                OP2 = getOperando(terceto.getThird());
                
                if (type != null && type.equals(Terceto.ERROR)) {
                    codigoAssembler.append("invoke MessageBoxA, NULL, ADDR _ERROR_POR_PANTALLA, ADDR title, MB_OK \n");
                    codigoAssembler.append("invoke ExitProcess, 0\n");
                    codigoAssembler.append("end " + tag);
                } else    
                switch (OP) {
                    case "*":
                    case "+":
                    case "-":
                    case "/":
                    case "=":
                    case ">=":
                    case ">":
                    case "<=":
                    case "<":
                    case "!!":
                    case "==":
                        switch (type) {
                            case TablaTipos.UINT_TYPE:
                                generarCodigoOperacionesEnterosSinSigno();
                                break;
                            case TablaTipos.LONG_TYPE:
                                generarCodigoOperacionesEnterosConSigno();
                                break;
                            case TablaTipos.DOUBLE_TYPE:
                                generarCodigoOperacionesDouble();
                                break;
                            default:
                                break;
                        }

                        break;
                    case "UB":
                        // Nos fijamos a dónde tenemos que saltar en el segundo operando.
                        generarAssemblerSaltoIncondicional();
                        break;

                    case "CB":
                        // Nos fijamos a dónde tenemos que saltar en el segundo operando.
                        // Mirar label de donde saltar, en generarOperando, en teoria lo tendria
                        generarAssemblerSaltoCondicional();
                        break;

                    case "CALL":
                        generarAssemblerInvocacion();
                        break;

                    case "RETURN":
                        generarAssemblerReturn();
                        break;

                    case "TOD":
                        generarAssemblerTOD();
                        break;

                    case "PRINT":
                        generarAssemblerPrint();
                        break;

                    default:
                        if (OP.contains(Terceto.LABEL)) {
                            codigoAssembler.append(tag + "_" + OP + ":").append("\n");
                        } else {
                            codigoAssembler
                                    .append("invoke MessageBoxA, NULL, ADDR _ERROR_POR_PANTALLA, ADDR title, MB_OK \n");
                            codigoAssembler.append("invoke ExitProcess, 0\n");
                            codigoAssembler.append("end START");
                        }
                        break;
                }

            }
        }

        codigoAssembler.append("invoke ExitProcess, 0\n")
                .append("end " + tag);
        generarCodigoLibrerias();
    }

    public static void generarCodigoLibrerias() { // Importamos las librerías que se necesitan en el Assembler.
        StringBuilder header = new StringBuilder();
        header.append(".386\n")
                .append(".model flat, stdcall\n")
                .append("option casemap :none\n")
                .append("include \\masm32\\include\\windows.inc\n")
                .append("include \\masm32\\include\\kernel32.inc\n")
                .append("include \\masm32\\include\\masm32.inc\n")
                .append("includelib \\masm32\\lib\\kernel32.lib\n")
                .append("includelib \\masm32\\lib\\masm32.lib\n")
                .append(".DATA\n") // Empieza la declaración de variables. Primero agregamos las constantes para
                                   // los errores.
                .append(auxiliar2bytes).append(" dw ? \n")
                .append("_OVERFLOW_PRODUCTO_ENTERO db \"" + OVERFLOW_PRODUCTO_ENTERO_CON_SIGNO + "\", 0\n")
                .append("_OVERFLOW_PRODUCTO_ENTERO db \"" + OVERFLOW_PRODUCTO_ENTERO_SIN_SIGNO + "\", 0\n")
                .append("_OVERFLOW_SUMA_PFLOTANTE db \"" + OVERFLOW_SUMA_PFLOTANTE + "\", 0\n")
                .append("_INVOCACION_RECURSIVA db \"" + INVOCACION_RECURSIVA + "\", 0\n")
                .append("_ERROR_POR_PANTALLA db \"" + ERROR_MSJ_POR_PANTALLA + "\", 0\n");

        generarCodigoVariables(header);

        header.append(".CODE\n")
                .append("START:\n");
        header.append(codigoAssembler);
        codigoAssembler = header;

    }

    public static void generarCodigoVariables(StringBuilder librerias) { // Generamos el código para las variables
                                                                         // declaradas.
        for (String func : TablaSimbolos.getTablaSimbolos()) {
            String tipo = TablaSimbolos.getTypeLexema(func);
            if (func.startsWith("@")) {
                if (tipo.equals(TablaTipos.LONG_TYPE)) {
                    librerias.append(func).append(" dd ? \n");
                } else if (tipo.equals(TablaTipos.UINT_TYPE))
                    librerias.append(func).append(" dw ? \n");
                else if (tipo.equals(TablaTipos.LONG_TYPE)) {
                    librerias.append(func).append(" dq ? \n");
                } else if (tipo.equals(TablaTipos.STRING)) {
                    librerias.append(func).append(", 0").append("\n");
                }
            }
            System.out.println("EL TIPO DE " + func + " ES " + tipo);
            switch (tipo) {
                case TablaTipos.UINT_TYPE:
                    if (!func.matches(".*\\d.*")) { // Si no es una constante, la declaramos como variable con su
                                                    // lexema.
                        librerias.append("__").append(func).append(" dw ? \n");
                    }
                    break;
                case TablaTipos.DOUBLE_TYPE:
                    if (!func.matches(".*\\d.*")) {
                        librerias.append("__").append(func).append(" dq ? \n");
                    }
                    break;
                case TablaTipos.LONG_TYPE:
                    if (!func.matches(".*\\d.*")) {
                        librerias.append("__").append(func).append(" dd ? \n");
                    }
                    break;
                default:
                    //TENGO QUE VER LAS FUNCIONES, VER CODIGO DE EJEMPLO
                    //SE DECLARAN EN .CODE TODO SU CODIGO.
                    break;
            }

        }

    }

    public static void generarConversionExplicita(String auxiliar) {
        // El auxiliar es para guardar la conversion del tod
        codigoAssembler.append("FILD ").append(OP1).append("\n");
    }

    public static void generarAssemblerPrint(){
        String aux = generarVariableAuxiliarString();
        codigoAssembler.append("MOV AH, 9").append("\n");
        codigoAssembler.append("MOV DX, ").append(aux.subSequence(0, 5)).append("\n");
        codigoAssembler.append("INT 21h ").append("\n");
        codigoAssembler.append("MOV AH, 4CH").append("\n");
        codigoAssembler.append("INT 21h").append("\n");
    }

    public static void generarCodigoOperacionesEnterosConSigno() { 
        switch (OP) {
            case "+":
                    codigoAssembler.append("MOV EAX, ").append(OP1).append("\n");
                    codigoAssembler.append("ADD EAX, ").append(OP2).append("\n");
                    auxiliar = generarVariableAuxiliar();
                    codigoAssembler.append("MOV ").append(auxiliar).append(", EAX\n");
                break;
            case "-":
                    codigoAssembler.append("MOV EAX, ").append(OP1).append("\n");
                    codigoAssembler.append("SUB EAX, ").append(OP2).append("\n");
                    auxiliar = generarVariableAuxiliar();
                    codigoAssembler.append("MOV ").append(auxiliar).append(", EAX\n");
            case "*":
                    codigoAssembler.append("MOV EAX, ").append(OP1).append("\n");
                    codigoAssembler.append("MUL EAX, ").append(OP2).append("\n");
                    auxiliar = generarVariableAuxiliar();
                    codigoAssembler.append("MOV ").append(auxiliar).append(", EAX\n");
            case "=":
                    codigoAssembler.append("MOV EAX, ").append(OP2).append("\n");
                    codigoAssembler.append("MOV ").append(OP1).append(", EAX\n"); 
                break;
            case "/":
                    auxiliar = generarVariableAuxiliar();
                    codigoAssembler.append("MOV EAX, ").append(OP1).append("\n");
                    codigoAssembler.append("DIV EAX, ").append(OP2);
                    codigoAssembler.append("MOV ").append(auxiliar).append(", EAX");
                break;
            case "==":
                    codigoAssembler.append("MOV EAX, ").append(OP2).append("\n");
                    codigoAssembler.append("CMP EAX, ").append(OP1).append("\n");
                    salto = "JE";
                break;

            case ">=": // JAE, JB
                    codigoAssembler.append("MOV EAX, ").append(OP2).append("\n");
                    codigoAssembler.append("CMP EAX, ").append(OP1).append("\n");
                    salto = "JLE ";
                    /*
                    .data

                    x dw 10
                    y dw 5

                    .code

                    start:

                    ; Comparamos los dos números
                    cmp x, y

                    ; Si los números son iguales, saltamos a la etiqueta `equal`
                    je equal

                    ; Los números no son iguales, por lo que salimos del IF
                    jmp exit

                    equal:

                    ; Los números son iguales, por lo que imprimimos un mensaje
                    mov eax, 1
                    call writedec

                    ; Continuamos con la ejecución del programa
                    jmp exit

                    exit:

                    ; Salimos del programa
                    invoke ExitProcess, 0


                    */
                break;
            case "<=": // JBE, JA
                    codigoAssembler.append("MOV EAX, ").append(OP2).append("\n");
                    codigoAssembler.append("CMP EAX, ").append(OP1).append("\n");
                    salto = "JLE ";
                break;
            case ">": // JG, JBE
                    codigoAssembler.append("MOV EAX, ").append(OP2).append("\n");
                    codigoAssembler.append("CMP EAX, ").append(OP1).append("\n");
                    salto = "JG ";
                    System.out.println("esto en mayor.");
                break;

            case "<":// JMP, JAE
                    codigoAssembler.append("MOV EAX, ").append(OP2).append("\n");
                    codigoAssembler.append("CMP EAX, ").append(OP1).append("\n");
                    salto = "JL ";
                break;
            case "!!":
                    codigoAssembler.append("MOV EAX, ").append(OP2).append("\n");
                    codigoAssembler.append("CMP EAX, ").append(OP1).append("\n");
                    salto = "JNE ";
                break;

            default:
                codigoAssembler.append("invoke MessageBoxA, NULL, ADDR _ERROR_POR_PANTALLA, ADDR title, MB_OK \n");
                codigoAssembler.append("invoke ExitProcess, 0\n");
                codigoAssembler.append("end START");
                break;
        }
    }


    public static void generarCodigoOperacionesEnterosSinSigno() { 
        switch (OP) {
            case "+":
                    codigoAssembler.append("MOV AX, ").append(OP1).append("\n");
                    codigoAssembler.append("ADD AX, ").append(OP2).append("\n");
                    auxiliar = generarVariableAuxiliar();
                    codigoAssembler.append("MOV ").append(auxiliar).append(", AX\n");
                break;
            case "-":
                    codigoAssembler.append("MOV AX, ").append(OP1).append("\n");
                    codigoAssembler.append("SUB AX, ").append(OP2).append("\n");
                    auxiliar = generarVariableAuxiliar();
                    codigoAssembler.append("MOV ").append(auxiliar).append(", AX\n");
            case "*":
                    codigoAssembler.append("MOV AX, ").append(OP1).append("\n");
                    codigoAssembler.append("MUL AX, ").append(OP2).append("\n");
                    auxiliar = generarVariableAuxiliar();
                    codigoAssembler.append("MOV ").append(auxiliar).append(", AX\n");
            case "=":
                    codigoAssembler.append("MOV AX, ").append(OP2).append("\n");
                    codigoAssembler.append("MOV ").append(OP1).append(", AX\n"); 
                break;
            case "/":
                    auxiliar = generarVariableAuxiliar();
                    codigoAssembler.append("MOV AX, ").append(OP1).append("\n");
                    codigoAssembler.append("DIV AX, ").append(OP2);
                    codigoAssembler.append("MOV ").append(auxiliar).append(", AX");
                break;
            case "==":
                    codigoAssembler.append("MOV AX, ").append(OP2).append("\n");
                    codigoAssembler.append("CMP AX, ").append(OP1).append("\n");
                    salto = "JE ";
                break;

            case ">=": 
                    codigoAssembler.append("MOV AX, ").append(OP2).append("\n");
                    codigoAssembler.append("CMP AX, ").append(OP1).append("\n");
                    salto = "JGE ";
                break;

            case "<=": 
                    codigoAssembler.append("MOV AX, ").append(OP2).append("\n");
                    codigoAssembler.append("CMP AX, ").append(OP1).append("\n");
                    salto = "JLE ";
                break;
            case ">":
                    codigoAssembler.append("MOV AX, ").append(OP2).append("\n");
                    codigoAssembler.append("CMP AX, ").append(OP1).append("\n");
                    salto = "JG ";
                break;

            case "<":
                    codigoAssembler.append("MOV AX, ").append(OP2).append("\n");
                    codigoAssembler.append("CMP AX, ").append(OP1).append("\n");
                    salto = "JL ";
                break;
            case "!!":
                    codigoAssembler.append("MOV AX, ").append(OP2).append("\n");
                    codigoAssembler.append("CMP AX, ").append(OP1).append("\n");
                    salto = "JNE ";
                break;

            default:
                codigoAssembler.append("invoke MessageBoxA, NULL, ADDR _ERROR_POR_PANTALLA, ADDR title, MB_OK \n");
                codigoAssembler.append("invoke ExitProcess, 0\n");
                codigoAssembler.append("end START");
                break;
        }
    }

    public static void generarCodigoOperacionesDouble() {
        String auxiliar = "@auxDouble";
        switch (OP) {
            case "+":
                codigoAssembler.append("FLD ").append(OP2).append("\n");
                codigoAssembler.append("FLD ").append(OP1).append("\n");
                codigoAssembler.append("FADD "); 
                auxiliar = generarVariableAuxiliar();
                codigoAssembler.append("FSTP ").append(auxiliar).append("\n");
                generarAssemblerOverflowFlotantes(auxiliar);
                break;
            case "-":
                codigoAssembler.append("FLD ").append(OP2).append("\n");
                codigoAssembler.append("FLD ").append(OP1).append("\n");
                codigoAssembler.append("FSUB "); 
                auxiliar = generarVariableAuxiliar();
                codigoAssembler.append("FSTP ").append(auxiliar).append("\n");
                generarAssemblerOverflowFlotantes(auxiliar);
                break;

            case "*":
                codigoAssembler.append("FLD ").append(OP2).append("\n");
                codigoAssembler.append("FLD ").append(OP1).append("\n");
                codigoAssembler.append("FMUL "); 
                auxiliar = generarVariableAuxiliar();
                codigoAssembler.append("FSTP ").append(auxiliar).append("\n");
                generarAssemblerOverflowFlotantes(auxiliar);
                break;

            case "/":
                codigoAssembler.append("FLD ").append(OP2).append("\n");
                codigoAssembler.append("FLD ").append(OP1).append("\n");
                codigoAssembler.append("FDIV "); 
                auxiliar = generarVariableAuxiliar();
                codigoAssembler.append("FSTP ").append(auxiliar).append("\n");
                break;
            case "=":
                codigoAssembler.append("FLD ").append(OP2).append("\n");
                codigoAssembler.append("FSTP ").append(OP1).append("\n");
                break;
            case ">=":
                codigoAssembler.append("FLD ").append(OP2).append("\n");
                codigoAssembler.append("FCOM ").append(OP1).append("\n");
                codigoAssembler.append("FSTSW ").append(auxiliar).append("\n");
                codigoAssembler.append("MOV AX ").append(auxiliar).append("\n");
                codigoAssembler.append("SAHF ").append("\n");
                auxiliar = generarVariableAuxiliar();
                codigoAssembler.append("MOV ").append(auxiliar).append(" OFFh\n");
                codigoAssembler.append("JAE ").append(auxiliar.substring(1)).append("\n");
                codigoAssembler.append("MOV ").append(auxiliar).append(" 00h\n");
                codigoAssembler.append(auxiliar.substring(1)).append("\n");
                break;
            case "<=":
                codigoAssembler.append("FLD ").append(OP2).append("\n");
                codigoAssembler.append("FCOM ").append(OP1).append("\n");
                codigoAssembler.append("FSTSW ").append(auxiliar).append("\n");
                codigoAssembler.append("MOV AX ").append(auxiliar).append("\n");
                codigoAssembler.append("SAHF ").append("\n");
                auxiliar = generarVariableAuxiliar();
                codigoAssembler.append("MOV ").append(auxiliar).append(" OFFh\n");
                codigoAssembler.append("JBE ").append(auxiliar.substring(1)).append("\n");
                codigoAssembler.append("MOV ").append(auxiliar).append(" 00h\n");
                codigoAssembler.append(auxiliar.substring(1)).append("\n");
                break;
            case ">":
                codigoAssembler.append("FLD ").append(OP2).append("\n");
                codigoAssembler.append("FCOM ").append(OP1).append("\n");
                codigoAssembler.append("FSTSW ").append(auxiliar).append("\n");
                codigoAssembler.append("MOV AX ").append(auxiliar).append("\n");
                codigoAssembler.append("SAHF ").append("\n");
                auxiliar = generarVariableAuxiliar();
                codigoAssembler.append("MOV ").append(auxiliar).append(" OFFh\n");
                codigoAssembler.append("JA ").append(auxiliar.substring(1)).append("\n");
                codigoAssembler.append("MOV ").append(auxiliar).append(" 00h\n");
                codigoAssembler.append(auxiliar.substring(1)).append("\n");
                break;
            case "<":
                codigoAssembler.append("FLD ").append(OP2).append("\n");
                codigoAssembler.append("FCOM ").append(OP1).append("\n");
                codigoAssembler.append("FSTSW ").append(auxiliar).append("\n");
                codigoAssembler.append("MOV AX ").append(auxiliar).append("\n");
                codigoAssembler.append("SAHF ").append("\n");
                auxiliar = generarVariableAuxiliar();
                codigoAssembler.append("MOV ").append(auxiliar).append(" OFFh\n");
                codigoAssembler.append("JB ").append(auxiliar.substring(1)).append("\n");
                codigoAssembler.append("MOV ").append(auxiliar).append(" 00h\n");
                codigoAssembler.append(auxiliar.substring(1)).append("\n");
                break;
            case "!!":
                codigoAssembler.append("FLD ").append(OP2).append("\n");
                codigoAssembler.append("FCOM ").append(OP1).append("\n");
                codigoAssembler.append("FSTSW ").append(auxiliar).append("\n");
                codigoAssembler.append("MOV AX ").append(auxiliar).append("\n");
                codigoAssembler.append("SAHF ").append("\n");
                auxiliar = generarVariableAuxiliar();
                codigoAssembler.append("MOV ").append(auxiliar).append(" OFFh\n");
                codigoAssembler.append("JNE ").append(auxiliar.substring(1)).append("\n");
                codigoAssembler.append("MOV ").append(auxiliar).append(" 00h\n");
                codigoAssembler.append(auxiliar.substring(1)).append("\n");
                break;
            case "==":
                codigoAssembler.append("FLD ").append(OP2).append("\n");
                codigoAssembler.append("FCOM ").append(OP1).append("\n");
                codigoAssembler.append("FSTSW ").append(auxiliar).append("\n");
                codigoAssembler.append("MOV AX ").append(auxiliar).append("\n");
                codigoAssembler.append("SAHF ").append("\n");
                auxiliar = generarVariableAuxiliar();
                codigoAssembler.append("MOV ").append(auxiliar).append(" OFFh\n");
                codigoAssembler.append("JE ").append(auxiliar.substring(1)).append("\n");
                codigoAssembler.append("MOV ").append(auxiliar).append(" 00h\n");
                codigoAssembler.append(auxiliar.substring(1)).append("\n");
                break;

            default:
                codigoAssembler.append("invoke MessageBoxA, NULL, ADDR _ERROR_POR_PANTALLA, ADDR title, MB_OK \n");
                codigoAssembler.append("invoke ExitProcess, 0\n");
                codigoAssembler.append("end START");
                break;
        }
    }

    public static void generarAssemblerOverflowEnterosConSigno(String variableAuxiliar) { // Controlamos el overflow del
                                                                                          // producto entre enteros (con
                                                                                          // signo).
        codigoAssembler.append("JNO ").append(variableAuxiliar.substring(1)); // Chequeamos el flag OF que indica
                                                                              // overflow en enteros con signo.
        codigoAssembler.append(
                "invoke MessageBox, NULL, addr _OVERFLOW_PRODUCTO_ENTERO_CON_SIGNO, addr _OVERFLOW_PRODUCTO_ENTERO_CON_SIGNO, MB_OK\n"); // Manejamos
                                                                                                                                         // el
                                                                                                                                         // overflow
                                                                                                                                         // con
                                                                                                                                         // un
                                                                                                                                         // cartel
                                                                                                                                         // de
                                                                                                                                         // error.
        codigoAssembler.append("invoke ExitProcess, 0\n"); // Si hay overflow, emitimos el mensaje de error anterior y
                                                           // terminamos.
        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta del salto si no hay overflow.
    }

    public static void generarAssemblerOverflowEnterosSinSigno(String variableAuxiliar) { // Controlamos el overflow del
                                                                                          // producto entre enteros (sin
                                                                                          // signo).
        codigoAssembler.append("JNC ").append(variableAuxiliar.substring(1)); // Chequeamos el flag CF que indica
                                                                              // overflow en enteros sin signo.
        codigoAssembler.append(
                "invoke MessageBox, NULL, addr _OVERFLOW_PRODUCTO_ENTERO_SIN_SIGNO, addr _OVERFLOW_PRODUCTO_ENTERO_SIN_SIGNO, MB_OK\n");
        codigoAssembler.append("invoke ExitProcess, 0\n");
        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n");
    }

    public static void generarAssemblerOverflowFlotantes(String variableAuxiliar) {
        // Comprueba el bit de overflow en el registro de flags. JA para mayor, JB para
        // menor.
        codigoAssembler.append("FSTSW AX\n"); // Nos fijamos si hay overflow (estado del coprocesador) y lo guardamos en
                                              // AX.
        codigoAssembler.append("SAHF\n"); // Mueve los flags del estado de la palabra al registro de flags del
                                          // procesador.
        codigoAssembler.append("JA ").append(variableAuxiliar.substring(1)).append("\n"); // Salta a la etiqueta si no
                                                                                          // hay overflow.
        codigoAssembler.append(
                "invoke MessageBox, NULL, addr _OVERFLOW_SUMA_PFLOTANTE, addr _OVERFLOW_SUMA_PFLOTANTE, MB_OK\n");
        codigoAssembler.append("invoke ExitProcess, 0\n");
        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta del salto si no hay overflow.
    }

    // @main 
    // ..
    // f(), -> CALL, f@main, [-]
    // Label@fmain
    // ..
    
    // RETURN Label@main
    //UB, [-], [30] 
    public static void generarAssemblerSaltoIncondicional() {
        codigoAssembler.append("JMP ").append(OP2).append("\n");
    }

    public static void generarAssemblerSaltoCondicional() {
        System.out.println("SATLO " + salto); //no llega els alto
        switch (salto) {
            case "JE ": //Equal
                codigoAssembler.append("JNE ").append(OP2).append("\n");
                break;
            case "JNE ": //Non equal
                codigoAssembler.append("JE ").append(OP2).append("\n");
                break;
            case "JLE ": //Less Equal
                codigoAssembler.append("JBE ").append(OP2).append("\n");
                break;
            case "JGE ": //Greater Equal
                codigoAssembler.append("JG ").append(OP2).append("\n");
                break;
            case "JL ": //Less
                codigoAssembler.append("JMP ").append(OP2).append("\n");
                break;
            case "JG ": //Greater
                codigoAssembler.append("JMP ").append(OP2).append("\n");
                break;
            default:
                System.out.println("hola estoy en defualts");
                break;
        }
    }

    public static void generarAssemblerInvocacion() {

        if (tag.equals(OP1)) {
            System.out.println("hay recursividad viejo, pero revisalo");
            return;
        }


        if (OP2.contains(Terceto.UNDEFINED)) // Nos fijamos si tiene parámetros.
            codigoAssembler.append("CALL ").append(OP1).append("\n");
        else {
            codigoAssembler.append("CALL ").append(OP1).append(OP2).append("\n"); // si es una referencia, ya se
                                                                                  // transformo
        }

    }

    public static void generarAssemblerReturn() {
        codigoAssembler.append("RET ").append("\n");    
    }

    public static void generarAssemblerTOD() {

        if (type.equals(Terceto.ERROR))
            return; // A chequear

        generarConversionExplicita(generarVariableAuxiliar());
    }

    public static String generarVariableAuxiliar() { // Generamos la variable auxiliar que
                                                     // vamos a necesitar para las
                                                     // conversiones y las operaciones
                                                     // aritméticas.
        String variableAuxiliar = AUX + number + tag;
        TablaSimbolos.addIdentificador(variableAuxiliar);
        TablaSimbolos.addTipo(type, variableAuxiliar);
        // System.out.println("Hice una variable auxiliar " + variableAuxiliar + " del
        // tipo " + TablaSimbolos.getTypeLexema(variableAuxiliar));

        return variableAuxiliar;
    }

    public static String generarVariableAuxiliarString() {
        String variableAuxiliar = AUX + number + " db " + "\"" + OP1 + "\"";
        TablaSimbolos.addIdentificador(variableAuxiliar);
        TablaSimbolos.addTipo(TablaTipos.STRING, variableAuxiliar);
        tercetosAsociados.put(variableAuxiliar, number); // Asociamos la variable auxiliar al número del terceto.
        return variableAuxiliar;

    }

    private static boolean esConstante(String s) { // Nos fijamos el uso para ver si es una constante o identificador.
        if (TablaSimbolos.getUse(s) == null) {
            return true;
        }
        return false;
    }

    public static void escribirCodigoEnArchivo(String nombreArchivo, String codigoAssembler) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            writer.write(codigoAssembler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
