package Lexico;

import java.util.HashMap;
import java.util.Map;

public class PalabrasReservadasTabla {
    private Map<String,Integer> palabrasReservadas = new HashMap<>();

    public PalabrasReservadasTabla(Map<String, Integer> palabrasReservadas) {
        this.palabrasReservadas = palabrasReservadas;
    };

    public Map<String, Integer> getPalabrasReservadas(){
        return this.palabrasReservadas; //cambiar después 
    }
}
