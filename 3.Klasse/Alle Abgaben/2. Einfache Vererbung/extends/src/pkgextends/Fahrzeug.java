package pkgextends;

/**
 *
 * @author riedl
 */
public class Fahrzeug {

    //Objekt
    //setter und getter
    public void setGeschwindigkeit(double geschwindigkeit) {
        this.geschwindigkeit = geschwindigkeit;
    }

    public double getGeschwindigkeit() {
        return geschwindigkeit;
    }
    
    
    public double geschwindigkeit;
    //methode
    public void fahren(double geschwindigkeit){
        System.out.println(geschwindigkeit);
    }
}

