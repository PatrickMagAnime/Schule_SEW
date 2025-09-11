package pkgextends;

/**
 *
 * @author riedl
 */
public class Auto extends Fahrzeug {
    public int anzahlTueren = 4;
  
    //überschreiben der geschwindigkeit
    public void fahren(double geschwindigkeit) {
        super.fahren(geschwindigkeit = 25);
    }


    
    
    
}
