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
    public static int contadorVariablesAux = 0;
    public static HashMap<String, Integer> tercetosAsociados = new HashMap<>();
    private static final Stack<String> pila_tokens = new Stack<>();
    private static String auxiliar2bytes = "@variable2bytes";
    private static String comparacionFalsa = "";

    private static final String OVERFLOW_SUMA_PFLOTANTE = "Error: se excedió el límite permitido (overflow)";
    private static final String OVERFLOW_PRODUCTO_ENTERO_SIN_SIGNO = "Error: se excedió el límite permitido (overflow)";
    private static final String OVERFLOW_PRODUCTO_ENTERO_CON_SIGNO = "Error: se excedió el límite permitido (overflow)";
    private static final String INVOCACION_RECURSIVA = "Error: no se permiten declaraciones recursivas.";

    public static void generarCodigoAssembler(Tercetos tercetosGenerados) {
        for (Map.Entry<String, ArrayList<Terceto>> func : tercetosGenerados.getTercetos().entrySet()) {
            String etiqueta = func.getKey();

            for (Terceto terceto : func.getValue()) {
                switch (terceto.getFirst()) {
                    case "*":
                    case "+":
                    case "-":
                    case "/":
                        String tipoOP1 = TablaSimbolos.getTypeLexema(terceto.getSecond());
                        String tipoOP2 = TablaSimbolos.getTypeLexema(terceto.getThird());
                        if (tipoOP1.equals(TablaTipos.DOUBLE_TYPE) && tipoOP2.equals(TablaTipos.DOUBLE_TYPE))
                            generarCodigoOperacionesDouble(terceto.getFirst(), terceto.getSecond(), terceto.getThird(),
                                    terceto.getNumber());
                        else
                            generarCodigoOperacionesEnteros(terceto.getFirst(), terceto.getSecond(), terceto.getThird(),
                                    terceto.getNumber());
                        break;
                    case "=":
                        // HACER LA ASIGNACION
                        break;
                    case ">=":
                    case ">":
                    case "<=":
                    case "<":
                    case "!!":
                    case "==":
                        String tipoOp1 = TablaSimbolos.getTypeLexema(terceto.getSecond());
                        String tipoOp2 = TablaSimbolos.getTypeLexema(terceto.getThird());
                        if (tipoOp1.equals(TablaTipos.DOUBLE_TYPE) && tipoOp2.equals(TablaTipos.DOUBLE_TYPE))
                            generarCodigoOperacionesDouble(terceto.getFirst(), terceto.getSecond(), terceto.getThird(),
                                    terceto.getNumber());
                        else
                            generarCodigoOperacionesEnteros(terceto.getFirst(), terceto.getSecond(), terceto.getThird(),
                                    terceto.getNumber());
                        break;
                    case "UB":
                        etiqueta = terceto.getSecond(); // Nos fijamos a dónde tenemos que saltar.
                        generarAssemblerSaltoIncondicional(etiqueta);
                        break;

                    case "CB":
                        etiqueta = terceto.getThird(); // Nos fijamos a dónde tenemos que saltar.
                        generarAssemblerSaltoCondicional(etiqueta);
                        break;

                    case "CALL":

                        break;

                    case "RETURN":

                        break;

                    default:
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
                .append(".data\n") // Empieza la declaración de variables. Primero agregamos las constantes para
                                   // los errores.
                .append(auxiliar2bytes).append(" dw ? \n")
                .append("_OVERFLOW_PRODUCTO_ENTERO db \"" + OVERFLOW_PRODUCTO_ENTERO_CON_SIGNO + "\", 0\n")
                .append("_OVERFLOW_PRODUCTO_ENTERO db \"" + OVERFLOW_PRODUCTO_ENTERO_SIN_SIGNO + "\", 0\n")
                .append("_OVERFLOW_SUMA_PFLOTANTE db \"" + OVERFLOW_SUMA_PFLOTANTE + "\", 0\n")
                .append("_INVOCACION_RECURSIVA db \"" + INVOCACION_RECURSIVA + "\", 0\n");

        generarCodigoVariables(header);

        header.append(".code\n");
        header.append(codigoAssembler);
        codigoAssembler = header;

    }

    public static void generarCodigoVariables(StringBuilder librerias) { // Generamos el código para las variables
                                                                         // declaradas.
        StringBuilder variables = new StringBuilder();
        for (String func : TablaSimbolos.getTablaSimbolos()) {
            String tipo = TablaSimbolos.getTypeLexema(func);
            if (func.startsWith("@")) {
                if (tipo.equals(TablaTipos.LONG_TYPE)) {
                    librerias.append(func).append(" dd ? \n");
                } else if (tipo.equals(TablaTipos.UINT_TYPE))
                    librerias.append(func).append(" dw ? \n");
                else {
                    librerias.append(func).append(" dq ? \n");
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

    public static void generarCodigoOperacionesEnteros(String operador, String operando1, String operando2,
            int numeroTerceto) { // Preguntar si tengo que involucrar a la pila al final de cada operacion.
                                 // PREGUNTAR
        String variableAuxiliar;
        switch (operador) {
            case "+":
                if (TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)
                        && TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE)) {
                    codigoAssembler.append("MOV EAX, ").append("__").append(operando1).append("\n"); // Movemos el valor
                                                                                                     // del OP1 al
                                                                                                     // registro EAX (32
                                                                                                     // bits).
                    codigoAssembler.append("ADD EAX, ").append("__").append(operando2).append("\n"); // Sumamos los
                                                                                                     // operandos.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                     // variable
                                                                                                     // auxiliar del
                                                                                                     // tipo del
                                                                                                     // resultado de la
                                                                                                     // operación
                                                                                                     // (LONG).
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", EAX\n");
                } else {
                    codigoAssembler.append("MOV AX, ").append("__").append(operando1).append("\n"); // Movemos el valor
                                                                                                    // del OP1 al
                                                                                                    // registro AX (16
                                                                                                    // bits).
                    codigoAssembler.append("ADD AX, ").append("__").append(operando2).append("\n"); // Sumamos los
                                                                                                    // operandos.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Hacemos la
                                                                                                     // variable
                                                                                                     // auxiliar del
                                                                                                     // tipo del
                                                                                                     // resultado de la
                                                                                                     // operación
                                                                                                     // (UINT).
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", AX\n");
                }
                break;
            case "-":
                if (TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)
                        && TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE)) {
                    codigoAssembler.append("MOV EAX, ").append("__").append(operando1).append("\n"); // Movemos el valor
                                                                                                     // del OP1 al
                                                                                                     // registro EAX (32
                                                                                                     // bits).
                    codigoAssembler.append("SUB EAX, ").append("__").append(operando2).append("\n"); // Restamos los
                                                                                                     // operandos.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                     // variable
                                                                                                     // auxiliar del
                                                                                                     // tipo del
                                                                                                     // resultado de la
                                                                                                     // operación
                                                                                                     // (LONG).
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", EAX\n");
                } else {
                    codigoAssembler.append("MOV AX, ").append("__").append(operando1).append("\n"); // Movemos el valor
                                                                                                    // del OP1 al
                                                                                                    // registro AX (16
                                                                                                    // bits).
                    codigoAssembler.append("SUB AX, ").append("__").append(operando2).append("\n"); // Restamos los
                                                                                                    // operandos.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Hacemos la
                                                                                                     // variable
                                                                                                     // auxiliar del
                                                                                                     // tipo del
                                                                                                     // resultado de la
                                                                                                     // operación
                                                                                                     // (UINT).
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", AX\n");
                }
                break;
            case "*":
                if (TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)
                        && TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE)) {
                    codigoAssembler.append("MOV EAX, ").append("__").append(operando1).append("\n"); // Movemos el valor
                                                                                                     // del OP1 al
                                                                                                     // registro EAX (32
                                                                                                     // bits).
                    codigoAssembler.append("MUL ").append("__").append(operando2).append("\n"); // Multiplicamos los
                                                                                                // operandos.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Hacemos la
                                                                                                     // variable
                                                                                                     // auxiliar del
                                                                                                     // tipo del
                                                                                                     // resultado de la
                                                                                                     // operación
                                                                                                     // (LONG).
                    generarAssemblerOverflowEnterosConSigno(variableAuxiliar);
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", EAX\n");
                } else {
                    codigoAssembler.append("MOV AX, ").append("__").append(operando1).append("\n"); // Movemos el valor
                                                                                                    // del OP1 al
                                                                                                    // registro AX (16
                                                                                                    // bits).
                    codigoAssembler.append("MUL ").append("__").append(operando2).append("\n"); // Multiplicamos los
                                                                                                // operandos.
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto); // Hacemos la
                                                                                                     // variable
                                                                                                     // auxiliar del
                                                                                                     // tipo del
                                                                                                     // resultado de la
                                                                                                     // operación
                                                                                                     // (UINT).
                    generarAssemblerOverflowEnterosSinSigno(variableAuxiliar);
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", AX\n");
                }
                break;
            case "=":
                if (TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE)
                        && TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)) {
                    // Asignación de enteros largos (32 bits)
                    codigoAssembler.append("MOV EAX, ").append("__").append(operando2).append("\n"); // Movemos el valor
                                                                                                     // de op2 a EAX
                    codigoAssembler.append("MOV ").append("__").append(operando1).append(", EAX\n"); // Asignamos el
                                                                                                     // valor de EAX a
                                                                                                     // op1
                } else {
                    // Asignación de enteros sin signo (16 bits)
                    codigoAssembler.append("MOV AX, ").append("__").append(operando2).append("\n"); // Movemos el valor
                                                                                                    // de op2 a AX
                    codigoAssembler.append("MOV ").append("__").append(operando1).append(", AX\n"); // Asignamos el
                                                                                                    // valor de AX a op1
                }
                break;
            case "/":
                if (TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE)
                        && TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)) {
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto);
                    codigoAssembler.append("MOV EAX ").append("__").append(operando1).append("\n"); // Guardamos el
                                                                                                    // dividendo en el
                                                                                                    // registro EAX (32
                                                                                                    // bits).
                    codigoAssembler.append("DIV ").append("__").append(operando2).append("\n"); // Realizamos la
                                                                                                // división.
                    codigoAssembler.append("MOV ").append(variableAuxiliar); // Guardamos el valor en la variable
                                                                             // auxiliar.
                } else {
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.UINT_TYPE, numeroTerceto);
                    codigoAssembler.append("MOV AX ").append("__").append(operando1).append("\n"); // Guardamos el
                                                                                                   // dividendo en el
                                                                                                   // registro AX (16
                                                                                                   // bits).
                    codigoAssembler.append("DIV ").append("__").append(operando2); // Realizamos la división.
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", AX"); // Guardo el resultado de la
                                                                                            // división en AX (16 bits).
                }
                break;
            case "==":
                if (TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE)
                        && TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)) {
                    codigoAssembler.append("MOV EAX ").append("__").append(operando2).append("\n");
                    codigoAssembler.append("CMP ").append("__").append(operando1).append("\n"); // Comparamos los
                                                                                                // valores entre
                                                                                                // operandos
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                     // valor de la
                                                                                                     // comparación.
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n"); // Movemos el valor para
                                                                                                // true.
                    codigoAssembler.append("JE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de ser
                                                                                                      // verdadera la
                                                                                                      // comparación,
                                                                                                      // saltamos a la
                                                                                                      // etiqueta que
                                                                                                      // hacemos
                                                                                                      // después.
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor para
                                                                                               // falso.
                    codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en caso de
                                                                                         // verdadero.
                    comparacionFalsa = "JNE "; // Guardamos la comparacion si llega a ser falso.
                } else {
                    codigoAssembler.append("MOV AX ").append("__").append(operando2).append("\n");
                    codigoAssembler.append("CMP ").append("__").append(operando1).append("\n");
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto);
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                    codigoAssembler.append("JE ").append(variableAuxiliar.substring(1)).append("\n");
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n");
                    codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n");
                    comparacionFalsa = "JNE";
                }

                break;

            case ">=":
                if (TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE)
                        && TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)) {
                    codigoAssembler.append("MOV EAX ").append("__").append(operando2).append("\n");
                    codigoAssembler.append("CMP ").append("__").append(operando1).append("\n"); // Comparamos los
                                                                                                // valores entre
                                                                                                // operandos
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                     // valor de la
                                                                                                     // comparación.
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n"); // Movemos el valor para
                                                                                                // true.
                    codigoAssembler.append("JAE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de ser
                                                                                                       // verdadera la
                                                                                                       // comparación,
                                                                                                       // saltamos a la
                                                                                                       // etiqueta que
                                                                                                       // hacemos
                                                                                                       // después.
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor para
                                                                                               // falso.
                    codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en caso de
                                                                                         // verdadero.
                    comparacionFalsa = "JB";
                } else {
                    codigoAssembler.append("MOV AX ").append("__").append(operando2).append("\n");
                    codigoAssembler.append("CMP ").append("__").append(operando1).append("\n");
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto);
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                    codigoAssembler.append("JAE ").append(variableAuxiliar.substring(1)).append("\n");
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n");
                    codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n");
                    comparacionFalsa = "JB";
                }
                break;

            case "<=":
                if (TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE)
                        && TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)) {
                    codigoAssembler.append("MOV EAX ").append("__").append(operando2).append("\n");
                    codigoAssembler.append("CMP ").append("__").append(operando1).append("\n"); // Comparamos los
                                                                                                // valores entre
                                                                                                // operandos
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                     // valor de la
                                                                                                     // comparación.
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n"); // Movemos el valor para
                                                                                                // true.
                    codigoAssembler.append("JBE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de ser
                                                                                                       // verdadera la
                                                                                                       // comparación,
                                                                                                       // saltamos a la
                                                                                                       // etiqueta que
                                                                                                       // hacemos
                                                                                                       // después.
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor para
                                                                                               // falso.
                    codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en caso de
                                                                                         // verdadero.
                    comparacionFalsa = "JA";
                } else {
                    codigoAssembler.append("MOV AX ").append("__").append(operando2).append("\n");
                    codigoAssembler.append("CMP ").append("__").append(operando1).append("\n");
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto);
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                    codigoAssembler.append("JBE ").append(variableAuxiliar.substring(1)).append("\n");
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n");
                    codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n");
                    comparacionFalsa = "JA";
                }
                break;

            case ">":
                if (TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE)
                        && TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)) {
                    codigoAssembler.append("MOV EAX ").append("__").append(operando2).append("\n");
                    codigoAssembler.append("CMP ").append("__").append(operando1).append("\n"); // Comparamos los
                                                                                                // valores entre
                                                                                                // operandos
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                     // valor de la
                                                                                                     // comparación.
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n"); // Movemos el valor para
                                                                                                // true.
                    codigoAssembler.append("JG ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de ser
                                                                                                      // verdadera la
                                                                                                      // comparación,
                                                                                                      // saltamos a la
                                                                                                      // etiqueta que
                                                                                                      // hacemos
                                                                                                      // después.
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor para
                                                                                               // falso.
                    codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en caso de
                                                                                         // verdadero.
                    comparacionFalsa = "JBE";
                } else {
                    codigoAssembler.append("MOV AX ").append("__").append(operando2).append("\n");
                    codigoAssembler.append("CMP ").append("__").append(operando1).append("\n");
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto);
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                    codigoAssembler.append("JG ").append(variableAuxiliar.substring(1)).append("\n");
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n");
                    codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n");
                    comparacionFalsa = "JBE";
                }
                break;

            case "<":
                if (TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE)
                        && TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)) {
                    codigoAssembler.append("MOV EAX ").append("__").append(operando2).append("\n");
                    codigoAssembler.append("CMP ").append("__").append(operando1).append("\n"); // Comparamos los
                                                                                                // valores entre
                                                                                                // operandos
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                     // valor de la
                                                                                                     // comparación.
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n"); // Movemos el valor para
                                                                                                // true.
                    codigoAssembler.append("JMP ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de ser
                                                                                                       // verdadera la
                                                                                                       // comparación,
                                                                                                       // saltamos a la
                                                                                                       // etiqueta que
                                                                                                       // hacemos
                                                                                                       // después.
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor para
                                                                                               // falso.
                    codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en caso de
                                                                                         // verdadero.
                    comparacionFalsa = "JAE";
                } else {
                    codigoAssembler.append("MOV AX ").append("__").append(operando2).append("\n");
                    codigoAssembler.append("CMP ").append("__").append(operando1).append("\n");
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto);
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                    codigoAssembler.append("JMP ").append(variableAuxiliar.substring(1)).append("\n");
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n");
                    codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n");
                    comparacionFalsa = "JAE";
                }
                break;

            case "!!":
                if (TablaSimbolos.getTypeLexema(operando2).equals(TablaTipos.LONG_TYPE)
                        && TablaSimbolos.getTypeLexema(operando1).equals(TablaTipos.LONG_TYPE)) {
                    codigoAssembler.append("MOV EAX ").append("__").append(operando2).append("\n");
                    codigoAssembler.append("CMP ").append("__").append(operando1).append("\n"); // Comparamos los
                                                                                                // valores entre
                                                                                                // operandos
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto); // Guardamos el
                                                                                                     // valor de la
                                                                                                     // comparación.
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n"); // Movemos el valor para
                                                                                                // true.
                    codigoAssembler.append("JNE ").append(variableAuxiliar.substring(1)).append("\n"); // En caso de ser
                                                                                                       // verdadera la
                                                                                                       // comparación,
                                                                                                       // saltamos a la
                                                                                                       // etiqueta que
                                                                                                       // hacemos
                                                                                                       // después.
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n"); // Movemos el valor para
                                                                                               // falso.
                    codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n"); // Etiqueta a saltar en caso de
                                                                                         // verdadero.
                    comparacionFalsa = "JE";
                } else {
                    codigoAssembler.append("MOV AX ").append("__").append(operando2).append("\n");
                    codigoAssembler.append("CMP ").append("__").append(operando1).append("\n");
                    variableAuxiliar = generarVariableAuxiliar(TablaTipos.LONG_TYPE, numeroTerceto);
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 0FFh\n");
                    codigoAssembler.append("JNE ").append(variableAuxiliar.substring(1)).append("\n");
                    codigoAssembler.append("MOV ").append(variableAuxiliar).append(", 00h\n");
                    codigoAssembler.append(variableAuxiliar.substring(1)).append(":\n");
                    comparacionFalsa = "JE";
                }
                break;

            default:
                codigoAssembler.append("Error: operacion inválida.\n");
                break;
        }
    }

    public static void generarCodigoOperacionesDouble(String operador, String operando1, String operando2,
            int numeroTerceto) {
        String variableAuxiliar;
        String auxiliar = "@auxDouble";
        switch (operador) {
            case "+":
                codigoAssembler.append("FLD ").append("__").append(operando2).append("\n"); // Cargamos en la pila del
                                                                                            // coprocesador los valores
                                                                                            // de punto flotante.
                codigoAssembler.append("FLD ").append("__").append(operando1).append("\n");
                codigoAssembler.append("FADD "); // Hacemos la suma de los operandos recién cargados en la pila.
                variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE, numeroTerceto);
                codigoAssembler.append("FSTP ").append(contadorVariablesAux).append("\n"); // Almacenamos el resultado y
                                                                                           // desapilamos.
                generarAssemblerOverflowFlotantes(variableAuxiliar);
                break;

            case "-":
                codigoAssembler.append("FLD ").append("__").append(operando2).append("\n"); // Cargamos en la pila del
                                                                                            // coprocesador los valores
                                                                                            // de punto flotante.
                codigoAssembler.append("FLD ").append("__").append(operando1).append("\n");
                codigoAssembler.append("FSUB "); // Hacemos la resta de los operandos recién cargados en la pila.
                variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE, numeroTerceto);
                codigoAssembler.append("FSTP ").append(contadorVariablesAux).append("\n"); // Almacenamos el resultado y
                                                                                           // desapilamos.
                break;

            case "*":
                codigoAssembler.append("FLD ").append("__").append(operando2).append("\n"); // Cargamos en la pila del
                                                                                            // coprocesador los valores
                                                                                            // de punto flotante.
                codigoAssembler.append("FLD ").append("__").append(operando1).append("\n");
                codigoAssembler.append("FMUL "); // Hacemos la multiplicación de los operandos recién cargados en la
                                                 // pila.
                variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE, numeroTerceto);
                codigoAssembler.append("FSTP ").append(contadorVariablesAux).append("\n"); // Almacenamos el resultado y
                                                                                           // desapilamos.
                break;

            case "/":
                codigoAssembler.append("FLD ").append("__").append(operando2).append("\n"); // Cargamos en la pila del
                                                                                            // coprocesador los valores
                                                                                            // de punto flotante.
                codigoAssembler.append("FLD ").append("__").append(operando1).append("\n");
                codigoAssembler.append("FDIV "); // Hacemos la división de los operandos recién cargados en la pila.
                variableAuxiliar = generarVariableAuxiliar(TablaTipos.DOUBLE_TYPE, numeroTerceto);
                codigoAssembler.append("FSTP ").append(contadorVariablesAux).append("\n"); // Almacenamos el resultado y
                                                                                           // desapilamos.
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
                codigoAssembler.append("Error: operacion inválida.\n");
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

    public static void generarAssemblerSaltoIncondicional(String label) {
        codigoAssembler.append("JMP ").append(label).append("\n");
    }

    public static void generarAssemblerSaltoCondicional(String label) {
        codigoAssembler.append("JLE ").append(label).append("\n");
    }

    public static void generarAssemblerErrorFuncionRecursiva(String funcionLlamadora, String funcionLlamada) {

    }

    public static String generarVariableAuxiliar(String tipo, int numeroTerceto) { // Generamos la variable auxiliar que
                                                                                   // vamos a necesitar para las
                                                                                   // conversiones y las operaciones
                                                                                   // aritméticas.
        String variableAuxiliar = "@aux" + numeroTerceto;
        TablaSimbolos.addIdentificador(variableAuxiliar);
        TablaSimbolos.addTipo(tipo, variableAuxiliar);

        tercetosAsociados.put(variableAuxiliar, numeroTerceto); // Asociamos la variable auxiliar al número del terceto.
        return variableAuxiliar;
    }

    public static int getTercetoAsociado(String variableAuxiliar) { // Según alguna variable auxiliar que nos manden,
                                                                    // nos fijamos el terceto.
        return tercetosAsociados.getOrDefault(variableAuxiliar, -1);
    }

    public static void escribirCodigoEnArchivo(String nombreArchivo, String codigoAssembler) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            writer.write(codigoAssembler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
