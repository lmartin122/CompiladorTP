package GAssembler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import GCodigo.Scope;
import GCodigo.Terceto;
import GCodigo.Tercetos;
import Tools.TablaSimbolos;
import Tools.TablaTipos;

public class GeneradorAssembler {

    public static StringBuilder codigoAssembler = new StringBuilder();
    private static StringBuilder codigoFunciones = new StringBuilder();
    public static HashMap<String, Integer> tercetosAsociados = new HashMap<>();
    
    private static String auxiliar2bytes = "@variable2bytes";

    private static final String AUX = Scope.SEPARATOR + "aux";
    private static String auxiliar = "";
    private static final String TAG_RECURSIVIDAD = "recursividad";
    private static final String TAG_OVERFLOW_UINT = "overflow_UINT";
    private static final String TAG_OVERFLOW_LONG ="overflow_LONG";
    private static final String TAG_OVERFLOW_DOUBLE ="overflow_DOUBLE";
    private static final String OVERFLOW_SUMA_PFLOTANTE = "Error: se excedió el límite permitido (overflow)";
    private static final String OVERFLOW_PRODUCTO_ENTERO_SIN_SIGNO = "Error: se excedió el límite permitido (overflow)";
    private static final String OVERFLOW_PRODUCTO_ENTERO_CON_SIGNO = "Error: se excedió el límite permitido (overflow)";
    private static final String INVOCACION_RECURSIVA = "Error: no se permiten declaraciones recursivas.";
    private static final String ERROR_MSJ_POR_PANTALLA = "Error: se terminará el programa.";
    private static final String _RECURSIVIDAD = "Error: no se permiten llamadas recursivas.";

    private static String tag = null; //Ambito
    private static String OP = null;
    private static String OP1 = null;
    private static String OP2 = null;
    private static String type = null;
    private static String uso = null;
    private static int number = 0;
    private static String salto = "";


    private static String getOperando(String r) {
        if (r.contains("["))
        {
            r = r.substring(1, r.length()-1);
            if (r.equals(Terceto.UNDEFINED))
                return r;
            
            if(OP.equals("UB") || OP.equals("CB")){
                return tag + "_" + Terceto.LABEL + r;
            }

            return AUX + r + tag;
        }else{
            
            if (esConstante(r)){
                r = "_cte_" + r.replaceAll("\\.","");
                return r;
            }
            r = r.replaceAll("\\.","\\_");
            if(OP.equals("CALL"))
                return r;
            
            if(OP.equals("PRINT"))
                return "__" + r;
            
                            
            return "__" + r;
        }
    }

