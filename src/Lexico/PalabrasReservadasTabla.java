package Lexico;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PalabrasReservadasTabla {
    public static final Map<String,Integer> p = new HashMap<>();

    public PalabrasReservadasTabla() {
        String dir = System.getProperty("user.dir") + "\\data\\palabrasReservadas.txt";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line  = br.readLine()) != null) {
                String palabra = line.split("=")[0];
                String numero = line.split("=")[1];
                p.put(palabra, Integer.valueOf(numero));
            }
            br.close();
            fr.close();
        } catch(IOException e) {
            e.printStackTrace();
        };

    };

    public Map<String, Integer> getPalabrasReservadas(){
        return p; //cambiar después
    }
}
