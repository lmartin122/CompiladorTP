package Lexico;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public final class PalabrasReservadasTabla {
    private static final HashMap<String, Short> p = cargarMatriz();

    private PalabrasReservadasTabla() {
    };

    private static HashMap<String, Short> cargarMatriz() {
        String dir = System.getProperty("user.dir") + "/data/palabrasReservadas.txt";
        try {
            HashMap<String, Short> out = new HashMap<String, Short>();
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String palabra = line.split("=")[0];
                String numero = line.split("=")[1];
                out.put(palabra, Short.valueOf(numero));
            }
            br.close();
            fr.close();
            return out;
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;
        return null;
    }

    public static Short getClave(String clave) {
        return p.get(clave);
    }

    public static HashMap<String, Short> getPalabrasReservadas() {
        return p; // cambiar después
    }

    public static boolean contienePalabra(String palabra) {
        return p.containsKey(palabra);
    }
}