    public static void generarCodigoAssembler(Tercetos tercetosGenerados) {
        generarAssemblerRecursividad();
        generarAssemblerOverflowEnterosSinSigno();
        generarAssemblerOverflowEnterosConSigno();
        generarAssembelerOverflowDouble();
        
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
                    codigoAssembler.append("invoke MessageBoxA, NULL, ADDR _ERROR_POR_PANTALLA, ADDR _ERROR_POR_PANTALLA, MB_OK \n");
                    codigoAssembler.append("invoke ExitProcess, 0\n");
                    codigoAssembler.append("end " + tag + "\n");
                } else { 
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
                    case "UB": //Salto incondicional.
                        generarAssemblerSaltoIncondicional();
                        break;

                    case "CB": //Salto condicional
                        generarAssemblerSaltoCondicional();
                        break;

                    case "CALL": //Llamada a función
                        generarAssemblerInvocacion();
                        break;

                    case "RETURN": //Cierre de función
                        generarAssemblerReturn();
                        break;

                    case "TOD": //Conversión explícita
                        generarAssemblerTOD();
                        break;

                    case "PRINT": //Invocación de una cadena de texto
                        generarAssemblerPrint();
                        break;

                    default: //Terceto con una etiqueta para saltar. 
                        if (OP.contains(Terceto.LABEL)) {
                            codigoAssembler.append(tag + "_" + OP + ":").append("\n");
                        } else {
                            codigoAssembler
                                    .append("invoke MessageBoxA, NULL, ADDR _ERROR_POR_PANTALLA, ADDR _ERROR_POR_PANTALLA, MB_OK \n");
                            codigoAssembler.append("invoke ExitProcess, 0\n");
                            codigoAssembler.append("end START\n");
                        }
                        break;
                }
                }
                    
                
            }
            
        }
        
        codigoAssembler.append("invoke ExitProcess, 0\n")
                .append("end " + tag + "\n");
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
                .append("include \\masm32\\include\\user32.inc\n")
                .append("includelib \\masm32\\lib\\masm32.lib\n")
                .append("includelib \\masm32\\lib\\user32.lib\n")
                .append(".DATA\n") // Empieza la declaración de variables. Primero agregamos las constantes para los errores.
                .append(auxiliar2bytes).append(" dw ? \n")
                .append("_OVERFLOW_PRODUCTO_ENTERO_CON_SIGNO db \"" + OVERFLOW_PRODUCTO_ENTERO_CON_SIGNO + "\", 0\n")
                .append("_OVERFLOW_PRODUCTO_ENTERO_SIN_SIGNO db \"" + OVERFLOW_PRODUCTO_ENTERO_SIN_SIGNO + "\", 0\n")
                .append("_OVERFLOW_SUMA_PFLOTANTE db \"" + OVERFLOW_SUMA_PFLOTANTE + "\", 0\n")
                .append("_INVOCACION_RECURSIVA db \"" + INVOCACION_RECURSIVA + "\", 0\n")
                .append("_ERROR_POR_PANTALLA db \"" + ERROR_MSJ_POR_PANTALLA + "\", 0\n")
                .append("_RECURSIVIDAD db \"" + _RECURSIVIDAD + "\", 0\n")
                .append("_flagRecursividad DWORD 0\n");


        generarCodigoVariables(header);
        codigoAssembler.append("\n");
        
        header.append(".CODE\n");
        header.append(codigoFunciones);
        header.append(codigoAssembler);
        codigoAssembler = header;

    }

    private static String getPrefix(String ref){
        return (ref.startsWith(Scope.SEPARATOR))? "" : "__";
    }

    public static void generarCodigoVariables(StringBuilder librerias) { // Generamos el código para las variables declaradas.
        for (String func : TablaSimbolos.getTablaSimbolos()) {
            type = TablaSimbolos.getTypeLexema(func);
            uso = TablaSimbolos.getUse(func);
            
            if(TablaSimbolos.isFunction(func)){ //Si es una función, declaramos la constante para controlar la recursividad.
                librerias.append("__").append(func).append(" DWORD 0 \n");
            } else {
                if (type != null){
                    switch (type) { //Si no, declaramos la constante o variable para cada símbolo correspondiente.
                    case TablaTipos.UINT_TYPE:
                        if (!esConstante(func)) {
                            librerias.append(getPrefix(func)).append(func.replaceAll("[.-]","\\_")).append(" dw ? \n");
                        } else {
                            librerias.append("_cte_" + func).append(" dw " + func + "\n");
                        }
                        break;
                    case TablaTipos.DOUBLE_TYPE:
                        if (!esConstante(func)) {
                                librerias.append(getPrefix(func)).append(func.replaceAll("\\.","\\_")).append(" dq ? \n");
                        } else {
                            librerias.append("_cte_" + func.replaceAll("[.-]", "")).append(" dq " + func + "\n");
                        }
                        break;
                    case TablaTipos.LONG_TYPE:
                        if (!esConstante(func)) {
                            librerias.append(getPrefix(func)).append(func.replaceAll("[.-]","\\_")).append(" dd ? \n");
                        } else {
                            librerias.append("_cte_" + func).append(" dd " + func.substring(0, func.indexOf("L")) + "\n");
                        }
                        break;
                    case TablaTipos.STRING:
                            librerias.append("_cte_" + func.replaceAll("\\s","\\_")).append(" db ").append("\"" + func + "\"").append(", 0 \n");
                        break;
                    default:
                        break;
                }
                } else{
                    System.out.println("");
                }
                
            }
        }
    }

    public static void generarConversionExplicita(String auxiliarString) {
        codigoAssembler.append("FILD ").append(OP1).append("\n");
    }

    public static void generarAssemblerPrint(){
        codigoAssembler.append("invoke MessageBoxA, NULL, ADDR " + OP1.replaceAll("\\s","\\_") + ", ADDR "+ OP1.replaceAll("\\s","\\_") + ", MB_OK \n");
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
                    break;
            case "*":
                    codigoAssembler.append("MOV EAX, ").append(OP1).append("\n");
                    codigoAssembler.append("MOV EBX, ").append(OP2).append("\n");
                    codigoAssembler.append("MUL EBX ").append("\n");
                    auxiliar = generarVariableAuxiliar();
                    codigoAssembler.append("JO " + TAG_OVERFLOW_LONG + "\n");
                    codigoAssembler.append("MOV ").append(auxiliar).append(", EAX\n");
                    
                    break;
            case "=":
                    codigoAssembler.append("MOV EAX, ").append(OP2).append("\n");
                    codigoAssembler.append("MOV ").append(OP1).append(", EAX\n"); 
                break;
            case "/":
                    auxiliar = generarVariableAuxiliar();
                    codigoAssembler.append("MOV EAX, ").append(OP1).append("\n");
                    codigoAssembler.append("MOV EBX, ").append(OP2).append("\n");
                    codigoAssembler.append("XOR EDX, EDX").append("\n");
                    codigoAssembler.append("DIV EBX ").append("\n");
                    codigoAssembler.append("MOV ").append(auxiliar).append(", EAX").append("\n");
                break;
            case "==":
                    codigoAssembler.append("MOV EAX, ").append(OP2).append("\n");
                    codigoAssembler.append("CMP EAX, ").append(OP1).append("\n");
                    salto = "JE ";
                break;

            case ">=":
                    codigoAssembler.append("MOV EAX, ").append(OP2).append("\n");
                    codigoAssembler.append("CMP EAX, ").append(OP1).append("\n");
                    salto = "JLE ";
                break;
            case "<=": // JBE, JA
                    codigoAssembler.append("MOV EAX, ").append(OP2).append("\n");
                    codigoAssembler.append("CMP EAX, ").append(OP1).append("\n");
                    salto = "JGE ";
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
                codigoAssembler.append("invoke MessageBoxA, NULL, ADDR _ERROR_POR_PANTALLA, ADDR _ERROR_POR_PANTALLA, MB_OK \n");
                codigoAssembler.append("invoke ExitProcess, 0\n");
                codigoAssembler.append("end START\n");
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
                    break;
            case "*":
                    codigoAssembler.append("MOV AX, ").append(OP1).append("\n");
                    codigoAssembler.append("MOV BX, ").append(OP2).append("\n");
                    codigoAssembler.append("MUL BX ").append("\n");
                    auxiliar = generarVariableAuxiliar();
                    codigoAssembler.append("JO " + TAG_OVERFLOW_UINT + "\n");
                    codigoAssembler.append("MOV ").append(auxiliar).append(", AX\n");
                    break;
            case "=":
                    codigoAssembler.append("MOV AX, ").append(OP2).append("\n");
                    codigoAssembler.append("MOV ").append(OP1).append(", AX\n"); 
                break;
            case "/":
                    auxiliar = generarVariableAuxiliar();
                    codigoAssembler.append("MOV AX, ").append(OP1).append("\n");
                    codigoAssembler.append("MOV BX, ").append(OP2).append("\n");
                    codigoAssembler.append("XOR DX, DX").append("\n");
                    codigoAssembler.append("DIV BX ").append("\n");
                    codigoAssembler.append("MOV ").append(auxiliar).append(", AX").append("\n");
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
                codigoAssembler.append("invoke MessageBoxA, NULL, ADDR _ERROR_POR_PANTALLA, ADDR _ERROR_POR_PANTALLA, MB_OK \n");
                codigoAssembler.append("invoke ExitProcess, 0\n");
                codigoAssembler.append("end START\n");
                break;
        }
    }

    public static void generarCodigoOperacionesDouble() {
        String auxiliar = "@auxDouble";
        switch (OP) {
            case "+":
                codigoAssembler.append("FLD ").append(OP2).append("\n");
                codigoAssembler.append("FADD ").append(OP1).append("\n");
                auxiliar = generarVariableAuxiliar();
                codigoAssembler.append("FST ").append(auxiliar).append("\n");
                codigoAssembler.append("JC ").append("overflow_DOUBLE").append("\n");
                break;
            case "-":
                codigoAssembler.append("FLD ").append(OP2).append("\n");
                codigoAssembler.append("FSUB ").append(OP1).append("\n");
                auxiliar = generarVariableAuxiliar();
                codigoAssembler.append("FST ").append(auxiliar).append("\n");
                break;

            case "*":
                codigoAssembler.append("FLD ").append(OP2).append("\n");
                codigoAssembler.append("FMUL ").append(OP1).append("\n");
                auxiliar = generarVariableAuxiliar();
                codigoAssembler.append("FST ").append(auxiliar).append("\n");
                break;

            case "/":
                codigoAssembler.append("FLD ").append(OP2).append("\n");
                codigoAssembler.append("FDIV ").append(OP1).append("\n");
                auxiliar = generarVariableAuxiliar();
                codigoAssembler.append("FST ").append(auxiliar).append("\n");
                break;
            case "=":
                codigoAssembler.append("FLD ").append(OP2).append("\n");
                codigoAssembler.append("FST ").append(OP1).append("\n");
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
                codigoAssembler.append("invoke MessageBoxA, NULL, ADDR _ERROR_POR_PANTALLA, ADDR _ERROR_POR_PANTALLA, MB_OK \n");
                codigoAssembler.append("invoke ExitProcess, 0\n");
                codigoAssembler.append("end START\n");
                break;
        }
    }

    public static void generarAssemblerOverflowFlotantes() {        
        codigoAssembler.append("FLD ").append(auxiliar).append("\n");
        codigoAssembler.append("FCOM").append("\n");
        codigoAssembler.append("FSTSW AX\n"); // Nos fijamos si hay overflow (estado del coprocesador)
        codigoAssembler.append("SAHF\n"); // Mueve los flags del estado de la palabra al registro de flags
        codigoAssembler.append("JC ").append("overflow_DOUBLE").append("\n"); // Salta a la etiqueta si no hay overflow.
    }

    public static void generarAssemblerSaltoIncondicional() {
        codigoAssembler.append("JMP ").append(OP2).append("\n");
    }

    public static void generarAssemblerSaltoCondicional() {
        
        switch (salto) {
            case "JE ": //Equal
                codigoAssembler.append("JE ").append(OP2).append("\n");
                break;
            case "JNE ": //Non equal
                codigoAssembler.append("JNE ").append(OP2).append("\n");
                break;
            case "JLE ": //Less Equal
                codigoAssembler.append("JLE ").append(OP2).append("\n");
                break;
            case "JGE ": //Greater Equal
                codigoAssembler.append("JGE ").append(OP2).append("\n");
                break;
            case "JL ": //Less
                codigoAssembler.append("JL ").append(OP2).append("\n");
                break;
            case "JG ": //Greater
                codigoAssembler.append("JG ").append(OP2).append("\n");
                break;
            default:
                codigoAssembler.append("invoke MessageBoxA, NULL, ADDR _ERROR_POR_PANTALLA, ADDR _ERROR_POR_PANTALLA, MB_OK \n");
                codigoAssembler.append("invoke ExitProcess, 0\n");
                break;
        }
    }

    public static void generarAssemblerRecursividad(){
        codigoAssembler.append(TAG_RECURSIVIDAD).append(":").append("\n");
        codigoAssembler.append("invoke MessageBoxA, NULL, ADDR _RECURSIVIDAD, ADDR _RECURSIVIDAD, MB_OK \n");
        codigoAssembler.append("invoke ExitProcess, 0\n");
        codigoAssembler.append('\n');
    }

    public static void generarAssemblerOverflowEnterosSinSigno(){
        codigoAssembler.append(TAG_OVERFLOW_UINT).append(":").append("\n");
        codigoAssembler.append("invoke MessageBoxA, NULL, ADDR _OVERFLOW_PRODUCTO_ENTERO_SIN_SIGNO, ADDR _OVERFLOW_PRODUCTO_ENTERO_SIN_SIGNO, MB_OK \n");
        codigoAssembler.append("invoke ExitProcess, 0\n");
        codigoAssembler.append('\n');
    }

    public static void generarAssemblerOverflowEnterosConSigno(){
        codigoAssembler.append(TAG_OVERFLOW_LONG).append(":").append("\n");
        codigoAssembler.append("invoke MessageBoxA, NULL, ADDR _OVERFLOW_PRODUCTO_ENTERO_CON_SIGNO, ADDR _OVERFLOW_PRODUCTO_ENTERO_CON_SIGNO, MB_OK \n");
        codigoAssembler.append("invoke ExitProcess, 0\n");
        codigoAssembler.append('\n');
    }

    public static void generarAssembelerOverflowDouble(){
        codigoAssembler.append(TAG_OVERFLOW_DOUBLE).append(":").append("\n");
        codigoAssembler.append("invoke MessageBoxA, NULL, ADDR _OVERFLOW_SUMA_PFLOTANTE, ADDR _OVERFLOW_SUMA_PFLOTANTE, MB_OK \n");
        codigoAssembler.append("invoke ExitProcess, 0\n");
        codigoAssembler.append('\n');
    }

    public static void generarAssemblerInvocacion() {
        if(!tag.equals(Scope.getScopeMain())){
            String var = "__" + tag;
            codigoAssembler.append("CMP " + var + ", " + 0).append("\n"); //Controlamos recursividad
            codigoAssembler.append("JNE " + TAG_RECURSIVIDAD).append("\n");
            codigoAssembler.append("INC " + var).append("\n");
            codigoAssembler.append("CALL ").append(OP1).append("\n");
            codigoAssembler.append("DEC " + var).append("\n");
        } else {
            codigoAssembler.append("CALL ").append(OP1).append("\n");
        }

    }

    public static void generarAssemblerReturn() {
        codigoAssembler.append("RET ").append("\n");    
        codigoAssembler.append("\n");
    }

    public static void generarAssemblerTOD() {

        if (type.equals(Terceto.ERROR))
            return;

        generarConversionExplicita(generarVariableAuxiliar());
    }

    public static String generarVariableAuxiliar() { // Generamos la variable auxiliar que
                                                     // vamos a necesitar para las
                                                     // conversiones y las operaciones
                                                     // aritméticas.
        auxiliar = AUX + number + tag;
        TablaSimbolos.addIdentificador(auxiliar);
        TablaSimbolos.addTipo(type, auxiliar);
        TablaSimbolos.setUsed(auxiliar);
        return auxiliar;
    }

    private static boolean esConstante(String s) { // Nos fijamos el uso para ver si es una constante o identificador.
        
        if (TablaSimbolos.getUse(s) == "" || TablaSimbolos.getUse(s) == null) {
            return true;
        }
        return false;
    }

    public static void generateFile(String fileName) throws Exception {
        File file = new File(fileName);
        FileWriter writer = new FileWriter(file);
        writer.write(codigoAssembler.toString());
        writer.close();
    }

}
