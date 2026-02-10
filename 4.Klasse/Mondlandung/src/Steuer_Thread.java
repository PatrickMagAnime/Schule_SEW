import java.util.*;

class Steuer_Thread extends Thread {
    Scanner scanner = new Scanner(System.in);
    Ph_Thread pt;

    public Steuer_Thread(Ph_Thread pt) {
        this.pt = pt; //Verbindung zur Rakete herstellen
    }

    @Override
    public void run() {
        while (true) {
            if (scanner.nextLine() != null) {
                //Gegenschub, verringern der geschwindigkeit
                pt.v -= 4; // Beispiel: 4 m/s bremskraft pro Enter
                System.out.println("SCHUB");
            }
        }
    }
}
