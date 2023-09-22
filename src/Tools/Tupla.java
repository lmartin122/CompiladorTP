package Tools;

public class Tupla<t1, t2> {
    private t1 estado;
    private t2 as;

    public Tupla(t1 first, t2 second) {
        this.estado = first;
        this.as = second;
    }

    public t1 getEstado() {
        return estado;
    }

    public void setEstado(t1 first) {
        this.estado = first;
    }

    public t2 getAs() {
        return as;
    }

    public void setAs(t2 second) {
        this.as = second;
    }
}
