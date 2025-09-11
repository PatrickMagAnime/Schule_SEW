package pkgextends;

/**
 *
 * @author riedl
 */
public class Fahrrad extends Fahrzeug {
    public boolean hatKlingel = true;
    
    //überschreiben der geschwindigkeit
    public void fahren(double geschwindigkeit) {
        super.fahren(geschwindigkeit = 15);
    }
}
