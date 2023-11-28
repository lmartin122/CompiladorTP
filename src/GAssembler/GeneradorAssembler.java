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
    private static int number = 0;
    private static String salto = "";
    private static StringBuilder codigoFunciones = new StringBuilder();
    private static HashMap<String,String> cadenas = new HashMap<>();
    private static HashMap<String,String> doubles = new HashMap<>();


    private static String getOperando(String r) {
        if (r.contains("["))
        {
            r = r.substring(1, r.length()-1);
            if (r.equals(Terceto.UNDEFINED))
                return r;
            
            if (Integer.valueOf(r) > number)
                return tag + "_" + Terceto.LABEL + r;

            return AUX + r + tag;
        }else{
            if(OP.equals("PRINT") || OP.equals("CALL"))
                return r;
            
            if (esConstante(r)){
                if(!TablaSimbolos.getTypeLexema(r).equals(TablaTipos.DOUBLE_TYPE))
                    r = r.replaceAll("\\D", "");
                return r;
            }
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
                    codigoAssembler.append("end " + tag);
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
                                    .append("invoke MessageBoxA, NULL, ADDR _ERROR_POR_PANTALLA, ADDR _ERROR_POR_PANTALLA, MB_OK \n");
                            codigoAssembler.append("invoke ExitProcess, 0\n");
                            codigoAssembler.append("end START");
                        }
                        break;
                }
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

    public static void generarCodigoVariables(StringBuilder librerias) { // Generamos el código para las variables declaradas.
        for (String func : TablaSimbolos.getTablaSimbolos()) {
            //System.out.println("FUNC " + func);
            type = TablaSimbolos.getTypeLexema(func);
            
            if (type != null){
            if (func.startsWith("@")) {
                if (type.equals(TablaTipos.LONG_TYPE)) {
                    librerias.append(func).append(" dd ? \n");
                } else if (type.equals(TablaTipos.UINT_TYPE))
                    librerias.append(func).append(" dw ? \n");
                else if (type.equals(TablaTipos.DOUBLE_TYPE)) {
                    librerias.append(func).append(" dq ? \n");
                } else if (type.equals(TablaTipos.STRING)) {
                    func = "cadena_" + func;
                    if(func.contains("@aux")){
                        for(Map.Entry<String,String> entrada: cadenas.entrySet()){
                            if(entrada.getKey().equals(func)){
                                librerias.append(entrada.getKey()).append(" db \"" + entrada.getValue() +"\", 0\n");
                            }
                        }
                    }
                }
            }   
                
                switch (type) {
                case TablaTipos.UINT_TYPE:
                    if (!esConstante(func) && !func.startsWith("@")) { // Si no es una constante, la declaramos como variable con su
                                                    // lexema.
                        librerias.append("__").append(func).append(" dw ? \n");
                    }
                    break;
                case TablaTipos.DOUBLE_TYPE:
                    System.out.println("EL FUNC ES " + func);
                    if (!esConstante(func) && !func.startsWith("@")) {
                        System.out.println("NO SOY UN AXULIAR " + func);
                        librerias.append("__").append(func).append(" dq ? \n");
                    }
                    break;
                case TablaTipos.LONG_TYPE:
                    if (!esConstante(func) && !func.startsWith("@")) {
                        librerias.append("__").append(func).append(" dd ? \n");
                    }
                    break;                    
            }
            } else {
                if(TablaSimbolos.isFunction(func)){
                    librerias.append("__").append(func).append(" DWORD 0 \n");
                }
                 
                }
                
            }
            

    }

    /*public static void generarCodigoFunciones(Tercetos tercetosGenerados){
        
        //codigoFunciones.append("HOLA ESTOY VIENDO DONDE ESTARIA ESTO EN EL CODIGO\n");
        //codigoFunciones.append(AUX);
        for (Map.Entry<String, ArrayList<Terceto>> func : tercetosGenerados.getTercetos().entrySet()) {
        for (Terceto terceto : func.getValue()) {
                number = terceto.getNumber();
                type = terceto.getType();
                OP = terceto.getFirst();
                OP1 = getOperando(terceto.getSecond());
                OP2 = getOperando(terceto.getThird());
                
                if (type != null && type.equals(Terceto.ERROR)) {
                    codigoAssembler.append("invoke MessageBoxA, NULL, ADDR _ERROR_POR_PANTALLA, ADDR _ERROR_POR_PANTALLA, MB_OK \n");
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
                    case "UB": //Salto incondicional.
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
                                    .append("invoke MessageBoxA, NULL, ADDR _ERROR_POR_PANTALLA, ADDR _ERROR_POR_PANTALLA, MB_OK \n");
                            codigoAssembler.append("invoke ExitProcess, 0\n");
                            codigoAssembler.append("end START");
                        }
                        break;
                }
            }
            }

    }*/

    public static void generarConversionExplicita(String auxiliar) {
        // El auxiliar es para guardar la conversion del tod
        codigoAssembler.append("FILD ").append(OP1).append("\n");
    }

    public static void generarAssemblerPrint(){
        auxiliar = "cadena_" + generarVariableAuxiliar();
        System.out.println("ESTOY ASOCIANDO " + auxiliar + " CON " + OP1);
        cadenas.put(auxiliar, OP1);
        codigoAssembler.append("invoke MessageBoxA, NULL, ADDR " + auxiliar + " , ADDR "+ auxiliar + ", MB_OK \n");
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
                codigoAssembler.append("end START");
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
                codigoAssembler.append("FLD ").append(OP1).append("\n");
                codigoAssembler.append("FSUB ").append("\n"); 
                auxiliar = generarVariableAuxiliar();
                codigoAssembler.append("FST ").append(auxiliar).append("\n");
                break;

            case "*":
                codigoAssembler.append("FLD ").append(OP2).append("\n");
                codigoAssembler.append("FLD ").append(OP1).append("\n");
                codigoAssembler.append("FMUL ").append("\n"); 
                auxiliar = generarVariableAuxiliar();
                codigoAssembler.append("FST ").append(auxiliar).append("\n");
                break;

            case "/":
                codigoAssembler.append("FLD ").append(OP2).append("\n");
                codigoAssembler.append("FLD ").append(OP1).append("\n");
                codigoAssembler.append("FDIV ").append("\n"); 
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
                codigoAssembler.append("end START");
                break;
        }
    }

    public static void generarAssemblerOverflowFlotantes() {
        // Comprueba el bit de overflow en el registro de flags. JA para mayor.
        
        codigoAssembler.append("FLD ").append(auxiliar).append("\n");
        codigoAssembler.append("FCOM").append("\n");
        codigoAssembler.append("FSTSW AX\n"); // Nos fijamos si hay overflow (estado del coprocesador)
        codigoAssembler.append("SAHF\n"); // Mueve los flags del estado de la palabra al registro de flags
        codigoAssembler.append("JC ").append("overflow_DOUBLE").append("\n"); // Salta a la etiqueta si no hay overflow.
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
        
        switch (salto) {
            case "JE ": //Equal
                codigoAssembler.append("JNE ").append(OP2).append("\n");
                break;
            case "JNE ": //Non equal
                codigoAssembler.append("JE ").append(OP2).append("\n");
                break;
            case "JLE ": //Less Equal
                codigoAssembler.append("JG ").append(OP2).append("\n");
                break;
            case "JGE ": //Greater Equal
                codigoAssembler.append("JL ").append(OP2).append("\n");
                break;
            case "JL ": //Less
                codigoAssembler.append("JGE ").append(OP2).append("\n");
                break;
            case "JG ": //Greater
                codigoAssembler.append("JLE ").append(OP2).append("\n");
                break;
            default:
                System.out.println("hola estoy en defualts");
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

    public static void asociarDoubles(){
        doubles.put(ERROR_MSJ_POR_PANTALLA, AUX);
    }

    public static void generarAssemblerReturn() {
        codigoAssembler.append("RET ").append("\n");    
        codigoAssembler.append("\n");
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
        auxiliar = AUX + number + tag;
        if(type != null){
            TablaSimbolos.addIdentificador(auxiliar);
            TablaSimbolos.addTipo(type, auxiliar);
            return auxiliar;
        } else{
            TablaSimbolos.addIdentificador(auxiliar);
            TablaSimbolos.addTipo(TablaTipos.STRING, auxiliar);
            return auxiliar;
        }
    }

    private static boolean esConstante(String s) { // Nos fijamos el uso para ver si es una constante o identificador.
        if (TablaSimbolos.getUse(s) == null) {
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
