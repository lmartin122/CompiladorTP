package Lexico;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import Lexico.AccionesSemanticas.AccionSemantica;
import Tools.BinaryFileReader;
import Tools.ProgramReader;

public class AnalizadorLexico {
    private final int ESTADOS = 19; // osea 20 estados 0 a 19
    private final int SIMBOLOS = 27; // el 27 seria el simbolo "otros"

    MatrizTransicion matrizTransicion;
    private Map<String, Integer> palabrasReservadas;
    private ProgramReader reader;

    public AnalizadorLexico(String p) {
        this.reader = new ProgramReader(p);
        cargarMatriz();

    }

    private AccionSemantica toAccionSemantica(String acc) {
        if (acc.equals("null"))
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

            // for (ArrayList<Character> l : data) {
            // for (String s : linea) {
            // System.out.println(s);
            // }

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

    public int generateToken() {

        if (hasFinishedTokenizer())
            return -1;

        // Variables
        int estado = 0;
        boolean error = false;
        AccionSemantica as = null;
        int token = 0;

        while (reader.next()) {

            char s = reader.character();

            estado = matrizTransicion.nextEstado(estado, s);

            as = matrizTransicion.accionSemantica(estado, s);

            if (as != null) {
                token = as.run(s, reader);
                if (token < 0) {
                    error = true;
                }
            }

        }

        return token;

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