package Tools;

import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TablaClases {
    public static String actualImplFor = "";
    public static ArrayList<String> i = new ArrayList<>();
    public static ArrayList<String> t = new ArrayList<>();
    public static ArrayList<String> m = new ArrayList<>();
    public static ArrayList<String> a = new ArrayList<>();
    public static void addClase(String clase){
        t.add(clase);
    };

    private static ArrayList<String> metodosDeInterfaz(String interfaz){
        ArrayList<String> metodosInterfaz = new ArrayList<>();
        for(String s: m){
            if (s.contains("@" + interfaz + "@")){
                metodosInterfaz.add(s.split("@")[0]);
            }
        }
        return metodosInterfaz;
    }
    public static boolean implementaMetodosInterfaz(String clase, String interfaz){
        boolean implementaTodo = true;
        ArrayList<String> metodosInterfaz = metodosDeInterfaz(interfaz);
        for(String s: metodosInterfaz){
            if(m.contains(s + "@" + clase + "@" + "false")){
                implementaTodo = false;
            }
        }
        return implementaTodo;
    };
    public static void addHerencia(String clase, String herencia){
        int index = t.indexOf(clase); //lo busco asi ya que si no tiene herencia se guarda solo el nombre de la clase
        t.add(clase + "@" + herencia);
        if(index != -1){
            t.remove(index);
        } else {
            System.out.println("se esta intentando borrar una clase que no existe");
        }
    };
    public static boolean existeClase(String clase){

        for(String c: t){
           if(c.split("@")[0].equals(clase)){
               return true;
           }
       }
        return false;
    }

    public static void addInterface(String interfaz) {i.add(interfaz);}

    public static void addMetodo(String metodo,String clase){
        if(existeClase(clase)){
            m.add(metodo + "@" + clase);
        }
    }
    public static void addInterfaz(String metodo,String interfaz){
        m.add(metodo + "@" + interfaz + "@" + "false");


    }

    public static void addAtributo(String tipo,String atributo, String clase){
        a.add(atributo + "@" + clase + "@" + tipo);
    }
    public static void setMetodoDeclarado(String metodo, String declarado){
        int i = m.indexOf(metodo);
        if (i != -1){
            String aux = m.get(i);
            aux = aux + "@" + declarado;
            m.remove(i);
            m.add(aux);
        }
    }
    public static String tipoDeAtributo(String atributo, String clasePerteneciente){
        int indice = -1;
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).contains(atributo + "@" + clasePerteneciente)) {
                indice = i;
            }
        }
        return a.get(indice).split("@")[2];
    }

    private static int getIndiceContains(String b, String c){
        int indice = -1;
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).contains(b + "@" + c)) {
                indice = i;
            }
        }
        return indice;
    }
    public static int cambiarMetodoADeclaradoImplFor(String metodo, String clasePerteneciente){
        int indice = -1;
        for (int i = 0; i < m.size(); i++) {
            if (m.get(i).contains(metodo + "@" + clasePerteneciente + "@" + "false")) {
                indice = i;
            }
            if (m.get(i).contains(metodo + "@" + clasePerteneciente + "@" + "true")) {
                return 2;

            }
        }
        if(indice != -1){
            String aux = m.get(indice);
            String[] arreglo = aux.split("@");
            arreglo[arreglo.length-1] = "true";
            aux = String.join("@",arreglo);
            System.out.println("AUX : " + aux);
            m.remove(indice);
            m.add(aux);
            return 0;
        }
        return 1;
    }

}
