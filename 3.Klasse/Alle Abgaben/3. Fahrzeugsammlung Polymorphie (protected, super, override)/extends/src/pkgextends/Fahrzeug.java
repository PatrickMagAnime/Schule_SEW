package pkgextends;

/**
 *
 * @author riedl
 */
public class Fahrzeug {

    //Objekt
    //setter und getter

    public double getGeschwindigkeit() {
        return geschwindigkeit;
    }

    public void setGeschwindigkeit(double geschwindigkeit) {
        this.geschwindigkeit = geschwindigkeit;
    }
    public void fahren(){
        System.out.println("Das auto fährt mit km/h " + geschwindigkeit);
    }
    protected double geschwindigkeit;

    //haupt konstruktor
    public Fahrzeug(double geschwindigkeit) {
        this.geschwindigkeit = geschwindigkeit;
    }
}

