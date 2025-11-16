import javax.swing.*;
import java.awt.*;

public class SolarSystem extends JPanel {
    int breite = 600, hoehe = 600;//breite und höhe des fensters
    int radius = 150;//radius vom planet
    int r_mond = 50;//radius vom mond

    //positionen werden später von threads verändert
    double planetX, planetY;
    double mondX, mondY;
    double anglePlanet = 0, angleMond = 0;//winkel der planeten

    public SolarSystem() {
        setPreferredSize(new Dimension(breite, hoehe));//fenster größe mit Dimension für breite und höhe
        setBackground(Color.black);//hintergrund schwarz machen

        //thread wird mit .start() für den planeten und mond gestartet
        new PlanetThread().start();
        new MondThread().start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int centerX = breite / 2, centerY = hoehe / 2; //man muss die fenster größe und breite durch zwei teilen damit alles in der mitte ist

        //Sonne
        g.setColor(Color.yellow);
        g.fillOval(centerX - 25, centerY - 25, 50, 50);

        //Planet
        g.setColor(Color.blue);
        g.fillOval((int) planetX - 15, (int) planetY - 15, 30, 30);

        //Mond
        g.setColor(Color.gray);
        g.fillOval((int) (mondX - 8), (int) mondY - 8, 16, 16);
        
    }

    //diese klasse bekommt die Thread klasse vererbt
    class PlanetThread extends Thread {
        public void run() {
            int sleep = 20; //wir brechnen die sleep dauer um die geschwindigkeit anzupassen
            double speed = 2 * Math.PI * sleep / 60000.0; //1 Umlauf pro minute
            int cx = breite / 2, cy = hoehe / 2;
            while (true) {
                anglePlanet += speed;
                planetX = cx + radius * Math.cos(anglePlanet);
                planetY = cy + radius * Math.sin(anglePlanet);
                repaint();
                try {Thread.sleep(sleep); } catch (InterruptedException ignored) {}
            }
        }
    }

    class MondThread extends Thread {
        public void run() {
            int sleep = 20;
            double speed = 2 * Math.PI * sleep / 10000.0; //das selbe wie beim planeten nur halt auf 10sek gestellt
            while (true) {
                angleMond += speed;
                // Mond kreist um Planet
                mondX = planetX + r_mond * Math.cos(angleMond);
                mondY = planetY + r_mond * Math.sin(angleMond);
                repaint();
                try { Thread.sleep(sleep); } catch (InterruptedException ignored) {}
            }
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Testverbesserung");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setContentPane(new SolarSystem());
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
