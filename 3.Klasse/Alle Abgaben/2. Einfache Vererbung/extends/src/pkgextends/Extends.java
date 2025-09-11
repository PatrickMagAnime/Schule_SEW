package pkgextends;

/**
 * @author riedl
 */
public class Extends {

    public static void main(String[] args) {
        Auto auto = new Auto();
        Fahrrad fahrrad = new Fahrrad();
        
        System.out.println("Das Auto fährt mit "+auto.geschwindigkeit+"km/h und hat "+auto.anzahlTueren+" Tueren");
        System.out.println("Das Fahrrad fährt mit "+fahrrad.geschwindigkeit+"km/h und hat eine Klingel: "+fahrrad.hatKlingel);
    }
    
}
