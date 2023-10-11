package Lexico;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import Lexico.AccionesSemanticas.AccionSemantica;
import Tools.BinaryFileReader;
import Tools.ProgramReader;
import Tools.Tupla;

public class AnalizadorLexico {
    private final int ESTADOS = 20; // osea 20 estados 0 a 19
    private final int SIMBOLOS = 29; // el 27 seria el simbolo "otros"

    public static int estado_error = 0;
    public static boolean error = false;
    public static final ArrayList<String> lista_token = new ArrayList<>();
    MatrizTransicion matrizTransicion;
    private Map<String, Integer> palabrasReservadas;
    private ProgramReader reader;

    public AnalizadorLexico(String p) {
        this.reader = new ProgramReader(p);
        cargarMatriz();
    }

    private AccionSemantica toAccionSemantica(String acc) {
        acc = acc.trim();
        if (acc.equals("null") || acc.equals("-1"))
            return null;

        try {
            // Obtener la clase a partir del nombre
            Class<?> clase = Class.forName("Lexico.AccionesSemanticas." + acc);

            // Crear una instancia de la clase
            return (AccionSemantica) clase.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;

    }

    private void cargarMatriz() {
        this.matrizTransicion = new MatrizTransicion(ESTADOS, SIMBOLOS);

        ArrayList<ArrayList<Character>> data = BinaryFileReader.read("matrizTransicion.txt", "data");
        int e0 = -1;
        int e1 = -1;
        int e2 = -1;
        String acc = null;

        for (ArrayList<Character> l : data) {
            String linea[] = l.stream().map(Object::toString).collect(Collectors.joining("")).split("\\s*;\\s*");

            try {
                e0 = Integer.parseInt(linea[0]);
                e1 = Integer.parseInt(linea[1]);
                e2 = Integer.parseInt(linea[2]);
                acc = linea[3];

            } catch (NumberFormatException e) {
            }

            this.matrizTransicion.addTransicion(e0,
                    e1,
                    e2,
                    toAccionSemantica(acc));
        }

    }

    public Tupla<String, Short> generateToken() {

        if (hasFinishedTokenizer())
            return new Tupla<>("Fin del programa.", (short) 0);

        // Variables
        int estado = 0;

        AccionSemantica as = null;
        Tupla<String, Short> token = null;

        while (!reader.hasFinished() && estado != 20) {

            char s = reader.character();

            as = matrizTransicion.accionSemantica(estado, s);
            estado = matrizTransicion.nextEstado(estado, s);

            if (as != null) {
                token = as.run(s, reader);
                if (token != null && AnalizadorLexico.estado_error < 0) {
                    estado = 20; //terminó de leer en caso de error
                    error = true;
                    estado_error = 1;
                }
            }
                if(estado == -1){ //error simbomlo desconocido
                    estado = 0;
                    error = true;
                }
            System.out.println();
            reader.next();
        }
        return token;

    }

    public int getTokenPosition() {
        return reader.getCurrentLine();
    }

    public boolean hasFinishedTokenizer() {
        return reader.hasFinished();
    }

    public boolean hasReadWell() {
        return this.reader.hasProgram();
    }

    public String getProgram() {
        return reader.programToString();
    }
}