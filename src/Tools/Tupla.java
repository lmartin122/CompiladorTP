package Tools;

public class Tupla<t1, t2> {
    private t1 estado;
    private t2 as;

    public Tupla(t1 first, t2 second) {
        this.estado = first;
        this.as = second;
    }

    public Tupla(){
        
    }

    public t1 getEstado() {
        return estado;
    }

    public t1 getFirst() {
        return estado;
    }

    public t2 getSecond() {
        return as;
    }

    public void setFirst(t1 first) {
        this.estado = first;
    }

    public t2 getAs() {
        return as;
    }

    public void setSecond(t2 second) {
        this.as = second;
    }
}
