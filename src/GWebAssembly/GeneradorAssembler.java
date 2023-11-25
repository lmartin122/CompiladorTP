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
    private static final String OVERFLOW_SUMA_PFLOTANTE = "Error: se excedió el límite permitido (overflow)";
    private static final String OVERFLOW_PRODUCTO_ENTERO_SIN_SIGNO = "Error: se excedió el límite permitido (overflow)";
    private static final String OVERFLOW_PRODUCTO_ENTERO_CON_SIGNO = "Error: se excedió el límite permitido (overflow)";
    private static final String INVOCACION_RECURSIVA = "Error: no se permiten declaraciones recursivas.";
    private static final String ERROR_MSJ_POR_PANTALLA = "Error: se terminará el programa.";

    private static String tag = null;
    private static String OP = null;
    private static String OP1 = null;
    private static String OP2 = null;
    private static String type = null;
    private static int number = 0;
    private static String comparacionFalsa = "";

    private static String getOperando(String r) {

        if (r.contains("["))
            r = r.replaceAll("\\D", "");
        else
            return r;

        if (r.equals(Terceto.UNDEFINED))
            return r;

        if (Integer.valueOf(r).compareTo(number) <= 0)
            return Terceto.LABEL + r;

        return AUX + r + tag;
    }

    public static void generarCodigoAssembler(Tercetos tercetosGenerados) {
        for (Map.Entry<String, ArrayList<Terceto>> func : tercetosGenerados.getTercetos().entrySet()) {
            tag = func.getKey();

            for (Terceto terceto : func.getValue()) {
                number = terceto.getNumber();
                type = terceto.getType();
                OP = terceto.getFirst();
                OP1 = getOperando(terceto.getSecond());
                OP2 = getOperando(terceto.getThird());

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
                            case TablaTipos.LONG_TYPE:
                                generarCodigoOperacionesEnteros(OP, OP1, OP2, number);
                                break;
                            case TablaTipos.DOUBLE_TYPE:
                                generarCodigoOperacionesDouble(OP, OP1, OP2, number);
                                break;
                            default:
                                codigoAssembler
                                        .append("invoke MessageBoxA, NULL, ADDR _ERROR_POR_PANTALLA, ADDR title, MB_OK \n");
                                break;
                        }

                        // Lo que hice fue para un terceto normal, por ej (+,a@main,2_ui), le cargo 2 a
                        // "a". Pero tengo que ver si tengo una referencia a otro terceto, tengo que
                        // buscarlo.
                        break;
                    case "UB":
                        // Nos fijamos a dónde tenemos que saltar en el segundo operando.
                        generarAssemblerSaltoIncondicional(OP2);
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
                        // System.out.println(
                        // "Tengo el operando " + terceto.getSecond() + " y " + terceto.getThird() +
                        // "\n");
                        // System.out.println("Con tipos " + tipoOP1 + " y " + tipoOP2 + "\n");
                        // System.out.println("El numero del terceto del tod es " +
                        // terceto.getNumber());
                        generarAssemblerTOD();
                        break;

                    case "PRINT":
                        // generarAssemblerPrint()
                        String aux = generarVariableAuxiliarString(terceto.getSecond(), terceto.getNumber());
                        codigoAssembler.append("MOV AH, 9").append("\n");
                        codigoAssembler.append("MOV DX, ").append(aux.subSequence(0, 5)).append("\n");
                        codigoAssembler.append("INT 21h ").append("\n");
                        codigoAssembler.append("MOV AH, 4CH").append("\n");
                        codigoAssembler.append("INT 21h").append("\n");
                        break;
                    default:
                        if (OP.contains(Terceto.LABEL)) {
                            // Generar una etiqueta con OP
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
                .append("end START");
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
            }

        }

    }

    public static void generarConversionExplicita(String auxiliar) {
        // El auxiliar es para guardar la conversion del tod
        codigoAssembler.append("FILD ").append(OP1).append("\n");
    }

    public static void generarCodigoOperacionesEnteros(String operador, String operando1, String operando2,
            int numeroTerceto) { // Preguntar si tengo que involucrar a la pila al final de cada operacion.
                                 // PREGUNTAR
        String variableAuxiliar;
        String tipoOperando1 = TablaSimbolos.getTypeLexema(operando1);
        String tipoOperando2 = TablaSimbolos.getTypeLexema(operando2);
        switch (operador) {
            case "+":
                if (tipoOperando1.equals(TablaTipos.LONG_TYPE) && tipoOperando2.equals(TablaTipos.LONG_TYPE)) {
                    if (esConstante(operando2) && esConstante(operando1)) { // Nos fijamos si es una constante.
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append(operando1).append("\n"); // Movemos el valor del OP1
                                                                                            // al registro EAX (32
                                                                                            // bits).
                        codigoAssembler.append("ADD EAX, ").append(operando2).append("\n"); // Sumamos los operandos.
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                         // variable
                                                                                                         // auxiliar del
                                                                                                         // tipo del
                                                                                                         // resultado de
                                                                                                         // la operación
                                                                                                         // (LONG).
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", EAX\n");
                    } else if (esConstante(operando2) && !esConstante(operando1)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando1).append("\n"); // Movemos el
                                                                                                         // valor del
                                                                                                         // OP1 al
                                                                                                         // registro EAX
                                                                                                         // (32 bits).
                        codigoAssembler.append("ADD EAX, ").append(operando2).append("\n"); // Sumamos los operandos.
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                         // variable
                                                                                                         // auxiliar del
                                                                                                         // tipo del
                                                                                                         // resultado de
                                                                                                         // la operación
                                                                                                         // (LONG).
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", EAX\n");
                    } else {
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando1).append("\n"); // Movemos el
                                                                                                         // valor del
                                                                                                         // OP1 al
                                                                                                         // registro EAX
                                                                                                         // (32 bits).
                        codigoAssembler.append("ADD EAX, ").append("__").append(operando2).append("\n"); // Sumamos los
                                                                                                         // operandos.
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                         // variable
                                                                                                         // auxiliar del
                                                                                                         // tipo del
                                                                                                         // resultado de
                                                                                                         // la operación
                                                                                                         // (LONG).
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", EAX\n");
                    }
                } else {
                    if (esConstante(operando2) && esConstante(operando1)) { // Nos fijamos si es una constante.
                        codigoAssembler.append("MOV AX, ").append(operando1).append("\n"); // Movemos el valor del OP1
                                                                                           // al registro EAX (32 bits).
                        codigoAssembler.append("ADD AX, ").append(operando2).append("\n"); // Sumamos los operandos.
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                         // variable
                                                                                                         // auxiliar del
                                                                                                         // tipo del
                                                                                                         // resultado de
                                                                                                         // la operación
                                                                                                         // (LONG).
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", AX\n");
                    } else if (esConstante(operando2) && !esConstante(operando1)) {
                        codigoAssembler.append("MOV AX, ").append("__").append(operando1).append("\n"); // Movemos el
                                                                                                        // valor del OP1
                                                                                                        // al registro
                                                                                                        // EAX (32
                                                                                                        // bits).
                        codigoAssembler.append("ADD AX, ").append(operando2).append("\n"); // Sumamos los operandos.
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                         // variable
                                                                                                         // auxiliar del
                                                                                                         // tipo del
                                                                                                         // resultado de
                                                                                                         // la operación
                                                                                                         // (LONG).
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", AX\n");
                    } else {
                        codigoAssembler.append("MOV AX, ").append("__").append(operando1).append("\n"); // Movemos el
                                                                                                        // valor del OP1
                                                                                                        // al registro
                                                                                                        // EAX (32
                                                                                                        // bits).
                        codigoAssembler.append("ADD AX, ").append("__").append(operando2).append("\n"); // Sumamos los
                                                                                                        // operandos.
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                         // variable
                                                                                                         // auxiliar del
                                                                                                         // tipo del
                                                                                                         // resultado de
                                                                                                         // la operación
                                                                                                         // (LONG).
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", AX\n");
                    }
                }
                break;
            case "-":
                if (tipoOperando1.equals(TablaTipos.LONG_TYPE) && tipoOperando2.equals(TablaTipos.LONG_TYPE)) {
                    if (esConstante(operando2) && esConstante(operando1)) { // Nos fijamos si es una constante.
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append(operando1).append("\n"); // Movemos el valor del OP1
                                                                                            // al registro EAX (32
                                                                                            // bits).
                        codigoAssembler.append("SUB EAX, ").append(operando2).append("\n"); // Sumamos los operandos.
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                         // variable
                                                                                                         // auxiliar del
                                                                                                         // tipo del
                                                                                                         // resultado de
                                                                                                         // la operación
                                                                                                         // (LONG).
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", EAX\n");
                    } else if (esConstante(operando2) && !esConstante(operando1)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando1).append("\n"); // Movemos el
                                                                                                         // valor del
                                                                                                         // OP1 al
                                                                                                         // registro EAX
                                                                                                         // (32 bits).
                        codigoAssembler.append("SUB EAX, ").append(operando2).append("\n"); // Sumamos los operandos.
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                         // variable
                                                                                                         // auxiliar del
                                                                                                         // tipo del
                                                                                                         // resultado de
                                                                                                         // la operación
                                                                                                         // (LONG).
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", EAX\n");
                    } else {
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando1).append("\n"); // Movemos el
                                                                                                         // valor del
                                                                                                         // OP1 al
                                                                                                         // registro EAX
                                                                                                         // (32 bits).
                        codigoAssembler.append("SUB EAX, ").append("__").append(operando2).append("\n"); // Sumamos los
                                                                                                         // operandos.
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                         // variable
                                                                                                         // auxiliar del
                                                                                                         // tipo del
                                                                                                         // resultado de
                                                                                                         // la operación
                                                                                                         // (LONG).
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", EAX\n");
                    }
                } else {
                    if (esConstante(operando2) && esConstante(operando1)) { // Nos fijamos si es una constante.
                        codigoAssembler.append("MOV AX, ").append(operando1).append("\n"); // Movemos el valor del OP1
                                                                                           // al registro EAX (32 bits).
                        codigoAssembler.append("SUB AX, ").append(operando2).append("\n"); // Sumamos los operandos.
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                         // variable
                                                                                                         // auxiliar del
                                                                                                         // tipo del
                                                                                                         // resultado de
                                                                                                         // la operación
                                                                                                         // (LONG).
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", AX\n");
                    } else if (esConstante(operando2) && !esConstante(operando1)) {
                        codigoAssembler.append("MOV AX, ").append("__").append(operando1).append("\n"); // Movemos el
                                                                                                        // valor del OP1
                                                                                                        // al registro
                                                                                                        // EAX (32
                                                                                                        // bits).
                        codigoAssembler.append("SUB AX, ").append(operando2).append("\n"); // Sumamos los operandos.
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                         // variable
                                                                                                         // auxiliar del
                                                                                                         // tipo del
                                                                                                         // resultado de
                                                                                                         // la operación
                                                                                                         // (LONG).
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", AX\n");
                    } else {
                        codigoAssembler.append("MOV AX, ").append("__").append(operando1).append("\n"); // Movemos el
                                                                                                        // valor del OP1
                                                                                                        // al registro
                                                                                                        // EAX (32
                                                                                                        // bits).
                        codigoAssembler.append("SUB AX, ").append("__").append(operando2).append("\n"); // Sumamos los
                                                                                                        // operandos.
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                         // variable
                                                                                                         // auxiliar del
                                                                                                         // tipo del
                                                                                                         // resultado de
                                                                                                         // la operación
                                                                                                         // (LONG).
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", AX\n");
                    }
                }
                break;
            case "*":
                if (tipoOperando1.equals(TablaTipos.LONG_TYPE) && tipoOperando2.equals(TablaTipos.LONG_TYPE)) {
                    if (esConstante(operando2) && esConstante(operando1)) { // Nos fijamos si es una constante.
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append(operando1).append("\n"); // Movemos el valor del OP1
                                                                                            // al registro EAX (32
                                                                                            // bits).
                        codigoAssembler.append("MUL EAX, ").append(operando2).append("\n"); // Sumamos los operandos.
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                         // variable
                                                                                                         // auxiliar del
                                                                                                         // tipo del
                                                                                                         // resultado de
                                                                                                         // la operación
                                                                                                         // (LONG).
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", EAX\n");
                    } else if (esConstante(operando2) && !esConstante(operando1)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando1).append("\n"); // Movemos el
                                                                                                         // valor del
                                                                                                         // OP1 al
                                                                                                         // registro EAX
                                                                                                         // (32 bits).
                        codigoAssembler.append("MUL EAX, ").append(operando2).append("\n"); // Sumamos los operandos.
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                         // variable
                                                                                                         // auxiliar del
                                                                                                         // tipo del
                                                                                                         // resultado de
                                                                                                         // la operación
                                                                                                         // (LONG).
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", EAX\n");
                    } else {
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando1).append("\n"); // Movemos el
                                                                                                         // valor del
                                                                                                         // OP1 al
                                                                                                         // registro EAX
                                                                                                         // (32 bits).
                        codigoAssembler.append("MUL EAX, ").append("__").append(operando2).append("\n"); // Sumamos los
                                                                                                         // operandos.
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                         // variable
                                                                                                         // auxiliar del
                                                                                                         // tipo del
                                                                                                         // resultado de
                                                                                                         // la operación
                                                                                                         // (LONG).
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", EAX\n");
                    }
                } else {
                    if (esConstante(operando2) && esConstante(operando1)) { // Nos fijamos si es una constante.
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        codigoAssembler.append("MOV AX, ").append(operando1).append("\n"); // Movemos el valor del OP1
                                                                                           // al registro EAX (32 bits).
                        codigoAssembler.append("MUL AX, ").append(operando2).append("\n"); // Multiplicamos los
                                                                                           // operandos.
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                         // variable
                                                                                                         // auxiliar del
                                                                                                         // tipo del
                                                                                                         // resultado de
                                                                                                         // la operación
                                                                                                         // (LONG).
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", AX\n");
                    } else if (esConstante(operando2) && !esConstante(operando1)) {
                        codigoAssembler.append("MOV AX, ").append("__").append(operando1).append("\n"); // Movemos el
                                                                                                        // valor del OP1
                                                                                                        // al registro
                                                                                                        // EAX (32
                                                                                                        // bits).
                        codigoAssembler.append("MUL AX, ").append(operando2).append("\n"); // Multiplicamos los
                                                                                           // operandos.
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                         // variable
                                                                                                         // auxiliar del
                                                                                                         // tipo del
                                                                                                         // resultado de
                                                                                                         // la operación
                                                                                                         // (LONG).
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", AX\n");
                    } else {
                        codigoAssembler.append("MOV AX, ").append("__").append(operando1).append("\n"); // Movemos el
                                                                                                        // valor del OP1
                                                                                                        // al registro
                                                                                                        // EAX (32
                                                                                                        // bits).
                        codigoAssembler.append("MUL AX, ").append("__").append(operando2).append("\n"); // Multiplicamos
                                                                                                        // los
                                                                                                        // operandos.
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                         // variable
                                                                                                         // auxiliar del
                                                                                                         // tipo del
                                                                                                         // resultado de
                                                                                                         // la operación
                                                                                                         // (LONG).
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", AX\n");
                    }
                }
                break;
            case "=":
                if (tipoOperando2.equals(TablaTipos.LONG_TYPE) && tipoOperando1.equals(TablaTipos.LONG_TYPE)) {
                    // Asignación de enteros largos (32 bits)
                    if (esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append(operando2).append("\n"); // Movemos el valor de op2 a
                                                                                            // EAX
                        codigoAssembler.append("MOV ").append("__").append(operando1).append(", EAX\n"); // Asignamos el
                                                                                                         // valor de EAX
                                                                                                         // a op1
                    } else {
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando2).append("\n"); // Movemos el
                                                                                                         // valor de op2
                                                                                                         // a EAX
                        codigoAssembler.append("MOV ").append("__").append(operando1).append(", EAX\n"); // Asignamos el
                                                                                                         // valor de EAX
                                                                                                         // a op1
                    }
                } else {
                    // Asignación de enteros sin signo (16 bits)
                    if (TablaSimbolos.getUse(operando2) == null) {
                        codigoAssembler.append("MOV AX, ").append(operando2).append("\n"); // Movemos el valor de op2 a
                                                                                           // AX
                        codigoAssembler.append("MOV ").append("__").append(operando1).append(", AX\n"); // Asignamos el
                                                                                                        // valor de AX a
                                                                                                        // op1
                    } else {
                        codigoAssembler.append("MOV AX, ").append("__").append(operando2).append("\n"); // Movemos el
                                                                                                        // valor de op2
                                                                                                        // a AX
                        codigoAssembler.append("MOV ").append("__").append(operando1).append(", AX\n"); // Asignamos el
                                                                                                        // valor de AX a
                                                                                                        // op1
                    }

                }
                break;
            case "/":
                if (tipoOperando2.equals(TablaTipos.LONG_TYPE) && tipoOperando1.equals(TablaTipos.LONG_TYPE)) {

                    if (esConstante(operando2) && esConstante(operando1)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto);
                        codigoAssembler.append("MOV EAX, ").append(operando1).append("\n"); // Guardamos el dividendo en
                                                                                            // el registro EAX (32
                                                                                            // bits).
                        codigoAssembler.append("DIV EAX, ").append(operando2).append("\n"); // Realizamos la división.
                        codigoAssembler.append("MOV ").append(variableAuxiliar); // Guardamos el valor en la variable
                                                                                 // auxiliar.

                    } else if (esConstante(operando2) && !esConstante(operando1)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto);
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando1).append("\n"); // Guardamos el
                                                                                                         // dividendo en
                                                                                                         // el registro
                                                                                                         // EAX (32
                                                                                                         // bits).
                        codigoAssembler.append("DIV EAX, ").append(operando2).append("\n"); // Realizamos la división.
                        codigoAssembler.append("MOV ").append(variableAuxiliar); // Guardamos el valor en la variable
                                                                                 // auxiliar.
                    } else {
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto);
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando1).append("\n"); // Guardamos el
                                                                                                         // dividendo en
                                                                                                         // el registro
                                                                                                         // EAX (32
                                                                                                         // bits).
                        codigoAssembler.append("DIV EAX, ").append("__").append(operando2).append("\n"); // Realizamos
                                                                                                         // la división.
                        codigoAssembler.append("MOV ").append(variableAuxiliar); // Guardamos el valor en la variable
                                                                                 // auxiliar.
                    }
                } else {
                    if (esConstante(operando2) && esConstante(operando1)) {
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto);
                        codigoAssembler.append("MOV AX, ").append(operando1).append("\n"); // Guardamos el dividendo en
                                                                                           // el registro AX (16 bits).
                        codigoAssembler.append("DIV AX, ").append(operando2); // Realizamos la división.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", AX"); // Guardo el resultado
                                                                                                // de la división en AX
                                                                                                // (16 bits).
                    } else if (esConstante(operando2) && !esConstante(operando1)) {
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto);
                        codigoAssembler.append("MOV AX, ").append("__").append(operando1).append("\n"); // Guardamos el
                                                                                                        // dividendo en
                                                                                                        // el registro
                                                                                                        // AX (16 bits).
                        codigoAssembler.append("DIV AX, ").append(operando2); // Realizamos la división.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", AX"); // Guardo el resultado
                                                                                                // de la división en AX
                                                                                                // (16 bits).
                    } else {
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto);
                        codigoAssembler.append("MOV AX, ").append("__").append(operando1).append("\n"); // Guardamos el
                                                                                                        // dividendo en
                                                                                                        // el registro
                                                                                                        // AX (16 bits).
                        codigoAssembler.append("DIV AX, ").append("__").append(operando2); // Realizamos la división.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", AX"); // Guardo el resultado
                                                                                                // de la división en AX
                                                                                                // (16 bits).
                    }
                }
                break;
            case "==":
                if (tipoOperando2.equals(TablaTipos.LONG_TYPE) && tipoOperando1.equals(TablaTipos.LONG_TYPE)) {
                    if (esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                            // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                          // ser
                                                                                                          // verdadera
                                                                                                          // la
                                                                                                          // comparación,
                                                                                                          // saltamos a
                                                                                                          // la etiqueta
                                                                                                          // que hacemos
                                                                                                          // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JNE "; // Guardamos la comparacion si llega a ser falso.
                    } else if (!esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                         // los valores
                                                                                                         // entre
                                                                                                         // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                          // ser
                                                                                                          // verdadera
                                                                                                          // la
                                                                                                          // comparación,
                                                                                                          // saltamos a
                                                                                                          // la etiqueta
                                                                                                          // que hacemos
                                                                                                          // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JNE "; // Guardamos la comparacion si llega a ser falso.
                    } else if (esConstante(operando1) && !esConstante(operando2)) {
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                            // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                          // ser
                                                                                                          // verdadera
                                                                                                          // la
                                                                                                          // comparación,
                                                                                                          // saltamos a
                                                                                                          // la etiqueta
                                                                                                          // que hacemos
                                                                                                          // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JNE "; // Guardamos la comparacion si llega a ser falso.
                    } else {
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                         // los valores
                                                                                                         // entre
                                                                                                         // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                          // ser
                                                                                                          // verdadera
                                                                                                          // la
                                                                                                          // comparación,
                                                                                                          // saltamos a
                                                                                                          // la etiqueta
                                                                                                          // que hacemos
                                                                                                          // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JNE "; // Guardamos la comparacion si llega a ser falso.
                    }
                } else {
                    if (esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV AX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                           // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                          // ser
                                                                                                          // verdadera
                                                                                                          // la
                                                                                                          // comparación,
                                                                                                          // saltamos a
                                                                                                          // la etiqueta
                                                                                                          // que hacemos
                                                                                                          // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JNE "; // Guardamos la comparacion si llega a ser falso.
                    } else if (!esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        codigoAssembler.append("MOV AX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                        // los valores
                                                                                                        // entre
                                                                                                        // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                          // ser
                                                                                                          // verdadera
                                                                                                          // la
                                                                                                          // comparación,
                                                                                                          // saltamos a
                                                                                                          // la etiqueta
                                                                                                          // que hacemos
                                                                                                          // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JNE "; // Guardamos la comparacion si llega a ser falso.
                    } else if (esConstante(operando1) && !esConstante(operando2)) {
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV AX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                           // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                          // ser
                                                                                                          // verdadera
                                                                                                          // la
                                                                                                          // comparación,
                                                                                                          // saltamos a
                                                                                                          // la etiqueta
                                                                                                          // que hacemos
                                                                                                          // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JNE "; // Guardamos la comparacion si llega a ser falso.
                    } else {
                        codigoAssembler.append("MOV AX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                        // los valores
                                                                                                        // entre
                                                                                                        // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                          // ser
                                                                                                          // verdadera
                                                                                                          // la
                                                                                                          // comparación,
                                                                                                          // saltamos a
                                                                                                          // la etiqueta
                                                                                                          // que hacemos
                                                                                                          // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JNE "; // Guardamos la comparacion si llega a ser falso.
                    }
                }

                break;

            case ">=": // JAE, JB
                if (tipoOperando2.equals(TablaTipos.LONG_TYPE) && tipoOperando1.equals(TablaTipos.LONG_TYPE)) {
                    if (esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                            // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JAE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JB "; // Guardamos la comparacion si llega a ser falso.
                    } else if (!esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                         // los valores
                                                                                                         // entre
                                                                                                         // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JAE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JB "; // Guardamos la comparacion si llega a ser falso.
                    } else if (esConstante(operando1) && !esConstante(operando2)) {
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                            // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JAE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JB "; // Guardamos la comparacion si llega a ser falso.
                    } else {
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                         // los valores
                                                                                                         // entre
                                                                                                         // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JAE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JB "; // Guardamos la comparacion si llega a ser falso.
                    }
                } else {
                    if (esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV AX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                           // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JAE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JB "; // Guardamos la comparacion si llega a ser falso.
                    } else if (!esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        codigoAssembler.append("MOV AX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                        // los valores
                                                                                                        // entre
                                                                                                        // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JAE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JB "; // Guardamos la comparacion si llega a ser falso.
                    } else if (esConstante(operando1) && !esConstante(operando2)) {
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV AX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                           // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JAE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JB "; // Guardamos la comparacion si llega a ser falso.
                    } else {
                        codigoAssembler.append("MOV AX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                        // los valores
                                                                                                        // entre
                                                                                                        // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JAE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JB "; // Guardamos la comparacion si llega a ser falso.
                    }
                }
                break;

            case "<=": // JBE, JA
                if (tipoOperando2.equals(TablaTipos.LONG_TYPE) && tipoOperando1.equals(TablaTipos.LONG_TYPE)) {
                    if (esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                            // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JBE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JA "; // Guardamos la comparacion si llega a ser falso.
                    } else if (!esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                         // los valores
                                                                                                         // entre
                                                                                                         // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JBE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JA "; // Guardamos la comparacion si llega a ser falso.
                    } else if (esConstante(operando1) && !esConstante(operando2)) {
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                            // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JBE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JA "; // Guardamos la comparacion si llega a ser falso.
                    } else {
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                         // los valores
                                                                                                         // entre
                                                                                                         // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JBE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JA "; // Guardamos la comparacion si llega a ser falso.
                    }
                } else {
                    if (esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV AX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                           // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JBE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JA "; // Guardamos la comparacion si llega a ser falso.
                    } else if (!esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        codigoAssembler.append("MOV AX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                        // los valores
                                                                                                        // entre
                                                                                                        // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JBE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JA "; // Guardamos la comparacion si llega a ser falso.
                    } else if (esConstante(operando1) && !esConstante(operando2)) {
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV AX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                           // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JBE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JA "; // Guardamos la comparacion si llega a ser falso.
                    } else {
                        codigoAssembler.append("MOV AX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                        // los valores
                                                                                                        // entre
                                                                                                        // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JBE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JA "; // Guardamos la comparacion si llega a ser falso.
                    }
                }
                break;

            case ">": // JG, JBE
                if (tipoOperando2.equals(TablaTipos.LONG_TYPE) && tipoOperando1.equals(TablaTipos.LONG_TYPE)) {
                    if (esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                            // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JG ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                          // ser
                                                                                                          // verdadera
                                                                                                          // la
                                                                                                          // comparación,
                                                                                                          // saltamos a
                                                                                                          // la etiqueta
                                                                                                          // que hacemos
                                                                                                          // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JBE "; // Guardamos la comparacion si llega a ser falso.
                    } else if (!esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                         // los valores
                                                                                                         // entre
                                                                                                         // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JG ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                          // ser
                                                                                                          // verdadera
                                                                                                          // la
                                                                                                          // comparación,
                                                                                                          // saltamos a
                                                                                                          // la etiqueta
                                                                                                          // que hacemos
                                                                                                          // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JBE "; // Guardamos la comparacion si llega a ser falso.
                    } else if (esConstante(operando1) && !esConstante(operando2)) {
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                            // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JG ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                          // ser
                                                                                                          // verdadera
                                                                                                          // la
                                                                                                          // comparación,
                                                                                                          // saltamos a
                                                                                                          // la etiqueta
                                                                                                          // que hacemos
                                                                                                          // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JBE "; // Guardamos la comparacion si llega a ser falso.
                    } else {
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                         // los valores
                                                                                                         // entre
                                                                                                         // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JG ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                          // ser
                                                                                                          // verdadera
                                                                                                          // la
                                                                                                          // comparación,
                                                                                                          // saltamos a
                                                                                                          // la etiqueta
                                                                                                          // que hacemos
                                                                                                          // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JBE "; // Guardamos la comparacion si llega a ser falso.
                    }
                } else {
                    if (esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV AX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                           // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JG ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                          // ser
                                                                                                          // verdadera
                                                                                                          // la
                                                                                                          // comparación,
                                                                                                          // saltamos a
                                                                                                          // la etiqueta
                                                                                                          // que hacemos
                                                                                                          // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JBE "; // Guardamos la comparacion si llega a ser falso.
                    } else if (!esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        codigoAssembler.append("MOV AX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                        // los valores
                                                                                                        // entre
                                                                                                        // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JG ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                          // ser
                                                                                                          // verdadera
                                                                                                          // la
                                                                                                          // comparación,
                                                                                                          // saltamos a
                                                                                                          // la etiqueta
                                                                                                          // que hacemos
                                                                                                          // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JBE "; // Guardamos la comparacion si llega a ser falso.
                    } else if (esConstante(operando1) && !esConstante(operando2)) {
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV AX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                           // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JG ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                          // ser
                                                                                                          // verdadera
                                                                                                          // la
                                                                                                          // comparación,
                                                                                                          // saltamos a
                                                                                                          // la etiqueta
                                                                                                          // que hacemos
                                                                                                          // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JBE "; // Guardamos la comparacion si llega a ser falso.
                    } else {
                        codigoAssembler.append("MOV AX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                        // los valores
                                                                                                        // entre
                                                                                                        // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JG ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                          // ser
                                                                                                          // verdadera
                                                                                                          // la
                                                                                                          // comparación,
                                                                                                          // saltamos a
                                                                                                          // la etiqueta
                                                                                                          // que hacemos
                                                                                                          // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JBE "; // Guardamos la comparacion si llega a ser falso.
                    }
                }
                break;

            case "<":// JMP, JAE
                if (tipoOperando2.equals(TablaTipos.LONG_TYPE) && tipoOperando1.equals(TablaTipos.LONG_TYPE)) {
                    if (esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                            // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JMP ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JAE "; // Guardamos la comparacion si llega a ser falso.
                    } else if (!esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                         // los valores
                                                                                                         // entre
                                                                                                         // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JMP ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JAE "; // Guardamos la comparacion si llega a ser falso.
                    } else if (esConstante(operando1) && !esConstante(operando2)) {
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                            // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JMP ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JAE "; // Guardamos la comparacion si llega a ser falso.
                    } else {
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                         // los valores
                                                                                                         // entre
                                                                                                         // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JMP ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JAE "; // Guardamos la comparacion si llega a ser falso.
                    }
                } else {
                    if (esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV AX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                           // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JMP ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JAE "; // Guardamos la comparacion si llega a ser falso.
                    } else if (!esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        codigoAssembler.append("MOV AX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                        // los valores
                                                                                                        // entre
                                                                                                        // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JMP ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JAE "; // Guardamos la comparacion si llega a ser falso.
                    } else if (esConstante(operando1) && !esConstante(operando2)) {
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV AX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                           // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JMP ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JAE "; // Guardamos la comparacion si llega a ser falso.
                    } else {
                        codigoAssembler.append("MOV AX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                        // los valores
                                                                                                        // entre
                                                                                                        // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JMP ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JAE "; // Guardamos la comparacion si llega a ser falso.
                    }
                }
                break;

            case "!!":
                if (tipoOperando2.equals(TablaTipos.LONG_TYPE) && tipoOperando1.equals(TablaTipos.LONG_TYPE)) {
                    if (esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                            // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JNE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JE "; // Guardamos la comparacion si llega a ser falso.
                    } else if (!esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                         // los valores
                                                                                                         // entre
                                                                                                         // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JNE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JE "; // Guardamos la comparacion si llega a ser falso.
                    } else if (esConstante(operando1) && !esConstante(operando2)) {
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                            // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JNE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JE "; // Guardamos la comparacion si llega a ser falso.
                    } else {
                        codigoAssembler.append("MOV EAX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP EAX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                         // los valores
                                                                                                         // entre
                                                                                                         // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JNE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JE "; // Guardamos la comparacion si llega a ser falso.
                    }
                } else {
                    if (esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV AX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                           // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JNE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JE "; // Guardamos la comparacion si llega a ser falso.
                    } else if (!esConstante(operando1) && esConstante(operando2)) {
                        if (operando2.length() > 1) {
                            operando2 = operando2.substring(0, operando2.length() - 1);
                        } else {
                            operando2 = operando2.substring(0, 1);
                        }
                        codigoAssembler.append("MOV AX, ").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                        // los valores
                                                                                                        // entre
                                                                                                        // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JNE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JE "; // Guardamos la comparacion si llega a ser falso.
                    } else if (esConstante(operando1) && !esConstante(operando2)) {
                        if (operando1.length() > 1) {
                            operando1 = operando1.substring(0, operando1.length() - 1);
                        } else {
                            operando1 = operando1.substring(0, 1);
                        }
                        codigoAssembler.append("MOV AX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append(operando1).append("\n"); // Comparamos los valores
                                                                                           // entre operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JNE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JE "; // Guardamos la comparacion si llega a ser falso.
                    } else {
                        codigoAssembler.append("MOV AX, ").append("__").append(operando2).append("\n");
                        codigoAssembler.append("CMP AX, ").append("__").append(operando1).append("\n"); // Comparamos
                                                                                                        // los valores
                                                                                                        // entre
                                                                                                        // operandos
                        variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Guardamos el
                                                                                                         // valor de la
                                                                                                         // comparación.
                        // codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                        // // Movemos el valor para true.
                        codigoAssembler.append("JNE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de
                                                                                                           // ser
                                                                                                           // verdadera
                                                                                                           // la
                                                                                                           // comparación,
                                                                                                           // saltamos a
                                                                                                           // la
                                                                                                           // etiqueta
                                                                                                           // que
                                                                                                           // hacemos
                                                                                                           // después.
                        codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor
                                                                                                   // para falso.
                        codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en
                                                                                             // caso de verdadero.
                        comparacionFalsa = "JE "; // Guardamos la comparacion si llega a ser falso.
                    }
                }
                break;

            default:
                codigoAssembler.append("invoke MessageBoxA, NULL, ADDR _ERROR_POR_PANTALLA, ADDR title, MB_OK \n");
                codigoAssembler.append("invoke ExitProcess, 0\n");
                codigoAssembler.append("end START");
                break;
        }
    }

    public static void generarCodigoOperacionesDouble(String operador, String operando1, String operando2,
            int numeroTerceto) {
        String variableAuxiliar;
        String auxiliar = "@auxDouble";
        switch (operador) {
            case "+":

                if (esConstante(operando2) && esConstante(operando1)) {
                    codigoAssembler.append("FLD ").append(operando2).append("\n"); // Cargamos en la pila del
                                                                                   // coprocesador los valores de punto
                                                                                   // flotante.
                    codigoAssembler.append("FLD ").append(operando1).append("\n");
                    codigoAssembler.append("FADD "); // Hacemos la suma de los operandos recién cargados en la pila.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE, numeroTerceto);
                    codigoAssembler.append("FSTP ").append(variableAuxiliar).append("\n"); // Almacenamos el resultado y
                                                                                           // desapilamos.
                    generarAssemblerOverflowFlotantes(variableAuxiliar);
                } else if (esConstante(operando2) && !esConstante(operando1)) {
                    codigoAssembler.append("FLD ").append(operando2).append("\n"); // Cargamos en la pila del
                                                                                   // coprocesador los valores de punto
                                                                                   // flotante.
                    codigoAssembler.append("FLD ").append("__").append(operando1).append("\n");
                    codigoAssembler.append("FADD "); // Hacemos la suma de los operandos recién cargados en la pila.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE, numeroTerceto);
                    codigoAssembler.append("FSTP ").append(variableAuxiliar).append("\n"); // Almacenamos el resultado y
                                                                                           // desapilamos.
                    generarAssemblerOverflowFlotantes(variableAuxiliar);
                } else {
                    codigoAssembler.append("FLD ").append("__").append(operando2).append("\n"); // Cargamos en la pila
                                                                                                // del coprocesador los
                                                                                                // valores de punto
                                                                                                // flotante.
                    codigoAssembler.append("FLD ").append("__").append(operando1).append("\n");
                    codigoAssembler.append("FADD "); // Hacemos la suma de los operandos recién cargados en la pila.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE, numeroTerceto);
                    codigoAssembler.append("FSTP ").append(variableAuxiliar).append("\n"); // Almacenamos el resultado y
                                                                                           // desapilamos.
                    generarAssemblerOverflowFlotantes(variableAuxiliar);
                }
                break;

            case "-":
                if (esConstante(operando2) && esConstante(operando1)) {
                    codigoAssembler.append("FLD ").append(operando2).append("\n"); // Cargamos en la pila del
                                                                                   // coprocesador los valores de punto
                                                                                   // flotante.
                    codigoAssembler.append("FLD ").append(operando1).append("\n");
                    codigoAssembler.append("FSUB "); // Hacemos la resta de los operandos recién cargados en la pila.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE, numeroTerceto);
                    codigoAssembler.append("FSTP ").append(variableAuxiliar).append("\n"); // Almacenamos el resultado y
                                                                                           // desapilamos.
                } else if (esConstante(operando2) && !esConstante(operando1)) {
                    codigoAssembler.append("FLD ").append(operando2).append("\n"); // Cargamos en la pila del
                                                                                   // coprocesador los valores de punto
                                                                                   // flotante.
                    codigoAssembler.append("FLD ").append("__").append(operando1).append("\n");
                    codigoAssembler.append("FSUB "); // Hacemos la resta de los operandos recién cargados en la pila.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE, numeroTerceto);
                    codigoAssembler.append("FSTP ").append(variableAuxiliar).append("\n"); // Almacenamos el resultado y
                                                                                           // desapilamos.
                } else {
                    codigoAssembler.append("FLD ").append("__").append(operando2).append("\n"); // Cargamos en la pila
                                                                                                // del coprocesador los
                                                                                                // valores de punto
                                                                                                // flotante.
                    codigoAssembler.append("FLD ").append("__").append(operando1).append("\n");
                    codigoAssembler.append("FSUB "); // Hacemos la resta de los operandos recién cargados en la pila.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE, numeroTerceto);
                    codigoAssembler.append("FSTP ").append(variableAuxiliar).append("\n"); // Almacenamos el resultado y
                                                                                           // desapilamos.
                }
                break;

            case "*":
                if (esConstante(operando2) && esConstante(operando1)) {
                    codigoAssembler.append("FLD ").append(operando2).append("\n"); // Cargamos en la pila del
                                                                                   // coprocesador los valores de punto
                                                                                   // flotante.
                    codigoAssembler.append("FLD ").append(operando1).append("\n");
                    codigoAssembler.append("FMUL "); // Hacemos la multiplicación de los operandos recién cargados en la
                                                     // pila.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE, numeroTerceto);
                    codigoAssembler.append("FSTP ").append(variableAuxiliar).append("\n"); // Almacenamos el resultado y
                                                                                           // desapilamos.
                } else if (esConstante(operando2) && !esConstante(operando1)) {
                    codigoAssembler.append("FLD ").append(operando2).append("\n"); // Cargamos en la pila del
                                                                                   // coprocesador los valores de punto
                                                                                   // flotante.
                    codigoAssembler.append("FLD ").append("__").append(operando1).append("\n");
                    codigoAssembler.append("FMUL "); // Hacemos la multiplicación de los operandos recién cargados en la
                                                     // pila.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE, numeroTerceto);
                    codigoAssembler.append("FSTP ").append(variableAuxiliar).append("\n"); // Almacenamos el resultado y
                                                                                           // desapilamos.
                } else {
                    codigoAssembler.append("FLD ").append("__").append(operando2).append("\n"); // Cargamos en la pila
                                                                                                // del coprocesador los
                                                                                                // valores de punto
                                                                                                // flotante.
                    codigoAssembler.append("FLD ").append("__").append(operando1).append("\n");
                    codigoAssembler.append("FMUL "); // Hacemos la multiplicación de los operandos recién cargados en la
                                                     // pila.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE, numeroTerceto);
                    codigoAssembler.append("FSTP ").append(variableAuxiliar).append("\n"); // Almacenamos el resultado y
                                                                                           // desapilamos.
                }
                break;

            case "/":
                if (esConstante(operando2) && esConstante(operando1)) {
                    codigoAssembler.append("FLD ").append(operando2).append("\n"); // Cargamos en la pila del
                                                                                   // coprocesador los valores de punto
                                                                                   // flotante.
                    codigoAssembler.append("FLD ").append(operando1).append("\n");
                    codigoAssembler.append("FDIV "); // Hacemos la división de los operandos recién cargados en la pila.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE, numeroTerceto);
                    codigoAssembler.append("FSTP ").append(variableAuxiliar).append("\n"); // Almacenamos el resultado y
                                                                                           // desapilamos.
                } else if (esConstante(operando2) && !esConstante(operando1)) {
                    codigoAssembler.append("FLD ").append(operando2).append("\n"); // Cargamos en la pila del
                                                                                   // coprocesador los valores de punto
                                                                                   // flotante.
                    codigoAssembler.append("FLD ").append("__").append(operando1).append("\n");
                    codigoAssembler.append("FDIV "); // Hacemos la división de los operandos recién cargados en la pila.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE, numeroTerceto);
                    codigoAssembler.append("FSTP ").append(variableAuxiliar).append("\n"); // Almacenamos el resultado y
                                                                                           // desapilamos.
                } else {
                    codigoAssembler.append("FLD ").append("__").append(operando2).append("\n"); // Cargamos en la pila
                                                                                                // del coprocesador los
                                                                                                // valores de punto
                                                                                                // flotante.
                    codigoAssembler.append("FLD ").append("__").append(operando1).append("\n");
                    codigoAssembler.append("FDIV "); // Hacemos la división de los operandos recién cargados en la pila.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE, numeroTerceto);
                    codigoAssembler.append("FSTP ").append(variableAuxiliar).append("\n"); // Almacenamos el resultado y
                                                                                           // desapilamos.
                }
                break;
            case "=":
                codigoAssembler.append("FLD ").append(operando2).append("\n");
                codigoAssembler.append("FSTP ").append(operando1).append("\n");
                break;
            case ">=":
                codigoAssembler.append("FLD ").append("__").append(operando2).append("\n");
                codigoAssembler.append("FCOM ").append("__").append(operando1).append("\n"); // Comparamos los
                                                                                             // operandos.
                codigoAssembler.append("FSTSW ").append(auxiliar).append("\n"); // Guardamos en memoria el estado de la
                                                                                // comparación.
                codigoAssembler.append("MOV AX ").append(auxiliar).append("\n"); // Lo movemos a AX.
                codigoAssembler.append("SAHF ").append("\n"); // Guardamos los bits menos significativos.
                variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto);

                codigoAssembler.append("MOV ").append(variableAuxiliar).append(" OFFh\n");
                codigoAssembler.append("JAE ").append(variableAuxiliar.substring(1)).append("\n"); // Saltamos a la
                                                                                                   // etiqueta por
                                                                                                   // verdadero.
                codigoAssembler.append("MOV ").append(variableAuxiliar).append(" 00h\n"); // Cargamos en la variable
                                                                                          // auxiliar por falso.
                codigoAssembler.append(variableAuxiliar.substring(1)).append("\n"); // Etiqueta para saltar por
                                                                                    // verdadero.
                break;

            case "<=":

                codigoAssembler.append("FLD ").append("__").append(operando2).append("\n");
                codigoAssembler.append("FCOM ").append("__").append(operando1).append("\n");
                codigoAssembler.append("FSTSW ").append(auxiliar).append("\n");
                codigoAssembler.append("MOV AX ").append(auxiliar).append("\n");
                codigoAssembler.append("SAHF ").append("\n");
                variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto);

                codigoAssembler.append("MOV ").append(variableAuxiliar).append(" OFFh\n");
                codigoAssembler.append("JBE ").append(variableAuxiliar.substring(1)).append("\n");
                codigoAssembler.append("MOV ").append(variableAuxiliar).append(" 00h\n");
                codigoAssembler.append(variableAuxiliar.substring(1)).append("\n");
                break;

            case ">":

                codigoAssembler.append("FLD ").append("__").append(operando2).append("\n");
                codigoAssembler.append("FCOM ").append("__").append(operando1).append("\n");
                codigoAssembler.append("FSTSW ").append(auxiliar).append("\n");
                codigoAssembler.append("MOV AX ").append(auxiliar).append("\n");
                codigoAssembler.append("SAHF ").append("\n");
                variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto);

                codigoAssembler.append("MOV ").append(variableAuxiliar).append(" OFFh\n");
                codigoAssembler.append("JA ").append(variableAuxiliar.substring(1)).append("\n");
                codigoAssembler.append("MOV ").append(variableAuxiliar).append(" 00h\n");
                codigoAssembler.append(variableAuxiliar.substring(1)).append("\n");
                break;

            case "<":

                codigoAssembler.append("FLD ").append("__").append(operando2).append("\n");
                codigoAssembler.append("FCOM ").append("__").append(operando1).append("\n");
                codigoAssembler.append("FSTSW ").append(auxiliar).append("\n");
                codigoAssembler.append("MOV AX ").append(auxiliar).append("\n");
                codigoAssembler.append("SAHF ").append("\n");
                variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto);

                codigoAssembler.append("MOV ").append(variableAuxiliar).append(" OFFh\n");
                codigoAssembler.append("JB ").append(variableAuxiliar.substring(1)).append("\n");
                codigoAssembler.append("MOV ").append(variableAuxiliar).append(" 00h\n");
                codigoAssembler.append(variableAuxiliar.substring(1)).append("\n");
                break;

            case "!!":
                codigoAssembler.append("FLD ").append("__").append(operando2).append("\n");
                codigoAssembler.append("FCOM ").append("__").append(operando1).append("\n");
                codigoAssembler.append("FSTSW ").append(auxiliar).append("\n");
                codigoAssembler.append("MOV AX ").append(auxiliar).append("\n");
                codigoAssembler.append("SAHF ").append("\n");
                variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto);

                codigoAssembler.append("MOV ").append(variableAuxiliar).append(" OFFh\n");
                codigoAssembler.append("JNE ").append(variableAuxiliar.substring(1)).append("\n");
                codigoAssembler.append("MOV ").append(variableAuxiliar).append(" 00h\n");
                codigoAssembler.append(variableAuxiliar.substring(1)).append("\n");
                break;

            case "==":

                codigoAssembler.append("FLD ").append("__").append(operando2).append("\n");
                codigoAssembler.append("FCOM ").append("__").append(operando1).append("\n");
                codigoAssembler.append("FSTSW ").append(auxiliar).append("\n");
                codigoAssembler.append("MOV AX ").append(auxiliar).append("\n");
                codigoAssembler.append("SAHF ").append("\n");
                variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto);

                codigoAssembler.append("MOV ").append(variableAuxiliar).append(" OFFh\n");
                codigoAssembler.append("JE ").append(variableAuxiliar.substring(1)).append("\n");
                codigoAssembler.append("MOV ").append(variableAuxiliar).append(" 00h\n");
                codigoAssembler.append(variableAuxiliar.substring(1)).append("\n");
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

    public static void generarAssemblerSaltoIncondicional(String op) {
        codigoAssembler.append("JMP ").append(op).append("\n");
    }

    public static void generarAssemblerSaltoCondicional() {
        codigoAssembler.append("JLE ").append(OP2).append("\n");
        switch (OP1) {
            case value:

                break;

            default:
                break;
        }
    }

    public static void generarAssemblerErrorFuncionRecursiva(String funcionLlamadora, String funcionLlamada) {

    }

    public static void generarAssemblerInvocacion() {
        pilaFunciones.push(tag);

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
        String jump = pilaFunciones.pop();
        generarAssemblerSaltoIncondicional(jump);
    }

    public static void generarAssemblerTOD() {

        if (type.equals(Tercetos.ERROR))
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

    public static String generarVariableAuxiliarString(String cadena, int numeroTerceto) {
        String variableAuxiliar = AUX + numeroTerceto + " db " + "\"" + cadena + "\"";
        TablaSimbolos.addIdentificador(variableAuxiliar);
        TablaSimbolos.addTipo(TablaTipos.STRING, variableAuxiliar);
        tercetosAsociados.put(variableAuxiliar, numeroTerceto); // Asociamos la variable auxiliar al número del terceto.
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
